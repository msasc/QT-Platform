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

/**
 * Customizer of the <tt>JFormRecord</tt>. Extend and overwrite the necessary methods.
 * 
 * @author Miquel Sas
 */
public class JFormRecordCustomizer {

	/**
	 * Default constructor.
	 */
	public JFormRecordCustomizer() {
		super();
	}

	/**
	 * Used to make customizations on the form after layed out by the autoLayout feature, normally to add value actions
	 * to form controls.
	 * 
	 * @param form The form to configure.
	 */
	public void postLayoutConfigure(JFormRecord form) {
	}

	/**
	 * Validates the form.
	 * 
	 * @param form The <code>JFormRecord</code> to validate.
	 * @return A boolean indicating that the form values are valid.
	 */
	public boolean validateForm(JFormRecord form) {
		return true;
	}

}
