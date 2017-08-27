/*
 * Copyright (C) 2014 SONY
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.list.ListUtils;

/**
 * An order key.
 * 
 * @author Miquel Sas
 */
public class OrderKey extends ArrayList<OrderKey.Segment> implements Comparable<Object> {

	/**
	 * An order key segment is a small structure to pack segment (value,asc/desc) information.
	 */
	public static class Segment implements Comparable<Object> {

		/**
		 * The ascending flag.
		 */
		private boolean asc = true;
		/**
		 * The value.
		 */
		private Value value = null;

		/**
		 * Constructor assigning value and ascending flag.
		 *
		 * @param value The field value.
		 * @param asc The ascending flag.
		 */
		public Segment(Value value, boolean asc) {
			super();
			if (value == null) {
				throw new NullPointerException();
			}
			this.value = value;
			this.asc = asc;
		}

		/**
		 * Compares this segment with the argument object. Returns 0 if they are equal, -1 if this value is less than
		 * the argument, and 1 if it is greater.
		 *
		 * @return An integer.
		 * @param o The Object to compare with.
		 */
		@Override
		public int compareTo(Object o) {
			Segment segment = null;
			try {
				segment = (Segment) o;
			} catch (ClassCastException exc) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			int compare = value.compareTo(segment.value);
			if (compare != 0) {
				return compare * (asc ? 1 : -1);
			}
			return 0;
		}

		/**
		 * Check whether another object is equal to this segment.
		 *
		 * @param o The object to check
		 * @return A boolean
		 */
		@Override
		public boolean equals(Object o) {
			return compareTo(o) == 0;
		}

		/**
		 * Returns the hash code for this segment.
		 *
		 * @return The hash code
		 */
		@Override
		public int hashCode() {
			int hash = 0;
			hash ^= value.hashCode();
			hash ^= Boolean.valueOf(asc).hashCode();
			return hash;
		}

		/**
		 * Get the value.
		 *
		 * @return The value.
		 */
		public Value getValue() {
			return value;
		}

		/**
		 * Check the ascending flag.
		 *
		 * @return A boolean
		 */
		public boolean isAsc() {
			return asc;
		}

		/**
		 * Set the ascending flag.
		 *
		 * @param asc The ascending flag.
		 */
		public void setAsc(boolean asc) {
			this.asc = asc;
		}

		/**
		 * Set the value.
		 *
		 * @param value The value.
		 */
		public void setValue(Value value) {
			if (value == null) {
				throw new NullPointerException();
			}
			this.value = value;
		}

		/**
		 * Returns a string representation of this segment.
		 *
		 * @return A string
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(128);
			if (value != null) {
				b.append(value.toString());
			} else {
				b.append("null");
			}
			b.append(", ");
			b.append(isAsc() ? "ASC" : "DESC");
			return b.toString();
		}

	}

	/**
	 * Default constructor.
	 */
	public OrderKey() {
		super();
	}

	/**
	 * Constructor assigning the initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 */
	public OrderKey(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor assigning a list of values in ascending order.
	 * 
	 * @param values The list of values.
	 */
	public OrderKey(Value... values) {
		this(ListUtils.asList(values));
	}

	/**
	 * Constructor assigning a list of values in ascending order.
	 * 
	 * @param values The list of values.
	 */
	public OrderKey(List<Value> values) {
		super();
		for (Value value : values) {
			add(value, true);
		}
	}

	/**
	 * Add a value segment to the segment list.
	 *
	 * @param value The value of the segment.
	 * @param asc The ascending/descending flag
	 */
	public void add(Value value, boolean asc) {
		add(new Segment(value, asc));
	}

	/**
	 * Compares this order key with the argument object. Returns 0 if they are equal, -1 if this value is less than the
	 * argument, and 1 if it is greater.
	 *
	 * @return An integer.
	 * @param o The Object to compare with.
	 */
	@Override
	public int compareTo(Object o) {
		return ListUtils.compareTo(this, o);
	}

	/**
	 * Returns a string representation of this index key.
	 *
	 * @return A string
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(256);
		for (int i = 0; i < size(); i++) {
			b.append(get(i).toString());
			if (i < size() - 1) {
				b.append("; ");
			}
		}
		return b.toString();
	}
}
