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
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;

/**
 * A check box field to use with boolean fields.
 * 
 * @author Miquel Sas
 */
public class JCheckBoxField extends JCheckBox implements EditField {

	/**
	 * The associated edit context.
	 */
	private EditContext editContext;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JCheckBoxField(EditContext editContext) {
		super();
		this.editContext = editContext;
		setMargin(new Insets(0, 0, 0, 0));
		setBorder(null);
		setHorizontalTextPosition(SwingConstants.RIGHT);
	}

	/**
	 * Returns the associated edit context.
	 * 
	 * @return The edit context.
	 */
	@Override
	public EditContext getEditContext() {
		return editContext;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return editContext.getSession();
	}

	/**
	 * Clear the control with its default data.
	 */
	@Override
	public void clear() {
		setSelected(editContext.getField().getDefaultValue().getBoolean());
	}

	/**
	 * Get the value from the component, not the record.
	 * 
	 * @return The value.
	 */
	@Override
	public Value getValue() {
		return new Value(isSelected());
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

	@Override
	public void setValue(Value value, boolean fireValueActions) {
		if (!value.isBoolean()) {
			
		}
		Value previousValue = getValue();
		setSelected(value.getBoolean());
		if (fireValueActions) {
			getEditContext().fireValueActions(this, previousValue, value);
		}
	}

	/**
	 * Returns the component that will process the events of this edit field.
	 * 
	 * @return The {@link Component}.
	 */
	@Override
	public Component getComponent() {
		return this;
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
