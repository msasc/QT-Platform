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

import java.awt.Color;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.Time;
import com.qtplaf.library.util.Timestamp;

/**
 * A generic container for a value of different types. Valid types for a Value are Boolean, String, Decimal, Double,
 * Integer, Long, Date, Time, Timestamp, ByteArray.
 *
 * Note that other types will be included uppon need.
 *
 * @author Miquel Sas
 */
public class Value implements Comparable<Object> {

	/**
	 * The value type.
	 */
	private Types type;
	/**
	 * The value itself.
	 */
	private Object value;
	/**
	 * Modified flag.
	 */
	private boolean modified;
	/**
	 * An optional label for the value.
	 */
	private String label;
	/**
	 * Foreground color.
	 */
	private Color foregroundColor;
	/**
	 * Background color.
	 */
	private Color backgroundColor;

	/**
	 * The number of decimal places when it is a big decimal. It is neccesary to save it when created, because the value
	 * can be set to null and then set again through <code>setDouble</code>, for instance, and we do not want to loose
	 * the number of decimal places or scale.
	 */
	private int decimals = -1;

	/**
	 * Private constructor for internal usage.
	 */
	private Value() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param v A value
	 */
	public Value(Value v) {
		super();
		if (v == null) {
			throw new NullPointerException();
		}
		label = v.label;
		type = v.type;
		value = v.value;
		foregroundColor = v.foregroundColor;
		backgroundColor = v.backgroundColor;
	}

	/**
	 * Constructor assigning a value array.
	 * 
	 * @param valueArray A value array.
	 */
	public Value(ValueArray valueArray) {
		super();
		value = valueArray;
		type = Types.VALUEARRAY;
	}

	/**
	 * Constructor assigning a boolean.
	 *
	 * @param b A boolean
	 */
	public Value(boolean b) {
		super();
		value = Boolean.valueOf(b);
		type = Types.BOOLEAN;
	}

	/**
	 * Constructor assigning a boolean.
	 *
	 * @param b A boolean
	 */
	public Value(Boolean b) {
		super();
		value = b;
		type = Types.BOOLEAN;
	}

	/**
	 * Constructor assigning a string.
	 *
	 * @param s A string
	 */
	public Value(String s) {
		super();
		value = s;
		type = Types.STRING;
	}

	/**
	 * Constructor assigning a number with precision. Note that for a null big decimal the precision is set to 0.
	 *
	 * @param b A big decimal
	 */
	public Value(BigDecimal b) {
		super();
		value = b;
		type = Types.DECIMAL;
		if (b == null) {
			decimals = 0;
		} else {
			decimals = b.scale();
		}
	}

	/**
	 * Constructor assigning a double.
	 *
	 * @param d A double
	 */
	public Value(Double d) {
		super();
		value = d;
		type = Types.DOUBLE;
		decimals = -1;
	}

	/**
	 * Constructor assigning a double.
	 *
	 * @param d A double
	 */
	public Value(double d) {
		super();
		value = d;
		type = Types.DOUBLE;
		decimals = -1;
	}

	/**
	 * Constructor assigning an integer.
	 *
	 * @param i An integer
	 */
	public Value(Integer i) {
		super();
		value = i;
		type = Types.INTEGER;
		decimals = 0;
	}

	/**
	 * Constructor assigning an integer.
	 *
	 * @param i An integer
	 */
	public Value(int i) {
		super();
		value = i;
		type = Types.INTEGER;
		decimals = 0;
	}

	/**
	 * Constructor assigning a long.
	 *
	 * @param l A long
	 */
	public Value(Long l) {
		super();
		value = l;
		type = Types.LONG;
		decimals = 0;
	}

	/**
	 * Constructor assigning a long.
	 *
	 * @param l A long
	 */
	public Value(long l) {
		super();
		value = l;
		type = Types.LONG;
		decimals = 0;
	}

