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

package com.qtplaf.library.statistics;

import com.qtplaf.library.database.Types;

/**
 * Descriptor of statistics output.
 *
 * @author Miquel Sas
 */
public class Output {

	/**
	 * Identifier.
	 */
	private String id;
	/**
	 * Description.
	 */
	private String description;
	/**
	 * Value type.
	 */
	private Types type;

	/**
	 * Default constructor.
	 */
	public Output() {
		super();
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param id Id.
	 * @param description Description.
	 * @param type Type.
	 */
	public Output(String id, String description, Types type) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
	}

	/**
	 * Returns the identification string.
	 * 
	 * @return the id The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the identification string.
	 * 
	 * @param id The identification string.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the type.
	 * 
	 * @return The type.
	 */
	public Types getType() {
		return type;
	}

	/**
	 * Set the type
	 * 
	 * @param type The type.
	 */
	public void setType(Types type) {
		this.type = type;
	}

	/**
	 * Returns the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
