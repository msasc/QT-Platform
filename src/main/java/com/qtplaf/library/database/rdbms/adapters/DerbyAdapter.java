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
import com.qtplaf.library.util.FormatUtils;

/**
 * The Derby database adapter.
 *
 * @author Miquel Sas
 */
public class DerbyAdapter extends DBEngineAdapter {

	/**
	 * Default constructor.
	 */
	protected DerbyAdapter() {
		super();
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
		return "CURRENT_DATE";
	}

	/**
	 * Returns the CURRENT TIME function as a string.
	 *
	 * @return The CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() {
		return "CURRENT_TIME";
	}

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 *
	 * @return The CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() {
		return "CURRENT_TIMESTAMP";
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
			b.append("BLOB");
			b.append("(");
			b.append(field.getLength());
			b.append(")");
		} else if (type == Types.String) {
			if (field.getLength() <= Types.FIXED_LENGTH) {
				b.append("VARCHAR");
				b.append("(");
				b.append(Math.min(field.getLength(), Types.FIXED_LENGTH));
				b.append(")");
			} else {
				b.append("CLOB");
			}
		} else if (type == Types.Decimal) {
			b.append("DECIMAL");
			b.append("(");
			b.append(field.getLength());
			b.append(",");
			b.append(field.getDecimals());
			b.append(")");
		} else if (type == Types.Double) {
			b.append("DOUBLE");
		} else if (type == Types.Long) {
			b.append("BIGINT");
		} else if (type == Types.Integer) {
			b.append("INTEGER");
		} else if (type == Types.Date) {
			b.append("DATE");
		} else if (type == Types.Time) {
			b.append("TIME");
		} else if (type == Types.Timestamp) {
			b.append("TIMESTAMP");
		} else {
			throw new IllegalArgumentException("Invalid field type");
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
		return true;
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
		String syear = (sdate.length() == 8 ? sdate.substring(0, 4) : sdate.substring(0, 5));
		String smonth = (sdate.length() == 8 ? sdate.substring(4, 6) : sdate.substring(5, 7));
		String sday = (sdate.length() == 8 ? sdate.substring(6, 8) : sdate.substring(7, 9));
		return "'" + syear + "-" + smonth + "-" + sday + "'";
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
		String sHour = stime.substring(0, 2);
		String sMin = stime.substring(2, 4);
		String sSec = stime.substring(4, 6);
		return "'" + sHour + ":" + sMin + ":" + sSec + "'";
	}

	/**
	 * Return a string representation of the timestamp, valid to be used in an SQL statement.
	 *
	 * @param timestamp The timestamp.
	 * @return The representation.
	 */
	@Override
	public String toStringSQL(Timestamp timestamp) {
		String stime = FormatUtils.unformattedFromTimestamp(timestamp, false);
		String sYear = (stime.length() == 14 ? stime.substring(0, 4) : stime.substring(0, 5));
		String sMonth = (stime.length() == 14 ? stime.substring(4, 6) : stime.substring(5, 7));
		String sDay = (stime.length() == 14 ? stime.substring(6, 8) : stime.substring(7, 9));
		String sHour = (stime.length() == 14 ? stime.substring(8, 10) : stime.substring(9, 11));
		String sMin = (stime.length() == 14 ? stime.substring(10, 12) : stime.substring(11, 13));
		String sSec = (stime.length() == 14 ? stime.substring(12, 14) : stime.substring(13, 15));
		return "'" + sYear + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMin + ":" + sSec + "'";
	}
}
