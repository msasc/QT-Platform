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
package com.qtplaf.library.database.providers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.qtplaf.library.database.Database;
import com.qtplaf.library.database.DatabaseProvider;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.View;
import com.qtplaf.library.util.xml.Parser;

/**
 * An XML database provider organized in a file per each field, table and view, to allow for a lazy read instead of
 * having to read huge database definition file.
 * <p>
 * Files are organized under a parent directory, with one directory for fields, one for catalogs, under it one for each
 * catalog, under them one for schemas, under it one for each schema, and under them one for tables and one for views.
 * <ul>
 * <li><code>parent_folder</code>:
 * </ul>
 * 
 * @author Miquel Sas
 */
public class XMLDatabaseProvider implements DatabaseProvider {

	/**
	 * Enumeration of used directories.
	 */
	public static enum Directories {
		Fields,
		Catalogs,
		Schemas,
		Tables,
		Views;
	}

	/**
	 * The parent directory.
	 */
	private File parentDirectory;
	/**
	 * Map the names of the structure directories.
	 */
	private Map<Directories, String> directories = new HashMap<>();
	/**
	 * A flag that indicates is several catalogs are included in this XML database provider.
	 */
	private boolean catalogs = true;
	/**
	 * A flag that indicates if several schemas are included in this XML database provider.
	 */
	private boolean schemas = true;
	/**
	 * The database that will use this provider instance.
	 */
	private Database database;

	/**
	 * Constructor assigning the parent directory.
	 * 
	 * @param parentDirectory The parent directory to store definition files.
	 */
	public XMLDatabaseProvider(File parentDirectory) {
		super();
		this.parentDirectory = parentDirectory;
		setDirectoryName(Directories.Fields, "fields");
		setDirectoryName(Directories.Catalogs, "catalogs");
		setDirectoryName(Directories.Schemas, "schemas");
		setDirectoryName(Directories.Tables, "tables");
		setDirectoryName(Directories.Views, "views");
	}

	/**
	 * Set the database that will use this provider.
	 * 
	 * @param database The database.
	 */
	@Override
	public void setDatabase(Database database) {
		this.database = database;
	}

	/**
	 * Returns the database that is using this provider.
	 * 
	 * @return The database.
	 */
	@Override
	public Database getDatabase() {
		return database;
	}

	/**
	 * Set change the default names for the directory structure.
	 * 
	 * @param directory The directory which name is to be changed.
	 * @param directoryName The new directory name.
	 */
	public void setDirectoryName(Directories directory, String directoryName) {
		directories.put(directory, directoryName);
	}

	/**
	 * Returns the directory name.
	 * 
	 * @param directory The directory.
	 * @return The name.
	 */
	public String getDirectoryName(Directories directory) {
		return directories.get(directory);
	}

	/**
	 * Returns the root path given the catalog and the schema.
	 * 
	 * @param catalog The catalog or null if <i>!isCatalogs()</i>.
	 * @param schema The schemas or null if <i>!isSchemas()</i>.
	 * @return The root path.
	 */
	private String getPath(String catalog, String schema) {

		// Check that catalog conforms to the catalogs flag
		if (catalog != null && !catalog.isEmpty() && !isCatalogs()) {
			throw new IllegalArgumentException("Catalog must be null");
		}
		if ((catalog == null || catalog.isEmpty()) && isCatalogs()) {
			throw new IllegalArgumentException("Catalog can not be null or empty");
		}

		// Check that schema conforms to the schema flag
		if (schema != null && !schema.isEmpty() && !isSchemas()) {
			throw new IllegalArgumentException("Schema must be null");
		}
		if ((schema == null || schema.isEmpty()) && isSchemas()) {
			throw new IllegalArgumentException("Schema can not be null or empty");
		}

		// Build the path
		StringBuilder path = new StringBuilder();
		if (isCatalogs()) {
			path.append(getDirectoryName(Directories.Catalogs));
			path.append(File.separator);
			path.append(catalog);
		}
		if (isSchemas()) {
			if (path.length() != 0) {
				path.append(File.separator);
			}
			path.append(getDirectoryName(Directories.Schemas));
			path.append(File.separator);
			path.append(schema);
		}
		return path.toString();
	}

