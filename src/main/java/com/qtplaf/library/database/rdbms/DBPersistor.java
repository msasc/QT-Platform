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

import java.sql.SQLException;
import java.util.List;

import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.OrderKey;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueMap;
import com.qtplaf.library.database.View;
import com.qtplaf.library.database.rdbms.sql.Select;

/**
 * Database persistor.
 * 
 * @author Miquel Sas
 */
public class DBPersistor implements Persistor {

	/**
	 * The underlying <code>DBEngine</code>.
	 */
	private DBEngine dbEngine;
	/**
	 * The underlying <code>View</code>.
	 */
	private View view;

	/**
	 * Constructor.
	 * 
	 * @param dbEngine The <code>DBEngine</code>.
	 * @param table The <code>Table</code>.
	 */
	public DBPersistor(DBEngine dbEngine, Table table) {
		super();
		this.dbEngine = dbEngine;
		this.view = table.getSimpleView(table.getPrimaryKey());
		this.view.setPersistor(this);
	}

	/**
	 * Constructor.
	 * 
	 * @param dbEngine The <code>DBEngine</code>.
	 * @param view The <code>View</code>.
	 */
	public DBPersistor(DBEngine dbEngine, View view) {
		super();
		this.dbEngine = dbEngine;
		this.view = view;
		this.view.setPersistor(this);
	}


	/**
	 * Returns a suitable DDL.
	 * 
	 * @return The DDL.
	 */
	@Override
	public PersistorDDL getDDL() {
		return new DBPersistorDDL(dbEngine);
	}
	
	/**
	 * Returns the underlying view of the persistor.
	 * 
	 * @return The view.
	 */
	@Override
	public View getView() {
		return view;
	}

	/**
	 * Returns the default record of the underlying view.
	 * 
	 * @return The default record of the underlying view.
	 */
	@Override
	public Record getDefaultRecord() {
		return view.getDefaultRecord();
	}

