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
 * Interface that should implement back end data definition.
 * 
 * @author Miquel Sas
 */
public interface PersistorDDL {

	/**
	 * Executes an add foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int addForeignKey(Table table, ForeignKey foreignKey) throws PersistorException;

	/**
	 * Executes an add primary key statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int addPrimaryKey(Table table) throws PersistorException;

	/**
	 * Executes a table build.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int buildTable(Table table) throws PersistorException;

	/**
	 * Executes a create schema statement.
	 * 
	 * @param schema The schema.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int createSchema(String schema) throws PersistorException;

	/**
	 * Executes a create table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int createTable(Table table) throws PersistorException;

	/**
	 * Executes a create index statement.
	 *
	 * @param index The index to create.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int createIndex(Index index) throws PersistorException;

	/**
	 * Executes a drop foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int dropForeignKey(Table table, ForeignKey foreignKey) throws PersistorException;

	/**
	 * Executes a drop index statement.
	 *
	 * @param index The index to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int dropIndex(Index index) throws PersistorException;

	/**
	 * Executes the drop table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int dropTable(Table table) throws PersistorException;

	/**
	 * Check if the schema exists.
	 * 
	 * @param schema The schema name.
	 * @return A boolean.
	 * @throws PersistorException If a persistor error occurs.
	 */
	boolean existsSchema(String schema) throws PersistorException;

	/**
	 * Check if the table exists.
	 * 
	 * @param table The table.
	 * @return A boolean.
	 * @throws PersistorException If a persistor error occurs.
	 */
	boolean existsTable(Table table) throws PersistorException;

	/**
	 * Check if the table exists.
	 * 
	 * @param schema The schema name.
	 * @param table The table name.
	 * @return A boolean.
	 * @throws PersistorException If a persistor error occurs.
	 */
	boolean existsTable(String schema, String table) throws PersistorException;
}
