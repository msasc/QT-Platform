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

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.server.feed.FeedDispatcher;

/**
 * Dukascopy feed dispatcher.
 * 
 * @author Miquel Sas
 */
public class DkFeedDispatcher extends FeedDispatcher {

	/**
	 * Dukascopy server.
	 */
	private DkServer server;

	/**
	 * Constructor.
	 * 
	 * @param server The server,
	 */
	public DkFeedDispatcher(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Adds a current data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 * @param dkBar The Dukascopy bar.
	 */
	public void addCurrentBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = server.getDkConverter().fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = server.getDkConverter().fromDkOfferSide(dkOfferSide);
		Data data = server.getDkConverter().fromDkBar(dkBar);
		addCurrentData(instrument, period, offerSide, data);
	}

	/**
	 * Adds a completed data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 * @param dkBar The Dukascopy bar.
	 */
	public void addBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = server.getDkConverter().fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = server.getDkConverter().fromDkOfferSide(dkOfferSide);
		Data data = server.getDkConverter().fromDkBar(dkBar);
		addData(instrument, period, offerSide, data);
	}

	/**
	 * Add a tick data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkTick The Dukascopy tick.
	 */
	public void addTick(Instrument dkInstrument, ITick dkTick) {
		com.qtplaf.library.trading.data.Instrument instrument = server.getDkConverter().fromDkInstrument(dkInstrument);
		Tick tick = server.getDkConverter().fromDkTick(dkTick);
		addTick(instrument, tick);
	}
}
