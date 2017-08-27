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

package com.qtplaf.library.database.rdbms;

import java.sql.SQLException;

import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.MetaData;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Table;

/**
 * Database persistor data definition.
 * 
 * @author Miquel Sas
 */
public class DBPersistorDDL implements PersistorDDL {

	/**
	 * The underlying <code>DBEngine</code>.
	 */
	private DBEngine dbEngine;

	/**
	 * Constructor.
	 * 
	 * @param dbEngine The underlying database engine.
	 */
	public DBPersistorDDL(DBEngine dbEngine) {
		super();
		this.dbEngine = dbEngine;
	}

	/**
	 * Executes an add foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int addForeignKey(Table table, ForeignKey foreignKey) throws PersistorException {
		try {
			return dbEngine.executeAddForeignKey(table, foreignKey);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}

	/**
	 * Executes an add primary key statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int addPrimaryKey(Table table) throws PersistorException {
		try {
			return dbEngine.executeAddPrimaryKey(table);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}


	/**
	 * Executes a table build.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int buildTable(Table table) throws PersistorException {
		try {
			return dbEngine.executeBuildTable(table);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}

	/**
	 * Executes a create schema statement.
	 * 
	 * @param schema The schema.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int createSchema(String schema) throws PersistorException{
		try {
			return dbEngine.executeCreateSchema(schema);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}
	
	/**
	 * Executes a create table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int createTable(Table table) throws PersistorException {
		try {
			return dbEngine.executeCreateTable(table);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}

	/**
	 * Executes a create index statement.
	 *
	 * @param index The index to create.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int createIndex(Index index) throws PersistorException {
		try {
			return dbEngine.executeCreateIndex(index);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}


	/**
	 * Executes a drop foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int dropForeignKey(Table table, ForeignKey foreignKey) throws PersistorException {
		try {
			return dbEngine.executeDropForeignKey(table, foreignKey);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}
	
	/**
	 * Executes a drop index statement.
	 *
	 * @param index The index to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int dropIndex(Index index) throws PersistorException {
		try {
			return dbEngine.executeDropIndex(index);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}

	/**
	 * Executes the drop table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int dropTable(Table table) throws PersistorException {
		try {
			return dbEngine.executeDropTable(table);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}


	/**
	 * Check if the schema exists.
	 * 
	 * @param schema The schema name.
	 * @return A boolean.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public boolean existsSchema(String schema) throws PersistorException {
		try {
			MetaData metaData = new MetaData(dbEngine);
			return metaData.existsSchema(schema);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}
	
	/**
	 * Check if the table exists.
	 * 
	 * @param table The table.
	 * @return A boolean.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public boolean existsTable(Table table) throws PersistorException {
		return existsTable(table.getSchema(), table.getName());
	}

	/**
	 * Check if the table exists.
	 * 
	 * @param schema The schema name.
	 * @param table The table name.
	 * @return A boolean.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public boolean existsTable(String schema, String table) throws PersistorException {
		try {
			MetaData metaData = new MetaData(dbEngine);
			return metaData.existsTable(schema, table);
		} catch (SQLException exc) {
			throw new PersistorException(exc);
		}
	}
}
