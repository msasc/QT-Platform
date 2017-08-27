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

/**
 * An index definition, normally the index of a table in an SQL database.
 *
 * @author Miquel Sas
 */
public class Index extends Order {

	/**
	 * The name.
	 */
	private String name = null;
	/**
	 * A long description.
	 */
	private String description = null;
	/**
	 * An optional database schema.
	 */
	private String schema = null;
	/**
	 * The unique control flag.
	 */
	private boolean unique = false;
	/**
	 * The parent table.
	 */
	private Table table = null;

	/**
	 * Default constructor.
	 */
	public Index() {
		super();
	}

	/**
	 * Constructor assigning the initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 */
	public Index(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Returns the name of the index.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the index.
	 *
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns an optional description of the index.
	 *
	 * @return The optional description of the index.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets an optional description of the index.
	 *
	 * @param description The optional description of the index.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the optional database schema where this index will be located.
	 *
	 * @return The database schema.
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Gets the name qualified with the schema.
	 *
	 * @return The name qualified with the schema.
	 */
	public String getNameSchema() {
		if (schema != null && name != null) {
			return schema + "." + name;
		}
		return name;
	}

	/**
	 * Sets the database schema.
	 *
	 * @param schema The database schema.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Check if this index is unique.
	 *
	 * @return A boolean
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Set if this index is unique.
	 *
	 * @param unique A boolean
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * Returns the parent table.
	 *
	 * @return The parent table..
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Sets the parent table.
	 *
	 * @param table The table
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the hash code value for this index.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		if (getNameSchema() != null) {
			hash ^= getNameSchema().hashCode();
		}
		hash ^= super.hashCode();
		return hash;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @return A boolean.
	 * @param o The object to compare with.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		Index index = (Index) o;
		if (!getNameSchema().equals(index.getNameSchema())) {
			return false;
		}
		return super.equals(o);
	}

	/**
	 * Returns the field at the given index.
	 * 
	 * @param index The index of the field.
	 * @return The field.
	 */
	public Field getField(int index) {
		return get(index).getField();
	}
}
