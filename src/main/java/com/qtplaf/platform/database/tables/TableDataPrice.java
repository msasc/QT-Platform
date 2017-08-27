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
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.fields.FieldDataInstr;
import com.qtplaf.platform.database.fields.FieldIndex;
import com.qtplaf.platform.database.fields.FieldTime;
import com.qtplaf.platform.database.fields.FieldTimeFmt;
import com.qtplaf.platform.database.fields.FieldVolume;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Price table definition.
 * 
 * @author Miquel Sas
 */
public class TableDataPrice extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument Instrument.
	 * @param name The table name.
	 */
	public TableDataPrice(Session session, Server server, Instrument instrument, String name) {
		super(session);

		setName(name);
		setSchema(Schemas.server(server));

		addField(new FieldIndex(session, Fields.INDEX));
		addField(new FieldTime(session, Fields.TIME));
		addField(new FieldDataInstr(session, instrument, Fields.OPEN, "Open", "Open value"));
		addField(new FieldDataInstr(session, instrument, Fields.HIGH, "High", "High value"));
		addField(new FieldDataInstr(session, instrument, Fields.LOW, "Low", "Low value"));
		addField(new FieldDataInstr(session, instrument, Fields.CLOSE, "Close", "Close value"));
		addField(new FieldVolume(session, Fields.VOLUME));
		addField(new FieldTimeFmt(session, Fields.TIME_FMT));

		getField(Fields.TIME).setPrimaryKey(true);
		
		Index index = new Index();
		index.add(getField(Fields.INDEX));
		index.setUnique(true);
		addIndex(index);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
