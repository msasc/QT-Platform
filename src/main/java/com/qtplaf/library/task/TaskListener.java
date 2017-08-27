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

package com.qtplaf.library.task;

/**
 * Interface that should implement listeners interested in the interaction with a task.
 * 
 * @author Miquel Sas
 */
public interface TaskListener {

	/**
	 * Invoked to notify that the task is counting the number of steps.
	 * 
	 * @param task The task.
	 */
	void counting(Task task);

	/**
	 * Invoked to notify that the task has started processing.
	 * 
	 * @param task The task.
	 */
	void processing(Task task);

	/**
	 * Invoked to notify that the task has effectively been cancelled (after a cancel request).
	 * 
	 * @param task The task.
	 */
	void cancelled(Task task);

	/**
	 * Invoked to know if task cancellation has been requested.
	 * 
	 * @param task The task.
	 * @return A boolean that indicates if task cancellation has been requested.
	 */
	boolean cancelRequested(Task task);

	/**
	 * Invoked to notify that the task has effectively been paused (after a pause request).
	 * 
	 * @param task The task.
	 */
	void paused(Task task);

	/**
	 * Invoked to know if the task has been requested to pause. In non indeterminate tasks, it would normally be invoked
	 * at every step.
	 * 
	 * @param task The task.
	 * @return A boolean indicating if the task has been requested to pause.
	 */
	boolean pauseRequested(Task task);

	/**
	 * Invoked to notify that the task has effectively been resumed (after a resume request).
	 * 
	 * @param task The task.
	 */
	void resumed(Task task);

	/**
	 * Invoked to know that after pausing, if resume has beeen requested.
	 * 
	 * @param task The task.
	 * @return A boolean that indicates if resumen has been requested.
	 */
	boolean resumeRequested(Task task);

	/**
	 * Invoked to notify that the task has terminated.
	 * 
	 * @param task The task.
	 */
	void terminated(Task task);

	/**
	 * Invoked to notify that the non indeterminate task is going to executed the number of steps.
	 * 
	 * @param task The task.
	 */
	void stepCount(Task task);

	/**
	 * Invoked to notify that the step is going to start. Note that steps should start at 1 and end at the total number
	 * of steps to properly manage notification modulus and monitor.
	 * 
	 * @param task The task.
	 * @param text The text explaining the step.
	 */
	void stepStart(Task task, String text);

	/**
	 * Invoked to notify that the step has ended.
	 * 
	 * @param task The task.
	 */
	void stepEnd(Task task);

	/**
	 * Invoked to trace a message to the argument label.
	 * 
	 * @param task The task.
	 * @param label The label identifier.
	 * @param message The message.
	 */
	void setLabel(Task task, String label, String message);
}