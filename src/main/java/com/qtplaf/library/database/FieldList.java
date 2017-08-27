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
import java.util.ArrayList;
import java.util.List;

/**
 * A field list packs a list of field, its field map, the primary key pointers and the order key pointers if applicable.
 *
 * @author Miquel Sas
 */
public class FieldList {

	/**
	 * The field map to access fields by alias.
	 */
	private transient FieldMap fieldMap = null;
	/**
	 * The list of fields.
	 */
	private final List<Field> fields = new ArrayList<>();
	/**
	 * The list of persistent fields.
	 */
	private List<Field> persistentFields;
	/**
	 * The list of primary key fields.
	 */
	private List<Field> primaryKeyFields;
	/**
	 * Primary order.
	 */
	private Order primaryOrder;

	/**
	 * Default constructor.
	 */
	public FieldList() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param fieldList The source field list.
	 */
	public FieldList(FieldList fieldList) {
		super();
		for (int i = 0; i < fieldList.getFieldCount(); i++) {
			addField(new Field(fieldList.getField(i)));
		}
	}

	/**
	 * Returns a record with default values from this field list.
	 * 
	 * @return A default record.
	 */
	public Record getDefaultRecord() {
		return getRecord(getDefaultValues());
	}

	/**
	 * Returns a record created with this field list and the list of values. For performance issues this method does not
	 * validate that field-value type match. The method <i>validateValues</i> can be used to validate the values if
	 * necessary.
	 * 
	 * @param values The list of values.
	 * @return The record.
	 */
	public Record getRecord(List<Value> values) {
		Record record = new Record();
		record.setFieldListAndValues(this, values);
		return record;
	}

	/**
	 * Validates the values against this field list checking that all field-value types are equal.
	 * 
	 * @param values The list of values to validate.
	 */
	public void validateValues(List<Value> values) {
		if (fields.size() != values.size()) {
			throw new IllegalArgumentException("Invalid number of values");
		}
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			Value value = values.get(i);
			if (!field.getType().equals(value.getType())) {
				throw new IllegalArgumentException(
					MessageFormat.format(
						"Field {0} type {1} does not match the corresponding value type {2}",
						field.getName(),
						field.getType(),
						value.getType()));
			}
		}
	}

	/**
	 * Check if the argument object is equal to this field list.
	 * 
	 * @param obj The object to check.
	 * @return A boolean indicating if the argument object is equal to this field list.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldList) {
			FieldList fieldList = (FieldList) obj;
			if (fields.equals(fieldList.fields)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clear this field list.
	 */
	public void clear() {
		fields.clear();
		clearFieldMap();
	}

	/**
	 * Returns this field list size.
	 *
	 * @return The number of fields.
	 */
	public int size() {
		return fields.size();
	}

	/**
	 * Clear the field map, necessary when changing fields.
	 */
	public void clearFieldMap() {
		FieldMap.removeMap(fieldMap);
		fieldMap = null;
		if (primaryKeyFields != null) {
			primaryKeyFields.clear();
		}
		primaryKeyFields = null;
		if (persistentFields != null) {
			persistentFields.clear();
		}
		persistentFields = null;
		primaryOrder = null;
	}

	/**
	 * Returns this field map.
	 *
	 * @return The field map.
	 */
	public FieldMap getFieldMap() {
		if (fieldMap == null) {
			fieldMap = new FieldMap(fields);
		}
		return fieldMap;
	}

	/**
	 * Returns the list of default values.
	 *
	 * @return The list of values.
	 */
	public List<Value> getDefaultValues() {
		List<Value> values = new ArrayList<>();
		for (Field field : fields) {
			values.add(field.getDefaultValue());
		}
		return values;
	}

	/**
	 * Add a field to the field list.
	 *
	 * @param field The field to add.
	 */
	public void addField(Field field) {
		fields.add(field);
		clearFieldMap();
	}

	/**
	 * Check if the field list contains the field with the argument alias.
	 * 
	 * @param alias The field alias.
	 * @return A bolean.
	 */
	public boolean containsField(String alias) {
		return (getFieldIndex(alias) >= 0);
	}

	/**
	 * Returns the number of fields in this table.
	 *
	 * @return The number of fields.
	 */
	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * Returns the field in the given index.
	 *
	 * @param index The index of the field.
	 * @return The field
	 */
	public Field getField(int index) {
		return fields.get(index);
	}

	/**
	 * Get a field by alias.
	 *
	 * @return The field or null if not found.
	 * @param alias The field alias.
	 */
	public Field getField(String alias) {
		int index = getFieldIndex(alias);
		return (index == -1 ? null : FieldList.this.getField(index));
	}

	/**
	 * Get a field index by alias.
	 *
	 * @return The field index or -1 if not found.
	 * @param alias The field alias.
	 */
	public int getFieldIndex(String alias) {
		return getFieldMap().getFieldIndex(alias);
	}

	/**
	 * Get a field index.
	 *
	 * @return The field index or -1 if not found.
	 * @param field The field.
	 */
	public int getFieldIndex(Field field) {
		return getFieldIndex(field.getAlias());
	}

	/**
	 * Returns the internal list of fields.
	 *
	 * @return The internal list of fields.
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Returns the list of persistent fields.
	 *
	 * @return The list of persistent fields.
	 */
	public List<Field> getPersistentFields() {
		if (persistentFields == null) {
			persistentFields = new ArrayList<>();
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				if (field.isLocal() && field.isPersistent()) {
					persistentFields.add(field);
				}
			}
		}
		return persistentFields;
	}

	/**
	 * Returns the primary order.
	 * 
	 * @return The primary order.
	 */
	public Order getPrimaryOrder() {
		if (primaryOrder == null) {
			primaryOrder = new Order();
			List<Field> pkFields = getPrimaryKeyFields();
			for (Field field : pkFields) {
				primaryOrder.add(field, true);
			}
		}
		return primaryOrder;
	}

	/**
	 * Removes all the fields in this table.
	 *
	 * @return The removed fields
	 */
	public List<Field> removeAllFields() {
		clearFieldMap();
		List<Field> removedFields = new ArrayList<>();
		removedFields.addAll(fields);
		fields.clear();
		return removedFields;
	}

	/**
	 * Remove the field at the given index.
	 *
	 * @param index The index of the field
	 * @return The removed field or null.
	 */
	public Field removeField(int index) {
		clearFieldMap();
		return fields.remove(index);
	}

	/**
	 * Remove the field with the given alias.
	 *
	 * @param alias The alias of the field
	 * @return The removed field or null.
	 */
	public Field removeField(String alias) {
		int index = getFieldIndex(alias);
		if (index < 0) {
			return null;
		}
		return FieldList.this.removeField(index);
	}

	/**
	 * Returns the list of primary key fields.
	 *
	 * @return The list of primary key fields.
	 */
	public List<Field> getPrimaryKeyFields() {
		if (primaryKeyFields == null) {
			primaryKeyFields = new ArrayList<>();
			for (Field field : fields) {
				if (field.isLocal() && field.isPrimaryKey()) {
					primaryKeyFields.add(field);
				}
			}
		}
		return primaryKeyFields;
	}
}
