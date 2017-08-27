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

package com.qtplaf.library.swing.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionList;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.task.TaskHandler;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.Timestamp;

/**
 * A panel aimed to monitor the progress of a time consuming task normally executed in thread separated from the event
 * dispatch thread.
 * <p>
 * The panel has the following components layed vertically:
 * <ul>
 * <li>An optional task label to describe the task being executed.</li>
 * <li>An optional processStatus label to describe initial counting, the current completion percentage, with optional
 * <i>Pause</i>/<i>Resume</i> and <i>Cancel</i> buttons.</li>
 * <li>An optional time label to describe the time elapsed, estimated and remaining.</li>
 * <li>An optional step label to describe step information.</li>
 * <li>The progress bar, that can be time indeterminate.</li>
 * <li>An error label with an info button to review the error detail if produced.</li>
 * <li>An optional list of user labels to describe any additional information. The name of the labels can not be one of
 * the predefined names.</li>
 * </ul>
 * All labels are set by defaul with the dialog font normal, except for the task label that is bold. Label fonts can be
 * changed.
 * 
 * @author Miquel Sas
 */
public class JPanelProgress extends JPanel {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Mouse listener.
	 */
	class MouseAdapter extends MouseHandler {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * Enum run actions.
	 */
	enum Run {
		Start,
		Pause,
		Resume
	}

	/**
	 * Action start/pause/resume.
	 */
	class ActionRun extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionRun() {
			ActionUtils.setMenuItemSourceText(this, getSession().getString("tokenPause"));
			ActionUtils.setShortDescription(this, getSession().getString("panelProgressPause"));
			ActionUtils.setSession(this, getSession());
			ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
			ActionUtils.setSmallIcon(this, Icons.flat_24x24_pause);
			ActionUtils.setSortIndex(this, 0);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// No monitoring task.
			if (monitoringTask == null) {
				return;
			}
			// Task is not processing start it.
			if (!monitoringTask.isProcessing()) {
				new Thread(monitoringTask, monitoringTask.getName()).start();
				setActionRunProperties(Run.Pause);
				getButtonCancel().setEnabled(true);
				return;
			}
			// Task is not paused, request it.
			if (!monitoringTask.isPaused()) {
				pauseRequested = true;
				return;
			}
			// Task is paused, request resume.
			if (monitoringTask.isPaused()) {
				resumeRequested = true;
				return;
			}
		}
	}

