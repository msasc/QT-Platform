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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dukascopy.api.IContext;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.AccountType;
import com.qtplaf.library.trading.server.ConnectionManager;
import com.qtplaf.library.trading.server.FeedManager;
import com.qtplaf.library.trading.server.HistoryManager;
import com.qtplaf.library.trading.server.OrderManager;
import com.qtplaf.library.trading.server.ServerException;
import com.qtplaf.library.trading.server.servers.AbstractServer;
import com.qtplaf.library.trading.server.servers.dukascopy.listeners.DkStrategyListener;
import com.qtplaf.library.trading.server.servers.dukascopy.listeners.DkSystemListener;

/**
 * Dukascopy server implementation.
 * 
 * @author Miquel Sas
 */
public class DkServer extends AbstractServer {

	/**
	 * Client interface.
	 */
	private IClient client;
	/**
	 * System listener.
	 */
	private DkSystemListener systemListener;
	/**
	 * The strategy listener to get access to the history, the data service, the engine and the account.
	 */
	private DkStrategyListener strategyListener;
	/**
	 * Instance of the connection manager.
	 */
	private DkConnectionManager connectionManager;
	/**
	 * Instance of the history manager.
	 */
	private DkHistoryManager historyManager;
	/**
	 * Instance of the order manager.
	 */
	private DkOrderManager orderManager;
	/**
	 * Instance of the feed manager.
	 */
	private DkFeedManager feedManager;
	/**
	 * Instance of the converter.
	 */
	private DkConverter dkConverter;
	/**
	 * List of subscribed instruments.
	 */
	private Set<com.dukascopy.api.Instrument> subscribedInstruments =
		new HashSet<>();

	/**
	 * Constructor.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	public DkServer() throws ServerException {
		super();

		try {

			// Initialize the Dukascopy client.
			client = ClientFactory.getDefaultInstance();

			// Initialize and set the system listener.
			systemListener = new DkSystemListener(this);
			client.setSystemListener(systemListener);

			// Initialize the context strategy.
			strategyListener = new DkStrategyListener(this);

		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException cause) {
			throw new ServerException(cause);
		}

		// Name, id, title.
		setName("Dukascopy");
		setId("dkcp");
		setTitle("Dukascopy Bank SA");
	}

	/**
	 * Check that when some information is required from the instrument, the instrument has been subscribed.
	 * 
	 * @param instrument The Dukascopy instrument.
	 * @throws ServerException If a server error occurs.
	 */
	public void checkSubscribed(com.dukascopy.api.Instrument instrument) throws ServerException {
		if (!getConnectionManager().isConnected()) {
			return;
		}
		if (!subscribedInstruments.contains(instrument)) {
			subscribedInstruments.add(instrument);
			getClient().setSubscribedInstruments(subscribedInstruments);
		}
	}

	/**
	 * Returns the URL to connect to the given account type.
	 * 
	 * @param accountType The account type (Live/Demo)
	 * @return The URL.
	 */
	@Override
	public String getURL(AccountType accountType) {
		if (accountType.equals(AccountType.Live)) {
			return "http://platform.dukascopy.com/live/jforex.jnlp";
		}
		if (accountType.equals(AccountType.Demo)) {
			return "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
		}
		throw new IllegalArgumentException("Invalid account type " + accountType);
	}

	/**
	 * Returns the system listener.
	 * 
	 * @return The system listener.
	 */
	public DkSystemListener getSystemListener() {
		return systemListener;
	}

	/**
	 * Returns the strategy listener.
	 * 
	 * @return The strategy listener.
	 */
	public DkStrategyListener getStrategyListener() {
		return strategyListener;
	}

	/**
	 * Returns the reference to the client.
	 * 
	 * @return the client
	 */
	public IClient getClient() {
		return client;
	}

	/**
	 * Returns the reference to the context.
	 * 
	 * @return The context.
	 */
	public IContext getContext() {
		return strategyListener.getContext();
	}

	/**
	 * Returns a list with all available instruments.
	 * 
	 * @return A list with all available instruments.
	 */
	@Override
	public List<Instrument> getAvailableInstruments() {
		Set<com.dukascopy.api.Instrument> dukascopyInstruments = getClient().getAvailableInstruments();
		List<Instrument> instruments = new ArrayList<>();
		for (com.dukascopy.api.Instrument dukascopyInstrument : dukascopyInstruments) {
			Instrument instrument = getDkConverter().fromDkInstrument(dukascopyInstrument);
			if (instrument.hasCurrency()) {
				instruments.add(instrument);
			}
		}
		return instruments;
	}

	/**
	 * Returns the connection manager associated to this server.
	 * 
	 * @return The connection manager.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public ConnectionManager getConnectionManager() throws ServerException {
		if (connectionManager == null) {
			connectionManager = new DkConnectionManager(this);
		}
		return connectionManager;
	}

	/**
	 * Returns the order manager associated to this server.
	 * 
	 * @return The order manager.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public OrderManager getOrderManager() throws ServerException {
		if (orderManager == null) {
			orderManager = new DkOrderManager(this);
		}
		return orderManager;
	}

	/**
	 * Returns the history manager associated to this server.
	 * 
	 * @return The history manager.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public HistoryManager getHistoryManager() throws ServerException {
		if (historyManager == null) {
			historyManager = new DkHistoryManager(this);
		}
		return historyManager;
	}

	/**
	 * Returns the feed manager to receive live feed events.
	 * 
	 * @return The feed manager.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public FeedManager getFeedManager() throws ServerException {
		if (feedManager == null) {
			feedManager = new DkFeedManager(this);
		}
		return feedManager;
	}

	/**
	 * Returns the converter.
	 * 
	 * @return The converter.
	 */
	public DkConverter getDkConverter() {
		if (dkConverter == null) {
			dkConverter = new DkConverter(this);
		}
		return dkConverter;
	}
}
