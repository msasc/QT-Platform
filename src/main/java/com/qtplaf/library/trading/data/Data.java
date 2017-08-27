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
package com.qtplaf.library.trading.data;

/**
 * Base class of timed data. An arbitrary number of double values with the starting time. It can be an data (open, high,
 * low, close, volume) pack or the list of values of an indicator.
 * 
 * @author Miquel Sas
 */
public class Data {

	/** Open index. */
	public static final int OPEN = 0;
	/** High index. */
	public static final int HIGH = 1;
	/** Low index. */
	public static final int LOW = 2;
	/** Close index. */
	public static final int CLOSE = 3;
	/** Volume index. */
	public static final int VOLUME = 4;
	/** Median price: (High + Low) / 2 */
	public static final int MEDIAN_PRICE = -1;
	/** Typical price: (High + Low + Close) / 3 */
	public static final int TYPICAL_PRICE = -2;
	/** Weighted close price: (High + Low + 2*Close) / 4 */
	public static final int WEIGHTED_CLOSE_PRICE = -3;

	/** Data price (OHLCV) size. */
	public static final int DATA_PRICE_SIZE = 5;
	
	/**
	 * Returns the median price: (H + L) / 2
	 * 
	 * @param data The data element.
	 * @return The median price.
	 */
	public static double getMedianPrice(Data data) {
		return (getHigh(data) + getLow(data)) / 2;
	}

	/**
	 * Returns the typical price: (H + L + C) / 3
	 * 
	 * @param data The data element.
	 * @return The typical price.
	 */
	public static double getTypicalPrice(Data data) {
		return (getHigh(data) + getLow(data) + getClose(data)) / 3;
	}

	/**
	 * Returns the weighted close price: (H + L + (2*C)) / 4
	 * 
	 * @param data The data element.
	 * @return The weighted close price.
	 */
	public static double getWeightedClosePrice(Data data) {
		return (getHigh(data) + getLow(data) + (2 * getClose(data))) / 4;
	}

	/**
	 * Returns the open value.
	 * 
	 * @param data The data element.
	 * @return The open value.
	 */
	public static double getOpen(Data data) {
		checkSize(data, 4);
		return data.getValue(OPEN);
	}

	/**
	 * Set the open value.
	 * 
	 * @param data The data.
	 * @param open The value.
	 */
	public static void setOpen(Data data, double open) {
		checkSize(data, 4);
		data.setValue(OPEN, open);
	}

	/**
	 * Returns the high value.
	 * 
	 * @param data The data element.
	 * @return The high value.
	 */
	public static double getHigh(Data data) {
		checkSize(data, 4);
		return data.getValue(HIGH);
	}

	/**
	 * Set the high value.
	 * 
	 * @param data The data.
	 * @param high The value.
	 */
	public static void setHigh(Data data, double high) {
		checkSize(data, 4);
		data.setValue(HIGH, high);
	}

	/**
	 * Returns the low value.
	 * 
	 * @param data The data element.
	 * @return The low value.
	 */
	public static double getLow(Data data) {
		checkSize(data, 4);
		return data.getValue(LOW);
	}

	/**
	 * Set the low value.
	 * 
	 * @param data The data.
	 * @param low The value.
	 */
	public static void setLow(Data data, double low) {
		checkSize(data, 4);
		data.setValue(LOW, low);
	}

	/**
	 * Returns the close value.
	 * 
	 * @param data The data element.
	 * @return The close value.
	 */
	public static double getClose(Data data) {
		checkSize(data, 4);
		return data.getValue(CLOSE);
	}

	/**
	 * Set the close value.
	 * 
	 * @param data The data.
	 * @param close The value.
	 */
	public static void setClose(Data data, double close) {
		checkSize(data, 4);
		data.setValue(CLOSE, close);
	}

	/**
	 * Returns the volume.
	 * 
	 * @param data The data.
	 * @return The volume.
	 */
	public static double getVolume(Data data) {
		if (data.size() < 5) {
			return 0;
		}
		return data.getValue(VOLUME);
	}

