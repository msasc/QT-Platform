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
 * Vertical axis plot parameters.
 *
 * @author Miquel Sas
 */
public class VerticalAxisPlotParameters {

	/**
	 * Vertical axis: the line color.
	 */
	private Color verticalAxisLineColor = Color.BLACK;
	/**
	 * Vertical axis: the length of the small line before each value.
	 */
	private int verticalAxisLineLength = 5;
	/**
	 * The surround cursor value fill color.
	 */
	private Color verticalAxisSurroundFillColor = new Color(224, 224, 224);
	/**
	 * Vertical axis: the insets to surround the price with a rectangle.
	 */
	private Insets verticalAxisSurroundInsets = new Insets(1, 4, 1, 4);
	/**
	 * Vertical axis: the stroke for the surround rectangle.
	 */
	private Stroke verticalAxisSurroundStroke = new BasicStroke();
	/**
	 * Vertical axis: the vertical text font.
	 */
	private Font verticalAxisTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * Vertical axis: insets of the text.
	 */
	private Insets verticalAxisTextInsets = new Insets(5, 8, 5, 8);

	/**
	 * Constructor assigning ther parent chart.
	 */
	public VerticalAxisPlotParameters() {
		super();
	}

	/**
	 * Returns the vertical axis line color.
	 * 
	 * @return The vertical axis line color.
	 */
	public Color getVerticalAxisLineColor() {
		return verticalAxisLineColor;
	}

	/**
	 * Returns the vertical axis small line length.
	 * 
	 * @return The vertical axis small line length.
	 */
	public int getVerticalAxisLineLength() {
		return verticalAxisLineLength;
	}

	/**
	 * Returns the vertical axis surround fill color for te cursor value. Other surround color for averages and so on
	 * will be taken from the plot color of the indicator.
	 * 
	 * @return The vertical axis surround fill color for te cursor value.
	 */
	public Color getVerticalAxisSurroundFillColor() {
		return verticalAxisSurroundFillColor;
	}

	/**
	 * Returns the vertical axis surround insets.
	 * 
	 * @return The vertical axis surround insets.
	 */
	public Insets getVerticalAxisSurroundInsets() {
		return verticalAxisSurroundInsets;
	}

	/**
	 * Returns the vertical axis surround stroke.
	 * 
	 * @return The vertical axis surround stroke.
	 */
	public Stroke getVerticalAxisSurroundStroke() {
		return verticalAxisSurroundStroke;
	}

	/**
	 * Returns the vertical axis text font.
	 * 
	 * @return The vertical axis text font.
	 */
	public Font getVerticalAxisTextFont() {
		return verticalAxisTextFont;
	}

	/**
	 * Returns the vertical axis text insets.
	 * 
	 * @return The vertical axis text insets.
	 */
	public Insets getVerticalAxisTextInsets() {
		return verticalAxisTextInsets;
	}

	/**
	 * Set the vertical axis small line color.
	 * 
	 * @param vertiicalAxisLineColor The vertical axis small line color.
	 */
	public void setVerticalAxisLineColor(Color vertiicalAxisLineColor) {
		this.verticalAxisLineColor = vertiicalAxisLineColor;
	}

	/**
	 * Sets the vertical axis small line length.
	 * 
	 * @param verticalAxisLineLength The vertical axis small line length.
	 */
	public void setVerticalAxisLineLength(int verticalAxisLineLength) {
		this.verticalAxisLineLength = verticalAxisLineLength;
	}

	/**
	 * Sets the vertical axis surround fill color for te cursor value. Other surround color for averages and so on will
	 * be taken from the plot color of the indicator.
	 * 
	 * @param verticalAxisSurroundFillColor The vertical axis surround fill color for te cursor value.
	 */
	public void setVerticalAxisSurroundFillColor(Color verticalAxisSurroundFillColor) {
		this.verticalAxisSurroundFillColor = verticalAxisSurroundFillColor;
	}

	/**
	 * Sets the vertical axis surround insets.
	 * 
	 * @param verticalAxisSurroundInsets The vertical axis surround insets.
	 */
	public void setVerticalAxisSurroundInsets(Insets verticalAxisSurroundInsets) {
		this.verticalAxisSurroundInsets = verticalAxisSurroundInsets;
	}

	/**
	 * Sets the vertical axis surround stroke.
	 * 
	 * @param verticalAxisSurroundStroke The vertical axis surround stroke.
	 */
	public void setVerticalAxisSurroundStroke(Stroke verticalAxisSurroundStroke) {
		this.verticalAxisSurroundStroke = verticalAxisSurroundStroke;
	}

	/**
	 * Sets the vertical axis text font.
	 * 
	 * @param verticalAxisTextFont The vertical axis text font.
	 */
	public void setVerticalAxisTextFont(Font verticalAxisTextFont) {
		this.verticalAxisTextFont = verticalAxisTextFont;
	}

	/**
	 * Sets the vertical axis text insets.
	 * 
	 * @param verticalAxisTextInsets The vertical axis text insets.
	 */
	public void setVerticalAxisTextInsets(Insets verticalAxisTextInsets) {
		this.verticalAxisTextInsets = verticalAxisTextInsets;
	}

}
