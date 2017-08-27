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

package com.qtplaf.library.swing.action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.core.SwingUtils;

/**
 * Action aimed to search values to refresh edit fields, normally executed as a value action.
 * 
 * @author Miquel Sas
 */
public abstract class ActionSearchAndRefresh extends AbstractAction {

	/**
	 * The list of names of key edit field controls to build the search key.
	 */
	private List<String> keyEditFieldNames = new ArrayList<>();
	/**
	 * The list of names of refresh edit field controls.
	 */
	private List<String> refreshEditFieldNames = new ArrayList<>();
	/**
	 * The list of names of clear edit field controls.
	 */
	private List<String> clearEditFieldNames = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public ActionSearchAndRefresh() {
		super();
	}

	/**
	 * Adds an edit field name to the list of key names.
	 * 
	 * @param name The component name.
	 */
	public void addKeyEditFieldName(String name) {
		keyEditFieldNames.add(name);
	}

	/**
	 * Adds an edit field name to the list of refresh names.
	 * 
	 * @param name The component name.
	 */
	public void addRefreshEditFieldName(String name) {
		refreshEditFieldNames.add(name);
	}

	/**
	 * Adds an edit field name to the list of clear names.
	 * 
	 * @param name The component name.
	 */
	public void addClearEditFieldName(String name) {
		clearEditFieldNames.add(name);
	}

	/**
	 * Executed as a value action when the value of tne source edit field control changes.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Check previous and current value.
		Value previousValue = ActionUtils.getPreviousValue(this);
		Value currentValue = ActionUtils.getCurrentValue(this);
		if (previousValue == null && currentValue == null) {
			return;
		}
		if (previousValue != null && currentValue != null && currentValue.equals(previousValue)) {
			return;
		}

		// Get the window ancestor and component map.
		Component source = (Component) e.getSource();
		Window topWindow = SwingUtils.getWindowAncestor(source);
		Map<String, Component> componentMap = SwingUtils.getComponentMap(topWindow);

		// Build the list of key edit fields.
		List<EditField> keyEditFields = new ArrayList<>();
		for (String name : keyEditFieldNames) {
			Component component = componentMap.get(name);
			if (component == null) {
				throw new IllegalStateException("Key component " + name + " not found");
			}
			if (!(component instanceof EditField)) {
				throw new IllegalStateException("Key component " + name + " is not an EditField");
			}
			keyEditFields.add((EditField) component);
		}

		// Build the list of refresh edit fields.
		List<EditField> refreshEditFields = new ArrayList<>();
		for (String name : refreshEditFieldNames) {
			Component component = componentMap.get(name);
			if (component == null) {
				throw new IllegalStateException("Refresh component " + name + " not found");
			}
			if (!(component instanceof EditField)) {
				throw new IllegalStateException("Refresh component " + name + " is not an EditField");
			}
			refreshEditFields.add((EditField) component);
		}

		// Get the list of values to apply to refresh edit fields.
		if (!refreshEditFields.isEmpty()) {
			List<Value> refreshValues = getRefreshValues(keyEditFields, refreshEditFields);
			if (!refreshValues.isEmpty()) {
				for (int i = 0; i < refreshEditFields.size(); i++) {
					if (refreshValues.size() <= i) {
						break;
					}
					refreshEditFields.get(i).setValue(refreshValues.get(i));
				}
			} else {
				// Clear refresh edit fields.
				for (int i = 0; i < refreshEditFields.size(); i++) {
					refreshEditFields.get(i).clear();
				}
			}
		}

		// Clear the clear edit fields.
		for (String name : clearEditFieldNames) {
			Component component = componentMap.get(name);
			if (component == null) {
				throw new IllegalStateException("Clear component " + name + " not found");
			}
			if (!EditField.class.isInstance(component)
				&&
				!JTextComponent.class.isInstance(component)
				&&
				!JLabel.class.isInstance(component)) {
				throw new IllegalStateException(
					"Clear components must be of types EditField, JTextComponent or JLabel");
			}
			if (JTextComponent.class.isInstance(component)) {
				((JTextComponent) component).setText("");
				continue;
			}
			if (JLabel.class.isInstance(component)) {
				((JLabel) component).setText("");
				continue;
			}
		}
	}

	/**
	 * Get by any means the list of refresh values, in the same order as the refresh edit fields.
	 * 
	 * @param keyEditFields The list of key edit fields.
	 * @param refreshEditFields The refresh of key edit fields.
	 * @return The list of refresh values.
	 */
	protected abstract List<Value> getRefreshValues(List<EditField> keyEditFields, List<EditField> refreshEditFields);
}
