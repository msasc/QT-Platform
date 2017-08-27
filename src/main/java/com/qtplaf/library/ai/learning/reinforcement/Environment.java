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

package com.qtplaf.library.ai.learning.reinforcement;

import com.qtplaf.library.util.Properties;

/**
 * An environment of the reinforcement learning process with policy gradient strategy.
 *
 * @author Miquel Sas
 */
public abstract class Environment {

	/**
	 * 
	 */
	public Environment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return the environment state properties at a given time.
	 * 
	 * @return The environment state properties.
	 */
	public abstract Properties getState();

	/**
	 * Rewind this environment source of inputs.
	 */
	public abstract void rewind();

	/**
	 * Check whether the environment has inputs remaining to deliver.
	 * 
	 * @return A boolean.
	 */
	public abstract boolean hasNext();

	/**
	 * Return the next input to be delivered to the agent. At the time the environment delivers the next input it must
	 * set any state information related to the next state.
	 * 
	 * @return The next input.
	 */
	public abstract double[] next();
}
