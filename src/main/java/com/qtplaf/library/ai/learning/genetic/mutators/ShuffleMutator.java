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

package com.qtplaf.library.ai.learning.genetic.mutators;

import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.Mutator;
import com.qtplaf.library.util.Random;

/**
 * Mutate by randomly flipping weights.
 *
 * @author Miquel Sas
 */
public class ShuffleMutator implements Mutator {

	/** Number of flips. */
	private int flips = 500;

	/**
	 * Constructor.
	 */
	public ShuffleMutator() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param flips Number of flips.
	 */
	public ShuffleMutator(int flips) {
		super();
		this.flips = flips;
	}

	/**
	 * Mutate the argument network.
	 * 
	 * @param genome The genome to mutate.
	 * @return The mutated network.
	 */
	@Override
	public Genome mutate(Genome genome) {

		// Clone the network.
		Genome mutated = new Genome(genome.getNetwork().clone());

		// Subsequent layers.
		for (int layer = 1; layer < mutated.getNetwork().getLayers(); layer++) {
			
			// Weights.
			double[][] weights = mutated.getNetwork().getWeights(layer);

			// Rows (neurons layer out)
			int rows = mutated.getNetwork().getNeurons(layer);
			// Columns (neurons layer in)
			int cols = mutated.getNetwork().getNeurons(layer - 1);
			
			for (int flip = 0; flip < flips; flip++) {
				
				// Flips rows and columns.
				int r1 = Random.nextInt(rows);
				int c1 = Random.nextInt(cols);
				int r2 = Random.nextInt(rows);
				int c2 = Random.nextInt(cols);
				
				// Do flip.
				double w = weights[r1][c1];
				weights[r1][c1] = weights[r2][c2];
				weights[r2][c2] = w;
			}
		}
		
		return mutated;
	}

}
