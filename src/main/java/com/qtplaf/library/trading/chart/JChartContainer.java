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
package com.qtplaf.library.trading.chart;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.LineBorderSides;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.Timestamp;

/**
 * A pane that contains a left pane to display prices, indicators and volumes. It contains a plotter and a vertical
 * axis.
 * 
 * @author Miquel Sas
 */
public class JChartContainer extends JPanel {

	/**
	 * The parent chart.
	 */
	private JChart chart;
	/**
	 * Info chart.
	 */
	private JChartInfo chartInfo;
	/**
	 * The plotter chart.
	 */
	private JChartPlotter chartPlotter;
	/**
	 * The vertical axis chart.
	 */
	private JChartVerticalAxis chartVerticalAxis;
	/**
	 * The data that is to be plotted in this container.
	 */
	private PlotData plotData;

	/**
	 * Constructor assigning the parent chart..
	 * 
	 * @param chart The parent chart.
	 */
	public JChartContainer(JChart chart) {
		super();
		this.chart = chart;

		// Layout
		setLayout(new GridBagLayout());

		// Info constraints.
		GridBagConstraints constraintsInfo = new GridBagConstraints();
		constraintsInfo.anchor = GridBagConstraints.NORTH;
		constraintsInfo.fill = GridBagConstraints.BOTH;
		constraintsInfo.gridheight = 1;
		constraintsInfo.gridwidth = 2;
		constraintsInfo.weightx = 0;
		constraintsInfo.weighty = 0;
		constraintsInfo.gridx = 0;
		constraintsInfo.gridy = 0;
		constraintsInfo.insets = new Insets(0, 0, 0, 0);

		// Add the info chart.
		chartInfo = new JChartInfo(this);
		add(chartInfo, constraintsInfo);

		// Plotter constraints.
		GridBagConstraints constraintsPlotter = new GridBagConstraints();
		constraintsPlotter.anchor = GridBagConstraints.EAST;
		constraintsPlotter.fill = GridBagConstraints.BOTH;
		constraintsPlotter.gridheight = 1;
		constraintsPlotter.gridwidth = 1;
		constraintsPlotter.weightx = 1;
		constraintsPlotter.weighty = 1;
		constraintsPlotter.gridx = 0;
		constraintsPlotter.gridy = 1;
		constraintsPlotter.insets = new Insets(0, 0, 0, 0);

		// Add the plotter.
		chartPlotter = new JChartPlotter(this);
		add(chartPlotter, constraintsPlotter);

		// Constrints vertical axis.
		GridBagConstraints constraintsAxis = new GridBagConstraints();
		constraintsAxis.anchor = GridBagConstraints.WEST;
		constraintsAxis.fill = GridBagConstraints.VERTICAL;
		constraintsAxis.gridheight = 1;
		constraintsAxis.gridwidth = 1;
		constraintsAxis.weightx = 0;
		constraintsAxis.weighty = 1;
		constraintsAxis.gridx = 1;
		constraintsAxis.gridy = 1;
		constraintsAxis.insets = new Insets(0, 0, 0, 0);

		// Add the vertical axis.
		chartVerticalAxis = new JChartVerticalAxis(this);
		chartVerticalAxis.setBorder(new LineBorderSides(Color.BLACK, 1, false, true, false, false));
		add(chartVerticalAxis, constraintsAxis);
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getChart().getSession();
	}

	/**
	 * Returns the parent chart.
	 * 
	 * @return The parent chart.
	 */
	public JChart getChart() {
		return chart;
	}

	/**
	 * Returns the info chart.
	 * 
	 * @return The info chart.
	 */
	public JChartInfo getChartInfo() {
		return chartInfo;
	}

	/**
	 * Returns the plotter chart.
	 * 
	 * @return The plotter chart.
	 */
	public JChartPlotter getChartPlotter() {
		return chartPlotter;
	}

	/**
	 * Returns the vertical axis chart.
	 * 
	 * @return The vertical axis chart.
	 */
	public JChartVerticalAxis getChartVerticalAxis() {
		return chartVerticalAxis;
	}

	/**
	 * Returns this contaner plot data.
	 * 
	 * @return The plot data.
	 */
	public PlotData getPlotData() {
		return plotData;
	}

	/**
	 * Sets this container plot data.
	 * 
	 * @param plotData The plot data.
	 */
	public void setPlotData(PlotData plotData) {
		this.plotData = plotData;
		chartVerticalAxis.setMaximumMinimumAndPreferredSizes();
	}

