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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qtplaf.library.util.Alignment;

/**
 * A status panel for the record table.
 * 
 * @author Miquel Sas
 */
public class JTableRecordStatusPanel extends JPanel {

	/**
	 * A label at the right with righ alignment.
	 */
	private JLabel statusLabel;

	/**
	 * Constructor.
	 */
	public JTableRecordStatusPanel() {
		super(new GridBagLayout());

		// Add the status label.
		add(getStatusLabel(), getStatusLabelConstraints());
	}

	/**
	 * Properly initialize the status label.
	 * 
	 * @return The status label initialized.
	 */
	public JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel();
			statusLabel.setHorizontalAlignment(Alignment.Right.getSwingAlignment());
			Font font = statusLabel.getFont();
			statusLabel.setFont(new Font(font.getFamily(), Font.ITALIC, font.getSize() - 2));
		}
		return statusLabel;
	}

	/**
	 * Returns the status label constraints.
	 * 
	 * @return The status label constraints.
	 */
	private GridBagConstraints getStatusLabelConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		return constraints;
	}
}
