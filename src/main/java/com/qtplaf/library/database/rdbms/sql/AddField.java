/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.sql;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;

/**
 * A builder of ADD COLUMN statements.
 *
 * @author Miquel Sas
 */
public class AddField extends Statement {

	/**
	 * The table to create.
	 */
	private Table table;
	/**
	 * The column to add.
	 */
	private Field column;

	/**
	 * Default constructor.
	 */
	public AddField() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Sets the table to which the column will be added.
	 *
	 * @param table The table to which the column will be added.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the columns.
	 *
	 * @return the column
	 */
	public Field getField() {
		return column;
	}

	/**
	 * Sets the column to add to the table.
	 *
	 * @param column The column to add to the table.
	 */
	public void setField(Field column) {
		this.column = column;
	}

	/**
	 * Returns this <code>ALTER TABLE ADD column</code> query as a string.
	 *
	 * @return The add column command as an SQL string.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed ADD COLUMN query: table is null");
		}
		if (getField() == null) {
			throw new IllegalStateException("Malformed ADD COLUMN query: field is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" ADD ");
		b.append(" ");
		b.append(getDBEngineAdapter().getFieldDefinition(getField()));
		b.append(getDBEngineAdapter().getFieldDefinitionSuffix(getField()));

		return b.toString();
	}
}
