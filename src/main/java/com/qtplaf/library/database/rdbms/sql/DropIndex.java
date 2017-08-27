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

import com.qtplaf.library.database.Index;

/**
 * A builder of DROP INDEX statements.
 *
 * @author Miquel Sas
 */
public class DropIndex extends Statement {

	/**
	 * The table to create.
	 */
	private Index index = null;

	/**
	 * Default constructor.
	 */
	public DropIndex() {
		super();
	}

	/**
	 * Returns the index to be dropped.
	 *
	 * @return The index.
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * Set the index to be dropped.
	 *
	 * @param index The index.
	 */
	public void setIndex(Index index) {
		this.index = index;
	}

	/**
	 * Returns this <code>DROP INDEX</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getIndex() == null) {
			throw new IllegalStateException("Malformed DROP INDEX query: index is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("DROP INDEX ");
		b.append(getIndex().getNameSchema());

		return b.toString();
	}

}
