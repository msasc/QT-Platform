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

package com.qtplaf.platform.util;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.View;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBPersistor;
import com.qtplaf.library.database.rdbms.DBPersistorDDL;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.TableDataFilters;
import com.qtplaf.platform.database.tables.TableDataPrice;
import com.qtplaf.platform.database.tables.TableInstruments;
import com.qtplaf.platform.database.tables.TableOfferSides;
import com.qtplaf.platform.database.tables.TablePeriods;
import com.qtplaf.platform.database.tables.TableServers;
import com.qtplaf.platform.database.tables.TableStatistics;
import com.qtplaf.platform.database.tables.TableTickers;

/**
 * Centralizes persistors access.
 * 
 * @author Miquel Sas
 */
public class PersistorUtils {

	/** The database engine used to set the persistor to tables. */
	private static DBEngine dbEngine;

	/**
	 * Sets the database engine to assign the proper persistor to tables.
	 * 
	 * @param dbEngine The database engine.
	 */
	public static void setDBEngine(DBEngine dbEngine) {
		PersistorUtils.dbEngine = dbEngine;
	}

	/**
	 * Returns the database engine in use.
	 * 
	 * @return The database engine in use.
	 */
	public static DBEngine getDBEngine() {
		return dbEngine;
	}

	/**
	 * Returns the persistor for the view.
	 * 
	 * @param view The view.
	 * @return The persistor.
	 */
	public static Persistor getPersistor(View view) {
		return new DBPersistor(getDBEngine(), view);
	}

	/**
	 * Returns a suitable DDL.
	 * 
	 * @return The DDL.
	 */
	public static PersistorDDL getDDL() {
		return new DBPersistorDDL(getDBEngine());
	}

	/**
	 * Returns the data price persistor.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @param instrument Instrument.
	 * @param name The table name.
	 * @return The persistor.
	 */
	public static Persistor getPersistorDataPrice(Session session, Server server, Instrument instrument, String name) {
		return new TableDataPrice(session, server, instrument, name).getPersistor();
	}

	/**
	 * Returns the instruments persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorInstruments(Session session) {
		return new TableInstruments(session).getPersistor();
	}

	/**
	 * Returns the periods persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorPeriods(Session session) {
		return new TablePeriods(session).getPersistor();
	}

	/**
	 * Returns the servers persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorServers(Session session) {
		return new TableServers(session).getPersistor();
	}

	/**
	 * Returns the statistics persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorStatistics(Session session) {
		return new TableStatistics(session).getPersistor();
	}

	/**
	 * Returns the tickers persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorTickers(Session session) {
		return new TableTickers(session).getPersistor();
	}

	/**
	 * Returns the offer side persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorOfferSides(Session session) {
		return new TableOfferSides(session).getPersistor();
	}

	/**
	 * Returns the data filter side persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorDataFilters(Session session) {
		return new TableDataFilters(session).getPersistor();
	}
}
