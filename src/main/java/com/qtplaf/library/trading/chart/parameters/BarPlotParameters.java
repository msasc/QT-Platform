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
 * Bar plot parameters.
 *
 * @author Miquel Sas
 */
public class BarPlotParameters {

	/**
	 * Stroke.
	 */
	private BasicStroke stroke = new BasicStroke();
	/**
	 * Color.
	 */
	private Color color = Color.BLACK;

	/**
	 * 
	 */
	public BarPlotParameters() {
		super();
	}

	/**
	 * Returns the stroke.
	 * 
	 * @return The stroke.
	 */
	public BasicStroke getStroke() {
		return stroke;
	}

	/**
	 * Set the stroke.
	 * 
	 * @param stroke The bar stroke.
	 */
	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
	}

	/**
	 * Returns the color.
	 * 
	 * @return The color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the color.
	 * 
	 * @param color The color.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
