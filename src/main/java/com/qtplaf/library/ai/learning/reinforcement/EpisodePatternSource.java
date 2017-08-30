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

import com.qtplaf.library.ai.data.ListPatternSource;
import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;

/**
 * A pattern source built from a list of episodes.
 *
 * @author Miquel Sas
 */
public class EpisodePatternSource extends PatternSource {

	/** The list of episodes. */
	private final List<Episode> episodes;
	/** The total list of episode patterns. */
	private List<EpisodePattern> allEpisodePatterns;

	/**
	 * Constructor.
	 * 
	 * @param episodes The list of episodes.
	 */
	public EpisodePatternSource(List<Episode> episodes) {
		super();
		this.episodes = episodes;
	}

	/**
	 * Return the list of all episodes.
	 * 
	 * @return The list of all episodes.
	 */
	private List<EpisodePattern> getAllEpisodePatterns() {
		if (allEpisodePatterns == null) {
			allEpisodePatterns = new ArrayList<>();
			for (int i = 0; i < episodes.size(); i++) {
				allEpisodePatterns.addAll(episodes.get(i).getEpisodePatterns());
			}
		}
		return allEpisodePatterns;
	}
	
	/**
	 * Returns the pattern at the given index.
	 * 
	 * @param index The index.
	 * @return The pattern.
	 */
	@Override
	public Pattern get(int index) {
		return getAllEpisodePatterns().get(index);
	}

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	@Override
	public int size() {
		return getAllEpisodePatterns().size();
	}

	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isEmpty() {
		return episodes.isEmpty();
	}

	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @return The list of sub-pattern sources.
	 */
	@Override
	public List<PatternSource> getBatches() {
		List<Pattern> patterns = new ArrayList<>();
		for (Episode episode : episodes) {
			patterns.addAll(episode.getEpisodePatterns());
		}
		ListPatternSource patternSource = new ListPatternSource(patterns);
		return patternSource.getBatches();
	}
}
