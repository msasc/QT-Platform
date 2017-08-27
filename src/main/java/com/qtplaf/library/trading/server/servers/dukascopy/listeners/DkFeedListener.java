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

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.server.feed.FeedListenerAdapter;
import com.qtplaf.library.trading.server.feed.DataSubscription;
import com.qtplaf.library.trading.server.feed.TickSubscription;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;

/**
 * Dukascopy feed listener.
 * 
 * @author Miquel Sas
 */
public class DkFeedListener extends FeedListenerAdapter {

	/** DK server. */
	private DkServer server;

	/**
	 * Constructor.
	 * 
	 * @param server The server.
	 */
	public DkFeedListener(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Adds a subscription to current bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 */
	public void addCurrentDataSubscription(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = server.getDkConverter().fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = server.getDkConverter().fromDkOfferSide(dkOfferSide);
		DataSubscription subscription = new DataSubscription(instrument, period, offerSide);
		addCurrentDataSubscription(subscription);
	}

	/**
	 * Adds a subscription to comppleted bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 */
	public void addDataSubscription(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = server.getDkConverter().fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = server.getDkConverter().fromDkOfferSide(dkOfferSide);
		DataSubscription subscription = new DataSubscription(instrument, period, offerSide);
		addDataSubscription(subscription);
	}

	/**
	 * Adds a subscription to comppleted bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 */
	public void addTickSubscription(Instrument dkInstrument) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		TickSubscription subscription = new TickSubscription(instrument);
		addTickSubscription(subscription);
	}
}
