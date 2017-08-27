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
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.formatters.DataValue;

/**
 * Centralizes recordset operations.
 *
 * @author Miquel Sas
 */
public class RecordSetUtils {

	/**
	 * Returns a record set with the available instruments for the argument server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The record set.
	 * @throws PersistorException If any persistence error occurs.
	 */
	public static RecordSet getRecordSetAvailableInstruments(Session session, Server server) throws PersistorException {

		Persistor persistor = PersistorUtils.getPersistorInstruments(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Fields.SERVER_ID), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);

		// Track max pip and tick scale to set their values decimals.
		int maxPipScale = 0;
		int maxTickScale = 0;
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			maxPipScale = Math.max(maxPipScale, record.getValue(Fields.INSTRUMENT_PIP_SCALE).getInteger());
			maxTickScale = Math.max(maxTickScale, record.getValue(Fields.INSTRUMENT_TICK_SCALE).getInteger());
		}
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			record.getField(Fields.INSTRUMENT_PIP_VALUE).setFormatter(
				new DataValue(session, maxPipScale));
			record.getField(Fields.INSTRUMENT_TICK_VALUE).setFormatter(
				new DataValue(session, maxTickScale));
		}

		return recordSet;
	}

	/**
	 * Returns the tickers recordset for the given server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The tickers recordset.
	 * @throws PersistorException If any persistence error occurs.
	 */
	public static RecordSet getRecordSetTickers(Session session, Server server) throws PersistorException {
		Persistor persistor = PersistorUtils.getPersistorTickers(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Fields.SERVER_ID), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);
		return recordSet;
	}

	/**
	 * Returns the statistics recordset for the given server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The statistics recordset.
	 * @throws Exception If an error occurs.
	 */
	public static RecordSet getRecordSetStatistics(Session session, Server server) throws Exception {
		Persistor persistor = PersistorUtils.getPersistorStatistics(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Fields.SERVER_ID), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);
		return recordSet;
	}
}
