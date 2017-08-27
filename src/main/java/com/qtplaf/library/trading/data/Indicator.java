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
import com.qtplaf.library.trading.data.info.IndicatorInfo;

/**
 * Indicator base class.
 * <p>
 * Implementations must define the <b><i>IndicatorInfo</i></b> that, additionally to the base <b><i>DataInfo</i></b>
 * outputs, indicates the input parameters and their values.
 * <p>
 * Implementations must also implement the <b><i>calculate</i></b> method. This method receives 3 parameters:
 * <ul>
 * <li>The index of the data to be calculated.</li>
 * <li>The list of indicator sources defined in the <b><i>IndicatorInfo</i></b> as inputs.</li>
 * <li>The list of already calculated values.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public abstract class Indicator {

	/**
	 * Calculates an indicator.
	 * 
	 * @param session The working session.
	 * @param indicator The indicator to calculate.
	 * @param indicatorSources The list of indicator sources.
	 * @return The indicator data list.
	 */
	public static DataList calculate(Session session, Indicator indicator, List<IndicatorSource> indicatorSources) {
		IndicatorDataList indicatorData = new IndicatorDataList(session, indicator, indicatorSources);
		indicator.start(indicatorSources);
		int size = indicatorSources.get(0).getDataList().size();
		for (int index = 0; index < size; index++) {
			Data data = indicator.calculate(index, indicatorSources, indicatorData);
			indicatorData.add(data);
		}
		return indicatorData;
	}

	/**
	 * The indicator info to be configured.
	 */
	private IndicatorInfo indicatorInfo;
	/**
	 * The number of indexes for all the indicator sources.
	 */
	private int numIndexes;
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public Indicator(Session session) {
		super();
		this.session = session;
		indicatorInfo = new IndicatorInfo(session, this);
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
	 * Returns the indicator info.
	 * 
	 * @return The indicator info.
	 */
	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	/**
	 * Calculates the total number of indexes as a helper to further calculations. Normally should be called in the
	 * <i>start</i> method.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	protected void calculateNumIndexes(List<IndicatorSource> indicatorSources) {
		numIndexes = 0;
		for (IndicatorSource source : indicatorSources) {
			numIndexes += source.getIndexes().size();
		}
	}

	/**
	 * Returns the total number of indexes, that must be previously calculated with a call to
	 * <i>calculateNumIndexes</i>.
	 * 
	 * @return the numIndexes
	 */
	protected int getNumIndexes() {
		return numIndexes;
	}

	/**
	 * Set the number of indexes, for indicators that do not set it based on the input sources.
	 * 
	 * @param numIndexes The number of indexes.
	 */
	protected void setNumIndexes(int numIndexes) {
		this.numIndexes = numIndexes;
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	public abstract void start(List<IndicatorSource> indicatorSources);

	/**
	 * Calculates the indicator data at the given index, for the list of indicator sources.
	 * <p>
	 * This indicator already calculated data is passed as a parameter because some indicators may need previous
	 * calculated values or use them to improve calculation performance.
	 * 
	 * @param index The data index.
	 * @param indicatorSources The list of indicator sources.
	 * @param indicatorData This indicator already calculated data.
	 * @return The result data.
	 */
	public abstract Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData);

	/**
	 * Check if this indicator is equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Indicator) {
			Indicator indicator = (Indicator) obj;
			if (!getIndicatorInfo().equals(indicator.getIndicatorInfo())) {
				return false;
			}
			if (numIndexes != indicator.getNumIndexes()) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns a string representation.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		return getIndicatorInfo().toString();
	}

}
