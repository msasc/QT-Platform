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

/**
 * A cluster of patterns.
 *
 * @author Miquel Sas
 */
public abstract class Cluster {

	/** Master optional label of the cluster. */
	private String label;
	/** The list of patterns in the cluster. */
	private List<Pattern> patterns = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Cluster() {
		super();
	}

	/**
	 * Return the label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label The label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Check if the cluster has a label.
	 * 
	 * @return A boolean.
	 */
	public boolean isLabel() {
		return getLabel() != null;
	}

	/**
	 * Add a pattern to the cluster.
	 * 
	 * @param pattern The pattern.
	 */
	public void addPattern(Pattern pattern) {
		patterns.add(pattern);
	}

	/**
	 * Return the pattern.
	 * 
	 * @param index The index of the pattern.
	 * @return The pattern.
	 */
	public Pattern getPattern(int index) {
		return patterns.get(index);
	}

	/**
	 * Remove the given pattern.
	 * 
	 * @param pattern The pattern to remove.
	 * @return A boolean indicating whether the pattern was removed.
	 */
	public boolean removePattern(Pattern pattern) {
		return patterns.remove(pattern);
	}

	/**
	 * Remove the pattern.
	 * 
	 * @param index The index of the pattern.
	 * @return The removed pattern.
	 */
	public Pattern removePattern(int index) {
		return patterns.remove(index);
	}

	/**
	 * Return the number of patterns.
	 * 
	 * @return The number of patterns.
	 */
	public int getPatternCount() {
		return patterns.size();
	}

	/**
	 * Check whether the cluster contains the pattern.
	 * 
	 * @param pattern The pattern.
	 * @return A boolean.
	 */
	public boolean containsPattern(Pattern pattern) {
		return patterns.contains(pattern);
	}

	/**
	 * Check if the cluster if empty, ie has no patterns.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return patterns.isEmpty();
	}

	/**
	 * Convenience method to access the patterns from extenders.
	 * 
	 * @return The list of patterns.
	 */
	protected List<Pattern> getPatterns() {
		return patterns;
	}

	/**
	 * Calculate any useful internal metrics (centroid, cohesion, dispersion, etc).
	 */
	public abstract void calculateMetrics();

	/**
	 * Return a measure of the distance of a pattern versus the cluster.
	 * 
	 * @param pattern The pattern.
	 * @return The measure of the distance.
	 */
	public abstract double getDistance(Pattern pattern);

	/**
	 * Return a measure of the cohesion of the patterns in the cluster.
	 * 
	 * @return A measure of the cohesion.
	 */
	public abstract double getCohesion();

	/**
	 * Returns a measure of the cohesion including the given pattern.
	 * 
	 * @param pattern The pattern.
	 * @return A measure of the cohesion.
	 */
	public abstract double getCohesion(Pattern pattern);

	/**
	 * Return a measure of the dispersion of the patterns in the cluster.
	 * 
	 * @return A measure of the dispersion.
	 */
	public abstract double getDispersion();

	/**
	 * Returns a measure of the dispersion including the given pattern.
	 * 
	 * @param pattern The pattern.
	 * @return A measure of the dispersion.
	 */
	public abstract double getDispersion(Pattern pattern);

}
