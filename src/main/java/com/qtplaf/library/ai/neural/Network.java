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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.function.Activation;
import com.qtplaf.library.util.math.Matrix;

/**
 * Feed forward neural network.
 * <p>
 * All weights and related matrices are [rows=output, columns=input].
 * <p>
 * This network version does not accept context layers.
 *
 * @author Miquel Sas
 */
public class Network {

	/**
	 * Data generated during the back propagation.
	 * <ul>
	 * <li>Deltas or increases/decreases of the outputs, starting with the output error.</li>
	 * <li>Gradients or increases/decreases of the weights.</li>
	 * </ul>
	 *
	 * @author Miquel Sas
	 */
	public static class Backward {

		/** List of per layer deltas. */
		private final List<double[]> deltas;
		/** List of per layer gradients. */
		private final List<double[][]> gradients;

		/**
		 * Constructor.
		 * 
		 * @param network The network.
		 */
		public Backward(Network network) {
			super();
			this.deltas = NetworkUtils.createLayerVectors(network);
			this.gradients = NetworkUtils.createLayerMatrices(network);
		}

		/**
		 * Return the deltas of the layer.
		 * 
		 * @param layer The layer.
		 * @return The deltas.
		 */
		public double[] getDeltas(int layer) {
			return deltas.get(layer);
		}

		/**
		 * Return all the gradients.
		 * 
		 * @return The gradients.
		 */
		public List<double[][]> getGradients() {
			return gradients;
		}

		/**
		 * Return the gradients of the layer.
		 * 
		 * @param layer The layer.
		 * @return The gradients.
		 */
		public double[][] getGradients(int layer) {
			return gradients.get(layer);
		}
	}

	/**
	 * Data generated when the network processes inputs in a feed forward, composed of:
	 * <ul>
	 * <li>Signals or weighted sums of inputs.</li>
	 * <li>Triggers or signals plus biases.</li>
	 * <li>Outputs as a result of applying the activation to the signals.</li>
	 * </ul>
	 * Signals, triggers and outputs are recorded because in the back propagation process the derivative, depending on
	 * the activation function, can require the trigger or input or the output.
	 *
	 * @author Miquel Sas
	 */
	public static class Forward {

		/** List of per layer signals. */
		private final List<double[]> signals;
		/** List of per layer triggers. */
		private final List<double[]> triggers;
		/** List of per layer outputs. */
		private final List<double[]> outputs;

		/**
		 * Constructor.
		 * 
		 * @param network The network.
		 */
		public Forward(Network network) {
			super();
			this.signals = NetworkUtils.createLayerVectors(network);
			this.triggers = NetworkUtils.createLayerVectors(network);
			this.outputs = NetworkUtils.createLayerVectors(network);
		}

		/**
		 * Return the signals of the layer.
		 * 
		 * @param layer The layer.
		 * @return The signals.
		 */
		public double[] getSignals(int layer) {
			return signals.get(layer);
		}

		/**
		 * Return the triggers of the layer.
		 * 
		 * @param layer The layer.
		 * @return The triggers.
		 */
		public double[] getTriggers(int layer) {
			return triggers.get(layer);
		}

		/**
		 * Return the outputs of the layer.
		 * 
		 * @param layer The layer.
		 * @return The outputs.
		 */
		public double[] getOutputs(int layer) {
			return outputs.get(layer);
		}

		/**
		 * Return the list of per layer outputs.
		 * 
		 * @return The list of per layer outputs.
		 */
		public List<double[]> getOutputs() {
			return outputs;
		}
	}

	/** Flat spot to avoid near zero derivatives in the back propagation process. */
	private static double flatSpot = 0.01;

	/**
	 * Set the flat spot to avoid near zero derivatives.
	 * 
	 * @param flatSpot The flat spot to avoid near zero derivatives.
	 */
	public static void setFlatSpot(double flatSpot) {
		Network.flatSpot = flatSpot;
	}

	/**
	 * Backward process of the network deltas or errors and produce the backward data, cumulating gradients.
	 * 
	 * @param network The network.
	 * @param forward The forward data generated in the forward process.
	 * @param backward The backward data (gradients are cumulated)
	 * @param networkDeltas The network deltas or errors.
	 */
	public static void backward(
		Network network,
		Forward forward,
		Backward backward,
		double[] networkDeltas) {

		// Process the output layer.
		backwardOutputLayer(network, forward, backward, networkDeltas);
		// Process hidden layers.
		for (int layer = network.getLayers() - 2; layer >= 1; layer--) {
			backwardHiddenLayer(network, layer, forward, backward);
		}
		// Process the input layer.
		backwardInputLayer(network, forward, backward);
	}

