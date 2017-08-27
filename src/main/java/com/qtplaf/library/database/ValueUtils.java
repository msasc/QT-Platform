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

package com.qtplaf.library.database;

import java.util.Collection;

/**
 * Static value utilities.
 * 
 * @author Miquel Sas
 */
public class ValueUtils {

	/**
	 * Check if a list of values are empty.
	 *
	 * @param values The list of values
	 * @return A boolean
	 */
	public static boolean areEmpty(Value... values) {
		boolean empty = true;
		for (Value value : values) {
			if (!value.isEmpty()) {
				empty = false;
				break;
			}
		}
		return empty;
	}

	/**
	 * Check if a list of values are full.
	 *
	 * @param values The list of values
	 * @return A boolean
	 */
	public static boolean areFull(Value... values) {
		boolean full = true;
		for (Value value : values) {
			if (value.isEmpty()) {
				full = false;
				break;
			}
		}
		return full;
	}

	/**
	 * Create a copy of a list of values.
	 *
	 * @param values The list of values to copy.
	 * @return The copy of the list.
	 */
	public static Value[] copy(Value[] values) {
		Value[] newValues = new Value[values.length];
		for (int i = 0; i < newValues.length; i++) {
			newValues[i] = new Value(values[i]);
		}
		return newValues;
	}

	/**
	 * Returns true if this value is in the list, false otherwise.
	 *
	 * @param value The value to check.
	 * @param values The list of values to check.
	 * @return True if this value is in the list.
	 */
	public static boolean in(Value value, Collection<Value> values) {
		return ValueUtils.indexOf(value, values) >= 0;
	}

	/**
	 * Returns true if this value is in the list, false otherwise.
	 *
	 * @param value The value to check.
	 * @param values The list of values to check.
	 * @return True if this value is in the list.
	 */
	public static boolean in(Value value, Value... values) {
		return ValueUtils.indexOf(value, values) >= 0;
	}

	/**
	 * Returns the index of this value in a list of values, or -1 if this value is not in the list.
	 *
	 * @param value The value to check.
	 * @param values The list of values.
	 * @return The index of this value in the list or -1.
	 */
	public static int indexOf(Value value, Collection<Value> values) {
		return ValueUtils.indexOf(value, values.toArray(new Value[values.size()]));
	}

	/**
	 * Returns the index of this value in a list of values, or -1 if this value is not in the list.
	 *
	 * @param value The value to check.
	 * @param values The list of values.
	 * @return The index of this value in the list or -1.
	 */
	public static int indexOf(Value value, Value... values) {
		for (int i = 0; i < values.length; i++) {
			if (value.equals(values[i])) {
				return i;
			}
		}
		return -1;
	}

}
