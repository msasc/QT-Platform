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
import java.io.IOException;

/**
 * Listener to be implemented by those applications interested in receiving file scan events.
 * 
 * @author Miquel Sas
 */
public interface FileScannerListener {

	/**
	 * Received when a file is scanned.
	 * 
	 * @param sourceDirectory The current source directory being scanned, from the list of source directories to scan.
	 * @param file The file.
	 * @throws IOException If any error occurs during processing.
	 */
	void file(File sourceDirectory, File file) throws IOException;
}
