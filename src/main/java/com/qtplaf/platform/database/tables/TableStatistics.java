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
import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Table;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldInstrumentId;
import com.qtplaf.platform.database.fields.FieldPeriodId;
import com.qtplaf.platform.database.fields.FieldServerId;
import com.qtplaf.platform.database.fields.FieldStatisticsId;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Statistics table definition.
 *
 * @author Miquel Sas
 */
public class TableStatistics extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TableStatistics(Session session) {
		super(session);

		setName(Tables.Statistics);
		setSchema(Schemas.qtp);

		addField(new FieldServerId(session, Fields.SERVER_ID));
		addField(new FieldInstrumentId(session, Fields.INSTRUMENT_ID));
		addField(new FieldPeriodId(session, Fields.PERIOD_ID));
		addField(new FieldStatisticsId(session, Fields.STATISTICS_ID));

		getField(Fields.SERVER_ID).setPrimaryKey(true);
		getField(Fields.INSTRUMENT_ID).setPrimaryKey(true);
		getField(Fields.PERIOD_ID).setPrimaryKey(true);
		getField(Fields.STATISTICS_ID).setPrimaryKey(true);

		Table tablePeriods = new TablePeriods(session);
		ForeignKey fkPeriods = new ForeignKey(false);
		fkPeriods.setLocalTable(this);
		fkPeriods.setForeignTable(tablePeriods);
		fkPeriods.add(getField(Fields.PERIOD_ID), tablePeriods.getField(Fields.PERIOD_ID));
		addForeignKey(fkPeriods);
		
		Order order = new Order();
		order.add(getField(Fields.SERVER_ID));
		order.add(getField(Fields.INSTRUMENT_ID));
		order.add(tablePeriods.getField(Fields.PERIOD_UNIT_INDEX));
		order.add(tablePeriods.getField(Fields.PERIOD_SIZE));
		order.add(getField(Fields.STATISTICS_ID));
		
		setPersistor(PersistorUtils.getPersistor(getComplexView(order)));
	}

}
