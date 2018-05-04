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

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.action.ActionSelectColumns;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordSetUtils;

/**
 * Shows the list of available instruments for the server set as launch argument.
 * 
 * @author Miquel Sas
 */
public class ActionAvailableInstruments extends AbstractAction {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

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
	 * Runnable to launch it in a thread.
	 */
	class RunAction implements Runnable {
		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionAvailableInstruments.this);
				Server server = LaunchArgs.getServer(ActionAvailableInstruments.this);
				Persistor persistor = PersistorUtils.getPersistorInstruments(session);
				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.SINGLE_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(Fields.INSTRUMENT_ID);
				tableModelRecord.addColumn(Fields.INSTRUMENT_DESC);
				tableModelRecord.addColumn(Fields.INSTRUMENT_PIP_VALUE);
				tableModelRecord.addColumn(Fields.INSTRUMENT_PIP_SCALE);
				tableModelRecord.addColumn(Fields.INSTRUMENT_TICK_VALUE);
				tableModelRecord.addColumn(Fields.INSTRUMENT_TICK_SCALE);
				tableModelRecord.addColumn(Fields.INSTRUMENT_VOLUME_SCALE);
				tableModelRecord.addColumn(Fields.INSTRUMENT_PRIMARY_CURRENCY);
				tableModelRecord.addColumn(Fields.INSTRUMENT_SECONDARY_CURRENCY);
				tableModelRecord.setRecordSet(RecordSetUtils.getRecordSetAvailableInstruments(session, server));
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);
				frame.setTitle(server.getName() + " " + session.getString("qtMenuServersAvInst").toLowerCase());
				frame.setComponent(panelTableRecord);
				frame.addAction(new ActionClose(session));
				frame.addAction(new ActionSelectColumns(tableRecord));
				frame.setSize(0.6, 0.8);
				frame.showFrame();

			} catch (Exception exc) {
				LOGGER.catching(exc);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public ActionAvailableInstruments() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new RunAction()).start();
	}

}
