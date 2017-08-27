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

import java.util.List;

/**
 * Interface that must implement tasks potentially time consuming, normally executed in a separated thread, managed by a
 * task manager and monitored by task monitor.
 * 
 * @author Miquel Sas
 */
public interface Task extends Runnable {

	/**
	 * Add a listener to notify task events.
	 * 
	 * @param listener The task listener.
	 */
	void addListener(TaskListener listener);

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	long countSteps() throws Exception;

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	void execute() throws Exception;

	/**
	 * Returns the exception occurred and thrown, or null.
	 * 
	 * @return The exception occurred and thrown, or null.
	 */
	Exception getException();

	/**
	 * Returns the list of exceptions ocurred when processing, not thrown and catched and registered.
	 * 
	 * @return The list of exceptions.
	 */
	List<Exception> getExceptions();

	/**
	 * Returns the total number of steps.
	 * 
	 * @return The total number of steps.
	 */
	long getSteps();

	/**
	 * Returns the current executing step.
	 * 
	 * @return The current executing step.
	 */
	long getStep();

	/**
	 * Returns the increase of steps, because steps are not always notified one by one.
	 * 
	 * @return The increase of steps.
	 */
	long getStepIncrease();

	/**
	 * Returns the task name. It is recomended that the identifier be unique.
	 * 
	 * @return The name.
	 */
	String getName();

	/**
	 * Set the task name.
	 * 
	 * @param name The name.
	 */
	void setName(String name);

	/**
	 * Returns the description of the task.
	 * 
	 * @return The description..
	 */
	String getDescription();

	/**
	 * Set the task description.
	 * 
	 * @param description The description.
	 */
	void setDescription(String description);

	/**
	 * Install a task monitor, allowing the task to start monitoring when starting execution and removing it from the
	 * monitor when terminated if the task so decides.
	 * 
	 * @param monitor The task monitor to install.
	 */
	void setMonitor(TaskMonitor monitor);

	/**
	 * Returns a boolean indicating if the task should be removed from the monitor when terminated. Normally top tasks
	 * will not be removed, while child task will do.
	 * 
	 * @return A boolean indicating if the task should be removed from the monitor when terminated.
	 */
	boolean isRemoveFromMonitorWhenTerminated();

	/**
	 * Set if the task should be removed from the monitor if it has been set.
	 * 
	 * @param remove A boolean.
	 */
	void setRemoveFromMonitorWhenTerminated(boolean remove);

	/**
	 * Indicates if the task has been cancelled by an external reason, perhaps a user request.
	 * 
	 * @return A boolean indicating whether the task has been cancelled by an external reason, perhaps a user request.
	 */
	boolean isCancelled();

	/**
	 * Returns a boolean indicating whether the task will support cancel requests.
	 * 
	 * @return A boolean.
	 */
	boolean isCancelSupported();

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * 
	 * @return A boolean.
	 */
	boolean isCountStepsSupported();

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	boolean isIndeterminate();

	/**
	 * Returns a boolean indicating whether the task terminated with an exception.
	 * 
	 * @return A boolean.
	 */
	boolean isException();

	/**
	 * Indicates if the task has been paused, normally by anexternal reason, mainly a user request.
	 * 
	 * @return A boolean that indicates whether the task has been paused, normally by anexternal reason, mainly a user
	 *         request.
	 */
	boolean isPaused();

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests.
	 * 
	 * @return A boolean.
	 */
	boolean isPauseSupported();

	/**
	 * Returns a boolean indicating that the task has started processing and has not terminated.
	 * 
	 * @return A boolean.
	 */
	boolean isProcessing();

	/**
	 * Returns a boolean indicating whether the task has terminated, either an exception or cancellation occured or not
	 * and the job terminated ok.
	 * 
	 * @return A boolean.
	 */
	boolean isTerminated();

	/**
	 * Returns the parent task when this task is a child of an upper level task.
	 * 
	 * @return The parent task.
	 */
	Task getParent();

	/**
	 * Sets the parent task when this task is a child of an upper level task.
	 * 
	 * @param parent The parent task.
	 */
	void setParent(Task parent);

	/**
	 * Returns the list of children tasks when this task is an upper level task made of aa list of tasks.
	 * 
	 * @return The list of children tasks
	 */
	List<Task> getChildren();

	/**
	 * Returns the list of optional additional labels to trace messages.
	 * 
	 * @return The list of optional additional labels.
	 */
	List<String> getAdditionalLabels();
}
