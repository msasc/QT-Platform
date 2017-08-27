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

package com.qtplaf.library.trading.server.servers;

import com.qtplaf.library.trading.server.Server;

/**
 * Root of servers. Implements common methods.
 * 
 * @author Miquel Sas
 */
public abstract class AbstractServer implements Server {

	/**
	 * KeyServer name.
	 */
	private String name;
	/**
	 * KeyServer id.
	 */
	private String id;
	/**
	 * KeyServer title.
	 */
	private String title;

	/**
	 * Constructor.
	 */
	public AbstractServer() {
		super();
	}

	/**
	 * Returns the server name.
	 * 
	 * @return The server name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the server name.
	 * 
	 * @param name The server name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the unique and short identifier.
	 * 
	 * @return The unique and short identifier.
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique and short identifier.
	 * 
	 * @param id The unique and short identifier.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the server title.
	 * 
	 * @return The server title.
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the server title.
	 * 
	 * @param title The server title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
