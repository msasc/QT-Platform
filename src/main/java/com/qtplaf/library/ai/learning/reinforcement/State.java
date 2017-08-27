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
 * A state of the reinforcement learning process with policy gradient strategy.
 * <p>
 * A <b><i>State</i></b> contains the following information components:
 * <ul>
 * <li>The input vector that will be delivered to the agent to be processed to produce the next action.</li>
 * <li>The necessary agent state properties at the current time.</li>
 * <li>Any necessary environment state properties, visible or not to the agent, at the current time.</li>
 * </ul>
 *
 * @author Miquel Sas
 */
public class State {

	/** Inputs of the network. */
	private double[] inputs;
	/** Any agent state properties. */
	private final Properties agentState;
	/** Any environment state properties. */
	private final Properties environmentState;

	/**
	 * Constructor.
	 * 
	 * @param inputs Inputs.
	 * @param agentState The agent state properties.
	 * @param environmentState The environment state properties.
	 */
	public State(double[] inputs, Properties agentState, Properties environmentState) {
		super();
		this.inputs = inputs;
		this.agentState = agentState;
		this.environmentState = environmentState;
	}

	/**
	 * Return the inputs required for the network.
	 * 
	 * @return The inputs.
	 */
	public double[] getInputs() {
		return inputs;
	}

	/**
	 * Returns the agent properties.
	 * 
	 * @return The agent properties.
	 */
	public Properties getAgentState() {
		return agentState;
	}

	/**
	 * Return the environment properties.
	 * 
	 * @return The environment properties.
	 */
	public Properties getEnvironmentState() {
		return environmentState;
	}
}
