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

package com.qtplaf.library.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.util.NumberUtils;

/**
 * Utility to plot 2d line charts, used to graphically view results of calculations.
 *
 * @author Miquel Sas
 */
public class Plot2D {

	/**
	 * Enum the scales.
	 */
	enum Scale {
		Linear,
		Logarithmic
	}

	/**
	 * Shape.
	 */
	class PShape {
		/** List of points. */
		private List<Point2D.Double> points = new ArrayList<>();
		/** Stroke. */
		private Stroke stroke = new BasicStroke();
		/** Color. */
		private Color color = Color.BLACK;

		/**
		 * Constructor with basic 1 pt stroke and color black, pending to add points.
		 */
		public PShape() {
			super();
		}

		/**
		 * Add a point.
		 * 
		 * @param x x value.
		 * @param y y value.
		 */
		public void add(double x, double y) {
			points.add(new Point2D.Double(x, y));
		}

		/**
		 * Return the points.
		 * 
		 * @return The shape points.
		 */
		public List<Point2D.Double> getPoints() {
			return points;
		}

		/**
		 * Return the stroke.
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
		 * Return the color.
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

	/**
	 * The plotter panel.
	 */
	class Plotter extends JPanel {

		/** Plotter insets. */
		private Insets insets = new Insets(20, 20, 20, 20);

		Plotter() {
			setOpaque(true);
			setBackground(Color.WHITE);
		}

		/**
		 * Paint this chart.
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);

			// The graphics object.
			Graphics2D g2 = (Graphics2D) g;

			// Retrieve min/max x/y values.
			double[] min_max = getMinMaxXY();
			double xMin = min_max[0];
			double xMax = min_max[1];
			double yMin = min_max[2];
			double yMax = min_max[3];

			// Save color and stroke.
			Color saveColor = g2.getColor();
			Stroke saveStroke = g2.getStroke();

			// Plot axes.
			g2.setPaint(Color.BLACK);
			g2.setStroke(new BasicStroke());
			g2.draw(getAxes(xMin, xMax, yMin, yMax));

			// Plot PShapes.
			for (PShape pshape : pshapes) {
				g2.setPaint(pshape.getColor());
				g2.setStroke(pshape.getStroke());
				g2.draw(getShape(pshape.getPoints(), xMin, xMax, yMin, yMax));
			}

			// Restore color and stroke.
			g2.setColor(saveColor);
			g2.setStroke(saveStroke);
		}

		/**
		 * Return the x coordinate given the x value.
		 * 
		 * @param x The x value.
		 * @param xMin The minimum x.
		 * @param xMax The maximum x.
		 * @return The x coordinate.
		 */
		private double getXCoord(double x, double xMin, double xMax) {
			if (xMin == xMax) {
				return 0;
			}
			// Apply scale to values if necessary.
			if (xScale.equals(Scale.Logarithmic)) {
				x = log(x);
				xMin = log(xMin);
				xMax = log(xMax);
			}
			double f = (x - xMin) / (xMax - xMin);
			double xRel = NumberUtils.round(f * (getWidth() - insets.left - insets.right), 0);
			return insets.left + xRel;
		}

		/**
		 * Return the log value positive or negative.
		 * 
		 * @param v The value to log
		 * @return The log value.
		 */
		private double log(double v) {
			if (v >= 0) {
				return Math.log1p(v);
			}
			return (-1.0) * Math.log1p(Math.abs(v));
		}

		/**
		 * Returns the Y coordinate, starting at the top of the paint area.
		 * 
		 * @param y The y value.
		 * @param yMin The minimum y.
		 * @param yMax The maximum y.
		 * @return The y coordinate.
		 */
		private double getYCoord(double y, double yMin, double yMax) {
			if (yMin == yMax) {
				return 0;
			}
			// Apply scale to values if necessary.
			if (yScale.equals(Scale.Logarithmic)) {
				y = log(y);
				yMin = log(yMin);
				yMax = log(yMax);
			}
			double f = (y - yMin) / (yMax - yMin);
			double yRel = NumberUtils.round(f * (getHeight() - insets.top - insets.bottom), 0);
			return insets.top + (getHeight() - insets.top - insets.bottom) - yRel;
		}

		/**
		 * Return the shape for the list of points.
		 * 
		 * @param points The list of points.
		 * @param xMin Min x.
		 * @param xMax Max x.
		 * @param yMin Min y.
		 * @param yMax Max y.
		 * @return The AWT shape.
		 */
		private Shape getShape(List<Point2D.Double> points, double xMin, double xMax, double yMin, double yMax) {
			GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
			for (int i = 0; i < points.size(); i++) {
				double x = getXCoord(points.get(i).getX(), xMin, xMax);
				double y = getYCoord(points.get(i).getY(), yMin, yMax);
				if (i == 0) {
					shape.moveTo(x, y);
				} else {
					shape.lineTo(x, y);
				}
			}
			return shape;
		}

