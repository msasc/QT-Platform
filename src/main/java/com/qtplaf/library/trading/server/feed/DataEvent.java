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

import java.util.EventObject;

import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * Data price feed event.
 * 
 * @author Miquel Sas
 */
public class DataEvent extends EventObject {

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
	 * The price data.
	 */
	private Data data;

	/**
	 * Constructor assigning fields.
	 * 
	 * @param source Event source.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide Offer side.
	 * @param data The data.
	 */
	public DataEvent(Object source, Instrument instrument, Period period, OfferSide offerSide, Data data) {
		super(source);
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
		this.data = data;
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
	 * Returns the price data.
	 * 
	 * @return The price data.
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Returns a string representation of this event.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getSource().getClass().getSimpleName());
		b.append(", ");
		b.append(getInstrument().getDescription());
		b.append(", ");
		b.append(getPeriod());
		b.append(", ");
		b.append(getOfferSide());
		b.append(", ");
		b.append(getData());
		return b.toString();
	}

}
