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

import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.States;

/**
 * Calculate ranges (min-max) values.
 *
 * @author Miquel Sas
 */
public class TaskRanges extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** States data list. */
	private PersistorDataList statesList;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics.
	 */
	public TaskRanges(States states) {
		super(states.getSession());
		this.states = states;
		this.statesList = states.getDataListStates();
		this.statesList.setCacheSize(10000);

		setNameAndDescription(states, "ranges", "Min-Max ranges of raw values");
	}

	/**
	 * Count the steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {

		// Notify counting.
		notifyCounting();

		// Number of steps.
		int count = statesList.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returns the result record.
	 * 
	 * @param persistor The persistor.
	 * @param name The field name.
	 * @param period The period.
	 * @param minimum Minimum/maximum.
	 * @param value The value.
	 * @param index Source index.
	 * @param time Source time.
	 * @return The record.
	 */
	private Record getRecord(
		Persistor persistor,
		String name,
		int period,
		boolean minimum,
		double value,
		int index,
		long time) {
		Record record = persistor.getDefaultRecord();
		record.setValue(Fields.NAME, name);
		record.setValue(Fields.PERIOD, period);
		record.setValue(Fields.MIN_MAX, (minimum ? "min" : "max"));
		record.setValue(Fields.VALUE, value);
		record.setValue(Fields.INDEX, index);
		record.setValue(Fields.TIME, time);
		return record;
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
		Table table = states.getTableRanges();
		Persistor persistor = table.getPersistor();

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// Periods of ranges: two more slowest averages.
		int[] periods = new int[] {
			states.getAverages().get(states.getAverages().size() - 1).getPeriod(),
			states.getAverages().get(states.getAverages().size() - 2).getPeriod() };

		// Field names.
		List<String> fieldNames = states.getFieldsToNormalize(Fields.Suffix.RAW);
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

			// Do calculate if min-max for each name and period.
			for (String name : fieldNames) {
				int valueIndex = statesList.getDataInfo().getOutputIndex(name);
				double value = statesList.get(index).getValue(valueIndex);
				if (value == 0) {
					continue;
				}
				for (int period : periods) {
					if (value < 0) {
						if (statesList.isMinimum(index, valueIndex, period)) {
							long time = statesList.get(index).getTime();
							Record record = getRecord(persistor, name, period, true, value, index, time);
							persistor.insert(record);
						}
					}
					if (value > 0) {
						if (statesList.isMaximum(index, valueIndex, period)) {
							long time = statesList.get(index).getTime();
							Record record = getRecord(persistor, name, period, false, value, index, time);
							persistor.insert(record);
						}
					}
				}
			}

			// Skip to next index.
			index++;

			// Notify step end.
			notifyStepEnd();
		}
	}

}
