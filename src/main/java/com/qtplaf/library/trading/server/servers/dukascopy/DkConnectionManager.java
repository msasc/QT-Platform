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
package com.qtplaf.library.trading.server.servers.dukascopy;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.trading.server.AccountType;
import com.qtplaf.library.trading.server.ConnectionListener;
import com.qtplaf.library.trading.server.ConnectionManager;
import com.qtplaf.library.trading.server.ServerException;

/**
 * Dukascopy connection manager implementation.
 * 
 * @author Miquel Sas
 */
public class DkConnectionManager implements ConnectionManager {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * KeyServer reference.
	 */
	private DkServer server;
	/**
	 * The connection type.
	 */
	private AccountType connectionType;

	/**
	 * Constructor assigning the reference server.
	 * 
	 * @param server The Dukascopy server.
	 */
	public DkConnectionManager(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Add a connection listener to receive connection events.
	 * 
	 * @param listener The connection listener.
	 */
	@Override
	public void addListener(ConnectionListener listener) {
		server.getSystemListener().addConnectionListener(listener);
	}

	/**
	 * Connect to the server, using the given string and password, for the given connection type.
	 * <p>
	 * It is the responsibility of the server implementation to ask for any additional information to connect, like for
	 * instance a PIN code.
	 * 
	 * @param username The user name.
	 * @param password The password.
	 * @param accountType The type of connection.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void connect(String username, String password, AccountType accountType) throws ServerException {

		// Check if already connected.
		if (isConnected()) {
			throw new ServerException("KeyServer is already connected, please disconnect first.");
		}

		// Remember the connection type.
		this.connectionType = accountType;

		// Set the corresponding URL.
		String url = server.getURL(accountType);

		// Do connect.
		try {
			server.getClient().connect(url, username, password);

			// Wait for connection.
			int i = 10; // wait max ten seconds
			while (i > 0 && !server.getClient().isConnected()) {
				LOGGER.info("i=" + i);
				Thread.sleep(1000);
				i--;
			}
			if (!server.getClient().isConnected()) {
				LOGGER.error("Failed to connect Dukascopy servers");
				System.exit(1);
			}
		} catch (Exception cause) {
			throw new ServerException(cause);
		}

		// Install the strategy listener.
		server.getClient().startStrategy(server.getStrategyListener());
	}

	/**
	 * Disconnect from the server.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void disconnect() throws ServerException {
		server.getClient().disconnect();
	}

	/**
	 * Returns the connection type of the connection or null if not connected.
	 * 
	 * @return The connection type.
	 */
	@Override
	public AccountType getConnectionType() {
		return connectionType;
	}

	/**
	 * Returns a boolean indicating if the client is correctly connected to the server.
	 * 
	 * @return A boolean indicating if the client is correctly connected to the server.
	 */
	@Override
	public boolean isConnected() {
		return server.getClient().isConnected();
	}

	/**
	 * Tries to reconnect to the server using the current client information.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void reconnect() throws ServerException {
		try {
			server.getClient().reconnect();
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}
}
