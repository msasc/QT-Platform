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

package com.qtplaf.library.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JConsole;
import com.qtplaf.library.swing.core.JFrameSession;
import com.qtplaf.library.swing.core.JPanelButtons;
import com.qtplaf.library.swing.core.JPanelStatus;
import com.qtplaf.library.swing.core.JPanelTreeMenu;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.event.WindowHandler;
import com.qtplaf.library.util.Alignment;

/**
 * The frame that handles a tree menu and a console.
 * 
 * @author Miquel Sas
 */
public class FrameMenu {

	/**
	 * Execute action.
	 */
	class ActionExecute extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionExecute() {
			super();
			ActionUtils.configureExecute(getSession(), this);
		}

		/**
		 * Perform the action, execute the selected menu option if applicable.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			getPanelTreeMenu().processExecute();
		}

	}

	/**
	 * Exit action.
	 */
	class ActionExit extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionExit() {
			super();
			ActionUtils.configureExit(getSession(), this);
		}

		/**
		 * Perform the action, exit the application.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = getSession().getString("frameMenuExitApplication");
			if (MessageBox.question(getSession(), message, MessageBox.YES_NO, MessageBox.YES) == MessageBox.YES) {
				if (getPreExitAction() != null) {
					getPreExitAction().actionPerformed(e);
				}
				System.exit(0);
			}
		}
	}

	/**
	 * Clear the console.
	 */
	class ActionClear extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionClear() {
			super();
			ActionUtils.configureClear(getSession(), this);
		}

