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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.trading.chart.drawings.Line;
import com.qtplaf.library.trading.data.DataList;

/**
 * Line plotter for data list items.
 * 
 * @author Miquel Sas
 */
public class BufferedLinePlotter extends DataPlotter {

	/**
	 * The line buffer.
	 */
	private List<Line> lineBuffer = new ArrayList<>();
	/**
	 * The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	private Stroke stroke = new BasicStroke(0f);

	/**
	 * Constructor.
	 */
	public BufferedLinePlotter() {
		super();
		setName("Line");
		setColorBearishEven(Color.BLACK);
		setColorBearishOdd(Color.BLACK);
		setColorBullishEven(Color.BLACK);
		setColorBullishOdd(Color.BLACK);
	}

	/**
	 * Set the indexes within the data element used by the plotter. For a line plotter, only one index is accepted.
	 * 
	 * @param indexes The indexes within the data element used by the plotter.
	 */
	@Override
	public void setIndexes(int[] indexes) {
		if (indexes.length != 1) {
			throw new IllegalArgumentException();
		}
		super.setIndexes(indexes);
	}

	/**
	 * Set the index.
	 * 
	 * @param index The index to get the value within the data.
	 */
	public void setIndex(int index) {
		setIndexes(new int[] { index });
	}

	/**
	 * Returns the index within the data.
	 * 
	 * @return The index within the data.
	 */
	public int getIndex() {
		return getIndexes()[0];
	}

	/**
	 * Check if a given line can be buffered in agiven buffer, because the stroke and the color are the same.
	 * 
	 * @param line The line.
	 * @return A boolean that indicates if a given line can be buffered in agiven buffer, because the stroke and the
	 *         color are the same.
	 */
	private boolean canBuffer(Line line) {
		if (lineBuffer.isEmpty()) {
			return true;
		}
		Line check = lineBuffer.get(0);
		if (check.getParameters().getStroke().equals(line.getParameters().getStroke()) &&
			check.getParameters().getColor().equals(line.getParameters().getColor())) {
			return true;
		}
		return false;
	}

	/**
	 * Add a line to a buffer.
	 * 
	 * @param line The line.
	 */
	private void bufferLine(Line line) {
		lineBuffer.add(line);
	}

	/**
	 * Plots the given buffer and clears it.
	 * 
	 * @param g2 The graphics context.
	 */
	private void plotBuffer(Graphics2D g2) {

		// If nothing to plot...
		if (lineBuffer.isEmpty()) {
			return;
		}

		// Build a path appending the line shapes.
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, lineBuffer.size());
		for (Line line : lineBuffer) {
			shape.append(line.getShape(getContext()), true);
		}

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			lineBuffer.clear();
			return;
		}

		// Save the color and stroke
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set color and stroke.
		g2.setColor(lineBuffer.get(0).getParameters().getColor());
		g2.setStroke(lineBuffer.get(0).getParameters().getStroke());

		// Do plot.
		g2.draw(shape);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);

		// Clear the buffer.
		lineBuffer.clear();
	}

	/**
	 * Returns the line for the given data list and index.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return An array of lines for the given data list and index.
	 */
	private Line getLine(DataList dataList, int index) {

		// Current and previous indexes. The previous index must the first previous valid. If no one found, skip
		// plotting.
		int index1 = index - 1;
		int index2 = index;
		while (!dataList.get(index1).isValid()) {
			index1--;
			if (index1 < 0) {
				break;
			}
		}
		// Index previous is less than 0, not foound.
		if (index1 < 0) {
			return null;
		}

		// Odd/even period.
		boolean odd = dataList.isOdd(index);
		
		// Color bullish or bearish depending on odds.
		Color colorBullish;
		Color colorBearish;
		if (odd) {
			colorBullish = getColorBullishOdd();
			colorBearish = getColorBearishOdd();
		} else {
			colorBullish = getColorBullishEven();
			colorBearish = getColorBearishEven();
		}

		// Current and previous values to plot, depending on the data type of the list.
		double value1 = dataList.get(index1).getValue(getIndex());
		double value2 = dataList.get(index2).getValue(getIndex());
		
		// Bullish/bearish.
		boolean bullish = (value2 >= value1);
		Color color = (bullish ? colorBullish : colorBearish);
		
		// Stroke.
		Stroke stroke = getStroke();

		// The line.
		Line line = new Line(index1, value1, index2, value2);
		line.getParameters().setStroke(stroke);
		line.getParameters().setColor(color);

		return line;
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

		// To plot a line we need the current index and the previous, so index must be greater than or equal to 1.
		if (index < 1) {
			return;
		}
		// The same for the start index.
		int startIndex = getContext().getPlotData().getStartIndex();
		if (index < startIndex + 1) {
			return;
		}

		// The line.
		Line line = getLine(dataList, index);
		
		// Do plot or buffer.
		if (!canBuffer(line)) {
			plotBuffer(g2);
		}
		bufferLine(line);
	}

	/**
	 * Termination method to end the plot and clear or close resources.
	 * <p>
	 * Plot the remaining lines in line buffer and clear them.
	 * 
	 * @param g2 The graphics context.
	 */
	@Override
	public void endPlot(Graphics2D g2) {
		plotBuffer(g2);
	}

	/**
	 * Returns the stroke that applies to lines, bars and candlesticks and histograms borders.
	 * 
	 * @return The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Sets the stroke that applies to lines, bars and candlesticks and histograms borders.
	 * 
	 * @param stroke The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}