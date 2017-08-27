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
package com.qtplaf.library.database.rdbms;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueMap;
import com.qtplaf.library.database.View;
import com.qtplaf.library.database.rdbms.sql.AddCheck;
import com.qtplaf.library.database.rdbms.sql.AddField;
import com.qtplaf.library.database.rdbms.sql.AddForeignKey;
import com.qtplaf.library.database.rdbms.sql.AddPrimaryKey;
import com.qtplaf.library.database.rdbms.sql.CreateIndex;
import com.qtplaf.library.database.rdbms.sql.CreateSchema;
import com.qtplaf.library.database.rdbms.sql.CreateTable;
import com.qtplaf.library.database.rdbms.sql.Delete;
import com.qtplaf.library.database.rdbms.sql.DropConstraint;
import com.qtplaf.library.database.rdbms.sql.DropField;
import com.qtplaf.library.database.rdbms.sql.DropForeignKey;
import com.qtplaf.library.database.rdbms.sql.DropIndex;
import com.qtplaf.library.database.rdbms.sql.DropPrimaryKey;
import com.qtplaf.library.database.rdbms.sql.DropSchema;
import com.qtplaf.library.database.rdbms.sql.DropTable;
import com.qtplaf.library.database.rdbms.sql.Insert;
import com.qtplaf.library.database.rdbms.sql.Select;
import com.qtplaf.library.database.rdbms.sql.Update;

/**
 * A <code>DBEngineAdapter</code> represents the back-end system to which we connect through a JDBC driver. This class
 * addresses several problems faced when building systems that aim to be as much independent of the underlying database
 * as possible.
 * <p>
 * <b>Type mapping and table creation</b>. Although JDBC drivers may include helper classes or methods to map JDBC types
 * to the names used to create tables, this is always a particular and optional feature of the driver. Thus, a
 * parameterized type map to build <code>CREATE TABLE</code> statements is necessary for the system to address table
 * creation. Note that none of the database access specifications (JDBC, ODBC!) has a solution for this problem and
 * tables are supposed to exist or to be created by other means or hand written statements that are invalid applied to
 * other database systems.
 * <p>
 * <b>Relational queries</b>. Unfortunately, relations have to be expressed in a different way from one database to
 * another. Explicit and implicit relations should be supported for a system to be compatible. Explicit relations take
 * the form,
 * <p>
 * <code>
 * SELECT Detail.product_id, Product.description<br>
 * FROM Detail Left OUTER JOIN Product ON Detail.product_id = Product.id
 * </code>
 * <p>
 * while the same implicit relation would look like,
 * <p>
 * <code>
 * SELECT Detail.product_id, Product.description <br>
 * FROM Detail, Product<br>
 * WHERE Detail.product_id = Product.id(+)
 * </code>
 * <p>
 * <b>System functions.</b> System functions like the current date, time or timestamp unfortunately have different names
 * for different databases. This class addresses this issue by mapping database function names for the current date,
 * time or timestamp.
 *
 * @author Miquel Sas
 */
public abstract class DBEngineAdapter {

	/**
	 * The JDBC driver class name.
	 */
	private String driverClassName;

	/**
	 * Default constructor.
	 */
	public DBEngineAdapter() {
		super();
	}

	/**
	 * Returns the CURRENT DATE function as a string.
	 *
	 * @return The CURRENT DATE function as a string.
	 */
	public abstract String getCurrentDate();

	/**
	 * Returns the CURRENT TIME function as a string.
	 *
	 * @return The CURRENT TIME function as a string.
	 */
	public abstract String getCurrentTime();

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 *
	 * @return The CURRENT TIMESTAMP function as a string.
	 */
	public abstract String getCurrentTimestamp();

	/**
	 * Gets the field definition to use in a <code>CREATE TABLE</code> statement, given a field.
	 *
	 * @return The field definition.
	 * @param field The field.
	 */
	public abstract String getFieldDefinition(Field field);

	/**
	 * Check if the underlying database accepts explicit relations.
	 *
	 * @return A boolean.
	 */
	public abstract boolean isExplicitRelation();

	/**
	 * Returns a appropriate data source.
	 * 
	 * @param info The data source info.
	 * @return The data source.
	 */
	public abstract DataSource getDataSource(DataSourceInfo info);

	/**
	 * Check if the underlying database accepts implicit relations.
	 *
	 * @return A boolean.
	 */
	public boolean isImplicitRelation() {
		return !isExplicitRelation();
	}

	/**
	 * Get the driver class name.
	 * <p>
	 * 
	 * @return The driver class name.
	 */
	public String getDriverClassName() {
		return driverClassName;
	}

