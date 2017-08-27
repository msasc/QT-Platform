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
 * DistanceFunction functions interface, aimed to mesure the difference between two vectors.
 *
 * @author Miquel Sas
 */
public interface Distance {

	/**
	 * Returns the distance between the two vectors.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The distance.
	 */
	double calculate(double[] a, double[] b);
}
