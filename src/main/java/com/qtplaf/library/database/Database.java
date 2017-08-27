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
package com.qtplaf.library.database;

import java.util.Map;
import java.util.TreeMap;

/**
 * A <i>Database</i> is a dictionary for <i>Field</i>'s, <i>Table</i>'s and <i>View</i>'s. Domains are organized by name
 * because they are sharable among tables and views of different physical databases, while tables and views are
 * organized by catalog, schema and name.
 * 
 * @author Miquel Sas
 */
public class Database {

	/**
	 * A map for fields.
	 */
	private Map<String, Field> mapFields = new TreeMap<>();
	/**
	 * A map for tables.
	 */
	private Map<String, Table> mapTables = new TreeMap<>();
	/**
	 * A map for views.
	 */
	private Map<String, View> mapViews = new TreeMap<>();
	/**
	 * The database information provider.
	 */
	private DatabaseProvider databaseProvider;

	/**
	 * Constructor assigning the database provider.
	 * 
	 * @param databaseProvider The database provider.
	 */
	public Database(DatabaseProvider databaseProvider) {
		super();
		this.databaseProvider = databaseProvider;
		this.databaseProvider.setDatabase(this);
	}

	/**
	 * Returns the database provider.
	 * 
	 * @return The database provider.
	 */
	public DatabaseProvider getDatabaseProvider() {
		return databaseProvider;
	}

	/**
	 * Returns a field of the given name retrieving it from the provider and caching it in the fields map.
	 * 
	 * @param name The field name.
	 * @return The field or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public Field getField(String name) throws Exception {
		Field field = mapFields.get(name);
		if (field == null) {
			field = getDatabaseProvider().getField(name);
			if (field != null) {
				mapFields.put(name, field);
			}
		}
		return field;
	}

	/**
	 * Returns a table definition lazily retrieving it from the database provider.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public Table getTable(String catalog, String schema, String name) throws Exception {
		String key = getKey(catalog, schema, name);
		Table table = mapTables.get(key);
		if (table == null) {
			table = getDatabaseProvider().getTable(catalog, schema, name);
			if (table != null) {
				mapTables.put(key, table);
			}
		}
		return table;
	}

	/**
	 * Returns a table definition lazily retrieving it from the database provider. This is a simplified version for
	 * providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public Table getTable(String schema, String name) throws Exception {
		return getTable(null, schema, name);
	}

	/**
	 * Returns a table definition lazily retrieving it from the database provider. This is a simplified version for
	 * providers with a single catalog and a single schema.
	 * 
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public Table getTable(String name) throws Exception {
		return getTable(null, null, name);
	}

	/**
	 * Returns a view definition lazily retrieving it from the database provider.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public View getView(String catalog, String schema, String name) throws Exception {
		String key = getKey(catalog, schema, name);
		View view = mapViews.get(key);
		if (view == null) {
			view = getDatabaseProvider().getView(catalog, schema, name);
			if (view != null) {
				mapViews.put(key, view);
			}
		}
		return view;
	}

	/**
	 * Returns a view definition lazily retrieving it from the database provider. This is a simplified version for
	 * providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public View getView(String schema, String name) throws Exception {
		return getView(null, schema, name);
	}

	/**
	 * Returns a view definition lazily retrieving it from the database provider. This is a simplified version for
	 * providers with a single catalog and a single schema.
	 * 
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	public View getView(String name) throws Exception {
		return getView(null, null, name);
	}

	/**
	 * Returns the key by catalog, schema and name to put and get tables and views from their maps.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The object name, table or view.
	 * @return The string key.
	 */
	private String getKey(String catalog, String schema, String name) {
		StringBuilder b = new StringBuilder();
		if (catalog != null) {
			b.append(catalog);
			b.append("-");
		}
		if (schema != null) {
			b.append(schema);
			b.append("-");
		}
		b.append(name);
		return b.toString();
	}
}
