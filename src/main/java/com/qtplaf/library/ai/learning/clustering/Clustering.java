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

package com.qtplaf.library.ai.learning.clustering;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.learning.LearningMethod;

/**
 * Abstract clustering.
 *
 * @author Miquel Sas
 */
public abstract class Clustering extends LearningMethod {

	/** List of clusters. */
	private List<Cluster> clusters = new ArrayList<>();
	/** Changes after the iteration. */
	private int changes;

	/**
	 * Constructor.
	 */
	public Clustering() {
		super();
	}

	/**
	 * Add a cluster.
	 * 
	 * @param cluster The cluster.
	 */
	public void addCluster(Cluster cluster) {
		clusters.add(cluster);
	}

	/**
	 * Returns the cluster.
	 * 
	 * @param index The index of the cluster.
	 * @return The cluster.
	 */
	public Cluster getCluster(int index) {
		return clusters.get(index);
	}

	/**
	 * Return the number of clusters.
	 * 
	 * @return The number of clusters.
	 */
	public int getClusterCount() {
		return clusters.size();
	}

	/**
	 * Remove the cluster.
	 * 
	 * @param index The index.
	 * @return The removed cluster.
	 */
	public Cluster removeCluster(int index) {
		return clusters.remove(index);
	}

	/**
	 * Initialize the clustering process.
	 * 
	 * @param source The list of patterns.
	 */
	public abstract void initializeClustering(PatternSource source);

	/**
	 * Returns the best matching cluster for the pattern applying algorithm criteria..
	 * 
	 * @param pattern The pattern.
	 * @return The best matching cluster.
	 */
	public abstract Cluster getBestMatchingCluster(Pattern pattern);

	/**
	 * Initialize the iteration.
	 */
	protected abstract void initializeIteration();

	/**
	 * Returns a boolean indicating whether there are more patterns pending to process in the current iteration.
	 * 
	 * @return A boolean.
	 */
	protected abstract boolean hasMoreIterationPatterns();

	/**
	 * Return the next iteration pattern.
	 * 
	 * @return The next iteration pattern.
	 */
	protected abstract Pattern nextIterationPattern();

	/**
	 * Returns the number of changes performed after the iteration.
	 * 
	 * @return The current number of changes.
	 */
	public int getChanges() {
		return changes;
	}

	/**
	 * Called after an iteration ends, to allow any internal after iteration processing.
	 */
	@Override
	public void afterIteration() {
	}

	/**
	 * Called before an iteration starts, to allow any internal before iteration processing.
	 */
	@Override
	public void beforeIteration() {
	}

	/**
	 * Return the cluster that contains the pattern.
	 * 
	 * @param pattern The target pattern.
	 * @return The cluster that contains the pattern or null.
	 */
	private Cluster getCluster(Pattern pattern) {
		for (Cluster cluster : clusters) {
			if (cluster.containsPattern(pattern)) {
				return cluster;
			}
		}
		return null;
	}

	/**
	 * Perform an iteration and return the number of changes or pattern movements.
	 */
	@Override
	public void performIteration() {

		// Call any before iteration processing.
		beforeIteration();

		// Initialize the iteration.
		fireLearningEvent("Initializing iteration");
		initializeIteration();

		// Number of changes performed.
		changes = 0;

		// Iterate patterns.
		int patternNumber = 0;
		while (hasMoreIterationPatterns()) {
			fireLearningEvent("Processing pattern " + (++patternNumber));
			Pattern pattern = nextIterationPattern();

			// Do not leave an empty cluster. If the pattern has a cluster assigned and the cluster has only one
			// pattern, skip processing the pattern.
			Cluster patternCluster = getCluster(pattern);
			if (patternCluster != null && patternCluster.getPatternCount() == 1) {
				continue;
			}

			// Find the best matching cluster.
			Cluster bestMatching = getBestMatchingCluster(pattern);
			if (bestMatching != null) {

				if (patternCluster == null) {
					bestMatching.addPattern(pattern);
					bestMatching.calculateMetrics();
					changes++;
					continue;
				}

				if (!bestMatching.equals(patternCluster)) {

					patternCluster.removePattern(pattern);
					patternCluster.calculateMetrics();

					bestMatching.addPattern(pattern);
					bestMatching.calculateMetrics();

					changes++;
					continue;
				}
			}
		}

		// Call any after iteration processing.
		afterIteration();
	}
}
