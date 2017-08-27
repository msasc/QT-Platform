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

package com.qtplaf.platform.statistics;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;

/**
 * Manager to centralize states statistics access.
 *
 * @author Miquel Sas
 */
public class Manager {

	/** Working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Manager(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the list of possible statistics id values.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The list of possible statistics id values.
	 */
	public List<Value> getStatisticsIdPossibleValues(Server server, Instrument instrument, Period period) {
		List<TickerStatistics> statistics = getListStatistics(server, instrument, period);
		List<Value> values = new ArrayList<>();
		for (TickerStatistics stats : statistics) {
			Value value = new Value(stats.getId());
			value.setLabel(stats.getTitle());
			values.add(value);
		}
		return values;
	}

	/**
	 * Returns the statictics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param id The statistics id.
	 * @return The statistics definition.
	 */
	public TickerStatistics getStatistics(Server server, Instrument instrument, Period period, String id) {
		List<TickerStatistics> statistics = getListStatistics(server, instrument, period);
		for (TickerStatistics stats : statistics) {
			if (stats.getId().equals(id)) {
				return stats;
			}
		}
		return null;
	}

	/**
	 * Returns the states statistics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The states statistics.
	 */
	public States getStates(Server server, Instrument instrument, Period period) {
		States states = new States(getSession());
		states.setServer(server);
		states.setInstrument(instrument);
		states.setPeriod(period);
		
		Average avg_5 = new Average(Average.Type.WMA, 5, 5, 3);
		Average avg_20 = new Average(Average.Type.SMA, 20, 5, 3);
		Average avg_50 = new Average(Average.Type.SMA, 50, 10, 3);
		Average avg_200 = new Average(Average.Type.SMA, 200, 20, 5);
		states.setConfiguration("sm", avg_5, avg_20, avg_50, avg_200);
		
		return states;
	}

	/**
	 * Returns the list of statictics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The list of statistics definition.
	 */
	public List<TickerStatistics> getListStatistics(Server server, Instrument instrument, Period period) {
		List<TickerStatistics> statistics = new ArrayList<>();
		statistics.add(getStates(server, instrument, period));
		return statistics;
	}

}
