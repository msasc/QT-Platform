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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Indicator;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.library.trading.data.info.InputInfo;
import com.qtplaf.library.trading.data.info.ParameterInfo;
import com.qtplaf.library.trading.data.info.validators.IntegerValidator;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.math.Matrix;

/**
 * Base class for indicators that uses a period parameters
 * 
 * @author Miquel Sas
 */
public abstract class PeriodIndicator extends Indicator {

	/**
	 * Returns the EMA data calculated from the begining to the argument index, when index is less that period.
	 * 
	 * @param ma The MA indicator.
	 * @param index The index to calculate.
	 * @param indicatorSources The indicator sources.
	 * @param indicatorData The already indicator calculated data.
	 * @return The data element.
	 */
	public static Data getEMA(
		PeriodIndicator ma, 
		int index, 
		List<IndicatorSource> indicatorSources, 
		DataList indicatorData) {

		int numIndexes = ma.getNumIndexes();
		int period = ma.getIndicatorInfo().getParameter(PERIOD_NAME).getValue().getInteger();
		double alpha = Double.valueOf(2) / Double.valueOf(period + 1);
		
		// If index < period, calculate the mean from scratch.
		if (index < period) {
			return getSMA(ma, index, indicatorSources, indicatorData);
		}

		// Improved performance calculation retrieving from the last calculated average the first value of the series
		// (divided by the period) and adding the new value of the series (also divided bythe period).
		double[] averages = new double[numIndexes];
		Arrays.fill(averages, 0);
		int averageIndex = 0;
		for (IndicatorSource source : indicatorSources) {
			DataList dataList = source.getDataList();
			List<Integer> indexes = source.getIndexes();
			for (Integer dataIndex : indexes) {
				double lastAverage = 0;
				Data lastData = indicatorData.get(index - 1);
				if (lastData != null) {
					lastAverage = lastData.getValue(averageIndex);
				} else {
					lastAverage = dataList.get(index).getValue(dataIndex);
				}
				double nextValue = dataList.get(index).getValue(dataIndex);
				double average = nextValue * alpha + (1 - alpha) * lastAverage;
				averages[averageIndex] += average;
				averageIndex++;
			}
		}
		
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}

	/**
	 * Returns the SMA data calculated from the begining to the argument index, when index is less that period.
	 * 
	 * @param ma The MA indicator.
	 * @param index The index to calculate.
	 * @param indicatorSources The indicator sources.
	 * @param indicatorData The already indicator calculated data.
	 * @return The data element.
	 */
	public static Data getSMA(
		PeriodIndicator ma, 
		int index, 
		List<IndicatorSource> indicatorSources, 
		DataList indicatorData) {
		return getSMA(ma, index, indicatorSources, indicatorData, false);
	}

	/**
	 * Returns the SMA data calculated from the begining to the argument index, when index is less that period.
	 * 
	 * @param ma The MA indicator.
	 * @param index The index to calculate.
	 * @param indicatorSources The indicator sources.
	 * @param indicatorData The already indicator calculated data.
	 * @param optimize A boolean that indicates whether to optimize.
	 * @return The data element.
	 */
	public static Data getSMA(
		PeriodIndicator ma, 
		int index, 
		List<IndicatorSource> indicatorSources, 
		DataList indicatorData,
		boolean optimize) {
		
		// Num indexes and period.
		int numIndexes = ma.getNumIndexes();
		int period = ma.getIndicatorInfo().getParameter(PERIOD_NAME).getValue().getInteger();
		
		int start = 0;
		boolean canOptimize = (index > period);
		if (index >= period) {
			start = index - period + 1;
		}
		
		if (optimize && canOptimize) {
			if (indicatorData instanceof IndicatorDataList) {
				IndicatorDataList indicatorDataList = (IndicatorDataList) indicatorData;
				canOptimize = indicatorDataList.hasCalculated(index - 1);
				if (canOptimize) {
					int averageIndex;
					double divisor = period;
					
					double[] delAvgs = new double[numIndexes];
					Arrays.fill(delAvgs, 0);
					int deleteIndex = start - 1;
					averageIndex = 0;
					for (IndicatorSource source : indicatorSources) {
						DataList dataList = source.getDataList();
						List<Integer> indexes = source.getIndexes();
						for (Integer dataIndex : indexes) {
							delAvgs[averageIndex] = dataList.get(deleteIndex).getValue(dataIndex) / divisor;
							averageIndex++;
						}
					}
					
					double[] currAvgs = new double[numIndexes];
					Arrays.fill(currAvgs, 0);
					int currentIndex = index - 1;
					for (int i = 0; i < numIndexes; i++) {
						currAvgs[i] = indicatorDataList.get(currentIndex).getValue(i);
					}
					
					double[] addAvgs = new double[numIndexes];
					Arrays.fill(addAvgs, 0);
					averageIndex = 0;
					for (IndicatorSource source : indicatorSources) {
						DataList dataList = source.getDataList();
						List<Integer> indexes = source.getIndexes();
						for (Integer dataIndex : indexes) {
							addAvgs[averageIndex] = dataList.get(index).getValue(dataIndex) / divisor;
							averageIndex++;
						}
					}

					double[] averages = Matrix.add(addAvgs, Matrix.subtract(currAvgs, delAvgs));
					Data data = new Data();
					data.setData(averages);
					data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
					return data;				
				}
			}
		}
		
		double[] averages = new double[numIndexes];
		Arrays.fill(averages, 0);
		for (int i = start; i <= index; i++) {
			int averageIndex = 0;
			for (IndicatorSource source : indicatorSources) {
				DataList dataList = source.getDataList();
				List<Integer> indexes = source.getIndexes();
				for (Integer dataIndex : indexes) {
					averages[averageIndex] += dataList.get(i).getValue(dataIndex);
					averageIndex++;
				}
			}
		}
		double divisor = index + 1;
		if (index >= period) {
			divisor = period;
		}
		for (int i = 0; i < averages.length; i++) {
			averages[i] = averages[i] / divisor;
		}
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}

