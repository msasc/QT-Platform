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

package com.qtplaf.library.ai.function.normalize;

import com.qtplaf.library.ai.function.Normalizer;

/**
 * Basic normalizer.
 *
 * @author Miquel Sas
 */
public class StdNormalizer implements Normalizer {

	/** Data high. */
	private double dataHigh;
	/** Data low. */
	private double dataLow;
	/** Normalized high. */
	private double normalizedHigh;
	/** Normalized low. */
	private double normalizedLow;

	/**
	 * Default constructor.
	 */
	public StdNormalizer() {
		super();
	}

	/**
	 * Constructor setting the normalized high and low as the data high and low..
	 * 
	 * @param dataHigh Data high.
	 * @param dataLow Data low.
	 */
	public StdNormalizer(double dataHigh, double dataLow) {
		super();
		this.dataHigh = dataHigh;
		this.dataLow = dataLow;
		this.normalizedHigh = dataHigh;
		this.normalizedLow = dataLow;
	}

	/**
	 * Constructor.
	 * 
	 * @param dataHigh Data high.
	 * @param dataLow Data low.
	 * @param normalizedHigh Normalized high.
	 * @param normalizedLow Normalized low.
	 */
	public StdNormalizer(double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
		super();
		this.dataHigh = dataHigh;
		this.dataLow = dataLow;
		this.normalizedHigh = normalizedHigh;
		this.normalizedLow = normalizedLow;
	}

	/**
	 * Return the data high.
	 * 
	 * @return The data high.
	 */
	public final double getDataHigh() {
		return dataHigh;
	}

	/**
	 * Set the data high.
	 * 
	 * @param dataHigh The data high.
	 */
	public void setDataHigh(double dataHigh) {
		this.dataHigh = dataHigh;
	}

	/**
	 * Return the data low.
	 * 
	 * @return The data low.
	 */
	public final double getDataLow() {
		return dataLow;
	}

	/**
	 * Set the data low.
	 * 
	 * @param dataLow The data low.
	 */
	public void setDataLow(double dataLow) {
		this.dataLow = dataLow;
	}

	/**
	 * Return the normalized high.
	 * 
	 * @return The normalized high.
	 */
	public final double getNormalizedHigh() {
		return normalizedHigh;
	}

	/**
	 * Set the normalized high.
	 * 
	 * @param normalizedHigh The normalized high.
	 */
	public void setNormalizedHigh(double normalizedHigh) {
		this.normalizedHigh = normalizedHigh;
	}

	/**
	 * Return the normalized low.
	 * 
	 * @return The normalized low.
	 */
	public final double getNormalizedLow() {
		return normalizedLow;
	}

	/**
	 * Set the normalized low.
	 * 
	 * @param normalizedLow The normalized low.
	 */
	public void setNormalizedLow(double normalizedLow) {
		this.normalizedLow = normalizedLow;
	}

	/**
	 * Normalize the specified value.
	 * 
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	@Override
	public double normalize(double value) {
		if (value > dataHigh) {
			return normalizedHigh;
		} else if (value < dataLow) {
			return normalizedLow;
		} else {
			double normalized = (value - dataLow);
			normalized /= (dataHigh - dataLow);
			normalized *= (normalizedHigh - normalizedLow);
			normalized += normalizedLow;
			return normalized;
		}
	}

	/**
	 * Denormalize the specified value.
	 * 
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	@Override
	public double denormalize(final double value) {
		double denormalized = dataLow - dataHigh;
		denormalized *= value;
		denormalized -= (normalizedHigh * dataLow);
		denormalized += (dataHigh * normalizedLow);
		denormalized /= (normalizedLow - normalizedHigh);
		return denormalized;
	}
}
