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

package com.qtplaf.platform.statistics.task;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.ai.function.Centroid;
import com.qtplaf.library.ai.function.Distance;
import com.qtplaf.library.ai.function.centroid.BasicCentroid;
import com.qtplaf.library.ai.function.distance.EuclideanDistance;
import com.qtplaf.library.ai.learning.LearningEvent;
import com.qtplaf.library.ai.learning.LearningListener;
import com.qtplaf.library.ai.learning.clustering.Cluster;
import com.qtplaf.library.ai.learning.clustering.centroid.kmeans.KMeansClustering;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueMap;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.States;
import com.qtplaf.platform.statistics.patterns.Patterns;
import com.qtplaf.platform.statistics.patterns.TablePatternSource;

/**
 * Perform KMeans clustering on a list of patterns not labeled, and then register the suggested cluster to analyze the
 * results.
 *
 * @author Miquel Sas
 */
public class TaskClusterKMeans extends TaskAverages {

	/** Label to notify clustering messages. */
	private static final String K_LABEL = "kl";
	/** Label to notify summary report. */
	private static final String S_LABEL = "sl";

	/**
	 * Clustering listener.
	 */
	class Listener implements LearningListener {
		/**
		 * Called after a certain clustering action has been performed.
		 * 
		 * @param e The event.
		 */
		@Override
		public void learningEvent(LearningEvent e) {
			String message = e.getMessage();
			notifyLabel(K_LABEL, message);
		}

	}

	/** Underlying states statistics. */
	private States states;
	/** Underlying table pattern source. */
	private TablePatternSource patternSource;

	/**
	 * Constructor.
	 * 
	 * @param states Underlying states statistics.
	 * @param patternSource Underlying table pattern source.
	 */
	public TaskClusterKMeans(States states, TablePatternSource patternSource) {
		super(states.getSession());
		this.states = states;
		this.patternSource = patternSource;

		PatternInfo info = patternSource.getPatternInfo();
		setNameAndDescription(this.states, "KMeans-" + info.getId(), info.getDescription());

		addAdditionalLabel(K_LABEL);
		addAdditionalLabel(S_LABEL);
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task supports counting steps.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return false;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This task is not indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return true;
	}

	/**
	 * Count the steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {

		// Notify counting.
		notifyCounting();

		// Number of steps.
		long count = patternSource.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Reset cluster numbers.
		notifyLabel(S_LABEL, "Reset cluster numbers...");
		resetClusterNumbers();

		// Start initializing the source randomly
		int cacheSize = 50000;
		notifyLabel(S_LABEL, "Initializing source");
		patternSource.setCacheSize(cacheSize);
		patternSource.fillRandomly(cacheSize);
		notifyLabel(S_LABEL, "Clustering...");

		int outputCount = patternSource.getPatternInfo().getOutputCount();
		Distance distance = new EuclideanDistance();
		Centroid centroid = new BasicCentroid();
		KMeansClustering km = new KMeansClustering(outputCount, centroid, distance);
		km.addListener(new Listener());
		km.initializeClustering(patternSource);

		int iteration = 0;
		while (true) {

			// Check request of cancel.
			if (checkCancel()) {
				break;
			}

			// Check pause resume.
			if (checkPause()) {
				continue;
			}

			km.iteration();
			int changes = km.getChanges();
			iteration++;

			// After iteration message
			StringBuilder b = new StringBuilder();
			b.append("Iteration " + iteration + ": ");
			b.append(changes);
			b.append(" changes.");
			notifyLabel(S_LABEL, b.toString());

			if (changes == 0) {
				saveClusterNumbers(km);
				break;
			}
		}
	}

	/**
	 * Reset cluster numbers.
	 * 
	 * @throws Exception If a db error occurs.
	 */
	private void resetClusterNumbers() throws Exception {
		Persistor persistor = patternSource.getPersistor();
		ValueMap map = new ValueMap();
		map.put(Fields.CLUSTER, new Value(0));
		persistor.update(new Criteria(), map);
	}

	/**
	 * Save cluster numbers to later analyze the results.
	 * 
	 * @param km The clustering algorithm.
	 * @throws Exception If an error occurs.
	 */
	private void saveClusterNumbers(KMeansClustering km) throws Exception {
		notifyLabel(K_LABEL, "Saving cluster numbers... ");
		Persistor persistor = patternSource.getPersistor();
		for (int i = 0; i < km.getClusterCount(); i++) {
			Cluster cluster = km.getCluster(i);
			for (int j = 0; j < cluster.getPatternCount(); j++) {
				notifyLabel(S_LABEL, "Cluster " + (i + 1) + " pattern " + (j + 1));
				Pattern pattern = cluster.getPattern(j);
				Record record = Patterns.getPatternRecord(pattern);
				record.getValue(Fields.CLUSTER).setInteger(i + 1);
				persistor.update(record);
			}
		}
		clearAdditionalLabels();
	}
}