		/**
		 * Return the shape of the axes.
		 * 
		 * @param xMin Min x.
		 * @param xMax Max x.
		 * @param yMin Min y.
		 * @param yMax Max y.
		 * @return The AWT shape of the x and y axes.
		 */
		private Shape getAxes(double xMin, double xMax, double yMin, double yMax) {
			GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
			shape.moveTo(getXCoord(xMin, xMin, xMax), getYCoord(0, yMin, yMax));
			shape.lineTo(getXCoord(xMax, xMin, xMax), getYCoord(0, yMin, yMax));
			shape.moveTo(getXCoord(0, xMin, xMax), getYCoord(yMin, yMin, yMax));
			shape.lineTo(getXCoord(0, xMin, xMax), getYCoord(yMax, yMin, yMax));
			return shape;
		}
	}

	/** List of shapes to plot. */
	private List<PShape> pshapes = new ArrayList<>();
	/** The x scale. */
	private Scale xScale = Scale.Linear;
	/** The y scale. */
	private Scale yScale = Scale.Linear;

	/**
	 * Constructor.
	 */
	public Plot2D() {
		super();
	}

	/**
	 * Add a shape of y points with x values 1,2,...,n
	 * 
	 * @param y The array of y values.
	 */
	public void addShape(double[] y) {
		addShape(getX(y.length), y, null, null);
	}

	/**
	 * Add a shape of y points with x values 1,2,...,n
	 * 
	 * @param y The array of y values.
	 * @param color The color.
	 */
	public void addShape(double[] y, Color color) {
		addShape(getX(y.length), y, color, null);
	}

	/**
	 * Add a shape of y points with x values 1,2,...,n
	 * 
	 * @param y The array of y values.
	 * @param color The color.
	 * @param stroke The stroke.
	 */
	public void addShape(double[] y, Color color, Stroke stroke) {
		addShape(getX(y.length), y, color, stroke);
	}

	/**
	 * Add a generic shape with
	 * 
	 * @param x The array of x values.
	 * @param y The array of y values.
	 */
	public void addShape(double[] x, double[] y) {
		addShape(x, y, null, null);
	}

	/**
	 * Add a generic shape with
	 * 
	 * @param x The array of x values.
	 * @param y The array of y values.
	 * @param color The color.
	 */
	public void addShape(double[] x, double[] y, Color color) {
		addShape(x, y, color, null);
	}

	/**
	 * Add a generic shape with
	 * 
	 * @param x The array of x values.
	 * @param y The array of y values.
	 * @param color The color.
	 * @param stroke The stroke.
	 */
	public void addShape(double[] x, double[] y, Color color, Stroke stroke) {
		if (x.length != y.length) {
			throw new IllegalArgumentException();
		}
		PShape shape = new PShape();
		if (color != null) {
			shape.setColor(color);
		}
		if (stroke != null) {
			shape.setStroke(stroke);
		}
		for (int i = 0; i < y.length; i++) {
			shape.add(x[i], y[i]);
		}
		pshapes.add(shape);
	}

	/**
	 * Set the x scale linear.
	 */
	public void setXScaleLinear() {
		this.xScale = Scale.Linear;
	}

	/**
	 * Set the y scale linear.
	 */
	public void setYScaleLinear() {
		this.yScale = Scale.Linear;
	}

	/**
	 * Set the x scale logarithmic.
	 */
	public void setXScaleLogarithmic() {
		this.xScale = Scale.Logarithmic;
	}

	/**
	 * Set the x scale logarithmic.
	 */
	public void setYScaleLogarithmic() {
		this.yScale = Scale.Logarithmic;
	}

	/**
	 * Show it.
	 * 
	 * @param title The title of the frame.
	 */
	public void show(String title) {

		// The frame.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(SwingUtils.factorScreenDimension(frame, 0.9, 0.9));
		frame.setLocation(SwingUtils.centerOnScreen(frame));

		frame.getContentPane().add(new Plotter());
		frame.setVisible(true);
	}

	/**
	 * Return a default x axis values starting at 1.
	 * 
	 * @param length The length.
	 * @return The x axis.
	 */
	private double[] getX(int length) {
		double[] x = new double[length];
		for (int i = 0; i < length; i++) {
			x[i] = i + 1;
		}
		return x;
	}

	/**
	 * Returns the minimum and maximum x and y values as an array { xMin, xMax, yMin, yMax }
	 * 
	 * @return The min-max array.
	 */
	private double[] getMinMaxXY() {
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		for (PShape shape : pshapes) {
			List<Point2D.Double> points = shape.getPoints();
			for (Point2D.Double point : points) {
				if (point.getX() < xMin)
					xMin = point.getX();
				if (point.getX() > xMax)
					xMax = point.getX();
				if (point.getY() < yMin)
					yMin = point.getY();
				if (point.getY() > yMax)
					yMax = point.getY();
			}
		}
		if (xMin > 0) {
			xMin = 0;
		}
		if (yMin > 0) {
			yMin = 0;
		}
//		double xDelta = (xMax - xMin) * 0.1;
//		double yDelta = (yMax - yMin) * 0.1;
//		xMin -= xDelta;
//		xMax += xDelta;
//		yMin -= yDelta;
//		yMax += yDelta;
		
		return new double[] { xMin, xMax, yMin, yMax };
	}
}
