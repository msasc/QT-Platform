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

import java.util.List;

/**
 * Utilities to score.
 *
 * @author Miquel Sas
 */
public class ScoreUtils {

	/**
	 * Scored networks comparator.
	 */
	public static class Comparator implements java.util.Comparator<Genome> {

		/** Minimize. */
		private boolean minimize;

		/**
		 * Constructor.
		 * 
		 * @param minimize Minimize flag.
		 */
		public Comparator(boolean minimize) {
			super();
			this.minimize = minimize;
		}

		/**
		 * Do compare.
		 * 
		 * @param g1 First genome.
		 * @param g2 Second genome.
		 */
		@Override
		public int compare(Genome g1, Genome g2) {
			return ScoreUtils.compare(g1.getScore(), g2.getScore(), minimize);
		}

	}

	/**
	 * Compare two scores. Returns a negative integer, zero or a positive integer as the first score is better, equal to
	 * or worst than the second score.
	 * 
	 * @param score1 The first score.
	 * @param score2 The second score.
	 * @param minimize A boolean that indicates if scores should be minimized.
	 * @return A comparison integer.
	 */
	public static int compare(double score1, double score2, boolean minimize) {
		if (minimize) {
			if (score1 < score2) {
				return -1;
			}
			if (score1 > score2) {
				return 1;
			}
		} else {
			if (score1 > score2) {
				return -1;
			}
			if (score1 < score2) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Return the winner (best) of the list.
	 * 
	 * @param genomes The list of genomes.
	 * @param minimize A boolean that indicates if scores should be minimized.
	 * @return The winner or best genome.
	 */
	public static Genome getWinner(List<Genome> genomes, boolean minimize) {
		Genome winner = null;
		for (Genome member : genomes) {
			if (winner == null) {
				winner = member;
			}
			if (compare(winner.getScore(), member.getScore(), minimize) > 0) {
				winner = member;
			}
		}
		return winner;
	}

	/**
	 * Return the looser (worst) of the list.
	 * 
	 * @param genomes The list of genomes.
	 * @param minimize A boolean that indicates if scores should be minimized.
	 * @return The loser or worst genome.
	 */
	public static Genome getLooser(List<Genome> genomes, boolean minimize) {
		Genome looser = null;
		for (Genome member : genomes) {
			if (looser == null) {
				looser = member;
			}
			if (compare(looser.getScore(), member.getScore(), minimize) < 0) {
				looser = member;
			}
		}
		return looser;
	}
}