	/**
	 * Set the volume.
	 * 
	 * @param data The data.
	 * @param volume The volume.
	 */
	public static void setVolume(Data data, double volume) {
		checkSize(data, 5);
		data.setValue(VOLUME, volume);
	}

	/**
	 * Check that the data has at least the size.
	 * 
	 * @param data The data.
	 * @param size The size.
	 */
	private static void checkSize(Data data, int size) {
		if (data.size() < size) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns a boolean indicating if this bar is bullish.
	 * 
	 * @param data The data.
	 * @return A boolean indicating if this bar is bullish.
	 */
	public static boolean isBullish(Data data) {
		return getClose(data) >= getOpen(data);
	}

	/**
	 * Returns a boolean indicating if this bar is bearish.
	 * 
	 * @param data The data.
	 * @return A boolean indicating if this bar is bearish.
	 */
	public static boolean isBearish(Data data) {
		return !isBullish(data);
	}

	/**
	 * Check if the data bar is flat (open = high = low = close).
	 * 
	 * @param data The data bar.
	 * @return A boolean.
	 */
	public static boolean isFlat(Data data) {
		double open = getOpen(data);
		double high = getHigh(data);
		double low = getLow(data);
		double close = getClose(data);
		return (open == high && open == low && open == close);
	}

	/**
	 * Check if the data should accepted applyuuing the filter.
	 * 
	 * @param data The data.
	 * @param filter The filter.
	 * @return A boolean.
	 */
	public static boolean accept(Data data, Filter filter) {
		switch (filter) {
		case NoFilter:
			return true;
		case AllFlats:
			return !isFlat(data);
		case Weekends:
			// TODO implement weekend filter.
			return !isFlat(data);
		default:
			return true;
		}
	}

	/**
	 * The data.
	 */
	private double[] data;
	/**
	 * The start time in millis.
	 */
	private long time;
	/**
	 * A boolean that indicates if the data is valid and, for instance, should be plotted. Recall that some indicators
	 * can need a look backward or necessary number of bars to be calculated, and sometimes also a look forward, while
	 * the list of data has the same number of elements that the origin instrument list.
	 */
	private boolean valid = true;

	/**
	 * Default constructor.
	 */
	public Data() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param time The time.
	 * @param data The list of values.
	 */
	public Data(long time, double... data) {
		super();
		setTime(time);
		setData(data);
	}

	/**
	 * Constructs a data of the given size.
	 * 
	 * @param size The size or number of values.
	 */
	public Data(int size) {
		super();
		data = new double[size];
	}

	/**
	 * Returns the start time in millis.
	 * 
	 * @return The start time in millis.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the start time in millis.
	 * 
	 * @param time The start time in millis.
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Returns the data structure.
	 * 
	 * @return The data structure.
	 */
	public double[] getData() {
		return data;
	}

	/**
	 * Returns the value at the given index.
	 * 
	 * @param index The index.
	 * @return The value.
	 */
	public double getValue(int index) {
		return data[index];
	}

	/**
	 * Sets the value at the given index.
	 * 
	 * @param index The index.
	 * @param value The value.
	 */
	public void setValue(int index, double value) {
		data[index] = value;
	}

	/**
	 * Returns the internal data length.
	 * 
	 * @return The internal data length.
	 */
	public int size() {
		return data.length;
	}

	/**
	 * Sets the data structure.
	 * 
	 * @param data The data structure.
	 */
	public void setData(double[] data) {
		this.data = data;
	}

	/**
	 * Sets the data structure.
	 * 
	 * @param values The data structure.
	 */
	public void setValues(double[] values) {
		this.data = values;
	}

	/**
	 * Check if this data is valid.
	 * 
	 * @return A boolean that indicates if the data is valid and should be plotted or considered.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Set if this data is valid and should be plotted or considered.
	 * 
	 * @param valid A boolean that indicates if the data is valid and should be plotted or considered.
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	/**
	 * Returns a string representation of this data object.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (data != null) {
			boolean comma = false;
			for (double d : data) {
				if (comma) {
					b.append(", ");
				}
				comma = true;
				b.append(d);
			}
		}
		return b.toString();
	}
}
