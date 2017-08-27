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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.States;

/**
 *
 *
 * @author Miquel Sas
 */
public class TaskNormalize extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** States persistor. */
	private DataPersistor persistor;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics.
	 */
	public TaskNormalize(States states) {
		super(states.getSession());
		this.states = states;
		this.persistor = new DataPersistor(states.getTableStates().getPersistor());

		setNameAndDescription(states, "normalize", "Min-Max ranges of raw values");
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
		int count = Long.valueOf(persistor.size()).intValue();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returns the normalizers map.
	 * 
	 * @return The normalizers map.
	 */
	private Map<String, StdNormalizer> getNormalizersMap() {
		Map<String, StdNormalizer> map = new HashMap<>();

		RecordSet recordSet = states.getRecordSetRanges(false);
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			String name = record.getValue(Fields.NAME).getString();
			String min_max = record.getValue(Fields.MIN_MAX).getString();
			double average = record.getValue(Fields.AVERAGE).getDouble();
			double stddev = record.getValue(Fields.STDDEV).getDouble();

			StdNormalizer normalizer = map.get(name);
			if (normalizer == null) {
				normalizer = new StdNormalizer();
				normalizer.setNormalizedHigh(1);
				normalizer.setNormalizedLow(-1);
				normalizer.setDataHigh(0);
				normalizer.setDataLow(0);
				map.put(name, normalizer);
			}
			if (min_max.equals("min")) {
				double dataLow = average - (3.0 * stddev);
				normalizer.setDataLow(dataLow);
			} else {
				double dataHigh = average + (3.0 * stddev);
				normalizer.setDataHigh(dataHigh);
			}
		}

		return map;
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

			// Normalizers map.
			Map<String, StdNormalizer> map = getNormalizersMap();

			// Count steps.
			countSteps();

			// Field names raw and nrm.
			List<String> fieldNamesRaw = states.getFieldsToNormalize(Fields.Suffix.RAW);
			List<String> fieldNamesNrm = states.getFieldsToNormalize(Fields.Suffix.NRM);

			// Source iterator.
			iterator = persistor.iterator(new Criteria(), getSelectOrder());

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

				// Normalize raw fields.
				for (int i = 0; i < fieldNamesRaw.size(); i++) {
					String nameRaw = fieldNamesRaw.get(i);
					String nameNrm = fieldNamesNrm.get(i);
					StdNormalizer normalizer = map.get(nameRaw);
					double valueRaw = record.getValue(nameRaw).getDouble();
					double valueNrm = normalizer.normalize(valueRaw);
					record.getValue(nameNrm).setDouble(valueNrm);
				}

				// Update the record.
				persistor.update(record);

				// Notify step end.
				notifyStepEnd();
			}

		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}

}
