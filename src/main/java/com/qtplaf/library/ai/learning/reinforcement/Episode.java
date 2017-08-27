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

/**
 * An episode of the reinforcement learning process with policy gradient strategy. The episode records the states and
 * the correspondant actions. The rewards, although they could be calculated at each state, must also be calculated when
 * the episode ends to compute the total reward of the episode.
 *
 * @author Miquel Sas
 */
public class Episode {

	/** List of state-action patterns. */
	private final List<EpisodePattern> episodePatterns = new ArrayList<>();
	/** A boolean to test encourage-discourage. */
	private Boolean encourage;

	/**
	 * Constructor.
	 */
	public Episode() {
		super();
	}

	/**
	 * Add a state-action pair to this episode.
	 * 
	 * @param state The state.
	 * @param action The action.
	 * @param reward The reward.
	 */
	public void add(State state, Action action, double reward) {
		episodePatterns.add(new EpisodePattern(this, state, action, reward));
	}

	/**
	 * Returns the total reward of the episode.
	 * 
	 * @return The total reward of the episode.
	 */
	public double getReward() {
		double reward = 0;
		for (int i = 0; i < episodePatterns.size(); i++) {
			reward += episodePatterns.get(i).getReward();
		}
		return reward;
	}

	/**
	 * Check if this episode should be encouraged.
	 * 
	 * @return A boolean.
	 */
	public boolean isEncourge() {
		if (encourage == null) {
			encourage = (getReward() > 0);
		}
		return encourage;
	}
	
	/**
	 * Check if this episode should be discouraged.
	 * 
	 * @return A boolean.
	 */
	public boolean isDiscourage() {
		return !isEncourge();
	}

	/**
	 * Return the size of this episode.
	 * 
	 * @return The size.
	 */
	public int size() {
		return episodePatterns.size();
	}

	/**
	 * Returns the list of episode patterns.
	 * 
	 * @return The list of episode patterns.
	 */
	public List<EpisodePattern> getEpisodePatterns() {
		return episodePatterns;
	}
}
