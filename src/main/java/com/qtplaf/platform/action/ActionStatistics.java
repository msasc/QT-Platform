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

package com.qtplaf.platform.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JPopupMenuConfigurator;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Lookup;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.statistics.TickerStatistics;
import com.qtplaf.platform.util.FormUtils;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.PeriodUtils;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordSetUtils;

/**
 * Statistics definitions.
 *
 * @author Miquel Sas
 */
public class ActionStatistics extends AbstractAction {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Popup configurator.
	 */
	class PopupConfigurator implements JPopupMenuConfigurator {
		private JTableRecord tableRecord;

		/**
		 * Constructor.
		 * 
		 * @param tableRecord The table record.
		 */
		public PopupConfigurator(JTableRecord tableRecord) {
			this.tableRecord = tableRecord;
		}

		/**
		 * Configura the popup menu.
		 */
		@Override
		public void configure(JPopupMenu popupMenu) {
			Record record = tableRecord.getSelectedRecord();
			if (record == null) {
				return;
			}
			TickerStatistics statistics = getStatistics(record);
			List<Action> actions = statistics.getActions();
			if (actions != null && !actions.isEmpty()) {
				SwingUtils.addMenuItems(popupMenu, actions);
			}
		}
	}

	/**
	 * Action to close the frame.
	 */
	class ActionClose extends AbstractAction {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionClose(Session session) {
			super();
			ActionUtils.configureClose(session, this);
			ActionUtils.setDefaultCloseAction(this, true);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionFrame frame = (JOptionFrame) ActionUtils.getUserObject(this);
			frame.setVisible(false);
			frame.dispose();

		}
	}

	/**
	 * Action to create a new ticker.
	 */
	class ActionCreate extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionCreate(Session session) {
			super();
			ActionUtils.configureCreate(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record rcTicker = Lookup.selectTicker(session, server);
				if (rcTicker == null) {
					return;
				}

				Instrument instrument = InstrumentUtils.getInstrumentFromRecordTickers(session, rcTicker);
				Period period = PeriodUtils.getPeriodFromRecordTickers(rcTicker);
				Record rcStats = FormUtils.getStatistics(session, server, instrument, period);
				if (rcStats == null) {
					return;
				}
				String statsId = rcStats.getValue(Fields.STATISTICS_ID).getString();
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);

				// Create the statistics record.
				Persistor persistor = PersistorUtils.getPersistorStatistics(session);
				persistor.insert(rcStats);
				getTableModel().insertRecord(rcStats, persistor.getView().getOrderBy());
				getTableRecord().setSelectedRecord(rcStats);
				
				// Create the detail of table records
				List<Table> tables = statistics.getTables();
				for (Table table : tables) {
					persistor.getDDL().buildTable(table);
				}

			} catch (Exception exc) {
				LOGGER.catching(exc);
			}
		}
	}

	/**
	 * Action to delete a ticker (and its data).
	 */
	class ActionDelete extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionDelete(Session session) {
			super();
			ActionUtils.configureDelete(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}

				// Ask delete.
				String question = session.getString("qtAskDeleteStatistic");
				if (MessageBox.question(session, question, MessageBox.YES_NO) != MessageBox.YES) {
					return;
				}
				int row = getTableRecord().getSelectedRow();

				String serverId = server.getId();
				String instrId = record.getValue(Fields.INSTRUMENT_ID).getString();
				String periodId = record.getValue(Fields.PERIOD_ID).getString();
				String statsId = record.getValue(Fields.STATISTICS_ID).getString();
				
				Instrument instrument = InstrumentUtils.getInstrument(session, serverId, instrId);
				Period period = Period.parseId(periodId);
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
				
				List<Table> tables = statistics.getTables();
				for (Table table : tables) {
					PersistorUtils.getDDL().dropTable(table);
				}
				
				PersistorUtils.getPersistorStatistics(session).delete(record);
				getTableModel().deleteRecord(record);
				getTableRecord().setSelectedRow(row);

			} catch (Exception exc) {
				LOGGER.catching(exc);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public ActionStatistics() {
		super();
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return ActionUtils.getSession(this);
	}

	/**
	 * Returns the server.
	 * 
	 * @return The server.
	 */
	public Server getServer() {
		return LaunchArgs.getServer(this);
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {

			Persistor persistor = PersistorUtils.getPersistorStatistics(getSession());
			Record masterRecord = persistor.getDefaultRecord();

			JTableRecord tableRecord = new JTableRecord(getSession(), ListSelectionModel.SINGLE_SELECTION);
			JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
			TableModelRecord tableModelRecord = new TableModelRecord(getSession(), masterRecord);
			tableModelRecord.addColumn(Fields.INSTRUMENT_ID);
			tableModelRecord.addColumn(Fields.PERIOD_NAME);
			tableModelRecord.addColumn(Fields.STATISTICS_ID);

			tableModelRecord.setRecordSet(RecordSetUtils.getRecordSetStatistics(getSession(), getServer()));
			tableRecord.setModel(tableModelRecord);

			JOptionFrame frame = new JOptionFrame(getSession());
			frame.setTitle(
				getServer().getName() + " " + getSession().getString("qtMenuServersTickersStatistics").toLowerCase());
			frame.setComponent(panelTableRecord);

			ActionCreate actionCreate = new ActionCreate(getSession());
			ActionUtils.setSortIndex(actionCreate, 0);
			frame.addAction(actionCreate);

			ActionDelete actionDelete = new ActionDelete(getSession());
			ActionUtils.setSortIndex(actionDelete, 1);
			frame.addAction(actionDelete);

			frame.addAction(new ActionClose(getSession()));
			
			frame.setPopupConfigurator(new PopupConfigurator(tableRecord));
			
			frame.setSize(0.6, 0.8);
			frame.showFrame();

		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
	}

	/**
	 * Returns the instrument given the selected record.
	 * 
	 * @param record The statistics selected record.
	 * @return The instrument.
	 */
	public Instrument getInstrument(Record record) {
		String instrId = record.getValue(Fields.INSTRUMENT_ID).getString();
		Instrument instrument = InstrumentUtils.getInstrument(getSession(), getServer().getId(), instrId);
		return instrument;
	}

	/**
	 * Returns the period given the selected record.
	 * 
	 * @param record The statistics selected record.
	 * @return The period.
	 */
	public Period getPeriod(Record record) {
		String periodId = record.getValue(Fields.PERIOD_ID).getString();
		Period period = Period.parseId(periodId);
		return period;
	}

	/**
	 * Returns the statistics of the record.
	 * 
	 * @param record The selected record.
	 * @return The statistics.
	 */
	public TickerStatistics getStatistics(Record record) {
		Instrument instrument = getInstrument(record);
		Period period = getPeriod(record);
		String statsId = record.getValue(Fields.STATISTICS_ID).getString();
		Manager manager = new Manager(getSession());
		TickerStatistics statistics = manager.getStatistics(getServer(), instrument, period, statsId);
		return statistics;
	}

}