	/**
	 * Action cancel.
	 */
	class ActionCancel extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionCancel() {
			ActionUtils.setMenuItemSourceText(this, getSession().getString("tokenCancel"));
			ActionUtils.setShortDescription(this, getSession().getString("panelProgressCancel"));
			ActionUtils.setSession(this, getSession());
			ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
			ActionUtils.setSmallIcon(this, Icons.flat_24x24_cancel);
			ActionUtils.setSortIndex(this, 1);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// No monitoring task.
			if (monitoringTask == null) {
				return;
			}
			// Task is not cancelled, request it.
			if (!monitoringTask.isCancelled()) {
				cancelRequested = true;
				return;
			}
		}
	}

	/**
	 * Action error info.
	 */
	class ActionErrorInfo extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionErrorInfo() {
			ActionUtils.setMenuItemSourceText(this, getSession().getString("panelProgressError"));
			ActionUtils.setShortDescription(this, getSession().getString("panelProgressErrorInfo"));
			ActionUtils.setSession(this, getSession());
			ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
			ActionUtils.setSmallIcon(this, Icons.flat_24x24_info);
			ActionUtils.setSortIndex(this, 3);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// No monitoring task.
			if (monitoringTask == null) {
				return;
			}
			if (errorDescription != null && !errorDescription.isEmpty()) {
				showErrorDescription();
				return;
			}
		}
	}

	/**
	 * Action close.
	 */
	class ActionClose extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionClose() {
			ActionUtils.setMenuItemSourceText(this, getSession().getString("tokenClose"));
			ActionUtils.setShortDescription(this, getSession().getString("panelProgressClose"));
			ActionUtils.setSession(this, getSession());
			ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
			ActionUtils.setSmallIcon(this, Icons.flat_24x24_close);
			ActionUtils.setSortIndex(this, 1);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// No monitoring task.
			if (monitoringTask == null) {
				return;
			}
			// Task must be terminated to close.
			if (!monitoringTask.isTerminated()) {
				return;
			}
			// Close the panel.
			if (JPanelProgress.this.getParent() != null) {
				Container container = JPanelProgress.this.getParent();
				if (container instanceof JPanelProgressGroup) {
					JPanelProgressGroup group = (JPanelProgressGroup) container;
					group.remove(JPanelProgress.this);
				} else {
					container.remove(JPanelProgress.this);
					container.repaint();
				}
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
			// Register counting.
			counting = true;
			// Update status counting.
			updateStatusCounting();
			getProgressBar().setIndeterminate(true);
		}

		/**
		 * Invoked to notify that the task has started processing.
		 * 
		 * @param task The task.
		 */
		@Override
		public void processing(Task task) {
			synchronized (lock) {
				processStartTime = System.currentTimeMillis();
				// Update status.
				updateStatusProcessing();
				// If indeterminated launch the thread to update time
				if (task.isIndeterminate()) {
					String name = task.toString() + "Time updater";
					new Thread(new TimeProcessedUpdater(task), name).start();
					getProgressBar().setIndeterminate(true);
				}
			}
		}

		/**
		 * Invoked to notify that the task has effectively been cancelled (after a cancel request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void cancelled(Task task) {
			synchronized (lock) {
				// Cancel requested is no longer valid.
				cancelRequested = false;
				// If the bar is indeterminate, reset it.
				if (getProgressBar().isIndeterminate()) {
					getProgressBar().setIndeterminate(false);
				}
				// Disable cancel and pause/resume buttons.
				getButtonCancel().setEnabled(false);
				getButtonPauseResume().setEnabled(false);
				// Update status.
				if (task.isIndeterminate()) {
					// Update status to cancelled.
					updateStatusCancelled();
				} else {
					// Update status processed.
					updateStatusProcessed(lastPerformed);
				}
				// Update time ellapsed.
				updateTimeElapsed();
				// Update error with cancelled.
				updateErrorCancelledByUser(task);
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
			return cancelRequested;
		}

		/**
		 * Invoked to notify that the task has effectively been paused (after a pause request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void paused(Task task) {
			synchronized (lock) {
				// Pause requested is no longer valid.
				pauseRequested = false;
				// Set the proper icon to the start/pause/resume button.
				setActionRunProperties(Run.Resume);
				// If is indeterminate, stop the progress bar.
				if (task.isIndeterminate() || counting) {
					getProgressBar().setIndeterminate(false);
					updateStatusPaused();
				}
			}
		}

		/**
		 * Invoked to know if the task has been requested to pause. In non indeterminate tasks, it would normally be
		 * invoked at every step.
		 * 
		 * @param task The task.
		 * @return A boolean indicating if the task has been requested to pause.
		 */
		@Override
		public boolean pauseRequested(Task task) {
			return pauseRequested;
		}

		/**
		 * Invoked to notify that the task has effectively been resumed (after a resume request).
		 * 
		 * @param task The task.
		 */
		@Override
		public void resumed(Task task) {
			synchronized (lock) {
				// Resume requested is no longer valid.
				resumeRequested = false;
				// Set the proper icon to the pause/resume button.
				setActionRunProperties(Run.Pause);
				// If is indeterminate, restart the progress bar.
				if (task.isIndeterminate() || counting) {
					getProgressBar().setIndeterminate(true);
					if (counting) {
						updateStatusCounting();
					} else {
						updateStatusProcessing();
					}
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
			return resumeRequested;
		}

		/**
		 * Invoked to notify that the task has terminated.
		 * 
		 * @param task The task.
		 */
		@Override
		public void terminated(Task task) {
			synchronized (lock) {
				// Disable cancel and pause/resume buttons.
				getButtonPauseResume().setEnabled(false);
				getButtonCancel().setEnabled(false);
				if (errorDescription == null || errorDescription.isEmpty()) {
					getButtonErrorInfo().setEnabled(false);
				}
				// Update status.
				if (task.isIndeterminate()) {
					// If not cancelled update status to processed 100%
					if (!task.isCancelled()) {
						updateStatusProcessed(100);
					}
				} else {
					// Update status processed to its percentage.
					updateStatusProcessed(lastPerformed);
				}
				// Update time elapsed.
				updateTimeElapsed();
				// Check error if applicable.
				updateStatusError(task);
				// Reset progress bar if indeterminate.
				if (task.isIndeterminate() || getProgressBar().isIndeterminate()) {
					getProgressBar().setIndeterminate(false);
				}
				// Enable close button.
				getButtonClose().setEnabled(true);
			}
		}

		/**
		 * Invoked to notify that the non indeterminate task is going to executed the steps.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepCount(Task task) {
			// Counting is no more valid.
			counting = false;
			// Reset the progress bar to determinate.
			getProgressBar().setIndeterminate(false);
			// Update the progress bar.
			updateProgressBar(task.getStep(), task.getSteps());
		}

		/**
		 * Invoked to notify that the step is going to start. Note that steps should start at 1 and end at the total
		 * number of steps to properly manage notification modulus and monitor.
		 * 
		 * @param task The task.
		 * @param text The text explaining the step.
		 */
		@Override
		public void stepStart(Task task, String text) {
			// Update the progress bar.
			updateProgressBar(task.getStep(), task.getSteps());
			// Update status and time processing information.
			updateStatusAndTimeProcessing(lastPerformed);
			// Trace the text.
			getLabelStep().setText(text);
		}

		/**
		 * Invoked to notify that the step has ended.
		 * 
		 * @param task The task.
		 */
		@Override
		public void stepEnd(Task task) {
		}

		/**
		 * Invoked to trace a message to the argument label.
		 * 
		 * @param task The task.
		 * @param label The label identifier.
		 * @param message The message.
		 */
		@Override
		public void setLabel(Task task, String label, String message) {
			getAdditionalLabel(label).setText(message);
		}
	}

	/**
	 * A runnable to update time processed when the task is indeterminate.
	 */
	class TimeProcessedUpdater implements Runnable {
		private Task task;
		private int sleepMaximum = 5000;
		private int sleepIncrease = 50;

		TimeProcessedUpdater(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			int sleep = 0;
			while (true) {
				if (sleep < sleepMaximum) {
					sleep += sleepIncrease;
				}
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ignore) {
				}
				if (task.isTerminated()) {
					break;
				}
				updateTimeElapsed();
			}
		}
	}

	/** The list of actions. */
	private ActionList actions = new ActionList();
	/** Task label. */
	private JLabel labelTask;
	/** Status label. */
	private JLabel labelStatus;
	/** Time label. */
	private JLabel labelTime;
	/** Step label. */
	private JLabel labelStep;
	/** Progress bar. */
	private JProgressBar progressBar;
	/** Error text area. */
	private JLabel labelError;
	/** List of optional/additional labels. */
	private List<JLabel> additionalLabels = new ArrayList<>();

	/** Default insets. */
	private Insets insets = new Insets(2, 2, 2, 2);

	/** Working session. */
	private Session session;
	/** Initial panel width. */
	private int panelProgressWidth = 500;

	/** Default label font. */
	private Font defaultLabelFont = new Font("Dialog", Font.PLAIN, 12);

	/** The number of processSteps of the monitored process. */
	private long processSteps;
	/** The time the monitored process started. */
	private long processStartTime;
	/** A boolean that indicates if a pause has been requested, by pressing the pause button. */
	private boolean pauseRequested = false;
	/** A boolean that indicates if resume has been requested, by pressing the resume button. */
	private boolean resumeRequested = false;
	/** A boolean that indicates if cancel has been requested, by pressing the cancel button. */
	private boolean cancelRequested = false;
	/** Detailed error description. */
	private String errorDescription;
	/** Progress decimals. */
	private int progressDecimals = 1;

	/** Track last perform percentage to update status info when cancelled or an error has ocurred. */
	private double lastPerformed = 0;

	/** General purpose lock. */
	private Object lock = new Object();

	/** The task to monitor. */
	private Task monitoringTask;

	/**
	 * A boolean that indicates that the task is counting. Set to <tt>true</tt> when notified counting, and to
	 * <tt>false</tt> when notified step count.
	 */
	private boolean counting = false;

	/**
	 * Default constructor.
	 * 
	 * @param session The working session.
	 */
	public JPanelProgress(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Check additional labels names.
	 * 
	 * @param name The name of the label.
	 */
	private void checkAdditionalLabelName(String name) {
		if (name.equals("Task") || name.equals("Status") || name.equals("Time") || name.equals("Step")) {
			throw new IllegalArgumentException("Invalid additional label name");
		}
	}

	/**
	 * Add an additional label with the given name and sample text.
	 * 
	 * @param name The name of the label.
	 */
	public void addAdditionalLabel(String name) {
		addAdditionalLabel(name, defaultLabelFont);
	}

	/**
	 * Add an additional label with the given name and sample text.
	 * 
	 * @param name The name of the label.
	 * @param font The font to apply.
	 */
	public void addAdditionalLabel(String name, Font font) {
		checkAdditionalLabelName(name);
		JLabel label = new JLabel();
		label.setFont(font);
		label.setName(name);
		additionalLabels.add(label);
	}

	/**
	 * Install the process listener to the process being monitored.
	 * 
	 * @param task The task to monitor.
	 */
	synchronized public void monitorTask(Task task) {

		// Avoid monitoring a task when there is a taskbeing monitored that has not terminated.
		if (monitoringTask != null) {
			if (!monitoringTask.isTerminated()) {
				throw new IllegalStateException("Currently monitoring a non terminated task.");
			}
		}

		// Store task and register listener.
		monitoringTask = task;
		monitoringTask.addListener(new TaskAdapter());

		// If the monitoring task has additional labels and no label has been added, add them.
		if (additionalLabels.isEmpty() && !monitoringTask.getAdditionalLabels().isEmpty()) {
			for (String label : monitoringTask.getAdditionalLabels()) {
				addAdditionalLabel(label);
			}
		}

		// Do layout.
		layoutComponents();

		// If task has not started set the run button to start and disable cancel.
		if (!monitoringTask.isProcessing()) {
			setActionRunProperties(Run.Start);
			getButtonCancel().setEnabled(false);
		}
	}

	/**
	 * Returns the task being monitored.
	 * 
	 * @return The task being monitored.
	 */
	public Task getMonitoringTask() {
		return monitoringTask;
	}

	/**
	 * Set the task label text.
	 * 
	 * @param text The text.
	 */
	public void setTextTask(String text) {
		getLabelTask().setText(text);
	}

	/**
	 * Set the processStatus label text.
	 * 
	 * @param text The text.
	 */
	public void setTextStatus(String text) {
		getLabelStatus().setText(text);
	}

	/**
	 * Set the step label text.
	 * 
	 * @param text The text.
	 */
	public void setTextStep(String text) {
		getLabelStep().setText(text);
	}

	/**
	 * Set the time label text.
	 * 
	 * @param text The text.
	 */
	public void setTextTime(String text) {
		getLabelTime().setText(text);
	}

	/**
	 * Sets the additional label text.
	 * 
	 * @param name The name of the label.
	 * @param text The text.
	 */
	public void setTextAdditionalLabel(String name, String text) {
		getAdditionalLabel(name).setText(text);
	}

	/**
	 * Set the error text.
	 * 
	 * @param text The text.
	 */
	public void setTextError(String text) {
		getLabelError().setText(text);
		getButtonErrorInfo().setEnabled(true);
	}

	/**
	 * Set the task label font.
	 * 
	 * @param font The font.
	 */
	public void setFontTask(Font font) {
		getLabelTask().setFont(font);
	}

	/**
	 * Set the processStatus label font.
	 * 
	 * @param font The font.
	 */
	public void setFontStatus(Font font) {
		getLabelStatus().setFont(font);
	}

	/**
	 * Set the time label font.
	 * 
	 * @param font The font.
	 */
	public void setFontTime(Font font) {
		getLabelTime().setFont(font);
	}

	/**
	 * Set the step label font.
	 * 
	 * @param font The font.
	 */
	public void setFontStep(Font font) {
		getLabelStep().setFont(font);
	}

	/**
	 * Sets the additional label font.
	 * 
	 * @param name The name.
	 * @param font The font.
	 */
	public void setFontAdditionalLabel(String name, Font font) {
		getAdditionalLabel(name).setFont(font);
	}

	/**
	 * Sets the action start/pause/resume properties.
	 * 
	 * @param run The run action.
	 */
	private void setActionRunProperties(Run run) {
		if (run.equals(Run.Pause)) {
			getButtonPauseResume().setIcon(ImageIconUtils.getImageIcon(Icons.flat_24x24_pause));
		} else {
			getButtonPauseResume().setIcon(ImageIconUtils.getImageIcon(Icons.flat_24x24_resume));
		}
		String menuItem = null;
		String shortDescription = null;
		switch (run) {
		case Start:
			menuItem = "tokenStart";
			shortDescription = "panelProgressStart";
			break;
		case Pause:
			menuItem = "tokenPause";
			shortDescription = "panelProgressPause";
			break;
		case Resume:
			menuItem = "tokenResume";
			shortDescription = "panelProgressResume";
			break;
		default:
			throw new IllegalArgumentException();
		}
		ActionUtils.setMenuItemSourceText(getButtonPauseResume().getAction(), getSession().getString(menuItem));
		ActionUtils.setShortDescription(getButtonPauseResume().getAction(), getSession().getString(shortDescription));
	}

	/**
	 * Returns the additional label.
	 * 
	 * @param name The name.
	 * @return The label, throws IllegalArgumentException if the name is not valid.
	 */
	private JLabel getAdditionalLabel(String name) {
		for (JLabel label : additionalLabels) {
			if (label.getName().equals(name)) {
				return label;
			}
		}
		throw new IllegalArgumentException("Invalid label name");
	}

	/**
	 * Set the labels and progress bar preferred and minimum sizes based on a sample text.
	 */
	private void setLabelsAndProgressBarPreferredAndminimumSizes() {
		String sampleText = "Sample text";
		SwingUtils.setLabelPreferredAndMinimumSize(getLabelTask(), sampleText);
		SwingUtils.setLabelPreferredAndMinimumSize(getLabelStatus(), sampleText);
		SwingUtils.setLabelPreferredAndMinimumSize(getLabelTime(), sampleText);
		SwingUtils.setLabelPreferredAndMinimumSize(getLabelStep(), sampleText);
		for (JLabel label : additionalLabels) {
			SwingUtils.setLabelPreferredAndMinimumSize(label, sampleText);
		}
		SwingUtils.setLabelPreferredAndMinimumSize(getLabelError(), sampleText);
		Dimension size = SwingUtils.getLabelPreferredSize(new JLabel());
		getProgressBar().setPreferredSize(size);
		getProgressBar().setMinimumSize(size);
	}

	/**
	 * Initialize and layout components.
	 */
	private void layoutComponents() {
		removeAll();

		actions.clear();
		actions.add(new ActionRun());
		actions.add(new ActionCancel());
		actions.add(new ActionErrorInfo());
		actions.add(new ActionClose());

		getButtonCancel().setVisible(monitoringTask.isCancelSupported());
		getButtonPauseResume().setVisible(monitoringTask.isPauseSupported());
		getProgressBar().setIndeterminate(false);
		getButtonClose().setEnabled(false);
		getButtonErrorInfo().setEnabled(false);

		getLabelTask().setText(monitoringTask.toString());

		// Layout manager.
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());

		// Set preferred and minimum sizes.
		setLabelsAndProgressBarPreferredAndminimumSizes();

		int gridy = 0;

		// Panel for the task label and buttons.
		JPanel panelTask = new JPanel();
		panelTask.setOpaque(false);
		panelTask.setLayout(new GridBagLayout());
		panelTask.add(getLabelTask(), getConstraintsLabelTask(0));
		panelTask.add(getButtonPauseResume(), getConstraintsButton(1));
		panelTask.add(getButtonCancel(), getConstraintsButton(2));
		panelTask.add(getButtonErrorInfo(), getConstraintsButton(3));
		panelTask.add(getButtonClose(), getConstraintsButton(4));
		add(panelTask, getConstraints(gridy++));

		// Status label.
		add(getLabelStatus(), getConstraints(gridy++));

		// Time label.
		add(getLabelTime(), getConstraints(gridy++));

		// Step label.
		add(getLabelStep(), getConstraints(gridy++));

		// Progress bar.
		add(getProgressBar(), getConstraints(gridy++));

		// Additional labels if any.
		for (JLabel label : additionalLabels) {
			add(label, getConstraints(gridy++));
		}

		// Error label.
		add(getLabelError(), getConstraints(gridy++));

		// Calculate and set the preferred size.
		setPreferredSize(calculatePreferredSize());

		// The mouse listener to handle popup menu.
		MouseAdapter mouseAdapter = new MouseAdapter();
		List<Component> components = SwingUtils.getAllComponents(this);
		for (Component component : components) {
			component.addMouseListener(mouseAdapter);
		}
	}

	/**
	 * Calculate and return the preferred size.
	 * 
	 * @return The calculated preferred size.
	 */
	protected Dimension calculatePreferredSize() {
		int height = 0;
		height +=
			insets.top
				+ Math.max(getLabelTask().getPreferredSize().height, getButtonClose().getPreferredSize().height)
				+ insets.bottom;
		height += insets.top + getLabelStatus().getPreferredSize().height + insets.bottom;
		height += insets.top + getLabelTime().getPreferredSize().height + insets.bottom;
		height += insets.top + getLabelStep().getPreferredSize().height + insets.bottom;
		height += insets.top + getProgressBar().getPreferredSize().height + insets.bottom;
		for (JLabel label : additionalLabels) {
			height += insets.top + label.getPreferredSize().height + insets.bottom;
		}
		height += insets.top + getLabelError().getPreferredSize().height + insets.bottom;
		return new Dimension(panelProgressWidth, height);
	}

	/**
	 * Returns the constraints for a button in the task row.
	 * 
	 * @param gridx Grid x.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsButton(int gridx) {
		return getConstraints(GridBagConstraints.WEST, GridBagConstraints.NONE, gridx, 0, 0, new Insets(0, 1, 0, 0));
	}

	/**
	 * Returns the constraints for the task label.
	 * 
	 * @param gridx Grid x.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsLabelTask(int gridx) {
		return getConstraints(
			GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL,
			gridx,
			0,
			1,
			new Insets(0, 0, 0, 0));
	}

	/**
	 * Returns the constraints for an edit field.
	 * 
	 * @param gridy Grid y coordinate.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraints(int gridy) {
		return getConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, gridy, 1, insets);
	}

	/**
	 * Returns the constraints for an edit field.
	 * 
	 * @param anchor Anchor.
	 * @param fill Fill.
	 * @param gridx Grid x coordinate.
	 * @param gridy Grid y coordinate.
	 * @param weightx Weight x.
	 * @param insets Insets.
	 * @return The constraints.
	 */
	private
		GridBagConstraints
		getConstraints(int anchor, int fill, int gridx, int gridy, double weightx, Insets insets) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = anchor;
		constraints.fill = fill;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.insets = insets;
		constraints.weightx = weightx;
		constraints.weighty = 1;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		return constraints;
	}

	/**
	 * Returns the task label.
	 * 
	 * @return The task label.
	 */
	private JLabel getLabelTask() {
		if (labelTask == null) {
			labelTask = new JLabel();
			labelTask.setName("Task");
			labelTask.setFont(new Font("Dialog", Font.BOLD, 14));
		}
		return labelTask;
	}

	/**
	 * Returns the processStatus label.
	 * 
	 * @return The task label.
	 */
	private JLabel getLabelStatus() {
		if (labelStatus == null) {
			labelStatus = new JLabel();
			labelStatus.setName("Status");
			labelStatus.setFont(defaultLabelFont);
		}
		return labelStatus;
	}

	/**
	 * Returns the pause button.
	 * 
	 * @return The pause button.
	 */
	private JButton getButtonPauseResume() {
		return actions.getButton(ActionRun.class);
	}

	/**
	 * Returns the cancel button.
	 * 
	 * @return The cancel button.
	 */
	private JButton getButtonCancel() {
		return actions.getButton(ActionCancel.class);
	}

	/**
	 * Returns the close button.
	 * 
	 * @return The cancel button.
	 */
	private JButton getButtonClose() {
		return actions.getButton(ActionClose.class);
	}

	/**
	 * Returns the error info button.
	 * 
	 * @return The error info button.
	 */
	private JButton getButtonErrorInfo() {
		return actions.getButton(ActionErrorInfo.class);
	}

	/**
	 * Returns the time label.
	 * 
	 * @return The time label.
	 */
	private JLabel getLabelTime() {
		if (labelTime == null) {
			labelTime = new JLabel();
			labelTime.setName("Time");
			labelTime.setFont(defaultLabelFont);
		}
		return labelTime;
	}

	/**
	 * Returns the step label.
	 * 
	 * @return The step label.
	 */
	private JLabel getLabelStep() {
		if (labelStep == null) {
			labelStep = new JLabel();
			labelStep.setName("Step");
			labelStep.setFont(defaultLabelFont);
		}
		return labelStep;
	}

	/**
	 * Returns the progress bar.
	 * 
	 * @return The progress bar.
	 */
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setOpaque(false);
			progressBar.setBorder(new LineBorder(Color.LIGHT_GRAY));
			progressBar.setIndeterminate(false);
		}
		return progressBar;
	}

	/**
	 * Returns the error text area.
	 * 
	 * @return The error text area.
	 */
	private JLabel getLabelError() {
		if (labelError == null) {
			labelError = new JLabel();
			labelError.setName("Error");
			labelError.setFont(defaultLabelFont);
			labelError.setForeground(Color.RED);
		}
		return labelError;
	}

	/**
	 * Returns the progress time elapsed information string.
	 * 
	 * @return The time elapsed information.
	 */
	private String getProgressTimeElapsed() {
		double processCurrenTime = System.currentTimeMillis();
		double timeElapsed = processCurrenTime - processStartTime;
		StringBuilder b = new StringBuilder();
		b.append(getSession().getString("tokenTime"));
		b.append(" ");
		b.append(getSession().getString("tokenElapsed").toLowerCase());
		b.append(" ");
		b.append(getTimeString(timeElapsed));
		return b.toString();
	}

	/**
	 * Returns the progress time information string given the percentage performed, as an elapsed, estimated and pending
	 * info.
	 * 
	 * @param performed The performed percentage.
	 * @return The time information.
	 */
	private String getProgressTimeInfo(double performed) {
		StringBuilder b = new StringBuilder();
		if (performed == 0) {
			return b.toString();
		}

		double processCurrenTime = System.currentTimeMillis();
		double timeElapsed = processCurrenTime - processStartTime;
		double timeEstimated = timeElapsed * (100.0 / Double.valueOf(performed));
		double timeRemaining = timeEstimated - timeElapsed;

		b.append(getSession().getString("tokenTime"));
		b.append(" ");
		b.append(getSession().getString("tokenElapsed").toLowerCase());
		b.append(" ");
		b.append(getTimeString(timeElapsed));
		b.append(", ");
		b.append(getSession().getString("tokenEstimated").toLowerCase());
		b.append(" ");
		b.append(getTimeString(timeEstimated));
		b.append(", ");
		b.append(getSession().getString("tokenRemaining").toLowerCase());
		b.append(" ");
		b.append(getTimeString(timeRemaining));
		b.append(", ");
		b.append(getSession().getString("panelProgressStartTime").toLowerCase());
		b.append(" ");
		b.append(getTimestampString(processStartTime));
		b.append(", ");
		b.append(getSession().getString("panelProgressEndTime").toLowerCase());
		b.append(" ");
		b.append(getTimestampString((long) (processStartTime + timeEstimated)));

		return b.toString();
	}

	/**
	 * Returns the formatted timestamp.
	 * 
	 * @param time The time in millis.
	 * @return The formatted timestamp.
	 */
	private String getTimestampString(long time) {
		return FormatUtils.formattedFromTimestamp(new Timestamp(time), getSession().getLocale());
	}

	/**
	 * Returns the time information string (seconds,minutes or hours).
	 * 
	 * @param time The time in millis.
	 * @return The time info.
	 */
	private String getTimeString(double time) {
		int decimals = 1;
		double seconds = (time / 1000.0);
		if (seconds < 60) {
			StringBuilder b = new StringBuilder();
			b.append(FormatUtils.formattedFromDouble(seconds, decimals, getSession().getLocale()));
			b.append(" ");
			b.append(getSession().getString("tokenSeconds").toLowerCase());
			return b.toString();
		}
		double minutes = (time / (1000.0 * 60.0));
		if (minutes < 60) {
			StringBuilder b = new StringBuilder();
			b.append(FormatUtils.formattedFromDouble(minutes, decimals, getSession().getLocale()));
			b.append(" ");
			b.append(getSession().getString("tokenMinutes").toLowerCase());
			return b.toString();
		}
		double hours = (time / (1000.0 * 60.0 * 60.0));
		StringBuilder b = new StringBuilder();
		b.append(FormatUtils.formattedFromDouble(hours, decimals, getSession().getLocale()));
		b.append(" ");
		b.append(getSession().getString("tokenHours").toLowerCase());
		return b.toString();
	}

	/**
	 * RunTickers the error description.
	 */
	private void showErrorDescription() {

		// Text area for the detailed description.
		JTextArea textArea = new JTextArea();
		textArea.setColumns(60);
		textArea.setText(errorDescription);
		textArea.setEditable(false);

		// Scroll panel to scroll the text.
		JScrollPane scrollPane = new JScrollPane(textArea);

		// Option pane to show it.
		JOptionPane optionPane = new JOptionPane();
		optionPane.setLocale(getSession().getLocale());
		optionPane.setMessage(scrollPane);
		optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
		String option = getSession().getString("tokenClose");
		optionPane.setOptions(new String[] { option });

		// The dialog to show the pane.
		StringBuilder title = new StringBuilder();
		if (getLabelTask().getText() != null && !getLabelTask().getText().isEmpty()) {
			title.append(getLabelTask().getText());
			title.append(" - ");
			title.append(getSession().getString("tokenError").toLowerCase());
			title.append(" ");
			title.append(getSession().getString("tokenDetail").toLowerCase());
		} else {
			title.append(getSession().getString("tokenError"));
			title.append(" ");
			title.append(getSession().getString("tokenDetail").toLowerCase());
		}
		JDialog dialog = optionPane.createDialog(title.toString());
		dialog.setResizable(true);
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		SwingUtils.setMnemonics(SwingUtils.getAllButtons(dialog));
		SwingUtils.installAcceleratorKeyListener(dialog);
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Updates the status line with Cancelled
	 */
	private void updateStatusCancelled() {
		getLabelStatus().setText(getSession().getString("panelProgressCancelled"));
	}

	/**
	 * Updates the error line with cancelled by user info.
	 * 
	 * @param task The task.
	 */
	private void updateErrorCancelledByUser(Task task) {
		String textError = getSession().getString("panelProgressCancelledByUserRequest");
		getLabelError().setText(textError);

		// Store small detailed error description.
		StringBuilder b = new StringBuilder();
		b.append(getSession().getString("tokenTask"));
		b.append(": ");
		b.append(task);
		b.append("\n\n");
		b.append(getSession().getString("panelProgressHasBeenCancelledByUserRequest"));
		errorDescription = b.toString();
		getButtonErrorInfo().setEnabled(true);
	}

	/**
	 * Updates the status line with Counting...
	 */
	private void updateStatusCounting() {
		getLabelStatus().setText(getSession().getString("panelProgressCounting") + "...");
		getProgressBar().setIndeterminate(true);
	}

	/**
	 * Updates the status error.
	 * 
	 * @param task The task.
	 */
	private void updateStatusError(Task task) {
		if (task.isException()) {
			// All buttons disabled except the error info button.
			getButtonPauseResume().setEnabled(false);
			getButtonCancel().setEnabled(false);
			// Trace the short description.
			getLabelError().setText(task.getException().getMessage());
			// Store the detailed error description (stack trace).
			errorDescription = StringUtils.getStackTrace(task.getException());
			getButtonErrorInfo().setEnabled(true);

			// If is indeterminate, update status with error.
			if (task.isIndeterminate()) {
				getLabelStatus().setText(getSession().getString("panelProgressError"));
			}

			// Log the exception
			LOGGER.catching(task.getException());
		}
	}

	/**
	 * Updates the status line with Paused
	 */
	private void updateStatusPaused() {
		getLabelStatus().setText(getSession().getString("panelProgressPaused"));
	}

	/**
	 * Updates the status line with Processing...
	 */
	private void updateStatusProcessing() {
		getLabelStatus().setText(getSession().getString("panelProgressProcessing"));
	}

	/**
	 * Updates the status and time label with the processing info.
	 * 
	 * @param performed The performed percentage.
	 */
	private void updateStatusAndTimeProcessing(double performed) {
		StringBuilder b = new StringBuilder();
		b.append(getSession().getString("panelProgressProcessing"));
		b.append(" ");
		b.append(NumberUtils.getBigDecimal(performed, progressDecimals));
		b.append(" %");
		getLabelStatus().setText(b.toString());
		getLabelTime().setText(getProgressTimeInfo(performed));
	}

	/**
	 * Updates the status and time label with the processed info.
	 * 
	 * @param performed The performed percentage.
	 */
	private void updateStatusProcessed(double performed) {
		StringBuilder b = new StringBuilder();
		b.append(getSession().getString("panelProgressProcessed"));
		b.append(" ");
		b.append(NumberUtils.getBigDecimal(performed, progressDecimals));
		b.append("%");
		getLabelStatus().setText(b.toString());
	}

	/**
	 * Update the tume label with the time elapsed.
	 */
	private void updateTimeElapsed() {
		getLabelTime().setText(getProgressTimeElapsed());
	}

	/**
	 * Updates the progress bar with the proper progress information.
	 * 
	 * @param step The current step.
	 * @param steps The total number of steps.
	 */
	private void updateProgressBar(long step, long steps) {
		if (steps <= 0) {
			return;
		}
		// Register the number of process steps.
		processSteps = steps;
		// Set the progress bar minimum and maximum values to 0 and 100.
		if (getProgressBar().getMaximum() <= 0) {
			getProgressBar().setMinimum(0);
			getProgressBar().setMaximum(100);
			getProgressBar().setValue(0);
		}
		// Percentage performed.
		double performed = (Double.valueOf(step) * 100.0 / Double.valueOf(processSteps));
		// Register last preformed.
		lastPerformed = performed;
		// Calculate current progress value. Steps start at 0 and end at stepCout-1.
		getProgressBar().setValue(Double.valueOf(NumberUtils.round(performed, 0)).intValue());
	}

	/**
	 * Returns the popup menu.
	 * 
	 * @return The popup menu.
	 */
	private JPopupMenu getPopupMenu() {

		// Sorted actions.
		List<Action> itemActions = actions.getActions();

		// Popup menu.
		JPopupMenu popupMenu = new JPopupMenu();

		// Add items.
		for (int i = 0; i < itemActions.size(); i++) {
			Action action = itemActions.get(i);
			popupMenu.add(getMenuItem(actions.getButton(action)));
		}

		return popupMenu;
	}

	/**
	 * Returns the menu item.
	 * 
	 * @param button The source button.
	 * @return The menu iem.
	 */
	private JMenuItem getMenuItem(JButton button) {
		JMenuItem menuItem = new JMenuItem();
		Action action = button.getAction();
		menuItem.addActionListener(action);
		menuItem.setIcon(button.getIcon());
		menuItem.setText(ActionUtils.getMenuItemSourceText(action));
		menuItem.setToolTipText(button.getToolTipText());
		menuItem.setVisible(button.isVisible());
		menuItem.setEnabled(button.isEnabled());
		menuItem.setBackground(Color.WHITE);
		return menuItem;
	}

	/**
	 * Return the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the panel progress width.
	 * 
	 * @return The panel progress width.
	 */
	public int getPanelProgressWidth() {
		return panelProgressWidth;
	}

	/**
	 * Return the progress decimals.
	 * @return The progress decimals.
	 */
	public int getProgressDecimals() {
		return progressDecimals;
	}

	/**
	 * Set the progress decimals.
	 * @param progressDecimals The progress decimals.
	 */
	public void setProgressDecimals(int progressDecimals) {
		if (progressDecimals < 0) {
			throw new IllegalArgumentException();
		}
		this.progressDecimals = progressDecimals;
	}

	/**
	 * Sets the panel progress width.
	 * 
	 * @param panelProgressWidth The panel progress width.
	 */
	public void setPanelProgressWidth(int panelProgressWidth) {
		this.panelProgressWidth = panelProgressWidth;
	}

}
