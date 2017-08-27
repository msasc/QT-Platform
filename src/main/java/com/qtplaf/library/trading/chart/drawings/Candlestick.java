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
package com.qtplaf.library.trading.chart.drawings;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.qtplaf.library.trading.chart.parameters.CandlestickPlotParameters;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.util.ColorUtils;

/**
 * A candlestick drawing.
 * 
 * @author Miquel Sas
 */
public class Candlestick extends DataDrawing {

	/** Plot parameters. */
	private CandlestickPlotParameters parameters;
	/** Indexes to retrieve data. */
	private int[] indexes;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param data The data.
	 * @param indexes The indexes to retrieve values.
	 * @param parameters Plot parameters.
	 */
	public Candlestick(int index, Data data, int[] indexes, CandlestickPlotParameters parameters) {
		super(index, data);
		this.indexes = indexes;
		this.parameters = parameters;
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
	 * Check if this bar or candlestick is bullish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bullish.
	 */
	public boolean isBullish() {
		return Data.isBullish(getData());
	}

	/**
	 * Check if this bar or candlestick is bearish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bearish.
	 */
	public boolean isBearish() {
		return Data.isBearish(getData());
	}

	/**
	 * Returns the candlestick shape.
	 * 
	 * @param context The plotter context.
	 * @return The candlestick shape.
	 */
	@Override
	public Shape getShape(PlotterContext context) {
		// The values to plot.
		Data data = getData();
		double open = data.getValue(indexes[0]);
		double high = data.getValue(indexes[1]);
		double low = data.getValue(indexes[2]);
		double close = data.getValue(indexes[3]);

		// The X coordinate to start painting.
		int x = context.getCoordinateX(getIndex());

		// And the Y coordinate for each value.
		int openY = context.getCoordinateY(open);
		int highY = context.getCoordinateY(high);
		int lowY = context.getCoordinateY(low);
		int closeY = context.getCoordinateY(close);

		// The X coordinate of the vertical line, either the candle.
		int candlestickWidth = context.getDataItemWidth();
		int verticalLineX = context.getDrawingCenterCoordinateX(x);

		// The bar candle is bullish/bearish.
		boolean bullish = Data.isBullish(data);

		// The candlestick shape.
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
		// If bar width is 1...
		if (candlestickWidth == 1) {
			// The vertical line only.
			shape.moveTo(verticalLineX, highY);
			shape.lineTo(verticalLineX, lowY);
		} else {
			if (bullish) {
				// Upper shadow.
				shape.moveTo(verticalLineX, highY);
				shape.lineTo(verticalLineX, closeY - 1);
				// Body.
				shape.moveTo(x, closeY);
				shape.lineTo(x + candlestickWidth - 1, closeY);
				shape.lineTo(x + candlestickWidth - 1, openY);
				shape.lineTo(x, openY);
				shape.lineTo(x, closeY);
				// Lower shadow.
				shape.moveTo(verticalLineX, openY + 1);
				shape.lineTo(verticalLineX, lowY);
			} else {
				// Upper shadow.
				shape.moveTo(verticalLineX, highY);
				shape.lineTo(verticalLineX, openY - 1);
				// Body.
				shape.moveTo(x, openY);
				shape.lineTo(x + candlestickWidth - 1, openY);
				shape.lineTo(x + candlestickWidth - 1, closeY);
				shape.lineTo(x, closeY);
				shape.lineTo(x, openY);
				// Lower shadow.
				shape.moveTo(verticalLineX, closeY + 1);
				shape.lineTo(verticalLineX, lowY);
			}
		}

		return shape;
	}

	/**
	 * Draw the candlestick.
	 * 
	 * @param g2 The graphics object.
	 * @param context The plotter context.
	 */
	@Override
	public void draw(Graphics2D g2, PlotterContext context) {
		
		// The shape.
		Shape shape = getShape(context);

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set the stroke.
		g2.setStroke(getParameters().getStroke());

		// Once defined the path the paint strategy will depend on whether the border if painted or not, and whether
		// the color is raised or not.
		Color color = getParameters().getFillColor();
		if (getParameters().isPaintBorder()) {
			if (getParameters().isColorRaised()) {
				// Create a raised color.
				Data data = getData();
				double open = Data.getOpen(data);
				double close = Data.getClose(data);
				int candlestickWidth = context.getDataItemWidth();
				int x = context.getCoordinateX(getIndex());
				int yOpen = context.getCoordinateY(open);
				int yClose = context.getCoordinateY(close);
				Color colorRaised = ColorUtils.brighter(color, getParameters().getBrightnessFactor());
				Point2D pt1;
				Point2D pt2;
				if (isBullish()) {
					pt1 = new Point2D.Float(x, yClose);
					pt2 = new Point2D.Float(x + candlestickWidth - 1, yClose);
				} else {
					pt1 = new Point2D.Float(x, yOpen);
					pt2 = new Point2D.Float(x + candlestickWidth - 1, yOpen);
				}
				GradientPaint raisedPaint = new GradientPaint(pt1, colorRaised, pt2, color, true);
				g2.setPaint(raisedPaint);
				g2.fill(shape);
			} else {
				// Set the fill color and do fill.
				g2.setPaint(color);
				g2.fill(shape);
			}
			// Set the border color and draw the path.
			g2.setPaint(getParameters().getBorderColor());
			g2.draw(shape);
		} else {
			// Set the fill color and do fill.
			g2.setPaint(color);
			g2.fill(shape);
			g2.draw(shape);
		}

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}
}
