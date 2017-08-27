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

package com.qtplaf.library.task.sample;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.TaskRunner;

/**
 * Sample task useful to test the different task implementations and to serve as an example of how a task should be
 * written.
 * 
 * @author Miquel Sas
 */
public class SampleTask extends TaskRunner {

	/** Count steps supported. */
	private boolean countStepsSupported;
	/** Indeterminate, unknown number of steps. */
	private boolean indeterminate;
	/** Cancel supported. */
	private boolean cancelSupported;
	/** Pause/resume supported. */
	private boolean pauseSupported;

	/** Steps base. Steps are calculated randomizing this base, zero is not accepted. */
	private int stepsBase = 100000;

	/** Maximum sleep when counting, used value will be randomized */
	private int countSleep = 100;
	/** Maximum step sleep in millis, used value will be randomized. */
	private int stepSleepMillis = 50;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public SampleTask(Session session) {
		super(session);
	}

	/**
	 * Set the extent of the task in milliseconds, without including the count sleep that, by default is 100 millis.
	 * 
	 * @param extent The extent in millis.
	 */
	public void setExtent(int extent) {
		
		// Do not admit less than one second.
		if (extent < 1000) {
			throw new IllegalArgumentException("The time must be greater than one second.");
		}

		// Set a step sleep of 10 millis (double it if averaged)
		stepSleepMillis = 10;
		
		// Number of steps.
		stepsBase = extent / 10;
		
		// Notify modulus to notify 50 times.
		setNotifyModulus(extent / 1000);
	}

	/**
	 * @param stepsBase the stepsBase to set
	 */
	public synchronized void setStepsBase(int stepsBase) {
		if (stepsBase <= 0) {
			throw new IllegalArgumentException("Base steps must be greater than zero.");
		}
		this.stepsBase = stepsBase;
	}

	/**
	 * @param countSleep the countSleep to set
	 */
	public synchronized void setCountSleep(int countSleep) {
		this.countSleep = countSleep;
	}

	/**
	 * Set the step sleep in millis.
	 * 
	 * @param stepSleepMillis The stepSleep in millis.
	 */
	public synchronized void setStepSleep(int stepSleepMillis) {
		this.stepSleepMillis = stepSleepMillis;
	}

	/**
	 * Setup as an indeterminate task: does not notify either the number of steps or the steps themselves. Used default
	 * values.
	 * 
	 * @param cancelSupported Cancel supported.
	 * @param pauseSupported Pause/resume supported.
	 */
	public void setupIndeterminate(boolean cancelSupported, boolean pauseSupported) {
		this.indeterminate = true;
		this.countStepsSupported = false;
		this.cancelSupported = cancelSupported;
		this.pauseSupported = pauseSupported;
	}

	/**
	 * Setup as a determinate task that does not support countSteps: notifies either the number of steps and the steps
	 * themselves. Uses default values.
	 * 
	 * @param cancelSupported Cancel supported.
	 * @param pauseSupported Pause/resume supported.
	 */
	public void setupDeterminateCountStepsNotSupported(boolean cancelSupported, boolean pauseSupported) {
		this.indeterminate = false;
		this.countStepsSupported = false;
		this.cancelSupported = cancelSupported;
		this.pauseSupported = pauseSupported;
	}

	/**
	 * Setup as a determinate task that does support countSteps: notifies either the number of steps and the steps
	 * themselves. Uses default values.
	 * 
	 * @param cancelSupported Cancel supported.
	 * @param pauseSupported Pause/resume supported.
	 */
	public void setupDeterminateCountStepsSupported(boolean cancelSupported, boolean pauseSupported) {
		this.indeterminate = false;
		this.countStepsSupported = true;
		this.cancelSupported = cancelSupported;
		this.pauseSupported = pauseSupported;
	}

	/**
	 * @param countStepsSupported the countStepsSupported to set
	 */
	public void setCountStepsSupported(boolean countStepsSupported) {
		this.countStepsSupported = countStepsSupported;
	}

	/**
	 * @param indeterminate the indeterminate to set
	 */
	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
	}

	/**
	 * @param cancelSupported the cancelSupported to set
	 */
	public void setCancelSupported(boolean cancelSupported) {
		this.cancelSupported = cancelSupported;
	}

	/**
	 * @param pauseSupported the pauseSupported to set
	 */
	public void setPauseSupported(boolean pauseSupported) {
		this.pauseSupported = pauseSupported;
	}

	/**
	 * Useful sleep without having to deal with the exception.
	 * 
	 * @param millis Milliseconds
	 */
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
		}
	}

	/**
	 * Sleep prior to count.
	 */
	private void sleepCount() {
		if (countSleep >= 1) {
			sleep(countSleep);
		}
	}

	/**
	 * Sleep a step. Nanos are not randomized.
	 */
	private void sleepStep() {
		if (stepSleepMillis >= 1) {
			sleep(stepSleepMillis);
		}
	}

	/**
	 * Calculates the number of steps by multiplying the multpliyer by random.netxInt(100)
	 * 
	 * @return The number of steps to use.
	 */
	private long calculateSteps() {
		sleepCount();
		return stepsBase;
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * 
	 * @return The number of steps.
	 */
	@Override
	public long countSteps() {
		if (!countStepsSupported) {
			throw new UnsupportedOperationException("Count steps is not supported.");
		}
		notifyStepCount(calculateSteps());
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Counting: this is somethig all tasks should do.
		// - Indeterminate -> no count.
		// - countSteps not supported -> do count.
		// - countSteps supported -> count if not already done.
		if (!isIndeterminate()) {
			if (!isCountStepsSupported() || getSteps() == 0) {
				notifyStepCount(calculateSteps());
			}
		}

		// Main iteration loop.
		long step = 0;
		long steps = getSteps();
		if (isIndeterminate()) {
			steps = calculateSteps();
		}
		while (step < steps) {

			// Check request of cancel.
			if (checkCancel()) {
				break;
			}

			// Check pause resume.
			if (checkPause()) {
				continue;
			}

			// Increase step.
			step++;

			// Notify step start.
			if (!isIndeterminate()) {
				notifyStepStart(step, getStepMessage(step, steps, null, null));
			}

			// Step sleep.
			sleepStep();

			// Notify step end.
			if (!isIndeterminate()) {
				notifyStepEnd();
			}
		}
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return cancelSupported;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return countStepsSupported;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return indeterminate;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return pauseSupported;
	}

}
