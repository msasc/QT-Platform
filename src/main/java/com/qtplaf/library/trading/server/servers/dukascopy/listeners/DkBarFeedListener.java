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

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IBarFeedListener;
import com.qtplaf.library.trading.server.servers.dukascopy.DkFeedDispatcher;

/**
 * Implementation of the <i>IBarFeedListener</i> interface.
 * 
 * @author Miquel Sas
 */
public class DkBarFeedListener implements IBarFeedListener {

	/**
	 * The Dukascopy dispatcher.
	 */
	private DkFeedDispatcher dispatcher;

	/**
	 * Constructor.
	 * 
	 * @param dispatcher The dispatcher.
	 */
	public DkBarFeedListener(DkFeedDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}

	/**
	 * The method is being called when next bar arrives and posts the bar to the dispatcher.
	 * 
	 * @param dkInstrument Instrument.
	 * @param dkPeriod Period.
	 * @param dkOfferSide Offer side.
	 * @param dkBar Bar.
	 */
	@Override
	public void onBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		dispatcher.addBar(dkInstrument, dkPeriod, dkOfferSide, dkBar);
	}

}
