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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;

/**
 * Base task implementation. Extender must implement the abstract methods:
 * <ul>
 * <li><code>countSteps()</code></li>
 * <li><code>execute()</code></li>
 * <li><code>isCancelSupported()</code></li>
 * <li><code>isDeterminate()</code></li>
 * <li><code>isPauseSupported()</code></li>
 * </ul>
 * To execute the task call the <code>run()</code> method, not the <code>execute()</code> that is called whithin
 * <code>run()</code>, tracking several issues like termination and exceptions.
 * <p>
 * Pause/resume and cancel support is recomended be managed through the usage at the begining of the main loop of,
 * <ul>
 * <li><code>if (checkCancel()) break;</code></li>
 * <li><code>if (checkPause()) continue;</code></li>
 * </ul>
 * because updating the internal <code>cancelled</code> and <code>paused</code> is not permited.
 * 
 * @author Miquel Sas
 */
public abstract class TaskRunner implements Task {

	/**
	 * The name of the task.
	 */
	private String name;
	/**
	 * The task description.
	 */
	private String description;
	/**
	 * The list of listeners.
	 */
	private List<TaskListener> listeners = new ArrayList<>();
	/**
	 * A boolean that indicates that the task has started processing and has not terminated.
	 */
	private boolean processing = false;
	/**
	 * A boolean that indicates if the process has been cancelled.
	 */
	private boolean cancelled = false;
	/**
	 * A boolean that indicates that the task is about to cancel.
	 */
	private boolean cancelling = false;
	/**
	 * A boolean that indicates if the process has been paused.
	 */
	private boolean paused = false;
	/**
	 * A boolean that indicates if the process has terminated its job, either correctly or not. It is an internal
	 * control.
	 */
	private boolean terminated = false;
	/**
	 * The exception if not terminated ok.
	 */
	private Exception exception;
	/**
	 * The total number of steps.
	 */
	private long steps = 0;
	/**
	 * The current executing step.
	 */
	private long step = 0;
	/**
	 * Previous executed step, necessary in task pools where tasks notify steps at a discretional pace.
	 */
	private long stepPrev = 0;
	/**
	 * Notify modulus to notify steps at the give pace. A task can have a huge number of steps and there is no need to
	 * notify all them.
	 */
	private int notifyModulus = 1;
	/**
	 * A boolean to indicate if the incoming step should be notified by means of the notify modulus.
	 */
	private boolean notifyStep = true;
	/**
	 * Optional task monitor.
	 */
	private TaskMonitor monitor;
	/**
	 * A boolean indicating if the task should be removed from the monitor when terminated.
	 */
	private boolean removeFromMonitorWhenTerminated = false;
	/**
	 * The parent task if this task if the child of an upper level task.
	 */
	private Task parent;
	/**
	 * List of exceptions ocurred when processing, not thrown and catched and registered.
	 */
	private List<Exception> exceptions = new ArrayList<>();
	/**
	 * List of additional labels to trace messages.
	 */
	private List<String> additionalLabels = new ArrayList<>();

	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor assingning the working session.
	 * 
	 * @param session The working session.
	 */
	public TaskRunner(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Add a listener to notify task events.
	 * 
	 * @param listener The task listener.
	 */
	@Override
	public void addListener(TaskListener listener) {
		listeners.add(listener);
	}

	/**
	 * Returns the exception occurred and thrown, or null.
	 * 
	 * @return The exception occurred and thrown, or null.
	 */
	@Override
	public Exception getException() {
		return exception;
	}

	/**
	 * Add an exception to the list of exceptions not thrown and catched.
	 * 
	 * @param exception The exception ocurred, catched, not thrown that is registered.
	 */
	protected void addException(Exception exception) {
		exceptions.add(exception);
	}

	/**
	 * Returns the list of exceptions ocurred when processing, not thrown and catched and registered.
	 * 
	 * @return The list of exceptions.
	 */
	@Override
	public List<Exception> getExceptions() {
		return exceptions;
	}

	/**
	 * Indicates if the task has been cancelled by an external reason, perhaps a user request.
	 * 
	 * @return A boolean indicating whether the task has been cancelled by an external reason, perhaps a user request.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Returns a boolean indicating whether the task terminated with an exception.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isException() {
		return (exception != null);
	}

	/**
	 * Indicates if the task has been paused, normally by anexternal reason, mainly a user request.
	 * 
	 * @return A boolean that indicates whether the task has been paused, normally by anexternal reason, mainly a user
	 *         request.
	 */
	@Override
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Returns a boolean indicating whether the task has terminated, either an exception or cancellation occured or not
	 * and the job terminated ok.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Returns a boolean indicating that the task has started processing and has not terminated.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isProcessing() {
		return processing;
	}

	/**
	 * Ste if next step sould be notified.
	 * 
	 * @param stepNext The incoming step.
	 */
	private void setNotifyStep(long stepNext) {
		notifyStep = (stepNext - step >= notifyModulus || stepNext >= steps);
	}

	/**
	 * Check if the incoming step should be notified.
	 * 
	 * @return A boolean.
	 */
	protected boolean isNotifyStep() {
		return notifyStep;
	}

	/**
	 * Gets the process name.
	 * 
	 * @return The process name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the process name.
	 * 
	 * @param id The process id.
	 */
	@Override
	public void setName(String id) {
		this.name = id;
	}

	/**
	 * Returns the description.
	 * 
	 * @return The description.
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description The description.
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the total number of steps.
	 * 
	 * @return The total number of steps.
	 */
	@Override
	public long getSteps() {
		return steps;
	}

	/**
	 * Sets the total number of steps.
	 * 
	 * @param steps The total number of steps
	 */
	private void setSteps(long steps) {
		this.steps = steps;
	}

	/**
	 * Returns the current executing step.
	 * 
	 * @return The current executing step.
	 */
	@Override
	public long getStep() {
		return step;
	}

	/**
	 * Sets the current and previous steps.
	 * 
	 * @param step The step.
	 */
	private void setStep(long step) {
		this.stepPrev = this.step;
		this.step = step;
	}

	/**
	 * Returns the increase of steps. Steps are not always notified by one.
	 * 
	 * @return The increase of steps.
	 */
	@Override
	public long getStepIncrease() {
		return (stepPrev <= 0 ? step : step - stepPrev);
	}

	/**
	 * Reset steps counters, useful when several main loops are performed.
	 */
	protected void resetSteps() {
		step = 0;
		stepPrev = 0;
		steps = 0;
	}

	/**
	 * Returns the notify modulus.
	 * 
	 * @return The notifyModulus.
	 */
	public int getNotifyModulus() {
		return notifyModulus;
	}

	/**
	 * Sets the the notify modulus.
	 * 
	 * @param notifyModulus The notify modulus.
	 */
	public void setNotifyModulus(int notifyModulus) {
		if (notifyModulus < 1) {
			throw new IllegalArgumentException("Notify modulus must be GE than 1");
		}
		this.notifyModulus = notifyModulus;
	}

	/**
	 * Returns the monitor.
	 * 
	 * @return The monitor
	 */
	public TaskMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Set the monitor.
	 * 
	 * @param monitor The task monitor.
	 */
	@Override
	public void setMonitor(TaskMonitor monitor) {
		if (isProcessing()) {
			throw new IllegalStateException("The task monitor can not be set while processing.");
		}
		this.monitor = monitor;
	}

	/**
	 * Returns a boolean indicating if the task should be removed from the monitor when terminated.
	 * 
	 * @return A boolean indicating if the task should be removed from the monitor when terminated.
	 */
	@Override
	public boolean isRemoveFromMonitorWhenTerminated() {
		return removeFromMonitorWhenTerminated;
	}

	/**
	 * Set a boolean indicating if the task should be removed from the monitor when terminated.
	 * 
	 * @param removeFromMonitorWhenTerminated A boolean indicating if the task should be removed from the monitor when
	 *        terminated.
	 */
	@Override
	public void setRemoveFromMonitorWhenTerminated(boolean removeFromMonitorWhenTerminated) {
		this.removeFromMonitorWhenTerminated = removeFromMonitorWhenTerminated;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Run the task.
	 */
	@Override
	public void run() {
		// Control members.
		synchronized (this) {

			// Avoid re-entrant execution.
			if (processing) {
				throw new IllegalStateException("Re-entrant execution not possible");
			}

			// Reset in case of re-run.
			cancelled = false;
			cancelling = false;
			paused = false;
			terminated = false;
			exception = null;

			// Processing started.
			processing = true;

			// If there is a monitor available, add the task.
			if (getMonitor() != null) {
				getMonitor().add(this);
			}
		}
		// Notify listeners that processing has started.
		notifyProcessing();
		try {
			execute();
		} catch (Exception exc) {
			// Save the exception.
			exception = exc;
		}
		// Processing/terminated flags.
		synchronized (this) {
			processing = false;
			if (cancelling) {
				cancelled = true;
			}
			terminated = true;
		}
		// If cancelled, notified it.
		if (isCancelled()) {
			notifyCancelled();
		}
		// Notify listeners that processing has terminated.
		notifyTerminated();
		// If there is a monitor and the task should be removed, remove it.
		synchronized (this) {
			if (getMonitor() != null) {
				if (isRemoveFromMonitorWhenTerminated()) {
					getMonitor().remove(this);
				}
			}
		}
	}

	/**
	 * Convenience method to deal with cancel requests when cancel is supported. Usage: at the begining of the main
	 * loop, if check break the loop.
	 * <p>
	 * <code>if (checkCancel()) break;</code>
	 * 
	 * @return A boolean indicating that cancel has been accepted and processing should terminate.
	 */
	protected boolean checkCancel() {
		if (isCancelSupported()) {
			if (cancelRequested()) {
				cancelling = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Convinience method to deal with pause/resume requests when pause/resume is supported. Usage: at the begining of
	 * the main loop, if check continue.
	 * <p>
	 * <code>if (checkPause()) continue;</code>
	 * 
	 * @return A boolean indicating that pause has been accepted and processing should pause.
	 */
	protected boolean checkPause() {
		if (isPauseSupported()) {
			if (!isPaused()) {
				if (pauseRequested()) {
					notifyPaused();
					return true;
				}
				return false;
			}
			if (resumeRequested()) {
				notifyResumed();
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Ask listeners if cancel has been requested.
	 * 
	 * @return A boolean that indicates if cancel has been requested.
	 */
	protected boolean cancelRequested() {
		for (TaskListener listener : listeners) {
			if (listener.cancelRequested(this)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Notify listeners that the task has already been cancelled.
	 */
	protected void notifyCancelled() {
		for (TaskListener listener : listeners) {
			listener.cancelled(this);
		}
	}

	/**
	 * Ask listeners if a pause has been requested.
	 * 
	 * @return A boolean that indicates if a pause has been requested.
	 */
	protected boolean pauseRequested() {
		for (TaskListener listener : listeners) {
			if (listener.pauseRequested(this)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Notify listeners that the task has already been paused.
	 */
	protected void notifyPaused() {
		this.paused = true;
		for (TaskListener listener : listeners) {
			listener.paused(this);
		}
	}

	/**
	 * Ask listeners if resume has been requested.
	 * 
	 * @return A boolean that indicates if resume has been requested.
	 */
	protected boolean resumeRequested() {
		for (TaskListener listener : listeners) {
			if (listener.resumeRequested(this)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Notify listeners that the task has already been resumed.
	 */
	protected void notifyResumed() {
		this.paused = false;
		for (TaskListener listener : listeners) {
			listener.resumed(this);
		}
	}

	/**
	 * Notify listeners that the task has terminated.
	 */
	private void notifyTerminated() {
		for (TaskListener listener : listeners) {
			listener.terminated(this);
		}
	}

	/**
	 * Notify listeners that the task is counting.
	 */
	protected void notifyCounting() {
		for (TaskListener listener : listeners) {
			listener.counting(this);
		}
	}

	/**
	 * Notify listeners that the task is processing.
	 */
	private void notifyProcessing() {
		for (TaskListener listener : listeners) {
			listener.processing(this);
		}
	}

	/**
	 * Invoked to notify that the non indeterminate task is going to executed the argument number of steps.
	 * 
	 * @param steps The number of steps to execute.
	 */
	protected void notifyStepCount(long steps) {
		setSteps(steps);
		for (TaskListener listener : listeners) {
			listener.stepCount(this);
		}
	}

	/**
	 * Invoked to notify that the step is going to start. Note that steps should start at 1 and end at the total number
	 * of steps to properly manage notification modulus and monitor.
	 * 
	 * @param step The next step.
	 * @param text The text explaining the step.
	 */
	protected void notifyStepStart(long step, String text) {
		setNotifyStep(step);
		if (!isNotifyStep()) {
			return;
		}
		setStep(step);
		for (TaskListener listener : listeners) {
			listener.stepStart(this, text);
		}
	}

	/**
	 * Invoked to notify that the current step being executed has ended.
	 */
	protected void notifyStepEnd() {
		if (!isNotifyStep()) {
			return;
		}
		for (TaskListener listener : listeners) {
			listener.stepEnd(this);
		}
	}

	/**
	 * Invoked to notify that the argument label should display the message.
	 * 
	 * @param label The label identifier.
	 * @param message The message.
	 */
	protected void notifyLabel(String label, String message) {
		for (TaskListener listener : listeners) {
			listener.setLabel(this, label, message);
		}
	}

	/**
	 * Returns the hash code.
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Returns a boolean indicating whether the argument object is equal to this task.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Task) {
			Task task = (Task) obj;
			if (getName() != null && task.getName() != null) {
				return getName().equals(task.getName());
			}
			return this == task;
		}
		return false;
	}

	/**
	 * Returns a string representation.
	 * 
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		boolean name = (getName() != null && !getName().isEmpty());
		boolean description = (getDescription() != null && !getDescription().isEmpty());
		if (name)
			b.append(getName());
		if (name && description)
			b.append(" - ");
		if (description)
			b.append(getDescription());
		return b.toString();
	}
	
	/**
	 * Convenient method to get a step of steps message.
	 * 
	 * @param step The current step
	 * @param steps The number of steps.
	 * @return The default step message.
	 */
	public String getStepMessage(long step, long steps) {
		return getStepMessage(step, steps, null, null);
	}

	/**
	 * Convenient method to get a step of steps message.
	 * 
	 * @param step The current step
	 * @param steps The number of steps.
	 * @param prefix The string prefix.
	 * @param suffix The string suffix.
	 * @return The default step message.
	 */
	public String getStepMessage(long step, long steps, String prefix, String suffix) {
		String stepOfSteps = getSession().getString("taskStepOfSteps");
		String message = MessageFormat.format(stepOfSteps, step, steps);
		StringBuilder b = new StringBuilder();
		if (prefix != null && !prefix.isEmpty()) {
			b.append(prefix);
		}
		b.append(message.toLowerCase());
		if (suffix != null && !suffix.isEmpty()) {
			b.append(suffix);
		}
		return b.toString();
	}

	/**
	 * Returns the parent task when this task is a child of an upper level task.
	 * 
	 * @return The parent task.
	 */
	@Override
	public Task getParent() {
		return parent;
	}

	/**
	 * Sets the parent task when this task is a child of an upper level task.
	 * 
	 * @param parent The parent task.
	 */
	@Override
	public void setParent(Task parent) {
		this.parent = parent;
	}

	/**
	 * Returns the list of children tasks when this task is an upper level task made of aa list of tasks.
	 * 
	 * @return The list of children tasks
	 */
	@Override
	public List<Task> getChildren() {
		return new ArrayList<>();
	}

	/**
	 * Returns the list of optional additional labels to trace messages.
	 * 
	 * @return The list of optional additional labels.
	 */
	@Override
	public List<String> getAdditionalLabels() {
		return additionalLabels;
	}

	/**
	 * Add an additional label.
	 * 
	 * @param label The label to add.
	 */
	public void addAdditionalLabel(String label) {
		additionalLabels.add(label);
	}

	/**
	 * Clear the text of additional labels.
	 */
	protected void clearAdditionalLabels() {
		for (String label : additionalLabels) {
			notifyLabel(label, "");
		}
	}
}
