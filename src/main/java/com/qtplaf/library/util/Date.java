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
 * A date that masks out the time information. An SQL date should not have time info.
 *
 * @author Miquel Sas
 */
public class Date extends java.sql.Date {

	/**
	 * Number of millis in a day
	 */
	public static final long DAY = 24 * 60 * 60 * 1000;

	/**
	 * Default constructor..
	 */
	public Date() {
		this(System.currentTimeMillis());
	}

	/**
	 * Creates a new instance of Date.
	 *
	 * @param time milliseconds since January 1, 1970, 00:00:00 GMT not to exceed the milliseconds representation for
	 *        the year 8099. A negative number indicates the number of milliseconds before January 1, 1970, 00:00:00
	 *        GMT.
	 */
	public Date(long time) {
		super(time);
	}

	/**
	 * Copy constructor.
	 *
	 * @param date The date to be copied.
	 */
	public Date(java.sql.Date date) {
		this(date.getTime());
	}
}
