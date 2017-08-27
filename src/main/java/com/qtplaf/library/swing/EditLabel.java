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

package com.qtplaf.library.swing;

import java.awt.Component;

/**
 * An edit label represents the link between the control, generally a <i>JLabel</i>, and the field being edited or
 * shown.
 * 
 * @author Miquel Sas
 */
public interface EditLabel {
	/**
	 * Returns the name that uniquely identifies this edit label within the dialog or frame.
	 * 
	 * @return The name.
	 */
	String getName();

	/**
	 * Sets the name that uniquely identifies this edit label within the dialog or frame.
	 * 
	 * @param name The name.
	 */
	void setName(String name);

	/**
	 * Returns a boolean indicating if the underlying component is enabled.
	 * 
	 * @return A boolean indicating if the underlying component is enabled.
	 */
	boolean isEnabled();

	/**
	 * Sets a boolean indicating if the underlying component is enabled.
	 * 
	 * @param enabled A boolean indicating if the underlying component is enabled.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Returns a boolean indicating if the underlying component is visible.
	 * 
	 * @return A boolean indicating if the underlying component is visible.
	 */
	boolean isVisible();

	/**
	 * Set a boolean indicating if the underlying component is visible.
	 * 
	 * @param visible A boolean indicating if the underlying component is visible.
	 */
	void setVisible(boolean visible);

	/**
	 * Returns the associated edit context, that normally should have been set inn the constructor of the underlying
	 * component.
	 * 
	 * @return The edit context.
	 */
	EditContext getEditContext();

	/**
	 * Returns the component (this).
	 * 
	 * @return The {@link Component}.
	 */
	Component getComponent();
}
