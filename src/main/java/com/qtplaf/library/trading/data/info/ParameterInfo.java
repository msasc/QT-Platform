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
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Value;

/**
 * An input parameter information definition.
 * 
 * @author Miquel Sas
 */
public class ParameterInfo {

	/**
	 * A field that defines the properties of this parameter.
	 */
	private Field field;
	/**
	 * The list of values assigned.
	 */
	private List<Value> values = new ArrayList<>();
	/**
	 * The maximum number of accepted values, less equal zero for a variable list.
	 */
	private int maximumValues = 1;
	/**
	 * Working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public ParameterInfo(Session session) {
		super();
		this.session = session;
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
	 * Returns the field that defines the properties of this parameter.
	 * 
	 * @return The field that defines the properties of this parameter.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Sets the field that defines the properties of this parameter.
	 * 
	 * @param field The field that defines the properties of this parameter.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Returns the maximum number of values accepted.
	 * 
	 * @return The maximum number of values accepted.
	 */
	public int getMaximumValues() {
		return maximumValues;
	}

	/**
	 * Sets the maximum number of values accepted.
	 * 
	 * @param maximumValues The maximum number of values accepted.
	 */
	public void setMaximumValues(int maximumValues) {
		this.maximumValues = maximumValues;
	}

	/**
	 * Add a value to the list of values.
	 * 
	 * @param value The value.
	 */
	public void addValue(Value value) {
		if (maximumValues > 0 && values.size() == maximumValues) {
			throw new IllegalStateException("Maximum number of valuees reached: " + maximumValues);
		}
		check(value);
		values.add(value);
	}

	/**
	 * Clear the list of values.
	 */
	public void clearValues() {
		values.clear();
	}

	/**
	 * Returns the size or current number of values.
	 * 
	 * @return The size or current number of values.
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Returns the value at the given index.
	 * 
	 * @param index The index.
	 * @return The value at the given index.
	 */
	public Value getValue(int index) {
		return values.get(index);
	}

	/**
	 * Returns the first assigned value.
	 * 
	 * @return The first assigned value.
	 */
	public Value getValue() {
		if (!values.isEmpty()) {
			return values.get(0);
		}
		return null;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value The value.
	 */
	public void setValue(Value value) {
		check(value);
		if (values.isEmpty()) {
			values.add(value);
		} else {
			values.set(0, value);
		}
	}

	/**
	 * Sets the value at the given index.
	 * 
	 * @param index The index.
	 * @param value The value.
	 */
	public void setValue(int index, Value value) {
		check(value);
		values.set(index, value);
	}

	/**
	 * Check whether this parameter info is equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParameterInfo) {
			ParameterInfo pi = (ParameterInfo) obj;
			if (!getField().equals(pi.getField())) {
				return false;
			}
			if (size() != pi.size()) {
				return false;
			}
			for (int i = 0; i < size(); i++) {
				if (!getValue(i).equals(pi.getValue(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Check if the value is acceptable.
	 * 
	 * @param value The value to check.
	 */
	private void check(Value value) {
		if (field == null) {
			throw new IllegalArgumentException("The field must be set.");
		}
		if (!field.validate(value)) {
			throw new IllegalArgumentException(field.getValidationMessage(session, value));
		}
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (field != null) {
			b.append(field.getName());
		}
		if (!values.isEmpty()) {
			b.append(" ");
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append(values.get(i));
			}
		}
		return b.toString();
	}

}
