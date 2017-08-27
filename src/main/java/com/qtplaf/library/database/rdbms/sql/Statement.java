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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;

/**
 * An abstract class that all SQL statements should extend.
 *
 * @author Miquel Sas
 */
public abstract class Statement {
	
	/**
	 * The database engine adapter.
	 */
	private DBEngineAdapter dbEngineAdapter = null;

	/**
	 * The list of possible values.
	 */
	private List<Value> values = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	protected Statement() {
		super();
	}

	/**
	 * Returns this statement as an SQL string, eventually with parameters.
	 *
	 * @return The query.
	 */
	public abstract String toSQL();

	/**
	 * Returns the array of parameterized values. By default returns an empty list of values.
	 *
	 * @return The array of values.
	 */
	public List<Value> getValues() {
		return values;
	}

	/**
	 * Add a value to the list of values.
	 *
	 * @param value The value to add.
	 */
	protected void addValue(Value value) {
		values.add(value);
	}

	/**
	 * Set the database adapter.
	 *
	 * @param databaseAdapter The database adapter.
	 */
	public void setDBEngineAdapter(DBEngineAdapter databaseAdapter) {
		this.dbEngineAdapter = databaseAdapter;
	}

	/**
	 * Returns the database adapter.
	 *
	 * @return The database adapter.
	 */
	public DBEngineAdapter getDBEngineAdapter() {
		return dbEngineAdapter;
	}

	/**
	 * Returns this query as a string, eventually with parameters. A default database is used if necessary.
	 *
	 * @return The query.
	 */
	@Override
	public String toString() {
		return toSQL();
	}
}
