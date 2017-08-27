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
import java.awt.Paint;
import java.awt.Stroke;

/**
 * Rectangle plot parameters.
 *
 * @author Miquel Sas
 */
public class RectanglePlotParameters {

	/**
	 * Border stroke.
	 */
	private Stroke stroke = new BasicStroke();
	/**
	 * Border color.
	 */
	private Color color = Color.BLACK;
	/**
	 * Fill paint.
	 */
	private Paint paint = new Color(0.75f, 0.75f, 0.75f, 0.2f);
	/**
	 * A boolean to paint the border or not.
	 */
	private boolean paintBorder = true;
	/**
	 * A boolean to fill the shape.
	 */
	private boolean fillShape = true;

	/**
	 * 
	 */
	public RectanglePlotParameters() {
		super();
	}

	/**
	 * Returns the stroke.
	 * 
	 * @return The stroke.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Set the stroke.
	 * 
	 * @param stroke The stroke.
	 */
	public void setStroke(Stroke stroke) {
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

	/**
	 * Returns the fill paint.
	 * 
	 * @return he fill paint.
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * Set the fill paint
	 * 
	 * @param paint The fill paint.
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	/**
	 * Returns a boolean indicating if the border with the specified border color, should be painted.
	 * 
	 * @return A boolean that indicates if the border color should be painted.
	 */
	public boolean isPaintBorder() {
		return paintBorder;
	}

	/**
	 * Set a boolean indicating if the border with the specified border color, should be painted.
	 * 
	 * @param paintBorder A boolean that indicates if the border color should be painted.
	 */
	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}

	/**
	 * Returns a boolean that indicates if the shape should be filled.
	 * 
	 * @return A boolean.
	 */
	public boolean isFillShape() {
		return fillShape;
	}

	/**
	 * Set a boolean that indicates if the shape should be filled.
	 * 
	 * @param fillShape A boolean that indicates if the shape should be filled.
	 */
	public void setFillShape(boolean fillShape) {
		this.fillShape = fillShape;
	}

}
