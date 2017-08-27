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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.rdbms.sql.Select;

/**
 * A class to scan a view of a database.
 *
 * @author Miquel Sas
 */
public class Cursor {

	/**
	 * The connection.
	 */
	private Connection cn;
	/**
	 * The prepared statement used to execute the query.
	 */
	private PreparedStatement ps;
	/**
	 * The result set to scan data.
	 */
	private ResultSet rs;
	/**
	 * Default fetch size.
	 */
	private int fetchSize = 100;
	/**
	 * The number of records per page.
	 */
	private int pageSize = 100;
	/**
	 * The field list.
	 */
	private FieldList fieldList;
	/**
	 * The forward only flag.
	 */
	private boolean forwardOnly = true;
	/**
	 * A flag to control if this cursor is closed.
	 */
	private boolean closed = false;
	/**
	 * The last page read.
	 */
	private RecordSet page = null;
	/**
	 * The last record read.
	 */
	private Record record = null;
	/**
	 * The persistor to assign to the record.
	 */
	private DBPersistor persistor;

	/**
	 * Constructor assigning the connection, the select and indicating if the cursor shoulb forward only.
	 *
	 * @param dbEngine The database engine.
	 * @param cn The connection.
	 * @param select The select query.
	 * @param forwardOnly A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public Cursor(DBEngine dbEngine, Connection cn, Select select, boolean forwardOnly) throws SQLException {
		super();
		this.cn = cn;
		this.forwardOnly = forwardOnly;
		List<Value> values = select.getValues();
		String sql = select.toSQL();
		if (forwardOnly) {
			ps = cn.prepareStatement(sql);
		} else {
			ps = cn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		}
		for (int i = 0; i < values.size(); i++) {
			DBUtils.toPreparedStatement(values.get(i), i + 1, ps);
		}
		ps.setFetchSize(getFetchSize());
		rs = ps.executeQuery();
		fieldList = select.getView().getFieldList();
		persistor = new DBPersistor(dbEngine, select.getView());
	}

	/**
	 * Check if the cursor is closed.
	 * 
	 * @throws SQLException If such an error occurs.
	 */
	private void checkClosedCursor() throws SQLException {
		if (closed) {
			throw new SQLException("Cursor is closed.");
		}
	}

	/**
	 * Check if the cursor forward only.
	 * 
	 * @throws SQLException If such an error occurs.
	 */
	private void checkForwardOnly() throws SQLException {
		if (forwardOnly) {
			throw new SQLException(">Unsupported operation for a forward only cursor.");
		}
	}

