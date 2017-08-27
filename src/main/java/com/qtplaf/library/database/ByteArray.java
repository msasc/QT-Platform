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
 * A non fixed size byte array that is comparable.
 *
 * @author Miquel Sas
 */
public class ByteArray extends ArrayList<Byte> implements Comparable<Object> {

	/**
	 * Default constructor.
	 */
	public ByteArray() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param byteArray The byta array to copy.
	 */
	public ByteArray(ByteArray byteArray) {
		super(byteArray.size());
		addAll(byteArray);
	}

	/**
	 * Construct a list with an initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 */
	public ByteArray(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Add an array of bytes.
	 *
	 * @param bytes The array of bytes to add.
	 * @return A boolean if this list changed as a result of adding the array of bytes.
	 */
	public boolean addAll(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return false;
		}
		for (byte b : bytes) {
			add(b);
		}
		return true;
	}

	/**
	 * Returns the internal data as an array of bytes.
	 *
	 * @return The array of bytes.
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[size()];
		for (int i = 0; i < size(); i++) {
			bytes[i] = get(i);
		}
		return bytes;
	}

	/**
	 * Returns a safe copy of this ByteArray.
	 *
	 * @return The ByteArray copy.
	 */
	public ByteArray copy() {
		ByteArray byteArray = new ByteArray(size());
		byteArray.addAll(this);
		return byteArray;
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
