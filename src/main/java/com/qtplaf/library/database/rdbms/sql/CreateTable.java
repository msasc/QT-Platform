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
 * A builder of simple CREATE TABLE statements.
 *
 * @author Miquel Sas
 */
public class CreateTable extends Statement {

	/**
	 * The table to create.
	 */
	private Table table;

	/**
	 * Default constructor.
	 */
	public CreateTable() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return The table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the table.
	 *
	 * @param table The table
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns this CREATE TABLE statement as a string.
	 *
	 * @return The statement.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed CREATE TABLE query: table is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("CREATE TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" (");

		boolean comma = false;
		for (int i = 0; i < getTable().getFieldCount(); i++) {
			Field field = getTable().getField(i);
			if (field.isPersistent()) {
				if (comma) {
					b.append(", ");
				}
				b.append(getDBEngineAdapter().getFieldDefinition(field));
				b.append(getDBEngineAdapter().getFieldDefinitionSuffix(field));
				comma = true;
			}
		}
		b.append(") ");

		return b.toString();
	}
}
