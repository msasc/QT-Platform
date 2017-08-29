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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.IterationError;
import com.qtplaf.library.ai.function.error.iteration.MeanSquared;
import com.qtplaf.library.ai.learning.LearningMethod;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.math.Matrix;
import com.qtplaf.library.util.task.Executor;

/**
 * Root back propagation algorithms.
 *
 * @author Miquel Sas
 */
public abstract class Propagation extends LearningMethod {

	/**
	 * Gradients calculator.
	 */
	class TaskGradients extends RecursiveAction {

		PatternSource source;
		List<double[][]> gradients;

		TaskGradients(PatternSource source) {
			this.source = source;
		}

		/**
		 * Compute the task.
		 */
		@Override
		protected void compute() {

			// Network and backward data.
			Network network = getNetwork();
			Network.Backward backward = new Network.Backward(network);

			// Iterate learning data.
			for (int i = 0; i < source.size(); i++) {

				// Retrive the pattern.
				Pattern pattern = source.get(i);
				double[] patternInputs = pattern.getInputs();

				// Process inputs.
				Network.Forward forward = Network.forward(network, patternInputs);

				// Network outputs.
				double[] networkOutputs = ListUtils.getLast(forward.getOutputs());

				// Calculate the output error.
				double[] errors = pattern.getErrors(networkOutputs);

				// Cumulate error.
				double error = getIterationErrorFunction().getError(errors);
				getIterationErrorFunction().addError(error);

				// Process backward.
				Network.backward(network, forward, backward, errors);
			}

			// Retrieve the gradients from the backward data.
			gradients = backward.getGradients();
		}
	}

	/** The neural network. */
	private Network network;
	/** Learning/training data source. */
	private PatternSource learningData;
	/** The optional check data, not seen during the learning process. */
	private PatternSource checkData;
	/** Iteration error function. */
	private IterationError iterationErrorFunction = new MeanSquared();
	/** Last calculated error. */
	private double lastError = 1.0;

	/** List of weight gradients of subsequent layers (number of layers-1). */
	private List<double[][]> gradients;
	/** List of tasks for parallel process. */
	private List<TaskGradients> tasks = new ArrayList<>();

	/** A boolean that indicates if pattern should be processed in batch mode. */
	private boolean batchMode = true;

	/**
	 * Constructor.
	 * 
	 * @param network The neural network.
	 */
	public Propagation(Network network) {
		super();
		this.network = network;
	}

	/**
	 * Check if process should be batch.
	 * 
	 * @return A boolean.
	 */
	public boolean isBatchMode() {
		return batchMode;
	}

	/**
	 * Set if process should be batch.
	 * 
	 * @param batchMode A boolean.
	 */
	public void setBatchMode(boolean batchMode) {
		this.batchMode = batchMode;
	}

	/**
	 * Returns the underlying network.
	 * 
	 * @return The network.
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Returns the learning data.
	 * 
	 * @return The learning data.
	 */
	public PatternSource getLearningData() {
		return learningData;
	}

	/**
	 * Set the learning data.
	 * 
	 * @param learningData The learning data.
	 */
	public void setLearningData(PatternSource learningData) {
		this.learningData = learningData;
	}

	/**
	 * Returns the check data.
	 * 
	 * @return The check data.
	 */
	public PatternSource getCheckData() {
		return checkData;
	}

	/**
	 * Set the check data.
	 * 
	 * @param checkData The check data.
	 */
	public void setCheckData(PatternSource checkData) {
		this.checkData = checkData;
	}

	/**
	 * Returns the error function.
	 * 
	 * @return The error function.
	 */
	public IterationError getIterationErrorFunction() {
		return iterationErrorFunction;
	}

	/**
	 * Set the error function.
	 * 
	 * @param iterationErrorFunction The error function.
	 */
	public void setIterationErrorFunction(IterationError iterationErrorFunction) {
		this.iterationErrorFunction = iterationErrorFunction;
	}

	/**
	 * Returns the error from the last iteration.
	 * 
	 * @return The error from the last iteration.
	 */
	public double getLastError() {
		return lastError;
	}

