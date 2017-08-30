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
package com.qtplaf.library.ai.function.activation;

import com.qtplaf.library.ai.function.Activation;

/**
 * A Sigmoid activation function.
 * 
 * @author Miquel Sas
 */
public class ActivationSigmoid implements Activation {

	/**
	 * Default constructor.
	 */
	public ActivationSigmoid() {
	}

	/**
	 * Returns the output values of the function given the input values.
	 * 
	 * @param triggers The trigger (weighted sum plus bias) values.
	 * @param outputs The outputs to set.
	 * @return The output values.
	 */
	@Override
	public void activations(double[] triggers, double[] outputs) {
		int length = triggers.length;
		for (int i = 0; i < length; i++) {
			outputs[i] = 1 / (1 + Math.exp(-triggers[i]));
		}
	}

	/**
	 * Returns the first derivatives of the function, given the signals and the outputs. Some activations require the
	 * output and some the signal.
	 * 
	 * @param triggers The triggers applied to <i>getOutputs</i>.
	 * @param outputs The outputs obtained applying the signals to <i>getOutputs</i>.
	 * @param derivatives The derivatives to set.
	 * @return The first derivatives.
	 */
	@Override
	public void derivatives(double[] triggers, double[] outputs, double[] derivatives) {
		int length = triggers.length;
		for (int i = 0; i < length; i++) {
			derivatives[i] = outputs[i] * (1 - outputs[i]);
		}
	}
}
