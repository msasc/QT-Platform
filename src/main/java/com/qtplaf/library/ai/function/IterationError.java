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
 * Interface used to calculate the error value for an error vector and accumulate it to register the total error in an
 * iteration learning method.
 * 
 * @author Miquel Sas
 */
public interface IterationError {

	/**
	 * Returns the total network error.
	 * 
	 * @return The total network error.
	 */
	double getTotalError();

	/**
	 * Returns the error value given a list of output errors, an error vector.
	 * 
	 * @param errorVector The error values.
	 * @return The error value.
	 */
	double getError(double[] errorVector);

	/**
	 * Adds the error to the total network error.
	 * 
	 * @param error The error to accumulate.
	 */
	void addError(double error);

	/**
	 * Reset the total error setting it to zero.
	 */
	void reset();
}
