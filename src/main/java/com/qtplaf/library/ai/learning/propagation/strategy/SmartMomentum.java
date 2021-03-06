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
 * Smart lmomentum strategy for back propagation.
 *
 * @author Miquel Sas
 */
public class SmartMomentum implements Strategy {

	/** The minimum improvement to adjust momentum. */
	private static final double MIN_IMPROVEMENT = 0.0001;
	/** The starting momentum. */
	private static final double START_MOMENTUM = 0.1;
	/** How much to increase momentum by. */
	private static final double MOMENTUM_INCREASE = 0.01;
	/** How many cycles to accept before adjusting momentum. */
	private static final double MOMENTUM_CYCLES = 10;

	/** The last improvement in error rate. */
	private double lastImprovement;
	/** The error rate from the previous iteration. */
	private double lastError;
	/** Has one iteration passed, and we are now ready to start evaluation. */
	private boolean ready;
	/** The last momentum. */
	private int lastMomentum;
	/** The current momentum. */
	private double currentMomentum;
	
	/** Back propagation learning method. */
	private BackPropagation backPropagation;
	
	/**
	 * Constructor.
	 * 
	 * @param backPropagation The back propagation learning method.
	 */
	public SmartMomentum(BackPropagation backPropagation) {
		super();
		this.backPropagation = backPropagation;
	}

	/**
	 * Called before the learning method starts the first6 iteration.
	 */
	@Override
	public void initialize() {
		ready = false;
		backPropagation.setMomentum(0);
		currentMomentum = 0;
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
			double currentError = backPropagation.getLastError();
			lastImprovement = (currentError - lastError) / lastError;
			if ((lastImprovement > 0) || (Math.abs(lastImprovement) < MIN_IMPROVEMENT)) {
				lastMomentum++;
				if (lastMomentum > MOMENTUM_CYCLES) {
					lastMomentum = 0;
					if (((int) currentMomentum) == 0) {
						currentMomentum = START_MOMENTUM;
					}
					currentMomentum *= (1.0 + MOMENTUM_INCREASE);
					backPropagation.setMomentum(currentMomentum);
				}
			} else {
				currentMomentum = 0;
				backPropagation.setMomentum(0);
			}
		}
		ready = true;
	}
}
