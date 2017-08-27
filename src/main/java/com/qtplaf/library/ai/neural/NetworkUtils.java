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

package com.qtplaf.library.ai.neural;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.Activation;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.math.Matrix;
import com.qtplaf.library.util.task.Executor;

/**
 * Network utilities.
 *
 * @author Miquel Sas
 */
public class NetworkUtils {

	/**
	 * Creates a structure of layer vectors of neurons size.
	 * 
	 * @param network The network.
	 * @return A structure of layer vectors.
	 */
	public static List<double[]> createLayerVectors(Network network) {
		List<double[]> layerVectors = new ArrayList<>();
		for (int layer = 0; layer < network.getLayers(); layer++) {
			layerVectors.add(new double[network.getNeurons(layer)]);
		}
		return layerVectors;
	}

	/**
	 * Creates a structure of layer matrices valid for gradients and similar.
	 * 
	 * @param network The network.
	 * @return A structure of layer matrices.
	 */
	public static List<double[][]> createLayerMatrices(Network network) {
		List<double[][]> matrices = new ArrayList<>();
		matrices.add(new double[][] {});
		for (int layer = 1; layer < network.getLayers(); layer++) {
			int layerOut = layer;
			int layerIn = layer - 1;
			int neuronsOut = network.getNeurons(layerOut);
			int neuronsIn = network.getNeurons(layerIn);
			matrices.add(new double[neuronsOut][neuronsIn]);
		}
		return matrices;
	}

	/**
	 * Returns all the activations as an array.
	 * 
	 * @param network The network.
	 * @return All the activations.
	 */
	public static Activation[] getActivations(Network network) {
		int size = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			size += network.getActivations(layer).length;
		}
		Activation[] data = new Activation[size];
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			Activation[] activations = network.getActivations(layer);
			for (int i = 0; i < activations.length; i++) {
				data[index++] = activations[i];
			}
		}
		return data;
	}

	/**
	 * Returns the biases as an array of double values.
	 * 
	 * @param network The network.
	 * @return All the biases.
	 */
	public static double[] getBiases(Network network) {
		int size = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			size += network.getBiases(layer).length;
		}
		double[] data = new double[size];
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			double[] biases = network.getBiases(layer);
			for (int i = 0; i < biases.length; i++) {
				data[index++] = biases[i];
			}
		}
		return data;
	}

	/**
	 * Set all the activations passing an array.
	 * 
	 * @param network The network.
	 * @param data The array of activations.
	 */
	public static void setActivations(Network network, Activation[] data) {
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			Activation[] activations = network.getActivations(layer);
			for (int i = 0; i < activations.length; i++) {
				activations[i] = data[index++];
			}
		}
	}

	/**
	 * Set all the biases passing an array of double values.
	 * 
	 * @param network The network.
	 * @param data The array of double values.
	 */
	public static void setBiases(Network network, double[] data) {
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			double[] biases = network.getBiases(layer);
			for (int i = 0; i < biases.length; i++) {
				biases[i] = data[index++];
			}
		}
	}

	/**
	 * Returns the weights as an array of double values.
	 * 
	 * @param network The network.
	 * @return All the weights.
	 */
	public static double[] getWeights(Network network) {
		int size = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			int neuronsOut = network.getNeurons(layer);
			int neuronsIn = network.getNeurons(layer - 1);
			size += (neuronsOut * neuronsIn);
		}
		double[] data = new double[size];
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			int neuronsOut = network.getNeurons(layer);
			int neuronsIn = network.getNeurons(layer - 1);
			double[][] weights = network.getWeights(layer);
			for (int out = 0; out < neuronsOut; out++) {
				for (int in = 0; in < neuronsIn; in++) {
					data[index++] = weights[out][in];
				}
			}
		}
		return data;
	}

	/**
	 * Set all the weights passing an array of double values that must have as values as the total number of weights.
	 * 
	 * @param network The network.
	 * @param data The array of double values.
	 */
	public static void setWeights(Network network, double[] data) {
		int index = 0;
		for (int layer = 1; layer < network.getLayers(); layer++) {
			int neuronsOut = network.getNeurons(layer);
			int neuronsIn = network.getNeurons(layer - 1);
			double[][] weights = network.getWeights(layer);
			for (int out = 0; out < neuronsOut; out++) {
				for (int in = 0; in < neuronsIn; in++) {
					weights[out][in] = data[index++];
				}
			}
		}
	}

	/**
	 * Task to calculate performance in parallel.
	 */
	private static class TaskPerformance extends RecursiveAction {
		/** The network. */
		Network network;
		/** Number of matches. */
		double matches;
		/** The pattern source. */
		PatternSource source;

		/**
		 * Constructor.
		 * 
		 * @param source The pattern source.
		 */
		TaskPerformance(Network network, PatternSource source) {
			super();
			this.network = network;
			this.source = source;
		}

		/**
		 * Compute the task.
		 */
		@Override
		protected void compute() {
			matches = 0;
			for (int i = 0; i < source.size(); i++) {
				Pattern pattern = source.get(i);
				double[] patternInputs = pattern.getInputs();
				double[] patternOutputs = pattern.getOutputs();
				Network.Forward processOutputs = Network.forward(network, patternInputs);
				double[] networkOutputs = ListUtils.getLast(processOutputs.getOutputs());
				if (Matrix.areEqual(patternOutputs, networkOutputs, 0)) {
					matches++;
				}
			}
		}
	}

	/**
	 * Returns the network performance.
	 * 
	 * @param network The network.
	 * @param source The source of patterns to chekc.
	 * @param decimals The decimal places of the reult.
	 * @return The performance.
	 */
	public static double getPerformance(Network network, PatternSource source, int decimals) {
		List<PatternSource> batches = source.getBatches();
		List<TaskPerformance> tasks = new ArrayList<>();
		for (PatternSource batch : batches) {
			TaskPerformance task = new TaskPerformance(network, batch);
			tasks.add(task);
		}
		ForkJoinPool.commonPool().invoke(new Executor(tasks));
		double matches = 0;
		double size = 0;
		for (TaskPerformance task : tasks) {
			matches += task.matches;
			size += task.source.size();
		}
		BigDecimal performance = NumberUtils.getBigDecimal(matches / size, decimals);
		return performance.doubleValue();
	}

	/**
	 * Randomize biases.
	 * 
	 * @param network The network.
	 */
	public static void randomizeBiases(Network network) {
		Random random = new Random();
		for (int layer = 1; layer < network.getLayers(); layer++) {
			double[] biases = network.getBiases(layer);
			for (int i = 0; i < biases.length; i++) {
				biases[i] = random.nextGaussian();
			}
		}
	}

	/**
	 * Randomize weights.
	 * 
	 * @param network The network.
	 */
	public static void randomizeWeights(Network network) {
		Random random = new Random();
		for (int layer = 1; layer < network.getLayers(); layer++) {
			int neuronsOut = network.getNeurons(layer);
			int neuronsIn = network.getNeurons(layer - 1);
			double[][] weights = network.getWeights(layer);
			for (int out = 0; out < neuronsOut; out++) {
				for (int in = 0; in < neuronsIn; in++) {
					weights[out][in] = random.nextGaussian();
				}
			}
		}
	}
}
