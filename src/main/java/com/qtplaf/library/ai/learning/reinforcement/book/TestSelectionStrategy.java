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

package com.qtplaf.library.ai.learning.reinforcement.book;

import com.qtplaf.library.plot.Plot2D;
import com.qtplaf.library.util.Random;
import com.qtplaf.library.util.math.Matrix;

/**
 * Test decayed epsilon, optimistic, UCB1 and bayesian.
 *
 * @author Miquel Sas
 */
public class TestSelectionStrategy {
	/**
	 * Bandit.
	 *
	 * @author Miquel Sas
	 */
	static class Bandit {

		/**
		 * Get the best bandit index based on the mean.
		 * 
		 * @param bandits The list of bandits.
		 * @return The max mean bandit.
		 */
		public static Bandit getMaxMean(Bandit[] bandits) {
			Bandit max = null;
			for (int i = 0; i < bandits.length; i++) {
				Bandit bandit = bandits[i];
				if (max == null) {
					max = bandit;
				}
				if (bandit.mean > max.mean) {
					max = bandit;
				}
			}
			return max;
		}

		/**
		 * Get the bandit with max mean bounds.
		 * 
		 * @param n The sample index.
		 * @param bandits The list of bandits.
		 * @return The max bounds bandit.
		 */
		public static Bandit getMaxBounds(int n, Bandit[] bandits) {
			Bandit max = null;
			double maxBound = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < bandits.length; i++) {
				Bandit bandit = bandits[i];
				int ni = bandit.n + 1;
				double bound = bandit.mean + Math.sqrt(2 * Math.log(n) / ni);
				if (bound > maxBound) {
					maxBound = bound;
					max = bandit;
				}
			}
			return max;
		}

		/** True mean. */
		double m;
		/** Calculated mean. */
		double mean;
		/** Number of samples. */
		int n;

		/**
		 * Constructor, assign the true mean.
		 * @param m True mean.
		 */
		public Bandit(double m) {
			this.m = m;
			this.mean = 0;
			this.n = 0;
		}

		/**
		 * Simulates pulling the bandits arm.
		 * 
		 * @return The result.
		 */
		public double pull() {
			return Random.nextGaussian() + m;
		}