	/**
	 * Set the chart info.
	 * 
	 * @param x The mouse x.
	 * @param y The mouse y.
	 */
	public void setChartInfo(int x, int y) {

		// Plot data.
		PlotData plotData = getPlotData();
		if (plotData.isEmpty()) {
			return;
		}

		// The chart info panel.
		JChartInfo chartInfo = getChartInfo();

		// Indicate the instrument and the period.
		chartInfo.setInfo("Instrument", getInfoInstrument(), Color.BLACK, false);
		chartInfo.setInfo("Period", getInfoPeriod(), Color.BLUE, false);

		// The data index.
		int index = getPlotterContext().getDataIndex(x);

		// Time info.
		chartInfo.setInfo("Time", getInfoTime(index), Color.BLACK, false);

		// Iterate data lists.
		boolean black = false;
		for (int i = 0; i < plotData.size(); i++) {
			Color color = (black ? Color.BLACK : Color.BLUE);
			black = !black;
			DataInfo dataInfo = plotData.get(i).getDataInfo();
			String id = "Data-" + i;
			String text = "";
			if (index >= 0 && index < plotData.get(i).size()) {
				Data data = plotData.get(i).get(index);
				if (data.isValid()) {
					text = dataInfo.getInfoData(data);
				}
			}
			if (!text.isEmpty()) {
				chartInfo.setInfo(id, text, color, false);
			} else {
				chartInfo.deactivateInfo(id);
			}
		}

		// The cursor value.
		if (y >= 0) {
			chartInfo.setInfo("Cursor-Value", getInfoValue(y), Color.RED, false);
			chartInfo.setInfo("Cursor", "(" + x + ", " + y + ")", Color.BLACK, false);
		} else {
			chartInfo.deactivateInfo("Cursor-Value");
		}

		// Force repaint when all info items are set.
		chartInfo.repaintInfo();
	}

	/**
	 * Returns the intrument info.
	 * 
	 * @return The intrument info.
	 */
	private String getInfoInstrument() {
		StringBuilder b = new StringBuilder();
		b.append(getPlotData().get(0).getDataInfo().getInstrument().getId());
		return b.toString();
	}

	/**
	 * Returns the period info.
	 * 
	 * @return The period info.
	 */
	private String getInfoPeriod() {
		PlotData plotData = chartPlotter.getChartContainer().getPlotData();
		StringBuilder b = new StringBuilder();
		b.append(plotData.getPeriod().toString());
		return b.toString();
	}

	/**
	 * Returns the time information given an idex.
	 * 
	 * @param index The index.
	 * @return The time information.
	 */
	private String getInfoTime(int index) {
		StringBuilder b = new StringBuilder();
		PlotData plotData = getPlotData();
		if (plotData == null || plotData.isEmpty()) {
			return b.toString();
		}
		if (index < 0 || index >= plotData.get(0).size()) {
			return b.toString();
		}
		Data data = plotData.get(0).get(index);
		long time = data.getTime();
		Timestamp timestamp = new Timestamp(time);
		boolean year = true;
		boolean month = true;
		boolean day = true;
		boolean hour = true;
		boolean minute = true;
		boolean second = true;
		boolean millis = true;
		switch (plotData.getPeriod().getUnit()) {
		case Millisecond:
			break;
		case Second:
			millis = false;
			break;
		case Minute:
			millis = false;
			second = false;
			break;
		case Hour:
			millis = false;
			second = false;
			break;
		case Day:
			millis = false;
			second = false;
			minute = false;
			hour = false;
			break;
		case Week:
			millis = false;
			second = false;
			minute = false;
			hour = false;
			break;
		case Month:
			millis = false;
			second = false;
			minute = false;
			hour = false;
			day = false;
			break;
		case Year:
			millis = false;
			second = false;
			hour = false;
			minute = false;
			day = false;
			break;
		default:
			break;
		}
		b.append(FormatUtils.unformattedFromTimestamp(
			timestamp,
			year,
			month,
			day,
			hour,
			minute,
			second,
			millis,
			true));
		return b.toString();
	}

	/**
	 * Returns the information of the data values.
	 * 
	 * @param y The y coordinate.
	 * @return The information string.
	 */
	private String getInfoValue(int y) {
		StringBuilder b = new StringBuilder();
		PlotData plotData = getPlotData();
		if (plotData == null || plotData.isEmpty()) {
			return b.toString();
		}
		// Scale to apply to value.
		int tickScale = plotData.getTickScale();
		double value = getPlotterContext().getDataValue(y);
		b.append("P: ");
		b.append(FormatUtils.formattedFromDouble(value, tickScale, getSession().getLocale()));
		return b.toString();
	}

	/**
	 * Returns a suitable plotter context.
	 * 
	 * @return A plotter context.
	 */
	private PlotterContext getPlotterContext() {
		return new PlotterContext(getChartPlotter(), getPlotData());
	}
}
