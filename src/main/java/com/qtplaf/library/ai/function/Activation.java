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

package com.qtplaf.library.ai.function;

/**
 * Activation functions interface.
 *
 * @author Miquel Sas
 */
public interface Activation {

	/**
	 * Returns the output value of the function given the input value.
	 * 
	 * @param signal The signal (weighted sum) value.
	 * @return The output value.
	 */
	double getOutput(double signal);

	/**
	 * Returns the first derivative of the function, given the signal and the output. Some activations require the
	 * output and some the signal.
	 * 
	 * @param signal The signal applied to <i>getOutput</i>.
	 * @param output The output obtained applying the signal to <i>getOutput</i>.
	 * @return The first derivative of the output value.
	 */
	double getDerivative(double signal, double output);
}
