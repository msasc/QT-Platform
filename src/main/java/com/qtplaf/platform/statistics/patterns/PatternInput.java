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

package com.qtplaf.platform.statistics.patterns;

import java.util.List;

import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.trading.data.Data;

/**
 * Root of input functions to retrieve input values for a data pattern.
 *
 * @author Miquel Sas
 */
public abstract class PatternInput {
	
	/** Parent data pattern info. */
	private PatternInfo patternInfo;

	/**
	 * Constructor.
	 * 
	 * @param patternInfo The parent data pattern info.
	 */
	public PatternInput(PatternInfo patternInfo) {
		super();
		this.patternInfo = patternInfo;
	}

	/**
	 * Return the parent data pattern info.
	 * @return The parent data pattern info.
	 */
	public PatternInfo getPatternInfo() {
		return patternInfo;
	}

	/**
	 * Return the input value.
	 * 
	 * @param datas The list of pattern datas.
	 * @return The input value.
	 */
	public abstract double getInput(List<Data> datas);
}
