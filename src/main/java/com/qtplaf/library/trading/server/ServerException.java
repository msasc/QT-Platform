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

/**
 * Exceptions thrown by the server.
 * 
 * @author Miquel Sas
 */
public class ServerException extends Exception {

	/**
	 * Default constructor.
	 */
	public ServerException() {
		super();
	}

	/**
	 * Constructor assigning the message.
	 * 
	 * @param message The message.
	 */
	public ServerException(String message) {
		super(message);
	}

	/**
	 * Constructor assigning the cause.
	 * 
	 * @param cause The cause.
	 */
	public ServerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor assigning the message and the cause..
	 * 
	 * @param message The message.
	 * @param cause The cause.
	 */
	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
