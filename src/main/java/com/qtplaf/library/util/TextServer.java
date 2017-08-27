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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.qtplaf.library.util.file.FileUtils;

/**
 * A <code>TextServer</code> services text resources. Text resources can be located in property files under a common
 * directory root, in property files directly passed to the server, or in compressed files available through the class
 * path, that contain a set of property files.
 * 
 * @author Miquel Sas
 */
public class TextServer {

	/**
	 * List of base resources loaded.
	 */
	private static ArrayList<String> baseResources = new ArrayList<>();
	/**
	 * The text server that has the base resources loaded.
	 */
	private static TextServer baseTextServer = new TextServer();
	/**
	 * A map with localized text servers.
	 */
	private static HashMap<Locale, TextServer> localizedTextServers = new HashMap<>();

	/**
	 * Returns the not found string for a given key.
	 * 
	 * @param key The search key.
	 * @return The not found string.
	 */
	public static String notFoundKey(String key) {
		return "[" + key + "]";
	}

	/**
	 * Returns a string for a given locale.
	 * 
	 * @param key The key to search the string.
	 * @param locale The locale to use. base server when the key is not found.
	 * @return the String.
	 */
	public static String getString(String key, Locale locale) {
		TextServer textServer = null;
		String string = null;
		// Try with the appropriate server.
		textServer = getLocalizedTextServer(locale);
		string = getString(key, textServer, locale);
		if (string != null) {
			return string;
		}
		// Try with the base text server.
		textServer = getBaseTextServer();
		string = getString(key, textServer, new Locale(""));
		if (string != null) {
			return string;
		}
		// Then return the key.
		return notFoundKey(key);
	}

	/**
	 * Clears the text server buffer and forces a reloading of all resources.
	 * 
	 * @param locale The locale to clear.
	 */
	public static void clear(Locale locale) {
		getLocalizedTextServer(locale).clear();
		getBaseTextServer().clear();
	}

	/**
	 * Get a string from a given text server.
	 * 
	 * @param key The string key
	 * @param textServer The text server to use.
	 * @param locale The local use.
	 * @return The required string or null.
	 */
	private static String getString(String key, TextServer textServer, Locale locale) {
		String string = null;
		// First attempt to check if the string is already loaded in the serve.
		string = textServer.getServerString(key);
		if (string != null) {
			return string;
		}
		// A second attempt to load not loaded resources.
		for (int i = 0; i < baseResources.size(); i++) {
			String fileName = baseResources.get(i);
			if (textServer.hasLoaded(fileName)) {
				continue;
			}
			try {
				textServer.loadResource(fileName, locale);
			} catch (IOException e) {
				Logger.getLogger(TextServer.class).error("Can't load the resource: " + fileName + " locale: " + locale);
			}
			string = textServer.getServerString(key);
			if (string != null) {
				return string;
			}
		}
		// Finally the string was not found.
		return null;
	}

	/**
	 * Adds a base resource to the list of base resources.
	 * 
	 * @param fileName The base resource to add.
	 */
	public static void addBaseResource(String fileName) {
		if (!baseResources.contains(fileName)) {
			baseResources.add(fileName);
		}
	}

	/**
	 * Loads a base resource.
	 * 
	 * @param fileName The base resource to load.
	 * @throws IOException If an IO error occurs.
	 */
	protected static void loadBaseResourse(String fileName) throws IOException {
		getBaseTextServer().loadResource(fileName, null);
	}

	/**
	 * Load a resource file, either a normal properties file, or a zipped file with many properties files.
	 * <p>
	 * 
	 * @param fileName The absolute file name.
	 * @param locale The locale or null to load base resources.
	 * @throws IOException If and IO error occurs.
	 */
	protected static void loadLocalizedResource(String fileName, Locale locale) throws IOException {
		getLocalizedTextServer(locale).loadResource(fileName, locale);
	}

	/**
	 * Returns the base text server.
	 * 
	 * @return The base text server.
	 */
	private static TextServer getBaseTextServer() {
		return baseTextServer;
	}

	/**
	 * Returns the localized text server.
	 * 
	 * @param locale The locale to use.
	 * @return The localized text server.
	 */
	private static TextServer getLocalizedTextServer(Locale locale) {
		TextServer textServer = localizedTextServers.get(locale);
		if (textServer == null) {
			textServer = new TextServer();
			localizedTextServers.put(locale, textServer);
		}
		return textServer;
	}

