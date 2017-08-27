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

/**
 * The <i>DatabaseProvider</i> defines an interface to provide meta data information at the level of fields, tables and
 * views. Implementations may use any persistence media, like XML files or a database connection.
 * 
 * @author Miquel Sas
 */
public interface DatabaseProvider {

	/**
	 * Set the database that will use this provider.
	 * 
	 * @param database The database.
	 */
	void setDatabase(Database database);

	/**
	 * Returns the database that is using this provider.
	 * 
	 * @return The database.
	 */
	Database getDatabase();

	/**
	 * Returns the field definition for the given name.
	 * 
	 * @param name The name of the field.
	 * @return The field definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	Field getField(String name) throws Exception;

	/**
	 * Returns a table definition.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	Table getTable(String catalog, String schema, String name) throws Exception;

	/**
	 * Returns a table definition. This is a simplified version for providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	Table getTable(String schema, String name) throws Exception;

	/**
	 * Returns a table definition. This is a simplified version for providers with a single catalog and a single schema.
	 * 
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	Table getTable(String name) throws Exception;

	/**
	 * Returns a view definition.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	View getView(String catalog, String schema, String name) throws Exception;

	/**
	 * Returns a view definition. This is a simplified version for providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	View getView(String schema, String name) throws Exception;

	/**
	 * Returns a view definition. This is a simplified version for providers with a single catalog and a single schema.
	 * 
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	View getView(String name) throws Exception;
}