	/**
	 * Set the driver class name.
	 * <p>
	 * 
	 * @param driverClassName The driver class name.
	 */
	protected void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	/**
	 * Register the JDBC driver.
	 * 
	 * @throws ClassNotFoundException If such an exception occurs.
	 * @throws IllegalAccessException If such an exception occurs.
	 * @throws InstantiationException If such an exception occurs.
	 * @throws SQLException If such an exception occurs.
	 */
	public void registerDriver()
		throws ClassNotFoundException,
		InstantiationException,
		IllegalAccessException,
		SQLException {
		String driverName = getDriverClassName();
		Class<?> driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		DriverManager.registerDriver(driver);
	}

	/**
	 * Returns the suffix part of a field definition, that is standard, with DEFAULT values and NOT NULL.
	 *
	 * @param field The argument field.
	 * @return The suffix part of the field definition.
	 */
	public String getFieldDefinitionSuffix(Field field) {
		StringBuilder b = new StringBuilder();
		if (field.isCurrentDateTimeOrTimestamp()) {
			b.append("DEFAULT ");
			if (field.isCurrentDate()) {
				b.append(getCurrentDate());
			}
			if (field.isCurrentTime()) {
				b.append(getCurrentTime());
			}
			if (field.isCurrentTimestamp()) {
				b.append(getCurrentTimestamp());
			}
		} else {
			if (field.getInitialValue() != null) {
				b.append(" DEFAULT ");
				b.append(toStringSQL(field.getInitialValue()));
			}
			if (!field.isNullable()) {
				if (field.getInitialValue() == null) {
					if (!field.isDateTimeOrTimestamp()) {
						b.append(" DEFAULT ");
						b.append(toStringSQL(field.getBlankValue()));
					}
				}
				b.append(" NOT NULL");
			}
		}
		return b.toString();
	}

	/**
	 * Returns the add check statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the ADD CONSTRAINT ... CHECK statement.
	 * 
	 * @param table The table.
	 * @param field The field with the constraint.
	 * @return The add check statement.
	 */
	public AddCheck getStatementAddCheck(Table table, Field field) {
		AddCheck addCheck = new AddCheck();
		addCheck.setDBEngineAdapter(this);
		addCheck.setTable(table);
		addCheck.setField(field);
		return addCheck;
	}

	/**
	 * Returns the add field statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the ALTER TABLE <i>table</i> ADD <i>field definition</i> statement.
	 * 
	 * @param table The table.
	 * @param field The field to add.
	 * @return The add field statement.
	 */
	public AddField getStatementAddField(Table table, Field field) {
		AddField addField = new AddField();
		addField.setDBEngineAdapter(this);
		addField.setTable(table);
		addField.setField(field);
		return addField;
	}

	/**
	 * Returns the add foreign key statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the ALTER TABLE <i>table</i> ADD FOREIGN KEY <i>field definition</i> statement.
	 * 
	 * @param table The table.
	 * @param foreignKey The foreign key to add.
	 * @return The add foreign statement.
	 */
	public AddForeignKey getStatementAddForeignKey(Table table, ForeignKey foreignKey) {
		AddForeignKey addForeignKey = new AddForeignKey();
		addForeignKey.setDBEngineAdapter(this);
		addForeignKey.setTable(table);
		addForeignKey.setForeignKey(foreignKey);
		return addForeignKey;
	}

	/**
	 * Returns the add primary key statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the ALTER TABLE ADD PRIMARY KEY statement.
	 * 
	 * @param table The table.
	 * @return The add primary key statement.
	 */
	public AddPrimaryKey getStatementAddPrimaryKey(Table table) {
		AddPrimaryKey addPrimaryKey = new AddPrimaryKey();
		addPrimaryKey.setDBEngineAdapter(this);
		addPrimaryKey.setTable(table);
		return addPrimaryKey;
	}

	/**
	 * Returns the create index statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the CREATE INDEX statement.
	 * 
	 * @param index The index to create.
	 * @return The create table statement.
	 */
	public CreateIndex getStatementCreateIndex(Index index) {
		CreateIndex createIndex = new CreateIndex();
		createIndex.setDBEngineAdapter(this);
		createIndex.setIndex(index);
		return createIndex;
	}

	/**
	 * Returns the create schema statement.
	 * 
	 * @param schema The schema to create.
	 * @return The statement.
	 */
	public CreateSchema getStatementCreateSchema(String schema) {
		CreateSchema createSchema = new CreateSchema();
		createSchema.setDBEngineAdapter(this);
		createSchema.setSchema(schema);
		return createSchema;
	}

