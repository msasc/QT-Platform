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
import com.qtplaf.platform.database.fields.FieldServerId;
import com.qtplaf.platform.database.fields.FieldServerName;
import com.qtplaf.platform.database.fields.FieldServerTitle;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Servers table definition.
 * 
 * @author Miquel Sas
 */
public class TableServers extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TableServers(Session session) {
		super(session);
		
		setName(Tables.Servers);
		setSchema(Schemas.qtp);
		
		addField(new FieldServerId(session, Fields.SERVER_ID));
		addField(new FieldServerName(session, Fields.SERVER_NAME));
		addField(new FieldServerTitle(session, Fields.SERVER_TITLE));
		
		getField(Fields.SERVER_ID).setPrimaryKey(true);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
