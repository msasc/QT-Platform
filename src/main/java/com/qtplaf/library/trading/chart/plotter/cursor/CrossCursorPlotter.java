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
package com.qtplaf.library.trading.chart.plotter.cursor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import com.qtplaf.library.trading.chart.drawings.CrossCursor;
import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;

/**
 * A cursor plotter.
 * 
 * @author Miquel Sas
 */
public class CrossCursorPlotter extends Plotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param context The plotter context.
	 */
	public CrossCursorPlotter(PlotterContext context) {
		super();
		setContext(context);
		setName("Cross cursor");
	}

	/**
	 * Plot a cross cursor.
	 * 
	 * @param g2 The graphics context.
	 * @param crossCursor The cross cursor.
	 */
	public void plot(Graphics2D g2, CrossCursor crossCursor) {

		// The shape.
		Shape shape = crossCursor.getShape(getContext());

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set color and stroke.
		g2.setColor(crossCursor.getColor());
		g2.setStroke(crossCursor.getStroke());

		// Draw it.
		g2.draw(shape);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}
}
