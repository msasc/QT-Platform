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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.ForeignKey.Segment;
import com.qtplaf.library.util.Properties;

/**
 * A database table definition.
 *
 * @author Miquel Sas
 */
public class Table implements Comparable<Object> {

	/**
	 * Returns a copy of the table suitable to be used in a relation as a related table. Related tables may have
	 * different aliases as well as their field may also have different aliases.
	 * 
	 * @param table The table to copy.
	 * @return The copy of the table to be used in a relation.
	 */
	public static Table copyForRelation(Table table) {
		Table copy = new Table();
		copy.setName(table.getName());
		copy.setAlias(table.getAlias());
		copy.setDescription(table.getDescription());
		for (int i = 0; i < table.getFieldCount(); i++) {
			copy.addField(new Field(table.getField(i)));
		}
		return copy;
	}

	/**
	 * The name of the view.
	 */
	private String name;
	/**
	 * A title or short description.
	 */
	private String title;
	/**
	 * An optional long description.
	 */
	private String description;
	/**
	 * The alias.
	 */
	private String alias;
	/**
	 * The database schema.
	 */
	private String schema;
	/**
	 * The list of fields.
	 */
	private final FieldList fields = new FieldList();
	/**
	 * A flag that indicates if this table is persistent.
	 */
	private boolean persistent = true;
	/**
	 * A flag that indicates if this table has persistent constraints.
	 */
	private boolean persistentConstraints = true;
	/**
	 * The primary key.
	 */
	private Index primaryKey;
	/**
	 * The list of secondary indexes.
	 */
	private final List<Index> indexes = new ArrayList<>();
	/**
	 * The list of foreign keys.
	 */
	private final List<ForeignKey> foreignKeys = new ArrayList<>();
	/**
	 * The persistor that provides persistence to this table.
	 */
	private Persistor persistor;
	/**
	 * Optional working session, not strictly necessary.
	 */
	private Session session;
	/**
	 * Optional and additional properties.
	 */
	private Properties properties;

	/**
	 * Default constructor.
	 */
	public Table() {
		super();
	}

