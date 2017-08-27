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

import java.util.List;

import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;

/**
 * A builder of DELETE FROM statements
 *
 * @author Miquel Sas
 */
public class Delete extends Statement {

	/**
	 * The table to delete from.
	 */
	private Table table;
	/**
	 * The filter.
	 */
	private Filter filter;

	/**
	 * Default constructor.
	 */
	public Delete() {
		super();
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
	 * Returns the table.
	 *
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the record to delete.
	 *
	 * @param record The record to delete.
	 */
	public void setRecord(Record record) {
		if (getTable() == null) {
			throw new NullPointerException("The record should be set after the table.");
		}
		setFilter(getTable().getPrimaryKeyFilter(record));
	}

	/**
	 * Set the filter to use in the delele statement.
	 *
	 * @param filter The filter.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
		this.filter.setUsage(Filter.Usage.DELETE);
	}

	/**
	 * Set the filter to use in the delele statement.
	 *
	 * @param filter The filter.
	 */
	public void setWhere(Filter filter) {
		setFilter(filter);
	}

	/**
	 * Returns the filter.
	 *
	 * @return The filter.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Returns the list of values to assign to parameters.
	 *
	 * @return The list of values to assign to parameters.
	 */
	@Override
	public List<Value> getValues() {
		if (getFilter() != null && !getFilter().isEmpty()) {
			return getFilter().getValues();
		}
		return super.getValues();
	}

	/**
	 * Returns this <code>ALTER TABLE ADD FOREIGN KEY</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed DELETE query: table is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("DELETE FROM ");
		b.append(getTable().getNameSchema());
		if (getFilter() != null && !getFilter().isEmpty()) {
			b.append(" WHERE ");
			b.append(getFilter().toString());
		}

		return b.toString();
	}
}
