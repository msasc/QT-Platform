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
package com.qtplaf.library.database.rdbms.adapters;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import javax.sql.DataSource;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.DataSourceInfo;
import com.qtplaf.library.database.rdbms.adapters.sql.OracleCreateSchema;
import com.qtplaf.library.database.rdbms.adapters.sql.OracleDropSchema;
import com.qtplaf.library.database.rdbms.sql.CreateSchema;
import com.qtplaf.library.database.rdbms.sql.DropSchema;
import com.qtplaf.library.util.FormatUtils;

/**
 * The Oracle database adapter.
 * 
 * @author Miquel Sas
 */
public class OracleAdapter extends DBEngineAdapter {

	/**
	 * Default constructor.
	 */
	public OracleAdapter() {
		super();
		setDriverClassName("oracle.jdbc.driver.OracleDriver");
	}

	/**
	 * Returns a appropriate data source.
	 * 
	 * @param info The data source info.
	 * @return The data source.
	 */
	@Override
	public DataSource getDataSource(DataSourceInfo info) {
		return null;
	}

	/**
	 * Returns the CURRENT DATE function as a string.
	 *
	 * @return The CURRENT DATE function as a string.
	 */
	@Override
	public String getCurrentDate() {
		return "SYSDATE";
	}

	/**
	 * Returns the CURRENT TIME function as a string.
	 *
	 * @return The CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() {
		return "SYSDATE";
	}

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 *
	 * @return The CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() {
		return "SYSDATE";
	}

	/**
	 * Gets the field definition to use in a <code>CREATE TABLE</code> statement, given a field.
	 *
	 * @return The field definition.
	 * @param field The field.
	 */
	@Override
	public String getFieldDefinition(Field field) {
		StringBuilder b = new StringBuilder();

		b.append(field.getNameCreate());
		b.append(" ");

		Types type = field.getType();
		if (type == Types.Boolean) {
			b.append("CHAR(1)");
		} else if (type == Types.ByteArray) {
			if (field.getLength() <= Types.FIXED_LENGTH) {
				b.append("RAW");
				b.append("(");
				b.append(field.getLength());
				b.append(")");
			} else {
				b.append("LONG RAW");
			}
		} else if (type == Types.String) {
			if (field.getLength() <= Types.FIXED_LENGTH) {
				b.append("VARCHAR2");
				b.append("(");
				b.append(Math.min(field.getLength(), Types.FIXED_LENGTH));
				b.append(")");
			} else {
				b.append("LONG");
			}
		} else if (type == Types.Decimal) {
			b.append("NUMBER");
			b.append("(");
			b.append(field.getLength());
			b.append(",");
			b.append(field.getDecimals());
			b.append(")");
		} else if (type == Types.Double) {
			b.append("NUMBER");
		} else if (type == Types.Long) {
			b.append("NUMBER");
		} else if (type == Types.Integer) {
			b.append("NUMBER");
		} else if (type == Types.Date) {
			b.append("DATE");
		} else if (type == Types.Time) {
			b.append("DATE");
		} else if (type == Types.Timestamp) {
			b.append("DATE");
		} else {
			throw new IllegalArgumentException("Invalid field type to create the field");
		}

		return b.toString();
	}

	/**
	 * Check if the underlying database accepts explicit relations.
	 *
	 * @return A boolean.
	 */
	@Override
	public boolean isExplicitRelation() {
		return false;
	}

	/**
	 * Return a string representation of the date, valid to be used in an SQL statement.
	 * 
	 * @param date The date.
	 * @return The representation.
	 */
	@Override
	public String toStringSQL(Date date) {
		String sdate = FormatUtils.unformattedFromDate(date);
		return "TO_DATE('" + sdate + "','SYYYYMMDD')";
	}

	/**
	 * Return a string representation of the time, valid to be used in an SQL statement.
	 * 
	 * @param time The time.
	 * @return The representation.
	 */
	@Override
	public String toStringSQL(Time time) {
		String stime = FormatUtils.unformattedFromTime(time);
		return "TO_DATE('" + stime + "','HH24MISS')";
	}

	/**
	 * Return a string representation of the timestamp, valid to be used in an SQL statement.
	 * <p>
	 * 
	 * @param timestamp The timestamp.
	 * @return The representation.
	 */
	@Override
	public String toStringSQL(Timestamp timestamp) {
		String stime = FormatUtils.unformattedFromTimestamp(timestamp, false);
		return "TO_DATE('" + stime + "','YYYYMMDDHH24MISS')";
	}

	/**
	 * Returns the create schema statement.
	 * 
	 * @param schema The schema to create.
	 * @return The statement.
	 */
	@Override
	public CreateSchema getStatementCreateSchema(String schema) {
		OracleCreateSchema createSchema = new OracleCreateSchema();
		createSchema.setDBEngineAdapter(this);
		createSchema.setSchema(schema);
		return createSchema;
	}

	/**
	 * Returns the drop schema statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DROP TABLE statement.
	 *
	 * @param schema The schema.
	 * @return The drop schema statement.
	 */
	@Override
	public DropSchema getStatementDropSchema(String schema) {
		OracleDropSchema dropSchema = new OracleDropSchema();
		dropSchema.setDBEngineAdapter(this);
		dropSchema.setSchema(schema);
		return dropSchema;
	}

}
