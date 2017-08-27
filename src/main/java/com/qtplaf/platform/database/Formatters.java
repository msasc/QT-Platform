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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.TickValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.database.formatters.VolumeValue;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Centralizes formatters intallation into persistors.
 * 
 * @author Miquel Sas
 */
public class Formatters {

	/**
	 * Returns the time formatter.
	 * 
	 * @param period The period.
	 * @return The formatter.
	 */
	public static TimeFmtValue getTimeFmtValue(Period period) {
		return new TimeFmtValue(period.getUnit());
	}

	/**
	 * Returns the tick value formatter.
	 * 
	 * @param session The session.
	 * @param instrument The instrument.
	 * @return The formatter.
	 */
	public static TickValue getTickValue(Session session, Instrument instrument) {
		return new TickValue(session, instrument);
	}

	/**
	 * Returnas the appropriate value formatter.
	 * 
	 * @param session Working session.
	 * @param suffix The type suffix.
	 * @return The formatter.
	 */
	public static DataValue getValueFormatter(Session session, String suffix) {
		if (suffix.equals(Fields.Suffix.RAW)) {
			return new DataValue(session, 10);
		}
		if (suffix.equals(Fields.Suffix.NRM)) {
			return new DataValue(session, 4);
		}
		if (suffix.equals(Fields.Suffix.DSC)) {
			return new DataValue(session, 4);
		}
		if (suffix.equals(Fields.Suffix.IN)) {
			return new DataValue(session, 4);
		}
		if (suffix.equals(Fields.Suffix.OUT)) {
			return new DataValue(session, 4);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Set formatters to the data price persistor.
	 *
	 * @param session Working session.
	 * @param persistor The persistor.
	 * @param serverId The server id.
	 * @param instrId The instrument id.
	 * @param periodId The period id.
	 * @throws PersistorException If any persistence error occurs.
	 */
	public static void configureDataPrice(
		Session session,
		Persistor persistor,
		String serverId,
		String instrId,
		String periodId)
		throws PersistorException {

		// Time based on period.
		Period period = Period.parseId(periodId);
		TimeFmtValue timeFmt = new TimeFmtValue(period.getUnit());
		persistor.getField(Fields.TIME_FMT).setFormatter(timeFmt);
		persistor.getField(Fields.TIME_FMT).setCalculator(timeFmt);

		Record recordInstr = RecordUtils.getRecordInstrument(session, serverId, instrId);
		Instrument instrument = InstrumentUtils.getInstrumentFromRecordInstruments(recordInstr);
		persistor.getField(Fields.OPEN).setFormatter(new TickValue(session, instrument));
		persistor.getField(Fields.HIGH).setFormatter(new TickValue(session, instrument));
		persistor.getField(Fields.LOW).setFormatter(new TickValue(session, instrument));
		persistor.getField(Fields.CLOSE).setFormatter(new TickValue(session, instrument));
		persistor.getField(Fields.VOLUME).setFormatter(new VolumeValue(session, instrument));
	}

}
