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
import java.util.Arrays;
import java.util.List;

import com.qtplaf.library.ai.function.Activation;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.math.Matrix;

/**
 * Feedforward neural network.
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
		 * Return all the deltas.
		 * 
		 * @return The deltas.
		 */
		public List<double[]> getDeltas() {
			return deltas;
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
	 * <li>Ouputs as a result of applying the activation to the signals.</li>
	 * </ul>
	 * Signals and ouputs should be recorded because in the back propagation process the derivative, depending on the
	 * activation function, can require the signal or input or the output.
	 *
	 * @author Miquel Sas
	 */
	public static class Forward {

		/** List of per layer signals. */
		private final List<double[]> signals;
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
		 * Return the list of per layer signals.
		 * 
		 * @return The list of per layer signals.
		 */
		public List<double[]> getSignals() {
			return signals;
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
	
		double[] signals = forward.getSignals(layer);
		double[] outputs = forward.getOutputs(layer);
		double[] deltas = backward.getDeltas(layer);
	
		Activation[] activations = network.getActivations(layer);
	
		for (int out = 0; out < neurons; out++) {
			double signal = signals[out];
			double output = outputs[out];
			Activation activation = activations[out];
			double derivative = activation.getDerivative(signal, output) + flatSpot;
			double delta = networkDeltas[out] * derivative;
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
	
		double[] signalsIn = forward.getSignals(layerIn);
		double[] outputsIn = forward.getOutputs(layerIn);
		double[] deltasIn = backward.getDeltas(layerIn);
		double[] deltasOut = backward.getDeltas(layerOut);
	
		double[][] gradients = backward.getGradients(layerOut);
		double[][] weights = network.getWeights(layerOut);
	
		Activation[] activationsIn = network.getActivations(layerIn);
	
		for (int in = 0; in < neuronsIn; in++) {
			double signal = signalsIn[in];
			double output = outputsIn[in];
			double weightedDelta = 0;
			for (int out = 0; out < neuronsOut; out++) {
				double delta = deltasOut[out];
				double weight = weights[out][in];
				weightedDelta += (delta * weight);
				gradients[out][in] += (output * delta);
			}
			Activation activation = activationsIn[in];
			double derivative = activation.getDerivative(signal, output) + flatSpot;
			double delta = weightedDelta * derivative;
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
			double[] outputs = forward.getOutputs(layerOut);
			Activation[] activations = network.getActivations(layerOut);
			double[] biases = network.getBiases(layerOut);
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
	
				// Save signal.
				signals[out] = signal;
	
				// Activate.
				Activation activation = activations[out];
				double bias = biases[out];
				double trigger = signal + bias;
				double output = activation.getOutput(trigger);
	
				// Save output.
				outputs[out] = output;
			}
		}
	
		return forward;
	}

	/** List of sizes of each layer. */
	private List<Integer> layers = new ArrayList<>();
	/** List of activations of neurons of subsequent layers (number of layers-1). */
	private List<Activation[]> activations = new ArrayList<>();
	/** List of biases of neurons of subsequent layers (number of layers-1). */
	private List<double[]> biases = new ArrayList<>();
	/** List of weights of subsequent layers (number of layers-1). */
	private List<double[][]> weights = new ArrayList<>();
	/** List of types of neurons per layer. */
	private List<NeuronType> types = new ArrayList<>();

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
		types.clear();
		weights.clear();

		for (int i = 0; i < sizes.length; i++) {
			int sizeOut = sizes[i];
			int sizeIn = (i == 0 ? 0 : sizes[i - 1]);
			NeuronType type =
				(i == 0 ? NeuronType.Input : (i < sizes.length - 1 ? NeuronType.Hidden : NeuronType.Output));
			createStructure(sizeOut, sizeIn, type);
		}
	}

	/**
	 * Creates an empty structure for a layer.
	 * 
	 * @param sizeOut Output size.
	 * @param sizeIn Input size.
	 * @param type Neuron type.
	 */
	private void createStructure(int sizeOut, int sizeIn, NeuronType type) {
		layers.add(sizeOut);
		if (sizeIn == 0) {
			activations.add(new Activation[] {});
			biases.add(new double[] {});
			weights.add(new double[][] {});
		} else {
			activations.add(new Activation[sizeOut]);
			biases.add(new double[sizeOut]);
			weights.add(new double[sizeOut][sizeIn]);
		}
		types.add(type);

	}

	/**
	 * Add the input layer.
	 * 
	 * @param neurons The number of neurons.
	 */
	public void addInputLayer(int neurons) {
		if (!layers.isEmpty()) {
			throw new IllegalStateException();
		}
		layers.add(neurons);
		activations.add(new Activation[] {});
		biases.add(new double[] {});
		weights.add(new double[][] {});

		// Set the layer type.
		types.add(NeuronType.Input);
	}

	/**
	 * Add a hidden layer with the given number of neurons, the same activation function for each neuron, and the same
	 * bias for each neuron. The output layer is the last layer added.
	 * 
	 * @param neurons The number of neurons.
	 * @param activation The activation function.
	 * @param bias The bias.
	 */
	public void addHiddenLayer(int neurons, Activation activation, double bias) {

		// Not callable for the input layer.
		if (layers.isEmpty()) {
			throw new IllegalStateException();
		}

		// Not callable if the output layer has been added.
		if (ListUtils.getLast(types) == NeuronType.Output) {
			throw new IllegalStateException();
		}

		// Add the layer structure.
		addLayer(neurons, activation, bias);

		// Set the layer type.
		types.add(NeuronType.Hidden);
	}

	/**
	 * Add the output layer with the given number of neurons, the same activation function for each neuron, and the same
	 * bias for each neuron. The output layer is the last layer added.
	 * 
	 * @param neurons The number of neurons.
	 * @param activation The activation function.
	 * @param bias The bias.
	 */
	public void addOutputLayer(int neurons, Activation activation, double bias) {

		// Not callable for the input layer.
		if (layers.isEmpty()) {
			throw new IllegalStateException();
		}

		// Add the layer structure.
		addLayer(neurons, activation, bias);

		// Set the layer type.
		types.add(NeuronType.Output);
	}

	/**
	 * Add subsequent layers with the given number of neurons, the same activation function for each neuron, and the
	 * same bias for each neuron. The output layer is the last layer added.
	 * 
	 * @param neurons The number of neurons.
	 * @param activation The activation function.
	 * @param bias The bias.
	 */
	private void addLayer(int neurons, Activation activation, double bias) {

		// Layer number.
		int layer = getLayers();

		// Set neurons.
		layers.add(neurons);

		// Activations.
		Activation[] layerActivations = new Activation[neurons];
		Arrays.fill(layerActivations, activation);
		activations.add(layerActivations);

		// Biases
		double[] layerBiases = new double[neurons];
		Arrays.fill(layerBiases, bias);
		biases.add(layerBiases);

		// Weights withs previous layer.
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
			throw new IllegalArgumentException();
		}
		return weights.get(layer);
	}

	/**
	 * Returns the biases of the layer.
	 * 
	 * @param layer The layer.
	 * @return The biases.
	 */
	public double[] getBiases(int layer) {
		if (layer == 0) {
			throw new IllegalArgumentException();
		}
		return biases.get(layer);
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
	 * Returns the activations of the layer.
	 * 
	 * @param layer The layer.
	 * @return The activations.
	 */
	public Activation[] getActivations(int layer) {
		if (layer == 0) {
			throw new IllegalArgumentException();
		}
		return activations.get(layer);
	}

	/**
	 * Returns the type of the layer.
	 * 
	 * @param layer The layer.
	 * @return The type.
	 */
	public NeuronType getType(int layer) {
		return types.get(layer);
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

			// Neuron types
			network.types.add(types.get(layer));

			// Activations
			Activation[] layerActivations = activations.get(layer);
			Activation[] targetActivations = new Activation[layerActivations.length];
			for (int i = 0; i < layerActivations.length; i++) {
				targetActivations[i] = layerActivations[i];
			}
			network.activations.add(targetActivations);

			// Biases
			double[] layerBiases = biases.get(layer);
			double[] targetBiases = new double[layerBiases.length];
			for (int i = 0; i < layerBiases.length; i++) {
				targetBiases[i] = layerBiases[i];
			}
			network.biases.add(targetBiases);

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
