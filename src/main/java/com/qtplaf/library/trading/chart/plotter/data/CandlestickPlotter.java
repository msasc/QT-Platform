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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import com.qtplaf.library.trading.chart.drawings.Candlestick;
import com.qtplaf.library.trading.chart.parameters.CandlestickPlotParameters;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;

/**
 * PlotterOld of candlesticks.
 * 
 * @author Miquel Sas
 */
public class CandlestickPlotter extends DataPlotter {

	/**
	 * Plot parameters.
	 */
	private CandlestickPlotParameters parameters = new CandlestickPlotParameters();

	/**
	 * Constructor.
	 */
	public CandlestickPlotter() {
		super();
		setIndexes(new int[] { 0, 1, 2, 3 });
		setName("Candlestick");
	}

	/**
	 * Returns the plot parameters.
	 * 
	 * @return The plot parameters.
	 */
	public CandlestickPlotParameters getParameters() {
		return parameters;
	}

	/**
	 * Set the plot parameters.
	 * 
	 * @param parameters The plot parameters.
	 */
	public void setParameters(CandlestickPlotParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the candlestick drawing for a given data list and index in the range.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return The candlestick drawing.
	 */
	private Candlestick getCandlestick(DataList dataList, int index) {
		Data data = dataList.get(index);
		return new Candlestick(index, data, getIndexes(data), getParameters());
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is not -1, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	@Override
	public void plotDataIndex(Graphics2D g2, DataList dataList, int index) {

		// The candlestick.
		Candlestick candlestick = getCandlestick(dataList, index);
		Shape shape = candlestick.getShape(getContext());

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// The color to apply bullish/bearish-odd/even.
		Color color;
		if (dataList.isOdd(index)) {
			if (candlestick.isBullish()) {
				color = getColorBullishOdd();
			} else {
				color = getColorBearishOdd();
			}
		} else {
			if (candlestick.isBullish()) {
				color = getColorBullishEven();
			} else {
				color = getColorBearishEven();
			}
		}
		candlestick.getParameters().setFillColor(color);
		candlestick.draw(g2, getContext());
	}

}
