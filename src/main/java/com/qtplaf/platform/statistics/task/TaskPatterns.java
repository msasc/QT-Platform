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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.data.info.InputInfo;
import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.States;
import com.qtplaf.platform.statistics.patterns.PatternInput;
import com.qtplaf.platform.statistics.patterns.Patterns;

/**
 * Generate non already exiting patterns for the given pattern info, from the states table.
 *
 * @author Miquel Sas
 */
public class TaskPatterns extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** Pattern info. */
	private PatternInfo patternInfo;
	/** States persistor. */
	private DataPersistor statesPersistor;

	/**
	 * @param states Underlying states statistics.
	 * @param patternInfo Pattern info.
	 */
	public TaskPatterns(States states, PatternInfo patternInfo) {
		super(states.getSession());
		this.states = states;
		this.patternInfo = patternInfo;
		this.statesPersistor = new DataPersistor(states.getTableStates().getPersistor());

		Patterns.setDataInfo(patternInfo, states.getDataListStates().getDataInfo());

		setNameAndDescription(states, "patterns-" + patternInfo.getId(), patternInfo.getDescription());
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
		long count = statesPersistor.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returns the select order.
	 * 
	 * @return The select order.
	 */
	private Order getSelectOrder() {
		Order order = new Order();
		order.add(states.getTableStates().getField(Fields.INDEX));
		return order;
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Source iterator.
		RecordIterator iterator = null;

		try {

			// Source iterator.
			iterator = statesPersistor.iterator(new Criteria(), getSelectOrder());

			// Count steps.
			countSteps();

			// Result table and persistor.
			Table table = states.getTablePattern(patternInfo);
			Persistor persistor = table.getPersistor();

			// Create the table if not exists.
			if (!persistor.getDDL().existsTable(table)) {
				persistor.getDDL().buildTable(table);
			}

			// Look backward property.
			int lookBackward = Patterns.getLookBackward(patternInfo);

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

				// End achieved? should not happen.
				if (!iterator.hasNext()) {
					break;
				}
				Record record = iterator.next();
				int index = record.getValue(Fields.INDEX).getInteger();

				// Skip not enough look backward.
				if (index + 1 < lookBackward) {
					notifyStepEnd();
					continue;
				}

				// Data time
				Value time = record.getValue(Fields.TIME);

				// If the current record exists as a pattern, skip it.
				if (persistor.exists(time)) {
					notifyStepEnd();
					index++;
					continue;
				}

				// Get the list of datas involved in the pattern.
				List<Data> datas = getDatas(record, index, lookBackward);

				// Default pattern record.
				Record pattern = table.getDefaultRecord();
				pattern.setValue(Fields.INDEX, index);
				pattern.setValue(Fields.TIME, time);

				for (int i = 0; i < patternInfo.getInputCount(); i++) {
					InputInfo input = patternInfo.getInput(i);
					String alias = input.getId();
					PatternInput function = Patterns.getPatternInput(input);
					double inputValue = function.getInput(datas);
					pattern.setValue(alias, inputValue);
				}
				persistor.insert(pattern);

				// Notify step end.
				notifyStepEnd();
			}

		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}

	}

	/**
	 * Returns the list of datas involved in the pattern
	 * 
	 * @param record Current record.
	 * @param index The current index.
	 * @param lookBackward The number of records to look backward.
	 * @return The list of datas.
	 * @throws Exception If any error occurs.
	 */
	private List<Data> getDatas(Record record, int index, int lookBackward) throws Exception {
		List<Data> datas = new ArrayList<>();
		datas.add(statesPersistor.getData(record));

		// If look backward == 1, we are done.
		if (lookBackward == 1) {
			return datas;
		}

		// Retrieve previous look bacward records.
		int startIndex = index - lookBackward + 1;
		int endIndex = index - 1;
		Field fIndex = statesPersistor.getField(Fields.INDEX);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldGE(fIndex, new Value(startIndex)));
		criteria.add(Condition.fieldLE(fIndex, new Value(endIndex)));
		RecordSet rs = statesPersistor.select(criteria, getSelectOrder());
		for (int i = 0; i < rs.size(); i++) {
			record = rs.get(i);
			datas.add(statesPersistor.getData(record));
		}
		return datas;
	}
}
