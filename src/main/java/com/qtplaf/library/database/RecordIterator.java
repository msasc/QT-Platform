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

import java.util.NoSuchElementException;

/**
 * A simple record iterator used to iterate forward only cursors.
 * 
 * @author Miquel Sas
 */
public interface RecordIterator {
	/**
	 * Returns {@code true} if the iteration has more records. (In other words, returns {@code true} if {@link #next}
	 * would return a record rather than throwing an exception.)
	 *
	 * @return {@code true} if the iteration has more elements
	 */
	boolean hasNext();

	/**
	 * Returns the next record in the iteration.
	 *
	 * @return the next record in the iteration
	 * @throws NoSuchElementException if the iteration has no more records
	 */
	Record next();

	/**
	 * Close underlying resources.
	 * 
	 * @throws PersistorException if any errors occur closing the underlying resources
	 */
	void close() throws PersistorException;
}
