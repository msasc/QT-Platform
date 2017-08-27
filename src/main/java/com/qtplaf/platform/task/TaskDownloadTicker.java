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

package com.qtplaf.platform.task;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.Filter;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.DataIterator;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.ServerConnector;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.tables.TableDataPrice;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Task to download a ticker from a server, starting at the last data downloaded, up to the last data available in the
 * server.
 * 
 * @author Miquel Sas
 */
public class TaskDownloadTicker extends TaskRunner {

	/** The server to download the ticker from. */
	private Server server;
	/** The instrument to download. */
	private Instrument instrument;
	/** The period. */
	private Period period;
	/** The offer side. */
	private OfferSide offerSide;
	/** The data filter. */
	private Filter filter;

	/** The time to download from. */
	private Long timeFrom = null;
	/** The time to download to. */
	private Long timeTo = null;

	/** The persistor. */
	private DataPersistor persistor;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server to download the ticker from.
	 * @param instrument The instrument to download.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param filter The data filter.
	 */
	public TaskDownloadTicker(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		OfferSide offerSide,
		Filter filter) {
		super(session);
		this.server = server;
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
		this.filter = filter;
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * This task does not support count steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {
		notifyCounting();
		notifyStepCount(getStepCount());
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
		if (countSteps() <= 0) {
			notifyCancelled();
			return;
		}

		// Delete data from time from on in the table.
		deleteFromTimeFrom();

		// Iterate receiving data (bars).
		DataIterator iter =
			getServer().getHistoryManager().getDataIterator(
				instrument,
				period,
				offerSide,
				filter,
				getTimeFrom(),
				getTimeTo());
		
		while (true) {

			// Check request of cancel.
			if (checkCancel()) {
				break;
			}

			// Check pause resume.
			if (checkPause()) {
				continue;
			}

			// Check next record to exit loop.
			if (!iter.hasNext()) {
				break;
			}

			// Next bar.
			Data data = iter.next();

			// Get the step from the bar time and notify.
			long time = data.getTime();
			long step = getStepCurrent(time);
			long steps = getSteps();
			notifyStepStart(step, getStepMessage(step, steps));

			// Get the data record.
			Record record = RecordUtils.getRecordDataPrice(getPersistor().getDefaultRecord(), data);

			// Insert the record.
			getPersistor().insert(record);

			// Notify step end.
			notifyStepEnd();
		}

	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests. This task supports cancel.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task supports counting steps.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This task is not indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. This task supports pause.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}

	/**
	 * Returns the table to fill with downloaded data.
	 * 
	 * @return The table.
	 */
	private Table getTable() {
		String tableName = Tables.ticker(instrument, period);
		return new TableDataPrice(getSession(), server, instrument, tableName);
	}

	/**
	 * Returns the persistor to use.
	 * 
	 * @return The persistor.
	 */
	private DataPersistor getPersistor() {
		if (persistor == null) {
			persistor = new DataPersistor(getTable().getPersistor());
		}
		return persistor;
	}

	/**
	 * Returns the server conveniently connected.
	 * 
	 * @return The server.
	 * @throws Exception If such an error occurs.
	 */
	private Server getServer() throws Exception {
		if (!ServerConnector.isConnected(server)) {
			ServerConnector.connect(server);
		}
		return server;
	}

	/**
	 * Returns the time to download to. This will be the time of the last bar returned by the server.
	 * 
	 * @return The time to download to.
	 * @throws Exception If such an error occurs.
	 */
	private long getTimeTo() throws Exception {
		if (timeTo == null) {
			Data data = getServer().getHistoryManager().getLastData(instrument, period);
			timeTo = data.getTime();
		}
		return timeTo;
	}

	/**
	 * Returns the time to download from. This will be the last downloaded time or the first time of data in the server.
	 * 
	 * @return The time to download from.
	 * @throws Exception If such an error occurs.
	 */
	private long getTimeFrom() throws Exception {
		if (timeFrom == null) {
			long time = getTimeOfLastDowloaded();
			if (time == -1) {
				time = getServer().getHistoryManager().getTimeOfFirstData(instrument, period);
			}
			timeFrom = time;
		}
		return timeFrom;
	}

	/**
	 * Returns the last time of downloaded price data.
	 * 
	 * @return The last time.
	 * @throws PersistorException If such an error occurs.
	 */
	private long getTimeOfLastDowloaded() throws PersistorException {
		Persistor persistor = getPersistor();
		Field fTIME = persistor.getField(Fields.TIME);
		Order order = new Order();
		order.add(fTIME, false);
		Record record = null;
		RecordIterator iter = persistor.iterator(null, order);
		if (iter.hasNext()) {
			record = iter.next();
		}
		iter.close();
		if (record != null) {
			return record.getValue(Fields.TIME).getLong();
		}
		return -1;
	}

	/**
	 * Returns the current step given the time of an data bar.
	 * 
	 * @param time The current time.
	 * @return The corresponding step.
	 * @throws Exception If such an error occurs.
	 */
	private long getStepCurrent(long time) throws Exception {
		return time - getTimeFrom();
	}

	/**
	 * Returns the number of steps and counts it if necessary.
	 * 
	 * @return The number of steps.
	 * @throws Exception If such an error occurs.
	 */
	private long getStepCount() throws Exception {
		return getTimeTo() - getTimeFrom();
	}

	/**
	 * Delete the download table from time from on. This should delete only the last downloaded bar.
	 * 
	 * @throws Exception If such an error occurs.
	 */
	private void deleteFromTimeFrom() throws Exception {
		Persistor persistor = getPersistor();
		Field fTIME = persistor.getField(Fields.TIME);
		Value vTIME = new Value(getTimeFrom());
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldGT(fTIME, vTIME));
		persistor.delete(criteria);
	}
}