	/**
	 * Returns the create table statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the CREATE TABLE statement.
	 * 
	 * @param table The table to create.
	 * @return The create table statement.
	 */
	public CreateTable getStatementCreateTable(Table table) {
		CreateTable createTable = new CreateTable();
		createTable.setDBEngineAdapter(this);
		createTable.setTable(table);
		return createTable;
	}

	/**
	 * Returns the delete statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DELETE statement.
	 * 
	 * @param table The table.
	 * @param filter The filter.
	 * @return The delete statement.
	 */
	public Delete getStatementDelete(Table table, Filter filter) {
		Delete delete = new Delete();
		delete.setDBEngineAdapter(this);
		delete.setTable(table);
		delete.setFilter(filter);
		return delete;
	}

	/**
	 * Returns the delete statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DELETE statement.
	 * 
	 * @param table The table.
	 * @param record The record.
	 * @return The delete statement.
	 */
	public Delete getStatementDelete(Table table, Record record) {
		Delete delete = new Delete();
		delete.setDBEngineAdapter(this);
		delete.setTable(table);
		delete.setRecord(record);
		return delete;
	}

	/**
	 * Returns the drop field statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the ALTER TABLE <i>table</i> DROP COLUMN <i>field</i> statement.
	 *
	 * @return The drop field statement.
	 */
	public DropField getStatementDropField() {
		DropField dropField = new DropField();
		dropField.setDBEngineAdapter(this);
		return dropField;
	}

	/**
	 * Returns the drop constraint statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the ALTER TABLE <i>table</i> DROP CONSTRAINT <i>constraint</i> statement.
	 *
	 * @return The drop constraint statement.
	 */
	public DropConstraint getStatementDropConstraint() {
		DropConstraint dropConstraint = new DropConstraint();
		dropConstraint.setDBEngineAdapter(this);
		return dropConstraint;
	}

	/**
	 * Returns the drop constraint statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the ALTER TABLE <i>table</i> DROP FOREIGN KEY statement.
	 *
	 * @param table The table.
	 * @param foreignKey The foreign key to drop.
	 * @return The drop foreign key statement.
	 */
	public DropForeignKey getStatementDropForeignKey(Table table, ForeignKey foreignKey) {
		DropForeignKey dropForeignKey = new DropForeignKey();
		dropForeignKey.setDBEngineAdapter(this);
		dropForeignKey.setTable(table);
		dropForeignKey.setForeignKey(foreignKey);
		return dropForeignKey;
	}

	/**
	 * Returns the drop index statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DROP INDEX statement.
	 *
	 * @param index The index to drop.
	 * @return The drop index statement.
	 */
	public DropIndex getStatementDropIndex(Index index) {
		DropIndex dropIndex = new DropIndex();
		dropIndex.setDBEngineAdapter(this);
		dropIndex.setIndex(index);
		return dropIndex;
	}

	/**
	 * Returns the drop primary key statement. This method is aimed to be overwritten if the database adapter has a
	 * different syntax for the ALTER TABLE <i>table</i> DROP PRIMARY KEY statement.
	 *
	 * @return The drop primary key statement.
	 */
	public DropPrimaryKey getStatementDropPrimearyKey() {
		DropPrimaryKey dropPrimaryKey = new DropPrimaryKey();
		dropPrimaryKey.setDBEngineAdapter(this);
		return dropPrimaryKey;
	}

	/**
	 * Returns the drop schema statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DROP TABLE statement.
	 *
	 * @param schema The schema.
	 * @return The drop schema statement.
	 */
	public DropSchema getStatementDropSchema(String schema) {
		DropSchema dropSchema = new DropSchema();
		dropSchema.setDBEngineAdapter(this);
		dropSchema.setSchema(schema);
		return dropSchema;
	}

	/**
	 * Returns the drop table statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the DROP TABLE statement.
	 *
	 * @param table The table.
	 * @return The drop table statement.
	 */
	public DropTable getStatementDropTable(Table table) {
		DropTable dropTable = new DropTable();
		dropTable.setDBEngineAdapter(this);
		dropTable.setTable(table);
		return dropTable;
	}

	/**
	 * Returns the insert statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the INSERT INTO statement.
	 *
	 * @param table The table.
	 * @param record The record to insert.
	 * @return The insert statement.
	 */
	public Insert getStatementInsert(Table table, Record record) {
		Insert insert = new Insert();
		insert.setDBEngineAdapter(this);
		insert.setTable(table);
		insert.setRecord(record);
		return insert;
	}

	/**
	 * Returns the select query. This method is aimed to be overwritten if the database adapter has a different syntax
	 * for the SELECT query.
	 *
	 * @param view The view.
	 * @return The select query.
	 */
	public Select getQuerySelect(View view) {
		Select select = new Select();
		select.setDBEngineAdapter(this);
		select.setView(view);
		return select;
	}