	/**
	 * Backward process the output layer.
	 * 
	 * @param network The network.
	 * @param forward The forward data.
	 * @param backward The backward data.
	 * @param networkDeltas The network deltas or errors.
	 */
	private static void backwardOutputLayer(
		Network network,
		Forward forward,
		Backward backward,
		double[] networkDeltas) {

		int layer = network.getLayers() - 1;
		int neurons = network.getNeurons(layer);

		double[] triggers = forward.getTriggers(layer);
		double[] outputs = forward.getOutputs(layer);
		double[] deltas = backward.getDeltas(layer);

		double[] derivatives = new double[neurons];
		Activation activation = network.getActivation(layer);
		activation.derivatives(triggers, outputs, derivatives);

		for (int out = 0; out < neurons; out++) {
			double delta = networkDeltas[out] * (derivatives[out] + flatSpot);
			deltas[out] = delta;
		}
	}

	/**
	 * Backward process a hidden layer.
	 * 
	 * @param network The network.
	 * @param layer The layer.
	 * @param forward The forward data.
	 * @param backward The backward data.
	 */
	private static void backwardHiddenLayer(
		Network network,
		int layer,
		Forward forward,
		Backward backward) {

		int layerOut = layer + 1;
		int layerIn = layer;

		int neuronsOut = network.getNeurons(layerOut);
		int neuronsIn = network.getNeurons(layerIn);

		double[] triggersIn = forward.getTriggers(layerIn);
		double[] outputsIn = forward.getOutputs(layerIn);
		double[] deltasIn = backward.getDeltas(layerIn);
		double[] deltasOut = backward.getDeltas(layerOut);

		double[][] gradients = backward.getGradients(layerOut);
		double[][] weights = network.getWeights(layerOut);

		double[] derivativesIn = new double[neuronsIn];
		Activation activationIn = network.getActivation(layerIn);
		activationIn.derivatives(triggersIn, outputsIn, derivativesIn);

		for (int in = 0; in < neuronsIn; in++) {
			double output = outputsIn[in];
			double weightedDelta = 0;
			for (int out = 0; out < neuronsOut; out++) {
				double delta = deltasOut[out];
				double weight = weights[out][in];
				weightedDelta += (delta * weight);
				gradients[out][in] += (output * delta);
			}
			double delta = weightedDelta * (derivativesIn[in] + flatSpot);
			deltasIn[in] = delta;
		}
	}

	/**
	 * Backward process the input layer.
	 * 
	 * @param network The network.
	 * @param forward The forward data.
	 * @param backward The backward data.
	 */
	private static void backwardInputLayer(Network network, Forward forward, Backward backward) {

		int layerIn = 0;
		int layerOut = 1;

		int neuronsIn = network.getNeurons(layerIn);
		int neuronsOut = network.getNeurons(layerOut);

		double[] outputs = forward.getOutputs(layerIn);
		double[] deltas = backward.getDeltas(layerOut);

		double[][] gradients = backward.getGradients(layerOut);

		for (int in = 0; in < neuronsIn; in++) {
			double output = outputs[in];
			for (int out = 0; out < neuronsOut; out++) {
				double delta = deltas[out];
				gradients[out][in] += (output * delta);
			}
		}
	}

	/**
	 * Process the inputs and store the outputs.
	 * 
	 * @param network The network.
	 * @param networkInputs The inputs to process.
	 * @return The forward data.
	 */
	public static Forward forward(Network network, double[] networkInputs) {

		// Forward data.
		Forward forward = new Forward(network);

		// Input layer outputs are the network inputs.
		Matrix.copy(networkInputs, forward.getOutputs(0));

		// Subsequent layers.
		for (int i = 1; i < network.getLayers(); i++) {

			// Layers out and in.
			int layerOut = i;
			int layerIn = i - 1;

			int neuronsOut = network.getNeurons(layerOut);
			int neuronsIn = network.getNeurons(layerIn);

			double[] signals = forward.getSignals(layerOut);
			double[] triggers = forward.getTriggers(layerOut);
			double bias = network.getBias(layerOut);
			double[][] weights = network.getWeights(layerOut);

			double[] inputs = forward.getOutputs().get(layerIn);

			// Weighted sum.
			for (int out = 0; out < neuronsOut; out++) {

				// Signal, weighted inputs.
				double signal = 0;
				for (int in = 0; in < neuronsIn; in++) {
					double input = inputs[in];
					double weight = weights[out][in];
					signal += (input * weight);
				}
				signals[out] = signal;

				// Trigger.
				double trigger = signal + bias;
				triggers[out] = trigger;
			}

			double[] outputs = forward.getOutputs(layerOut);
			Activation activation = network.getActivation(layerOut);
			activation.activations(triggers, outputs);
		}

		return forward;
	}

	/** List of sizes of each layer. */
	private List<Integer> layers = new ArrayList<>();
	/** List of activations of neurons of subsequent layers (number of layers-1). */
	private List<Activation> activations = new ArrayList<>();
	/** List of biases of neurons of subsequent layers (number of layers-1). */
	private List<Double> biases = new ArrayList<>();
	/** List of weights of subsequent layers (number of layers-1). */
	private List<double[][]> weights = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Network() {
		super();
	}

