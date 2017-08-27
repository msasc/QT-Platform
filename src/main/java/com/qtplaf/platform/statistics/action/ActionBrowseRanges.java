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

package com.qtplaf.platform.statistics.action;

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.platform.statistics.States;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Browse the states ranges.
 *
 * @author Miquel Sas
 */
public class ActionBrowseRanges extends ActionBrowse {

	/**
	 * Constructor.
	 * 
	 * @param statistics The ticker statistics.
	 * @param titleSuffix The title suffix.
	 */
	public ActionBrowseRanges(TickerStatistics statistics, String titleSuffix) {
		super(statistics, titleSuffix);
		if (!(statistics instanceof States)) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Return the recordset to browse.
	 * 
	 * @return The recordset to browse.
	 */
	@Override
	public RecordSet getRecordSet() {
		States states = (States) getStatistics();
		return states.getRecordSetRanges(true);
	}

}