	/**
	 * Returns the select query. This method is aimed to be overwritten if the database adapter has a different syntax
	 * for the SELECT query.
	 *
	 * @param view The view.
	 * @param filter The filter.
	 * @return The select query.
	 */
	public Select getQuerySelect(View view, Filter filter) {
		Select select = new Select();
		select.setDBEngineAdapter(this);
		select.setView(view);
		select.setFilter(filter);
		return select;
	}

	/**
	 * Returns the select query. This method is aimed to be overwritten if the database adapter has a different syntax
	 * for the SELECT query.
	 *
	 * @param view The view.
	 * @param criteria The filter criteria.
	 * @return The select query.
	 */
	public Select getQuerySelect(View view, Criteria criteria) {
		Select select = new Select();
		select.setDBEngineAdapter(this);
		select.setView(view);
		select.setFilter(new Filter(criteria));
		return select;
	}

	/**
	 * Returns the update statement. This method is aimed to be overwritten if the database adapter has a different
	 * syntax for the UPDATE statement.
	 *
	 * @param table The table.
	 * @param record The record to update.
	 * @return The update statement.
	 */
	public Update getStatementUpdate(Table table, Record record) {
		Update update = new Update();
		update.setDBEngineAdapter(this);
		update.setTable(table);
		update.setRecord(record);
		return update;
	}

	/**
	 * Returns the massive update statement for the table, filter and field map values.
	 * 
	 * @param table The table.
	 * @param filter The filter.
	 * @param map The map of values.
	 * @return The update statement.
	 */
	public Update getStatementUpdate(Table table, Filter filter, ValueMap map) {
		Update update = new Update();
		update.setDBEngineAdapter(this);
		update.setTable(table);
		update.setFilter(filter);
		if (map.isTypeIndex()) {
			List<ValueMap.IndexPair> indexPairs = map.getIndexPairs();
			for (ValueMap.IndexPair indexPair : indexPairs) {
				update.set(table.getField(indexPair.index), indexPair.value);
			}
		}
		if (map.isTypeAlias()) {
			List<ValueMap.AliasPair> aliasPairs = map.getAliasPairs();
			for (ValueMap.AliasPair aliasPair : aliasPairs) {
				update.set(table.getField(aliasPair.alias), aliasPair.value);
			}
		}
		if (map.isTypeField()) {
			List<ValueMap.FieldPair> fieldPairs = map.getFieldPairs();
			for (ValueMap.FieldPair fieldPair : fieldPairs) {
				update.set(fieldPair.field, fieldPair.value);
			}
		}
		return update;
	}

	/**
	 * Return a string representation of the date, valid to be used in an SQL statement.
	 * 
	 * @param date The date.
	 * @return The SQL string representation.
	 */
	public abstract String toStringSQL(java.sql.Date date);

	/**
	 * Return a string representation of the time, valid to be used in an SQL statement.
	 * 
	 * @param time The time.
	 * @return The SQL string representation.
	 */
	public abstract String toStringSQL(java.sql.Time time);

	/**
	 * Return a string representation of the timestamp, valid to be used in an SQL statement.
	 * 
	 * @param timestamp The timestamp.
	 * @return The SQL string representation.
	 */
	public abstract String toStringSQL(java.sql.Timestamp timestamp);

	/**
	 * Return a string representation of the number, valid to be used in an SQL statement.
	 * 
	 * @param number The number.
	 * @return The SQL string representation.
	 */
	public String toStringSQL(Number number) {
		return number.toString();
	}

	/**
	 * Return a string representation of the string, valid to be used in an SQL statement.
	 * 
	 * @param string The string.
	 * @return The SQL string representation.
	 */
	public String toStringSQL(String string) {
		return "'" + string + "'";
	}

	/**
	 * Return a string representation of the value valid to be used in an SQL statement.
	 * 
	 * @param value A xvr.com.lib.entity.Value
	 * @return The SQL string representation of the value.
	 */
	public String toStringSQL(Value value) {
		if (value == null) {
			return "";
		}
		if (value.isBoolean()) {
			return toStringSQL(value.getBoolean() ? "Y" : "N");
		}
		if (value.isNumber()) {
			return toStringSQL(value.getNumber());
		}
		if (value.isString()) {
			return toStringSQL(value.getString());
		}
		if (value.isDate()) {
			return toStringSQL(value.getDate());
		}
		if (value.isTime()) {
			return toStringSQL(value.getTime());
		}
		if (value.isTimestamp()) {
			return toStringSQL(value.getTimestamp());
		}
		throw new IllegalArgumentException("Invalid value type: " + value.getType().name());
	}

}
