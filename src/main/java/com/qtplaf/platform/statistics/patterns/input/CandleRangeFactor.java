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

import java.util.List;

import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.patterns.Patterns;

/**
 * Range factor candle input.
 *
 * @author Miquel Sas
 */
public class CandleRangeFactor extends Candle {
	
	/**
	 * Constructor.
	 * 
	 * @param index The index.
	 * @param patternInfo The parent data pattern info.
	 */
	public CandleRangeFactor(int index, PatternInfo patternInfo) {
		super(index, patternInfo);
	}

	/**
	 * Return the input value.
	 * 
	 * @param datas The list of pattern datas.
	 * @return The input value.
	 */
	@Override
	public double getInput(List<Data> datas) {
		Data data = datas.get(getIndex());
		String alias = Fields.suffix(Fields.RANGE, Fields.Suffix.NRM);
		DataInfo dataInfo = Patterns.getDataInfo(getPatternInfo());
		int i = dataInfo.getOutputIndex(alias);
		double range_nrm = data.getValue(i);
		return range_nrm;
	}

}
