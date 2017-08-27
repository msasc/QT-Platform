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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.OrderKey;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueMap;
import com.qtplaf.library.database.View;
import com.qtplaf.library.database.rdbms.sql.Insert;
import com.qtplaf.library.database.rdbms.sql.Select;
import com.qtplaf.library.database.rdbms.sql.Statement;
import com.qtplaf.library.database.rdbms.sql.Update;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A database engine represent a backend JDBC database.
 *
 * @author Miquel Sas
 */
public class DBEngine {

	/**
	 * The database engine adapter.
	 */
	private DBEngineAdapter dbEngineAdapter;
	/**
	 * Data source info.
	 */
	private DataSourceInfo dataSourceInfo;
	/**
	 * Data source.
	 */
	private DataSource dataSource;

	/**
	 * Creates a <i>DBEngine</i> assigning the database adapter and the connection information.
	 * 
	 * @param dbEngineAdapter The database engine adapter.
	 * @param dataSourceInfo The connection information.
	 */
	public DBEngine(DBEngineAdapter dbEngineAdapter, DataSourceInfo dataSourceInfo) {
		super();
		this.dbEngineAdapter = dbEngineAdapter;
		this.dataSourceInfo = dataSourceInfo;
	}

	/**
	 * Returns the data source.
	 * 
	 * @return The data source.
	 */
	public DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = getDBEngineAdapter().getDataSource(dataSourceInfo);
		}
		return dataSource;
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
	 * Returns the connection, pooled or not depending on the driver.
	 *
	 * @return The connection.
	 * @throws SQLException If such an error occurs.
	 */
	public Connection getConnection() throws SQLException {
		Connection cn = getDataSource().getConnection();
		cn.setAutoCommit(false);
		return cn;
	}

	/**
	 * Execute a statement, not a Select.
	 *
	 * @param statement A statement.
	 * @return The number of records modified.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeStatement(Statement statement) throws SQLException {
		return executeStatement(statement, (Connection) null);
	}

	/**
	 * Execute a statement, not Select.
	 *
	 * @param statement The statement.
	 * @param cn The connection.
	 * @return The number of records modified.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeStatement(Statement statement, Connection cn) throws SQLException {
		if (statement.getClass() == Select.class) {
			throw new SQLException("Select statements not supported");
		}
		int count = 0;
		PreparedStatement ps = null;
		boolean closeConnection = (cn == null);
		try {
			if (closeConnection) {
				cn = getConnection();
			}
			String sql = statement.toSQL();
			if (cn != null) {
				ps = cn.prepareStatement(sql);
				List<Value> values = statement.getValues();
				for (int i = 0; i < values.size(); i++) {
					Value value = values.get(i);
					DBUtils.toPreparedStatement(value, i + 1, ps);
				}
				count = ps.executeUpdate();
			}
		} finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			if (closeConnection && cn != null) {
				cn.commit();
				cn.close();
			}
		}
		return count;
	}

	/**
	 * Executes a list of statements.
	 *
	 * @param statements The list of statements.
	 * @param cn The connection.
	 * @return The number of records modified.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeStatements(List<Statement> statements, Connection cn) throws SQLException {
		int count = 0;
		for (Statement statement : statements) {
			count += executeStatement(statement, cn);
		}
		return count;
	}

	/**
	 * Executes a list of statements within a transaction.
	 *
	 * @param statements The list of statements
	 * @return The number of records modified.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeTransaction(List<Statement> statements) throws SQLException {
		Connection cn = null;
		int countModified = 0;
		try {
			cn = getConnection();
			countModified = executeStatements(statements, cn);
			cn.commit();
		} catch (SQLException e) {
			if (cn != null && !cn.isClosed()) {
				cn.rollback();
			}
			throw e;
		} finally {
			if (cn != null && !cn.isClosed()) {
				cn.close();
			}
		}
		return countModified;
	}

	/**
	 * Executes an add check statement.
	 *
	 * @param table The table
	 * @param field The field
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddCheck(Table table, Field field) throws SQLException {
		return executeAddCheck(table, field, (Connection) null);
	}

	/**
	 * Executes an add check statement.
	 *
	 * @param table The table.
	 * @param field The field.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddCheck(Table table, Field field, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementAddCheck(table, field), cn);
	}

	/**
	 * Executes an add field statement.
	 *
	 * @param table The table
	 * @param field The field
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddField(Table table, Field field) throws SQLException {
		return executeAddField(table, field, (Connection) null);
	}

	/**
	 * Executes an add field statement.
	 *
	 * @param table The table.
	 * @param field The field.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddField(Table table, Field field, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementAddField(table, field), cn);
	}

	/**
	 * Executes an add foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddForeignKey(Table table, ForeignKey foreignKey) throws SQLException {
		return executeAddForeignKey(table, foreignKey, (Connection) null);
	}

	/**
	 * Executes an add foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddForeignKey(Table table, ForeignKey foreignKey, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementAddForeignKey(table, foreignKey), cn);
	}

	/**
	 * Executes an add primary key statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddPrimaryKey(Table table) throws SQLException {
		return executeAddPrimaryKey(table, (Connection) null);
	}

	/**
	 * Executes an add primary key statement.
	 *
	 * @param table The table.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeAddPrimaryKey(Table table, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementAddPrimaryKey(table), cn);
	}

	/**
	 * Executes a create index statement.
	 *
	 * @param index The index to create.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeCreateIndex(Index index) throws SQLException {
		return executeCreateIndex(index, (Connection) null);
	}

	/**
	 * Executes a create index statement.
	 *
	 * @param index The index to create.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeCreateIndex(Index index, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementCreateIndex(index), cn);
	}

	/**
	 * Executes a create schema statement.
	 * 
	 * @param schema The schema.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeCreateSchema(String schema) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementCreateSchema(schema));
	}

	/**
	 * Executes a create table statement, with primary key, indexes, persistent foreign keys and constraints.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeBuildTable(Table table) throws SQLException {
		int updated = 0;

		// Create the table.
		updated += executeCreateTable(table);

		// Add the primary key.
		if (table.getPrimaryKey() != null) {
			updated += executeAddPrimaryKey(table);
		}

		// Add secondary indexes.
		for (int i = 0; i < table.getIndexCount(); i++) {
			updated += executeCreateIndex(table.getIndex(i));
		}

		// Persistent foreign keys.
		for (int i = 0; i < table.getForeignKeyCount(); i++) {
			if (table.getForeignKey(i).isPersistent()) {
				updated += executeAddForeignKey(table, table.getForeignKey(i));
			}
		}

		return updated;
	}

	/**
	 * Executes a create table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeCreateTable(Table table) throws SQLException {
		return executeCreateTable(table, (Connection) null);
	}

	/**
	 * Executes a create table statement.
	 *
	 * @param table The table.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeCreateTable(Table table, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementCreateTable(table), cn);
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param view The view which master table will be used to delete records.
	 * @param criteria The filter criteria.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(View view, Criteria criteria) throws SQLException {
		return executeDelete(view.getMasterTable(), new Filter(criteria));
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param table The table.
	 * @param criteria The filter criteria.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(Table table, Criteria criteria) throws SQLException {
		return executeDelete(table, new Filter(criteria));
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param table The table.
	 * @param filter The filter.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(Table table, Filter filter) throws SQLException {
		return executeDelete(table, filter, (Connection) null);
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param table The table.
	 * @param filter The filter.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(Table table, Filter filter, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementDelete(table, filter), cn);
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param table The table.
	 * @param record The record.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(Table table, Record record) throws SQLException {
		return executeDelete(table, record, (Connection) null);
	}

	/**
	 * Executes a delete statement.
	 *
	 * @param table The table.
	 * @param record The record.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDelete(Table table, Record record, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementDelete(table, record), cn);
	}

	/**
	 * Executes a drop foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropForeignKey(Table table, ForeignKey foreignKey) throws SQLException {
		return executeDropForeignKey(table, foreignKey, (Connection) null);
	}

	/**
	 * Executes a drop foreign key statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key to drop.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropForeignKey(Table table, ForeignKey foreignKey, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementDropForeignKey(table, foreignKey), cn);
	}

	/**
	 * Executes a drop index statement.
	 *
	 * @param index The index to drop.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropIndex(Index index) throws SQLException {
		return executeDropIndex(index, (Connection) null);
	}

	/**
	 * Executes a drop index statement.
	 *
	 * @param index The index to drop.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropIndex(Index index, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementDropIndex(index), cn);
	}

	/**
	 * Executes the drop table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropTable(Table table) throws SQLException {
		return executeDropTable(table, (Connection) null);
	}

	/**
	 * Executes the drop table statement.
	 *
	 * @param table The table.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeDropTable(Table table, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementDropTable(table), cn);
	}

	/**
	 * Returns a record iterator given the select query.
	 * 
	 * @param select The select query.
	 * @return The record iterator.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordIterator iterator(Select select) throws SQLException {
		Cursor cursor = executeSelectCursor(select);
		return new DBRecordIterator(cursor);
	}

	/**
	 * Returns a record iterator given a view and a filter.
	 * 
	 * @param view The view.
	 * @param filter The filter.
	 * @return The record iterator.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordIterator iterator(View view, Filter filter) throws SQLException {
		Select select = getDBEngineAdapter().getQuerySelect(view, filter);
		return iterator(select);
	}

	/**
	 * Returns a record iterator given a view and a filter criteria.
	 * 
	 * @param view The view.
	 * @param criteria The filter criteria.
	 * @return The record iterator.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordIterator iterator(View view, Criteria criteria) throws SQLException {
		return iterator(view, new Filter(criteria));
	}

	/**
	 * Executes a select and returns the cursor.
	 *
	 * @param select The select statement.
	 * @return The cursor.
	 * @throws SQLException If such an error occurs.
	 */
	public Cursor executeSelectCursor(Select select) throws SQLException {
		return executeSelectCursor(select, true, (Connection) null);
	}

	/**
	 * Executes a select and returns the cursor.
	 *
	 * @param select The select statement.
	 * @param forwardOnly A flag that indicates if the cursor is forward only.
	 * @return The cursor.
	 * @throws SQLException If such an error occurs.
	 */
	public Cursor executeSelectCursor(Select select, boolean forwardOnly) throws SQLException {
		return executeSelectCursor(select, forwardOnly, (Connection) null);
	}

	/**
	 * Executes a select and returns the cursor.
	 *
	 * @param select The select statement.
	 * @param cn The connection.
	 * @return The cursor.
	 * @throws SQLException If such an error occurs.
	 */
	public Cursor executeSelectCursor(Select select, Connection cn) throws SQLException {
		return executeSelectCursor(select, false, cn);
	}

	/**
	 * Executes a select and returns the cursor.
	 *
	 * @param select The select statement.
	 * @param forwardOnly A flag that indicates if the cursor is forward only.
	 * @param cn The connection.
	 * @return The cursor.
	 * @throws SQLException If such an error occurs.
	 */
	public Cursor executeSelectCursor(Select select, boolean forwardOnly, Connection cn) throws SQLException {
		if (cn == null) {
			cn = getConnection();
		}
		return new Cursor(this, cn, select, forwardOnly);
	}

	/**
	 * Executes a select recordset.
	 *
	 * @param view The view.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(View view) throws SQLException {
		return executeSelectRecordSet(getDBEngineAdapter().getQuerySelect(view));
	}

	/**
	 * Executes a select recordset.
	 *
	 * @param view The view.
	 * @param filter The filter.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(View view, Filter filter) throws SQLException {
		return executeSelectRecordSet(getDBEngineAdapter().getQuerySelect(view, filter));
	}

	/**
	 * Executes a select recordset.
	 *
	 * @param select The select statement.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(Select select) throws SQLException {
		return executeSelectCursor(select, true).getAllRecordsAndClose();
	}

	/**
	 * Executes a select recordset.
	 *
	 * @param select The select statement.
	 * @param maxRecords The maximum number of record to retrieve.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(Select select, int maxRecords) throws SQLException {
		return executeSelectCursor(select, true).getAllRecordsAndClose(maxRecords);
	}

	/**
	 * Executes a select recordset. The connection stays opened.
	 *
	 * @param select The select statement.
	 * @param cn The connection.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(Select select, Connection cn) throws SQLException {
		return executeSelectCursor(select, true, cn).getAllRecords();
	}

	/**
	 * Executes a select recordset. The connection stays opened.
	 *
	 * @param select The select statement.
	 * @param maxRecords The maximum number of records to retrieve.
	 * @param cn The connection.
	 * @return The recordset.
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet executeSelectRecordSet(Select select, int maxRecords, Connection cn) throws SQLException {
		return executeSelectCursor(select, true, cn).getAllRecords(maxRecords);
	}

	/**
	 * Executes a select record.
	 *
	 * @param table The table.
	 * @param primaryKey The primary key.
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record executeSelectPrimaryKey(Table table, OrderKey primaryKey) throws SQLException {
		return executeSelectPrimaryKey(table.getSimpleView(null), primaryKey);
	}

	/**
	 * Executes a select record.
	 *
	 * @param table The table.
	 * @param primaryKey The primary key.
	 * @param cn The connection.
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record executeSelectPrimaryKey(Table table, OrderKey primaryKey, Connection cn) throws SQLException {
		return executeSelectPrimaryKey(table.getSimpleView(null), primaryKey, cn);
	}

	/**
	 * Executes a select record on a view, passing the primary key of the master table.
	 * 
	 * @param view The view.
	 * @param primaryKey The primary key.
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record executeSelectPrimaryKey(View view, OrderKey primaryKey) throws SQLException {
		return executeSelectPrimaryKey(view, primaryKey, (Connection) null);
	}

	/**
	 * Executes a select record on a view, passing the primary key of the master table.
	 * 
	 * @param view The view.
	 * @param primaryKey The primary key.
	 * @param cn The connection.
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record executeSelectPrimaryKey(View view, OrderKey primaryKey, Connection cn) throws SQLException {
		Filter filter = view.getMasterTable().getPrimaryKeyFilter(primaryKey);
		Select select = getDBEngineAdapter().getQuerySelect(view, filter);
		boolean closeConnection = (cn == null);
		try {
			if (closeConnection) {
				cn = getConnection();
			}
			RecordSet recordSet = executeSelectRecordSet(select, cn);
			if (!recordSet.isEmpty()) {
				return recordSet.get(0);
			}
			return null;
		} finally {
			if (closeConnection && cn != null) {
				cn.close();
			}
		}
	}

	/**
	 * Executes the insert statement.
	 *
	 * @param insert The insert statement.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeInsert(Insert insert) throws SQLException {
		return executeInsert(insert, (Connection) null);
	}

	/**
	 * Executes the insert statement.
	 *
	 * @param insert The insert statement.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeInsert(Insert insert, Connection cn) throws SQLException {
		return executeStatement(insert, cn);
	}

	/**
	 * Executes the insert statement.
	 *
	 * @param table The table.
	 * @param record The record to insert.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeInsert(Table table, Record record) throws SQLException {
		return executeInsert(table, record, (Connection) null);
	}

	/**
	 * Executes the insert statement.
	 *
	 * @param table The table.
	 * @param record The record to insert.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeInsert(Table table, Record record, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementInsert(table, record), cn);
	}

	/**
	 * Executes the update statement.
	 *
	 * @param update The update statement.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Update update) throws SQLException {
		return executeUpdate(update, (Connection) null);
	}

	/**
	 * Executes the update statement.
	 *
	 * @param update The update statement.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Update update, Connection cn) throws SQLException {
		return executeStatement(update, cn);
	}

	/**
	 * Executes the update statement.
	 *
	 * @param table The table.
	 * @param record The record to update.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Table table, Record record) throws SQLException {
		return executeUpdate(table, record, (Connection) null);
	}

	/**
	 * Executes the update statement.
	 *
	 * @param table The table.
	 * @param record The record to update.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Table table, Record record, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementUpdate(table, record), cn);
	}

	/**
	 * Execute a massive update on table, with a filter for a map of field-values.
	 * 
	 * @param table The table.
	 * @param filter The filter.
	 * @param map The map of field-values.
	 * @return The number of records updated.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Table table, Filter filter, ValueMap map) throws SQLException {
		return executeUpdate(getDBEngineAdapter().getStatementUpdate(table, filter, map), (Connection) null);
	}

	/**
	 * Execute a massive update on table, with a filter for a map of field-values.
	 * 
	 * @param table The table.
	 * @param filter Ther filter.
	 * @param map The map of field-values.
	 * @param cn The connection.
	 * @return The number of records updated.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeUpdate(Table table, Filter filter, ValueMap map, Connection cn) throws SQLException {
		return executeStatement(getDBEngineAdapter().getStatementUpdate(table, filter, map), cn);
	}

	/**
	 * Saves the record (insert if not exists, otherwise update)
	 *
	 * @param table The table.
	 * @param record The record.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeSave(Table table, Record record) throws SQLException {
		return executeSave(table, record, (Connection) null);
	}

	/**
	 * Saves the record (insert if not exists, otherwise update)
	 *
	 * @param table The table.
	 * @param record The record.
	 * @param cn The connection.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int executeSave(Table table, Record record, Connection cn) throws SQLException {
		if (existsRecord(table, record.getPrimaryKey(), cn)) {
			return executeUpdate(table, record, cn);
		}
		return executeInsert(table, record, cn);
	}

	/**
	 * Execute a count in the view with the filter..
	 *
	 * @param sourceView The source view
	 * @param filter The filter.
	 * @return The number of rows in the view applying the filter.
	 * @throws SQLException If such an error occurs.
	 */
	public long executeSelectCount(View sourceView, Filter filter) throws SQLException {
		View view = new View(sourceView); // Save the source view.

		Field field = new Field();
		field.setName("COUNTER");
		field.setType(Types.Long);
		field.setFunction("COUNT(*)");

		view.removeAllFields();
		view.setOrderBy(null);
		view.addField(field);

		Select select = getDBEngineAdapter().getQuerySelect(view, filter);
		RecordSet recordSet = executeSelectRecordSet(select);
		long count = 0;
		if (recordSet.size() > 0) {
			count = recordSet.get(0).getValue(0).getLong();
		}

		return count;
	}

	/**
	 * Get the functions (MIN, MAX or SUM) for a list of fields.
	 *
	 * @param function The function.
	 * @param sourceView The source view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The list of function values.
	 * @throws SQLException If such an error occurs.
	 */
	private List<Value> executeSelectFunction(String function, View sourceView, Filter filter, List<Integer> indexes)
		throws SQLException {

		// Check the function MIN, MAX, SUM
		if (!StringUtils.in(function, "MIN", "MAX", "SUM")) {
			throw new IllegalArgumentException("Allowed functions are MIN, MAX and SUM.");
		}
		boolean sum = function.equals("SUM");

		View view = new View(sourceView); // Save the source view.
		view.removeAllFields();
		view.setOrderBy(null);
		for (int index : indexes) {
			Field field = new Field(sourceView.getField(index));
			String argument = (sum ? field.getNameWhere() : "");
			field.setFunction(function + "(" + argument + ")");
			view.addField(field);
		}

		Select select = getDBEngineAdapter().getQuerySelect(view, filter);
		RecordSet rs = executeSelectRecordSet(select);
		if (!rs.isEmpty()) {
			Record rc = rs.get(0);
			List<Value> values = new ArrayList<>();
			for (int i = 0; i < indexes.size(); i++) {
				values.add(rc.getValue(i));
			}
			return values;
		}
		return null;
	}

	/**
	 * Get the functions (MIN, MAX or SUM) value map for a list of fields.
	 *
	 * @param function The function.
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The list of function values.
	 * @throws SQLException If such an error occurs.
	 */
	private ValueMap executeSelectFunctionMap(String function, View view, Filter filter, List<Integer> indexes)
		throws SQLException {
		List<Value> values = executeSelectFunction(function, view, filter, indexes);
		return ValueMap.getIndexesMap(indexes, values);
	}

	/**
	 * Get the functions (MIN, MAX or SUM) value map for a list of fields.
	 *
	 * @param function The function.
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The list of function values.
	 * @throws SQLException If such an error occurs.
	 */
	private ValueMap executeSelectFunctionMap(String function, View view, Filter filter, int... indexes)
		throws SQLException {
		List<Integer> indexesList = ListUtils.asList(indexes);
		List<Value> values = executeSelectFunction(function, view, filter, indexesList);
		return ValueMap.getIndexesMap(indexesList, values);
	}

	/**
	 * Get the functions (MIN, MAX or SUM) value map for a list of fields.
	 *
	 * @param function The function.
	 * @param view The view
	 * @param filter The filter.
	 * @param aliases The field aliases.
	 * @return The aliases value map.
	 * @throws SQLException If such an error occurs.
	 */
	private ValueMap executeSelectFunctionMap(String function, View view, Filter filter, String... aliases)
		throws SQLException {
		List<Integer> indexes = view.getFieldIndexes(aliases);
		List<Value> values = executeSelectFunction(function, view, filter, indexes);
		List<String> aliasesList = ListUtils.asList(aliases);
		return ValueMap.getAliasesMap(aliasesList, values);
	}

	/**
	 * Get the max for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The indexes value map.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMaxMap(View view, Filter filter, List<Integer> indexes) throws SQLException {
		return executeSelectFunctionMap("MAX", view, filter, indexes);
	}

	/**
	 * Get the max for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The indexes value map.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMaxMap(View view, Filter filter, int... indexes) throws SQLException {
		return executeSelectFunctionMap("MAX", view, filter, indexes);
	}

	/**
	 * Get the max for a list of fields.
	 * 
	 * @param view The view
	 * @param filter The filter.
	 * @param aliases The list of aliases.
	 * @return The map of max values.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMaxMap(View view, Filter filter, String... aliases) throws SQLException {
		return executeSelectFunctionMap("MAX", view, filter, aliases);
	}

	/**
	 * Get the min for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The indexes value map.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMinMap(View view, Filter filter, List<Integer> indexes) throws SQLException {
		return executeSelectFunctionMap("MIN", view, filter, indexes);
	}

	/**
	 * Get the min for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The indexes value map.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMinMap(View view, Filter filter, int... indexes) throws SQLException {
		return executeSelectFunctionMap("MIN", view, filter, indexes);
	}

	/**
	 * Get the min for a list of fields.
	 * 
	 * @param view The view
	 * @param filter The filter.
	 * @param aliases The list of aliases.
	 * @return The map of max values.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectMinMap(View view, Filter filter, String... aliases) throws SQLException {
		return executeSelectFunctionMap("MIN", view, filter, aliases);
	}

	/**
	 * Get the sum for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The map of sum values.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectSumMap(View view, Filter filter, List<Integer> indexes) throws SQLException {
		return executeSelectFunctionMap("SUM", view, filter, indexes);
	}

	/**
	 * Get the sum for a list of fields.
	 *
	 * @param view The view
	 * @param filter The filter.
	 * @param indexes The field indexes.
	 * @return The map of sum values.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectSumMap(View view, Filter filter, int... indexes) throws SQLException {
		return executeSelectFunctionMap("SUM", view, filter, indexes);
	}

	/**
	 * Get the sum for a list of fields.
	 * 
	 * @param view The view
	 * @param filter The filter.
	 * @param aliases The list of aliases.
	 * @return The map of sum values.
	 * @throws SQLException If such an error occurs.
	 */
	public ValueMap executeSelectSumMap(View view, Filter filter, String... aliases) throws SQLException {
		return executeSelectFunctionMap("SUM", view, filter, aliases);
	}

	/**
	 * Execute the sum for a field.
	 *
	 * @param view The view.
	 * @param filter The filter.
	 * @param index The field index.
	 * @return The sum value.
	 * @throws SQLException If such an error occurs.
	 */
	public Value executeSelectSumValue(View view, Filter filter, int index) throws SQLException {
		ValueMap valueMap = executeSelectSumMap(view, filter, index);
		return valueMap.get(index);
	}

	/**
	 * Execute the sum for a field.
	 *
	 * @param view The view.
	 * @param filter The filter.
	 * @param alias The field alias.
	 * @return The sum value.
	 * @throws SQLException If such an error occurs.
	 */
	public Value executeSelectSumValue(View view, Filter filter, String alias) throws SQLException {
		ValueMap valueMap = executeSelectSumMap(view, filter, alias);
		return valueMap.get(alias);
	}

	/**
	 * Checks if the argument primary key exists in the table.
	 *
	 * @param table The table.
	 * @param primaryKey The primary key.
	 * @return A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean existsRecord(Table table, OrderKey primaryKey) throws SQLException {
		return existsRecord(table, primaryKey, (Connection) null);
	}

	/**
	 * Checks if the argument primary key exists in the table.
	 *
	 * @param table The table.
	 * @param primaryKey The primary key.
	 * @param cn The connection.
	 * @return A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean existsRecord(Table table, OrderKey primaryKey, Connection cn) throws SQLException {
		return (executeSelectPrimaryKey(table, primaryKey, cn) != null);
	}

	/**
	 * Checks if the argument record exists in the table.
	 *
	 * @param table The table
	 * @param record The record.
	 * @return A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean existsRecord(Table table, Record record) throws SQLException {
		return existsRecord(table, record, (Connection) null);
	}

	/**
	 * Checks if the argument record exists in the table.
	 *
	 * @param table The table
	 * @param record The record.
	 * @param cn The connection.
	 * @return A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean existsRecord(Table table, Record record, Connection cn) throws SQLException {
		return existsRecord(table, record.getPrimaryKey(), cn);
	}
}
