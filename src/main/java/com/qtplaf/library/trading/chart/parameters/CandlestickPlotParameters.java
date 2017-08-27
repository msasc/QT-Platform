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

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * Candlestick plot parameters.
 *
 * @author Miquel Sas
 */
public class CandlestickPlotParameters {

	/**
	 * Default candlestick border stroke.
	 */
	private BasicStroke stroke = new BasicStroke();
	/**
	 * The border color applies only to candlesticks and histograms.
	 */
	private Color borderColor = Color.BLACK;
	/**
	 * A boolean that indicates if the border with the specified border color, that applies only to candlesticks and
	 * histograms, should be painted. Explicitly set although it could be deduced if the border color is null.
	 */
	private boolean paintBorder = false;
	/**
	 * A boolean that indicates if the color in candlesticks and histograms should be raised.
	 */
	private boolean colorRaised = true;
	/**
	 * The brightness factor to apply for raised colors.
	 */
	private double brightnessFactor = 0.95;
	/**
	 * Fill color.
	 */
	private Color fillColor = Color.WHITE;

	/**
	 * Constructor.
	 */
	public CandlestickPlotParameters() {
		super();
	}

	/**
	 * Returns the border stroke
	 * 
	 * @return The border stroke.
	 */
	public BasicStroke getStroke() {
		return stroke;
	}

	/**
	 * @param borderStroke the borderStroke to set
	 */
	public void setStroke(BasicStroke borderStroke) {
		this.stroke = borderStroke;
	}

	/**
	 * Returns the border color that applies only to candlesticks and histograms.
	 * 
	 * @return the colorBorder The border color.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the border color that applies only to candlesticks and histograms.
	 * 
	 * @param colorBorder The border color.
	 */
	public void setBorderColor(Color colorBorder) {
		this.borderColor = colorBorder;
	}

	/**
	 * Returns a boolean indicating if the border with the specified border color, should be painted. Applies only to
	 * candlesticks and histograms and is explicitly set although it could be deduced setting the border color to null.
	 * 
	 * @return A boolean that indicates if the border color should be painted.
	 */
	public boolean isPaintBorder() {
		return paintBorder;
	}

	/**
	 * Set a boolean indicating if the border with the specified border color, should be painted. Applies only to
	 * candlesticks and histograms and is explicitly set although it could be deduced setting the border color to null.
	 * 
	 * @param paintBorder A boolean that indicates if the border color should be painted.
	 */
	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}

	/**
	 * Sets a boolean indicating if the color should be raised in candlesticks and histograms.
	 * 
	 * @return A boolean indicating if the color should be raised in candlesticks and histograms.
	 */
	public boolean isColorRaised() {
		return colorRaised;
	}

	/**
	 * Sets a boolean indicating if the color should be raised in candlesticks and histograms.
	 * 
	 * @param colorRaised A boolean indicating if the color should be raised in candlesticks and histograms.
	 */
	public void setColorRaised(boolean colorRaised) {
		this.colorRaised = colorRaised;
	}

	/**
	 * Returns the brightness factor.
	 * 
	 * @return The brightness factor.
	 */
	public double getBrightnessFactor() {
		return brightnessFactor;
	}

	/**
	 * Sets the brightness factor.
	 * 
	 * @param brightnessFactor The brightness factor.
	 */
	public void setBrightnessFactor(double brightnessFactor) {
		if (brightnessFactor <= 0 || brightnessFactor >= 1) {
			throw new IllegalArgumentException("Brightness factor must be > 0 and < 1");
		}
		this.brightnessFactor = brightnessFactor;
	}

	/**
	 * Returns the fill color.
	 * 
	 * @return The fill color.
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Set the fill color.
	 * 
	 * @param fillColor The fill color.
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
}
