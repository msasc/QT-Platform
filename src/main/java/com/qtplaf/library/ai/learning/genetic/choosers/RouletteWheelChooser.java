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

package com.qtplaf.library.ai.learning.genetic.choosers;

import java.util.List;

import com.qtplaf.library.ai.learning.genetic.Chooser;
import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.ScoreUtils;
import com.qtplaf.library.util.Random;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Roulette wheel chooser.
 *
 * @author Miquel Sas
 */
public class RouletteWheelChooser implements Chooser {

	/** Minimize. */
	private boolean minimize;

	/**
	 * Constructor.
	 * 
	 * @param minimize Minimize flag.
	 */
	public RouletteWheelChooser(boolean minimize) {
		super();
		this.minimize = minimize;
	}

	/**
	 * Choose a network from a list of genomes.
	 * 
	 * @param genomes The source list.
	 * @return The choosed network.
	 */
	@Override
	public Genome choose(List<Genome> genomes) {
		// Sum scores.
		double totalScore = 0;
		for (Genome genome : genomes) {
			totalScore += genome.getScore();
		}
		if (totalScore == 0) {
			return ListUtils.randomGet(genomes);
		}
		// Uniform random 0-1
		double random = Random.nextDouble();
		// Cumulate weighted score.
		double score = 0;
		for (Genome genome : genomes) {
			score += genome.getScore() / totalScore;
			if (ScoreUtils.compare(random, score, minimize) > 0) {
				return genome;
			}
		}
		// Never should come here.
		throw new IllegalStateException();
	}

}
