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
package com.qtplaf.library.trading.server.feed;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * Defines a subscription to an instrument price data.
 * 
 * @author Miquel Sas
 */
public class DataSubscription {

	/**
	 * Instrument.
	 */
	private Instrument instrument;
	/**
	 * Period.
	 */
	private Period period;
	/**
	 * Offer side.
	 */
	private OfferSide offerSide;
	/**
	 * A user object.
	 */
	private Object object;

	/**
	 * Constructor.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 */
	public DataSubscription(Instrument instrument, Period period, OfferSide offerSide) {
		super();
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Returns the offer side.
	 * 
	 * @return The offer side.
	 */
	public OfferSide getOfferSide() {
		return offerSide;
	}

	/**
	 * Returns the user object.
	 * 
	 * @return The user object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Sets the user object.
	 * 
	 * @param object The user object.
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * Check if this subscription should accept the argument data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @return A boolean indicating if this subscription should accept the argument data.
	 */
	public boolean acceptsData(Instrument instrument, Period period, OfferSide offerSide) {
		return this.instrument.equals(instrument) && this.period.equals(period) && this.offerSide.equals(offerSide);
	}
}
