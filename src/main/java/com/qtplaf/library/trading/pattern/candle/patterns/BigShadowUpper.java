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
 * Big candle with small body and long upper shadow (hammer).
 *
 * @author Miquel Sas
 */
public class BigShadowUpper extends CandlePattern {

	/**
	 * Constructor.
	 */
	public BigShadowUpper() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Big candle with small body and long upper shadow");
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
		double bodyCenter = getBodyCenterFactor(data);
		if (control.getFactor(rangeFactor) >= 0.5) {
			if (control.checkLE(bodyFactor, Size.Small)) {
				if (control.checkLE(bodyCenter, Position.MiddleDown)) {
					return true;
				}
			}
		}
		return false;
	}

}
