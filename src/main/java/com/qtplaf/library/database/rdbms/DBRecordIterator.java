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
import java.util.NoSuchElementException;

import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;

/**
 * A database record iterator.
 * 
 * @author Miquel Sas
 */
public class DBRecordIterator implements RecordIterator {
	
	/**
	 * The underlying cursor.
	 */
	private Cursor cursor;

	/**
	 * Constructor.
	 * 
	 * @param cursor The underlying cursor.
	 */
	public DBRecordIterator(Cursor cursor) {
		super();
		this.cursor = cursor;
	}

	/**
	 * Returns {@code true} if the iteration has more records. (In other words, returns {@code true} if {@link #next}
	 * would return a record rather than throwing an exception.)
	 *
	 * @return {@code true} if the iteration has more elements
	 */
	@Override
	public boolean hasNext() {
		if (cursor.isClosed()) {
			return false;
		}
		boolean next = false;
		try {
			next = cursor.nextRecord();
		} catch (SQLException e) {
			silentlyClose();
		}
		if (!next) {
			silentlyClose();
		}
		return next;
	}

	/**
	 * Returns the next record in the iteration.
	 *
	 * @return the next record in the iteration
	 * @throws NoSuchElementException if the iteration has no more records
	 */
	@Override
	public Record next() {
		return cursor.getRecord();
	}

	/**
	 * Close underlying resources.
	 * 
	 * @throws PersistorException if any errors occur closing the underlying resources
	 */
	@Override
	public void close() throws PersistorException {
		if (!cursor.isClosed()) {
			try {
				cursor.close();
			} catch (SQLException exc) {
				throw new PersistorException(exc.getMessage(), exc);
			}
		}
	}
	
	/**
	 * Silently close this iterator underlying cursor.
	 */
	private void silentlyClose() {
		try {
			close();
		} catch (PersistorException exc) {
			throw new IllegalStateException(exc);
		}
	}
}
