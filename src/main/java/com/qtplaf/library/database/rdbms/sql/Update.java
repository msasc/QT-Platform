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
package com.qtplaf.library.database.rdbms.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;

/**
 * An UPDATE statement builder.
 *
 * @author Miquel Sas
 */
public class Update extends Statement {

	/**
	 * Inner class to handle assignments.
	 */
	class Assignment {

		Value value;
		String string;

		Assignment(Value value) {
			this.value = value;
		}

		Assignment(String string) {
			this.string = string;
		}

		Value[] getValues() {
			if (value != null) {
				return new Value[] { value };
			}
			return new Value[0];
		}

		String toSQL() {
			StringBuilder b = new StringBuilder();
			if (value != null) {
				b.append("?");
			}
			if (string != null) {
				b.append(string);
			}
			return b.toString();
		}
	}

	/**
	 * A map to handle field assignments.
	 */
	private HashMap<Field, Assignment> assignmentMap = new HashMap<>();
	/**
	 * The table to update.
	 */
	private Table table;
	/**
	 * The record to update when applicable.
	 */
	private Record record;
	/**
	 * The filter to apply.
	 */
	private Filter filter;

	/**
	 * Default constructor.
	 */
	public Update() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return The table.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Sets the table to update.
	 *
	 * @param table The table.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the filter.
	 *
	 * @return The filter.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Set the filter to use in the update statement.
	 *
	 * @param filter The filter.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
		this.filter.setUsage(Filter.Usage.UPDATE);
	}

	/**
	 * Set the filter to use in the update statement.
	 *
	 * @param filter The filter.
	 */
	public void setWhere(Filter filter) {
		setFilter(filter);
	}

	/**
	 * Returns the record to update if applicable.
	 *
	 * @return The record to update.
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * Set the record to delete, that must belong to the table.
	 *
	 * @param record The record to delete.
	 */
	public void setRecord(Record record) {
		if (getTable() == null) {
			throw new IllegalStateException("Malformed UPDATE query: the record must be set after the table");
		}
		this.record = record;
		for (int i = 0; i < record.getFieldCount(); i++) {
			Field field = record.getField(i);
			Value value = record.getValue(i);
			if (value.isModified() && field.isPersistent()) {
				set(field, value);
			}
		}
		setFilter(getTable().getPrimaryKeyFilter(record));
	}

	/**
	 * Set a field value.
	 *
	 * @param field The field to update.
	 * @param string The literal string to set.
	 */
	public void set(Field field, String string) {
		assignmentMap.put(field, new Assignment(string));
	}

	/**
	 * Set a field value.
	 *
	 * @param field The field to update.
	 * @param value The value to set.
	 */
	public void set(Field field, Value value) {
		assignmentMap.put(field, new Assignment(value));
	}

	/**
	 * Returns the array of values to assign to parameters.
	 *
	 * @return The array of values to assign to parameters.
	 */
	@Override
	public ArrayList<Value> getValues() {

		ArrayList<Value> values = new ArrayList<>();

		Iterator<Field> iter = assignmentMap.keySet().iterator();
		while (iter.hasNext()) {
			Field field = iter.next();
			Assignment assignment = assignmentMap.get(field);
			if (assignment.value != null) {
				values.add(assignment.value);
			}
		}

		if (filter != null && !filter.isEmpty()) {
			values.addAll(filter.getValues());
		}
		return values;
	}

	/**
	 * Returns this <code>UPDATE</code> query as a string, eventually with parameters.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (table == null) {
			throw new IllegalStateException("Malformed UPDATE query: table is null");
		}

		StringBuilder b = new StringBuilder(256);
		b.append("UPDATE ");
		b.append(table.getNameSchema());
		b.append(" SET ");

		Object[] objects = assignmentMap.keySet().toArray();
		for (int i = 0; i < objects.length; i++) {
			if (i > 0) {
				b.append(", ");
			}
			Field field = (Field) objects[i];
			Assignment assignment = assignmentMap.get(field);
			if (assignment.value != null) {
				b.append(field.getNameCreate());
				b.append(" = ");
				b.append("?");
				continue;
			}
			if (assignment.string != null) {
				b.append(field.getNameCreate());
				b.append(" = (");
				b.append(assignment.string);
				b.append(")");
			}
		}

		if (filter != null && !filter.isEmpty()) {
			b.append(" WHERE ");
			b.append(filter.toSQL());
		}

		return b.toString();
	}
}
