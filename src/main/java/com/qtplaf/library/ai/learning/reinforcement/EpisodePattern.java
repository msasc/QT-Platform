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

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.util.Properties;

/**
 * A pattern or item of an episode. Handles the state, the action and the reward.
 *
 * @author Miquel Sas
 */
public class EpisodePattern implements Pattern {

	/** The parent episode. */
	private final Episode episode;
	/** The state. */
	private final State state;
	/** The action taken. */
	private final Action action;
	/** The current reward. */
	private final double reward;

	/**
	 * Constructor.
	 * 
	 * @param episode The parent episode.
	 * @param state The state.
	 * @param action The action.
	 * @param reward The reward.
	 */
	public EpisodePattern(Episode episode, State state, Action action, double reward) {
		super();
		this.episode = episode;
		this.state = state;
		this.action = action;
		this.reward = reward;
	}

	/**
	 * Return the parent episode.
	 * 
	 * @return The parent episode.
	 */
	public Episode getEpisode() {
		return episode;
	}

	/**
	 * Return the state.
	 * 
	 * @return The state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Return the action.
	 * 
	 * @return The action.
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Return the reward.
	 * 
	 * @return the reward
	 */
	public double getReward() {
		return reward;
	}

	/**
	 * Return the pattern inputs.
	 * 
	 * @return The pattern inputs.
	 */
	@Override
	public double[] getInputs() {
		return state.getInputs();
	}

	/**
	 * Return the optional pattern outputs, not applicable.
	 * 
	 * @return The pattern outputs.
	 */
	@Override
	public double[] getOutputs() {
		return null;
	}

	/**
	 * Return the errors given the network outputs. The errors are be conditioned by the overall episode reward and are
	 * determined by the action encourage or discourage gradients.
	 * 
	 * @param networkOutputs The list of network outputs.
	 * @return The errors.
	 */
	@Override
	public double[] getErrors(double[] networkOutputs) {
		return (episode.isEncourge() ? action.getGradientsEncourage() : action.getGradientsDiscourage());
	}

	/**
	 * Return the optional label.
	 * 
	 * @return The label.
	 */
	@Override
	public String getLabel() {
		return null;
	}

	/**
	 * Return the additional properties.
	 * 
	 * @return The additional properties.
	 */
	@Override
	public Properties getProperties() {
		return null;
	}
}
