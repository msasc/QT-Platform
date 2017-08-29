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

import java.util.List;

/**
 * A source of patterns for learning or performance check.
 *
 * @author Miquel Sas
 */
public abstract class PatternSource {

	/**
	 * Constructor.
	 */
	public PatternSource() {
		super();
	}

	/**
	 * Returns the pattern at the given index.
	 * 
	 * @param index The index.
	 * @return The pattern.
	 */
	public abstract Pattern get(int index);

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	public abstract int size();

	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @return The list of sub-pattern sources.
	 */
	public abstract List<PatternSource> getBatches();

	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @param size The number of pattern batches.
	 * @return The list of sub-pattern sources.
	 */
	public abstract List<PatternSource> getBatches(int size);
}
