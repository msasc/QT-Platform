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

import java.awt.Component;

import javax.swing.JLabel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditLabel;

/**
 * A label related to an edit context.
 * 
 * @author Miquel Sas
 */
public class JLabelField extends JLabel implements EditLabel {

	/**
	 * The edit context.
	 */
	private EditContext editContext;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JLabelField(EditContext editContext) {
		super();
		this.editContext = editContext;
		setText(editContext.getField().getDisplayLabel());
	}

	/**
	 * Returns the associated edit context.
	 * 
	 * @return The edit context.
	 */
	@Override
	public EditContext getEditContext() {
		return editContext;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getEditContext().getSession();
	}

	/**
	 * Returns the component (this).
	 * 
	 * @return The {@link Component}.
	 */
	@Override
	public Component getComponent() {
		return this;
	}
	
	/**
	 * Updates the component with context information, the text of the label.
	 */
	public void updateComponent() {
		if (getEditContext() == null) {
			setText("");
			return;
		}
		setText(getEditContext().getField().getDisplayLabel());
	}

}
