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

package com.qtplaf.library.ai.learning.genetic;

import com.qtplaf.library.ai.neural.Network;

/**
 * A genome, a network with a score.
 *
 * @author Miquel Sas
 */
public class Genome {

	/** The network. */
	private final Network network;
	/** The score . */
	private double score;

	/**
	 * Constructor.
	 * 
	 * @param network The network.
	 */
	public Genome(Network network) {
		super();
		this.network = network;
	}

	/**
	 * Return the network.
	 * 
	 * @return The network.
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Return the score.
	 * 
	 * @return The score.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Set the score.
	 * 
	 * @param score The score.
	 */
	public void setScore(double score) {
		this.score = score;
	}

}