	/**
	 * The loaded properties.
	 */
	private Properties textProperties = new Properties();
	/**
	 * List of resources loaded in this server.
	 */
	private List<String> resources = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public TextServer() {
		super();
	}

	/**
	 * Check if the server has loaded the given resource.
	 * 
	 * @param fileName The file name of the text resource.
	 * @return A boolean.
	 */
	private boolean hasLoaded(String fileName) {
		return resources.contains(fileName);
	}

	/**
	 * Gets a string searching by key.
	 * 
	 * @param key The key to search.
	 * @return The string.
	 */
	public String getServerString(String key) {
		return textProperties.getProperty(key);
	}

	/**
	 * Clears the text server buffer and forces a reloading of all resources.
	 */
	public void clear() {
		textProperties.clear();
		resources.clear();
	}

	/**
	 * Load a resource file, either a normal properties file, or a zipped file with many properties files.
	 * 
	 * @param fileName The absolute file name.
	 * @param locale The locale or null to load base resources.
	 * @throws IOException If an IO error occurs.
	 */
	private void loadResource(String fileName, Locale locale) throws IOException {
		// Check the resource to load
		if (hasLoaded(fileName)) {
			return;
		}
		// Separate name and extension.
		String ext = FileUtils.getFileExtension(fileName);
		// Check compressed.
		boolean zipped = (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("jar"));
		if (!zipped) {
			loadResourceStd(fileName, locale);
		} else {
			loadResourceZip(fileName, locale);
		}
		resources.add(fileName);
	}

	/**
	 * Load a normal resource file.
	 * 
	 * @param fileName The absolute file name.
	 * @param locale The locale or null to load base resources.
	 * @throws IOException If an IO error occurs.
	 */
	private void loadResourceStd(String fileName, Locale locale) throws IOException {
		String name = FileUtils.getFileName(fileName);
		String ext = FileUtils.getFileExtension(fileName);
		File file = FileUtils.getLocalizedFile(locale, name, ext);
		if (file != null) {
			Properties properties = SystemUtils.getProperties(file);
			mergeResources(properties);
		}
	}

	/**
	 * Load the resources from the zip file, taking only those that apply the locale.
	 * 
	 * @param fileName The absolute file name.
	 * @param locale The locale or null to load base resources.
	 * @throws IOException If an IO error occurs.
	 */
	private void loadResourceZip(String fileName, Locale locale) throws IOException {
		File file = FileUtils.getFileFromClassPathEntries(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			String name = entry.getName();
			boolean merge = false;
			if (locale != null && isLocalizedResource(name, locale)) {
				merge = true;
			}
			if (locale == null && isBaseResource(name)) {
				merge = true;
			}
			if (merge) {
				mergeResources(SystemUtils.getProperties(zis));
			}
		}
		zis.close();
		fis.close();
	}

	/**
	 * Check if a resource name is a base name.
	 * 
	 * @param resourceName The resource name.
	 * @return A boolean that indicates if the resoource is a base resource.
	 */
	private boolean isBaseResource(String resourceName) {
		String name = FileUtils.getFileName(resourceName);
		if (name.charAt(name.length() - 3) == '_') {
			return false;
		}
		return true;
	}

	/**
	 * Check if a resource is localized.
	 * 
	 * @param resourceName The resource name.
	 * @param locale The locale.
	 * @return A boolean that indicates if the resoource is a base resource.
	 */
	private boolean isLocalizedResource(String resourceName, Locale locale) {
		String name = FileUtils.getFileName(resourceName);
		String language_country = locale.getLanguage() + "_" + locale.getCountry();
		String language = locale.getLanguage();
		if (name.endsWith(language_country) || name.endsWith(language)) {
			return true;
		}
		return false;
	}

	/**
	 * Merge the incoming properties with this text server properties.
	 * 
	 * @param properties The properties to merge with this text server.
	 */
	private void mergeResources(Properties properties) {
		Enumeration<Object> e = properties.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			if (!textProperties.containsKey(key)) {
				textProperties.put(key, properties.get(key));
			}
		}
	}
}
