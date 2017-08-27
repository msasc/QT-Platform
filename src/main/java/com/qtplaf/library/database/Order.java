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
import java.util.Objects;

/**
 * An order definition.
 *
 * @author Miquel Sas
 */
public class Order extends ArrayList<Order.Segment> {

	/**
	 * An order segment is a small structure to pack order segment information.
	 */
	public static class Segment implements Comparable<Object> {

		/**
		 * The field.
		 */
		private Field field;
		/**
		 * The ascending flag.
		 */
		private boolean asc = true;

		/**
		 * Default constructor.
		 */
		public Segment() {
			super();
		}

		/**
		 * Constructor assigning field and asc.
		 *
		 * @param field The field
		 * @param asc The ascending flag
		 */
		public Segment(Field field, boolean asc) {
			super();
			if (field == null) {
				throw new NullPointerException();
			}
			this.field = field;
			this.asc = asc;
		}

		/**
		 * Get the field.
		 *
		 * @return The field.
		 */
		public Field getField() {
			return field;
		}

		/**
		 * Check the ascending flag.
		 *
		 * @return A <code>boolean</code>
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
		 * Set the field.
		 *
		 * @param field The field.
		 */
		public void setField(Field field) {
			if (field == null) {
				throw new NullPointerException();
			}
			this.field = field;
		}

		/**
		 * Returns the hash code for this order key.
		 *
		 * @return The hash code
		 */
		@Override
		public int hashCode() {
			int hash = 0;
			hash ^= field.hashCode();
			hash ^= Boolean.valueOf(asc).hashCode();
			return hash;
		}

		/**
		 * Check whether the argument object is equal to this order segment.
		 *
		 * @param obj The object to compare
		 * @return A boolean.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Segment other = (Segment) obj;
			if (!Objects.equals(this.field, other.field)) {
				return false;
			}
			return this.asc == other.asc;
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
			Segment orderSegment = null;
			try {
				orderSegment = (Segment) o;
			} catch (ClassCastException exc) {
				throw new UnsupportedOperationException("Not comparable type: "
					+ o.getClass().getName());
			}
			int compare = field.compareTo(orderSegment.field);
			if (compare != 0) {
				return compare * (asc ? 1 : -1);
			}
			return 0;
		}

		/**
		 * Returns a string representation of this segment.
		 *
		 * @return A string
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(128);
			if (field != null) {
				b.append(field.toString());
			} else {
				b.append("null");
			}
			b.append(" - ");
			if (asc) {
				b.append("ASC");
			} else {
				b.append("DESC");
			}
			return b.toString();
		}

	}

	/**
	 * Default constructor.
	 */
	public Order() {
		super();
	}

	/**
	 * Constructor assigning the initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 */
	public Order(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Add an ascending segment with the given field.
	 *
	 * @param field The field.
	 */
	public void add(Field field) {
		add(new Segment(field, true));
	}

	/**
	 * Add a segment defined by the field and the ascending flag.
	 *
	 * @param field The field
	 * @param asc The ascending flag
	 */
	public void add(Field field, boolean asc) {
		add(new Segment(field, asc));
	}

	/**
	 * Returns a boolean indicating whether this order contains the argument field.
	 * 
	 * @param field The field.
	 * @return A boolean.
	 */
	public boolean contains(Field field) {
		for (Segment segment : this) {
			if (segment.getField().equals(field)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the segment containing the argument field or null.
	 * 
	 * @param field The field.
	 * @return The segment containing the argument field or null.
	 */
	public Segment get(Field field) {
		for (Segment segment : this) {
			if (segment.getField().equals(field)) {
				return segment;
			}
		}
		return null;
	}

	/**
	 * Sets the segment containing the field, if any, ascending or descending.
	 * 
	 * @param field The field.
	 * @param asc Ascending/descending flag.
	 */
	public void set(Field field, boolean asc) {
		Segment segment = get(field);
		if (segment != null) {
			segment.setAsc(asc);
		}
	}

	/**
	 * Returns a string representation of this order.
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
