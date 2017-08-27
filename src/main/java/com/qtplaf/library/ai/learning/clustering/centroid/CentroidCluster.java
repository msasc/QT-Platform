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

package com.qtplaf.library.ai.learning.clustering.centroid;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.function.Centroid;
import com.qtplaf.library.ai.function.Distance;
import com.qtplaf.library.ai.learning.clustering.Cluster;
import com.qtplaf.library.util.NumberUtils;

/**
 * A cluster with centroid and distance metrics.
 *
 * @author Miquel Sas
 */
public class CentroidCluster extends Cluster {

	/** Centroid function. */
	private Centroid centroidFunction;
	/** Distance function. */
	private Distance distanceFunction;

	/** Cluster centroid. */
	private double[] centroid;

	/**
	 * Constructor.
	 * 
	 * @param centroidFunction The centroid function.
	 * @param distanceFunction The distance function.
	 */
	public CentroidCluster(Centroid centroidFunction, Distance distanceFunction) {
		super();
		this.centroidFunction = centroidFunction;
		this.distanceFunction = distanceFunction;
	}

	/**
	 * Calculate the centroid.
	 */
	@Override
	public void calculateMetrics() {
		if (isEmpty()) {
			return;
		}
		centroid = centroidFunction.calculate(getPatterns());
	}

	/**
	 * Return a mesure of the distance of a pattern versus the cluster.
	 * 
	 * @param pattern The pattern.
	 * @return The mesure of the distance.
	 */
	@Override
	public double getDistance(Pattern pattern) {
		if (centroid == null) {
			return NumberUtils.MAX_DOUBLE;
		}
		double distance = distanceFunction.calculate(centroid, pattern.getInputs());
		return distance;
	}

	/**
	 * Return a mesure of the cohesion of the patterns in the cluster. KMeans does not use cohesion.
	 * 
	 * @return A mesure of the cohesion.
	 */
	@Override
	public double getCohesion() {
		return NumberUtils.MIN_DOUBLE;
	}

	/**
	 * Returns a mesure of the cohesion including the given pattern. KMeans does not use cohesion.
	 * 
	 * @param pattern The pattern.
	 * @return A mesure of the cohesion.
	 */
	@Override
	public double getCohesion(Pattern pattern) {
		return NumberUtils.MIN_DOUBLE;
	}

	/**
	 * Return a mesure of the dispersion of the patterns in the cluster. KMeans does not use dispersion.
	 * 
	 * @return A mesure of the dispersion.
	 */
	@Override
	public double getDispersion() {
		return NumberUtils.MAX_DOUBLE;
	}

	/**
	 * Returns a mesure of the dispersion including the given pattern. KMeans does not use dispersion.
	 * 
	 * @param pattern The pattern.
	 * @return A mesure of the dispersion.
	 */
	@Override
	public double getDispersion(Pattern pattern) {
		return NumberUtils.MAX_DOUBLE;
	}

}
