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
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Table;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldPeriodId;
import com.qtplaf.platform.database.fields.FieldPeriodName;
import com.qtplaf.platform.database.fields.FieldPeriodSize;
import com.qtplaf.platform.database.fields.FieldPeriodUnitIndex;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Servers table definition.
 * 
 * @author Miquel Sas
 */
public class TablePeriods extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TablePeriods(Session session) {
		super(session);
		
		setName(Tables.Periods);
		setSchema(Schemas.qtp);
		
		addField(new FieldPeriodId(session, Fields.PERIOD_ID));
		addField(new FieldPeriodName(session, Fields.PERIOD_NAME));
		addField(new FieldPeriodUnitIndex(session, Fields.PERIOD_UNIT_INDEX));
		addField(new FieldPeriodSize(session, Fields.PERIOD_SIZE));
		
		getField(Fields.PERIOD_ID).setPrimaryKey(true);

		Order order = new Order();
		order.add(getField(Fields.PERIOD_UNIT_INDEX));
		order.add(getField(Fields.PERIOD_SIZE));

		setPersistor(PersistorUtils.getPersistor(getSimpleView(order)));
	}

}
