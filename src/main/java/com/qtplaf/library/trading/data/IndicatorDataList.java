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

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.map.CacheMap;

/**
 * A data list that retrieves its data from an indicator, thus calculating the data each time it is retrieved through
 * the <tt>get</tt> method.
 * 
 * @author Miquel Sas
 */
public class IndicatorDataList extends DataList {

	/**
	 * The indicator that calculates the data list.
	 */
	private Indicator indicator;
	/**
	 * The list of indicator sources that the indicator will use to calculate data.
	 */
	private List<IndicatorSource> indicatorSources;
	/**
	 * A data list to cache this indicator calculated data.
	 */
	private CacheMap<Integer, Data> map = new CacheMap<>();

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param indicator The indicator.
	 * @param indicatorSources The list of indicator sources.
	 */
	public IndicatorDataList(
		Session session,
		Indicator indicator,
		List<IndicatorSource> indicatorSources) {
		super(session, indicator.getIndicatorInfo());
		this.indicator = indicator;
		this.indicatorSources = indicatorSources;
		this.indicator.start(indicatorSources);
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
	 * Check whether this indicator data list is equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IndicatorDataList) {
			IndicatorDataList idl = (IndicatorDataList) obj;
			if (!getIndicator().equals(idl.getIndicator())) {
				return false;
			}
			if (!ListUtils.equals(getIndicatorSources(), idl.getIndicatorSources())) {
				return false;
			}
			return super.equals(idl);
		}
		return false;
	}

	/**
	 * Returns the list of indicator sources.
	 * 
	 * @return The list of indicator sources.
	 */
	public List<IndicatorSource> getIndicatorSources() {
		return indicatorSources;
	}

	/**
	 * Returns the underlying indicator of this indicator data list.
	 * 
	 * @return The indicator.
	 */
	public Indicator getIndicator() {
		return indicator;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return indicatorSources.get(0).getDataList().size();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 */
	@Override
	public void add(Data data) {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns the data element at the given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {
		Data data = map.get(index);
		if (data != null) {
			return data;
		}
		return calculate(index);
	}

	/**
	 * Calculate and store data at the given index.
	 * 
	 * @param index The index.
	 * @return The calculated data.
	 */
	public Data calculate(int index) {
		Data data = indicator.calculate(index, indicatorSources, this);
		map.put(index, data);
		return data;
	}

	/**
	 * Remove the calculated data index.
	 * 
	 * @param index The index to remove.
	 * @return The removed data.
	 */
	@Override
	public Data remove(int index) {
		return map.remove(index);
	}

	/**
	 * Returns a boolean indicating if the argument index has been calculated.
	 * 
	 * @param index The index to check.
	 * @return A boolean indicating if the argument index has been calculated.
	 */
	public boolean hasCalculated(int index) {
		return map.containsKey(index);
	}

	/**
	 * Returns a string representation of this indicator data list.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString());
		// if (indicatorSources != null && !indicatorSources.isEmpty()) {
		// b.append(" Sources: ");
		// for (int i = 0; i < indicatorSources.size(); i++) {
		// if (i > 0) {
		// b.append(", ");
		// }
		// b.append(indicatorSources.get(i));
		// }
		// }
		return b.toString();
	}
}
