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
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.Properties;
import com.qtplaf.library.util.Time;
import com.qtplaf.library.util.Timestamp;

/**
 * A record packs a list of values and their corresponding field definitions.
 *
 * @author Miquel Sas
 */
public class Record implements Comparable<Object> {

	/**
	 * Key used to set the total property.
	 */
	public static final String TOTAL = "Total";

	/**
	 * Move values fron a source record to a destination record, by coincident alias and type.
	 * 
	 * @param source The source record.
	 * @param destination The destination record.
	 */
	public static void move(Record source, Record destination) {
		for (int i = 0; i < source.getFieldCount(); i++) {
			String alias = source.getField(i).getAlias();
			Value value = source.getValue(i);
			Types type = source.getField(i).getType();
			int index = destination.getFieldList().getFieldIndex(alias);
			if (index >= 0 && destination.getField(index).getType().equals(type)) {
				destination.setValue(index, value.getCopy());
			}
		}
	}

	/**
	 * Returns a copy of the argument source record.
	 * 
	 * @param source The source record.
	 * @return A copy of the argument source record.
	 */
	public static Record copy(Record source) {
		Record destination = new Record(source.getFieldList());
		copy(source, destination);
		return destination;
	}

	/**
	 * Returns a copy of the argument source record, with a copy of data and fields.
	 * 
	 * @param source The source record.
	 * @return A copy of the argument source record.
	 */
	public static Record copyDataAndFields(Record source) {
		Record destination = new Record(new FieldList(source.fields));
		copy(source, destination);
		return destination;
	}

	/**
	 * Copy date from source to destination.
	 * 
	 * @param source The source record.
	 * @param destination The destination record.
	 */
	public static void copy(Record source, Record destination) {
		destination.persistor = source.persistor;
		destination.properties = source.properties;
		destination.validator = source.validator;
		move(source, destination);
	}

	/**
	 * The list of values.
	 */
	private List<Value> values;
	/**
	 * The list of fields.
	 */
	private FieldList fields;
	/**
	 * An arbitrary map of properties.
	 */
	private Properties properties;
	/**
	 * The persistor.
	 */
	private Persistor persistor;
	/**
	 * The validator.
	 */
	private Validator<Record> validator;

	/**
	 * Default constructor.
	 */
	public Record() {
		super();
	}

	/**
	 * Constructor assigning the list of fields.
	 * 
	 * @param fields The list of fields.
	 */
	public Record(FieldList fields) {
		super();
		setFieldList(fields);
	}

	/**
	 * Sets the field list.
	 *
	 * @param fields The field list.
	 */
	public void setFieldList(FieldList fields) {
		this.fields = fields;
		this.values = fields.getDefaultValues();
	}

	/**
	 * Sets the field list and values.For performance issues this method does not validate that field-value type match.
	 * The method <i>FieldList.validateValues</i> can be used to validate the values if necessary.
	 *
	 * @param fields The field list.
	 * @param values The list of values.
	 */
	public void setFieldListAndValues(FieldList fields, List<Value> values) {
		this.fields = fields;
		this.values = values;
	}

	/**
	 * Returns the number of fields.
	 *
	 * @return The number of fields.
	 */
	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * Returns the field list.
	 *
	 * @return The field list.
	 */
	public FieldList getFieldList() {
		return fields;
	}

	/**
	 * Get the field at the given index.
	 *
	 * @param index The index of the field.
	 * @return The field.
	 */
	public Field getField(int index) {
		return fields.getField(index);
	}

	/**
	 * Get a field by alias.
	 *
	 * @param alias The field alias.
	 * @return The field or null if not found.
	 */
	public Field getField(String alias) {
		return fields.getField(alias);
	}

	/**
	 * Clears this record fields to their default values.
	 */
	public void clear() {
		values.clear();
		for (int i = 0; i < getFieldCount(); i++) {
			values.add(getField(i).getDefaultValue());
		}
	}

	/**
	 * Returns the value at a given index.
	 *
	 * @param index The index
	 * @return The value at the given index.
	 */
	public Value getValue(int index) {
		Calculator calculator = getField(index).getCalculator();
		if (calculator != null) {
			return calculator.getValue(this);
		}
		return values.get(index);
	}

	/**
	 * Get a value by field alias.
	 *
	 * @param alias The field alias
	 * @return The value.
	 */
	public Value getValue(String alias) {
		int index = fields.getFieldIndex(alias);
		return (index == -1 ? null : getValue(index));
	}

	/**
	 * Returns the list of values.
	 *
	 * @return The list of values.
	 */
	public List<Value> getValues() {
		return values;
	}

	/**
	 * Returns the list of persistent values.
	 *
	 * @return The list of persistent values.
	 */
	public List<Value> getPersistentValues() {
		List<Field> persistentFields = getPersistentFields();
		List<Value> persistentValues = new ArrayList<>(persistentFields.size());
		for (Field field : persistentFields) {
			persistentValues.add(getValue(field.getAlias()));
		}
		return persistentValues;
	}

	/**
	 * Returns the order key for the given order. The order must contain fields of the record.
	 * 
	 * @param order The order.
	 * @return The key.
	 */
	public OrderKey getOrderKey(Order order) {
		OrderKey key = new OrderKey();
		for (int i = 0; i < order.size(); i++) {
			Order.Segment segment = order.get(i);
			Field field = segment.getField();
			boolean asc = segment.isAsc();
			Value value = getValue(field.getAlias());
			if (value == null) {
				throw new IllegalArgumentException();
			}
			key.add(value, asc);
		}
		return key;
	}

