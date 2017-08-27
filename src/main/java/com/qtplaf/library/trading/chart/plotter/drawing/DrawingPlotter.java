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

package com.qtplaf.library.trading.chart.plotter.drawing;

import java.awt.Graphics2D;

import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.util.Properties;

/**
 * A plotter for generic drawings. Drawings contain the necessary information to be plotted.
 *
 * @author Miquel Sas
 */
public abstract class DrawingPlotter extends Plotter {

	/** Drawing necessary properties. */
	private Properties properties = new Properties();

	/**
	 * Constructor.
	 */
	public DrawingPlotter() {
		super();
	}

	/**
	 * Returns the properties.
	 * 
	 * @return The properties.
	 */
	protected Properties getProperties() {
		return properties;
	}
	
	/**
	 * Plot the underlying drawing.
	 * 
	 * @param g2 The graphics object.
	 */
	public abstract void plot(Graphics2D g2);
}
