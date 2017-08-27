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

package com.qtplaf.library.trading.pattern.candle;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.ai.fuzzy.Segment;
import com.qtplaf.library.ai.fuzzy.function.Linear;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.pattern.Pattern;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Root class of candlestick patterns. To correctly calculate proportions and ranges, it receives the average range
 * (hig-low) and its standard deviation.
 * <p>
 * Candle patterns are calculated comparing factors. For instance if the range factor is greater than or equal to big
 * and the body factor is also greater than or equal to big, and the candle is bullish, then it is a big bullish candle.
 * <p>
 * The fuzzy control to determine sizes and positions is configured pretty simple, big, medium, smal and top, middle,
 * bottom. For further detail about the position within a segment, the segment factor can be used again through the
 * control.
 *
 * @author Miquel Sas
 */
public abstract class CandlePattern extends Pattern {

	/**
	 * Enum the proportional sizes.
	 */
	public interface Size {
		String VeryBig = "6";
		String Big = "5";
		String MediumBig = "4";
		String Medium = "3";
		String MediumSmall = "2";
		String Small = "1";
		String VerySmall = "0";
	}

	/**
	 * Enum proportional positions.
	 */
	public interface Position {
		String Top = "6";
		String QuasiTop = "5";
		String MiddleUp = "4";
		String Middle = "3";
		String MiddleDown = "2";
		String QuasiBottom = "1";
		String Bottom = "0";
	}
	/** Average range statistically calculated for the time frame. */
	private double rangeAverage;
	/** Range standard deviation also statistically calculated for the time frame. */
	private double rangeStdDev;
	/** The fuzzy size control to check sizes in a range of 0 to 1. */
	private Control control;

	/**
	 * Contructor.
	 */
	public CandlePattern() {
		super();
	}

	/**
	 * Returns the maximum range for calculations, a rangeAverage + (2 * rangeStdDev)
	 * 
	 * @return The maximum range for calculations.
	 */
	public double getMaximumRange() {
		return rangeAverage + (2 * rangeStdDev);
	}

	/**
	 * Returns the fuzzy control to check sizes in a range of 0 to 1, normally used with factors like range, body,
	 * shadow factors.
	 * 
	 * @return The fuzzy control.
	 */
	public Control getControl() {
		if (control == null) {
			List<Segment> segments = new ArrayList<>();
			addSegment(segments, Size.VerySmall, 0.10, -1);
			addSegment(segments, Size.Small, 0.25, -1);
			addSegment(segments, Size.MediumSmall, 0.40, -1);
			addSegment(segments, Size.Medium, 0.60, 0);
			addSegment(segments, Size.MediumBig, 0.75, 1);
			addSegment(segments, Size.Big, 0.90, 1);
			addSegment(segments, Size.VeryBig, 1.00, 1);
			control = new Control(segments);
		}
		return control;
	}

	/**
	 * Convenience method to build the list of segments.
	 * 
	 * @param segments The list of segments.
	 * @param label The label.
	 * @param maximum The maximum value.
	 * @param sign The sign.
	 */
	private void addSegment(List<Segment> segments, String label, double maximum, int sign) {
		double minimum = 0.00;
		if (!segments.isEmpty()) {
			minimum = Math.nextUp(ListUtils.getLast(segments).getMaximum());
		}
		segments.add(new Segment(label, maximum, minimum, sign, new Linear()));
	}

	/**
	 * Returns the average range.
	 * 
	 * @return The average range.
	 */
	public double getRangeAverage() {
		return rangeAverage;
	}

	/**
	 * Set the average range.
	 * 
	 * @param rangeAverage The average range.
	 */
	public void setRangeAverage(double rangeAverage) {
		this.rangeAverage = rangeAverage;
	}

	/**
	 * Returns the range standard deviation.
	 * 
	 * @return The range standard deviation.
	 */
	public double getRangeStdDev() {
		return rangeStdDev;
	}

	/**
	 * Set the range standard deviation.
	 * 
	 * @param rangeStdDev The range standard deviation.
	 */
	public void setRangeStdDev(double rangeStdDev) {
		this.rangeStdDev = rangeStdDev;
	}

	/**
	 * Convenience method to check a pattern from anothe pattern.
	 * 
	 * @param pattern The pattern to check.
	 * @param dataList The data list.
	 * @param index The index.
	 * @return A boolean.
	 */
	public boolean isPattern(CandlePattern pattern, DataList dataList, int index) {
		pattern.setRangeAverage(getRangeAverage());
		pattern.setRangeStdDev(getRangeStdDev());
		return pattern.isPattern(dataList, index);
	}

	/**
	 * Returns the open value.
	 * 
	 * @param data The data element.
	 * @return The open value.
	 */
	public double getOpen(Data data) {
		return Data.getOpen(data);
	}

	/**
	 * Returns the high value.
	 * 
	 * @param data The data element.
	 * @return The high value.
	 */
	public double getHigh(Data data) {
		return Data.getHigh(data);
	}