	/**
	 * Returns the record given the primary key.
	 * 
	 * @param primaryKey The primary key.
	 * @return The record or null.
	 */
	@Override
	public Record getRecord(OrderKey primaryKey) throws PersistorException {
		try {
			return dbEngine.executeSelectPrimaryKey(view, primaryKey);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the record given the list of primnary key values.
	 * 
	 * @param primaryKeyValues The list of primnary key values.
	 * @return The record or null.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public Record getRecord(List<Value> primaryKeyValues) throws PersistorException {
		return getRecord(new OrderKey(primaryKeyValues));
	}

	/**
	 * Returns the record given the list of primnary key values.
	 * 
	 * @param primaryKeyValues The list of primnary key values.
	 * @return The record or null.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public Record getRecord(Value... primaryKeyValues) throws PersistorException {
		return getRecord(new OrderKey(primaryKeyValues));
	}

	/**
	 * Returns the number of fields this persistor manages.
	 * 
	 * @return The number of fields.
	 */
	@Override
	public int getFieldCount() {
		return view.getFieldCount();
	}

	/**
	 * Returns a field by index.
	 * 
	 * @param index The field index.
	 * @return The field or null.
	 */
	@Override
	public Field getField(int index) {
		return view.getField(index);
	}

	/**
	 * Returns the index of the field with the given alias or -1.
	 * 
	 * @param alias The field alias.
	 * @return The index of the field with the given alias or -1.
	 */
	@Override
	public int getFieldIndex(String alias) {
		return view.getFieldIndex(alias);
	}

	/**
	 * Returns a field by alias.
	 * 
	 * @param alias The field alias.
	 * @return The field or null.
	 */
	@Override
	public Field getField(String alias) {
		return view.getField(alias);
	}

	/**
	 * Count the number of records that agree with the criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @return The number of records that agree with the criteria.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public long count(Criteria criteria) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectCount(view, filter);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Deletes records based on a selection criteria.
	 * 
	 * @param criteria The criteria to select the entities to delete.
	 * @return The number of deleted records.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int delete(Criteria criteria) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeDelete(view.getMasterTable(), filter);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Delete a record, that must contain the primary key fields of the master table.
	 * 
	 * @param record The record to delete.
	 * @return The number deleted records (one or zero).
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int delete(Record record) throws PersistorException {
		try {
			return dbEngine.executeDelete(view.getMasterTable(), view.getMasterTableRecord(record));
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Check if the record exists within its persistent layer.
	 * 
	 * @param record The record.
	 * @return A boolean stating if the record exists within its persistent layer.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public boolean exists(Record record) throws PersistorException {
		try {
			return dbEngine.existsRecord(view.getMasterTable(), view.getMasterTableRecord(record));
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}
	
	/**
	 * Check if the primary key exists within its persistent layer.
	 * 
	 * @param primaryKey The primary key.
	 * @return A boolean stating if the record exists within its persistent layer.
	 * @throws PersistorException If a persistor error occurs.
	 */
	@Override
	public boolean exists(OrderKey primaryKey) throws PersistorException {
		try {
			return dbEngine.existsRecord(view.getMasterTable(), primaryKey);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}
	
	/**
	 * Check if the primary key exists within its persistent layer.
	 * 
	 * @param primaryKey The list of primary key values.
	 * @return A boolean stating if the record exists within its persistent layer.
	 * @throws PersistorException If a persistor error occurs.
	 */
	@Override
	public boolean exists(Value... primaryKey) throws PersistorException {
		OrderKey key = new OrderKey();
		for (Value value : primaryKey) {
			key.add(value, true);
		}
		return exists(key);
	}

	/**
	 * Returns true if the record has successfully refreshed.
	 * 
	 * @param record The source record that must have set at least the primary key
	 * @return A boolean indicating whether the record has successfully refreshed.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public boolean refresh(Record record) throws PersistorException {
		try {
			Record recordView = dbEngine.executeSelectPrimaryKey(view, record.getPrimaryKey());
			if (recordView != null) {
				Record.move(recordView, record);
				return true;
			}
			return false;
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Insert a record.
	 * 
	 * @param record The record to insert.
	 * @return The number of already inserted records (one or zero).
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int insert(Record record) throws PersistorException {
		try {
			return dbEngine.executeInsert(view.getMasterTable(), view.getMasterTableRecord(record));
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns a record iterator to scan the records that agree with the criteria.
	 * 
	 * @param criteria Filter criteria.
	 * @return The record iterator.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public RecordIterator iterator(Criteria criteria) throws PersistorException {
		return iterator(criteria, null);
	}

	/**
	 * Returns a record iterator to scan the records that agree with the criteria, in the given order.
	 * 
	 * @param criteria Filter criteria.
	 * @param order Order.
	 * @return The record iterator.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public RecordIterator iterator(Criteria criteria, Order order) throws PersistorException {
		try {
			// Use a copy of the view to change the order without side effects.
			View view = new View(this.view);
			if (order != null) {
				view.setOrderBy(order);
			}
			Filter filter = new Filter(criteria);
			Select select = dbEngine.getDBEngineAdapter().getQuerySelect(view, filter);
			Cursor cursor = dbEngine.executeSelectCursor(select);
			return new DBRecordIterator(cursor);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the maximum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The maximum values.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap max(Criteria criteria, int... indexes) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectMaxMap(view, filter, indexes);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the maximum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field aliases.
	 * @return The maximum value.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap max(Criteria criteria, String... aliases) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectMaxMap(view, filter, aliases);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The minimum values.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap min(Criteria criteria, int... indexes) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectMinMap(view, filter, indexes);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field aliases.
	 * @return The minimum values.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap min(Criteria criteria, String... aliases) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectMinMap(view, filter, aliases);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Saves the record, inserting if it does not exists and updating if it does.
	 * 
	 * @param record The record to save.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int save(Record record) throws PersistorException {
		try {
			return dbEngine.executeSave(view.getMasterTable(), view.getMasterTableRecord(record));
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Select a list of records based on a selection criteria.
	 *
	 * @param criteria The selection criteria.
	 * @return The list of records.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public RecordSet select(Criteria criteria) throws PersistorException {
		return select(criteria, view.getOrderBy());
	}

	/**
	 * Select a list of records based on a selection criteria, returning the list with the given order.
	 *
	 * @param criteria The selection criteria.
	 * @param order The selection order.
	 * @return The list of records.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public RecordSet select(Criteria criteria, Order order) throws PersistorException {
		try {
			View view = new View(this.view);
			view.setOrderBy(order);
			Filter filter = new Filter(criteria);
			Select select = dbEngine.getDBEngineAdapter().getQuerySelect(view, filter);
			return dbEngine.executeSelectRecordSet(select);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 *
	 * @param criteria The filter criteria.
	 * @param indexes The list of indexes.
	 * @return The list of values in a value map keyed by index.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap sum(Criteria criteria, int... indexes) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectSumMap(view, filter, indexes);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The list of field aliases.
	 * @return The list of values in a value map keyed by alias.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public ValueMap sum(Criteria criteria, String... aliases) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeSelectSumMap(view, filter, aliases);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Update a record.
	 * 
	 * @param record The record to update.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int update(Record record) throws PersistorException {
		try {
			return dbEngine.executeUpdate(view.getMasterTable(), view.getMasterTableRecord(record));
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

	/**
	 * Update a set of fields with given values for the firlter criteria. The map of values can be keyed either by
	 * index, alias or field.
	 * 
	 * @param criteria The filter criteria.
	 * @param map The map of field-values.
	 * @return The number of updated records.
	 * @throws PersistorException If such an error occurs.
	 */
	@Override
	public int update(Criteria criteria, ValueMap map) throws PersistorException {
		try {
			Filter filter = new Filter(criteria);
			return dbEngine.executeUpdate(view.getMasterTable(), filter, map);
		} catch (SQLException exc) {
			throw new PersistorException(exc.getMessage(), exc);
		}
	}

}
