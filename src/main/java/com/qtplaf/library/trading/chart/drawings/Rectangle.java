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
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.parameters.RectanglePlotParameters;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.util.NumberUtils;

/**
 * A rectangle drawing.
 * 
 * @author Miquel Sas
 */
public class Rectangle extends Drawing {

	/** Index 1 (start). */
	private int index1;
	/** Value 1 (start). */
	private double v1;
	/** Index 2 (end). */
	private int index2;
	/** Value 2 (end). */
	private double v2;
	/** Plot parameters. */
	private RectanglePlotParameters parameters = new RectanglePlotParameters();

	/**
	 * Constructor assigning the values, with a default stroke and color.
	 * 
	 * @param index1 Index 1 (start).
	 * @param v1 Value 1.
	 * @param index2 Index 2 (end).
	 * @param v2 Value 2.
	 */
	public Rectangle(int index1, double v1, int index2, double v2) {
		super();
		this.index1 = index1;
		this.v1 = v1;
		this.index2 = index2;
		this.v2 = v2;
		setName("Rectangle");
	}

	/**
	 * Returns the index 1 (start).
	 * 
	 * @return The index 1 (start).
	 */
	public int getIndex1() {
		return index1;
	}

	/**
	 * Returns the index 2 (end).
	 * 
	 * @return The index 2 (end).
	 */
	public int getIndex2() {
		return index2;
	}

	/**
	 * Returns the vaue 1.
	 * 
	 * @return The value 1.
	 */
	public double getV1() {
		return v1;
	}

	/**
	 * Returns the vaue 2.
	 * 
	 * @return The value 2.
	 */
	public double getV2() {
		return v2;
	}

	/**
	 * Returns the plot parameters.
	 * 
	 * @return The plot parameters.
	 */
	public RectanglePlotParameters getParameters() {
		return parameters;
	}

	/**
	 * Set the plot parameters.
	 * 
	 * @param parameters The plot parameters.
	 */
	public void setParameters(RectanglePlotParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the line shape.
	 * 
	 * @param context The plotter context.
	 * @return The line shape.
	 */
	@Override
	public Shape getShape(PlotterContext context) {

		int index1 = this.index1;
		int index2 = this.index2;
		double v1 = this.v1;
		double v2 = this.v2;

		// Vertical band.
		if (v1 == NumberUtils.MAX_DOUBLE) {
			v1 = context.getPlotData().getMaximumValue();
		}
		if (v1 == NumberUtils.MIN_DOUBLE) {
			v1 = context.getPlotData().getMinimumValue();
		}
		if (v2 == NumberUtils.MAX_DOUBLE) {
			v2 = context.getPlotData().getMaximumValue();
		}
		if (v2 == NumberUtils.MIN_DOUBLE) {
			v2 = context.getPlotData().getMinimumValue();
		}

		// Horizontal band.
		if (index1 == NumberUtils.MAX_INTEGER) {
			index1 = context.getPlotData().getEndIndex();
		}
		if (index1 == NumberUtils.MIN_INTEGER) {
			index1 = context.getPlotData().getStartIndex();
		}
		if (index2 == NumberUtils.MAX_INTEGER) {
			index2 = context.getPlotData().getEndIndex();
		}
		if (index2 == NumberUtils.MIN_INTEGER) {
			index2 = context.getPlotData().getStartIndex();
		}

		// Coordinates.
		int x2 = context.getDrawingCenterCoordinateX(context.getCoordinateX(index2));
		int x1 = context.getDrawingCenterCoordinateX(context.getCoordinateX(index1));
		int y2 = context.getCoordinateY(v2);
		int y1 = context.getCoordinateY(v1);

		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		shape.moveTo(x1, y1);
		shape.lineTo(x2, y1);
		shape.lineTo(x2, y2);
		shape.lineTo(x1, y2);
		shape.lineTo(x1, y1);

		return shape;
	}

	/**
	 * Draw the candlestick.
	 * 
	 * @param g2 The graphics object.
	 * @param context The plotter context.
	 */
	@Override
	public void draw(Graphics2D g2, PlotterContext context) {

		// Save color and stroke.
		Paint savePaint = g2.getPaint();
		Stroke saveStroke = g2.getStroke();

		// Draw.
		Shape shape = getShape(context);
		if (getParameters().isFillShape()) {
			g2.setPaint(getParameters().getPaint());
			g2.fill(shape);
		}
		if (getParameters().isPaintBorder()) {
			g2.setPaint(getParameters().getColor());
			g2.setStroke(getParameters().getStroke());
			g2.draw(shape);
		}

		// Restore color and stroke.
		g2.setPaint(savePaint);
		g2.setStroke(saveStroke);
	}
}