	/**
	 * Constructor assigning a ByteArray
	 *
	 * @param byteArray The ByteArray
	 */
	public Value(ByteArray byteArray) {
		super();
		value = byteArray;
		type = Types.BYTEARRAY;
	}

	/**
	 * Constructor assigning a binary (byte[])
	 *
	 * @param b The byte array
	 */
	public Value(byte[] b) {
		super();
		ByteArray byteArray = new ByteArray(b.length);
		byteArray.addAll(b);
		value = byteArray;
		type = Types.BYTEARRAY;
	}

	/**
	 * Constructor assigning a date.
	 *
	 * @param d The date
	 */
	public Value(Date d) {
		super();
		value = d;
		type = Types.DATE;
	}

	/**
	 * Constructor assigning a time.
	 *
	 * @param t The time
	 */
	public Value(Time t) {
		super();
		value = t;
		type = Types.TIME;
	}

	/**
	 * Constructor assigning a timestamp.
	 *
	 * @param t The timestamp
	 */
	public Value(Timestamp t) {
		super();
		value = t;
		type = Types.TIMESTAMP;
	}

	/**
	 * Returns a copy of this value.
	 * 
	 * @return The copy.
	 */
	public Value getCopy() {
		Value v = new Value();
		v.type = type;
		v.label = label;
		v.modified = modified;
		v.decimals = decimals;
		if (isNull()) {
			return v;
		}
		switch (type) {
		case BOOLEAN:
			v.value = new Boolean(getBoolean());
			break;
		case BYTEARRAY:
			v.value = new ByteArray(getByteArray());
			break;
		case DATE:
			v.value = new Date(getDate());
			break;
		case DECIMAL:
			v.value = new BigDecimal(getDouble()).setScale(decimals, BigDecimal.ROUND_HALF_UP);
			break;
		case DOUBLE:
			v.value = new Double(getDouble());
			break;
		case INTEGER:
			v.value = new Integer(getInteger());
			break;
		case LONG:
			v.value = new Long(getLong());
			break;
		case OBJECT:
			v.value = value;
			break;
		case STRING:
			v.value = new String(getString());
			break;
		case TIME:
			v.value = new Time(getTime());
			break;
		case TIMESTAMP:
			v.value = new Timestamp(getTimestamp());
			break;
		case VALUEARRAY:
			v.value = new ValueArray(getValueArray());
			break;
		default:
			v.value = value;
			break;
		}
		return v;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the
	 * specified value.
	 *
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(Object o) {
		// Argument cant be null
		if (o == null) {
			throw new NullPointerException();
		}
		// Must be the same type
		Value v = null;
		try {
			v = (Value) o;
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
		}

		// Null types
		if (isNull() && v.isNull()) {
			return 0;
		}
		if (isNull() && !v.isNull()) {
			return -1;
		}
		if (!isNull() && v.isNull()) {
			return 1;
		}
		// Compare only if comparable
		if (isBoolean()) {
			if (!v.isBoolean()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			boolean b1 = getBoolean();
			boolean b2 = v.getBoolean();
			return (!b1 && b2 ? -1 : (b1 && !b2 ? 1 : 0));
		}
		if (isNumber()) {
			if (!v.isNumber()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			double d1 = getDouble();
			double d2 = v.getDouble();
			return Double.compare(d1, d2);
		}
		if (isString()) {
			if (!v.isString()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			return getString().compareTo(v.getString());
		}
		if (isDate()) {
			if (!v.isDate()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			return getDate().compareTo(v.getDate());
		}
		if (isTime()) {
			if (!v.isTime()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			return getTime().compareTo(v.getTime());
		}
		if (isTimestamp()) {
			if (!v.isTimestamp()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			return getTimestamp().compareTo(v.getTimestamp());
		}
		if (isByteArray()) {
			if (!v.isByteArray()) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			return getByteArray().compareTo(v.getByteArray());
		}
		throw new IllegalArgumentException(MessageFormat.format("Value {0} is not comparable", toString()));
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @return A boolean.
	 * @param o The object to compare with.
	 */
	@Override
	public boolean equals(Object o) {
		// Null
		if (o == null) {
			return isNull();
		}
		if (isNull()) {
			return false;
		}
		// Boolean
		if (o instanceof Boolean) {
			if (!isBoolean()) {
				return false;
			}
			Boolean b = (Boolean) o;
			return getBoolean().equals(b);
		}
		// String
		if (o instanceof String) {
			if (!isString()) {
				return false;
			}
			String s = (String) o;
			return getString().equals(s);
		}
		// Decimal, Double, Integer, Long
		if (o instanceof Number) {
			if (!isNumber()) {
				return false;
			}
			Number n = (Number) o;
			return getNumber().equals(n);
		}
		// Date
		if (o instanceof Date) {
			if (!isDate()) {
				return false;
			}
			Date d = (Date) o;
			return getDate().equals(d);
		}
		// Time
		if (o instanceof Time) {
			if (!isTime()) {
				return false;
			}
			Time t = (Time) o;
			return getTime().equals(t);
		}
		// Timestamp
		if (o instanceof Timestamp) {
			if (!isTimestamp()) {
				return false;
			}
			Timestamp t = (Timestamp) o;
			return getTimestamp().equals(t);
		}
		// ByteArray
		if (o instanceof ByteArray) {
			if (!isByteArray()) {
				return false;
			}
			ByteArray byteArray = (ByteArray) o;
			return getByteArray().equals(byteArray);
		}
		// Value
		if (o instanceof Value) {
			Value v = (Value) o;
			// Types must be the same except for numbers where the number must be the same
			if ((isBoolean() && !v.isBoolean()) || (!isBoolean() && v.isBoolean())) {
				return false;
			}
			if ((isString() && !v.isString()) || (!isString() && v.isString())) {
				return false;
			}
			if ((isDateTimeOrTimestamp() && !v.isDateTimeOrTimestamp())
				|| (!isDateTimeOrTimestamp() && v.isDateTimeOrTimestamp())) {
				return false;
			}
			if ((isNumber() && !v.isNumber()) || (!isNumber() && v.isNumber())) {
				return false;
			}
			if ((isByteArray() && !v.isByteArray()) || (!isByteArray() && v.isByteArray())) {
				return false;
			}
			if (isBoolean()) {
				return getBoolean().equals(v.getBoolean());
			}
			if (isString()) {
				return getString().equals(v.getString());
			}
			if (isDateTimeOrTimestamp()) {
				return getTimestamp().equals(v.getTimestamp());
			}
			if (isNumber()) {
				return getNumber().equals(v.getNumber());
			}
			if (isByteArray()) {
				return getByteArray().equals(v.getByteArray());
			}
		}
		return false;
	}

	/**
	 * Direct equals.
	 *
	 * @param b Boolean to compare with.
	 * @return A boolean
	 */
	public boolean equals(boolean b) {
		return equals(Boolean.valueOf(b));
	}

	/**
	 * Direct equals.
	 *
	 * @param bytes Array of bytes to compare with.
	 * @return A boolean
	 */
	public boolean equals(byte[] bytes) {
		ByteArray byteArray = new ByteArray(bytes.length);
		byteArray.addAll(bytes);
		return equals(byteArray);
	}

	/**
	 * Direct equals.
	 *
	 * @param d Value to compare with.
	 * @return A boolean
	 */
	public boolean equals(double d) {
		return equals(Double.valueOf(d));
	}

	/**
	 * Direct equals.
	 *
	 * @param i The integer to compare with.
	 * @return A boolean
	 */
	public boolean equals(int i) {
		return equals(Integer.valueOf(i));
	}

	/**
	 * Direct equals.
	 *
	 * @param l The long to compare with.
	 * @return A boolean
	 */
	public boolean equals(long l) {
		return equals(Long.valueOf(l));
	}

	/**
	 * Returns the hash code for this value.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		if (!isNull()) {
			if (isBoolean()) {
				Boolean b = getBoolean();
				return b.hashCode();
			}
			if (isByteArray()) {
				return getByteArray().hashCode();
			}
			if (isNumber()) {
				Double d = getDouble();
				return d.hashCode();
			}
			if (isString()) {
				return getString().hashCode();
			}
			if (isDate()) {
				return getDate().hashCode();
			}
			if (isTime()) {
				return getTime().hashCode();
			}
			if (isTimestamp()) {
				return getTimestamp().hashCode();
			}
		}
		return NumberUtils.MIN_INTEGER;
	}

	/**
	 * Returns this value type.
	 *
	 * @return The type.
	 */
	public Types getType() {
		return type;
	}

	/**
	 * Get the value as a <code>boolean</code>.
	 *
	 * @return A boolean
	 */
	public Boolean getBoolean() {
		if (isBoolean()) {
			if (isNull()) {
				return false;
			}
			return ((Boolean) value);
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a boolean", value));
	}

	/**
	 * Get the value as a <code>String</code>.
	 *
	 * @return A String
	 */
	public String getString() {
		if (!isString()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a string", value));
		}
		return (String) value;
	}

	/**
	 * Get the value as a <code>Date</code>.
	 *
	 * @return A Date
	 */
	public Date getDate() {
		if (isDate()) {
			return (Date) value;
		}
		if (isNull()) {
			return null;
		}
		if (isTimestamp()) {
			return new Date(getTimestamp().getTime());
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a date", value));
	}

	/**
	 * Returns the number of decimal places. If it is not a number it throws an unsupported operation exception,
	 * otherwise it returns the number of decimal places, 0 or greater if it is a big decimal, 0 if it is an integer or
	 * a long, and -1 if it is a double.
	 * 
	 * @return The number of decimal places.
	 */
	public int getDecimals() {
		if (!isNumber()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
		}
		return decimals;
	}

	/**
	 * Get the value as a <code>Time</code>.
	 *
	 * @return A Time
	 */
	public Time getTime() {
		if (isTime()) {
			return (Time) value;
		}
		if (isNull()) {
			return null;
		}
		if (isTimestamp()) {
			return new Time(getTimestamp().getTime());
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a time", value));
	}

	/**
	 * Get the value as a <code>Timestamp</code>.
	 *
	 * @return A Timestamp
	 */
	public Timestamp getTimestamp() {
		if (isTimestamp()) {
			return (Timestamp) value;
		}
		if (isNull()) {
			return null;
		}
		if (isDate()) {
			return new Timestamp(getDate().getTime());
		}
		if (isTime()) {
			return new Timestamp(getTime().getTime());
		}
		throw new UnsupportedOperationException(
			MessageFormat.format("Value {0} is not a date, time or timestamp", value));
	}

	/**
	 * Get the value as a ByteArray.
	 *
	 * @return A ByteArray
	 */
	public ByteArray getByteArray() {
		if (isByteArray()) {
			return (ByteArray) value;
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a byte array", value));
	}

	/**
	 * Get the value as a ValueArray.
	 *
	 * @return A ValueArray
	 */
	public ValueArray getValueArray() {
		if (isValueArray()) {
			return (ValueArray) value;
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a value array", value));
	}

	/**
	 * Get the value as a number if it is so.
	 *
	 * @return The number.
	 */
	public Number getNumber() {
		if (isNumber()) {
			return (Number) value;
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
	}

	/**
	 * Check if this value is boolean.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return getType().isBoolean();
	}

	/**
	 * Check if this value is a string.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return getType().isString();
	}

	/**
	 * Check if this value is a number (decimal) with fixed precision.
	 *
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return getType().isDecimal();
	}

	/**
	 * Check if this value is a double.
	 *
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return getType().isDouble();
	}

	/**
	 * Check if this value is an integer.
	 *
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return getType().isInteger();
	}

	/**
	 * Check if this value is a long.
	 *
	 * @return A boolean.
	 */
	public boolean isLong() {
		return getType().isLong();
	}

	/**
	 * Check if this value is a number (decimal, double or integer).
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return getType().isNumber();
	}

	/**
	 * Check if this value is a floating point number.
	 *
	 * @return A boolean.
	 */
	public boolean isFloatingPoint() {
		return getType().isFloatingPoint();
	}

	/**
	 * Check if this value is a date.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() {
		return getType().isDate();
	}

	/**
	 * Check if this value is a time.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() {
		return getType().isTime();
	}

	/**
	 * Check if this value is a time.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return getType().isTimestamp();
	}

	/**
	 * Check if this value is a date, time or timestamp.
	 *
	 * @return A boolean.
	 */
	public boolean isDateTimeOrTimestamp() {
		return getType().isDateTimeOrTimestamp();
	}

	/**
	 * Check if this value is binary (byte[]).
	 *
	 * @return A boolean.
	 */
	public boolean isByteArray() {
		return getType().isByteArray();
	}

	/**
	 * Check if this value is a value array.
	 *
	 * @return A boolean.
	 */
	public boolean isValueArray() {
		return getType().isValueArray();
	}

	/**
	 * Check if this value is null. Null is not a type, but a value can be null if the holder object its so.
	 *
	 * @return A boolean indicating if the value is null.
	 */
	public boolean isNull() {
		return (value == null);
	}

	/**
	 * Check if the value is empty, that is, null, empty string or zero if is number.
	 *
	 * @return A boolean
	 */
	public boolean isEmpty() {
		if (isNull()) {
			return true;
		}
		if (isString() && getString().length() == 0) {
			return true;
		}
		return isNumber() && getDouble() == 0;
	}

	/**
	 * Check if this value is empty or a blank string (only spaces).
	 *
	 * @return A boolean
	 */
	public boolean isBlank() {
		return isEmpty() || (isString() && getString().trim().length() == 0);
	}

	/**
	 * Get the value as a BigDecimal it it's a number, otherwise throw an exception.
	 *
	 * @return A BigDecimal
	 */
	public BigDecimal getBigDecimal() {
		if (isDecimal()) {
			return (BigDecimal) value;
		}
		if (isNumber()) {
			if (isDouble()) {
				return new BigDecimal(getDouble());
			}
			return new BigDecimal(getLong()).setScale(decimals, BigDecimal.ROUND_HALF_UP);
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
	}

	/**
	 * Get the value as a double it it's a number, otherwise throw an exception.
	 *
	 * @return A double
	 */
	public Double getDouble() {
		if (isNumber()) {
			if (isNull()) {
				return (double) 0;
			}
			return ((Number) value).doubleValue();
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
	}

	/**
	 * Get the value as an <code>int</code>.
	 *
	 * @return An integer
	 */
	public Integer getInteger() {
		if (isNumber()) {
			if (isNull()) {
				return (int) 0;
			}
			return ((Number) value).intValue();
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
	}

	/**
	 * Get the optional label.
	 *
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the value as an <code>long</code>.
	 *
	 * @return A long
	 */
	public Long getLong() {
		if (isNumber()) {
			if (isNull()) {
				return (long) 0;
			}
			return ((Number) value).longValue();
		}
		throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
	}

	/**
	 * Set this value to NULL.
	 */
	public void setNull() {
		modified = modified || (!isNull());
		value = null;
		label = null;
	}

	/**
	 * Set the value.
	 *
	 * @param b A BigDecimal
	 */
	public void setBigDecimal(BigDecimal b) {
		if (!isNumber()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
		}
		modified = true;
		if (b == null) {
			setNull();
			return;
		}
		if (isDecimal()) {
			value = b.setScale(decimals, BigDecimal.ROUND_HALF_UP);
			return;
		}
		if (isDouble()) {
			value = b.doubleValue();
			decimals = -1;
			return;
		}
		if (isInteger()) {
			value = b.intValue();
			decimals = 0;
			return;
		}
		if (isLong()) {
			value = b.longValue();
			decimals = 0;
			return;
		}
		throw new UnsupportedOperationException("Not expected exception");
	}

	/**
	 * Set the value.
	 *
	 * @param b A BigDecimal
	 */
	public void setValue(BigDecimal b) {
		setBigDecimal(b);
	}

	/**
	 * Set the value.
	 *
	 * @param b A boolean
	 */
	public void setBoolean(boolean b) {
		if (!isBoolean()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a boolean", value));
		}
		modified = true;
		value = b;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param b A boolean
	 */
	public void setValue(boolean b) {
		setBoolean(b);
	}

	/**
	 * Set the value.
	 *
	 * @param bytes A byte[]
	 */
	public void setByteArray(byte[] bytes) {
		if (!isByteArray()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a byte array", value));
		}
		ByteArray byteArray = new ByteArray(bytes != null ? bytes.length : 0);
		byteArray.addAll(bytes);
		setByteArray(byteArray);
	}

	/**
	 * Set the value.
	 *
	 * @param bytes A byte[]
	 */
	public void setValue(byte[] bytes) {
		setByteArray(bytes);
	}

	/**
	 * Set the value.
	 *
	 * @param byteArray A ByteArray
	 */
	public void setByteArray(ByteArray byteArray) {
		if (!isByteArray()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a byte array", value));
		}
		if (byteArray == null) {
			setNull();
			return;
		}
		modified = true;
		value = byteArray;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param byteArray A ByteArray
	 */
	public void setValue(ByteArray byteArray) {
		setByteArray(byteArray);
	}

	/**
	 * Set the value.
	 *
	 * @param d A Date
	 */
	public void setDate(Date d) {
		if (!isDate()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a date", value));
		}
		modified = true;
		if (d == null) {
			setNull();
			return;
		}
		value = d;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param d A Date
	 */
	public void setValue(Date d) {
		setDate(d);
	}

	/**
	 * Set the value.
	 *
	 * @param s A String.
	 */
	public void setString(String s) {
		if (!isString()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a string", value));
		}
		modified = true;
		if (s == null) {
			setNull();
			return;
		}
		value = s;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param s A String.
	 */
	public void setValue(String s) {
		setString(s);
	}

	/**
	 * Set the value.
	 *
	 * @param t A Time
	 */
	public void setTime(Time t) {
		if (!isTime()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a time", value));
		}
		modified = true;
		if (t == null) {
			setNull();
			return;
		}
		value = t;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param t A Time
	 */
	public void setValue(Time t) {
		setTime(t);
	}

	/**
	 * Set the value.
	 *
	 * @param t A Timestamp
	 */
	public void setTimestamp(Timestamp t) {
		if (!isTimestamp()) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Value {0} is not a date, time or timestamp", value));
		}
		modified = true;
		if (t == null) {
			setNull();
			return;
		}
		value = t;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param t A Timestamp
	 */
	public void setValue(Timestamp t) {
		setTimestamp(t);
	}

	/**
	 * Set the value.
	 *
	 * @param valueArray A ValueArray
	 */
	public void setValueArray(ValueArray valueArray) {
		if (!isValueArray()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a value array", value));
		}
		modified = true;
		if (valueArray == null) {
			setNull();
			return;
		}
		value = valueArray;
		return;
	}

	/**
	 * Set the value.
	 *
	 * @param valueArray A ValueArray
	 */
	public void setValue(ValueArray valueArray) {
		setValueArray(valueArray);
	}

	/**
	 * Set the value.
	 *
	 * @param d The double value
	 */
	public void setDouble(double d) {
		if (!isNumber()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
		}
		modified = true;
		if (isDouble()) {
			value = d;
			return;
		}
		if (isDecimal()) {
			BigDecimal b = new BigDecimal(d).setScale(decimals, BigDecimal.ROUND_HALF_UP);
			value = b;
			return;
		}
		if (isInteger()) {
			value = (int) d;
			return;
		}
		if (isLong()) {
			value = (long) d;
			return;
		}
		throw new UnsupportedOperationException("Not expected exception");
	}

	/**
	 * Set the value.
	 *
	 * @param d The double value
	 */
	public void setValue(double d) {
		setDouble(d);
	}

	/**
	 * Set the value.
	 *
	 * @param i An int
	 */
	public void setInteger(int i) {
		if (!isNumber()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
		}
		modified = true;
		if (isInteger()) {
			value = i;
			return;
		}
		if (isLong()) {
			value = (long) i;
			return;
		}
		if (isDouble()) {
			value = (double) i;
			return;
		}
		if (isDecimal()) {
			BigDecimal b = new BigDecimal(i).setScale(decimals, BigDecimal.ROUND_HALF_UP);
			value = b;
			return;
		}
		throw new UnsupportedOperationException("Not expected exception");
	}

	/**
	 * Set the value.
	 *
	 * @param i An int
	 */
	public void setValue(int i) {
		setInteger(i);
	}

	/**
	 * Set the optional label.
	 *
	 * @param label The label.
	 * @return This value, useful to create the value and set the label.
	 */
	public Value setLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * Set the value.
	 *
	 * @param l A long
	 */
	public void setLong(long l) {
		if (!isNumber()) {
			throw new UnsupportedOperationException(MessageFormat.format("Value {0} is not a number", value));
		}
		modified = true;
		if (isInteger()) {
			value = (int) l;
			return;
		}
		if (isLong()) {
			value = l;
			return;
		}
		if (isDouble()) {
			value = (double) l;
			return;
		}
		if (isDecimal()) {
			BigDecimal b = new BigDecimal(l).setScale(decimals, BigDecimal.ROUND_HALF_UP);
			value = b;
			return;
		}
		throw new UnsupportedOperationException("Not expected exception");
	}

	/**
	 * Check if this value has been modified calling a setter method.
	 *
	 * @return the modified flag
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Privately access the value
	 *
	 * @return The value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns a string representation of this value.
	 *
	 * @return A string
	 */
	@Override
	public String toString() {
		if (isNull()) {
			return "null";
		}
		if (isByteArray()) {
			return new String((byte[]) value);
		}
		return value.toString();
	}

	/**
	 * Set the modified flag.
	 *
	 * @param modified The flag that indicates that the values shoud be considered modified.
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * Returns the foreground color if any.
	 * 
	 * @return The foreground color if any.
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Set the foreground color.
	 * 
	 * @param foregroundColor The foreground color.
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Returns the background color if any.
	 * 
	 * @return The background color.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Set the background color.
	 * 
	 * @param backgroundColor The background color.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Returns true if this value is in the list, false otherwise.
	 *
	 * @param values The list of values to check.
	 * @return True if this value is in the list.
	 */
	public boolean in(Value... values) {
		return ValueUtils.in(this, values);
	}

	/**
	 * Returns true if this value is in the list, false otherwise.
	 *
	 * @param values The list of values to check.
	 * @return True if this value is in the list.
	 */
	public boolean in(Collection<Value> values) {
		return ValueUtils.in(this, values);
	}

	/**
	 * Returns true if this value is not in the list, false otherwise.
	 *
	 * @param values The list of values to check.
	 * @return True if this value is not in the list.
	 */
	public boolean notIn(Value... values) {
		return !ValueUtils.in(this, values);
	}

	/**
	 * Returns true if this value is not in the list, false otherwise.
	 *
	 * @param values The list of values to check.
	 * @return True if this value is not in the list.
	 */
	public boolean notIn(List<Value> values) {
		return !ValueUtils.in(this, values);
	}

	/**
	 * Set the value from a formatted (localized) string.
	 * 
	 * @param str The argument string.
	 * @param locale The locale.
	 * @throws ParseException If a parse error occurs.
	 */
	public void fromStringFormatted(String str, Locale locale) throws ParseException {
		if (str == null) {
			return;
		}
		switch (getType()) {
		case BOOLEAN:
			setBoolean(FormatUtils.formattedToBoolean(str, locale));
			break;
		case BYTEARRAY:
			getByteArray().clear();
			getByteArray().addAll(str.getBytes());
			break;
		case DATE:
			if (str.trim().length() == 0) {
				setDate(null);
				break;
			}
			setDate(FormatUtils.formattedToDate(str, locale));
			break;
		case DECIMAL:
			if (str.trim().length() == 0) {
				str = "0";
			}
			BigDecimal bd = FormatUtils.formattedToBigDecimal(str, locale);
			bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_UP);
			setBigDecimal(bd);
			break;
		case DOUBLE:
			setDouble(FormatUtils.formattedToDouble(str, locale));
			break;
		case INTEGER:
			setInteger(FormatUtils.formattedToInteger(str, locale));
			break;
		case LONG:
			setLong(FormatUtils.formattedToLong(str, locale));
			break;
		case OBJECT:
			break;
		case STRING:
			setString(str);
			break;
		case TIME:
			if (str.trim().length() == 0) {
				setTime(null);
				break;
			}
			setTime(FormatUtils.formattedToTime(str, locale));
			break;
		case TIMESTAMP:
			if (str.trim().length() == 0) {
				setTimestamp(null);
				break;
			}
			setTimestamp(FormatUtils.formattedToTimestamp(str, locale));
			break;
		case VALUE:
			break;
		case VALUEARRAY:
			break;
		default:
			break;
		}
	}

	/**
	 * Set the value from an unformatted string.
	 * 
	 * @param str The argument string.
	 * @throws ParseException If a parse error occurs.
	 */
	public void fromStringUnformatted(String str) throws ParseException {
		if (str == null) {
			return;
		}
		switch (getType()) {
		case BOOLEAN:
			setBoolean(FormatUtils.unformattedToBoolean(str));
			break;
		case BYTEARRAY:
			getByteArray().clear();
			getByteArray().addAll(str.getBytes());
			break;
		case DATE:
			if (str.trim().length() == 0) {
				setDate(null);
				break;
			}
			setDate(FormatUtils.unformattedToDate(str));
			break;
		case DECIMAL:
			if (str.trim().length() == 0) {
				str = "0";
			}
			BigDecimal bd = FormatUtils.unformattedToBigDecimal(str);
			bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_UP);
			setBigDecimal(bd);
			break;
		case DOUBLE:
			setDouble(FormatUtils.unformattedToDouble(str));
			break;
		case INTEGER:
			setInteger(FormatUtils.unformattedToInteger(str));
			break;
		case LONG:
			setLong(FormatUtils.unformattedToLong(str));
			break;
		case OBJECT:
			break;
		case STRING:
			setString(str);
			break;
		case TIME:
			if (str.trim().length() == 0) {
				setTime(null);
				break;
			}
			setTime(FormatUtils.unformattedToTime(str));
			break;
		case TIMESTAMP:
			if (str.trim().length() == 0) {
				setTimestamp(null);
				break;
			}
			setTimestamp(FormatUtils.unformattedToTimestamp(str));
			break;
		case VALUE:
			break;
		case VALUEARRAY:
			break;
		default:
			break;
		}
	}

	/**
	 * Returns this value as a formatted string given the locale.
	 * 
	 * @param locale The locale.
	 * @return The formatted string.
	 */
	public String toStringFormatted(Locale locale) {

		// A null value returns an empty string.
		if (isNull()) {
			return "";
		}

		// Value array, build a comma separated string with each value.
		if (isValueArray()) {
			ValueArray valueArray = getValueArray();
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < valueArray.size(); i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append(valueArray.get(i).toStringFormatted(locale));
			}
			return b.toString();
		}

		// Single value.
		return FormatUtils.formattedFromValue(this, locale);
	}

	/**
	 * Set the decimals, only supported for type decimal.
	 * 
	 * @param decimals The number of decimal places.
	 */
	public void setDecimals(int decimals) {
		if (getType() != Types.DECIMAL) {
			throw new UnsupportedOperationException("Not supported for types different than decimal.");
		}
		this.decimals = decimals;
	}
}
