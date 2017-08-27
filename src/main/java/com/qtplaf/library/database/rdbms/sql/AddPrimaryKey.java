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

import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;

/**
 * An ALTER TABLE ADD PRIMARY KEY builder.
 *
 * @author Miquel Sas
 */
public class AddPrimaryKey extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table;

	/**
	 * Default constructor.
	 */
	public AddPrimaryKey() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return The table to which the primarykey should be added.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the table to which add its primary key.
	 *
	 * @param table The table.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns this <code>ALTER TABLE ADD PRIMARY KEY</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed ADD PRIMARY KEY query: table is null");
		}

		StringBuilder b = new StringBuilder(256);
		Index primaryKey = getTable().getPrimaryKey();
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" ADD PRIMARY KEY (");
		for (int i = 0; i < primaryKey.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(primaryKey.getField(i).getNameCreate());
		}
		b.append(") ");

		return b.toString();
	}
}
