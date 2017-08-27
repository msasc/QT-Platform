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
package com.qtplaf.library.database;

import java.text.MessageFormat;

/**
 * Simple data types supported by the system.
 *
 * @author Miquel Sas
 */
public enum Types {

	/**
	 * Type Boolean.
	 */
	Boolean,
	/**
	 * Type String.
	 */
	String,
	/**
	 * Type Decimal.
	 */
	Decimal,
	/**
	 * Type Double.
	 */
	Double,
	/**
	 * Type Integer.
	 */
	Integer,
	/**
	 * Type Long.
	 */
	Long,
	/**
	 * Type Date.
	 */
	Date,
	/**
	 * Type Time.
	 */
	Time,
	/**
	 * Type Timestamp.
	 */
	Timestamp,
	/**
	 * Type Binary.
	 */
	ByteArray,
	/**
	 * Type Value.
	 */
	Value,
	/**
	 * Value array.
	 */
	ValueArray,
	/**
	 * Type Object.
	 */
	Object;

	/**
	 * The fixed length to select VARCHAR/VARBINARY or LONVARCHAR/LONGVARBINARY.
	 */
	public static final int FIXED_LENGTH = 2000;

	/**
	 * Check if this type is a boolean.
	 *
	 * @return A boolean
	 */
	public boolean isBoolean() {
		return equals(Boolean);
	}

	/**
	 * Check if this type is a string.
	 *
	 * @return A boolean
	 */
	public boolean isString() {
		return equals(String);
	}

	/**
	 * Check if this type is a number with fixed precision (decimal)
	 *
	 * @return A boolean
	 */
	public boolean isDecimal() {
		return equals(Decimal);
	}

	/**
	 * Check if this type is a double.
	 *
	 * @return A boolean
	 */
	public boolean isDouble() {
		return equals(Double);
	}

	/**
	 * Check if this type is an integer.
	 *
	 * @return A boolean
	 */
	public boolean isInteger() {
		return equals(Integer);
	}

	/**
	 * Check if this type is a long.
	 *
	 * @return A boolean
	 */
	public boolean isLong() {
		return equals(Long);
	}

	/**
	 * Check if this value is a number type of value (decimal, double or integer)
	 *
	 * @return A boolean
	 */
	public boolean isNumber() {
		return isDecimal() || isDouble() || isInteger() || isLong();
	}

	/**
	 * Check if this type is a numeric foating point.
	 *
	 * @return A boolean.
	 */
	public boolean isFloatingPoint() {
		return isDouble();
	}

	/**
	 * Check if this type is a date.
	 *
	 * @return A boolean
	 */
	public boolean isDate() {
		return equals(Date);
	}

	/**
	 * Check if this type is a time.
	 *
	 * @return A boolean
	 */
	public boolean isTime() {
		return equals(Time);
	}

	/**
	 * Check if this type is a timestamp.
	 *
	 * @return A boolean
	 */
	public boolean isTimestamp() {
		return equals(Timestamp);
	}

	/**
	 * Check if this type is date, time or timestamp.
	 *
	 * @return A boolean
	 */
	public boolean isDateTimeOrTimestamp() {
		return isDate() || isTime() || isTimestamp();
	}

	/**
	 * Check if this type is a ByteArray.
	 *
	 * @return A boolean
	 */
	public boolean isByteArray() {
		return equals(ByteArray);
	}

	/**
	 * Check if this type is a ValueArray.
	 *
	 * @return A boolean
	 */
	public boolean isValueArray() {
		return equals(ValueArray);
	}

	/**
	 * Check if this type is a generic object.
	 *
	 * @return A boolean
	 */
	public boolean isObject() {
		return equals(Object);
	}

	/**
	 * Converts this type to a JDBV type
	 *
	 * @param length The length for string and binary data.
	 * @return The JDBC type.
	 */
	public int getJDBCType(int length) {
		switch (this) {
		case String:
			if (length <= FIXED_LENGTH) {
				return java.sql.Types.VARCHAR;
			}
			return java.sql.Types.LONGVARCHAR;
		case Decimal:
			return java.sql.Types.DECIMAL;
		case Boolean:
			return java.sql.Types.CHAR;
		case Double:
			return java.sql.Types.DOUBLE;
		case Integer:
			return java.sql.Types.INTEGER;
		case Long:
			return java.sql.Types.BIGINT;
		case Date:
			return java.sql.Types.DATE;
		case Time:
			return java.sql.Types.TIME;
		case Timestamp:
			return java.sql.Types.TIMESTAMP;
		case ByteArray:
			if (length <= FIXED_LENGTH) {
				return java.sql.Types.VARBINARY;
			}
			return java.sql.Types.LONGVARBINARY;
		default:
			break;
		}
		throw new IllegalArgumentException(MessageFormat.format("Unsupported type conversion to JDBC: {0}", this));
	}

	/**
	 * Returns the type with the given name, not case sensitive.
	 * 
	 * @param typeName The type name.
	 * @return The type.
	 * @throws IllegalArgumentException if the type name is not supported.
	 */
	public static Types parseType(String typeName) {
		Types[] types = values();
		for (Types type : types) {
			if (type.name().toLowerCase().equals(typeName.toLowerCase())) {
				return type;
			}
		}
		throw new IllegalArgumentException(MessageFormat.format("Unsupported type name: {0}", typeName));
	}
}
