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

import com.qtplaf.library.database.Value;

/**
 * Interface that must implement entry fields that store/retrieve their values from records.
 * 
 * @author Miquel Sas
 */
public interface EditField extends EditLabel {

	/**
	 * Clear the control with its default data.
	 */
	void clear();

	/**
	 * Get the value from the component, not the record.
	 * 
	 * @return The value.
	 */
	Value getValue();

	/**
	 * Update the value in the component, not the record.
	 * 
	 * @param value The value to set.
	 */
	void setValue(Value value);

	/**
	 * Update the value in the component, not the record, controlling whether value actions should be fired.
	 * 
	 * @param value The value to set.
	 * @param fireValueActions A boolean stating if value actions should be fired.
	 */
	void setValue(Value value, boolean fireValueActions);
}