	/**
	 * Creates an empty structure with the argument list of layer sizes. Weights and biases are left to zero, and
	 * activations are null. This method is intended for IO purposes to initialize the structure to later be filled with
	 * raw data.
	 * <p>
	 * This version creates only neurons of types <tt>Input</tt>, <tt>Hidden</tt> and <tt>Output</tt>.
	 * 
	 * @param sizes The list of layer sizes.
	 */
	public void createStructure(int... sizes) {

		// Clear lists for reuse.
		activations.clear();
		biases.clear();
		layers.clear();
		weights.clear();

		for (int i = 0; i < sizes.length; i++) {
			int sizeOut = sizes[i];
			int sizeIn = (i == 0 ? 0 : sizes[i - 1]);
			createStructure(sizeOut, sizeIn);
		}
	}

	/**
	 * Creates an empty structure for a layer.
	 * 
	 * @param sizeOut Output size.
	 * @param sizeIn Input size.
	 */
	private void createStructure(int sizeOut, int sizeIn) {
		layers.add(sizeOut);
		if (sizeIn > 0) {
			activations.add(null);
			biases.add(0d);
			weights.add(new double[sizeOut][sizeIn]);
		}
	}

	/**
	 * Add the input layer.
	 * 
	 * @param neurons The number of neurons.
	 */
	public void addLayer(int neurons) {
		if (!layers.isEmpty()) {
			throw new IllegalStateException();
		}
		layers.add(neurons);
	}

	/**
	 * Add subsequent layers with the given number of neurons, the same activation function for each neuron, and the
	 * same bias for each neuron. The output layer is the last layer added.
	 * 
	 * @param neurons The number of neurons.
	 * @param activation The activation function.
	 * @param bias The bias.
	 */
	public void addLayer(int neurons, Activation activation, double bias) {

		// Layer number.
		int layer = getLayers();

		// Set neurons.
		layers.add(neurons);

		// Activations.
		activations.add(activation);

		// Bias
		biases.add(bias);

		// Weights widths previous layer.
		int sizeOut = neurons;
		int sizeIn = getNeurons(layer - 1);
		double[][] layerWeights = new double[sizeOut][sizeIn];
		weights.add(layerWeights);
	}

	/**
	 * Returns the weights of the layer.
	 * 
	 * @param layer The layer.
	 * @return The weights.
	 */
	public double[][] getWeights(int layer) {
		if (layer == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return weights.get(layer - 1);
	}

	/**
	 * Returns the bias of the layer.
	 * 
	 * @param layer The layer.
	 * @return The bias.
	 */
	public double getBias(int layer) {
		if (layer == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return biases.get(layer - 1);
	}

	/**
	 * Set the bias of the layer.
	 * 
	 * @param layer The layer.
	 * @param bias The bias.
	 */
	public void setBias(int layer, double bias) {
		if (layer == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		biases.set(layer - 1, bias);
	}

	/**
	 * Returns the number of layers.
	 * 
	 * @return The number of layers.
	 */
	public int getLayers() {
		return layers.size();
	}

	/**
	 * Returns the number of neurons of the layer.
	 * 
	 * @param layer The layer number.
	 * @return The number of neurons of the layer.
	 */
	public int getNeurons(int layer) {
		return layers.get(layer);
	}

	/**
	 * Returns the activation function of the layer.
	 * 
	 * @param layer The layer.
	 * @return The activation function.
	 */
	public Activation getActivation(int layer) {
		if (layer == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return activations.get(layer - 1);
	}

	/**
	 * Set the activation of the layer.
	 * 
	 * @param layer The layer.
	 * @param activation The activation.
	 */
	public void setActivation(int layer, Activation activation) {
		activations.set(layer - 1, activation);
	}

	/**
	 * Create an exact copy of the network.
	 */
	@Override
	public Network clone() {
		Network network = new Network();
		for (int layer = 0; layer < getLayers(); layer++) {

			// Layer size (number of neurons)
			network.layers.add(new Integer(getNeurons(layer)));

			// Layer 0 is done.
			if (layer == 0) {
				continue;
			}

			// Activation
			network.activations.add(activations.get(layer));

			// Bias
			network.biases.add(biases.get(layer));

			// Weights
			double[][] layerWeights = weights.get(layer);
			double[][] targetWeights = new double[][] {};
			int neuronsOut = layerWeights.length;
			if (neuronsOut > 0) {
				int neuronsIn = layerWeights[0].length;
				targetWeights = new double[neuronsOut][neuronsIn];
				for (int out = 0; out < neuronsOut; out++) {
					for (int in = 0; in < neuronsIn; in++) {
						targetWeights[out][in] = layerWeights[out][in];
					}
				}
			}
			network.weights.add(targetWeights);
		}
		return network;
	}
}
