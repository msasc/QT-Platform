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
package com.qtplaf.library.trading.server.servers.dukascopy.listeners;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.system.ISystemListener;
import com.qtplaf.library.trading.server.ConnectionEvent;
import com.qtplaf.library.trading.server.ConnectionListener;
import com.qtplaf.library.trading.server.ServerException;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;

/**
 * Dukascopy system listener to forward connection and strategy start/stop events.
 * 
 * @author Miquel Sas
 */
public class DkSystemListener implements ISystemListener {

	/**
	 * The server.
	 */
	private DkServer server;
	/**
	 * List of connectio listeners.
	 */
	private List<ConnectionListener> listeners = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param server The server.
	 */
	public DkSystemListener(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Add a connection listener to receive connection events.
	 * 
	 * @param listener The connection listener.
	 */
	public void addConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notify listeners the connection event.
	 * 
	 * @param e The event.
	 */
	private void notifyConnectionEvent(ConnectionEvent e) {
		for (ConnectionListener listener : listeners) {
			listener.status(e);
		}
	}

	/**
	 * Called on new strategy start.
	 * 
	 * @param processId Id of the strategy.
	 */
	@Override
	public void onStart(long processId) {
	}

	/**
	 * Called on the strategy stop.
	 * 
	 * @param processId Id of the strategy.
	 */
	@Override
	public void onStop(long processId) {
	}

	/**
	 * Called on successful connect.
	 */
	@Override
	public void onConnect() {
		try {
			ConnectionEvent e = new ConnectionEvent(server.getClient());
			StringBuilder b = new StringBuilder();
			b.append("Connected to Dukascopy server: ");
			b.append(server.getConnectionManager().getConnectionType().name());
			e.setMessage(b.toString());
			notifyConnectionEvent(e);
		} catch (ServerException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Called on disconnect.
	 */
	@Override
	public void onDisconnect() {
		try {
			ConnectionEvent e = new ConnectionEvent(server.getClient());
			StringBuilder b = new StringBuilder();
			b.append("Disconnected from Dukascopy server: ");
			b.append(server.getConnectionManager().getConnectionType().name());
			e.setMessage(b.toString());
			notifyConnectionEvent(e);
		} catch (ServerException exc) {
			exc.printStackTrace();
		}
	}
}
