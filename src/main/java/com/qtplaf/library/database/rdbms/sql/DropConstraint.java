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
 * A generic DROP CONTRAINT builder.
 *
 * @author Miquel Sas
 */
public class DropConstraint extends Statement {

	/**
	 * The table to alter.
	 */
	private Table table = null;
	/**
	 * The constraint name.
	 */
	private String constraintName = null;

	/**
	 * Default constructor.
	 */
	public DropConstraint() {
		super();
	}

	/**
	 * Return the table.
	 *
	 * @return The table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the table to which the constraint should be dropped.
	 *
	 * @param table The table.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Return the constraint name.
	 *
	 * @return The constraintName
	 */
	public String getConstraintName() {
		return constraintName;
	}

	/**
	 * Set the name of the constraint to be dropped.
	 *
	 * @param constraintName The name of the constraint.
	 */
	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	/**
	 * Returns this ALTER TABLE <i>table</i> DROP CONSTRAINT <i>constraint</i> statement as a string.
	 * <p>
	 * 
	 * @return The statement as a string.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed DROP CONSTRAINT query: table is null");
		}
		if (getConstraintName() == null) {
			throw new IllegalStateException("Malformed DROP CONSTRAINT query: constraint name is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" DROP CONSTRAINT ");
		b.append(getConstraintName());

		return b.toString();
	}
}