		/**
		 * Update the sampled mean.
		 * 
		 * @param x The new value.
		 */
		public void update(double x) {
			n += 1;
			mean = (1.0 - 1.0 / n) * mean + (x / n);
		}
	}
	/**
	 * Bayesian Bandit.
	 *
	 * @author Miquel Sas
	 */
	static class BBandit {

		/**
		 * Get the bandit with max sample.
		 * 
		 * @param bandits The list of bandits.
		 * @return The max bounds bandit.
		 */
		public static BBandit getMaxSample(BBandit[] bandits) {
			BBandit max = null;
			double maxSample = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < bandits.length; i++) {
				BBandit bandit = bandits[i];
				double sample = bandit.sample();
				if (sample > maxSample) {
					maxSample = sample;
					max = bandit;
				}
			}
			return max;
		}

		double m;
		double m0;
		double lambda0;
		double sum_x;
		double tau;

		/**
		 * Constructor, assign the true mean.
		 * @param m True mean.
		 */
		public BBandit(double m) {
			this.m = m;
			this.m0 = 0;
			this.lambda0 = 1;
			this.sum_x = 0;
			this.tau = 1;
		}

		/**
		 * Simulates pulling the bandits arm.
		 * 
		 * @return The result.
		 */
		public double pull() {
			return Random.nextGaussian() + m;
		}
		
		public double sample() {
			return m0 + (Random.nextGaussian() / Math.sqrt(lambda0));
		}

		/**
		 * Update the sampled mean.
		 * 
		 * @param x The new value.
		 */
		public void update(double x) {
			lambda0 += 1;
			sum_x += x;
			m0 = tau * sum_x / lambda0;
		}
	}

	/**
	 * Run the decayin epsilon experiment.
	 * 
	 * @param m1 B1 mean
	 * @param m2 B2 mean
	 * @param m3 B3 mean
	 * @param n Number of plays
	 * @return The cumulative average
	 */
	private static double[] runEps(double m1, double m2, double m3, int n) {

		// Bandits
		Bandit[] bandits = new Bandit[] { new Bandit(m1), new Bandit(m2), new Bandit(m3) };

		// Each play value (x)
		double[] x = new double[n];

		// Do play
		for (int i = 0; i < n; i++) {
			// Epsilon-greedy
			Bandit bandit;
			if (Random.nextDouble() < (1.0 / (i + 1))) {
				bandit = bandits[Random.nextInt(bandits.length)];
			} else {
				bandit = Bandit.getMaxMean(bandits);
			}
			// Pull and update
			x[i] = bandit.pull();
			bandit.update(x[i]);
		}

		// Cumulative average.
		double[] cum = Matrix.avgCumulative(x);

		// Plot it.
//		Plot2D plot = new Plot2D();
//		plot.addShape(cum);
//		plot.addShape(Matrix.add(m1, new double[n]));
//		plot.addShape(Matrix.add(m2, new double[n]));
//		plot.addShape(Matrix.add(m3, new double[n]));
//		plot.setXScaleLogarithmic();
//		plot.show("Decaying epsilon");

		return cum;
	}

	/**
	 * Run the UCB1 experiment.
	 * 
	 * @param m1 B1 mean
	 * @param m2 B2 mean
	 * @param m3 B3 mean
	 * @param n Number of plays
	 * @return The cumulative average
	 */
	private static double[] runUCB(double m1, double m2, double m3, int n) {

		// Bandits
		Bandit[] bandits = new Bandit[] { new Bandit(m1), new Bandit(m2), new Bandit(m3) };

		// Each play value (x)
		double[] x = new double[n];

		// Do play
		for (int i = 0; i < n; i++) {
			// Greedy
			Bandit bandit = Bandit.getMaxBounds(i + 1, bandits);
			// Pull and update
			x[i] = bandit.pull();
			bandit.update(x[i]);
		}

		// Cumulative average.
		double[] cum = Matrix.avgCumulative(x);

//		Plot2D plot = new Plot2D();
//		plot.addShape(cum);
//		plot.addShape(Matrix.add(m1, new double[n]));
//		plot.addShape(Matrix.add(m2, new double[n]));
//		plot.addShape(Matrix.add(m3, new double[n]));
//		plot.setXScaleLogarithmic();
//		plot.show("UCB1 (Confidence bounds");

		return cum;
	}

	/**
	 * Run the Bayesian experiment.
	 * 
	 * @param m1 B1 mean
	 * @param m2 B2 mean
	 * @param m3 B3 mean
	 * @param n Number of plays
	 * @return The cumulative average
	 */
	private static double[] runBayes(double m1, double m2, double m3, int n) {
		
		// Bandits
		BBandit[] bandits = new BBandit[] {	new BBandit(m1), new BBandit(m2), new BBandit(m3) };

		// Each play value (x)
		double[] x = new double[n];

		// Do play
		for (int i = 0; i < n; i++) {
			// Optimistic max sample.
			BBandit bandit = BBandit.getMaxSample(bandits);
			// Pull and update
			x[i] = bandit.pull();
			bandit.update(x[i]);
		}

		// Cumulative average.
		double[] cum = Matrix.avgCumulative(x);

//		Plot2D plot = new Plot2D();
//		plot.addShape(cum);
//		plot.addShape(Matrix.add(m1, new double[n]));
//		plot.addShape(Matrix.add(m2, new double[n]));
//		plot.addShape(Matrix.add(m3, new double[n]));
//		plot.setXScaleLogarithmic();
//		plot.show("Bayesian");

		return cum;
	}

	/**
	 * @param args Start arguments.
	 */
	public static void main(String[] args) {
		double[] eps = runEps(1.0, 2.0, 3.0, 50000);
		double[] ucb = runUCB(1.0, 2.0, 3.0, 50000);
		double[] bay = runBayes(1.0, 2.0, 3.0, 50000);
		
		Plot2D plot = new Plot2D();
		plot.addShape(eps);
		plot.addShape(ucb);
		plot.addShape(bay);
		plot.setXScaleLogarithmic();
		plot.show("All");
	}

}
