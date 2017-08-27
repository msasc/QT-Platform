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

package com.qtplaf.platform.statistics.patterns.input;

import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.platform.statistics.patterns.PatternInput;

/**
 * Root of candle inputs.
 *
 * @author Miquel Sas
 */
public abstract class Candle extends PatternInput {

	/** Normalizer. */
	private StdNormalizer normalizer;
	/** Index in the list of datas. */
	private int index;

	/**
	 * Constructor.
	 * 
	 * @param index The index.
	 * @param patternInfo The parent data pattern info.
	 */
	public Candle(int index, PatternInfo patternInfo) {
		super(patternInfo);
		this.index = index;
	}

	/**
	 * Returns the index in the list of datas.
	 * 
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Return the normalizer.
	 * 
	 * @return The normalizer.
	 */
	public StdNormalizer getNormalizer() {
		return normalizer;
	}

	/**
	 * Set the normalizer.
	 * 
	 * @param normalizer The normalizer.
	 */
	public void setNormalizer(StdNormalizer normalizer) {
		this.normalizer = normalizer;
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
	 * @return The close value.
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
	 * Returns the range.
	 * 
	 * @param data The data element.
	 * @return The range.
	 */
	public double getRange(Data data) {
		double high = getHigh(data);
		double low = getLow(data);
		return high - low;
	}

	/**
	 * Check bullish.
	 * 
	 * @param data The data element.
	 * @return A boolean.
	 */
	public boolean isBullish(Data data) {
		double close = getClose(data);
		double open = getOpen(data);
		return close > open;
	}

	/**
	 * Check bearish.
	 * 
	 * @param data The data element.
	 * @return A boolean.
	 */
	public boolean isBearish(Data data) {
		double close = getClose(data);
		double open = getOpen(data);
		return close < open;
	}

	/**
	 * Check flat.
	 * 
	 * @param data The data element.
	 * @return A boolean.
	 */
	public boolean isFlat(Data data) {
		double close = getClose(data);
		double open = getOpen(data);
		return close == open;
	}
}
