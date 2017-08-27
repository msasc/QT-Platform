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
package com.qtplaf.library.database.rdbms;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.Time;
import com.qtplaf.library.util.Timestamp;

/**
 * Database utilities to write to a prepared statement or to read from a resultset.
 *
 * @author Miquel Sas
 */
public class DBUtils {

	/**
	 * Read a record from a ResultSet.
	 *
	 * @param fieldList The field list
	 * @param rs The source result set
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public static Record readRecord(FieldList fieldList, ResultSet rs) throws SQLException {
		Record record = new Record();
		record.setFieldList(fieldList);
		List<Field> fields = fieldList.getFields();
		List<Value> values = new ArrayList<>(fields.size());
		int index = 1;
		for (Field field : fields) {
			Types type = field.getType();
			int decimals = field.getDecimals();
			Value value;
			if (field.isPersistent() || field.isVirtual()) {
				value = DBUtils.fromResultSet(type, decimals, index++, rs);
			} else {
				value = field.getDefaultValue();
			}
			values.add(value);
		}
		record.setValues(values);
		return record;
	}

	/**
	 * Reads a value from a result set.
	 *
	 * @param type The type
	 * @param decimals The scale
	 * @param index The index in the result set
	 * @param resultSet The result set
	 * @return The appropriate value.
	 * @throws SQLException If such an error occurs.
	 */
	public static Value fromResultSet(Types type, int decimals, int index, ResultSet resultSet) throws SQLException {
		Value value = null;
		if (type == Types.Boolean) {
			String s = resultSet.getString(index);
			boolean b = (s != null && s.equals("Y"));
			value = new Value(b);
		} else if (type == Types.Decimal) {
			BigDecimal bd = resultSet.getBigDecimal(index);
			if (bd != null) {
				bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_UP);
				value = new Value(bd);
			}
		} else if (type == Types.Integer) {
			value = new Value(resultSet.getInt(index));
		} else if (type == Types.Long) {
			value = new Value(resultSet.getLong(index));
		} else if (type == Types.Double) {
			value = new Value(resultSet.getDouble(index));
		} else if (type == Types.Date) {
			java.sql.Date date = resultSet.getDate(index);
			if (date == null) {
				value = new Value((Date) null);
			} else {
				Calendar calendar = new Calendar(date.getTime());
				value = new Value(calendar.toDate());
			}
		} else if (type == Types.Time) {
			java.sql.Time time = resultSet.getTime(index);
			if (time == null) {
				value = new Value((Time) null);
			} else {
				Calendar calendar = new Calendar(time.getTime());
				value = new Value(calendar.toTime());
			}
		} else if (type == Types.Timestamp) {
			java.sql.Timestamp timestamp = resultSet.getTimestamp(index);
			if (timestamp == null) {
				value = new Value((Timestamp) timestamp);
			} else {
				value = new Value(new Timestamp(timestamp.getTime()));
			}
		} else if (type == Types.String) {
			value = new Value(resultSet.getString(index));
		} else if (type == Types.ByteArray) {
			value = new Value(resultSet.getBytes(index));
		}
		if (!type.isNumber() && resultSet.wasNull()) {
			if (value != null) {
				value.setNull();
			}
		}
		return value;
	}

	/**
	 * Set the value to a <code>PreparedStatement</code> parameter at the specified index.
	 *
	 * @param value The value to set to the prepared statement.
	 * @param index The parameter index.
	 * @param ps The <code>PreparedStatement</code>.
	 * @throws SQLException If such an error occurs.
	 */
	public static void toPreparedStatement(Value value, int index, PreparedStatement ps) throws SQLException {
		Types type = value.getType();
		if (value.isNull()) {
			ps.setNull(index, type.getJDBCType(0));
		} else if (type == Types.Boolean) {
			ps.setString(index, (value.getBoolean() ? "Y" : "N"));
		} else if (type == Types.Decimal) {
			ps.setBigDecimal(index, value.getBigDecimal());
		} else if (type == Types.Double) {
			ps.setDouble(index, value.getDouble());
		} else if (type == Types.Integer) {
			ps.setInt(index, value.getInteger());
		} else if (type == Types.Long) {
			ps.setLong(index, value.getLong());
		} else if (type == Types.String) {
			int length = value.getString().length();
			if (length <= Types.FIXED_LENGTH) {
				ps.setString(index, value.getString());
			} else {
				String string = value.getString();
				ps.setCharacterStream(index, new StringReader(string), string.length());
			}
		} else if (type == Types.ByteArray) {
			int length = value.getByteArray().size();
			if (length <= Types.FIXED_LENGTH) {
				ps.setBytes(index, value.getByteArray().getBytes());
			} else {
				byte[] bytes = value.getByteArray().getBytes();
				ps.setBinaryStream(index, new ByteArrayInputStream(bytes), bytes.length);
			}
		} else if (type == Types.Date) {
			ps.setDate(index, value.getDate());
		} else if (type == Types.Time) {
			ps.setTime(index, value.getTime());
		} else if (type == Types.Timestamp) {
			ps.setTimestamp(index, value.getTimestamp());
		}
	}
}
