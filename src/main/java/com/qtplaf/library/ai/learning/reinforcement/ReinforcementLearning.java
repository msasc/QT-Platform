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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.learning.LearningMethod;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A policy gradient reinforcement learning method. The policy gradient strategy has been choosen since it is the
 * natural function approximation for neural networks, and can be used to deal with environments with a huge number of
 * states.
 *
 * <h3>Agent</h3>
 *
 * The agent is a <b>feed forward neural network</b> that receives inputs and produces outputs as actions. The agent is
 * also responsible to maintain an agent state.
 *
 * <h3>Action</h3>
 *
 * The action is the output of the neural network. It is the resposibility of the designer to define a network that can
 * produce meaningful output actions.
 * <p>
 * Sometimes an action will require some <i>status</i> information to decide, for instance, suppose a simple trading
 * system with three posible positions:
 * <ul>
 * <li>1: position long</li>
 * <li>0: position out</li>
 * <li>-1: position short</li>
 * </ul>
 * <p>
 * and four possible actions, not all actions are possible at every position:
 * <ul>
 * <li>S: go short, only if current position is out</li>
 * <li>L: go long, only if current position is out</li>
 * <li>N: do nothing, can be done at every position</li>
 * <li>C: close the current position, only if the position is long or short</li>
 * </ul>
 * <p>
 * Such conditions must be coded in an activation function that must admit a derivative in order to properly apply a
 * back propagation of the gradient.
 * <p>
 * Additionally, the action must indicate if it is an intermediate, start or end of episode, to allow the environment to
 * start recording up to the end point and later apply the policy gradient strategy.
 * <p>
 * In the simple trading example, start is when the action takes a position, either long or short, and the end comes
 * when the action closes the position. Intermediate would be all actions between open and close, mainly do nothing.
 * <p>
 * Nearly any implementation can issue start and end points. In the classical <i>pong</i> game, the start point comes
 * with the first frame when a game starts, and the end point arrives when the game ends.
 * <p>
 * Finally, an action must provide gradients to encurage or discourage similar actions.
 * 
 * <h3>State</h3>
 *
 * The state is composed by the input delivered to the agent (the network), and any other necessary information to
 * calculate rewards at any time and for the agent to decide the next action.
 * <p>
 * In this implementation, the state has the inputs, the previous agent state and the current environment state.
 *
 * <h3>Environment</h3>
 *
 * The environment maintains its own state and provides the inputs for the agent.
 * 
 * <h3>Reward</h3>
 * 
 * The reward function receives the previous and current state and returns the reward.
 * 
 * @author Miquel Sas
 */
public class ReinforcementLearning extends LearningMethod {

	/** The agent, a neural network. */
	private Agent agent;
	/** The environment. */
	private Environment environment;
	/** Reward function. */
	private Reward rewardFunction;
	/** List of episodes record during the iteration. */
	private List<Episode> episodes = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public ReinforcementLearning() {
		super();
	}

	/**
	 * Set the agent.
	 * 
	 * @param agent The agent.
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * Set the environment.
	 * 
	 * @param environment The environment.
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Set the reward function.
	 * 
	 * @param reward The reward function.
	 */
	public void setRewardFunction(Reward reward) {
		this.rewardFunction = reward;
	}

	/**
	 * Returns the total reward of the current series of episodes.
	 * 
	 * @return The total reward.
	 */
	public double getReward() {
		double reward = 0;
		for (int i = 0; i < episodes.size(); i++) {
			reward += episodes.get(i).getReward();
		}
		return reward;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	protected void performIteration() {

		// Check necessary configuration.
		if (agent == null || environment == null || rewardFunction == null) {
			throw new IllegalStateException();
		}

		// Clear episodes and rewind the environment.
		episodes.clear();
		environment.rewind();

		// Loop through all inputs the environment can provide. Record episodes.
		boolean recording = false;
		State previousState = null;
		while (environment.hasNext()) {

			// Inputs to deliver to the agent. State and forward.
			double[] inputs = environment.next();
			State currentState = new State(inputs, agent.getState(), environment.getState());
			Action action = agent.processState(currentState);

			// If the action starts an episode, record it.
			if (!recording && action.isStartEpisode()) {
				Episode episode = new Episode();
				episodes.add(episode);
				recording = true;
			}

			// If in recording mode, do it.
			if (recording) {
				Episode episode = ListUtils.getLast(episodes);
				double reward = rewardFunction.getReward(previousState, currentState);
				episode.add(currentState, action, reward);
			}

			// Check whether recording mode terminates.
			if (recording && action.isEndEpisode()) {
				recording = false;
			}

			// Record previous state.
			previousState = currentState;
		}

		// All inputs have been delivered to the agent, it's time to analyze episodes and apply gradient corrections.

	}

}
