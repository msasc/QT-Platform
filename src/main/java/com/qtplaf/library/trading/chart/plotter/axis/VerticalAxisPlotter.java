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
package com.qtplaf.library.trading.chart.plotter.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.math.BigDecimal;
import java.util.List;

import com.qtplaf.library.trading.chart.parameters.VerticalAxisPlotParameters;
import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotScale;
import com.qtplaf.library.util.ColorUtils;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.NumberUtils;

/**
 * Vertical axis plotter.
 * 
 * @author Miquel Sas
 */
public class VerticalAxisPlotter extends Plotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param context The plotter context.
	 */
	public VerticalAxisPlotter(PlotterContext context) {
		super();
		setContext(context);
		setName("Vertical axis");
	}

	/**
	 * Plot th vertical axis scale of numbers.
	 * 
	 * @param g2 The graphics object.
	 */
	public void plotScale(Graphics2D g2) {

		// Plot data.
		PlotData plotData = getContext().getPlotData();

		// Retrieve the increase to apply and the decimal places to floor.
		BigDecimal increase = getIncreaseValue(g2, plotData.getMaximumValue());
		if (increase == null) {
			return;
		}
		int floorScale = increase.scale();
		int pipScale = plotData.getPipScale();

		// Iterate starting at the floor of the maximum value until the minimum value would be passed.
		double maximumValue =
			new BigDecimal(plotData.getMaximumValue()).setScale(pipScale - 1, BigDecimal.ROUND_FLOOR).doubleValue();
		double minimumValue = plotData.getMinimumValue();
		double plotValue = NumberUtils.floor(maximumValue, floorScale);

		PlotScale plotScale = plotData.getPlotScale();
		while (plotValue > minimumValue) {
			int y = getContext().getCoordinateY(plotValue);
			plotValue(g2, y, plotValue, pipScale, null);
			if (plotScale.equals(PlotScale.Logarithmic)) {
				increase = getIncreaseValue(g2, plotValue);
			}
			if (increase == null) {
				break;
			}
			plotValue -= increase.doubleValue();
		}
	}

	/**
	 * Draw the cursor value with the small line, surrounded by a rectangle.
	 * 
	 * @param g2 The graphics.
	 * @param y The y coordinate.
	 */
	public void plotCursorValue(Graphics2D g2, int y) {
		double value = getContext().getDataValue(y);
		int scale = getContext().getPlotData().getTickScale();
		plotValue(g2, y, value, scale, getPlotParameters().getVerticalAxisSurroundFillColor());
	}

	/**
	 * Draw a vertical axis value.
	 * 
	 * @param g2 The graphics.
	 * @param y The y coordinate.
	 * @param value The value to plot.
	 * @param scale The scale.
	 * @param surroundColor The surround color or null if not surrounded.
	 */
	private void plotValue(Graphics2D g2, int y, double value, int scale, Color surroundColor) {

		// Plot parameters.
		VerticalAxisPlotParameters plotParameters = getPlotParameters();

		// Save color, stroke, font.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();
		Font saveFont = g2.getFont();

		// Draw the small line.
		g2.setColor(plotParameters.getVerticalAxisLineColor());
		g2.drawLine(0, y, plotParameters.getVerticalAxisLineLength() - 1, y);

		// Set the font and retrieve the font metrics.
		g2.setFont(plotParameters.getVerticalAxisTextFont());
		FontMetrics fm = g2.getFontMetrics();

		// The string to draw and its coordinates.
		String strValue = FormatUtils.formattedFromDouble(value, scale, getContext().getSession().getLocale());
		int xStrValue =
			plotParameters.getVerticalAxisLineLength() + plotParameters.getVerticalAxisTextInsets().left - 1;
		int ascent = fm.getAscent();
		int descent = fm.getDescent();
		int yStrValue = y + (ascent / 2) - (descent / 2);

		// If need to surround it.
		if (surroundColor != null) {
			Insets insetsSurround = plotParameters.getVerticalAxisSurroundInsets();
			int xRectSurround = xStrValue - insetsSurround.left;
			int yRectSurround = yStrValue - ascent - insetsSurround.top + (descent / 2);
			int widthRectSurround = insetsSurround.left + fm.stringWidth(strValue) + insetsSurround.right;
			int heightRectSurround = insetsSurround.top + fm.getAscent() + insetsSurround.bottom;
			Rectangle rect = new Rectangle(xRectSurround, yRectSurround, widthRectSurround, heightRectSurround);
			g2.setColor(plotParameters.getVerticalAxisSurroundFillColor());
			g2.fill(rect);
			g2.setColor(Color.BLACK);
			g2.setStroke(plotParameters.getVerticalAxisSurroundStroke());
			g2.draw(rect);
		}

		// The color of the text is black or white depending on the darkness of the surround color if present.
		Color textColor = Color.BLACK;
		if (surroundColor != null) {
			if (ColorUtils.darkness(surroundColor) > 0.5) {
				textColor = Color.WHITE;
			}
		}
		g2.setColor(textColor);

		// Draw the string.
		g2.drawString(strValue, xStrValue, yStrValue);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
		g2.setFont(saveFont);
	}

	/**
	 * Returns the vertical axis plot parameters.
	 * 
	 * @return The vertical axis plot parameters.
	 */
	private VerticalAxisPlotParameters getPlotParameters() {
		return getContext().getVerticalAxisPlotParameters();
	}

	/**
	 * Returns the value by which should be increased an initial value to plot the rounded values.
	 * 
	 * @param g2 The graphics object.
	 * @param value The starting value.
	 * @return The increase value.
	 */
	private BigDecimal getIncreaseValue(Graphics2D g2, double value) {

		// Calculate the minimum line height to not overlap text adding some padding.
		FontMetrics fm = g2.getFontMetrics(getPlotParameters().getVerticalAxisTextFont());
		int minimumHeight = (int) (fm.getHeight() * 1.5);

		// The maximum value and its y coordinate.
		int integerDigits = NumberUtils.getDigits(value);
		int decimalDigits = getContext().getPlotData().getPipScale();
		int y = getContext().getCoordinateY(value);

		// The list of increases.
		List<BigDecimal> increases = NumberUtils.getIncreases(integerDigits, decimalDigits, 1, 2, 5);

		// Take the first increase that do not overlaps the text.
		for (BigDecimal increase : increases) {
			double nextValue = value - increase.doubleValue();
			int nextY = getContext().getCoordinateY(nextValue);
			if (nextY - y >= minimumHeight) {
				return increase;
			}
		}

		// Return null to indicate that it is not valid.
		return null;
	}
}
