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

package com.qtplaf.library.ai.learning.genetic.selectors;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.ScoreUtils;
import com.qtplaf.library.ai.learning.genetic.Selector;
import com.qtplaf.library.util.NumberUtils;

/**
 * A trucation selector that selects a percentage (ratio/factor) of the best members.
 *
 * @author Miquel Sas
 */
public class TruncationSelector implements Selector {
	
	/** The percentage (eg. 0.4) to select the winners. */
	private double factor = 0.3;
	/** Minimize. */
	private boolean minimize;

	/**
	 * Constructor.
	 * 
	 * @param factor The selection factor.
	 * @param minimize Minimize flag.
	 */
	public TruncationSelector(double factor, boolean minimize) {
		super();
		this.factor = factor;
		this.minimize = minimize;
	}

	/**
	 * Select some networks from a list.
	 * 
	 * @param genomes The source list.
	 * @return The selected networks.
	 */
	@Override
	public List<Genome> select(List<Genome> genomes) {
		
		// Source list.
		List<Genome> source = new ArrayList<>(genomes);
		
		// Sort it.
		source.sort(new ScoreUtils.Comparator(minimize));
		
		// Index of end element (exclusive).
		double size = Double.valueOf(source.size());
		int index = Double.valueOf(NumberUtils.round(size * factor, 0)).intValue();
		return source.subList(0, index);
	}

}
