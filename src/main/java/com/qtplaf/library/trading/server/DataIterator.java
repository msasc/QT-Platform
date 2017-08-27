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
package com.qtplaf.library.trading.server;

import com.qtplaf.library.trading.data.Data;

/**
 * Iterator interface aimed to download huge amounts of price data. If the underlying server does not support this
 * operation, the server interface must implement it using the normal list retrievement procedures.
 * 
 * @author Miquel Sas
 */
public interface DataIterator {

	/**
	 * Closes the iterator and any related resources.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	void close() throws ServerException;

	/**
	 * Returns a boolean indicating if there are remaining elements to retrieve.
	 * 
	 * @return A boolean indicating if there are remaining elements to retrieve.
	 * @throws ServerException If a server error occurs.
	 */
	boolean hasNext() throws ServerException;

	/**
	 * Returns the next element or throws an exception if there are no more elements.
	 * 
	 * @return The next element or throws an exception if there are no more elements.
	 * @throws ServerException If a server error occurs.
	 */
	Data next() throws ServerException;
}
