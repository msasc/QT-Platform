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

package com.qtplaf.library.trading.chart.plotter;

import java.awt.Dimension;
import java.awt.Insets;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.JChartPlotter;
import com.qtplaf.library.trading.chart.parameters.ChartPlotParameters;
import com.qtplaf.library.trading.chart.parameters.HorizontalAxisPlotParameters;
import com.qtplaf.library.trading.chart.parameters.VerticalAxisPlotParameters;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotScale;
import com.qtplaf.library.util.NumberUtils;

/**
 * Context information about the plot task.
 *
 * @author Miquel Sas
 */
public class PlotterContext {

	/**
	 * Chart plotter.
	 */
	private JChartPlotter chartPlotter;
	/**
	 * The plot data.
	 */
	private PlotData plotData;
	/**
	 * Calculated chart insets.
	 */
	private Insets chartInsets;
	/**
	 * Calculated chart width
	 */
	private int chartWidth;
	/**
	 * Calculated chart height.
	 */
	private int chartHeight;
	/**
	 * The calculated available width per data item, at least 1.
	 */
	private int availablewidthPerDataItem;
	/**
	 * The calculated data item (candlestick or bar) width.
	 */
	private int dataItemWidth;

	/**
	 * Constructor assinging the context values.
	 * 
	 * @param chartPlotter The parent chart plotter.
	 * @param plotData The plot data.
	 */
	public PlotterContext(JChartPlotter chartPlotter, PlotData plotData) {
		super();

		// Assign members.
		this.chartPlotter = chartPlotter;
		this.plotData = plotData;

		// Calculate chart insets, width and height.
		chartInsets = getChartPlotParameters().getChartPlotInsets(getChartSize());
		chartWidth = getChartSize().width - chartInsets.left - chartInsets.right;
		chartHeight = getChartSize().height - chartInsets.top - chartInsets.bottom;

		// Calculate the available width per data item.
		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();
		double periods = endIndex - startIndex + 1;
		availablewidthPerDataItem = (int) (chartWidth / periods);
		if (availablewidthPerDataItem < 1) {
			availablewidthPerDataItem = 1;
		}

		// Calculate the plot width of a bar. As a general rule, it can be 75% of the available width per bar, as anodd
		// number, and if the result is less than 2, plot just a vertical line of 1 pixel width.
		int widthPerItem = availablewidthPerDataItem;
		dataItemWidth = 1;
		if (widthPerItem > 1 && widthPerItem <= 3) {
			dataItemWidth = 3;
		} else {
			dataItemWidth =
				(int) NumberUtils.round((widthPerItem) * getChartPlotParameters().getDataItemWidthFactor(), 0);
			if (NumberUtils.isLeap(dataItemWidth)) {
				dataItemWidth -= 1;
			}
		}
	}

	/**
	 * Returns the chart plotter size.
	 * 
	 * @return The chart plotter size.
	 */
	public Dimension getChartSize() {
		return getChartPlotter().getSize();
	}

	/**
	 * Returns the chart insets calculated using the inset factors.
	 * 
	 * @return The insets.
	 */
	public Insets getChartInsets() {
		return chartInsets;
	}

	/**
	 * Returns the X coordinate where starts the area to plot the data of index, given the index of the data, the start
	 * index, the end index, the left inset and the plot area width.
	 * 
	 * @param index The data index.
	 * @return The X coordinate.
	 * @throws IllegalStateException If the data index is not between the start and the end indexes.
	 */
	public int getCoordinateX(double index) throws IllegalStateException {
		// Start and end indexes.
		double startIndex = plotData.getStartIndex();
		double endIndex = plotData.getEndIndex();
		// Check the index is in the start-end range.
		if (index < startIndex || index > endIndex) {
			// throw new IllegalStateException();
		}
		// The index factor: relation between index and the difference endIndex - startIndex.
		double indexFactor = (index - startIndex) / (endIndex - startIndex);
		// The relative X coordinate counted from the left of the plot area.
		int relativeX = (int) NumberUtils.round(indexFactor * chartWidth, 0);
		// Final X coordinate counted from the left of the paint area.
		int coordinateX = getChartInsets().left + relativeX;
		return coordinateX;
	}

	/**
	 * Returns the Y coordinate, starting at the top of the paint area, given the value, the maximum and minimum visible
	 * values, the top inset, the plot area height, once removed top and bottom insets, and the plot scale.
	 * 
	 * @param value The value to retrieve its Y coordinate.
	 * @return The Y coordinate for the argument value.
	 * @throws IllegalStateException If the value is not in the maximum-minimum value range.
	 */
	public int getCoordinateY(double value) throws IllegalStateException {
		
		// Maximum and minimum values.
		double maximumValue = plotData.getMaximumValue();
		double minimumValue = plotData.getMinimumValue();
		// Check that the value to plot is in the maximum-minimum range.
		if (value > maximumValue || value < minimumValue) {
			// throw new IllegalStateException();
		}

		// Apply scale to values if necessary.
		if (plotData.getPlotScale().equals(PlotScale.Logarithmic)) {
			maximumValue = Math.log1p(maximumValue);
			minimumValue = Math.log1p(minimumValue);
			value = Math.log1p(value);
		}

		// The value factor: relation between value and the difference maximumValue - minimumValue.
		double valueFactor = (value - minimumValue) / (maximumValue - minimumValue);
		// The relative Y coordinate (in a linear scale) counted from the bottom of the plot area.
		int relativeY = 0;
		if (Double.isFinite(value) && Double.isFinite(valueFactor)) {
			relativeY = (int) NumberUtils.round(valueFactor * chartHeight, 0);
		}
		// Final Y coordinate counted from the top of the paint area.
		int coordinateY = getChartInsets().top + chartHeight - relativeY;
		return coordinateY;
	}

