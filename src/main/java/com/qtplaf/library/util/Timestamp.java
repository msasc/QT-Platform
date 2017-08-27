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
package com.qtplaf.library.util;

/**
 * A <i>Timestamp</i> that conforms with the contract of hash code. Note that the superclass
 * <i>java.sql.Timestamp</i> does not. It's useful to have values used directly as keys in hash maps.
 *
 * @author Miquel Sas
 */
public class Timestamp extends java.sql.Timestamp {

	/**
	 * Default constructor.
	 */
	public Timestamp() {
		super(System.currentTimeMillis());
	}

	/**
	 * Creates a new instance of Timestamp.
	 *
	 * @param time The time in milliseconds
	 */
	public Timestamp(long time) {
		super(time);
	}

	/**
	 * Copy constructor.
	 *
	 * @param time The timestamp
	 */
	public Timestamp(java.sql.Timestamp time) {
		super(time.getTime());
	}

	/**
	 * Returns the hash code for this value.
	 * 
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		long ht = getTime() + getNanos() / 1000000;
		return (int) ht ^ (int) (ht >> 32);
	}

	/**
	 * Compares this timestamp with the argument timestamp. Returns 0 if they are equal, -1 if this value is less than
	 * the argument, and 1 if it is greater.
	 * 
	 * @return An <i>int</i>.
	 * @param t The <i>Timestamp</i> to compare with.
	 */
	public int compareTo(Timestamp t) {
		int compare = super.compareTo(t);
		if (compare == 0) {
			int n1 = getNanos();
			int n2 = t.getNanos();
			compare = (n1 == n2 ? 0 : (n1 < n2 ? -1 : 1));
		}
		return compare;
	}

	/**
	 * Check if the argument object is equal to this timestamp.
	 *
	 * @param obj The object to check
	 * @return A boolean indicating if the timestamp is equals to this timestamp.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Timestamp other = (Timestamp) obj;
		return other.getTime() == getTime() && other.getNanos() == getNanos();
	}
}