	/**
	 * Returns the path given catalog, schema, sub-directory and name.
	 * 
	 * @param catalog The catalog or null if <i>!isCatalogs()</i>.
	 * @param schema The schemas or null if <i>!isSchemas()</i>.
	 * @param directory The sub-directory, tables or views.
	 * @return The path.
	 */
	private String getPath(String catalog, String schema, Directories directory) {
		StringBuilder path = new StringBuilder(getPath(catalog, schema));
		if (path.length() != 0) {
			path.append(File.separator);
		}
		path.append(getDirectoryName(directory));
		return path.toString();
	}

	/**
	 * Indicates if several catalogs are included in this XML database provider.
	 * 
	 * @return A boolean indicating if several catalogs are included in this XML database provider.
	 */
	public boolean isCatalogs() {
		return catalogs;
	}

	/**
	 * Sets if several catalogs are included in this XML database provider.
	 * 
	 * @param catalogs A boolean indicating if several catalogs are included in this XML database provider.
	 */
	public void setCatalogs(boolean catalogs) {
		this.catalogs = catalogs;
	}

	/**
	 * Indicates if several schemas are included in this XML database provider.
	 * 
	 * @return A boolean indicating if several schemas are included in this XML database provider.
	 */
	public boolean isSchemas() {
		return schemas;
	}

	/**
	 * Sets if several schemas are included in this XML database provider.
	 * 
	 * @param schemas A boolean indicating if several schemas are included in this XML database provider.
	 */
	public void setSchemas(boolean schemas) {
		this.schemas = schemas;
	}

	/**
	 * Returns the field definition for the given name.
	 * 
	 * @param name The name of the field.
	 * @return The field definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public Field getField(String name) throws Exception {
		File fieldsDirectory = new File(parentDirectory, getDirectoryName(Directories.Fields));
		File fieldXMLFile = new File(fieldsDirectory, name + ".xml");
		Parser parser = new Parser();
		XMLDatabaseParserHandler handler = new XMLDatabaseParserHandler(this);
		parser.parse(fieldXMLFile, handler);
		return handler.getField();
	}

	/**
	 * Returns a table definition.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public Table getTable(String catalog, String schema, String name) throws Exception {
		File tablesDirectory = new File(parentDirectory, getPath(catalog, schema, Directories.Tables));
		File tableXMLFile = new File(tablesDirectory, name + ".xml");
		XMLDatabaseParserHandler handler = new XMLDatabaseParserHandler(this);
		Parser parser = new Parser();
		parser.parse(tableXMLFile, handler);
		return handler.getTable();
	}

	/**
	 * Returns a table definition. This is a simplified version for providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public Table getTable(String schema, String name) throws Exception {
		return getTable(null, schema, name);
	}

	/**
	 * Returns a table definition. This is a simplified version for providers with a single catalog and a single schema.
	 * 
	 * @param name The table name.
	 * @return The table definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public Table getTable(String name) throws Exception {
		return getTable(null, null, name);
	}

	/**
	 * Returns a view definition.
	 * 
	 * @param catalog The catalog name.
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public View getView(String catalog, String schema, String name) throws Exception {
		File viewsDirectory = new File(parentDirectory, getPath(catalog, schema, Directories.Views));
		@SuppressWarnings("unused")
		File viewXMLFile = new File(viewsDirectory, name + ".xml");
		return null;
	}

	/**
	 * Returns a view definition. This is a simplified version for providers with a single catalog and several schemas.
	 * 
	 * @param schema The schema name.
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public View getView(String schema, String name) throws Exception {
		return getView(null, schema, name);
	}

	/**
	 * Returns a view definition. This is a simplified version for providers with a single catalog and a single schema.
	 * 
	 * @param name The view name.
	 * @return The view definition or null if not found.
	 * @throws Exception If any persistence error occurs.
	 */
	@Override
	public View getView(String name) throws Exception {
		return getView(null, null, name);
	}

}
