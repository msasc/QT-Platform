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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;

/**
 * An SQL database view, build with a master table and relations. When defining a view, fields must belong to tables of
 * the relations or be non persistent.
 *
 * @author Miquel Sas
 */
public class View implements Comparable<Object> {

	/**
	 * Returns the master table record for the view and a record of the view.
	 * 
	 * @param view The view.
	 * @param record The record of the view.
	 * @return The master table record.
	 */
	public static Record getMasterTableRecord(View view, Record record) {
		Table masterTable = view.getMasterTable();
		Record masterTableRecord = masterTable.getDefaultRecord();
		Record.move(record, masterTableRecord);
		return masterTableRecord;
	}

	/**
	 * The name of the table.
	 */
	private String name = null;
	/**
	 * The alias.
	 */
	private String alias = null;
	/**
	 * An optional description.
	 */
	private String description = null;
	/**
	 * The master table.
	 */
	private Table masterTable = null;
	/**
	 * The array of relations.
	 */
	private final List<Relation> relations = new ArrayList<>();

	/**
	 * The list of fields.
	 */
	private final FieldList fields = new FieldList();

	/**
	 * The array of group by fields.
	 */
	private final List<Field> groupBy = new ArrayList<>();
	/**
	 * The order.
	 */
	private Order orderBy = null;
	/**
	 * Having clause for group by views.
	 */
	private String having;
	/**
	 * The persistor that provides persistence to this table.
	 */
	private Persistor persistor;
	/**
	 * Optional working session.
	 */
	private Session session;

	/**
	 * Default constructor.
	 */
	public View() {
		super();
	}

