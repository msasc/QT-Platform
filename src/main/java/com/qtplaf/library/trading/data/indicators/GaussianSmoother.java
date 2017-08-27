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
package com.qtplaf.library.trading.data.indicators;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.info.IndicatorInfo;

/**
 * Gaussian smoother indicator.
 * 
 * @author Miquel Sas
 */
public class GaussianSmoother extends PeriodIndicator {

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public GaussianSmoother(Session session) {
		super(session);

		// Indicator info to be configured.
		IndicatorInfo info = getIndicatorInfo();

		// Name and title.
		info.setName("GAUSSMOOTH");
		info.setTitle("Guassian curve smoother");

		// Instrument, period and scales will be setup at start using those of the unique <i>DataInfo</i> used.

		// Setup input information. Uses an unique input source, with one output value, of any data type.
		info.addInput(getDefaultInputInfo());

		// Setup the input parameter and default value: period.
		info.addParameter(getPeriodParameter());
	}

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
	@Override
	public Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData) {

		// If index < 0 do nothing.
		if (index < 0) {
			return null;
		}

		// The unique data list and the index of the data.
		int period = getIndicatorInfo().getParameter("PERIOD").getValue().getInteger();

		// If index < period, calculate the mean from scratch.
		if (index < period) {
			return getSource(index, indicatorSources);
		}

		// The Gaussian curve fitter and the Gaussian function.
		GaussianCurveFitter fitter = GaussianCurveFitter.create();
		Gaussian.Parametric function = new Gaussian.Parametric();

		// For every indicator source, build the list of observation points.
		int numIndexes = getNumIndexes();
		double[] values = new double[numIndexes];
		Arrays.fill(values, 0);
		int valueIndex = 0;
		for (IndicatorSource source : indicatorSources) {
			DataList dataList = source.getDataList();
			List<Integer> indexes = source.getIndexes();
			for (Integer dataIndex : indexes) {

				// The list of observations.
				WeightedObservedPoints obs = new WeightedObservedPoints();
				int startIndex = index - period + 1;
				int endIndex = index;
				int x = 0;
				for (int i = startIndex; i <= endIndex; i++) {
					double y = dataList.get(i).getValue(dataIndex);
					obs.add(x, y);
					x++;
				}

				// Reduce last x to get the last coordinate applied.
				x--;

				// The parameters to apply to the function.
				double[] params = fitter.fit(obs.toList());

				// The value.
				values[valueIndex] = function.value(x, params);
				valueIndex++;
			}
		}

		Data data = new Data();
		data.setData(values);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}
}
