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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.LineBorderSides;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.trading.chart.parameters.HorizontalAxisPlotParameters;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.chart.plotter.axis.HorizontalAxisPlotter;
import com.qtplaf.library.trading.data.PlotData;

/**
 * A panel that contains the horizontal axis in a chart view. The horizontal axis contans one or two lines and shows
 * time information depending on the period shown.
 * 
 * @author Miquel Sas
 */
public class JChartHorizontalAxis extends JPanel {

	/**
	 * The parent chart object.
	 */
	private JChart chart;

	/**
	 * Constructor assigning the parent chart.
	 * 
	 * @param chart The parent chart.
	 */
	public JChartHorizontalAxis(JChart chart) {
		super();
		this.chart = chart;

		setBackground(chart.getDefaultBackgroundColor());
		setBorder(new LineBorderSides(Color.BLACK, 1, true, false, false, false));

		// A grafics object and textFont metrics necessary to calculate the text width.
		HorizontalAxisPlotParameters parameters = chart.getHorizontalAxisPlotParameters();
		FontMetrics fm = SwingUtils.getFontMetrics(parameters.getHorizontalAxisTextFont());

		// Text insets and axis height.
		Insets insets = parameters.getHorizontalAxisTextInsets();
		int height = insets.top + fm.getAscent() + fm.getDescent() + insets.bottom;

		// Set the sizes.
		setMinimumSize(new Dimension(0, height));
		setMaximumSize(new Dimension(0, height));
		setPreferredSize(new Dimension(0, height));
	}

	/**
	 * Returns the parent chart.
	 * 
	 * @return The parent chart.
	 */
	public JChart getChart() {
		return chart;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return chart.getSession();
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
		HorizontalAxisPlotter plotter = getHorizontalAxisPlotter();

		// Do plot.
		plotter.plotAxis(g2);

	}

	/**
	 * Returns the horizontal axis plotter.
	 * 
	 * @return The horizontal axis plotter.
	 */
	private HorizontalAxisPlotter getHorizontalAxisPlotter() {
		JChartPlotter chartPlotter = chart.getChartContainer(0).getChartPlotter();
		PlotData plotData = chart.getChartContainer(0).getPlotData();
		return new HorizontalAxisPlotter(new PlotterContext(chartPlotter, plotData));
	}
}