	/**
	 * Returns the index on the data given the x coordinate in the plot area. The returned index is greater than or
	 * equal to <i>PlotData.startIndex</i> and less equal than <i>PlotData.endIndex</i>.
	 * 
	 * @param x The x coordinate in the plot area.
	 * @return The index on the data.
	 */
	public int getDataIndex(int x) {

		// If the x coordinate is less that the left inset, return the start index.
		if (x < chartInsets.left) {
			return plotData.getStartIndex();
		}

		// If the x coordinate is greater than the available width, return the end index.
		if (x > chartInsets.left + chartWidth - 1) {
			return plotData.getEndIndex();
		}

		// The x coordinate relative to the plot area width.
		double xRelative = x - chartInsets.left;

		// Start and end index.
		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();

		// The index.
		double factor = xRelative / Double.valueOf(chartWidth);
		if (!Double.isFinite(factor)) {
			return 0;
		}
		double indexes = endIndex - startIndex + 1;
		double numPeriods = indexes * factor;
		int periods = Double.valueOf(NumberUtils.round(numPeriods, 0)).intValue();
		int index = startIndex + periods - 1;
		return index;
	}

	/**
	 * Returns the candlestick or bar width.
	 * 
	 * @return The candlestick or bar width.
	 */
	public int getDataItemWidth() {
		return dataItemWidth;
	}

	/**
	 * Returns the data value given the y coordinate in the plot area. The returned value is greater than or equal to
	 * <i>PlotData.minimumValue</i> and less equal than <i>PlotData.maximumValue</i>.
	 * 
	 * @param y The y coordinate in the plot area.
	 * @return The data value.
	 */
	public double getDataValue(int y) {
		
		// The y coordinate relaive to the plot area.
		double yRelative = y - chartInsets.top;

		// Minimum and maximum values.
		double minimumValue = plotData.getMinimumValue();
		double maximumValue = plotData.getMaximumValue();

		// Apply scale to minimum and maximum values if necessary.
		if (plotData.getPlotScale().equals(PlotScale.Logarithmic)) {
			maximumValue = Math.log(maximumValue);
			minimumValue = Math.log(minimumValue);
		}

		// The value. Note that y is top-down.
		double height = chartHeight;
		double factor = (height - yRelative) / height;
		double value = minimumValue + ((maximumValue - minimumValue) * factor);

		// Apply the inverse scale if necessary.
		if (plotData.getPlotScale().equals(PlotScale.Logarithmic)) {
			value = Math.pow(Math.E, value);
		}

		int tickScale = plotData.getTickScale();
		return NumberUtils.round(value, tickScale);
	}

	/**
	 * Returns the coordinate of the drawing center for a bar, candle, line or histogram, given the starting X
	 * coordinate.
	 * 
	 * @param x The starting x coordinate.
	 * @return The verical line X coordinate.
	 */
	public int getDrawingCenterCoordinateX(int x) {
		int verticalLineWidth = 1;
		if (dataItemWidth > 1) {
			x += ((dataItemWidth - verticalLineWidth) / 2);
		}
		return x;
	}

	/**
	 * Returns the plot data.
	 * 
	 * @return The plot data.
	 */
	public PlotData getPlotData() {
		return plotData;
	}

	/**
	 * Returns the horizontal axis plot parameters.
	 * 
	 * @return The horizontal axis plot parameters.
	 */
	public HorizontalAxisPlotParameters getHorizontalAxisPlotParameters() {
		return getChartPlotter().getChartContainer().getChart().getHorizontalAxisPlotParameters();
	}

	/**
	 * Returns the axis plot parameters.
	 * 
	 * @return The axis plot parameters.
	 */
	public VerticalAxisPlotParameters getVerticalAxisPlotParameters() {
		return getChartPlotter().getChartContainer().getChart().getVerticalAxisPlotParameters();
	}

	/**
	 * Returns the chart plot parameters.
	 * 
	 * @return The chart plot parameters.
	 */
	public ChartPlotParameters getChartPlotParameters() {
		return getChartPlotter().getChartContainer().getChart().getChartPlotParameters();
	}

	/**
	 * Returns the parent chart plotter.
	 * 
	 * @return The chart plotter.
	 */
	public JChartPlotter getChartPlotter() {
		return chartPlotter;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getChartPlotter().getSession();
	}

}