	/**
	 * Returns the list of persistent fields.
	 *
	 * @return the list of persistent fields.
	 */
	public List<Field> getPersistentFields() {
		return fields.getPersistentFields();
	}

	/**
	 * Set the list of values.
	 *
	 * @param values The list of values.
	 */
	public void setValues(List<Value> values) {
		this.values = values;
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, Value value) {
		values.set(index, value);
		values.get(index).setModified(value.isModified());
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, BigDecimal value) {
		values.get(index).setBigDecimal(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, boolean value) {
		values.get(index).setBoolean(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, byte[] value) {
		values.get(index).setByteArray(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, ByteArray value) {
		values.get(index).setByteArray(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, Date value) {
		values.get(index).setDate(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, Time value) {
		values.get(index).setTime(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, Timestamp value) {
		values.get(index).setTimestamp(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, String value) {
		values.get(index).setString(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, double value) {
		values.get(index).setDouble(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, int value) {
		values.get(index).setInteger(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(int index, long value) {
		values.get(index).setLong(value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, Value value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, BigDecimal value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, boolean value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, byte[] value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, ByteArray value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, Date value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, Time value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, Timestamp value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, String value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, double value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, int value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param alias The index of the value.
	 * @param value The value to set.
	 */
	public void setValue(String alias, long value) {
		int index = fields.getFieldIndex(alias);
		setValue(index, value);
	}

	/**
	 * Get the list of primary key fields.
	 * 
	 * @return The list of primary key fields.
	 */
	public List<Field> getPrimaryKeyFields() {
		return fields.getPrimaryKeyFields();
	}

	/**
	 * Returns the primary order.
	 * 
	 * @return The primary order.
	 */
	public Order getPrimaryOrder() {
		return fields.getPrimaryOrder();
	}

	/**
	 * Get the primary key.
	 *
	 * @return An <code>IndexKey</code>
	 */
	public OrderKey getPrimaryKey() {
		List<Field> primaryKeyFields = getPrimaryKeyFields();
		OrderKey orderKey = new OrderKey(primaryKeyFields.size());
		for (Field field : primaryKeyFields) {
			orderKey.add(getValue(field.getAlias()), true);
		}
		return orderKey;
	}

	/**
	 * Returns the persistor associated with this record if any.
	 * 
	 * @return The persistor.
	 */
	public Persistor getPersistor() {
		return persistor;
	}

	/**
	 * Sets the persistor associated with this record if any.
	 * 
	 * @param persistor The persistor.
	 */
	public void setPersistor(Persistor persistor) {
		this.persistor = persistor;
	}

	/**
	 * Check if the record has been modified.
	 *
	 * @return A boolean.
	 */
	public boolean isModified() {
		for (Value value : values) {
			if (value.isModified()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this record is less than, equal to, or greater than
	 * the specified value.
	 *
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(Object o) {
		Record record = null;
		try {
			record = (Record) o;
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
		}
		// Compare using the primary key pointers.
		RecordComparator comparator = new RecordComparator(getPrimaryOrder());
		return comparator.compare(this, record);
	}

	/**
	 * Compares for equality. A record is considered to be equal if the primary keys are equal not considering the
	 * asc/desc flag.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Record) {
			Record record = (Record) obj;
			return getPrimaryKey().equals(record.getPrimaryKey());
		}
		return super.equals(obj);
	}

	/**
	 * Returns the additional properties.
	 * 
	 * @return The additional properties.
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	/**
	 * Set this background color to all values of the record.
	 * 
	 * @param color The color.
	 */
	public void setBackgroundColor(Color color) {
		for (int i = 0; i < getFieldCount(); i++) {
			getValue(i).setBackgroundColor(color);
		}
	}

	/**
	 * Set this foreground color to all values of the record.
	 * 
	 * @param color The color.
	 */
	public void setForegroundColor(Color color) {
		for (int i = 0; i < getFieldCount(); i++) {
			getValue(i).setForegroundColor(color);
		}
	}

	/**
	 * Returns this record validator.
	 * 
	 * @return The validator.
	 */
	public Validator<Record> getValidator() {
		return validator;
	}

	/**
	 * Sets this record validator.
	 * 
	 * @param validator The validator.
	 */
	public void setValidator(Validator<Record> validator) {
		this.validator = validator;
	}

	/**
	 * Returns a string representation of this record.
	 * 
	 * @return A string representation of this record.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < getFieldCount(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			Value value = getValue(i);
			Types type = getField(i).getType();
			switch (type) {
			case BOOLEAN:
				b.append(value.getBoolean());
				break;
			case BYTEARRAY:
				b.append(value.getByteArray());
				break;
			case DECIMAL:
			case DOUBLE:
			case INTEGER:
			case LONG:
				b.append(value.getNumber());
				break;
			case OBJECT:
				b.append(value.getType());
				break;
			case STRING:
			case DATE:
			case TIME:
			case TIMESTAMP:
				b.append("'" + value.toString() + "'");
				break;
			case VALUE:
				b.append(value.toString());
				break;
			case VALUEARRAY:
				b.append(value.toString());
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
		return b.toString();
	}

}
