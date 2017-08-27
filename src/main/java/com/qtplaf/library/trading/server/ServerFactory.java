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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;

/**
 * Support for servers has to be hard coded at this stage of development. This factory provides the supported servers.
 * 
 * @author Miquel Sas
 */
public class ServerFactory {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Standard connection listener.
	 */
	static class ServerConnectionListener implements ConnectionListener {
		@Override
		public void status(ConnectionEvent e) {
			LOGGER.info(e.getMessage());
		}
	}

	/** Dukascopy server key. */
	private static final String DUKASCOPY = "dkcp";

	/**
	 * The map to catch servers.
	 */
	private static Map<String, Server> mapServers = new ConcurrentHashMap<>();

	/**
	 * Returns a list with an instance of each supported server.
	 * 
	 * @return The list of servers.
	 * @throws ServerException If such an error occurs.
	 */
	public static List<Server> getSupportedServers() throws ServerException {
		List<Server> servers = new ArrayList<>();
		servers.add(getServerDukascopy());
		return servers;
	}

	/**
	 * Returns a supported server given its id.
	 * 
	 * @param id The server id.
	 * @return The server.
	 * @throws ServerException if the server is not supported.
	 */
	public static Server getServer(String id) throws ServerException {
		List<Server> servers = getSupportedServers();
		for (Server server : servers) {
			if (server.getId().equals(id)) {
				return server;
			}
		}
		throw new ServerException("KeyServer " + id + " not supported");
	}

	/**
	 * Returns a new instance of the Dukascopy server.
	 * 
	 * @return The server.
	 * @throws ServerException If such an error occurs.
	 */
	public static Server getServerDukascopy() throws ServerException {
		Server server = mapServers.get(DUKASCOPY);
		if (server == null) {
			server = new DkServer();
			server.getConnectionManager().addListener(new ServerConnectionListener());
			mapServers.put(DUKASCOPY, server);
		}
		return server;
	}

	/**
	 * Avoid construction.
	 */
	private ServerFactory() {
	}
}