		/**
		 * Perform the action, clear the console.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			getConsole().clear();
		}
	}

	/**
	 * Change listener to listen to tab selection.
	 */
	class TabChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			setupPanelButtons();
		}
	}

	/**
	 * Window adapter to handle closing.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			getActionExit().actionPerformed(new ActionEvent(e.getSource(), 0, "EXIT APP"));
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
			setupPanelButtons();
		}
	}

	/**
	 * Tree menu panel.
	 */
	private JPanelTreeMenu panelMenu;
	/**
	 * The console for <tt>out</tt> and <tt>err</tt>.
	 */
	private JConsole console;
	/**
	 * The buttons panel.
	 */
	private JPanelButtons panelButtons;
	/**
	 * The tabbed pane.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * The status panel.
	 */
	private JPanelStatus panelStatus;

	/**
	 * Action execute.
	 */
	private ActionExecute actionExecute;
	/**
	 * Action exit.
	 */
	private ActionExit actionExit;
	/**
	 * Action clear.
	 */
	private ActionClear actionClear;
	/**
	 * An optional action to be executed prior to exit the application.
	 */
	private Action preExitAction;
	/**
	 * Internal frame.
	 */
	private JFrameSession frame;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public FrameMenu(Session session) {
		super();
		frame = new JFrameSession(session);
		layoutComponents();
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return frame.getSession();
	}

	/**
	 * Layout components.
	 */
	private void layoutComponents() {

		// Ensure the panel buttons has been built.
		getPanelButtons();

		// Configure tabbed pane.
		getTabbedPane().removeAll();

		// Menu pane.
		String menuTitle = getSession().getString("frameMenuTabMenuLabel");
		String menuTooltip = getSession().getString("frameMenuTabMenuTooltip");
		getTabbedPane().addTab(menuTitle, null, getPanelTreeMenu(), menuTooltip);

		// Console pane.
		String consoleTitle = getSession().getString("frameMenuTabConsoleLabel");
		String consoleTooltip = getSession().getString("frameMenuTabConsoleTooltip");
		getTabbedPane().addTab(consoleTitle, null, new JScrollPane(getConsole()), consoleTooltip);

		// Set the content pane.
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = null;

		// Tabbed pane.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		frame.getContentPane().add(getTabbedPane(), constraints);

		// Status panel.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 10, 2, 10);
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		frame.getContentPane().add(getPanelStatus(), constraints);

		// Buttons panel.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 10, 0, 10);
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 2;
		frame.getContentPane().add(getPanelButtons(), constraints);

		// This window listener.
		WindowAdapter windowAdapter = new WindowAdapter();
		frame.addWindowListener(windowAdapter);
		frame.addWindowFocusListener(windowAdapter);

		// Setup panel buttons.
		setupPanelButtons();

		// Clear status.
		clearStatus();
	}

	/**
	 * Returns the console component (JScrollPane) installed in the tabbed pane.
	 * 
	 * @return The console component.
	 */
	private Component getConsoleComponent() {
		return getConsole().getParent().getParent();
	}

	/**
	 * Setup the buttons panel depending on the seletected tab.
	 */
	private void setupPanelButtons() {
		int index = getTabbedPane().getSelectedIndex();

		// Selected tab is menu: actions execute and exit.
		if (index == getTabbedPane().indexOfComponent(getPanelTreeMenu())) {
			ActionUtils.getButton(getActionExecute()).setVisible(true);
			ActionUtils.getButton(getActionClear()).setVisible(false);
			ActionUtils.getButton(getActionExit()).setVisible(true);
		}

		// Selected tab is console: actions clear and exit.
		if (getConsole().getParent() != null) {
			if (index == getTabbedPane().indexOfComponent(getConsoleComponent())) {
				ActionUtils.getButton(getActionExecute()).setVisible(false);
				ActionUtils.getButton(getActionClear()).setVisible(true);
				ActionUtils.getButton(getActionExit()).setVisible(true);
			}
		}

		// Setup accelerator key listeners.
		SwingUtils.removeAcceleratorKeyListener(frame);
		SwingUtils.installAcceleratorKeyListener(frame);
	}

	/**
	 * Returns the tree menu panel.
	 * 
	 * @return The tree menu panel.
	 */
	public JPanelTreeMenu getPanelTreeMenu() {
		if (panelMenu == null) {
			panelMenu = new JPanelTreeMenu(getSession());
			panelMenu.setStatusBar(getPanelStatus());
			panelMenu.setProcessExecute(false);
		}
		return panelMenu;
	}

	/**
	 * Returns the console for <tt>out</tt> and <tt>err</tt>.
	 * 
	 * @return The console.
	 */
	public JConsole getConsole() {
		if (console == null) {
			console = new JConsole();
		}
		return console;
	}

	/**
	 * Returns the buttons panel.
	 * 
	 * @return The buttons panel.
	 */
	protected JPanelButtons getPanelButtons() {
		if (panelButtons == null) {
			panelButtons = new JPanelButtons(Alignment.Right);
			panelButtons.add(getActionExecute());
			panelButtons.add(getActionClear());
			panelButtons.add(getActionExit());
		}
		return panelButtons;
	}

	/**
	 * Returns the tabbed pane.
	 * 
	 * @return The tabbed pane.
	 */
	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addChangeListener(new TabChangeListener());
		}
		return tabbedPane;
	}

	/**
	 * Returns the status panel.
	 * 
	 * @return The status panel.
	 */
	protected JPanelStatus getPanelStatus() {
		if (panelStatus == null) {
			panelStatus = new JPanelStatus();
		}
		return panelStatus;
	}

	/**
	 * Returns the execute action.
	 * 
	 * @return The execute action.
	 */
	private ActionExecute getActionExecute() {
		if (actionExecute == null) {
			actionExecute = new ActionExecute();
		}
		return actionExecute;
	}

	/**
	 * Returns the exit action.
	 * 
	 * @return The exit action.
	 */
	private ActionExit getActionExit() {
		if (actionExit == null) {
			actionExit = new ActionExit();
		}
		return actionExit;
	}

	/**
	 * Returns the clear action.
	 * 
	 * @return The clear action.
	 */
	private ActionClear getActionClear() {
		if (actionClear == null) {
			actionClear = new ActionClear();
		}
		return actionClear;
	}

	/**
	 * Returns the pre-exit action.
	 * 
	 * @return The pre-exit action.
	 */
	public Action getPreExitAction() {
		return preExitAction;
	}

	/**
	 * Sets the pre-exit action.
	 * 
	 * @param preExitAction The pre-exit action.
	 */
	public void setPreExitAction(Action preExitAction) {
		this.preExitAction = preExitAction;
	}

	/**
	 * RunTickers the console.
	 */
	public void showConsole() {
		getTabbedPane().setSelectedComponent(getConsoleComponent());
		setupPanelButtons();
	}

	/**
	 * RunTickers the tree menu.
	 */
	public void showTreeMenu() {
		getTabbedPane().setSelectedComponent(getPanelTreeMenu());
		setupPanelButtons();
	}

	/**
	 * Sets the size based on factor of the screen size.
	 * 
	 * @param widthFactor The width factor relative to the screen.
	 * @param heightFactor The height factor relative to the screen.
	 */
	public void setSize(double widthFactor, double heightFactor) {
		frame.setSize(SwingUtils.factorScreenDimension(frame, widthFactor, heightFactor));
	}

	/**
	 * Set the frame location.
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 */
	public void setLocation(int x, int y) {
		frame.setLocation(x, y);
	}

	/**
	 * Set the frame title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	/**
	 * Set the frame visible.
	 * 
	 * @param visible A boolean.
	 */
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	/**
	 * Set the status string showing only the label.
	 * 
	 * @param status The status text.
	 */
	public void setStatus(String status) {
		getPanelStatus().setStatus(status);
	}

	/**
	 * Set the status message showing the progress bar with the current and maximum values. Current values range from
	 * zero to maximum.
	 * 
	 * @param status The status text.
	 * @param value The current progress value.
	 * @param maximum The maximum value.
	 */
	public void setStatus(String status, int value, int maximum) {
		getPanelStatus().setStatus(status, value, maximum);
	}

	/**
	 * Set the status string with the progress bar indeterminate.
	 * 
	 * @param status The status text.
	 */
	public void setStatusIndeterminate(String status) {
		getPanelStatus().setStatusIndeterminate(status);
	}

	/**
	 * Clearthe status text.
	 */
	public void clearStatus() {
		getPanelStatus().clearStatus();
	}
}
