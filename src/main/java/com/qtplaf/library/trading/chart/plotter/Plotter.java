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
package com.qtplaf.library.trading.chart.plotter;

import java.awt.Rectangle;

import com.qtplaf.library.trading.data.PlotData;

/**
 * Base abstract class of all plotter subclasses. Note that the frame for the plot data must have been calculated by a
 * call to <i>PlotData.calculateFrame</i> prior to any plot operation, except for those repaints in a small clip bounds
 * that do not modify the frame maximum and minimum.
 * <p>
 * This base plotter primarily offers the methods to calculate coordinates from values and values from coordinates.
 * 
 * @author Miquel Sas
 */
public class Plotter {

	/**
	 * The plotter context.
	 */
	private PlotterContext context;
	/**
	 * The name of the plotter.
	 */
	private String name;

	/**
	 * Default constructor.
	 */
	public Plotter() {
		super();
	}

	/**
	 * Returns the plotter context.
	 * 
	 * @return The plotter context.
	 */
	public PlotterContext getContext() {
		return context;
	}

	/**
	 * Sets the plotter context.
	 * 
	 * @param context The plotter context.
	 */
	public void setContext(PlotterContext context) {
		this.context = context;
	}

	/**
	 * Returns the plotter name. Every defined plotter should have an unique name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the plotter name. Every defined plotter should have an unique name.
	 * 
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the plot data.
	 * 
	 * @return The plot data.
	 */
	public PlotData getPlotData() {
		return getContext().getPlotData();
	}

	/**
	 * Return the bounds for intersection increased by a unit, because intersection looks for the interior of a shape.
	 * 
	 * @param rect The source rectangle.
	 * @return The rectangle for intersection.
	 */
	public static Rectangle getIntersectionBounds(Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width + 4, rect.height + 4);
	}
}
