/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.sql;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.util.StringUtils;

/**
 * An ALTER TABLE ADD CONSTRAINT name CHECK (condition) builder to apply the constraints related to the field.
 *
 * @author Miquel Sas
 */
public class AddCheck extends Statement {

	/**
	 * Returns a suitable constraint name of a check constraint.
	 *
	 * @param table The table.
	 * @param fieldIndex The field index.
	 * @return The constraint name.
	 */
	public static String getConstraintName(Table table, int fieldIndex) {
		StringBuilder b = new StringBuilder();
		b.append(table.getName());
		b.append("_CHK");
		b.append(StringUtils.leftPad(Integer.toString(fieldIndex), 2, '0'));
		return b.toString();
	}

	/**
	 * The table to alter.
	 */
	private Table table;
	/**
	 * The field to check.
	 */
	private Field field;

	/**
	 * Default constructor.
	 */
	public AddCheck() {
		super();
	}

	/**
	 * Returns the table.
	 *
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Set the table.
	 *
	 * @param table The table to set the check constraint.
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the field.
	 *
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Set the field for the check constraint.
	 *
	 * @param field The field that sets the constraint.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Returns this <code>ALTER TABLE ADD FOREIGN KEY</code> query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {

		if (getTable() == null) {
			throw new IllegalStateException("Malformed ADD CHECK query: table is null");
		}
		if (getField() == null) {
			throw new IllegalStateException("Malformed ADD CHECK query: field is null");
		}
		if (getField().getMaximumValue() == null
				&& getField().getMinimumValue() == null
				&& getField().getPossibleValues().isEmpty()) {
			return new String();
		}

		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(getTable().getNameSchema());
		b.append(" ADD CONSTRAINT ");
		b.append(getConstraintName(getTable(),
				getTable().getFieldIndex(getField())));
		b.append(" CHECK (");
		if (getField().getPossibleValues().isEmpty()) {
			boolean and = false;
			if (getField().getMinimumValue() != null) {
				b.append(getField().getNameCreate());
				b.append(" >= ?");
				and = true;
			}
			if (getField().getMaximumValue() != null) {
				if (and) {
					b.append(" AND ");
				}
				b.append(getField().getNameCreate());
				b.append(" <= ?");
			}
		} else {
			b.append(getField().getNameCreate());
			b.append(" IN (");
			List<Value> values = getField().getPossibleValues();
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append("?");
			}
			b.append(")");
		}
		b.append(")");

		return b.toString();
	}

	/**
	 * Returns the array list of parameterized values.
	 *
	 * @return The array list of values.
	 */
	@Override
	public List<Value> getValues() {
		List<Value> values = new ArrayList<>();
		List<Value> possibleValues = getField().getPossibleValues();
		if (possibleValues.isEmpty()) {
			if (getField().getMinimumValue() != null) {
				values.add(getField().getMinimumValue());
			}
			if (getField().getMaximumValue() != null) {
				values.add(getField().getMaximumValue());
			}
		} else {
			values.addAll(possibleValues);
		}
		return values;
	}
}
