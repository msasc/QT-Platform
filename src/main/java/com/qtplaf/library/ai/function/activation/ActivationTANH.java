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
 * The hyperbolic tangent activation function takes the curved shape of the hyperbolic tangent. This activation function
 * produces both positive and negative output. Use this activation function if both negative and positive output is
 * desired.
 * 
 * @author Miquel Sas
 */
public class ActivationTANH implements Activation {

	/**
	 * Default constructor.
	 */
	public ActivationTANH() {
	}

	/**
	 * Returns the output value of the function given the input value.
	 * 
	 * @param signal The signal (weighted sum) value.
	 * @return The output value.
	 */
	@Override
	public double getOutput(double signal) {
		return Math.tanh(signal);
	}

	/**
	 * Returns the first derivative of the function, given the output.
	 * 
	 * @param signal The signal applied to <i>getOutput</i>.
	 * @param output The output obtained applying the input value to <i>getOutput</i>.
	 * @return The first derivative of the output value.
	 */
	@Override
	public double getDerivative(double signal, double output) {
		return (1.0 - output * output);
	}
}
