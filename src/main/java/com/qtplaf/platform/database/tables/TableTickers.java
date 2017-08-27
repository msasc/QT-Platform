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
import com.qtplaf.platform.database.fields.FieldDataFilter;
import com.qtplaf.platform.database.fields.FieldInstrumentId;
import com.qtplaf.platform.database.fields.FieldOfferSide;
import com.qtplaf.platform.database.fields.FieldPeriodId;
import com.qtplaf.platform.database.fields.FieldServerId;
import com.qtplaf.platform.database.fields.FieldTableName;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Tickers table definition.
 * 
 * @author Miquel Sas
 */
public class TableTickers extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TableTickers(Session session) {
		super(session);

		setName(Tables.Tickers);
		setSchema(Schemas.qtp);

		addField(new FieldServerId(session, Fields.SERVER_ID));
		addField(new FieldInstrumentId(session, Fields.INSTRUMENT_ID));
		addField(new FieldPeriodId(session, Fields.PERIOD_ID));
		addField(new FieldOfferSide(session, Fields.OFFER_SIDE));
		addField(new FieldDataFilter(session, Fields.DATA_FILTER));
		addField(new FieldTableName(session, Fields.TABLE_NAME));

		getField(Fields.SERVER_ID).setPrimaryKey(true);
		getField(Fields.INSTRUMENT_ID).setPrimaryKey(true);
		getField(Fields.PERIOD_ID).setPrimaryKey(true);

		Table tablePeriods = new TablePeriods(session);
		ForeignKey fkPeriods = new ForeignKey(false);
		fkPeriods.setLocalTable(this);
		fkPeriods.setForeignTable(tablePeriods);
		fkPeriods.add(getField(Fields.PERIOD_ID), tablePeriods.getField(Fields.PERIOD_ID));
		addForeignKey(fkPeriods);

		Table tableOfferSides = new TableOfferSides(session);
		ForeignKey fkOfferSides = new ForeignKey(false);
		fkOfferSides.setLocalTable(this);
		fkOfferSides.setForeignTable(tableOfferSides);
		fkOfferSides.add(getField(Fields.OFFER_SIDE), tableOfferSides.getField(Fields.OFFER_SIDE));
		addForeignKey(fkOfferSides);

		Table tableDataFilters = new TableDataFilters(session);
		ForeignKey fkDataFilters = new ForeignKey(false);
		fkDataFilters.setLocalTable(this);
		fkDataFilters.setForeignTable(tableDataFilters);
		fkDataFilters.add(getField(Fields.DATA_FILTER), tableDataFilters.getField(Fields.DATA_FILTER));
		addForeignKey(fkDataFilters);
		
		Order order = new Order();
		order.add(getField(Fields.SERVER_ID));
		order.add(getField(Fields.INSTRUMENT_ID));
		order.add(tablePeriods.getField(Fields.PERIOD_UNIT_INDEX));
		order.add(tablePeriods.getField(Fields.PERIOD_SIZE));
		
		setPersistor(PersistorUtils.getPersistor(getComplexView(order)));
	}

}
