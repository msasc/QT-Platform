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

import javax.swing.ListSelectionModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.core.JLookupRecords;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.RecordSetUtils;

/**
 * Centralizes lookup operations.
 * 
 * @author Miquel Sas
 */
public class Lookup {
	/**
	 * Lookup/select the instrument.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @return The selected instrument or null.
	 * @throws PersistorException If any persistence error occurs.
	 */
	public static Instrument selectIntrument(Session session, Server server) throws PersistorException {
		RecordSet recordSet = RecordSetUtils.getRecordSetAvailableInstruments(session, server);
		Record masterRecord = recordSet.getFieldList().getDefaultRecord();
		JLookupRecords lookup = new JLookupRecords(session, masterRecord);
		lookup.setTitle(server.getName() + " " + session.getString("qtMenuServersAvInst").toLowerCase());
		lookup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lookup.addColumn(Fields.INSTRUMENT_ID);
		lookup.addColumn(Fields.INSTRUMENT_DESC);
		lookup.addColumn(Fields.INSTRUMENT_PIP_VALUE);
		lookup.addColumn(Fields.INSTRUMENT_PIP_SCALE);
		lookup.addColumn(Fields.INSTRUMENT_TICK_VALUE);
		lookup.addColumn(Fields.INSTRUMENT_TICK_SCALE);
		lookup.addColumn(Fields.INSTRUMENT_VOLUME_SCALE);
		lookup.addColumn(Fields.INSTRUMENT_PRIMARY_CURRENCY);
		lookup.addColumn(Fields.INSTRUMENT_SECONDARY_CURRENCY);
		Record selected = lookup.lookupRecord(recordSet);
		return InstrumentUtils.getInstrumentFromRecordInstruments(selected);
	}
	
	/**
	 * Lookup/select the ticker.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @return The selected ticker or null.
	 * @throws PersistorException If any persistence error occurs.
	 */
	public static Record selectTicker(Session session, Server server) throws PersistorException {
		RecordSet recordSet = RecordSetUtils.getRecordSetTickers(session, server);
		Record masterRecord = recordSet.getFieldList().getDefaultRecord();
		JLookupRecords lookup = new JLookupRecords(session, masterRecord);
		lookup.setTitle(server.getName() + " " + session.getString("qtMenuServersTickers").toLowerCase());
		lookup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lookup.addColumn(Fields.INSTRUMENT_ID);
		lookup.addColumn(Fields.PERIOD_NAME);
		lookup.addColumn(Fields.TABLE_NAME);
		Record selected = lookup.lookupRecord(recordSet);
		return selected;
	}

}
