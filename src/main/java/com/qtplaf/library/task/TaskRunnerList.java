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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.qtplaf.library.app.Session;

/**
 * A task that executes a list of tasks sequentially. Tasks are executed in a separated thread.
 * 
 * @author Miquel Sas
 */
public class TaskRunnerList extends TaskRunner {

	/**
	 * The task listener that interacts with the task being executed.
	 */
	class TaskAdapter extends TaskHandler {

		/**
		 * Invoked to notify that the task is counting the number of steps.
		 * 
		 * @param task The task.
		 */
		@Override
		public void counting(Task task) {
		}

		/**
		 * Invoked to notify that the task has started processing.
		 * 
		 * @param task The task.
		 */
		@Override
		public void processing(Task task) {
		}

		/**
		 * Invoked to notify that the task has effectively been cancelled (after a cancel request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void cancelled(Task task) {
			synchronized (this) {
				cancelRequesting = true;
				notifyCancelled();
			}
		}

		/**
		 * Invoked to know if task cancellation has been requested.
		 * 
		 * @param task The task.
		 * @return A boolean that indicates if task cancellation has been requested.
		 */
		@Override
		public boolean cancelRequested(Task task) {
			return cancelRequesting;
		}

		/**
		 * Invoked to notify that the task has effectively been paused (after a pause request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void paused(Task task) {
			synchronized (this) {
				if (executingTasks.getLast().isPaused()) {
					resumeRequesting = false;
					pauseRequesting = true;
					notifyPaused();
				}
			}
		}

		/**
		 * Invoked to know if the task has been requested to pause. In non indeterminate tasks, it would normally be
		 * invoked at every currentStep.
		 * 
		 * @param task The task.
		 * @return A boolean indicating if the task has been requested to pause.
		 */
		@Override
		public boolean pauseRequested(Task task) {
			return pauseRequesting;
		}

		/**
		 * Invoked to notify that the task has effectively been resumed (after a resume request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void resumed(Task task) {
			synchronized (this) {
				if (!executingTasks.getLast().isPaused()) {
					pauseRequesting = false;
					notifyResumed();
				}
			}
		}

		/**
		 * Invoked to know that after pausing, if resume has beeen requested.
		 * 
		 * @param task The task.
		 * @return A boolean that indicates if resumen has been requested.
		 */
		@Override
		public boolean resumeRequested(Task task) {
			return resumeRequesting;
		}

		/**
		 * Invoked to notify that the task has terminated.
		 * 
		 * @param task The task.
		 */
		@Override
		public void terminated(Task task) {
			synchronized (this) {
				notifyStepEnd();
			}
		}

		/**
		 * Invoked to notify that the non indeterminate task is going to executed the argument number of steps.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepCount(Task task) {
		}

		/**
		 * Invoked to notify that the currentStep is going to start. Note that steps should start at 1 and end at the
		 * total number of steps to properly manage notification modulus and monitor.
		 * 
		 * @param task The task.
		 * @param text The text explaining the currentStep.
		 */
		@Override
		public void stepStart(Task task, String text) {
		}

		/**
		 * Invoked to notify that the currentStep has ended.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepEnd(Task task) {
		}
	}

	/** The list of tasks to be executed. */
	private List<Task> tasks = new ArrayList<>();
	/** Unique task listener to comunicate with the executing task. */
	private final TaskAdapter listener = new TaskAdapter();

	/** Operation boolean indicating that cancel is being requested to executing tasks. */
	private boolean cancelRequesting = false;
	/** Operation boolean indicating that pause is being requested to executing tasks. */
	private boolean pauseRequesting = false;
	/** Operation boolean indicating that resume is being requested to executing tasks. */
	private boolean resumeRequesting = false;

	/** List of executing tasks. */
	private Deque<Task> executingTasks = new ArrayDeque<>();

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public TaskRunnerList(Session session) {
		super(session);
	}

	/**
	 * Add a task to the list of tasks to be executed. The tasks must be added before any execution is started.
	 * 
	 * @param task The task to be added.
	 */
	public void addTask(Task task) {
		if (isProcessing()) {
			throw new IllegalStateException("Can not add tasks when processing.");
		}
		task.addListener(listener);
		task.setParent(this);
		tasks.add(task);
	}

