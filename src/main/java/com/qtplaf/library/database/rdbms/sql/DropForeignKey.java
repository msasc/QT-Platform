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

import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Table;

/**
 * A DROP FOREIGN KEY builder.
 *
 * @author Miquel Sas
 */
public class DropForeignKey extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table = null;
	/**
	 * The foreign key to add.
	 */
	private ForeignKey foreignKey = null;

	/**
	 * Default constructor.
	 */
	public DropForeignKey() {
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
	 * Set the table to which the foreign key should be dropped.
	 *
	 * @param table The table
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the foreign key.
	 * 
	 * @return The foreign key.
	 */
	public ForeignKey getForeignKey() {
		return foreignKey;
	}

	/**
	 * Set the foreign key to be dropped.
	 *
	 * @param foreignKey The foreign key.
	 */
	public void setForeignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * Returns this <code>DROP FOREIGN KEY</code> statement as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed DROP FOREIGN KEY query: table is null");
		}
		if (getForeignKey() == null) {
			throw new IllegalStateException("Malformed DROP FOREIGN KEY query: foreign key is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" DROP FOREIGN KEY ");
		b.append(getForeignKey().getName());

		return b.toString();
	}

}
