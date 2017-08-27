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

import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.chart.JFrameChart;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Default action to show a chart.
 *
 * @author Miquel Sas
 */
public class ActionChart extends ActionTickerStatistics {
	
	/** Title suffix. */
	private String titleSuffix;
	/** Plot data list. */
	private List<PlotData> plotDataList;

	/**
	 * Constructor.
	 * 
	 * @param statistics The ticker statistics.
	 * @param plotDataList The list of plot datas.
	 * @param titleSuffix The title suffix.
	 */
	public ActionChart(TickerStatistics statistics, List<PlotData> plotDataList, String titleSuffix) {
		super(statistics);
		this.plotDataList = plotDataList;
		this.titleSuffix = titleSuffix;
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
	}

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

		// The chart frame.
		JFrameChart frame = new JFrameChart(getSession());
		frame.setTitle(title.toString());
		for (PlotData plotData : plotDataList) {
			frame.getChart().addPlotData(plotData);
		}
	}

}
