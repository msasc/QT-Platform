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

package com.qtplaf.library.app;

/**
 * A user of the system applications.
 * 
 * @author Miquel Sas
 */
public class User {

	/**
	 * User id.
	 */
	private String id;
	/**
	 * User name.
	 */
	private String name;

	/**
	 * Default constructor.
	 */
	public User() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param id The user id.
	 * @param name The user name.
	 */
	public User(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the user id.
	 * 
	 * @return The user id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param id The user id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the user name.
	 * 
	 * @return The user name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the user name.
	 * 
	 * @param name The user name.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
