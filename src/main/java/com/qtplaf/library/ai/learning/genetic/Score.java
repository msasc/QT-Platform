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

package com.qtplaf.library.ai.learning.genetic;

/**
 * A score function used to score network outputs.
 *
 * @author Miquel Sas
 */
public interface Score {

	/**
	 * Calculate the score the network.
	 * 
	 * @param genome The genome.
	 * @return The score.
	 */
	double calculate(Genome genome);
	
	/**
	 * Return a boolean indicating if this score function best scores are the minimum ones.
	 * 
	 * @return A boolean.
	 */
	boolean isMinimize();
}
