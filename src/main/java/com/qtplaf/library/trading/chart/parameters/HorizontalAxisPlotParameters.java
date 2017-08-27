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
import java.awt.Font;
import java.awt.Insets;
import java.awt.Stroke;

/**
 * Horizontal axis plot parameters.
 *
 * @author Miquel Sas
 */
public class HorizontalAxisPlotParameters {

	/**
	 * Horizontal axis font.
	 */
	private Font horizontalAxisTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * Horizontal axis: the line and text color.
	 */
	private Color horizontalAxisColor = Color.GRAY;
	/**
	 * Horizontal axis: the stroke for the surround rectangle.
	 */
	private Stroke horizontalAxisLineStroke = new BasicStroke();
	/**
	 * Horizontal axis: insets of the text.
	 */
	private Insets horizontalAxisTextInsets = new Insets(3, 2, 3, 2);
	/**
	 * Horizontal axis height.
	 */
	private int horizontalAxisHeight = 40;

	/**
	 * Constructor assigning ther parent chart.
	 */
	public HorizontalAxisPlotParameters() {
		super();
	}

	/**
	 * Returns the horizontal axis text font.
	 * 
	 * @return The horizontal axis text font.
	 */
	public Font getHorizontalAxisTextFont() {
		return horizontalAxisTextFont;
	}

	/**
	 * Sets the horizontal axis text font.
	 * 
	 * @param horizontalAxisTextFont The horizontal axis text font.
	 */
	public void setHorizontalAxisTextFont(Font horizontalAxisTextFont) {
		this.horizontalAxisTextFont = horizontalAxisTextFont;
	}

	/**
	 * Returns the horizontal axis color for text and lines.
	 * 
	 * @return The horizontal axis color for text and lines.
	 */
	public Color getHorizontalAxisColor() {
		return horizontalAxisColor;
	}

	/**
	 * Returns the horizontal axis line stroke.
	 * 
	 * @return The horizontal axis line stroke.
	 */
	public Stroke getHorizontalAxisLineStroke() {
		return horizontalAxisLineStroke;
	}

	/**
	 * Returns the horizontal axis text insets.
	 * 
	 * @return The horizontal axis text insets.
	 */
	public Insets getHorizontalAxisTextInsets() {
		return horizontalAxisTextInsets;
	}

	/**
	 * Sets the horizontal axis color for text and lines.
	 * 
	 * @param horizontalAxisColor The horizontal axis color for text and lines.
	 */
	public void setHorizontalAxisColor(Color horizontalAxisColor) {
		this.horizontalAxisColor = horizontalAxisColor;
	}

	/**
	 * Sets the horizontal axis line stroke.
	 * 
	 * @param horizontalAxisLineStroke The horizontal axis line stroke.
	 */
	public void setHorizontalAxisLineStroke(Stroke horizontalAxisLineStroke) {
		this.horizontalAxisLineStroke = horizontalAxisLineStroke;
	}

	/**
	 * Sets the horizontal axis text insets.
	 * 
	 * @param horizontalAxisTextInsets The horizontal axis text insets.
	 */
	public void setHorizontalAxisTextInsets(Insets horizontalAxisTextInsets) {
		this.horizontalAxisTextInsets = horizontalAxisTextInsets;
	}

	/**
	 * Returns the horizontal axis height.
	 * 
	 * @return The horizontal axis height.
	 */
	public int getHorizontalAxisHeight() {
		return horizontalAxisHeight;
	}

	/**
	 * Sets the horizontal axis height.
	 * 
	 * @param horizontalAxisHeight The horizontal axis height.
	 */
	public void setHorizontalAxisHeight(int horizontalAxisHeight) {
		this.horizontalAxisHeight = horizontalAxisHeight;
	}

}
