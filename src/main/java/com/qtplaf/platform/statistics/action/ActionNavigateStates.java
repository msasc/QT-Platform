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

import java.awt.event.ActionEvent;
import java.util.List;

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.statistics.States;

/**
 *
 *
 * @author Miquel Sas
 */
public class ActionNavigateStates extends ActionNavigate {

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics
	 * @param titleSuffix The title suffix.
	 */
	public ActionNavigateStates(States states, String titleSuffix) {
		super(states, titleSuffix);
	}

	/**
	 * Return the underlying states statistics.
	 * 
	 * @return The underlying states statistics.
	 */
	protected States getStates() {
		return (States) getStatistics();
	}

	/**
	 * Return the list of plot datas.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		return getStates().getPlotDataListDefault();
	}

	/**
	 * Return the recordset to browse.
	 * 
	 * @return The recordset to browse.
	 */
	@Override
	public RecordSet getRecordSet() {
		DataPersistor persistor = new DataPersistor(getStates().getTableStates().getPersistor());
		return new DataRecordSet(persistor);
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}
}
