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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

/**
 * Parameters to plot information in the chart.
 *
 * @author Miquel Sas
 */
public class InformationPlotParameters {

	/**
	 * The brightness factor to apply to the background color for non selected chart.
	 */
	private double infoBackgroundBrightnessFactor = 0.7;
	/**
	 * The background color when the info panel gains focus.
	 */
	private Color infoBackgroundColor = new Color(208, 208, 208);
	/**
	 * Chart info: the infomation separator color.
	 */
	private Color infoSeparatorColor = Color.BLACK;
	/**
	 * Chart info: the information separator string.
	 */
	private String infoSeparatorString = " - ";
	/**
	 * Chart info: the information text font.
	 */
	private Font infoTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * Chart info: the information text insets.
	 */
	private Insets infoTextInsets = new Insets(1, 2, 1, 0);

	/**
	 * Constructor.
	 */
	public InformationPlotParameters() {
		super();
	}

	/**
	 * Returns the panel info background color brightness factor.
	 * 
	 * @return The panel info background color brightness factor.
	 */
	public double getInfoBackgroundBrightnessFactor() {
		return infoBackgroundBrightnessFactor;
	}

	/**
	 * Returns the panel info background color when focus is gained, when the focus is lost the color is brigthner.
	 * 
	 * @return the infoBackgroundColor The panel info background.
	 */
	public Color getInfoBackgroundColor() {
		return infoBackgroundColor;
	}

	/**
	 * Returns the chart info separator color.
	 * 
	 * @return The chart info separator color.
	 */
	public Color getInfoSeparatorColor() {
		return infoSeparatorColor;
	}

	/**
	 * Returns the chart info separator string.
	 * 
	 * @return The chart info separator string.
	 */
	public String getInfoSeparatorString() {
		return infoSeparatorString;
	}

	/**
	 * Returns the info text font.
	 * 
	 * @return The info text font.
	 */
	public Font getInfoTextFont() {
		return infoTextFont;
	}

	/**
	 * Returns the chart info text insets.
	 * 
	 * @return The chart info text insets.
	 */
	public Insets getInfoTextInsets() {
		return infoTextInsets;
	}

	/**
	 * Sets the panel info background color brightness factor.
	 * 
	 * @param infoBackgroundBrightnessFactor The panel info background color brightness factor.
	 */
	public void setInfoBackgroundBrightnessFactor(double infoBackgroundBrightnessFactor) {
		this.infoBackgroundBrightnessFactor = infoBackgroundBrightnessFactor;
	}

	/**
	 * Set the panel info background
	 * 
	 * @param infoBackgroundColor The panel info background
	 */
	public void setInfoBackgroundColor(Color infoBackgroundColor) {
		this.infoBackgroundColor = infoBackgroundColor;
	}

	/**
	 * Sets the chart info separator color.
	 * 
	 * @param infoSeparatorColor The color for the chart info separator string.
	 */
	public void setInfoSeparatorColor(Color infoSeparatorColor) {
		this.infoSeparatorColor = infoSeparatorColor;
	}

	/**
	 * Sets the chsrt info separator string.
	 * 
	 * @param infoSeparatorString The string that separates info items in the info chart.
	 */
	public void setInfoSeparatorString(String infoSeparatorString) {
		this.infoSeparatorString = infoSeparatorString;
	}

	/**
	 * Sets the info text font.
	 * 
	 * @param infoTextFont The font to set.
	 */
	public void setInfoTextFont(Font infoTextFont) {
		this.infoTextFont = infoTextFont;
	}

	/**
	 * Sets the info text insets.
	 * 
	 * @param infoTextInsets Information text insets..
	 */
	public void setInfoTextInsets(Insets infoTextInsets) {
		this.infoTextInsets = infoTextInsets;
	}

}
