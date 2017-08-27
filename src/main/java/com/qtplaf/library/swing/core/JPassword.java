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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPasswordField;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;

/**
 * A password field implementation.
 * 
 * @author Miquel Sas
 */
public class JPassword extends JPasswordField implements EditField, FocusListener {

	/**
	 * The selector that will select all invoked later.
	 */
	class SelectAll implements Runnable {
		/**
		 * Run it: select all the text in the field.
		 */
		@Override
		public void run() {
			selectAll();
		}
	}

	/**
	 * The selector that will select nothing invoked later.
	 */
	class SelectNothing implements Runnable {
		/**
		 * Run it: select nothing in the field.
		 */
		@Override
		public void run() {
			select(0, 0);
		}
	}

	/**
	 * The associated edit context.
	 */
	private EditContext editContext;
	/**
	 * Current value. It can be a single value or an array of values.
	 */
	private Value currentValue = null;
	/**
	 * Previous value to control when to fire value actions.
	 */
	private Value previousValue = null;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JPassword(EditContext editContext) {
		super();
	}

	/**
	 * Returns the associated edit context, that normally should have been set inn the constructor of the underlying
	 * component.
	 * 
	 * @return The edit context.
	 */
	@Override
	public EditContext getEditContext() {
		return editContext;
	}

	/**
	 * Returns the component (this).
	 * 
	 * @return The {@link Component}.
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Clear the control with its default data.
	 */
	@Override
	public void clear() {
		setValue(getEditContext().getField().getDefaultValue());
	}

	/**
	 * Get the value from the component, not the record.
	 * 
	 * @return The value.
	 * @throws IllegalArgumentException If such an error occurs.
	 */
	@Override
	public Value getValue() throws IllegalArgumentException {
		return new Value(new String(getPassword()));
	}

	/**
	 * Update the value in the component, not the record.
	 * 
	 * @param value The value to set.
	 */
	@Override
	public void setValue(Value value) {
		setValue(value, true);
	}

	/**
	 * Update the value in the component, not the record, controlling whether value actions should be fired.
	 * 
	 * @param value The value to set.
	 * @param fireValueActions A boolean stating if value actions should be fired.
	 */
	@Override
	public void setValue(Value value, boolean fireValueActions) {
		previousValue = getValue();
		currentValue = new Value(value);
		if (fireValueActions) {
			getEditContext().fireValueActions(this, previousValue, currentValue);
		}
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getEditContext().getSession();
	}

	/**
	 * When focus gained select the entire text.
	 */
	@Override
	public void focusGained(FocusEvent e) {
		EventQueue.invokeLater(new SelectAll());
	}

	/**
	 * Normally, when focus lost, one would expect to validate the field and launch actions related to value changes.
	 */
	@Override
	public void focusLost(FocusEvent e) {
		EventQueue.invokeLater(new SelectNothing());
	}

	/**
	 * Returns a string representation of this edit field.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		return SwingUtils.toString(this);
	}
}
