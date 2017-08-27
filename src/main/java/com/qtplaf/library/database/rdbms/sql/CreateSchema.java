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

package com.qtplaf.library.database.rdbms.sql;

/**
 * A builder of a create schema statement.
 * 
 * @author Miquel Sas
 */
public class CreateSchema extends Statement {

	/**
	 * The schema.
	 */
	private String schema;

	/**
	 * Default constructor.
	 */
	public CreateSchema() {
		super();
	}

	/**
	 * Returns the name of the schema.
	 * 
	 * @return The name of the schema.
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Sets the name of the schema.
	 * 
	 * @param schema The name of the schema.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Returns this CREATE SCHEMA statement as a string.
	 *
	 * @return The statement.
	 */
	@Override
	public String toSQL() {
		if (getSchema() == null) {
			throw new IllegalStateException("The name of the schema must be set.");
		}
		StringBuilder b = new StringBuilder(256);
		b.append("CREATE SCHEMA ");
		b.append(getSchema());
		return b.toString();
	}

}
