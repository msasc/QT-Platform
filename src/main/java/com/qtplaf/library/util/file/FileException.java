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

package com.qtplaf.library.util.file;

import java.io.File;

/**
 * A file exception that stores the file.
 * 
 * @author Miquel Sas
 */
public class FileException extends Exception {

	/**
	 * The file.
	 */
	private File file;

	/**
	 * Constructor.
	 * 
	 * @param file The file.
	 */
	public FileException(File file) {
		super();
		this.file = file;
	}

	/**
	 * Constructor.
	 * 
	 * @param file The file.
	 * @param message The message.
	 */
	public FileException(File file, String message) {
		super(message);
		this.file = file;
	}

	/**
	 * Constructor.
	 * 
	 * @param file The file.
	 * @param cause The cause.
	 */
	public FileException(File file, Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.
	 * 
	 * @param file The file.
	 * @param message The message.
	 * @param cause The cause.
	 */
	public FileException(File file, String message, Throwable cause) {
		super(message, cause);
		this.file = file;
	}

	/**
	 * Constructor.
	 * 
	 * @param file The file.
	 * @param message The message.
	 * @param cause The cause.
	 * @param enableSuppression Whether or not suppression is enabled or disabled.
	 * @param writableStackTrace Whether or not the stack trace should be writable.
	 */
	public FileException(
		File file,
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.file = file;
	}

	/**
	 * Returns the file.
	 * 
	 * @return The file.
	 */
	public File getFile() {
		return file;
	}

}
