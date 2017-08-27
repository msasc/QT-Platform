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

import java.util.Comparator;

import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;

/**
 * A recordset of time series data.
 * 
 * @author Miquel Sas
 */
public class DataRecordSet extends RecordSet {
	
	/**
	 * The persistor.
	 */
	private DataPersistor persistor;

	/**
	 * Constructor.
	 * 
	 * @param persistor The data persistor.
	 */
	public DataRecordSet(DataPersistor persistor) {
		super(persistor.getView().getFieldList());
		this.persistor = persistor;
	}
	
	/**
	 * Get a record given its index in the record list.
	 *
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	@Override
	public Record get(int index) {
		return persistor.getRecord(Long.valueOf(index));
	}

	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	@Override
	public int size() {
		return persistor.size().intValue();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Order order) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Comparator<Record> comparator) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check if the recordset can be sorted. Default is true, overwrite this method if the recorset implementation can
	 * not be sorted.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isSortable() {
		return false;
	}
}
