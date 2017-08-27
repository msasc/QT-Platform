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

package com.qtplaf.library.ai.learning;

/**
 * Training strategies are called before and after the iteration and have access to the training method to change
 * parameters. It is the responsibility of the strategy to get the instance of the appropriate learning method.
 *
 * @author Miquel Sas
 */
public interface Strategy {
	
	/**
	 * Called before the learning method starts the first iteration.
	 */
	void initialize();

	/**
	 * Called before the iteration.
	 */
	void beforeIteration();

	/**
	 * Called after the iteration.
	 */
	void afterIteration();
}
