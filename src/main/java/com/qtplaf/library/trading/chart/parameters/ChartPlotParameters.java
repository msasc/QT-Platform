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

package com.qtplaf.library.trading.chart.parameters;

import java.awt.Dimension;
import java.awt.Insets;

/**
 * Chart plot parameters.
 *
 * @author Miquel Sas
 */
public class ChartPlotParameters {

	/**
	 * The frame or plot insets, as a top, left, bottom and right factor of the available plot area. If, for example,
	 * the available width is 1400 pixels, a left inset of 0.02 will leave 28 pixels free of any paint to the left.
	 */
	private double[] chartPlotInsets = new double[] { 0.02, 0.01, 0.01, 0.02 };
	/**
	 * The data item (bar) width factor that is used to calculate the width of a bar or candle depending on the
	 * available width per data (bar).
	 */
	private double dataItemWidthFactor = 0.75;

	/**
	 * Constructor.
	 */
	public ChartPlotParameters() {
		super();
	}

	/**
	 * Returns the plot inset bottom as a factor of the plot height.
	 * 
	 * @return The plot inset bottom.
	 */
	public double getChartPlotInsetBottomFactor() {
		return chartPlotInsets[2];
	}

	/**
	 * Returns the plot inset left as a factor of the plot width.
	 * 
	 * @return The plot inset left.
	 */
	public double getChartPlotInsetLeftFactor() {
		return chartPlotInsets[1];
	}

	/**
	 * Returns the plot inset right as a factor of the plot width.
	 * 
	 * @return The plot inset right.
	 */
	public double getChartPlotInsetRightFactor() {
		return chartPlotInsets[3];
	}

	/**
	 * Returns the plot insets calculated with the plot factors.
	 * 
	 * @param size The chart size.
	 * @return The plot insets.
	 */
	public Insets getChartPlotInsets(Dimension size) {
		int paintAreaWidth = (int) size.getWidth();
		int paintAreaHeight = (int) size.getHeight();
		int insetTop = (int) (paintAreaHeight * getChartPlotInsetTopFactor());
		int insetLeft = (int) (paintAreaWidth * getChartPlotInsetLeftFactor());
		int insetBottom = (int) (paintAreaHeight * getChartPlotInsetBottomFactor());
		int insetRight = (int) (paintAreaWidth * getChartPlotInsetRightFactor());
		return new Insets(insetTop, insetLeft, insetBottom, insetRight);
	}

	/**
	 * Returns the plot inset top as a factor of the plot height.
	 * 
	 * @return The plot inset top.
	 */
	public double getChartPlotInsetTopFactor() {
		return chartPlotInsets[0];
	}

	/**
	 * Sets the frame or plot insets, as a top, left, bottom and right factor of the available plot area. If, for
	 * example, the available width is 1400 pixels, a left inset of 0.02 will leave 28 pixels free of any paint to the
	 * left.
	 * 
	 * @param top Top factor.
	 * @param left Left factor.
	 * @param bottom Bottom factor.
	 * @param right Right factor.
	 */
	public void setChartPlotInsets(double top, double left, double bottom, double right) {
		chartPlotInsets[0] = top;
		chartPlotInsets[1] = left;
		chartPlotInsets[2] = bottom;
		chartPlotInsets[3] = right;
	}

	/**
	 * Returns the factor to calculate the width of a bar. As a general rule, it can be 75% of the available width per
	 * bar, as an odd number, and if the result is less than 2, plot just a vertical line of 1 pixel width. Default is
	 * 0.75.
	 * 
	 * @return The factor to calculate the width of a bar.
	 */
	public double getDataItemWidthFactor() {
		return dataItemWidthFactor;
	}

	/**
	 * Sets the factor to calculate the width of a bar. As a general rule, it can be 75% of the available width per bar,
	 * as an odd number, and if the result is less than 2, plot just a vertical line of 1 pixel width. Default is 0.75.
	 * 
	 * @param dataWidthFactor The factor to calculate the width of a bar.
	 */
	public void setDataItemWidthFactor(double dataWidthFactor) {
		if (dataWidthFactor <= 0 || dataWidthFactor > 1) {
			throw new IllegalArgumentException("Data width factor must be between 0 and 1.");
		}
		this.dataItemWidthFactor = dataWidthFactor;
	}
}
