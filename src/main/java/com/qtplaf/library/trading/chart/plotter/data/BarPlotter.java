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
package com.qtplaf.library.trading.chart.plotter.data;

import java.awt.Graphics2D;
import java.awt.Shape;

import com.qtplaf.library.trading.chart.drawings.Bar;
import com.qtplaf.library.trading.chart.parameters.BarPlotParameters;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;

/**
 * PlotterOld of bars.
 * 
 * @author Miquel Sas
 */
public class BarPlotter extends DataPlotter {

	/**
	 * Plot parameters.
	 */
	private BarPlotParameters parameters = new BarPlotParameters();

	/**
	 * Constructor.
	 */
	public BarPlotter() {
		super();
		setIndexes(new int[] { 0, 1, 2, 3 });
		setName("Bar");
	}

	/**
	 * Returns the bar plot parameters.
	 * 
	 * @return The bar plot parameters.
	 */
	public BarPlotParameters getParameters() {
		return parameters;
	}

	/**
	 * Set the bar plot parameters.
	 * 
	 * @param parameters The bar plot parameters.
	 */
	public void setParameters(BarPlotParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is greater than 2, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	@Override
	public void plotDataIndex(Graphics2D g2, DataList dataList, int index) {

		// The bar.
		Data data = dataList.get(index);
		Bar bar = new Bar(index, data, getIndexes(data), getParameters());
		Shape shape = bar.getShape(getContext());

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// The color to apply.
		if (dataList.isOdd(index)) {
			if (bar.isBullish()) {
				bar.getParameters().setColor(getColorBullishOdd());
			} else {
				bar.getParameters().setColor(getColorBearishOdd());
			}
		} else {
			if (bar.isBullish()) {
				bar.getParameters().setColor(getColorBullishEven());
			} else {
				bar.getParameters().setColor(getColorBearishEven());
			}
		}

		// Draw the bar.
		bar.draw(g2, getContext());
	}
}
