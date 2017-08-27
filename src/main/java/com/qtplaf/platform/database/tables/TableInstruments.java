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

package com.qtplaf.platform.database.tables;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Table;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldInstrumentDesc;
import com.qtplaf.platform.database.fields.FieldInstrumentId;
import com.qtplaf.platform.database.fields.FieldInstrumentPipScale;
import com.qtplaf.platform.database.fields.FieldInstrumentPipValue;
import com.qtplaf.platform.database.fields.FieldInstrumentPrimaryCurrency;
import com.qtplaf.platform.database.fields.FieldInstrumentSecondaryCurrency;
import com.qtplaf.platform.database.fields.FieldInstrumentTickScale;
import com.qtplaf.platform.database.fields.FieldInstrumentTickValue;
import com.qtplaf.platform.database.fields.FieldInstrumentVolumeScale;
import com.qtplaf.platform.database.fields.FieldServerId;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Instruments table definition.
 * 
 * @author Miquel Sas
 */
public class TableInstruments extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TableInstruments(Session session) {
		super(session);
		
		setName(Tables.Instruments);
		setSchema(Schemas.qtp);
		
		addField(new FieldServerId(session, Fields.SERVER_ID));
		addField(new FieldInstrumentId(session, Fields.INSTRUMENT_ID));
		addField(new FieldInstrumentDesc(session, Fields.INSTRUMENT_DESC));
		addField(new FieldInstrumentPipValue(session, Fields.INSTRUMENT_PIP_VALUE));
		addField(new FieldInstrumentPipScale(session, Fields.INSTRUMENT_PIP_SCALE));
		addField(new FieldInstrumentTickValue(session, Fields.INSTRUMENT_TICK_VALUE));
		addField(new FieldInstrumentTickScale(session, Fields.INSTRUMENT_TICK_SCALE));
		addField(new FieldInstrumentVolumeScale(session, Fields.INSTRUMENT_VOLUME_SCALE));
		addField(new FieldInstrumentPrimaryCurrency(session, Fields.INSTRUMENT_PRIMARY_CURRENCY));
		addField(new FieldInstrumentSecondaryCurrency(session, Fields.INSTRUMENT_SECONDARY_CURRENCY));
		
		getField(Fields.SERVER_ID).setPrimaryKey(true);
		getField(Fields.INSTRUMENT_ID).setPrimaryKey(true);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
