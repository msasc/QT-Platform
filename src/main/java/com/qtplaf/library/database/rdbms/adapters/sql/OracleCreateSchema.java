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

package com.qtplaf.library.database.rdbms.adapters.sql;

import com.qtplaf.library.database.rdbms.sql.CreateSchema;

/**
 * Oracle CREATE SCHEMA adapter (uses CREATE USER)
 * 
 * @author Miquel Sas
 */
public class OracleCreateSchema extends CreateSchema {

	/**
	 * Default constructor.
	 */
	public OracleCreateSchema() {
		super();
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
		b.append("CREATE USER ");
		b.append(getSchema());
		return b.toString();
	}
}
