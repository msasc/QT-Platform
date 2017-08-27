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

package com.qtplaf.library.ai.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Root of machine learning methods.
 *
 * @author Miquel Sas
 */
public abstract class LearningMethod {

	/** List of strategies. */
	private List<Strategy> strategies = new ArrayList<>();
	/** The list of listeners interested in this learning process events. */
	private List<LearningListener> listeners = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public LearningMethod() {
		super();
	}

	/**
	 * Add a strategy.
	 * 
	 * @param strategy The strategy.
	 */
	public void addStrategy(Strategy strategy) {
		strategies.add(strategy);
	}

	/**
	 * Returns the list of strategies, aimed for processings that extend the normal initialize, before iteration and
	 * after iteration.
	 * 
	 * @return the strategies
	 */
	public List<Strategy> getStrategies() {
		return strategies;
	}

	/**
	 * Adds a learning listener to the list of listeners.
	 * 
	 * @param listener The listener to add.
	 */
	public void addListener(LearningListener listener) {
		listeners.add(listener);
	}

	/**
	 * Fires a learning event to the list of listeners.
	 * 
	 * @param e The learning event.
	 */
	protected void fireLearningEvent(LearningEvent e) {
		for (LearningListener listener : listeners) {
			listener.learningEvent(e);
		}
	}

	/**
	 * Fires a learning event to the list of listeners.
	 * 
	 * @param message The message.
	 */
	protected void fireLearningEvent(String message) {
		LearningEvent e = new LearningEvent(this, message);
		fireLearningEvent(e);
	}

	/**
	 * Initialize machine learning. By default calls the initialize method of the strategies.
	 */
	public void initialize() {
		for (Strategy strategy : strategies) {
			strategy.initialize();
		}
	}

	/**
	 * Called after an iteration ends. By default calls the after iteration method of the strategies.
	 */
	public void afterIteration() {
		for (Strategy strategy : strategies) {
			strategy.afterIteration();
		}
	}

	/**
	 * Called before an iteration starts. By default calls the before iteration method of the strategies.
	 */
	public void beforeIteration() {
		for (Strategy strategy : strategies) {
			strategy.beforeIteration();
		}
	}

	/**
	 * Called to perform an iteration.
	 */
	public void iteration() {

		// Call strategies before.
		beforeIteration();

		// Perform the iteration.
		performIteration();

		// Call strategies after.
		afterIteration();
	}

	/**
	 * Perform one training iteration.
	 */
	protected abstract void performIteration();
}
