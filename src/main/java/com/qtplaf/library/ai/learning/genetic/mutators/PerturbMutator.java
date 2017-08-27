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

import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.Mutator;
import com.qtplaf.library.util.Random;

/**
 * Randomly increase/decrease the weights.
 *
 * @author Miquel Sas
 */
public class PerturbMutator implements Mutator {

	/** A factor that indicates the proportion of the value to perturb. */
	private double perturbFactor = 0.5;
	/** A factor that indicates the proportion of values to perturb. */
	private double perturbSize = 0.5;

	/**
	 * Constructor.
	 * 
	 * @param perturbFactor The factor that indicates the proportion of the value to perturb.
	 * @param perturbSize The factor that indicates the proportion of values to perturb.
	 */
	public PerturbMutator(double perturbFactor, double perturbSize) {
		super();
		this.perturbFactor = perturbFactor;
		this.perturbSize = perturbSize;
	}

	/**
	 * Mutate the argument genome.
	 * 
	 * @param genome The genome to mutate.
	 * @return The mutated network.
	 */
	@Override
	public Genome mutate(Genome genome) {

		// Clone the genome.
		Genome mutated = new Genome(genome.getNetwork().clone());
		
		// Use a normalizer to calculate the delta to apply.
		StdNormalizer normalizer = new StdNormalizer();
		normalizer.setDataHigh(1.0);
		normalizer.setDataLow(0.0);

		// Subsequent layers.
		for (int layer = 1; layer < mutated.getNetwork().getLayers(); layer++) {
			
			// Weights.
			double[][] weights = mutated.getNetwork().getWeights(layer);

			// Rows (neurons layer out)
			int rows = mutated.getNetwork().getNeurons(layer);
			// Columns (neurons layer in)
			int cols = mutated.getNetwork().getNeurons(layer - 1);
			
			// Size (number of weights) to perturb.
			int size = Double.valueOf(Double.valueOf(rows * cols) * perturbSize).intValue();
			for (int i = 0; i < size; i++) {
				int row = Random.nextInt(rows);
				int col = Random.nextInt(cols);
				double value = weights[row][col];
				double normalizedHigh = Math.abs(value) * Math.abs(perturbFactor);
				double normalizedLow = (-1.0) * normalizedHigh;
				normalizer.setNormalizedHigh(normalizedHigh);
				normalizer.setNormalizedLow(normalizedLow);
				double delta = normalizer.normalize(Random.nextDouble());
				weights[row][col] += delta;
			}

		}

		return mutated;
	}

}
