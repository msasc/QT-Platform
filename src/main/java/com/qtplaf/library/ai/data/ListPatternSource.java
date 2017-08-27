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

package com.qtplaf.library.ai.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A list pattern source.
 *
 * @author Miquel Sas
 */
public class ListPatternSource extends PatternSource {

	/** The underlying pattern list. */
	private List<Pattern> patterns;
	/**
	 * Constructor.
	 * 
	 * @param patterns The list of patterns.
	 */
	public ListPatternSource(List<Pattern> patterns) {
		super();
		this.patterns = patterns;
	}

	/**
	 * Returns the pattern at the given index.
	 * 
	 * @param index The index.
	 * @return The pattern.
	 */
	@Override
	public Pattern get(int index) {
		return patterns.get(index);
	}

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	@Override
	public int size() {
		return patterns.size();
	}


	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isEmpty() {
		return patterns.isEmpty();
	}
	
	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @param size The number of pattern batches.
	 */
	@Override
	public List<PatternSource> getBatches() {
		return getBatches(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @param size The number of pattern batches.
	 * @return The list of sub-pattern sources.
	 */
	@Override
	public List<PatternSource> getBatches(int size) {
		List<PatternSource> subSources = new ArrayList<>();
		if (size > patterns.size()) {
			subSources.add(this);
			return subSources;
		}
		
		int segmentSize = patterns.size() / size;
		List<List<Pattern>> patternLists = new ArrayList<>();
		List<Pattern> patternList = new ArrayList<>();
		for (int i = 0; i < patterns.size(); i++) {
			if (patternList.size() == segmentSize) {
				if (patternLists.size() < size) {
					patternLists.add(patternList);
					patternList = new ArrayList<>();
				}
			}
			patternList.add(patterns.get(i));
		}
		if (!patternList.isEmpty()) {
			patternLists.add(patternList);
		}
		
		for (int i = 0; i < patternLists.size(); i++) {
			subSources.add(new ListPatternSource(patternLists.get(i)));
		}
		return subSources;
	}
}
