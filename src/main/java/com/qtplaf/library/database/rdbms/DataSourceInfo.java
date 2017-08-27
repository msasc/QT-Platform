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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.qtplaf.library.util.SystemUtils;

/**
 * @author mique
 *
 */
public class DataSourceInfo {
	
	/**
	 * Returns a data source info definition from an xml file with the properties xml format and standard keys:
	 * <ul>
	 * <li><b><tt>data-source-name</tt></b></li>
	 * <li><b><tt>server-name</tt></b></li>
	 * <li><b><tt>port-number</tt></b></li>
	 * <li><b><tt>database</tt></b></li>
	 * <li><b><tt>user</tt></b></li>
	 * <li><b><tt>password</tt></b></li>
	 * </ul>
	 * 
	 * @param file The xml file.
	 * @return The connection info.
	 * @throws IOException If such an error occurs.
	 */
	public static DataSourceInfo getDataSourceInfo(File file) throws IOException {
		
		Properties properties = SystemUtils.getProperties(file);
		String dataSourceName = properties.getProperty("data-source-name");
		String serverName = properties.getProperty("server-name");
		int portNumber = Integer.parseInt(properties.getProperty("port-number"));
		String database = properties.getProperty("database");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		
		DataSourceInfo info = new DataSourceInfo();
		info.setDataSourceName(dataSourceName);
		info.setServerName(serverName);
		info.setPortNumber(portNumber);
		info.setDatabase(database);
		info.setUser(user);
		info.setPassword(password);
		
		return info;
	}

	/**
	 * Connection dataSourceName.
	 */
	private String dataSourceName;
	/**
	 * The server or host.
	 */
	private String serverName;
	/**
	 * Port number.
	 */
	private int portNumber;
	/**
	 * The database connection string.
	 */
	private String database;
	/**
	 * The user.
	 */
	private String user;
	/**
	 * The password.
	 */
	private String password;

	/**
	 * Constructor.
	 */
	public DataSourceInfo() {
		super();
	}

	/**
	 * Get the dataSourceName.
	 *
	 * @return The dataSourceName.
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * Set the dataSourceName.
	 * 
	 * @param dataSourceName The dataSourceName to set.
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * Get the database connection string.
	 *
	 * @return The database connection string.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Set the database connection string.
	 *
	 * @param database The database connection string.
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * Get the user.
	 * 
	 * @return The user.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the user.
	 * 
	 * @param user The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the password.
	 * 
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 * 
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the server dataSourceName.
	 * 
	 * @return The server dataSourceName.
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Set the server dataSourceName.
	 * 
	 * @param serverName The server dataSourceName.
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Returns the port number.
	 * 
	 * @return The port number.
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * Set the port number.
	 * 
	 * @param portNumber The port number.
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}
