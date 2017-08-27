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
import com.qtplaf.library.util.list.ListUtils;

/**
 * Selection by tournaments.
 *
 * @author Miquel Sas
 */
public class TournamentSelector implements Selector {

	/** The percentage (eg. 0.3) to select the winners. */
	private double factor = 0.3;
	/** The number of rounds. */
	private int rounds = 4;
	/** Minimize. */
	private boolean minimize;

	/**
	 * Constructor.
	 * 
	 * @param factor Selection factor.
	 * @param rounds Number of rounds.
	 * @param minimize Minimize flag.
	 */
	public TournamentSelector(double factor, int rounds, boolean minimize) {
		super();
		this.factor = factor;
		this.rounds = rounds;
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

		// Number of solutions to select.
		double size = Double.valueOf(genomes.size());
		int count = Double.valueOf(NumberUtils.round(size * factor, 0)).intValue();

		// Create a copy of the list.
		List<Genome> source = new ArrayList<>(genomes);

		List<Genome> selection = new ArrayList<>();
		while (selection.size() < count) {
			List<Genome> tournament = new ArrayList<>();
			tournament.add(ListUtils.randomGet(source));
			for (int round = 0; round < rounds; round++) {
				tournament.add(ListUtils.randomGet(source));
			}
			Genome winner = ScoreUtils.getWinner(tournament, minimize);
			selection.add(winner);
			source.remove(winner);
		}

		return selection;
	}
}
