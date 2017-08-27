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

import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.util.Properties;

/**
 * An agent of the reinforcement learning process with policy gradient strategy. The agent is mainly a neural network.
 *
 * @author Miquel Sas
 */
public abstract class Agent {

	/** The network. */
	private final Network network;

	/**
	 * Constructor.
	 * 
	 * @param network The internal network.
	 */
	public Agent(Network network) {
		super();
		this.network = network;
	}

	/**
	 * Returns the internal network.
	 * 
	 * @return The internal network.
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Request the agent to process a state and return the correspondant action. During each process request, the agent
	 * is resposible to setup its state properties.
	 * 
	 * @param state The state to process.
	 * @return The applied action.
	 */
	public abstract Action processState(State state);

	/**
	 * Return the agent state properties at a given time.
	 * 
	 * @return The agent state properties.
	 */
	public abstract Properties getState();
}
