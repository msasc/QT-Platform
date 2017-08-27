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

package com.qtplaf.library.ai.learning.clustering.centroid.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.Centroid;
import com.qtplaf.library.ai.function.Distance;
import com.qtplaf.library.ai.learning.clustering.Cluster;
import com.qtplaf.library.ai.learning.clustering.Clustering;
import com.qtplaf.library.ai.learning.clustering.centroid.CentroidCluster;
import com.qtplaf.library.util.NumberUtils;

/**
 * Unsupervised KMeans clustering.
 *
 * @author Miquel Sas
 */
public class KMeansClustering extends Clustering {
	
	/** Temporary cluster index. */
	private int clusterIndex;
	/** Temporary pattern index. */
	private int patternIndex;
	

	/**
	 * Constructor.
	 * 
	 * @param clusters The numbers of desired clusters.
	 * @param centroidFunction The centroid function.
	 * @param distanceFunction The distance function.
	 */
	public KMeansClustering(int clusters, Centroid centroidFunction, Distance distanceFunction) {
		super();
		for (int i = 0; i < clusters; i++) {
			addCluster(new CentroidCluster(centroidFunction, distanceFunction));
		}
	}

	/**
	 * Initialize the clustering process.
	 * 
	 * @param source The list of patterns.
	 */
	@Override
	public void initializeClustering(PatternSource source) {

		// Fill a temporary list of patterns.
		List<Pattern> patterns = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			fireLearningEvent("Retrieving pattern "+(i+1));
			patterns.add(source.get(i));
		}

		// Randomly distribute.
		Random random = new Random();
		int p = 0;
		while (!patterns.isEmpty()) {
			fireLearningEvent("Randomly cluster "+(++p));
			for (int i = 0; i < getClusterCount(); i++) {
				if (patterns.isEmpty()) {
					break;
				}
				int index = random.nextInt(patterns.size());
				getCluster(i).addPattern(patterns.remove(index));
			}
		}
		for (int i = 0; i < getClusterCount(); i++) {
			fireLearningEvent("Calculate cluster metrics "+(i+1));
			getCluster(i).calculateMetrics();
		}
	}

	/**
	 * Returns the best mathing cluster for the pattern applying algorithm criteria..
	 * 
	 * @param pattern The pattern.
	 * @return The best matching cluster.
	 */
	@Override
	public Cluster getBestMatchingCluster(Pattern pattern) {
		double distance = NumberUtils.MAX_DOUBLE;
		Cluster bestCluster = null;
		for (int i = 0; i < getClusterCount(); i++) {
			double clusterDistance = getCluster(i).getDistance(pattern);
			if (clusterDistance < distance) {
				distance = clusterDistance;
				bestCluster = getCluster(i);
			}
		}
		return bestCluster;
	}

	/**
	 * Initialize the iteration.
	 */
	@Override
	protected void initializeIteration() {
		clusterIndex = 0;
		patternIndex = 0;
	}

	/**
	 * Returns a boolean inticating whether there are more patterns pending to process in the current iteration.
	 * 
	 * @return A boolean.
	 */
	@Override
	protected boolean hasMoreIterationPatterns() {
		if (clusterIndex == getClusterCount()) {
			return false;
		}
		if (patternIndex == getCluster(clusterIndex).getPatternCount()) {
			patternIndex = 0;
			clusterIndex++;
			if (clusterIndex == getClusterCount()) {
				return false;
			}
			if (getCluster(clusterIndex).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the next iteration pattern.
	 * 
	 * @return The next iteration pattern.
	 */
	@Override
	protected Pattern nextIterationPattern() {
		Pattern pattern = getCluster(clusterIndex).getPattern(patternIndex);
		patternIndex++;
		if (patternIndex == getCluster(clusterIndex).getPatternCount()) {
			patternIndex = 0;
			clusterIndex++;
		}
		return pattern;
	}

}