	/**
	 * Constructor assigning the working session.
	 * 
	 * @param session The working session.
	 */
	public View(Session session) {
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
	 * Copy constructor.
	 *
	 * @param view The source view
	 */
	public View(View view) {
		super();
		this.name = view.name;
		this.alias = view.alias;
		this.description = view.description;
		this.masterTable = view.masterTable;
		this.relations.addAll(view.relations);
		this.fields.getFields().addAll(view.fields.getFields());
		this.groupBy.addAll(view.groupBy);
		this.orderBy = view.orderBy;
		this.having = view.having;
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
	 * Gets the name to use in FROM clause.
	 *
	 * @return The appropriate name for a FROM clause.
	 */
	public String getNameFrom() {
		if (alias != null && getName() != null) {
			return getName() + " " + alias;
		}
		return getName();
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
	 * Get the description.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 *
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the master table.
	 *
	 * @return The master table.
	 */
	public Table getMasterTable() {
		return masterTable;
	}

	/**
	 * Returns the master table record for a record of this view.
	 * 
	 * @param record The record.
	 * @return The master table record.
	 */
	public Record getMasterTableRecord(Record record) {
		return getMasterTableRecord(this, record);
	}

	/**
	 * Set the master table.
	 *
	 * @param masterTable The master table.
	 */
	public void setMasterTable(Table masterTable) {
		this.masterTable = masterTable;
		setPersistor(masterTable.getPersistor());
	}

	/**
	 * Compares this table with the argument table. Returns 0 if they are equal, -1 if this value is less than the
	 * argument, and 1 if it is greater. A table is considered to be equal to another table in the context of a
	 * <code>SELECT</code> statement.
	 *
	 * @return An int
	 * @param o The object to compare with.
	 */
	@Override
	public int compareTo(Object o) {
		View view = null;
		try {
			view = (View) o;
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException("Not comparable type: "
				+ o.getClass().getName());
		}
		return getNameFrom().compareTo(view.getNameFrom());
	}

	/**
	 * Clear the field map, necessary when changing fields.
	 */
	public void clearFieldMap() {
		fields.clearFieldMap();
	}

	/**
	 * Add a field to this view.
	 *
	 * @param field The field to add.
	 */
	public void addField(Field field) {
		field.setParentView(this);
		fields.addField(field);
		clearFieldMap();
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
	 * Get a field index by alias.
	 *
	 * @return The field index or -1 if not found.
	 * @param alias The field alias.
	 */
	public int getFieldIndex(String alias) {
		return fields.getFieldIndex(alias);
	}

	/**
	 * Returns the list of indexes of the list of aliases.
	 * 
	 * @param aliases The list of aliases.
	 * @return The list of indexes.
	 */
	public List<Integer> getFieldIndexes(String... aliases) {
		List<Integer> indexes = new ArrayList<>();
		for (String alias : aliases) {
			int index = getFieldIndex(alias);
			if (index >= 0) {
				indexes.add(index);
			}
		}
		return indexes;
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
	 * Get a field by index.
	 *
	 * @return The field.
	 * @param index The field index.
	 */
	public Field getField(int index) {
		return fields.getField(index);
	}

	/**
	 * Get a field by name.
	 *
	 * @return The field or null if not found.
	 * @param alias The field alias.
	 */
	public Field getField(String alias) {
		return fields.getField(alias);
	}

	/**
	 * Add a field to the group by list.
	 *
	 * @param field The field to add.
	 */
	public void addGroupBy(Field field) {
		groupBy.add(field);
	}

	/**
	 * Returns the group by field given its index in the group by list.
	 *
	 * @param index The index in the group by list.
	 * @return The field.
	 */
	public Field getGroupBy(int index) {
		return groupBy.get(index);
	}

	/**
	 * Returns the number of items in the group by list of fields.
	 *
	 * @return The number of group by fields.
	 */
	public int getGroupByCount() {
		return groupBy.size();
	}

	/**
	 * Returns the having clause (only valid on group by views)
	 * 
	 * @return The having clause.
	 */
	public String getHaving() {
		return having;
	}

	/**
	 * Set the having clause (only valid on group by views)
	 * 
	 * @param having The having clause.
	 */
	public void setHaving(String having) {
		this.having = having;
	}

	/**
	 * Gets the order by index.
	 *
	 * @return The order by index.
	 */
	public Order getOrderBy() {
		return orderBy;
	}

	/**
	 * Set the order by.
	 *
	 * @param orderBy The order by index.
	 */
	public void setOrderBy(Order orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Add an order by segment.
	 *
	 * @param field The field of the order by segment.
	 */
	public void addOrderBy(Field field) {
		addOrderBy(field, true);
	}

	/**
	 * Add an order by segment.
	 *
	 * @param field The field of the order by segment.
	 * @param asc A flag indicating if the segment is ascending or descending.
	 */
	public void addOrderBy(Field field, boolean asc) {
		if (orderBy == null) {
			orderBy = new Order();
		}
		orderBy.add(field, asc);
	}

	/**
	 * Add a relation to this view.
	 *
	 * @param relation The relation to add.
	 */
	public void addRelation(Relation relation) {
		relations.add(relation);
		clearFieldMap();
	}

	/**
	 * Returns the number of relations.
	 *
	 * @return The number of relations.
	 */
	public int getRelationCount() {
		return relations.size();
	}

	/**
	 * Get a relation given its index in the list of relations.
	 *
	 * @return The relation.
	 * @param index The relation index.
	 */
	public Relation getRelation(int index) {
		return relations.get(index);
	}

	/**
	 * Returns the list of relations.
	 *
	 * @return The list of relations.
	 */
	public List<Relation> getRelations() {
		return relations;
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
	 * Remove all fields. At the same time removes the order by order and the group by fields.
	 *
	 * @return The removed fields
	 */
	public List<Field> removeAllFields() {
		List<Field> removedFields = new ArrayList<>();
		for (int i = 0; i < getFieldCount(); i++) {
			removedFields.add(getField(i));
		}
		fields.clear();
		orderBy = null;
		groupBy.clear();
		clearFieldMap();
		return removedFields;
	}

	/**
	 * Removes the field at the give index. At the same time removes the order by order and the group by fields.
	 *
	 * @param index The index of the field.
	 * @return The removed field
	 */
	public Field removeField(int index) {
		Field removedField = fields.removeField(index);
		orderBy = null;
		groupBy.clear();
		clearFieldMap();
		return removedField;
	}

	/**
	 * Removes all relations.
	 *
	 * @return The removed relations.
	 */
	public List<Relation> removeAllRelations() {
		List<Relation> removedRelations = new ArrayList<>();
		removedRelations.addAll(relations);
		return removedRelations;
	}

	/**
	 * Removes the relation at the given index.
	 *
	 * @param index The relation position
	 * @return The removed relation
	 */
	public Relation removeRelation(int index) {
		return relations.remove(index);
	}

	/**
	 * Removes all the group by fields.
	 *
	 * @return The removed group by fields
	 */
	public List<Field> removeAllGroupByFields() {
		List<Field> removedFields = new ArrayList<>();
		removedFields.addAll(groupBy);
		groupBy.clear();
		return removedFields;
	}

	/**
	 * Removes a group by field given its position.
	 *
	 * @param index The field position
	 * @return The removed group by field
	 */
	public Field removeGoupByField(int index) {
		return groupBy.remove(index);
	}

	/**
	 * Gets the list of tables, where the first is this view.
	 *
	 * @return The array of table definitions.
	 */
	public List<Table> getAllTables() {
		List<Table> list = new ArrayList<>();
		list.add(getMasterTable());
		for (Relation relation : relations) {
			if (!list.contains(relation.getLocalTable())) {
				list.add(relation.getLocalTable());
			}
			if (!list.contains(relation.getForeignTable())) {
				list.add(relation.getForeignTable());
			}
		}
		return list;
	}

}