	/**
	 * Set the error from the last iteration.
	 * 
	 * @param lastError The last iteration error.
	 */
	protected void setLastError(double lastError) {
		this.lastError = lastError;
	}

	/**
	 * Ask extenders (back and resilient propagation) to return the weight change to apply. It is also responsibility of
	 * the extender to update last gradients and weight changes, because it is the extender that defines their use.
	 * 
	 * @param layer The layer.
	 * @param gradients The gradients of the layer.
	 * @param out The output neuron of the weights matrix.
	 * @param in The input neuron of the weights matrix.
	 * @return The weight update to apply.
	 */
	protected abstract double getWeightChange(int layer, double[][] gradients, int out, int in);

	/**
	 * Update the weights of the network, applying the weight update of the extender. It is the resposibility of the
	 * extender to update last gradients and weight changes, because it is the extender that defines their use.
	 */
	protected void updateWeights(List<double[][]> gradients) {

		for (int layer = 1; layer < getNetwork().getLayers(); layer++) {

			int layerOut = layer;
			int layerIn = layer - 1;
			int neuronsOut = getNetwork().getNeurons(layerOut);
			int neuronsIn = getNetwork().getNeurons(layerIn);

			double[][] weights = getNetwork().getWeights(layerOut);

			for (int out = 0; out < neuronsOut; out++) {
				for (int in = 0; in < neuronsIn; in++) {
					// Query the weight change.
					double weightChange = getWeightChange(layer, gradients.get(layer), out, in);
					// Apply it to the weight.
					weights[out][in] += weightChange;
				}
			}
		}
	}

	/**
	 * Update gradients from tasks and set task gradients to zero.
	 */
	private void updateGradients() {
		gradients = NetworkUtils.createLayerMatrices(getNetwork());
		for (int layer = 1; layer < getNetwork().getLayers(); layer++) {
			for (int i = 0; i < tasks.size(); i++) {
				TaskGradients task = tasks.get(i);
				Matrix.cumulate(task.gradients.get(layer), gradients.get(layer));
			}
		}
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	protected void performIteration() {
		if (isBatchMode()) {
			performIterationBatch();
		} else {
			performIterationOnline();
		}
	}

	/**
	 * Perform the iteration in batch mode.
	 */
	private void performIterationBatch() {

		// If the list of tasks is empty, build it.
		if (tasks.isEmpty()) {
			List<PatternSource> sources = getLearningData().getBatches();
			for (PatternSource source : sources) {
				tasks.add(new TaskGradients(source));
			}
		}

		// Reset the error function.
		getIterationErrorFunction().reset();

		// Ensure reinitialize.
		for (TaskGradients task : tasks) {
			task.reinitialize();
		}

		// Wait completion.
		ForkJoinPool.commonPool().invoke(new Executor(tasks));

		// Save last error.
		setLastError(getIterationErrorFunction().getTotalError());

		// Update gradients from tasks.
		updateGradients();

		// Update weights (do learn).
		updateWeights(gradients);
	}

	/**
	 * Perform the iteration in online mode.
	 */
	private void performIterationOnline() {

		// Reset the error function.
		getIterationErrorFunction().reset();
		
		// Network and backward data.
		Network network = getNetwork();
		
		// Iterate patterns.
		for (int i = 0; i < getLearningData().size(); i++) {

			// Retrieve the pattern.
			Pattern pattern = getLearningData().get(i);
			double[] patternInputs = pattern.getInputs();

			// Process inputs.
			Network.Forward forward = Network.forward(network, patternInputs);
			

			// Network outputs.
			double[] networkOutputs = ListUtils.getLast(forward.getOutputs());

			// Calculate the output error.
			double[] errors = pattern.getErrors(networkOutputs);

			// Accumulate error.
			double error = getIterationErrorFunction().getError(errors);
			getIterationErrorFunction().addError(error);
			
			// Process backward.
			Network.Backward backward = new Network.Backward(network);
			Network.backward(network, forward, backward, errors);
			
			// Update weights.
			updateWeights(backward.getGradients());
		}
	}
}
