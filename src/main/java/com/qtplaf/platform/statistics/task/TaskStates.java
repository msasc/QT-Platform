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

package com.qtplaf.platform.statistics.task;

import java.util.List;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.indicators.StatesIndicator;
import com.qtplaf.platform.statistics.Average;
import com.qtplaf.platform.statistics.States;

/**
 * Calculates source states values.
 *
 * @author Miquel Sas
 */
public class TaskStates extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** States indicator. */
	private StatesIndicator indicator;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics.
	 */
	public TaskStates(States states) {
		super(states.getSession());
		this.states = states;
		this.indicator = new StatesIndicator(states);

		setNameAndDescription(states, "states", "States raw values");
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * This task supports counting steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {

		// Notify counting.
		notifyCounting();

		// The source price data list.
		PersistorDataList price = indicator.getDataListPrice();
		price.setCacheSize(10000);

		// Number of steps.
		int count = price.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Count steps.
		countSteps();

		// Result table and persistor.
		Table table = states.getTableStates();
		DataPersistor persistor = new DataPersistor(table.getPersistor());

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// And the result indicator info and data list.
		IndicatorInfo info = indicator.getIndicatorInfo();
		IndicatorDataList indicatorList = indicator.getDataList();
		// The list of indicator data lists that must be calculated prior as sources.
		List<IndicatorDataList> sources = indicator.getIndicatorDataListsToCalculate();

		// Averages.
		List<Average> averages = states.getAverages();

		// The current index to calculate.
		int index = 0;
		
		// Step and steps.
		long step = 0;
		long steps = getSteps();
		while (step < steps) {

			// Check request of cancel.
			if (checkCancel()) {
				break;
			}

			// Check pause resume.
			if (checkPause()) {
				continue;
			}

			// Increase step.
			step++;
			// Notify step start.
			notifyStepStart(step, getStepMessage(step, steps, null, null));

			// Calculate required sources for the current index.
			for (IndicatorDataList source : sources) {
				source.calculate(index);
			}
			// Calculate the result indicator and save the data.
			Data data = indicatorList.calculate(index);

			// Indicator data contains open, high, low, close and the averages. Raw spreads and slopes will be
			// calculated here.
			Record record = persistor.getDefaultRecord();

			// Time.
			record.getValue(Fields.TIME).setLong(data.getTime());

			// Open, high, low, close.
			double open = data.getValue(info.getOutputIndex(Fields.OPEN));
			double high = data.getValue(info.getOutputIndex(Fields.HIGH));
			double low = data.getValue(info.getOutputIndex(Fields.LOW));
			double close = data.getValue(info.getOutputIndex(Fields.CLOSE));
			record.getValue(Fields.OPEN).setDouble(open);
			record.getValue(Fields.HIGH).setDouble(high);
			record.getValue(Fields.LOW).setDouble(low);
			record.getValue(Fields.CLOSE).setDouble(close);

			// Averages.
			for (int i = 0; i < averages.size(); i++) {
				Average average = averages.get(i);
				String name = Fields.averageName(average);
				record.getValue(name).setDouble(data.getValue(info.getOutputIndex(name)));
			}
			
			// Range
			double range = high - low;
			if (index == 0) {
				range = 0;
			}
			String range_raw = Fields.suffix(Fields.RANGE, Fields.Suffix.RAW);
			record.getValue(range_raw).setDouble(range);

			// Raw spread WCP fast average.
			double wcp = (high + low + (2 * close)) / 4;
			Average fastAvg = states.getAverages().get(0);
			String spread_wcp_fast = Fields.spread(Fields.WCP, fastAvg, Fields.Suffix.RAW);
			String fast_avg_name = Fields.averageName(fastAvg);
			double fastAvgValue = record.getValue(fast_avg_name).getDouble();
			double spreadWCPFast = (wcp / fastAvgValue) - 1;
			record.getValue(spread_wcp_fast).setDouble(spreadWCPFast);

			// Raw spreads between averages.
			for (int i = 0; i < averages.size(); i++) {
				Average avgFast = averages.get(i);
				String avgFastName = Fields.averageName(avgFast);
				for (int j = i + 1; j < averages.size(); j++) {
					Average avgSlow = averages.get(j);
					String avgSlowName = Fields.averageName(avgSlow);
					String spreadName = Fields.spreadName(avgFast, avgSlow, Fields.Suffix.RAW);
					double valueFast = data.getValue(info.getOutputIndex(avgFastName));
					double valueSlow = data.getValue(info.getOutputIndex(avgSlowName));
					double valueSpread = (valueFast / valueSlow) - 1;
					record.getValue(spreadName).setDouble(valueSpread);
				}
			}

			// Raw slopes of averages.
			if (index > 0) {
				Data prev = indicatorList.get(index - 1);
				for (int i = 0; i < averages.size(); i++) {
					Average average = averages.get(i);
					String avgName = Fields.averageName(average);
					String slopeName = Fields.slopeName(average, Fields.Suffix.RAW);
					double valueCurr = data.getValue(info.getOutputIndex(avgName));
					double valuePrev = prev.getValue(info.getOutputIndex(avgName));
					double valueSlope = (valueCurr / valuePrev) - 1;
					record.getValue(slopeName).setDouble(valueSlope);
				}
			}

			// Raw differences between current and previous centers.
			if (index > 0) {
				String center_diff_raw = Fields.suffix(Fields.CENTER_DIFF, Fields.Suffix.RAW);
				Record recordPrev = persistor.getRecord(Long.valueOf(index - 1));
				double highPrev = recordPrev.getValue(Fields.HIGH).getDouble();
				double lowPrev = recordPrev.getValue(Fields.LOW).getDouble();
				double centerPrev = (highPrev + lowPrev) / 2;
				double centerCurr = (high + low) / 2;
				double centerDiff = (centerCurr / centerPrev) - 1;
				record.getValue(center_diff_raw).setDouble(centerDiff);
			}

			// Insert.
			persistor.insert(record);

			// Skip to next index.
			index++;

			// Notify step end.
			notifyStepEnd();
		}

	}

}
