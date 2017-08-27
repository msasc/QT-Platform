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

package com.qtplaf.platform.statistics;

/**
 * Defines a smoothed average used as a movement descriptor.
 * 
 * @author Miquel Sas
 */
public class Average {

	public enum Type {
		SMA,
		WMA
	}

	/** Average period. */
	private int period;
	/** Smoothing periods. */
	private int[] smooths;
	/** Average type. */
	private Type type = Type.SMA;

	/**
	 * Constructor.
	 * 
	 * @param period Average period.
	 * @param smooths Smoothing periods.
	 */
	public Average(int period, int... smooths) {
		super();
		this.period = period;
		this.smooths = smooths;
	}

	/**
	 * Constructor.
	 * 
	 * @param type The average type.
	 * @param period Average period.
	 * @param smooths Smoothing periods.
	 */
	public Average(Type type, int period, int... smooths) {
		super();
		this.type = type;
		this.period = period;
		this.smooths = smooths;
	}

	/**
	 * Returns the SMA period.
	 * 
	 * @return The SMA period.
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Returns the smoothing periods.
	 * 
	 * @return The smoothing periods.
	 */
	public int[] getSmooths() {
		return smooths;
	}

	/**
	 * Returns the type.
	 * 
	 * @return The type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type.
	 * 
	 * @param type The average type.
	 */
	public void setType(Type type) {
		this.type = type;
	}
}