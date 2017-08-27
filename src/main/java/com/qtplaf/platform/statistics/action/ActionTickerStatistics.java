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

package com.qtplaf.platform.statistics.action;

import javax.swing.AbstractAction;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Root of actions of tickers statistics.
 *
 * @author Miquel Sas
 */
public abstract class ActionTickerStatistics extends AbstractAction {

	/** Source statistics. */
	private TickerStatistics statistics;

	/**
	 * Constructor.
	 * 
	 * @param statistics Source statistics.
	 */
	public ActionTickerStatistics(TickerStatistics statistics) {
		super();
		this.statistics = statistics;

		ActionUtils.setSession(this, getSession());
	}

	/**
	 * Returns the source statistics.
	 * 
	 * @return The statistics.
	 */
	public TickerStatistics getStatistics() {
		return statistics;
	}

	/**
	 * Returns the server.
	 * 
	 * @return The server.
	 */
	public Server getServer() {
		return statistics.getServer();
	}

	/**
	 * Returns the session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return statistics.getSession();
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return statistics.getInstrument();
	}

	/**
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return statistics.getPeriod();
	}
}