	/**
	 * Move before the first record. The last record and page read are left unmodified.
	 *
	 * @throws SQLException If such an error occurs.
	 */
	public void beforeFirst() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		rs.beforeFirst();
	}

	/**
	 * Move after the last record. The last record and page read are left unmodified.
	 *
	 * @throws SQLException If such an error occurs.
	 */
	public void afterLast() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		rs.afterLast();
	}

	/**
	 * Moves to the next page of this cursor and sets the last page read to be retrieved with getPage.
	 *
	 * @return <i>false</i> if past the last record, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean nextPage() throws SQLException {
		checkClosedCursor();
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		int count = 0;
		while (count < pageSize && rs.next()) {
			count++;
			recordSet.add(readRecord());
		}
		page = recordSet;
		return (count == pageSize);
	}

	/**
	 * Moves to the next record of this cursor and sets the last record read to be retrieved with <i>getRecord</i>.
	 *
	 * @return <i>false</i> if past the last record, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean nextRecord() throws SQLException {
		checkClosedCursor();
		if (rs.next()) {
			record = readRecord();
			return true;
		}
		return false;
	}

	/**
	 * Moves to the first record of the second page and sets the last page read to be retrieved with <i>getPage</i>.
	 *
	 * @return <i>false</i> if no records present, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean firstPage() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		rs.beforeFirst();
		int count = 0;
		while (count < pageSize && rs.next()) {
			count++;
			recordSet.add(readRecord());
		}
		page = recordSet;
		return (count == pageSize);
	}

	/**
	 * Moves to the first record of this cursor and sets the last record read to be retrieved with <i>getRecord</i>.
	 *
	 * @return <i>false</i> if no records present, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean firstRecord() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		if (rs.first()) {
			record = readRecord();
			return true;
		}
		return false;
	}

	/**
	 * Moves to the record last page minus one of this cursor and sets the last page read to be retrieved with
	 * <i>getPage</i>.
	 *
	 * @return <i>false</i> if no records present, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean lastPage() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		rs.afterLast();
		int count = 0;
		while (count < pageSize && rs.previous()) {
			count++;
			recordSet.add(readRecord());
		}
		page = recordSet;
		return (count == pageSize);
	}

	/**
	 * Moves to the last record of this cursor and sets the last record read to be retrieved with <i>getRecord</i> .
	 *
	 * @return <i>false</i> if no records present, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean lastRecord() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		if (rs.last()) {
			record = readRecord();
			return true;
		}
		return false;
	}

	/**
	 * Moves to the previous page minus one of this cursor and sets the last page read to be retrieved with
	 * <i>getPage</i>.
	 *
	 * @return <i>false</i> if before the first record, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean previousPage() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		int count = 0;
		while (count < pageSize && rs.previous()) {
			count++;
			recordSet.add(readRecord());
		}
		page = recordSet;
		return (count == pageSize);
	}

	/**
	 * Moves to the previous record of this cursor and sets the last record read to be retrieved with <i>getRecord</i>.
	 *
	 * @return <i>false</i> if before the first record, <i>true</i> otherwise.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean previousRecord() throws SQLException {
		checkForwardOnly();
		checkClosedCursor();
		if (rs.previous()) {
			record = readRecord();
			return true;
		}
		return false;
	}

	/**
	 * Returns all the records in this cursor as a <i>RecordSet</i>. The underlying resultset cursor is left open after
	 * the last record.
	 *
	 * @return All this cursor records in a <i>RecordSet</i>
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet getAllRecords() throws SQLException {
		return getAllRecords(0);
	}

	/**
	 * Returns all the records in this cursor as a <i>RecordSet</i>, up to a maximum number of records. The underlying
	 * result set cursor is left open after the last record.
	 *
	 * @param maxRecords The maximum number of records to retrieve, 0 or less means all.
	 * @return All this cursor records in a <i>RecordSet</i>
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet getAllRecords(int maxRecords) throws SQLException {
		checkClosedCursor();
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		int count = 0;
		while (rs.next()) {
			recordSet.add(readRecord());
			count++;
			if (maxRecords > 0 && count >= maxRecords) {
				break;
			}
		}
		return recordSet;
	}

	/**
	 * Returns all the records in this cursor as a <i>RecordSet</i> and close any used resource.
	 *
	 * @return All this cursor records in a <i>RecordSet</i>
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet getAllRecordsAndClose() throws SQLException {
		return getAllRecordsAndClose(0);
	}

	/**
	 * Returns all the records in this cursor as a <i>RecordSet</i> up to a maximum number of records and close any used
	 * resource.
	 *
	 * @param maxRecords The maximum number of records to retrieve, 0 or less means all.
	 * @return All this cursor records in a <i>RecordSet</i>
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet getAllRecordsAndClose(int maxRecords) throws SQLException {
		RecordSet recordSet = getAllRecords(maxRecords);
		close();
		return recordSet;
	}

	/**
	 * Returns the last record read.
	 * <p>
	 * 
	 * @return The last record read.
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * Returns the last page read.
	 * <p>
	 * 
	 * @return The last page read as a <i>RecordSet</i>
	 */
	public RecordSet getPage() {
		return page;
	}

	/**
	 * Get the number of records per page.
	 *
	 * @return The number of records per page.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set the number of records per page.
	 *
	 * @param pageSize The size of the pages read.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns the default fetch size
	 * 
	 * @return The fetch size.
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * Set the fetch size.
	 * 
	 * @param fetchSize The fetch size.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Close this cursor and the underlying <i>ResultSet</i>, <i>PreparedStatement</i> and <i>Connection</i>.
	 *
	 * @throws SQLException If such an error occurs.
	 */
	public void close() throws SQLException {
		checkClosedCursor();
		if (rs != null) {
			rs.close();
			rs = null;
		}
		if (ps != null) {
			ps.close();
			ps = null;
		}
		if (cn != null && !cn.isClosed()) {
			cn.close();
			cn = null;
		}
		closed = true;
	}

	/**
	 * Check if this cursor is closed.
	 *
	 * @return A boolean.
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Let the garbage collector close this cursor if not already done.
	 */
	@Override
	protected void finalize() {
		try {
			close();
		} catch (SQLException e) {/* do nothing */

		}
	}

	/**
	 * Read the current record.
	 * <p>
	 * 
	 * @return The current record.
	 * @throws SQLException If such an error occurs.
	 */
	private Record readRecord() throws SQLException {
		Record record = DBUtils.readRecord(fieldList, rs);
		record.setPersistor(persistor);
		return record;
	}
}
