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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.SystemColor;

import javax.swing.Icon;

/**
 * An icon with an arrow inside.
 * 
 * @author Miquel Sas
 */
public class IconArrow implements Icon {

	/**
	 * Arrow directions.
	 */
	public enum Direction {
		/**
		 * North pointing direction.
		 */
		UP,
		/**
		 * South pointing direction.
		 */
		DOWN,
		/**
		 * West pointing direction.
		 */
		LEFT,
		/**
		 * East pointing direction.
		 */
		RIGHT
	}

	private int size = 15;

	private Direction direction = Direction.DOWN;

	/**
	 * Default constructor.
	 */
	public IconArrow() {
		super();
	}

	/**
	 * Constructs a new arrow button with the direction given.
	 * 
	 * @param direction The direction of the arrow.
	 */
	public IconArrow(Direction direction) {
		super();
		setDirection(direction);
	}

	/**
	 * Set the arrow direction.
	 * 
	 * @param direction The direction of the arrow.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Returns the icon's height.
	 *
	 * @return an int specifying the fixed height of the icon.
	 */
	@Override
	public int getIconHeight() {
		return size;
	}

	/**
	 * Returns the icon's width.
	 *
	 * @return an int specifying the fixed width of the icon.
	 */
	@Override
	public int getIconWidth() {
		return size;
	}

	/**
	 * Draw the icon at the specified location. Icon implementations may use the Component argument to get properties
	 * useful for painting, e.g. the foreground or background color.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		boolean enabled = c.isEnabled();
		Color oldColor = g.getColor();

		// X
		int ox = 4;
		int dx = size - ox * 2;
		int minX = ox;
		int maxX = ox + dx - 1;
		int midX = size / 2;
		// Y
		int minY = minX;
		int maxY = maxX;

		g.translate(x, y);
		if (enabled) {
			g.setColor(SystemColor.controlDkShadow);
		} else {
			g.setColor(SystemColor.controlShadow);
		}

		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Polygon p = new Polygon();
		switch (direction) {
		case UP:
			p.addPoint(minX, maxY);
			p.addPoint(maxX, maxY);
			p.addPoint(midX, minY);
			g.fillPolygon(p);
			break;
		case DOWN:
			// Perceptual correction
			p.addPoint(minX, minY + 1);
			p.addPoint(maxX, minY + 1);
			p.addPoint(midX, maxY + 1);
			g.fillPolygon(p);
			break;
		case LEFT:
			// Perceptual correction
			p.addPoint(maxY - 1, minX);
			p.addPoint(maxY - 1, maxX);
			p.addPoint(minY - 1, midX);
			g.fillPolygon(p);
			break;
		case RIGHT:
			p.addPoint(minY, minX);
			p.addPoint(minY, maxX);
			p.addPoint(maxY, midX);
			g.fillPolygon(p);
			break;
		default:
			throw new IllegalArgumentException();
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}
