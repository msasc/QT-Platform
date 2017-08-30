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

import com.qtplaf.library.ai.learning.propagation.strategy.SmartLearningRate;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;

/**
 * Batch back propagation with learning rate and momentum.
 *
 * @author Miquel Sas
 */
public class BackPropagation extends Propagation {

	/** List of last weights updates. */
	private List<double[][]> lastWeightChanges;
	/** Learning rate. */
	private double learningRate;
	/** Momentum. */
	private double momentum;

	/**
	 * Constructor.
	 * 
	 * @param network The network.
	 */
	public BackPropagation(Network network) {
		this(network, 0, 0);
		addStrategy(new SmartLearningRate(this));
//		addStrategy(new SmartMomentum(this));
	}

	/**
	 * Constructor.
	 * 
	 * @param network The network.
	 * @param learningRate The learning rate.
	 * @param momentum The momentum.
	 */
	public BackPropagation(Network network, double learningRate, double momentum) {
		super(network);
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.lastWeightChanges = NetworkUtils.createLayerMatrices(getNetwork());
	}

	/**
	 * Returns the learning rate.
	 * 
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * Sets the learning rate.
	 * 
	 * @param learningRate The learning rate.
	 */
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * Returns the momentum.
	 * 
	 * @return The momentum.
	 */
	public double getMomentum() {
		return momentum;
	}

	/**
	 * Set the momentum.
	 * 
	 * @param momentum The momentum.
	 */
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	/**
	 * Return the weight change to apply. Updates the last weight change and does nothing with last gradients.
	 * 
	 * @param layer The layer.
	 * @param gradients The gradients of the layer.
	 * @param out The output neuron of the weights matrix.
	 * @param in The input neuron of the weights matrix.
	 * @return The weight update to apply.
	 */
	@Override
	protected double getWeightChange(int layer, double[][] gradients, int out, int in) {

		double[][] lastWeightChanges = getLastWeightChanges(layer);

		double gradient = gradients[out][in];
		double learningRate = this.learningRate;
		double lastChange = lastWeightChanges[out][in];
		double momentum = this.momentum;
		double weightChange = (gradient * learningRate) + (lastChange * momentum);

		lastWeightChanges[out][in] = weightChange;

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

}
