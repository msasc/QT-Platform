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
package com.qtplaf.library.swing.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.border.AbstractBorder;

/**
 * A line border deciding the sides.
 * 
 * @author Miquel Sas
 */
public class LineBorderSides extends AbstractBorder {

	/**
	 * Thickness.
	 */
	private int thickness;
	/**
	 * Line color.
	 */
	private Color lineColor;
	/**
	 * A boolean that indicates if the top side should be painted.
	 */
	private boolean top = true;
	/**
	 * A boolean that indicates if the left side should be painted.
	 */
	private boolean left = true;
	/**
	 * A boolean that indicates if the bottom side should be painted.
	 */
	private boolean bottom = true;
	/**
	 * A boolean that indicates if the right side should be painted.
	 */
	private boolean right = true;

	/**
	 * General constructor.
	 * 
	 * @param lineColor Line color.
	 * @param thickness Thickness.
	 * @param top A boolean that indicates if the top side should be painted.
	 * @param left A boolean that indicates if the left side should be painted.
	 * @param bottom A boolean that indicates if the bottom side should be painted.
	 * @param right A boolean that indicates if the right side should be painted.
	 */
	public LineBorderSides(Color lineColor, int thickness, boolean top, boolean left, boolean bottom, boolean right) {
		super();
		this.lineColor = lineColor;
		this.thickness = thickness;
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * Returns tthe thickness.
	 * 
	 * @return The thickness.
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * Sets the thickness.
	 * 
	 * @param thickness The thickness.
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/**
	 * Returns the line color.
	 * 
	 * @return The line color.
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * Sets the line color.
	 * 
	 * @param lineColor The line color.
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * Returns a boolean that indicates if the top side should be painted.
	 * 
	 * @return A boolean that indicates if the top side should be painted.
	 */
	public boolean isTop() {
		return top;
	}

	/**
	 * Sets a boolean that indicates if the top side should be painted.
	 * 
	 * @param top A boolean that indicates if the top side should be painted.
	 */
	public void setTop(boolean top) {
		this.top = top;
	}

	/**
	 * Returns a boolean that indicates if the left side should be painted.
	 * 
	 * @return A boolean that indicates if the left side should be painted.
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * Sets a boolean that indicates if the left side should be painted.
	 * 
	 * @param left A boolean that indicates if the left side should be painted.
	 */
	public void setLeft(boolean left) {
		this.left = left;
	}

	/**
	 * Returns a boolean that indicates if the bottom side should be painted.
	 * 
	 * @return A boolean that indicates if the bottom side should be painted.
	 */
	public boolean isBottom() {
		return bottom;
	}

	/**
	 * Sets a boolean that indicates if the bottom side should be painted.
	 * 
	 * @param bottom A boolean that indicates if the bottom side should be painted.
	 */
	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}

	/**
	 * Returns a boolean that indicates if the right side should be painted.
	 * 
	 * @return A boolean that indicates if the right side should be painted.
	 */
	public boolean isRight() {
		return right;
	}

	/**
	 * Sets a boolean that indicates if the right side should be painted.
	 * 
	 * @param right A boolean that indicates if the right side should be painted.
	 */
	public void setRight(boolean right) {
		this.right = right;
	}
	
	/**
	 * Set this border empty.
	 */
	public void setEmpty() {
		top = left  = bottom = right = false;
	}
	
	/**
	 * Set the borders.
	 * 
	 * @param top A boolean that indicates if the top side should be painted.
	 * @param left A boolean that indicates if the left side should be painted.
	 * @param bottom A boolean that indicates if the bottom side should be painted.
	 * @param right A boolean that indicates if the right side should be painted.
	 */
	public void set(boolean top, boolean left, boolean bottom, boolean right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.set(thickness, thickness, thickness, thickness);
		return insets;
	}

	/**
	 * Paints the border for the specified component with the specified position and size.
	 * 
	 * @param c the component for which this border is being painted
	 * @param g the paint graphics
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if ((this.thickness > 0) && (g instanceof Graphics2D)) {
			Graphics2D g2d = (Graphics2D) g;

			// Save old color and set new.
			Color oldColor = g2d.getColor();
			g2d.setColor(this.lineColor);
			
			if (top) {
				Shape lineTop = new Rectangle2D.Float(x, y, width, thickness);
				g2d.fill(lineTop);
			}
			if (left) {
				Shape lineLeft = new Rectangle2D.Float(x, y, thickness, height);
				g2d.fill(lineLeft);
			}
			if (bottom) {
				Shape lineBottom = new Rectangle2D.Float(x, height-thickness, width, height);
				g2d.fill(lineBottom);
			}
			if (right) {
				Shape lineRight = new Rectangle2D.Float(width-thickness, y, width, height);
				g2d.fill(lineRight);
			}
			
			// Restore old color.
			g2d.setColor(oldColor);
		}
	}

}
