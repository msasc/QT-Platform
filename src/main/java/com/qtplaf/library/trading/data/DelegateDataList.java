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
import com.qtplaf.library.trading.data.info.DataInfo;

/**
 * A delegate data list, used when a copy of the datalist with a different data info is required. Can access a part of
 * the data indexes.
 * 
 * @author Miquel Sas
 */
public class DelegateDataList extends DataList {

	/**
	 * Source list.
	 */
	private DataList dataList;

	/**
	 * Constructor assigning the data type..
	 * 
	 * @param session The working session.
	 * @param dataInfo The data info.
	 * @param dataList The source data list.
	 */
	public DelegateDataList(Session session, DataInfo dataInfo, DataList dataList) {
		super(session, dataInfo);
		this.dataList = dataList;
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 */
	@Override
	public void add(Data data) {
		dataList.add(data);
	}

	/**
	 * Returns the data element atthe given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {
		return dataList.get(index);
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return dataList.isEmpty();
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return dataList.size();
	}

	/**
	 * Remove and return the data at the given index.
	 * 
	 * @param index The index.
	 * @return The removed data or null.
	 */
	@Override
	public Data remove(int index) {
		return dataList.remove(index);
	}

}
