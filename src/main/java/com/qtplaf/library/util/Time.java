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
 * A time that masks out date and millisecond information, referring always to the same 0 day. Note that an SQL time
 * does not have millisecond info. Use a timestamp if millis are needed.
 *
 * @author Miquel Sas
 */
public class Time extends java.sql.Time {

	public static final long DAY = 24 * 60 * 60 * 1000;

	/**
	 * Default constructor.
	 */
	public Time() {
		this(System.currentTimeMillis());
	}

	/**
	 * Creates a new instance of Time.
	 *
	 * @param time The time in millis
	 */
	public Time(long time) {
		// super(((time/1000)*1000)-((time/day)*day)+(3600*1000));
		super(time);
	}

	/**
	 * Copy constructor.
	 *
	 * @param time The time
	 */
	public Time(java.sql.Time time) {
		this(time.getTime());
	}
}
