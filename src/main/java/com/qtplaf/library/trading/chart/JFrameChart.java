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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JFrameSession;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * A frame to show a chart.
 * 
 * @author Miquel Sas
 */
public class JFrameChart extends JFrameSession {

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			dispose();
		}
	}

	/**
	 * The chart panel.
	 */
	private JChart chart;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public JFrameChart(Session session) {
		super(session);
		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints constraintsChartPanel = new GridBagConstraints();
		constraintsChartPanel.anchor = GridBagConstraints.NORTH;
		constraintsChartPanel.fill = GridBagConstraints.BOTH;
		constraintsChartPanel.gridheight = 1;
		constraintsChartPanel.gridwidth = 1;
		constraintsChartPanel.weightx = 1;
		constraintsChartPanel.weighty = 1;
		constraintsChartPanel.gridx = 0;
		constraintsChartPanel.gridy = 1;
		constraintsChartPanel.insets = new Insets(1, 1, 1, 1);

		chart = new JChart(getSession());
		getContentPane().add(chart, constraintsChartPanel);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		SwingUtils.setSizeAndCenterOnSreen(this, 0.8, 0.8);

		setWindowHandler(new WindowAdapter());
		setVisible(true);
	}

	/**
	 * Returns the chart object.
	 * 
	 * @return The chart.
	 */
	public JChart getChart() {
		return chart;
	}
}