	/**
	 * Constructor assigning the session.
	 * 
	 * @param session The working session.
	 */
	public Table(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the working session.
	 * 
	 * @param session The working session.
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Get the name.
	 *
	 * @return The table name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the name to use in <code>FROM</code> clause.
	 *
	 * @return The appropriate name for a <code>FROM</code> clause.
	 */
	public String getNameFrom() {
		if (alias != null && name != null) {
			return getNameSchema() + " " + alias;
		}
		return getNameSchema();
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
	 * Set the name.
	 *
	 * @param name The table name
	 */
	public void setName(String name) {
		this.name = null;
		if (name != null && name.trim().length() > 0) {
			this.name = name.trim();
		}
	}

	/**
	 * Returns the title or short description.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title or short description.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the optional description.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 *
	 * @param description The optional description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the alias.
	 *
	 * @return The alias.
	 */
	public String getAlias() {
		if (alias == null) {
			return name;
		}
		return alias;
	}

	/**
	 * Sets the alias.
	 *
	 * @param alias The table alias.
	 */
	public void setAlias(String alias) {
		this.alias = null;
		if (alias != null && alias.trim().length() > 0) {
			this.alias = alias;
		}
	}

	/**
	 * Get the schema.
	 *
	 * @return The table schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Set the schema.
	 *
	 * @param schema The schema name.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Returns the persistor used to retrieve records mainly from a database.
	 * 
	 * @return The persistor used to retrieve records mainly from a database.
	 */
	public Persistor getPersistor() {
		return persistor;
	}

	/**
	 * Sets the persistor used to retrieve records mainly from a database.
	 * 
	 * @param persistor The persistor used to retrieve records mainly from a database.
	 */
	public void setPersistor(Persistor persistor) {
		this.persistor = persistor;
	}

	/**
	 * Clear the field map, necessary when changing fields.
	 */
	public void clearFieldMap() {
		fields.clearFieldMap();
		if (primaryKey != null) {
			primaryKey.clear();
		}
		primaryKey = null;
	}

	/**
	 * Compares this table with the argument table. Returns 0 if they are equal, -1 if this value is less than the
	 * argument, and 1 if it is greater. A table is considered to be equal to another table in the context of a
	 * <code>SELECT</code> statement.
	 *
	 * @return An integer
	 * @param o The object to compare with.
	 */
	@Override
	public int compareTo(Object o) {
		Table table = null;
		try {
			table = (Table) o;
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
		}
		return getNameFrom().compareTo(table.getNameFrom());
	}

	/**
	 * Indicates whether an object is "equal to" this table. A table is considered to be equal to another table in the
	 * context of a SELECT statement, that is, with name from.
	 *
	 * @return A boolean
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
		final Table table = (Table) o;
		return getNameFrom().equals(table.getNameFrom());
	}

	/**
	 * Returns the hash code for this value. A table is considered to have the same hash code than another table in the
	 * context of a SELECT statement.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		return getNameFrom().hashCode();
	}

	/**
	 * Add a field to the field list.
	 *
	 * @param field The field to add.
	 */
	public void addField(Field field) {
		field.setParentTable(this);
		fields.addField(field);
		clearFieldMap();
	}

	/**
	 * Add a list of fields.
	 * 
	 * @param fields The list of fields.
	 */
	public void addFields(List<Field> fields) {
		for (Field field : fields) {
			addField(field);
		}
	}

	/**
	 * Add a list of fields.
	 * 
	 * @param fields The list of fields.
	 */
	public void addFields(FieldList fields) {
		addFields(fields.getFields());
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
		return fields.getField(index);
	}

	/**
	 * Get a field by alias.
	 *
	 * @return The field or null if not found.
	 * @param alias The field alias.
	 */
	public Field getField(String alias) {
		return fields.getField(alias);
	}

	/**
	 * Get a field index by alias.
	 *
	 * @return The field index or -1 if not found.
	 * @param alias The field alias.
	 */
	public int getFieldIndex(String alias) {
		return fields.getFieldIndex(alias);
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
	 * Returns the fields, for use in the friend class Cursor.
	 *
	 * @return The field list.
	 */
	public FieldList getFieldList() {
		return fields;
	}

	/**
	 * Removes all the fields in this table.
	 *
	 * @return The removed fields
	 */
	public List<Field> removeAllFields() {
		clearFieldMap();
		List<Field> removedFields = new ArrayList<>();
		for (int i = 0; i < getFieldCount(); i++) {
			removedFields.add(getField(i));
		}
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
		return fields.removeField(index);
	}

	/**
	 * Remove the with the given alias.
	 *
	 * @param alias The alias of the field
	 * @return The removed field or null.
	 */
	public Field removeField(String alias) {
		int index = getFieldIndex(alias);
		if (index < 0) {
			return null;
		}
		return removeField(index);
	}

	/**
	 * Returns the list of primary key fields.
	 *
	 * @return The list of primary key fields.
	 */
	public List<Field> getPrimaryKeyFields() {
		return fields.getPrimaryKeyFields();
	}

	/**
	 * Check if this table is persistent.
	 *
	 * @return A boolean
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * Set if this table is persistent.
	 *
	 * @param persistent A boolean
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Check if this table has persistent constraints.
	 *
	 * @return A boolean
	 */
	public boolean isPersistentConstraints() {
		if (!isPersistent()) {
			return false;
		}
		return persistentConstraints;
	}

	/**
	 * Set if this table has persistent constraints.
	 *
	 * @param persistentConstraints A boolean
	 */
	public void setPersistentConstraints(boolean persistentConstraints) {
		this.persistentConstraints = persistentConstraints;
	}

	/**
	 * Returns the primary key index.
	 *
	 * @return The primary key index
	 */
	public Index getPrimaryKey() {
		if (primaryKey == null) {
			if (!getPrimaryKeyFields().isEmpty()) {
				primaryKey = new Index();
				primaryKey.setTable(this);
				primaryKey.setUnique(true);
				// Default name
				primaryKey.setName(getName() + "_PK");
				primaryKey.setSchema(getSchema());
				primaryKey.setDescription(getName() + " primary key");
				List<Field> primaryKeyFields = getPrimaryKeyFields();
				for (Field field : primaryKeyFields) {
					primaryKey.add(field);
				}
			}
		}
		return primaryKey;
	}

	/**
	 * Removes the primary key index.
	 */
	public void removePrimaryKey() {
		primaryKey = null;
	}

	/**
	 * Add an index to the list of secondary indexes.
	 *
	 * @param index The index to add
	 */
	public void addIndex(Index index) {
		index.setTable(this);
		if (index.getName() == null) {
			index.setName(getName() + "_SK" + indexes.size());
		}
		if (index.getSchema() == null) {
			index.setSchema(getSchema());
		}
		if (index.getDescription() == null) {
			index.setDescription(getName()
				+ " secondary index "
				+ indexes.size());
		}
		indexes.add(index);
	}

	/**
	 * Remove all secondary indexes.
	 *
	 * @return The list with the removed indexes.
	 */
	public List<Index> removeAllIndexes() {
		List<Index> removedIndexes = new ArrayList<>();
		removedIndexes.addAll(indexes);
		indexes.clear();
		return removedIndexes;
	}

	/**
	 * Remove an index at a given position.
	 *
	 * @param index The index position
	 * @return The removed index.
	 */
	public Index removeIndex(int index) {
		return indexes.remove(index);
	}

	/**
	 * Returns the number of secondary indexes.
	 *
	 * @return The number of secondary indexes.
	 */
	public int getIndexCount() {
		return indexes.size();
	}

	/**
	 * Returns the index at the given position.
	 *
	 * @param index The index position.
	 * @return The index definition.
	 */
	public Index getIndex(int index) {
		return indexes.get(index);
	}

	/**
	 * Returns the index of the given name or null.
	 * 
	 * @param name The name of the index.
	 * @return The index or null.
	 */
	public Index getIndex(String name) {
		for (Index index : indexes) {
			if (index.getName().equals(name)) {
				return index;
			}
		}
		return null;
	}

	/**
	 * Add a foreign key to the list of foreign keys.
	 *
	 * @param foreignKey The foreign key to add.
	 */
	public void addForeignKey(ForeignKey foreignKey) {
		foreignKey.setLocalTable(this);
		foreignKeys.add(foreignKey);
	}

	/**
	 * Remove all foreign keys.
	 *
	 * @return The list with the removed foreign keys.
	 */
	public List<ForeignKey> removeAllForeignKeys() {
		List<ForeignKey> removedForeignKeys = new ArrayList<>();
		removedForeignKeys.addAll(foreignKeys);
		foreignKeys.clear();
		return removedForeignKeys;
	}

	/**
	 * Remove and return the foreiggn key at the given position.
	 *
	 * @param index The foreign key position
	 * @return The foreign key
	 */
	public ForeignKey removeForeignKey(int index) {
		return foreignKeys.remove(index);
	}

	/**
	 * Get the foreign key at the given position.
	 *
	 * @param index The foreign key position in the list
	 * @return The foreign key
	 */
	public ForeignKey getForeignKey(int index) {
		return foreignKeys.get(index);
	}

	/**
	 * Returns the number of foreign keys.
	 *
	 * @return The number of foreign keys.
	 */
	public int getForeignKeyCount() {
		return foreignKeys.size();
	}

	/**
	 * Returns the list of foreign keys.
	 * 
	 * @return The list of foreign keys.
	 */
	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	/**
	 * Returns a simple view of this table, using the primary key as index.
	 *
	 * @return The most simple view.
	 */
	public View getSimpleView() {
		return getSimpleView(getPrimaryKey());
	}

	/**
	 * Returns a simple view of this table, using the argument index as the order by index. No relations are build based
	 * on foreign keys.
	 *
	 * @return The most simple view.
	 * @param orderBy The order by index.
	 */
	public View getSimpleView(Order orderBy) {
		View view = new View(getSession());
		view.setMasterTable(this);
		for (int i = 0; i < getFieldCount(); i++) {
			Field field = new Field(getField(i));
			field.setParentView(view);
			view.addField(field);
		}
		view.setOrderBy(orderBy);
		view.setName(getName());
		view.setAlias(getAlias());
		return view;
	}

	/**
	 * Returns a complex view of this table, using the argument order as the order by clause. Relations are build based
	 * on chained foreign keys including all the fields of the related tables, in the same order as returned by
	 * <code>getAllFields</code>.
	 *
	 * @return The complex view.
	 * @param orderBy The order by index.
	 */
	public View getComplexView(Order orderBy) {
		View view = getSimpleView(orderBy);
		if (getForeignKeyCount() > 0) {
			if (getDescription() != null) {
				view.setDescription(getDescription());
			}
		}
		for (int i = 0; i < getForeignKeyCount(); i++) {
			ForeignKey foreignKey = getForeignKey(i);
			Relation relation = foreignKey.getRelation();
			List<Field> foreignKeyFields = new ArrayList<>();
			for (Segment segment : relation) {
				foreignKeyFields.add(segment.getForeignField());
			}
			Table foreignTable = relation.getForeignTable();
			for (int j = 0; j < foreignTable.getFieldCount(); j++) {
				if (!foreignKeyFields.contains(foreignTable.getField(j))) {
					Field field = new Field(foreignTable.getField(j));
					field.setParentView(view);
					view.addField(field);
				}
			}
			view.addRelation(relation);
		}
		return view;
	}

	/**
	 * Returns the appropriate filter for this table primery key and its argument record, that corresponds to a record
	 * of the table.
	 *
	 * @param record The record with values from this table.
	 * @return A filter that applies to the primary key.
	 */
	public Filter getPrimaryKeyFilter(Record record) {
		return getPrimaryKeyFilter(record.getPrimaryKey());
	}

	/**
	 * Returns the appropriate filter for this table primary key and its argument key, that corresponds to a record
	 * primary key.
	 *
	 * @param primaryKey The primary key definition.
	 * @return The filter.
	 */
	public Filter getPrimaryKeyFilter(OrderKey primaryKey) {
		Filter filter = new Filter();
		List<Field> primaryKeyFields = getPrimaryKeyFields();
		if (primaryKeyFields.size() != primaryKey.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < primaryKeyFields.size(); i++) {
			Field field = primaryKeyFields.get(i);
			Value value = primaryKey.get(i).getValue();
			if (i > 0) {
				filter.and();
			}
			filter.condSimple(field, "EQ", value);
		}
		return filter;
	}

	/**
	 * Returns the default record once assigned the persistor if any.
	 * 
	 * @return The default record.
	 */
	public Record getDefaultRecord() {
		Record record = getFieldList().getDefaultRecord();
		record.setPersistor(getPersistor());
		return record;
	}

	/**
	 * Returns the properties container to store any use additional property.
	 * 
	 * @return The properties.
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}
}
