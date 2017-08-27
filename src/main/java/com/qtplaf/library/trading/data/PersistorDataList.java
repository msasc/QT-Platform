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

package com.qtplaf.library.trading.data;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.util.map.CacheMap;

/**
 * A data list that retrieves its data from persistor. The contract for a persistor of data lists is that fields must be
 * defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory to start
 * at 1, not negative and indexed.</li>
 * <li>The second field is a long, the time of the timed data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * With this structure, the <tt>PersistorDataList</tt> can handle not only <tt>Data</tt> data, but any timed
 * <tt>Data</tt>.
 * 
 * @author Miquel Sas
 */
public class PersistorDataList extends DataList {

	/**
	 * The underlying persistor.
	 */
	private DataPersistor dataPersistor;
	/**
	 * A map to cache retrieved records by relative index.
	 */
	private CacheMap<Integer, Record> map = new CacheMap<>();
	/**
	 * The page size to read chunks.
	 */
	private int pageSize = 100;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param dataInfo The data info.
	 * @param persistor The persistor.
	 */
	public PersistorDataList(Session session, DataInfo dataInfo, Persistor persistor) {
		super(session, dataInfo);
		this.dataPersistor = new DataPersistor(persistor);
		this.map.setCacheSize(-1);
	}

	/**
	 * Returns the data persistor.
	 * 
	 * @return The data persistor.
	 */
	public DataPersistor getDataPersistor() {
		return dataPersistor;
	}

	/**
	 * Returns the index in the data item given the index in the record.
	 * 
	 * @param recordIndex The index in the record.
	 * @return The index in the data.
	 */
	public int getDataIndex(int recordIndex) {
		return dataPersistor.getDataIndex(recordIndex);
	}

	/**
	 * Returns the data index given the field alias.
	 * 
	 * @param alias The field alias.
	 * @return The index in the data.
	 */
	public int getDataIndex(String alias) {
		return dataPersistor.getDataIndex(alias);
	}

	/**
	 * Returns the index in the record given the index in the data item.
	 * 
	 * @param dataIndex The index in the data item.
	 * @return The index in the record.
	 */
	public int getRecordIndex(int dataIndex) {
		return dataPersistor.getRecordIndex(dataIndex);
	}

	/**
	 * Returns the cache size.
	 * 
	 * @return The cache size.
	 */
	public int getCacheSize() {
		return map.getCacheSize();
	}

	/**
	 * Sets the cache size. Setting the cache size to -1 has the effect to cache all data.
	 * 
	 * @param cacheSize The cache size.
	 */
	public void setCacheSize(int cacheSize) {
		map.setCacheSize(cacheSize);
	}

	/**
	 * Retuns the page size used to read chunks.
	 * 
	 * @return The page size.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return dataPersistor.size().intValue();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return dataPersistor.isEmpty();
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 */
	@Override
	public void add(Data data) {
		// TODO Pending to implement
	}

	/**
	 * Returns the data element at the given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {
		return getData(getRecord(index));
	}

	/**
	 * Returns the data given its record.
	 * 
	 * @param record The underlying record.
	 * @return The data element.
	 */
	public Data getData(Record record) {
		return dataPersistor.getData(record);
	}

	/**
	 * Returns the record given the index.
	 * 
	 * @param index The index.
	 * @return The record.
	 */
	public Record getRecord(int index) {
		Record record = getRecordFromCache(index);
		if (record != null) {
			return record;
		}
		record = dataPersistor.getRecord(Long.valueOf(index));
		if (record != null) {
			map.put(index, record);
		}
		return record;
	}

	/**
	 * Returns the record from the cache.
	 * 
	 * @param index The relative index starting at 0.
	 * @return The record or null if not present in the cache.
	 */
	private Record getRecordFromCache(int index) {
		return map.get(index);
	}

	/**
	 * Removes a data element from the cache.
	 * 
	 * @param index The index of the data to remove.
	 * @return The removed data or null.
	 */
	@Override
	public Data remove(int index) {
		Record record = map.remove(index);
		if (record == null) {
			return null;
		}
		return dataPersistor.getData(record);
	}
}
