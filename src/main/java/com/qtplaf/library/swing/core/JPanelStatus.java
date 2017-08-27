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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * A status panel that shows a status line and optionally, at its right, a progress bar.
 * 
 * @author Mique Sas
 */
public class JPanelStatus extends JPanel implements StatusBar {

	/**
	 * Status label.
	 */
	private JLabel label;
	/**
	 * Progress bar.
	 */
	private JProgressBar progressBar;

	/**
	 * Constructor.
	 */
	public JPanelStatus() {
		super();

		label = new JLabel();
		progressBar = new JProgressBar();

		setLayout(new GridBagLayout());

		GridBagConstraints constraintsLabel = new GridBagConstraints();
		constraintsLabel.anchor = GridBagConstraints.WEST;
		constraintsLabel.fill = GridBagConstraints.HORIZONTAL;
		constraintsLabel.gridheight = 1;
		constraintsLabel.gridwidth = 1;
		constraintsLabel.gridx = 0;
		constraintsLabel.gridy = 0;
		constraintsLabel.insets = new Insets(0, 0, 0, 0);
		constraintsLabel.weightx = 1;
		constraintsLabel.weighty = 1;
		constraintsLabel.ipadx = 0;
		constraintsLabel.ipady = 0;
		add(label, constraintsLabel);

		GridBagConstraints constraintsProgress = new GridBagConstraints();
		constraintsProgress.anchor = GridBagConstraints.EAST;
		constraintsProgress.fill = GridBagConstraints.NONE;
		constraintsProgress.gridheight = 1;
		constraintsProgress.gridwidth = 1;
		constraintsProgress.gridx = 0;
		constraintsProgress.gridy = 0;
		constraintsProgress.insets = new Insets(0, 0, 0, 0);
		constraintsProgress.weightx = 1;
		constraintsProgress.weighty = 1;
		constraintsProgress.ipadx = 0;
		constraintsProgress.ipady = 0;

		Dimension size = SwingUtils.getLabelPreferredSize(new JLabel());
		size.width = 200;
		progressBar.setPreferredSize(size);
		add(progressBar, constraintsProgress);
		
		setPreferredSize(new Dimension(0,size.height));
		setMinimumSize(new Dimension(0,size.height));
	}

	/**
	 * Set the status string showing only the label.
	 * 
	 * @param status The status text.
	 */
	@Override
	public void setStatus(String status) {
		progressBar.setVisible(false);
		label.setText(status);
	}

	/**
	 * Set the status message showing the progress bar with the current and maximum values. Current values range from
	 * zero to maximum.
	 * 
	 * @param status The status text.
	 * @param value The current progress value.
	 * @param maximum The maximum value.
	 */
	@Override
	public void setStatus(String status, int value, int maximum) {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		progressBar.setMinimum(0);
		progressBar.setMaximum(maximum);
		progressBar.setValue(value);
		label.setText(status);
	}

	/**
	 * Set the status string with the progress bar indeterminate.
	 * 
	 * @param status The status text.
	 */
	@Override
	public void setStatusIndeterminate(String status) {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(false);
		label.setText(status);
	}
	
	/**
	 * Clearthe status text.
	 */
	@Override
	public void clearStatus() {
		progressBar.setVisible(false);
		label.setText("");
	}
}
