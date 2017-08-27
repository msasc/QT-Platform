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

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;

/**
 * A generic DROP COLUMN builder.
 *
 * @author Miquel Sas
 */
public class DropField extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table = null;
	/**
	 * The field to drop.
	 */
	private Field field = null;

	/**
	 * Default constructor.
	 */
	public DropField() {
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
	 * Set the table.
	 *
	 * @param table The table.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the field.
	 *
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Set the field to drop.
	 *
	 * @param field The field.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Returns this <code>ALTER TABLE ADD FOREIGN KEY</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed DROP COLUMN query: table is null");
		}
		if (getField() == null) {
			throw new IllegalStateException("Malformed DROP COLUMN query: field is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" DROP COLUMN ");
		b.append(getField().getName());

		return b.toString();
	}
}
