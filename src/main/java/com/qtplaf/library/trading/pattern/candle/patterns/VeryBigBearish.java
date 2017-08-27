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

package com.qtplaf.library.trading.pattern.candle.patterns;

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.pattern.candle.CandlePattern;

/**
 * Very big bearish candle with body at least half the range.
 *
 * @author Miquel Sas
 */
public class VeryBigBearish extends CandlePattern {

	/**
	 * Constructor.
	 */
	public VeryBigBearish() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Very bearish candle with body at least half the range");
		setLookBackward(1);
	}

	/**
	 * Check if the pattern can be identified at the current data and index.
	 * 
	 * @param dataList The data list.
	 * @param index The current index.
	 * @return A boolean indicating that the pattern has been identified.
	 */
	@Override
	public boolean isPattern(DataList dataList, int index) {
		Data data = dataList.get(index);
		Control control = getControl();
		double rangeFactor = getRangeFactor(data);
		double bodyFactor = getBodyFactor(data);
		if (isBearish(data)) {
			if (control.checkIn(rangeFactor, Size.VeryBig)) {
				if (control.getFactor(bodyFactor) >= 0.5) {
					return true;
				}
			}
		}
		return false;
	}

}