	/**
	 * Returns the WMA data calculated from the begining to the argument index, when index is less that period.
	 * 
	 * @param ma The MA indicator.
	 * @param index The index to calculate.
	 * @param indicatorSources The indicator sources.
	 * @param indicatorData The already indicator calculated data.
	 * @return The data element.
	 */
	public static Data getWMA(
		PeriodIndicator ma, 
		int index, 
		List<IndicatorSource> indicatorSources, 
		DataList indicatorData) {
		
		// Num indexes and period.
		int numIndexes = ma.getNumIndexes();
		int period = ma.getIndicatorInfo().getParameter(PERIOD_NAME).getValue().getInteger();

		// Applied period.
		period = Math.min(period, index + 1);
		int startIndex = index - period + 1;
		int endIndex = index;

		// Must be calculated for all the period each time.
		double[] averages = new double[numIndexes];
		double[] weights = new double[numIndexes];
		Arrays.fill(averages, 0);
		Arrays.fill(weights, 0);
		double weight = 1;
		for (int i = startIndex; i <= endIndex; i++) {
			int averageIndex = 0;
			for (IndicatorSource source : indicatorSources) {
				DataList dataList = source.getDataList();
				List<Integer> indexes = source.getIndexes();
				for (Integer dataIndex : indexes) {
					averages[averageIndex] += (dataList.get(i).getValue(dataIndex) * weight);
					weights[averageIndex] += weight;
					averageIndex++;
				}
			}
			weight += 1;
		}
		for (int i = 0; i < averages.length; i++) {
			averages[i] = averages[i] / weights[i];
		}
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}

	/**
	 * The name of the PERIOD parameter.
	 */
	public static final String PERIOD_NAME = "PERIOD";
	/**
	 * The index of the PERIOD parameter.
	 */
	public static final int PERIOD_INDEX = 0;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public PeriodIndicator(Session session) {
		super(session);
	}

	/**
	 * Returns a suitable period parameter.
	 * 
	 * @return A suitable period parameter.
	 */
	protected ParameterInfo getPeriodParameter() {
		Field period = new Field();
		period.setName(PERIOD_NAME);
		period.setAlias(PERIOD_NAME);
		period.setLabel("Period");
		period.setTitle("Average period");
		period.setType(Types.INTEGER);
		period.setValidator(new IntegerValidator("Period", 1, NumberUtils.MAX_INTEGER));
		Value value = period.getDefaultValue();
		value.setInteger(20);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(period);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a default input info for Price, Volume and Indicator.
	 * 
	 * @return A default input info for Price, Volume and Indicator.
	 */
	protected InputInfo getDefaultInputInfo() {
		InputInfo inputInfo = new InputInfo();
		inputInfo.addPossibleInputSource(DataType.Price, 1);
		inputInfo.addPossibleInputSource(DataType.Volume, 1);
		inputInfo.addPossibleInputSource(DataType.Indicator, 1);
		return inputInfo;
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	@Override
	public void start(List<IndicatorSource> indicatorSources) {

		// Calculate the number of indexes for later use.
		calculateNumIndexes(indicatorSources);

		// Fill aditional info
		IndicatorInfo info = getIndicatorInfo();

		// Instrument, period and scale from the first source.
		DataInfo input = indicatorSources.get(0).getDataList().getDataInfo();
		info.setInstrument(input.getInstrument());
		info.setPeriod(input.getPeriod());
		info.setPipScale(input.getPipScale());
		info.setTickScale(input.getTickScale());

		// Output infos
		int numIndexes = getNumIndexes();
		int period = info.getParameter(PERIOD_NAME).getValue().getInteger();
		String indicatorName = info.getName();
		for (int i = 0; i < numIndexes; i++) {
			StringBuilder b = new StringBuilder();
			b.append(indicatorName);
			if (numIndexes > 1) {
				b.append("-" + i);
			}
			b.append("(" + period + ")");
			info.addOutput(b.toString(), b.toString(), i);
		}

		// Set look backward to the indicator info.
		info.setLookBackward(period);
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
	public abstract Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData);

	/**
	 * Returns the source data.
	 * 
	 * @param index The index.
	 * @param indicatorSources The list of indicator sources.
	 * @return The source data element.
	 */
	protected Data getSource(int index, List<IndicatorSource> indicatorSources) {
		int numIndexes = getNumIndexes();
		double[] values = new double[numIndexes];
		Arrays.fill(values, 0);

		int valueIndex = 0;
		for (IndicatorSource source : indicatorSources) {
			DataList dataList = source.getDataList();
			List<Integer> indexes = source.getIndexes();
			for (Integer dataIndex : indexes) {
				values[valueIndex] += dataList.get(index).getValue(dataIndex);
				valueIndex++;
			}
		}
		Data data = new Data();
		data.setData(values);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}
}
