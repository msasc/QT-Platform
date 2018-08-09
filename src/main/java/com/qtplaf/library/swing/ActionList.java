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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

/**
 * Manages a list of unique actions.
 * 
 * @author Miquel Sas
 */
public class ActionList {

	/**
	 * The internal list.
	 */
	private List<Action> actions = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public ActionList() {
		super();
	}

	/**
	 * Add an action to the list.
	 * 
	 * @param action The action to add.
	 */
	public void add(Action action) {
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	/**
	 * Returns the size of the list of actions.
	 * 
	 * @return The size.
	 */
	public int size() {
		return actions.size();
	}

	/**
	 * Returns the action at the given index.
	 * 
	 * @param index The index.
	 * @return The action.
	 */
	public Action get(int index) {
		return actions.get(index);
	}

	/**
	 * Remove the action.
	 * 
	 * @param action The action to remove.
	 * @return A boolean indicating if the action was removed.
	 */
	public boolean remove(Action action) {
		return actions.remove(action);
	}

	/**
	 * Clear the list.
	 */
	public void clear() {
		actions.clear();
	}

	/**
	 * Remove the action of the given action class and return it or null if not found.
	 * 
	 * @param actionClass The action class.
	 * @return The removed action or null.
	 */
	public Action remove(Class<? extends Action> actionClass) {
		for (Action action : actions) {
			if (action.getClass() == actionClass) {
				if (remove(action)) {
					return action;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a copy of the list of actions.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {
		return new ArrayList<>(actions);
	}

	/**
	 * Returns the action of the given class or null.
	 * 
	 * @param actionClass The action class.
	 * @return The action or null.
	 */
	public Action getAction(Class<? extends Action> actionClass) {
		for (Action action : actions) {
			if (action.getClass() == actionClass) {
				return action;
			}
		}
		return null;
	}

	/**
	 * Returns the list of actions that should be visible in a popup menu, conveniently sorted.
	 * 
	 * @return The list of visible actions.
	 */
	public List<Action> getActionsVisibleInPopupMenu() {
		List<Action> visibleActions = new ArrayList<>();
		for (Action action : actions) {
			if (ActionUtils.isVisibleInPopupMenu(action)) {
				// Do not show in a popup menu actions with VK_ESCAPE acceleretor key.
				if (ActionUtils.getAcceleratorKey(action).equals(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))) {
					continue;
				}
				visibleActions.add(action);
			}
		}
		return visibleActions;
	}

	/**
	 * Returns the action with the escape accelerator key or null.
	 * 
	 * @return The escape action.
	 */
	public Action getActionEscape() {
		for (Action action : actions) {
			if (ActionUtils.isVisibleInPopupMenu(action)) {
				if (ActionUtils.getAcceleratorKey(action).equals(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))) {
					return action;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the list of actions that should be visible in a buttons panel, conveniently sorted.
	 * 
	 * @return The list of visible actions.
	 */
	public List<Action> getActionsVisibleInButtonsPanel() {
		List<Action> visibleActions = new ArrayList<>();
		for (Action action : actions) {
			if (ActionUtils.isVisibleInButtonsPanel(action)) {
				visibleActions.add(action);
			}
		}
		return visibleActions;
	}

	/**
	 * Returns the list of buttons that should be visible in a buttons panel, conveniently sorted.
	 * 
	 * @return The list of visible buttons.
	 */
	public List<JButton> getButtonsVisibleInButtonsPanel() {
		List<Action> visibleActions = getActionsVisibleInButtonsPanel();
		List<JButton> buttons = new ArrayList<>();
		for (Action action : visibleActions) {
			JButton button = ActionUtils.getButton(action);
			if (button == null) {
				button = createStandardButton(action);
			}
			buttons.add(button);
		}
		return buttons;
	}

	/**
	 * Returns the appropriate button for the action. If the button has not been set it creates one.
	 * 
	 * @param actionClass The action class.
	 * @return The button.
	 */
	public JButton getButton(Class<? extends Action> actionClass) {
		Action action = getAction(actionClass);
		if (action == null) {
			return null;
		}
		return getButton(action);
	}

	/**
	 * Returns the appropriate button for the action. If the button has not been set it creates one.
	 * 
	 * @param action The action.
	 * @return The button.
	 */
	public JButton getButton(Action action) {
		JButton button = ActionUtils.getButton(action);
		if (button == null) {
			button = createButton(action);
		}
		return button;
	}

	/**
	 * Returns the sorted list of actions.
	 * 
	 * @param actions The sorted list of actions.
	 * @return The source list of actions.
	 */
	public static List<Action> sort(List<Action> actions) {
		Action[] arr = actions.toArray(new Action[actions.size()]);
		Arrays.sort(arr, new ActionComparator());
		List<Action> sorted = new ArrayList<>();
		for (Action action : arr) {
			sorted.add(action);
		}
		return sorted;
	}

	/**
	 * Creates the button for the action, a standard one or a small icon button if the action has only the icon and not
	 * a name or a source name.
	 * 
	 * @param action The action.
	 * @return The appropriate button.
	 */
	private JButton createButton(Action action) {
		String name = ActionUtils.getName(action);
		String sourceName = ActionUtils.getSourceName(action);
		Icon icon = ActionUtils.getSmallIcon(action);
		if (name == null && sourceName == null && icon != null) {
			return createIconButton(action);
		}
		return createStandardButton(action);
	}

	/**
	 * Creates the button that handles the argument action, with the action correctly parameterized to perform as
	 * expected by this system behavior.
	 * 
	 * @param action The source action.
	 * @return The action button.
	 */
	private JButton createStandardButton(Action action) {

		ActionUtils.setActionName(action);
		String name = ActionUtils.getName(action);

		// Create the button.
		JButton button = ActionUtils.getButton(action);
		if (button == null) {
			button = new JButton(action);
		}
		if (name != null) {
			button.setMargin(new Insets(2, 4, 2, 4));
			button.setText(name);
		} else {
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setBackground(Color.WHITE);
		}

		ActionUtils.setButton(action, button);

		return button;
	}

	/**
	 * Creates a small icon button with only the icon.
	 * 
	 * @param action The source action.
	 * @return The button.
	 */
	private JButton createIconButton(Action action) {

		Icon icon = ActionUtils.getSmallIcon(action);
		if (icon == null) {
			throw new IllegalArgumentException("The action must have an icon.");
		}

		JButton button = new JButton(action);
		button.setText(null);
		button.setBackground(Color.WHITE);
		button.setIconTextGap(0);
		button.setMargin(new Insets(0, 0, 0, 0));
		Dimension size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		button.setPreferredSize(size);

		ActionUtils.setButton(action, button);

		return button;
	}
}
