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

import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;

/**
 * A builder of INSERT statements. Insert can be of the following forms:
 * <ol>
 * <li>INSERT INTO table SELECT view</li>
 * <li>INSERT INTO table (fields...) VALUES (values...)</li>
 * <li>INSERT INTO table a record.</li>
 * </ol>
 *
 * @author Miquel Sas
 */
public class Insert extends Statement {

	/**
	 * The table to insert the record or the view into.
	 */
	private Table table;
	/**
	 * The fields to insert.
	 */
	private List<Field> fields;
	/**
	 * The list of values to insert.
	 */
	private List<Value> values;
	/**
	 * The select to insert.
	 */
	private Select select;
	/**
	 * A reference to the record when inserting one.
	 */
	private Record record;

	/**
	 * Default constructor.
	 */
	public Insert() {
		super();
	}

	/**
	 * Returns the table to insert into.
	 *
	 * @return The table.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Sets the table to insert into.
	 *
	 * @param table The table to insert into.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the list of fields to inserted.
	 *
	 * @return The list of fields.
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Sets the list of fields to be updated. If not set, the fields of the table are used instead.
	 *
	 * @param fields The list of fields to be updated.
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	/**
	 * Returns the list of values to insert.
	 *
	 * @return The list of values.
	 */
	@Override
	public List<Value> getValues() {
		return values;
	}

	/**
	 * Adds a list of values to build an insert statement of the form INSERT INTO table ( fields ) VALUES ( values ).
	 * This method can be called several times to create several insert operations. Obviously the values must correspond
	 * to the fields.
	 *
	 * @param values The list of values.
	 */
	public void setValues(List<Value> values) {
		this.values = values;
	}

	/**
	 * Returns the record to insert.
	 *
	 * @return The reocrd to insert.
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * Adds the list of values to build an insert statement of the form INSERT INTO table ( fields ) VALUES ( values )
	 * using the values given in the argument record. If the fields are not set and record has fields, the fields are
	 * taken from the record. Obviously the values in the record must correspond to the fields if already set.
	 *
	 * @param record The record to use to get the values.
	 */
	public void setRecord(Record record) {
		this.record = record;
	}

	/**
	 * Sets the select query to build an insert statement of the form INSERT INTO table ( fields ) SELECT ( query ).
	 *
	 * @param select The select builder.
	 */
	public void setSelect(Select select) {
		if (!getDBEngineAdapter().equals(select.getDBEngineAdapter())) {
			throw new IllegalStateException(
				"Malformed INSERT query: the select and the insert database adapters must be the same");
		}
		this.select = select;
	}

	/**
	 * Sets the select query to build an insert statement of the form INSERT INTO table ( fields ) SELECT ( query ).
	 *
	 * @param view The select view.
	 * @param filter The view filter.
	 */
	public void setSelect(View view, Filter filter) {
		Select select = new Select();
		select.setDBEngineAdapter(getDBEngineAdapter());
		select.setView(view);
		select.setWhere(filter);
		this.select = select;
	}

	/**
	 * Returns this <code>INSERT</code> query as a string, eventually with parameters.
	 *
	 * @return The statement.
	 */
	@Override
	public String toSQL() {

		if (table == null) {
			throw new IllegalStateException("Malformed INSERT query: table is null");
		}
		if (record == null && values == null && select == null) {
			throw new IllegalStateException("Malformed INSERT query: need record, values or select");
		}
		if (values != null && select != null) {
			throw new IllegalStateException("Malformed INSERT query: set only values or select");
		}
		if (record != null && select != null) {
			throw new IllegalStateException("Malformed INSERT query: set only record or select");
		}
		if (values != null && record != null) {
			throw new IllegalStateException("Malformed INSERT query: set only values or record");
		}
		if (select != null && fields == null) {
			throw new IllegalStateException("Malformed INSERT query: fields are needed for a select");
		}
		if (select != null && select.getView().getFieldCount() != getFields().size()) {
			throw new IllegalStateException("Malformed INSERT query: invalid number of fields");
		}
		if (getDBEngineAdapter() == null) {
			throw new IllegalStateException("The database adapter must be set");
		}

		if (record != null) {
			values = record.getPersistentValues();
		}

		StringBuilder b = new StringBuilder(256);
		b.append("INSERT INTO ");
		b.append(table.getNameSchema());

		b.append(" (");
		if (fields != null) {
			boolean comma = false;
			List<Field> fieldsInsert = getFields();
			for (Field field : fieldsInsert) {
				if (comma) {
					b.append(", ");
				}
				b.append(field.getNameCreate());
				comma = true;
			}
		}
		if (record != null) {
			boolean comma = false;
			List<Field> persistentFields = record.getPersistentFields();
			for (Field field : persistentFields) {
				if (comma) {
					b.append(", ");
				}
				b.append(field.getNameCreate());
				comma = true;
			}
		}
		b.append(") ");

		if (select != null) {
			b.append(select.toSQL());
			return b.toString();
		}

		if (values != null) {
			b.append("VALUES (");
			if (fields != null) {
				boolean comma = false;
				List<Field> fieldsInsert = getFields();
				for (Field field : fieldsInsert) {
					if (comma) {
						b.append(", ");
					}
					if (field.isCurrentDate()) {
						b.append(getDBEngineAdapter().getCurrentDate());
					} else if (field.isCurrentTime()) {
						b.append(getDBEngineAdapter().getCurrentTime());
					} else if (field.isCurrentTimestamp()) {
						b.append(getDBEngineAdapter().getCurrentTimestamp());
					} else {
						b.append("?");
					}
					comma = true;
				}
			}
			if (record != null) {
				boolean comma = false;
				List<Field> persistentFields = record.getPersistentFields();
				for (Field field : persistentFields) {
					if (comma) {
						b.append(", ");
					}
					if (field.isCurrentDate()) {
						b.append(getDBEngineAdapter().getCurrentDate());
					} else if (field.isCurrentTime()) {
						b.append(getDBEngineAdapter().getCurrentTime());
					} else if (field.isCurrentTimestamp()) {
						b.append(getDBEngineAdapter().getCurrentTimestamp());
					} else {
						b.append("?");
					}
					comma = true;
				}
			}
			b.append(")");
			return b.toString();
		}

		return b.toString();
	}
}
