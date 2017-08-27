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

import java.awt.Graphics2D;
import java.awt.Shape;

import com.qtplaf.library.trading.chart.plotter.PlotterContext;

/**
 * Base class of all drawings.
 * <p>
 * Drawings are responsible to return its shape given a plotter context that enables them to calculate their
 * coordinates. The shape can be used by the drawer to check, for instance, intersections.
 * <p>
 * Additionally, the drawing is responsible to draw itself in a graphics device.
 * 
 * @author Miquel Sas
 */
public abstract class Drawing {

	/**
	 * The name.
	 */
	private String name;

	/**
	 * Default constructor.
	 */
	public Drawing() {
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns this drawing shape.
	 * 
	 * @param context The plotter context.
	 * @return The shape.
	 */
	public abstract Shape getShape(PlotterContext context);

	/**
	 * Draw the candlestick.
	 * 
	 * @param g2 The graphics object.
	 * @param context The plotter context.
	 */
	public abstract void draw(Graphics2D g2, PlotterContext context);
}