	/**
	 * Returns the low value.
	 * 
	 * @param data The data element.
	 * @return The low value.
	 */
	public double getLow(Data data) {
		return Data.getLow(data);
	}

	/**
	 * Returns the close value.
	 * 
	 * @param data The data element.
	 * @return The open value.
	 */
	public double getClose(Data data) {
		return Data.getClose(data);
	}

	/**
	 * Returns the body of the candle.
	 * 
	 * @param data The data element.
	 * @return The body.
	 */
	public double getBody(Data data) {
		double open = getOpen(data);
		double close = getClose(data);
		return Math.abs(close - open);
	}

	/**
	 * Returns the body high.
	 * 
	 * @param data The data element.
	 * @return The body high.
	 */
	public double getBodyHigh(Data data) {
		double open = getOpen(data);
		double close = getClose(data);
		return Math.max(close, open);
	}

	/**
	 * Returns the body low.
	 * 
	 * @param data The data element.
	 * @return The body low.
	 */
	public double getBodyLow(Data data) {
		double open = getOpen(data);
		double close = getClose(data);
		return Math.min(close, open);
	}

	/**
	 * Returns the body factor (0 to 1) relating the body with the range.
	 * 
	 * @param data The data element.
	 * @return The body factor.
	 */
	public double getBodyFactor(Data data) {
		double body = getBody(data);
		double range = getRange(data);
		return Math.min(1.0, NumberUtils.zeroDiv(body, range));
	}

	/**
	 * Returns the body center.
	 * 
	 * @param data The data element.
	 * @return The body center.
	 */
	public double getBodyCenter(Data data) {
		double open = getOpen(data);
		double close = getClose(data);
		return (open + close) / 2;
	}

	/**
	 * Returns the body center factors that indicates the body position.
	 * 
	 * @param data The data element.
	 * @return The body center factor.
	 */
	public double getBodyCenterFactor(Data data) {
		double center = getBodyCenter(data);
		double low = getLow(data);
		double range = getRange(data);
		return NumberUtils.zeroDiv(center - low, range);
	}

	/**
	 * Returns the candle range (high - low) of the list of data elements.
	 * 
	 * @param datas The list data elements.
	 * @return The range (high - low).
	 */
	public double getRange(Data... datas) {
		double high = NumberUtils.MIN_DOUBLE;
		double low = NumberUtils.MAX_DOUBLE;
		for (Data data : datas) {
			high = Math.max(high, getHigh(data));
			low = Math.min(low, getLow(data));
		}
		return high - low;
	}

	/**
	 * Returns the range unitary factor (0 to 1) by comparing the range with an estimated maximum range.
	 * 
	 * @param data The data element.
	 * @return The range factor (range / maxRange).
	 */
	public double getRangeFactor(Data data) {
		return Math.min(1.0, NumberUtils.zeroDiv(getRange(data), getMaximumRange()));
	}

	/**
	 * Returns the shadows factor (1 - body factor).
	 * 
	 * @param data The data element.
	 * @return The shadow factor.
	 */
	public double getShadowsFactor(Data data) {
		return 1.0 - getBodyFactor(data);
	}

	/**
	 * Returns the the lower shadow of the candle.
	 * 
	 * @param data The data element.
	 * @return Tha lower shadow.
	 */
	public double getShadowLower(Data data) {
		double low = getLow(data);
		double open = getOpen(data);
		double close = getClose(data);
		return Math.min(open, close) - low;
	}

	/**
	 * Returns the lower shadow factor related to the range.
	 * 
	 * @param data The data element.
	 * @return The lower shadow factor.
	 */
	public double getShadowLowerFactor(Data data) {
		double shadow = getShadowLower(data);
		double range = getRange(data);
		return Math.min(1.0, NumberUtils.zeroDiv(shadow, range));
	}

	/**
	 * Returns the the upper shadow of the candle.
	 * 
	 * @param data The data element.
	 * @return Tha upper shadow.
	 */
	public double getShadowUpper(Data data) {
		double high = getHigh(data);
		double open = getOpen(data);
		double close = getClose(data);
		return high - Math.max(open, close);
	}

	/**
	 * Returns the upper shadow factor related to the range.
	 * 
	 * @param data The data element.
	 * @return The upper shadow factor.
	 */
	public double getShadowUpperFactor(Data data) {
		double shadow = getShadowUpper(data);
		double range = getRange(data);
		return Math.min(1.0, NumberUtils.zeroDiv(shadow, range));
	}

	/**
	 * Check bearish
	 * 
	 * @param data The data element.
	 * @return A boolean.
	 */
	public boolean isBearish(Data data) {
		return Data.isBearish(data);
	}

	/**
	 * Check bullish
	 * 
	 * @param data The data element.
	 * @return A boolean.
	 */
	public boolean isBullish(Data data) {
		return Data.isBullish(data);
	}

}
