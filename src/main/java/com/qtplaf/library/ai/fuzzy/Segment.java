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

package com.qtplaf.library.ai.fuzzy;

/**
 * A segment of a fuzzy controller. The segment has a label, a minimum, a maximum and a sign. Segment in a list can
 * overlap.
 * <p>
 * The sign can be -1, 0 or 1. A sign of 0 indicates the medium segment. Only a medium segment can exists in a list of
 * segments of a fuzzy control.
 * <p>
 * For a segment with sign 1, the minimum value yield 0 factor and the maximum value yields 1 factor.
 * <p>
 * For a segment with sigh -1, the minimum value yields 1 factor and the maximum value yields 0 factor.
 * <p>
 * For the medium segment with sign 0, a value of (maximumn-minimum)/2 yields 1 factor, while minimum and maximum yield
 * 0.
 * <p>
 * The result of the factor calculation is retrieved through a function.
 *
 * @author Miquel Sas
 */
public class Segment {

	/** Label. */
	private String label;
	/** Maximum. */
	private double maximum;
	/** Minimum. */
	private double minimum;
	/** Sign. */
	private int sign;
	/** Function. */
	private Function function;

	/**
	 * Constructor.
	 * 
	 * @param label The label.
	 * @param maximum The maximum.
	 * @param minimum The minimum.
	 * @param sign The sign.
	 * @param function The control function.
	 */
	public Segment(String label, double maximum, double minimum, int sign, Function function) {
		super();
		if (minimum >= maximum) {
			throw new IllegalArgumentException();
		}
		if (sign != -1 && sign != 0 && sign != 1) {
			throw new IllegalArgumentException();
		}
		this.label = label;
		this.maximum = maximum;
		this.minimum = minimum;
		this.sign = sign;
		this.function = function;
	}

	/**
	 * Returns the label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the minimum.
	 * 
	 * @return The minimum.
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * Returns the maximum.
	 * 
	 * @return The maximum.
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Returns the sign.
	 * 
	 * @return The sign.
	 */
	public int getSign() {
		return sign;
	}

	/**
	 * Returns the factor function.
	 * 
	 * @return The factor function.
	 */
	public Function getFunction() {
		return function;
	}

	/**
	 * Returns the factor (0 to 1) of the argument value in the range of the segment.
	 * 
	 * @param value The value to check.
	 * @return The factor.
	 */
	public double getFactor(double value) {
		return getFunction().getFactor(value, maximum, minimum, sign);
	}

	/**
	 * Check if the argument value is in the minimum/maximum range.
	 * 
	 * @param value The value.
	 * @return A boolean.
	 */
	public boolean inRange(double value) {
		return minimum <= value && value <= maximum;
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(label);
		b.append(", ");
		b.append(maximum);
		b.append(", ");
		b.append(minimum);
		b.append(", ");
		b.append(sign);
		return b.toString();
	}

}
