/*
 * Copyright (C) 2015 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.qtplaf.library.ai.learning.propagation;

import java.util.List;

import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.math.Matrix;

/**
 * Resilient back propagation.
 *
 * @author Miquel Sas
 */
public class ResilientPropagation extends Propagation {

	/**
	 * Resilient propagation implemented types.
	 */
	public static enum Type {
		/** RPROP+ : The classic RPROP algorithm. Uses weight back tracking. */
		RPROPp,
		/** iRPROP+ : New weight back tracking method, some consider this to be the most advanced RPROP. */
		iRPROPp
	}

	/**
	 * Determine the sign of the value.
	 * 
	 * @param value The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	public static int signum(final double value) {
		if (Math.abs(value) < DEFAULT_DOUBLE_EQUAL) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/** Default point at which two doubles are equal. */
	public static final double DEFAULT_DOUBLE_EQUAL = 0.0000000000001;
	/** The default zero tolerance. */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;
	/** The POSITIVE ETA value. */
	public static final double POSITIVE_ETA = 1.2;
	/** The NEGATIVE ETA value. */
	public static final double NEGATIVE_ETA = 0.5;
	/** The minimum delta value for a weight matrix value. */
	public static final double DELTA_MIN = 1e-6;
	/** The starting update for a delta. */
	public static final double DEFAULT_INITIAL_UPDATE = 0.1;
	/** The maximum amount a delta can reach. */
	public static final double DEFAULT_MAX_STEP = 50;

	/** List of layers last weight gradients). */
	private List<double[][]> lastGradients;
	/** List of layers last weights changes. */
	private List<double[][]> lastWeightChanges;
	/** List of layers deltas. */
	private List<double[][]> lastDeltas;

	/** Type of resilient propagation. */
	private Type type = Type.RPROPp;
	/** The maximum step value for rprop. */
	private double maximumStep = DEFAULT_MAX_STEP;
	/** Last iteration error. */
	private double lastError = NumberUtils.MAX_DOUBLE;

	/**
	 * Constructor.
	 * 
	 * @param network The neural network.
	 */
	public ResilientPropagation(Network network) {
		super(network);
		lastGradients = NetworkUtils.createLayerMatrices(getNetwork());
		lastWeightChanges = NetworkUtils.createLayerMatrices(getNetwork());
		lastDeltas = NetworkUtils.createLayerMatrices(getNetwork());

		// Initialize lastDeltas to DEFAULT_INITIAL_UPDATE
		for (int i = 1; i < lastDeltas.size(); i++) {
			Matrix.set(lastDeltas.get(i), DEFAULT_INITIAL_UPDATE);
		}
	}

	/**
	 * Returns the type of resilient propagation.
	 * 
	 * @return The type of resilient propagation.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type of resilient propagation.
	 * 
	 * @param type The type of resilient propagation.
	 */
	public void setType(Type type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
	}

	/**
	 * Called to perform an iteration.
	 */
	@Override
	public void afterIteration() {
		super.afterIteration();
		// Save last iteration error.
		lastError = getLastError();
	}

	/**
	 * Return the weight update to apply.
	 * 
	 * @param layer The layer.
	 * @param gradients The gradients of the layer.
	 * @param out The output neuron of the weights matrix.
	 * @param in The input neuron of the weights matrix.
	 * @return The weight update to apply.
	 */
	@Override
	protected double getWeightChange(int layer, double[][] gradients, int out, int in) {
		double weightChange = 0;
		switch (type) {
		case RPROPp:
			weightChange = updateWeightPlus(layer, gradients, out, in);
			break;
		case iRPROPp:
			weightChange = updateWeightPlus_i(layer, gradients, out, in);
			break;
		default:
			throw new IllegalArgumentException();
		}
		double[][] lastWeightChanges = getLastWeightChanges(layer);
		lastWeightChanges[out][in] = weightChange;
		return weightChange;
	}

