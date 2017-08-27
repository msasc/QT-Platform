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

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.server.feed.FeedListener;
import com.qtplaf.library.trading.server.feed.DataSubscription;
import com.qtplaf.library.trading.server.feed.TickSubscription;
import com.qtplaf.library.trading.server.servers.dukascopy.DkFeedDispatcher;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;

/**
 * Dukascopy strategy implementation to listen to strategy events, to retrieve the context, the history, ticks and all
 * data that requires a running strategy.
 * 
 * @author Miquel Sas
 */
public class DkStrategyListener implements IStrategy {

	/**
	 * The saved context.
	 */
	private IContext context;
	/**
	 * The Dukascopy dispatcher to dispatch current bars and ticks. Completed bars are dispatched through the
	 * <i>DkBarFeedListener</i>.
	 */
	private DkFeedDispatcher dispatcher;
	/**
	 * The list of system listeners.
	 */
	private List<FeedListener> listeners = new ArrayList<>();
	/**
	 * The server.
	 */
	private DkServer server;

	/**
	 * Constructor.
	 * 
	 * @param server The server.
	 */
	public DkStrategyListener(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Adds a system listener to the list of system listeners.
	 * 
	 * @param listener The system listener.
	 */
	public void addListener(FeedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the listener from the list.
	 * 
	 * @param listener The listener to remove.
	 */
	public void removeListener(FeedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the dispatcher.
	 * 
	 * @return The dispatcher.
	 */
	public DkFeedDispatcher getDispatcher() {
		return dispatcher;
	}

	/**
	 * Sets the dispatcher.
	 * 
	 * @param dispatcher The dispatcher.
	 */
	public void setDispatcher(DkFeedDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * Returns the context.
	 * 
	 * @return The context.
	 */
	public IContext getContext() {
		return context;
	}

	/**
	 * Called on strategy start, register the context.
	 */
	@Override
	public void onStart(IContext context) throws JFException {
		this.context = context;
	}

	/**
	 * Forwards the current bar on every tick to the listeners interested in.
	 * 
	 * @param dkInstrument The instrument.
	 * @throws JFException If an error occurs.
	 */
	private void forwardCurrentBars(Instrument dkInstrument) throws JFException {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		for (FeedListener listener : listeners) {
			List<DataSubscription> currentSubscriptions = listener.getCurrentDataSubscriptions();
			for (DataSubscription subscription : currentSubscriptions) {
				if (!subscription.getInstrument().equals(instrument)) {
					continue;
				}
				Period dkPeriod = server.getDkConverter().toDkPeriod(subscription.getPeriod());
				OfferSide dkOfferSide = server.getDkConverter().toDkOfferSide(subscription.getOfferSide());
				IBar dkBar = context.getHistory().getBar(dkInstrument, dkPeriod, dkOfferSide, 0);
				dispatcher.addCurrentBar(dkInstrument, dkPeriod, dkOfferSide, dkBar);
			}
		}
	}

	/**
	 * Forwards the tick to the listeners interested in.
	 * 
	 * @param dkInstrument The instrument.
	 * @param dkTick The tick.
	 */
	private void forwardTick(Instrument dkInstrument, ITick dkTick) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		for (FeedListener listener : listeners) {
			List<TickSubscription> tickSubscriptions = listener.getTickSubscriptions();
			for (TickSubscription subscription : tickSubscriptions) {
				if (!subscription.getInstrument().equals(instrument)) {
					continue;
				}
				dispatcher.addTick(dkInstrument, dkTick);
			}
		}
	}

	/**
	 * Called on every tick of every instrument that application is subscribed on.
	 */
	@Override
	public void onTick(Instrument dkInstrument, ITick dkTick) throws JFException {
		// Forward tick and current bars.
		forwardTick(dkInstrument, dkTick);
		forwardCurrentBars(dkInstrument);
	}

	/**
	 * Called on every bar for every basic period and instrument that application is subscribed on.
	 */
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	/**
	 * Called when new message is received.
	 */
	@Override
	public void onMessage(IMessage message) throws JFException {
	}

	/**
	 * Called when account information update is received.
	 */
	@Override
	public void onAccount(IAccount account) throws JFException {
	}

	/**
	 * Called before strategy is stopped.
	 */
	@Override
	public void onStop() throws JFException {
	}

}
