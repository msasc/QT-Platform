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

/**
 * A builder of CREATE INDEX statements.
 *
 * @author Miquel Sas
 */
public class CreateIndex extends Statement {

	/**
	 * The index to create.
	 */
	private Index index;

	/**
	 * Default constructor.
	 */
	public CreateIndex() {
		super();
	}

	/**
	 * Returns the index.
	 *
	 * @return The index definition.
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * Set the index to create.
	 *
	 * @param index The index definition.
	 */
	public void setIndex(Index index) {
		this.index = index;
	}

	/**
	 * Returns this CREATE INDEX statement as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getIndex() == null) {
			throw new IllegalStateException("Malformed CREATE INDEX query: index is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("CREATE ");
		if (getIndex().isUnique()) {
			b.append("UNIQUE ");
		}
		b.append("INDEX ");
		b.append(getIndex().getName());
		b.append(" ON ");
		b.append(getIndex().getTable().getNameSchema());
		b.append(" (");

		for (int i = 0; i < getIndex().size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(getIndex().getField(i).getNameCreate());
		}
		b.append(") ");

		return b.toString();
	}
}
