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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A task made of multiple tasks that are executed in concurrent threads. The tasks to be executed must correctly manage
 * the <code>TaskListener</code> idiom.
 * <p>
 * There are three possible execution scenarios:
 * <ol>
 * <li>One or more tasks are indeterminate. The number of records is not known and the overall number of steps will be
 * the number of tasks to execute.</li>
 * <li>No task is indeterminate, but one or more does not support <code>countSteps()</code>. The total number of steps
 * changes as tasks are being started and notify their number of steps.</li>
 * <li>No task is indeterminate and all support <code>countSteps()</code>. An initital count is made on every task.</li>
 * </ol>
 * 
 * @author Miquel Sas
 */
public class TaskRunnerThreads extends TaskRunner {

	/**
	 * The comparator to sort processes by currentStep count ascending.
	 */
	class TaskComparator implements Comparator<Task> {
		@Override
		public int compare(Task task0, Task task1) {
			long steps0 = task0.getSteps();
			long steps1 = task1.getSteps();
			if (steps0 < steps1) {
				return -1;
			} else if (steps0 > steps1) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * The task listener that interacts with the task monitored.
	 */
	class TaskAdapter extends TaskHandler {

		/**
		 * Invoked to notify that the task is counting the number of steps.
		 * 
		 * @param task The task.
		 */
		@Override
		public void counting(Task task) {
			notifyCounting(task);
		}

		/**
		 * Invoked to notify that the task has started processing.
		 * 
		 * @param task The task.
		 */
		@Override
		public void processing(Task task) {
			// Notify strategy: Tasks -> notify a currentStep start.
			if (isNotifyTasks()) {
				notifyStepStart(task);
			}
		}

		/**
		 * Invoked to notify that the task has effectively been cancelled (after a cancel request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void cancelled(Task task) {
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
			if (allExecutingTasksArePaused()) {
				notifyPaused();
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
			if (allExecutingTasksAreNotPaused()) {
				notifyResumed();
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
			// Notify strategy: Tasks -> notify a currentStep end.
			if (isNotifyTasks()) {
				notifyStepEnd(task);
			}
		}

		/**
		 * Invoked to notify that the non indeterminate task is going to executed the argument number of steps.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepCount(Task task) {
			// Notify strategy: StepsUnknown. The total number of steps changes while tasks enter the pool and notify
			// their number of steps.
			if (isNotifyStepsUnknown()) {
				notifyStepCount(task);
			}
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
			// Notify strategy: StepsUnknown/StepsKnown. Notify the current currentStep of the pool using the common
			// message.
			if (isNotifyStepsUnknown() || isNotifyStepsKnown()) {
				increaseCurrentStepAndNotifyStepStart(task);
			}
		}

		/**
		 * Invoked to notify that the currentStep has ended.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepEnd(Task task) {
			// Notify strategy: StepsUnknown/StepsKnown. Notify the current currentStep of the pool, no matter what task
			// notified its own.
			if (isNotifyStepsUnknown() || isNotifyStepsKnown()) {
				notifyStepEnd(task);
			}
		}
	}

	/**
	 * An enumeration of the possible notify strategies.
	 */
	enum NotifyStrategy {
		Tasks,
		StepsUnknown,
		StepsKnown;
	}

	/** The list of tasks to be executed. */
	private List<Task> tasks = new ArrayList<>();
	/** List of executing tasks. */
	private List<Task> executingTasks = new ArrayList<>();
	/** The number of maximum concurrent tasks. */
	private int maximumConcurrentTasks = 10;
	/** Unique task listener. */
	private final TaskAdapter listener = new TaskAdapter();
	/** Operation boolean indicating that cancel is being requested to executing tasks. */
	private boolean cancelRequesting = false;
	/** Operation boolean indicating that pause is being requested to executing tasks. */
	private boolean pauseRequesting = false;
	/** Operation boolean indicating that resume is being requested to executing tasks. */
	private boolean resumeRequesting = false;

	/** Notify strategy. */
	private NotifyStrategy notifyStrategy;

	/** Current currentStep must be tracked by the process. */
	private long currentStep = 0;

	/** A boolean that indicates if cancel is supported, by asking the list of tasks. */
	private Boolean cancelSupported;
	/** A boolean that indicates if pause is supported, by asking the list of tasks. */
	private Boolean pauseSupported;

	/**
	 * Constructor with default maximum number of concurrent tasks.
	 * 
	 * @param session The working session.
	 */
	public TaskRunnerThreads(Session session) {
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
		tasks.add(task);
	}

	/**
	 * Increase current step and notify step start.
	 * 
	 * @param task The task.
	 */
	synchronized private void increaseCurrentStepAndNotifyStepStart(Task task) {
		currentStep += task.getStepIncrease();
		notifyStepStart(currentStep, getStepMessage(currentStep, getSteps(), null, null));
	}

	/**
	 * Notify step count (synchronized).
	 * 
	 * @param task The notifying task.
	 */
	synchronized private void notifyStepCount(Task task) {
		notifyStepCount(getSteps() + task.getSteps());
	}

	/**
	 * Forward notify counting.
	 * 
	 * @param task The task.
	 */
	synchronized private void notifyCounting(Task task) {
		notifyCounting();
	}

	/**
	 * Forward notify start when notify strategy is Tasks.
	 * 
	 * @param task The task.
	 */
	synchronized private void notifyStepStart(Task task) {
		long step = getStep() + 1;
		long steps = getSteps();
		notifyStepStart(step, getStepMessage(step, steps, null, null));
	}

	/**
	 * Forward notify step end.
	 * 
	 * @param task The task.
	 */
	synchronized private void notifyStepEnd(Task task) {
		notifyStepEnd();
	}

	/**
	 * Returns the notify strategy.
	 * 
	 * @return The notify strategy.
	 */
	private NotifyStrategy getNotifyStrategy() {
		if (notifyStrategy == null) {
			// Any indeterminate -> Tasks
			for (Task task : tasks) {
				if (task.isIndeterminate()) {
					notifyStrategy = NotifyStrategy.Tasks;
					return notifyStrategy;
				}
			}
			// Any does not support count steps -> StepsUnknown
			for (Task task : tasks) {
				if (!task.isCountStepsSupported()) {
					notifyStrategy = NotifyStrategy.StepsUnknown;
					return notifyStrategy;
				}
			}
			notifyStrategy = NotifyStrategy.StepsKnown;
		}
		return notifyStrategy;
	}

	/**
	 * Returns a boolean indicating if notify strategy is <i>Tasks</i>.
	 * 
	 * @return A boolean.
	 */
	private boolean isNotifyTasks() {
		return getNotifyStrategy() == NotifyStrategy.Tasks;
	}

	/**
	 * Returns a boolean indicating if notify strategy is <i>StepsUnknown</i>.
	 * 
	 * @return A boolean.
	 */
	private boolean isNotifyStepsUnknown() {
		return getNotifyStrategy() == NotifyStrategy.StepsUnknown;
	}

	/**
	 * Returns a boolean indicating if notify strategy is <i>StepsKnown</i>.
	 * 
	 * @return A boolean.
	 */
	private boolean isNotifyStepsKnown() {
		return getNotifyStrategy() == NotifyStrategy.StepsKnown;
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param maximumConcurrentTasks The maximum number of concurrent tasks.
	 */
	public TaskRunnerThreads(Session session, int maximumConcurrentTasks) {
		super(session);
		this.maximumConcurrentTasks = maximumConcurrentTasks;
	}

	/**
	 * Returns the maximum number of concurrent tasks.
	 * 
	 * @return The maximum number of concurrent tasks.
	 */
	public int getMaximumConcurrentTasks() {
		return maximumConcurrentTasks;
	}

	/**
	 * Sets the maximum number of concurrent tasks.
	 * 
	 * @param maximumConcurrentTasks The maximum number of concurrent tasks.
	 */
	public void setMaximumConcurrentTasks(int maximumConcurrentTasks) {
		this.maximumConcurrentTasks = maximumConcurrentTasks;
	}

	/**
	 * If the task is determinate, a call to this method forces counting (and storing) the number of steps. If the task
	 * is not derteminate, this method has no sense.
	 * <p>
	 * Steps are counted if the overall process is determinate.
	 * 
	 * @return The number of steps.
	 */
	@Override
	public long countSteps() {
		return getSteps();
	}

	/**
	 * Process step count when notify strategy is StepsKnown.
	 * 
	 * @throws Exception If such an error occurs.
	 */
	private void processStepCount() throws Exception {

		// Already counted...
		if (getSteps() > 0) {
			return;
		}

		long steps = 0;
		long step = 0;

		String prefix = getSession().getString("panelProgressCounting") + ": ";
		
		// Save notify modulus and set it to one.
		int notifyModulus = getNotifyModulus();
		setNotifyModulus(1);

		// and trace it while performing...
		notifyStepCount(tasks.size());
		int i = 0;
		while (i < tasks.size()) {
			if (checkCancel()) {
				break;
			}
			if (checkPause()) {
				continue;
			}
			step += 1;
			Task task = tasks.get(i);
			notifyStepStart(step, getStepMessage(step, tasks.size(), prefix, null));
			steps += task.countSteps();
			notifyStepEnd();
			i++;
		}
		
		// Restore notify modulus.
		setNotifyModulus(notifyModulus);

		// Notify step count.
		notifyStepCount(steps);
	}

	/**
	 * Ensure that all tasks have a name and description.
	 */
	private void ensureTasksNamesAndDescriptions() {
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			if (task.getName() == null || task.getName().isEmpty()) {
				task.setName(StringUtils.leftPad(Integer.toString(i), tasks.size(), "0"));
			}
			if (task.getDescription() == null || task.getDescription().isEmpty()) {
				task.setDescription("Task " + StringUtils.leftPad(Integer.toString(i), tasks.size(), "0"));
			}
		}
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {
		
		// Reset control member in case of re-execute.
		executingTasks.clear();
		cancelRequesting = false;
		pauseRequesting = false;
		resumeRequesting = false;
		notifyStrategy = null;
		currentStep = 0;
		cancelSupported = null;
		pauseSupported = null;
		
		// Ensure tasks names and descriptions.
		ensureTasksNamesAndDescriptions();

		// Notify strategy: Tasks.
		if (isNotifyTasks()) {
			executeNotifyStrategyTasks();
			return;
		}

		// Notify strategy: StepsUnknown.
		if (isNotifyStepsUnknown()) {
			executeNotifyStrategyStepsUnknown();
			return;
		}

		// Notify strategy: StepsKnow.
		if (isNotifyStepsKnown()) {
			executeNotifyStrategyStepsKnown();
			return;
		}

	}

	/**
	 * Impements the tasks notify strategy.
	 * 
	 * @throws Exception If such an error occurs.
	 */
	private void executeNotifyStrategyTasks() throws Exception {
		// Fill the list of tasks to be executed.
		List<Task> tasksToExecute = new ArrayList<>(tasks);
		// Notify the total number of steps.
		notifyStepCount(tasksToExecute.size());
		// Lauch execution.
		executeList(tasksToExecute);
	}

	/**
	 * Impements the steps unknown notify strategy.
	 * 
	 * @throws Exception If such an error occurs.
	 */
	private void executeNotifyStrategyStepsUnknown() throws Exception {
		// Fill the list of tasks to be executed.
		List<Task> tasksToExecute = new ArrayList<>(tasks);
		// The total number of steps is unknown at this point, just lauch execution.
		executeList(tasksToExecute);
	}

	/**
	 * Impements the steps known notify strategy.
	 * 
	 * @throws Exception If such an error occurs.
	 */
	private void executeNotifyStrategyStepsKnown() throws Exception {
		// Do count steps.
		processStepCount();
		// Once counted, sort tasks.
		Task[] taskArray = tasks.toArray(new Task[tasks.size()]);
		Arrays.sort(taskArray, new TaskComparator());
		// Fill the list of tasks to be executed.
		List<Task> tasksToExecute = new ArrayList<>();
		for (Task task : taskArray) {
			tasksToExecute.add(task);
		}
		// The total number of steps is unknown at this point, just lauch execution.
		executeList(tasksToExecute);
	}

	/**
	 * Remove the tasks terminated from the list of executing tasks.
	 */
	private void removeTerminated() {
		List<Task> toRemove = new ArrayList<>();
		for (Task task : executingTasks) {
			if (task.isTerminated()) {
				toRemove.add(task);
			}
		}
		executingTasks.removeAll(toRemove);
	}

	/**
	 * Add task pending to execute to the list of executing tasks and start them.
	 * 
	 * @param tasksToExecute The list of task to execute.
	 */
	private void addPendingTasksToExecute(List<Task> tasksToExecute) {
		while (!tasksToExecute.isEmpty() && executingTasks.size() < getMaximumConcurrentTasks()) {
			Task task = ListUtils.removeLast(tasksToExecute);
			executingTasks.add(task);
			new Thread(task, task.toString()).start();
		}
	}

	/**
	 * Execute the list of tasks adding them at the pool in the inverse list order.
	 * 
	 * @param tasksToExecute The list of tasks to execute.
	 * @throws Exception If any executing tasks throws it.
	 */
	private void executeList(List<Task> tasksToExecute) throws Exception {

		// Process them. To avoid repetitive questions to tasks, previously check if cancel and pause are supported.
		boolean continueProcessing = true;
		while (true) {

			// Remove already terminated tasks from the executing list.
			removeTerminated();

			// Manage cancel request.
			if (checkCancel()) {
				// Do not break, must wait for all executing tasks to cancel.
				continueProcessing = false;
			}

			// Manage pause request.
			if (checkPause()) {
				continue;
			}

			// If not should continue processing, the exit when all currently executing tasks are terminated.
			if (!continueProcessing) {
				if (executingTasks.isEmpty()) {
					break;
				}
			}

			// Add tasks to execute if continue processing applies.
			if (continueProcessing && !pauseRequesting) {
				addPendingTasksToExecute(tasksToExecute);
			}

			// If all tasks have terminated, exit
			if (tasksToExecute.isEmpty() && executingTasks.isEmpty()) {
				break;
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
	@Override
	protected boolean checkCancel() {
		if (super.checkCancel()) {
			synchronized (this) {
				cancelRequesting = true;
			}
			return true;
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
	@Override
	protected boolean checkPause() {
		if (isPauseSupported()) {
			if (!isPaused()) {
				if (pauseRequested()) {
					if (executingTasks.isEmpty()) {
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
				if (executingTasks.isEmpty()) {
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
	 * Returns a boolean indicating if all current executing tasks are paused.
	 * 
	 * @return A boolean.
	 */
	private boolean allExecutingTasksArePaused() {
		for (Task task : executingTasks) {
			if (!task.isPaused()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a boolean indicating if all current executing tasks are not paused.
	 * 
	 * @return A boolean.
	 */
	private boolean allExecutingTasksAreNotPaused() {
		for (Task task : executingTasks) {
			if (task.isPaused()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests. Cancel is supported if all tasks to
	 * be executed support cancel.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		if (cancelSupported == null) {
			cancelSupported = true;
			for (Task task : tasks) {
				if (!task.isCancelSupported()) {
					cancelSupported = false;
					break;
				}
			}
		}
		return cancelSupported;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This concurrent task is indeterminate is always determinate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. Pause/resume is supported
	 * if all tasks to be executed support pause/resume.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		if (pauseSupported == null) {
			pauseSupported = true;
			for (Task task : tasks) {
				if (!task.isPauseSupported()) {
					pauseSupported = false;
				}
			}
		}
		return pauseSupported;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task supports it if the notify strategy is <i>StepsKnown</i>.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return isNotifyStepsKnown();
	}

}
