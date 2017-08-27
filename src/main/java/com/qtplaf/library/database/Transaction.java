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
 * An abstract transaction implemented on persistors. Persistors used in a transaction must belong to the same database
 * and the database must accept transactions.
 * 
 * @author Miquel Sas
 */
public interface Transaction {

	/**
	 * Starts the transaction.
	 * 
	 * @throws PersistorException If a persistor error occurs.
	 */
	void start() throws PersistorException;

	/**
	 * Commits the transaction.
	 * 
	 * @throws PersistorException If a persistor error occurs.
	 */
	void commit() throws PersistorException;

	/**
	 * Rolls back the transaction.
	 * 
	 * @throws PersistorException If a persistor error occurs.
	 */
	void rollback() throws PersistorException;

	/**
	 * Delete a record.
	 * 
	 * @param persistor The underlying persistor.
	 * @param record The record to delete.
	 * @return The number of affected rows.
	 * @throws PersistorException If a persistor error occurs.
	 */
	int delete(Persistor persistor, Record record) throws PersistorException;
}
