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

import com.qtplaf.library.ai.learning.genetic.Chooser;
import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.Selector;
import com.qtplaf.library.ai.learning.genetic.choosers.RouletteWheelChooser;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.Random;

/**
 * A roulette wheel selector.
 *
 * @author Miquel Sas
 */
public class RouletteWheelSelector implements Selector {
	
	/** The percentage (eg. 0.4) to select the winners. */
	private double factor = 0.3;
	/** Minimize. */
	private boolean minimize;

	/**
	 * Constructor.
	 * 
	 * @param factor The percentage (eg. 0.4) to select the winners.
	 * @param minimize Minimize flag.
	 */
	public RouletteWheelSelector(double factor, boolean minimize) {
		super();
		this.factor = factor;
		this.minimize = minimize;
	}

	/**
	 * Select some genomes from a list.
	 * 
	 * @param genomes The source list.
	 * @return The selected genomes.
	 */
	@Override
	public List<Genome> select(List<Genome> genomes) {
		
		// Source list.
		List<Genome> source = new ArrayList<>(genomes);
		
		// Number of members to select.
		int count = 0;
		if (factor > 0) {
			double size = Double.valueOf(source.size());
			count = Math.min(Double.valueOf(NumberUtils.round(size * factor, 0)).intValue(), source.size());
		} else {
			count = Math.min(Random.nextInt(source.size()), 1);
		}
		
		// Roulette wheel chooser.
		Chooser rw = new RouletteWheelChooser(minimize);
		
		// Do select.
		List<Genome> selection = new ArrayList<>();
		while (selection.size() < count) {
			Genome choosed = rw.choose(source);
			selection.add(choosed);
			source.remove(choosed);
		}		
		
		return selection;
	}

}
