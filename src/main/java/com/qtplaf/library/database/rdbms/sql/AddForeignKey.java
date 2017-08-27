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

import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Table;

/**
 * An ALTER TABLE ADD FOREIGN KEY builder.
 *
 * @author Miquel Sas
 */
public class AddForeignKey extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table;
	/**
	 * The foreign key to add.
	 */
	private ForeignKey foreignKey;

	/**
	 * Default constructor.
	 */
	public AddForeignKey() {
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
	 * Sets the table to which the column will be added.
	 *
	 * @param table The table to which the column will be added.
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
	 * Set the foreign key to add to the table.
	 *
	 * @param foreignKey The foreign key.
	 */
	public void setForeignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * Returns this <code>ALTER TABLE ADD FOREIGN KEY</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (table == null) {
			throw new IllegalStateException("Malformed ADD FOREIGN KEY query: table is null");
		}
		if (foreignKey == null) {
			throw new IllegalStateException("Malformed ADD FOREIGN KEY query: foreign key is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(table.getNameSchema());
		b.append(" ADD CONSTRAINT ");
		b.append(foreignKey.getName());
		b.append(" FOREIGN KEY (");
		for (int i = 0; i < foreignKey.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(foreignKey.get(i).getLocalField().getNameCreate());
		}
		b.append(") REFERENCES ");
		b.append(foreignKey.getForeignTable().getNameSchema());
		b.append(" (");
		for (int i = 0; i < foreignKey.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(foreignKey.get(i).getForeignField().getNameCreate());
		}
		b.append(")");
		if (foreignKey.getDeleteRestriction() == ForeignKey.OnDelete.CASCADE) {
			b.append(" ON DELETE CASCADE");
		}
		if (foreignKey.getDeleteRestriction() == ForeignKey.OnDelete.SET_NULL) {
			b.append(" ON DELETE SET NULL");
		}

		return b.toString();
	}
}
