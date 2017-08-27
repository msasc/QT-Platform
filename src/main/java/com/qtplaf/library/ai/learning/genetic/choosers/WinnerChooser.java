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

/**
 * Winner (best) chooser.
 *
 * @author Miquel Sas
 */
public class WinnerChooser implements Chooser {

	/** Minimize. */
	private boolean minimize;

	/**
	 * Constructor.
	 * 
	 * @param minimize Minimize flag.
	 */
	public WinnerChooser(boolean minimize) {
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
		return ScoreUtils.getWinner(genomes, minimize);
	}

}
