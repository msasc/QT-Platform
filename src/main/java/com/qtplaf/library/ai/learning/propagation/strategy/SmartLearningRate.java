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

package com.qtplaf.library.ai.learning.propagation.strategy;

import com.qtplaf.library.ai.learning.Strategy;
import com.qtplaf.library.ai.learning.propagation.BackPropagation;

/**
 * Smart learning rate strategy for back propagation.
 *
 * @author Miquel Sas
 */
public class SmartLearningRate implements Strategy {

	/** Learning decay rate. */
	private static final double LEARNING_DECAY = 0.99;
	
	/** Back propagation learning method. */
	private BackPropagation backPropagation;
	/** The current learning rate. */
	private double currentLearningRate;
	/** The error rate from the previous iteration. */
	private double lastError;
	/** Has one iteration passed, and we are now ready to start evaluation. */
	private boolean ready;

	/**
	 * Constructor.
	 * 
	 * @param backPropagation The back propagation learning method.
	 */
	public SmartLearningRate(BackPropagation backPropagation) {
		super();
		this.backPropagation = backPropagation;
	}

	/**
	 * Called before the learning method starts the first6 iteration.
	 */
	@Override
	public void initialize() {
		ready = false;
		int size = backPropagation.getLearningData().size();
		currentLearningRate = 1.0 / size;
		backPropagation.setLearningRate(currentLearningRate);
	}
	
	/**
	 * Called before the iteration.
	 */
	@Override
	public void beforeIteration() {
		lastError = backPropagation.getLastError();
	}

	/**
	 * Called after the iteration.
	 */
	@Override
	public void afterIteration() {
		if (ready) {
			if (backPropagation.getLastError() > lastError) {
				currentLearningRate *= SmartLearningRate.LEARNING_DECAY;
				backPropagation.setLearningRate(currentLearningRate);
			}
		}
		ready = true;
	}
}
