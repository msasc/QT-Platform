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

import java.util.ArrayList;

import com.qtplaf.library.util.list.ListUtils;

/**
 * A non fixed size value array that is comparable.
 *
 * @author Miquel Sas
 */
public class ValueArray extends ArrayList<Value> implements Comparable<Object> {

	/**
	 * Default constructor.
	 */
	public ValueArray() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param valueArray The value array to copy.
	 */
	public ValueArray(ValueArray valueArray) {
		super(valueArray.size());
		addAll(valueArray);
	}

	/**
	 * Construct a list with an initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 */
	public ValueArray(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Add an array of values.
	 *
	 * @param values The array of values to add.
	 * @return A boolean if this list changed as a result of adding the array of values.
	 */
	public boolean addAll(Value[] values) {
		if (values == null || values.length == 0) {
			return false;
		}
		addAll(ListUtils.asList(values));
		return true;
	}

	/**
	 * Returns a safe copy of this ValueArray.
	 *
	 * @return The ByteArray copy.
	 */
	public ValueArray copy() {
		ValueArray valueArray = new ValueArray(size());
		valueArray.addAll(this);
		return valueArray;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this list is less than, equal to, or greater than the
	 * specified argument list. Throws an UnsupportedOperationException if the argument is not an
	 *
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(Object o) {
		return ListUtils.compareTo(this, o);
	}
}
