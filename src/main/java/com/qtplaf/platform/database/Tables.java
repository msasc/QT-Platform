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

package com.qtplaf.platform.database;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;

/**
 * Table names.
 *
 * @author Miquel Sas
 */
public class Tables {

	public static final String DataFilters = "data_filters";
	public static final String Instruments = "instruments";
	public static final String OfferSides = "offer_sides";
	public static final String Periods = "periods";
	public static final String Servers = "servers";
	public static final String Statistics = "statistics";
	public static final String StatisticsTables = "statistics_tables";
	public static final String Tickers = "tickers";

	public static String ticker(Instrument instrument, Period period) {
		return ticker(instrument.getId(), period.getId());
	}

	public static String ticker(Instrument instrument, Period period, String suffix) {
		return ticker(instrument.getId(), period.getId(), suffix);
	}

	public static String ticker(String instrument, String period) {
		return ticker(instrument, period, null);
	}

	private static String ticker(String instrument, String period, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append(instrument.toLowerCase());
		b.append("_");
		b.append(period.toLowerCase());
		if (suffix != null) {
			b.append("_");
			b.append(suffix.toLowerCase());
		}
		return b.toString();
	}
}
