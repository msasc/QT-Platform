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
package com.qtplaf.library.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import com.qtplaf.library.util.file.FileUtils;

/**
 * Static system utilities.
 * 
 * @author Miquel Sas
 */
public class SystemUtils {
	/**
	 * Returns the system class path.
	 * 
	 * @return The system class path.
	 */
	public static String getSystemClassPath() {
		return System.getProperty("java.class.path");
	}

	/**
	 * Returns an array of system class path entries.
	 * 
	 * @return The array of system class path entries.
	 */
	public static String[] getClassPathEntries() {
		return getClassPathEntries(getSystemClassPath());
	}

	/**
	 * Returns an array of class path entries parsing the class path string.
	 * 
	 * @return An array of class path entries.
	 * @param classPath The class path.
	 */
	public static String[] getClassPathEntries(String classPath) {
		String pathSeparator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, pathSeparator);
		ArrayList<String> entries = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			entries.add(tokenizer.nextToken());
		}
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * Gets the properties by loading the file.
	 *
	 * @return The properties.
	 * @param file The file.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(File file) throws IOException {
		boolean xml = false;
		if (FileUtils.getFileExtension(file.getAbsolutePath()).toLowerCase().equals("xml")) {
			xml = true;
		}
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(fileIn, 4096);
		Properties properties = getProperties(buffer, xml);
		buffer.close();
		fileIn.close();
		return properties;
	}

	/**
	 * Gets the properties from the input stream.
	 * 
	 * @return The properties.
	 * @param stream The input stream.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream) throws IOException {
		return getProperties(stream, false);
	}

	/**
	 * Gets the properties from the input stream.
	 * 
	 * @return The properties.
	 * @param stream The input stream.
	 * @param xml A boolean that indicates if the input stream has an xml format
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream, boolean xml) throws IOException {
		Properties properties = new Properties();
		if (xml) {
			properties.loadFromXML(stream);
		} else {
			properties.load(stream);
		}
		return properties;
	}

}
