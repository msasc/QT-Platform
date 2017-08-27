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
package com.qtplaf.library.trading.chart;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.trading.chart.parameters.VerticalAxisPlotParameters;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.chart.plotter.axis.VerticalAxisPlotter;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.FormatUtils;

/**
 * A panel that contains the vertical axis in a chart view.
 *
 * @author Miquel Sas
 */
public class JChartVerticalAxis extends JPanel {

	/**
	 * The parent chart container.
	 */
	private JChartContainer chartContainer;
	/**
	 * The cursor (mouse) point in the chart panel..
	 */
	private Point mousePoint;

	/**
	 * Constructor assigning the parent chart container.
	 * 
	 * @param chartContainer The parent chart container.
	 */
	public JChartVerticalAxis(JChartContainer chartContainer) {
		super();
		this.chartContainer = chartContainer;
	}

	/**
	 * Returns the parent container.
	 * 
	 * @return The parent container.
	 */
	public JChartContainer getChartContainer() {
		return chartContainer;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getChartContainer().getSession();
	}

	/**
	 * Sets the maximum, minimum and preferred sizes based on the data and the insets.
	 */
	public void setMaximumMinimumAndPreferredSizes() {

		// Retrieve the plot data and calculate frema if necessary.
		PlotData plotData = chartContainer.getPlotData();
		if (!plotData.areMaximumAndMinimumValuesCalculated()) {
			plotData.calculateFrame();
		}
		double maximumValue = plotData.getMaximumValue();
		double minimumValue = plotData.getMinimumValue();
		int tickScale = plotData.getTickScale();
		String smaximumValue = FormatUtils.unformattedFromDouble(maximumValue, tickScale);
		String sminimumValue = FormatUtils.unformattedFromDouble(minimumValue, tickScale);

		// Plot parameters.
		VerticalAxisPlotParameters plotParameters = chartContainer.getChart().getVerticalAxisPlotParameters();

		// A grafics object and textFont metrics necessary to calculate the text width.
		FontMetrics fm = SwingUtils.getFontMetrics(plotParameters.getVerticalAxisTextFont());
		int textWidth = Math.max(fm.stringWidth(smaximumValue), fm.stringWidth(sminimumValue));

		// Calculate the total width.
		int width =
			plotParameters.getVerticalAxisLineLength()
				+
				plotParameters.getVerticalAxisTextInsets().left
				+
				textWidth
				+
				plotParameters.getVerticalAxisTextInsets().right;

		// Set the sizes.
		setMinimumSize(new Dimension(width, 0));
		setMaximumSize(new Dimension(width, 0));
		setPreferredSize(new Dimension(width, 0));

		// Repaint the container.
		chartContainer.repaint();
	}

	/**
	 * Set the mouse point and repaint.
	 * 
	 * @param mousePoint The mouse point.
	 * @param repaint A boolean that indicates if the panel sould be repainted.
	 */
	public void setMousePoint(Point mousePoint, boolean repaint) {
		this.mousePoint = mousePoint;
		if (repaint) {
			repaint();
		}
	}

	/**
	 * Clear the mouse point, so it does not show custom cursors.
	 * 
	 * @param repaint A boolean to force repaint.
	 */
	public void clearMousePoint(boolean repaint) {
		mousePoint = null;
		if (repaint) {
			repaint();
		}
	}

	/**
	 * Paint this chart.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// 2D graphics.
		Graphics2D g2 = (Graphics2D) g;

		// The plotter.
		VerticalAxisPlotter plotter = getVerticalAxisPlotter();

		// Plot the scale.
		plotter.plotScale(g2);

		// Plot the vursor value if the mouse pointer have been set.
		if (mousePoint != null) {
			plotter.plotCursorValue(g2, mousePoint.y);
		}
	}

	/**
	 * Returns the vertical axis plotter.
	 * 
	 * @return The vertical axis plotter.
	 */
	private VerticalAxisPlotter getVerticalAxisPlotter() {
		return new VerticalAxisPlotter(
			new PlotterContext(chartContainer.getChartPlotter(), chartContainer.getPlotData()));
	}

}
