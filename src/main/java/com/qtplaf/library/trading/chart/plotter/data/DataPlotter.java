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

import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.OutputInfo;
import com.qtplaf.library.util.StringUtils;

/**
 * Base class for data plotters of timed data.
 * 
 * @author Miquel Sas
 */
public abstract class DataPlotter extends Plotter {

	/**
	 * Indexes within the data element used by the plotter.
	 */
	private int[] indexes;
	/**
	 * The color used for a bearish line bar candle is an even period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBearishEven = new Color(128, 16, 16);
	/**
	 * The color used for a bearish line bar candle is an odd period. For periods lower than day, the color changes when
	 * the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBearishOdd = new Color(128, 16, 16);
//	private Color colorBearishOdd = new Color(25, 25, 25);
	/**
	 * The color used for a bullish line/bar/candle in an even period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBullishEven = new Color(16, 96, 16);
	/**
	 * The color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color changes when
	 * the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBullishOdd = new Color(16, 96, 16);
//	private Color colorBullishOdd = new Color(215, 215, 215);

	/**
	 * A boolean to control if the plotter should plot, thus allowing to hide/show plot actions.
	 */
	private boolean plot = true;

	/**
	 * Constructor.
	 */
	public DataPlotter() {
		super();
	}

	/**
	 * Check if the plotter should plot.
	 * 
	 * @return A boolean.
	 */
	public boolean isPlot() {
		return plot;
	}

	/**
	 * Set if the plotter should plot.
	 * 
	 * @param plot A boolean.
	 */
	public void setPlot(boolean plot) {
		this.plot = plot;
	}

	/**
	 * Returns the indexes within the data element used by the plotter.
	 * 
	 * @return The indexes within the data element used by the plotter.
	 */
	public int[] getIndexes() {
		return indexes;
	}

	/**
	 * Returns the indexes to apply to the data item. By default, all data values.
	 * 
	 * @param data The data item.
	 * @return The indexes.
	 */
	public int[] getIndexes(Data data) {
		if (indexes == null) {
			indexes = new int[data.size()];
			for (int i = 0; i < data.size(); i++) {
				indexes[i] = i;
			}
		}
		return indexes;
	}

	/**
	 * Returns the list of values given the data element.
	 * 
	 * @param data The data item.
	 * @return The list of values.
	 */
	public double[] getValues(Data data) {
		int[] indexes = getIndexes(data);
		double[] values = new double[indexes.length];
		for (int i = 0; i < indexes.length; i++) {
			values[i] = data.getValue(indexes[i]);
		}
		return values;
	}

	/**
	 * Set the indexes within the data element used by the plotter.
	 * 
	 * @param indexes The indexes within the data element used by the plotter.
	 */
	public void setIndexes(int[] indexes) {
		this.indexes = indexes;
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is not -1, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	public abstract void plotDataIndex(Graphics2D g2, DataList dataList, int index);

	/**
	 * Termination method to end the plot and clear or close resources. Overwrite if necessary.
	 * 
	 * @param g2 The graphics context.
	 */
	public void endPlot(Graphics2D g2) {
	}

	/**
	 * Sets the color used for a bearish line bar candle is an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @param colorBearishEven The color used for a bearish line bar candle is an even period.
	 */
	public void setColorBearishEven(Color colorBearishEven) {
		this.colorBearishEven = colorBearishEven;
	}

	/**
	 * Sets the color used for a bearish line bar candle is an odd period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 * 
	 * @param colorBearishOdd The color used for a bearish line bar candle is an odd period.
	 */
	public void setColorBearishOdd(Color colorBearishOdd) {
		this.colorBearishOdd = colorBearishOdd;
	}

	/**
	 * Sets the color used for a bullish line/bar/candle in an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @param colorBullishEven The color used for a bullish line/bar/candle in an even period.
	 */
	public void setColorBullishEven(Color colorBullishEven) {
		this.colorBullishEven = colorBullishEven;
	}

	/**
	 * Sets the color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 * 
	 * @param colorBullishOdd The color used for a bullish line/bar/candle in an odd period.
	 */
	public void setColorBullishOdd(Color colorBullishOdd) {
		this.colorBullishOdd = colorBullishOdd;
	}

	/**
	 * Returns the color used for a bearish line bar candle is an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBearishEven The color used for a bearish line bar candle is an even period.
	 */
	public Color getColorBearishEven() {
		return colorBearishEven;
	}

	/**
	 * Returns the color used for a bearish line bar candle is an odd period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBearishOdd The color used for a bearish line bar candle is an odd period.
	 */
	public Color getColorBearishOdd() {
		return colorBearishOdd;
	}

	/**
	 * Returns the color used for a bullish line/bar/candle in an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBullishEven The color used for a bullish line/bar/candle in an even period.
	 */
	public Color getColorBullishEven() {
		return colorBullishEven;
	}

	/**
	 * Returns the color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBullishOdd The color used for a bullish line/bar/candle in an odd period.
	 */
	public Color getColorBullishOdd() {
		return colorBullishOdd;
	}
	
	/**
	 * Returns a string representation of this plotter and the data it plots.
	 * 
	 * @param info The data info of the data list the plotter plots.
	 * @return A string representation.
	 */
	public String toString(DataInfo info) {
		StringBuilder b = new StringBuilder();
		StringUtils.append(b, getName());
		for (int i = 0; i < indexes.length; i++) {
			String sep = (i == 0 ? " " : ", ");
			int index = indexes[i];
			OutputInfo output = info.getOutputByDataIndex(index);
			if (output != null) {
				StringUtils.appendSep(b, output.getName(), sep);
			}
		}
		return b.toString();
	}
}