	/**
	 * Counts the number of steps.
	 * 
	 * @return The number of steps.
	 */
	@Override
	public long countSteps() {
		notifyStepCount(tasks.size());
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Reset control member in case of re-execute.
		cancelRequesting = false;
		pauseRequesting = false;
		resumeRequesting = false;
		executingTasks.clear();

		// Setup the monitor is applicable. Tasks in the list will remove themselves from the monitor.
		if (getMonitor() != null) {
			for (Task task : tasks) {
				task.setMonitor(getMonitor());
				task.setRemoveFromMonitorWhenTerminated(true);
			}
		}

		// List of tasks to execute and tasks executed.
		Deque<Task> pendingTasks = new ArrayDeque<>(tasks);

		// Notify the number of steps to be executed.
		notifyStepCount(tasks.size());

		// Current step.
		long step = 0;

		// Execute tasks while not all executed.
		while (true) {
			
			if (pendingTasks.isEmpty()) {
				if (allExecutingTerminated()) {
					break;
				}
			}

			// Cancel support. Exit when last executing has terminaded (either cancelled or not).
			if (checkCancel()) {
				if (executingTasks.getLast().isTerminated()) {
					break;
				}
				continue;
			}

			// Pause/resume support.
			if (checkPause()) {
				continue;
			}

			// Check if a new task should be lauched.
			synchronized (this) {
				if (executingTasks.isEmpty() || executingTasks.getLast().isTerminated()) {

					// Notify start
					step++;
					String prefix = getSession().getString("taskExecutingTask") + ": ";
					notifyStepStart(step, getStepMessage(step, getSteps(), prefix, null));

					// Do start
					Task task = pendingTasks.removeFirst();
					task.addListener(listener);
					executingTasks.addLast(task);
					new Thread(task, task.toString()).start();
				}
			}
		}

	}

	/**
	 * Check if all executing tasks are teminated.
	 * 
	 * @return A boolean.
	 */
	private boolean allExecutingTerminated() {
		if (executingTasks.isEmpty()) {
			return false;
		}
		for (Task task : executingTasks) {
			if (!task.isTerminated()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convenience method to deal with cancel requests when cancel is supported. Usage: at the begining of the main
	 * loop, if check break the loop.
	 * <p>
	 * <code>if (checkCancel()) break;</code>
	 * 
	 * @return A boolean indicating that cancel has been accepted and processing should terminate.
	 */
	@Override
	protected boolean checkCancel() {
		if (super.checkCancel()) {
			synchronized (this) {
				cancelRequesting = true;
			}
			return cancelRequesting;
		}
		return cancelRequesting;
	}

	/**
	 * Convinience method to deal with pause/resume requests when pause/resume is supported. Usage: at the begining of
	 * the main loop, if check continue.
	 * <p>
	 * <code>if (checkPause()) continue;</code>
	 * 
	 * @return A boolean indicating that pause has been accepted and processing should pause.
	 */
	@Override
	protected boolean checkPause() {
		if (isPauseSupported()) {
			if (!isPaused()) {
				if (pauseRequested()) {
					if (executingTasks.isEmpty()
						||
						executingTasks.getLast().isPaused()
						||
						executingTasks.getLast().isTerminated()) {
						notifyPaused();
						return true;
					}
					// Forward the request, will notify paused when all executing tasks are paused.
					synchronized (this) {
						resumeRequesting = false;
						pauseRequesting = true;
					}
					return true;
				}
				return false;
			}
			if (resumeRequested()) {
				if (executingTasks.isEmpty()
					||
					!executingTasks.getLast().isPaused()) {
					notifyResumed();
					return true;
				}
				// Forward the request, will notify resumed when all executing tasks are not paused.
				synchronized (this) {
					resumeRequesting = true;
					pauseRequesting = false;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests. Event if the current executin task
	 * does not support cancel, the task list can always cancel after the current task has terminated.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>. The
	 * number of steps is the number of tasks to execute, so this task always supports <code>countSteps()</code>.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps. A
	 * task list is always determinate and the number of steps is the number of tasks to execute. This method should
	 * always return <code>false</code>.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. Event if the current
	 * executin task does not support pause/resume, the task list can always pause after the current task has
	 * terminated.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}


	/**
	 * Returns the list of children tasks when this task is an upper level task made of aa list of tasks.
	 * 
	 * @return The list of children tasks
	 */
	@Override
	public List<Task> getChildren() {
		return new ArrayList<>(tasks);
	}
}
