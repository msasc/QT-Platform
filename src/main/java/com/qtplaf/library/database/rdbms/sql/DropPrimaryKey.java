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

import com.qtplaf.library.database.Table;

/**
 * An ALTER TABLE DROP PRIMARY KEY builder.
 *
 * @author Miquel Sas
 */
public class DropPrimaryKey extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table = null;

	/**
	 * Default constructor.
	 */
	public DropPrimaryKey() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return The table.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the table to which the primary key should be dropped.
	 *
	 * @param table The table.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns this <code>ALTER TABLE DROP PRIMARY KEY</code> statement as a string.
	 *
	 * @return The statement.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed DROP PRIMARY KEY query: table is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" DROP PRIMARY KEY");
		return b.toString();
	}

}
