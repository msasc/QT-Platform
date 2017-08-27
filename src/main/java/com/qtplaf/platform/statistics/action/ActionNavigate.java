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

import javax.swing.Action;

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.chart.JChartNavigate;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Action to navigate a chart with a companion record set, performing several actions.
 *
 * @author Miquel Sas
 */
public abstract class ActionNavigate extends ActionTickerStatistics {

	/** Chart navbigate object. */
	private JChartNavigate chartNavigate;
	/** Title suffix. */
	private String titleSuffix;

	/**
	 * Constructor.
	 * 
	 * @param statistics The source statistics.
	 * @param titleSuffix The title suffix.
	 */
	public ActionNavigate(TickerStatistics statistics, String titleSuffix) {
		super(statistics);
		this.titleSuffix = titleSuffix;
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
		
		this.chartNavigate = new JChartNavigate(getSession());
	}

	/**
	 * Add an action to the chart. The panel table record is also set to the action.
	 * 
	 * @param action The action to add.
	 */
	public void addActionToChart(Action action) {
		chartNavigate.addActionToChart(action);
	}

	/**
	 * Add an action to the table record panel. The chart is also set to the action.
	 * 
	 * @param action The action to add.
	 */
	public void addActionToTable(Action action) {
		chartNavigate.addActionToTable(action);
	}

	/**
	 * Return the list of plot datas.
	 * 
	 * @return The list of plot datas.
	 */
	public abstract List<PlotData> getPlotDataList();

	/**
	 * Return the recordset to browse.
	 * 
	 * @return The recordset to browse.
	 */
	public abstract RecordSet getRecordSet();

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Chart title.
		StringBuilder title = new StringBuilder();
		title.append(getServer().getName());
		title.append(", ");
		title.append(getInstrument().getId());
		title.append(" ");
		title.append(getPeriod());
		title.append(" [");
		title.append(titleSuffix);
		title.append("]");
		
		chartNavigate.setTitle(title.toString());
		chartNavigate.setVisible(true);
		
		List<PlotData> plotDataList = getPlotDataList();
		for (PlotData plotData : plotDataList) {
			chartNavigate.getChart().addPlotData(plotData);
		}
		
		chartNavigate.setRecordSet(getRecordSet());
		
	}

}