	/**
	 * The classic RPROP algorithm. Uses weight back tracking.
	 * 
	 * @param layer The layer.
	 * @param gradients The gradients of the layer.
	 * @param out The output neuron of the weights matrix.
	 * @param in The input neuron of the weights matrix.
	 * @return The weight update to apply.
	 */
	private double updateWeightPlus(int layer, double[][] gradients, int out, int in) {

		double[][] lastWeightChanges = getLastWeightChanges(layer);
		double[][] lastGradients = getLastGradients(layer);
		double[][] lastDeltas = getLastDeltas(layer);

		double weightChange = 0;

		// We want to see if the gradient has changed its sign.
		int signum = signum(gradients[out][in] * lastGradients[out][in]);

		// If the gradient has retained its sign, then we increase the delta so that it will converge faster.
		if (signum > 0) {
			double delta = Math.min(lastDeltas[out][in] * POSITIVE_ETA, maximumStep);
			weightChange = signum(gradients[out][in]) * delta;
			lastDeltas[out][in] = delta;
			lastGradients[out][in] = gradients[out][in];
		}

		// If signum < 0, then the sign has changed, and the last delta was too big. Set the previous gradent to zero so
		// that there will be no adjustment the next iteration.
		if (signum < 0) {
			double delta = Math.max(lastDeltas[out][in] * NEGATIVE_ETA, DELTA_MIN);
			weightChange = -lastWeightChanges[out][in];
			lastDeltas[out][in] = delta;
			lastGradients[out][in] = 0;
		}

		// If signum == 0 then there is no change to the delta.
		if (signum == 0) {
			double delta = lastDeltas[out][in];
			weightChange = signum(gradients[out][in]) * delta;
			lastGradients[out][in] = gradients[out][in];
		}

		return weightChange;
	}

	/**
	 * The classic RPROP algorithm. Uses weight back tracking.
	 * 
	 * @param layer The layer.
	 * @param gradients The gradients of the layer.
	 * @param out The output neuron of the weights matrix.
	 * @param in The input neuron of the weights matrix.
	 * @return The weight update to apply.
	 */
	private double updateWeightPlus_i(int layer, double[][] gradients, int out, int in) {

		double[][] lastWeightChanges = getLastWeightChanges(layer);
		double[][] lastGradients = getLastGradients(layer);
		double[][] lastDeltas = getLastDeltas(layer);

		double weightChange = 0;

		// We want to see if the gradient has changed its sign.
		int signum = signum(gradients[out][in] * lastGradients[out][in]);

		// If the gradient has retained its sign, then we increase the delta so that it will converge faster.
		if (signum > 0) {
			double delta = Math.min(lastDeltas[out][in] * POSITIVE_ETA, maximumStep);
			weightChange = signum(gradients[out][in]) * delta;
			lastDeltas[out][in] = delta;
			lastGradients[out][in] = gradients[out][in];
		}

		// If signum < 0, then the sign has changed, and the last delta was too big. Set the previous gradent to zero so
		// that there will be no adjustment the next iteration.
		if (signum < 0) {
			double delta = Math.max(lastDeltas[out][in] * NEGATIVE_ETA, DELTA_MIN);
			if (getLastError() > lastError) {
				weightChange = -lastWeightChanges[out][in];
			}
			lastDeltas[out][in] = delta;
			lastGradients[out][in] = 0;
		}

		// If signum == 0 then there is no change to the delta.
		if (signum == 0) {
			double delta = lastDeltas[out][in];
			weightChange = signum(gradients[out][in]) * delta;
			lastGradients[out][in] = gradients[out][in];
		}

		return weightChange;
	}

	/**
	 * Return the last weight changes of the layer.
	 * 
	 * @param layer The layer.
	 * @return The last weight changes.
	 */
	public double[][] getLastWeightChanges(int layer) {
		return lastWeightChanges.get(layer);
	}

	/**
	 * Returns the last weight gradients.
	 * 
	 * @param layer The layer.
	 * @return The last gradients.
	 */
	public double[][] getLastGradients(int layer) {
		return lastGradients.get(layer);
	}

	/**
	 * Returns the last applied deltas.
	 * 
	 * @param layer The layer.
	 * @return The last deltas.
	 */
	public double[][] getLastDeltas(int layer) {
		return lastDeltas.get(layer);
	}
}
