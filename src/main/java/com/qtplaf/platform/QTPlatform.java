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

package com.qtplaf.platform;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.DataSourceInfo;
import com.qtplaf.library.database.rdbms.adapters.PostgreSQLAdapter;
import com.qtplaf.library.swing.FrameMenu;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.core.JPanelTreeMenu;
import com.qtplaf.library.swing.core.TreeMenuItem;
import com.qtplaf.library.trading.data.Filter;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.trading.server.ServerFactory;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileUtils;
import com.qtplaf.platform.action.ActionAvailableInstruments;
import com.qtplaf.platform.action.ActionStatistics;
import com.qtplaf.platform.action.ActionSynchronizeServerInstruments;
import com.qtplaf.platform.action.ActionTickers;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.tables.TableDataFilters;
import com.qtplaf.platform.database.tables.TableInstruments;
import com.qtplaf.platform.database.tables.TableOfferSides;
import com.qtplaf.platform.database.tables.TablePeriods;
import com.qtplaf.platform.database.tables.TableServers;
import com.qtplaf.platform.database.tables.TableStatistics;
import com.qtplaf.platform.database.tables.TableTickers;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Main entry of the QT-Platform.
 * 
 * @author Miquel Sas
 */
public class QTPlatform {

	/** Logger configuration. */
	static {
		System.setProperty("log4j.configurationFile", "resources/LoggerQTPlatform.xml");
	}
	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Pre-exit action, disconnect any connected servers.
	 */
	static class PreExitAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				List<Server> servers = ServerFactory.getSupportedServers();
				for (Server server : servers) {
					if (server.getConnectionManager().isConnected()) {
						server.getConnectionManager().disconnect();
					}
				}
			} catch (Exception exc) {
				LOGGER.catching(exc);
			}
		}

	}

	/**
	 * main entry.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		// Text resources and session.
		TextServer.addBaseResource("resources/StringsLibrary.xml");
		TextServer.addBaseResource("resources/StringsQTPlatform.xml");
		Session session = new Session(Locale.UK);

		// Frame menu.
		FrameMenu frameMenu = new FrameMenu(session);
		frameMenu.setTitle(session.getString("qtMenuTitle"));
		frameMenu.setLocation(20, 20);
		frameMenu.setSize(0.4, 0.8);
		frameMenu.setPreExitAction(new PreExitAction());

		// Re-direct out and err.
		// System.setOut(frameMenu.getConsole().getPrintStream());
		// System.setErr(frameMenu.getConsole().getPrintStream());

		// RunTickers the menu.
		frameMenu.setVisible(true);

		// Command line argument: database connection (xml file name).
		Argument argConnection = new Argument("dataSourceFile", "Database connection file", true, true, false);
		ArgumentManager argMngr = new ArgumentManager(argConnection);
		if (!argMngr.parse(args)) {
			for (String error : argMngr.getErrors()) {
				MessageBox.error(session, error);
			}
			System.exit(1);
		}

		try {

			// Ensure database.
			LOGGER.info("Database checking...");
			configureDatabase(session, argMngr.getValue("dataSourceFile"));
			LOGGER.info("Database checked");

			// Configure the menu.
			LOGGER.info("Configuring menu...");
			configureMenu(frameMenu.getPanelTreeMenu());

			// RunTickers the menu.
			frameMenu.showTreeMenu();

		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
	}

	/**
	 * Ensure the database connection and required schemas.
	 * <ul>
	 * <li>QT-Platform system schema <tt>QTP</tt></li>
	 * <li>One schema for each supported server, for instance <tt>QTP_DKCP</tt></li>
	 * </ul>
	 *
	 * @param session The working session.
	 * @param connectionFile The connection file name.
	 * @throws Exception If any error occurs.
	 */
	private static void configureDatabase(Session session, String connectionFile) throws Exception {

		// Connection file.
		File cnFile = FileUtils.getFileFromClassPathEntries(connectionFile);

		// Data source info and db engine.
		DataSourceInfo info = DataSourceInfo.getDataSourceInfo(cnFile);
		DBEngineAdapter adapter = new PostgreSQLAdapter();
		DBEngine dbEngine = new DBEngine(adapter, info);
		PersistorUtils.setDBEngine(dbEngine);

		// Persistor DDL.
		PersistorDDL ddl = PersistorUtils.getDDL();

		// Check for the system schema.
		if (!ddl.existsSchema(Schemas.qtp)) {
			ddl.createSchema(Schemas.qtp);
		}

		// Check for supported servers schemas.
		List<Server> servers = ServerFactory.getSupportedServers();
		for (Server server : servers) {
			String schema = Schemas.server(server);
			if (!ddl.existsSchema(schema)) {
				ddl.createSchema(schema);
			}
		}

		// Check for the necessary table KeyServer in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.Servers)) {
			ddl.buildTable(new TableServers(session));
		}
		synchronizeSupportedServer(session);

		// Check for the necessary table Periods in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.Periods)) {
			ddl.buildTable(new TablePeriods(session));
		}
		synchronizeStandardPeriods(session);

		// Check for the necessary table OfferSides in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.OfferSides)) {
			ddl.buildTable(new TableOfferSides(session));
		}
		synchronizeStandardOfferSides(session, dbEngine);

		// Check for the necessary table DataFilters in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.DataFilters)) {
			ddl.buildTable(new TableDataFilters(session));
		}
		synchronizeStandardDataFilters(session);

		// Check for the necessary table Instruments in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.Instruments)) {
			ddl.buildTable(new TableInstruments(session));
		}

		// Check for the necessary table Tickers in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.Tickers)) {
			ddl.buildTable(new TableTickers(session));
		}

		// Check for the necessary table Statistics in the system schema.
		if (!ddl.existsTable(Schemas.qtp, Tables.Statistics)) {
			ddl.buildTable(new TableStatistics(session));
		}
	}

	/**
	 * Synchronize standard periods.
	 * 
	 * @param session The working session.
	 * @throws Exception If any error occurs.
	 */
	private static void synchronizeStandardPeriods(Session session) throws Exception {
		List<Period> periods = Period.getStandardPeriods();
		Persistor persistor = PersistorUtils.getPersistorPeriods(session);
		for (Period period : periods) {
			Record record = RecordUtils.getRecordPeriod(persistor.getDefaultRecord(), period);
			if (!persistor.exists(record)) {
				persistor.insert(record);
			}
		}
	}

	/**
	 * Synchronize standard offer sides.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @throws Exception If any error occurs.
	 */
	private static void synchronizeStandardOfferSides(Session session, DBEngine dbEngine) throws Exception {
		OfferSide[] offerSides = OfferSide.values();
		Table table = new TableOfferSides(session);
		for (OfferSide offerSide : offerSides) {
			Record record = RecordUtils.getRecordOfferSide(table.getDefaultRecord(), offerSide);
			if (!dbEngine.existsRecord(table, record)) {
				dbEngine.executeInsert(table, record);
			}
		}
	}

	/**
	 * Synchronize standard data filters.
	 * 
	 * @param session The working session.
	 * @throws Exception If any error occurs.
	 */
	private static void synchronizeStandardDataFilters(Session session) throws Exception {
		Filter[] dataFilters = Filter.values();
		Persistor persistor = PersistorUtils.getPersistorDataFilters(session);
		for (Filter dataFilter : dataFilters) {
			Record record = RecordUtils.getRecordDataFilter(persistor.getDefaultRecord(), dataFilter);
			if (!persistor.exists(record)) {
				persistor.insert(record);
			}
		}
	}

	/**
	 * Synchronize supported servers.
	 * 
	 * @param session The working session.
	 * @throws Exception If any error occurs.
	 */
	private static void synchronizeSupportedServer(Session session) throws Exception {
		List<Server> servers = ServerFactory.getSupportedServers();
		Persistor persistor = PersistorUtils.getPersistorServers(session);
		RecordSet recordSet = persistor.select(null);

		// Remove not supported servers.
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			boolean remove = true;
			for (Server server : servers) {
				if (server
					.getId()
					.toLowerCase()
					.equals(record.getValue(Fields.SERVER_ID).toString().toLowerCase())) {
					remove = false;
					break;
				}
			}
			if (remove) {
				persistor.delete(record);
			}
		}

		// Add add non-existing supported servers.
		for (Server server : servers) {
			String id = server.getId().toLowerCase();
			boolean included = false;
			for (int i = 0; i < recordSet.size(); i++) {
				Record record = recordSet.get(i);
				if (record.getValue(Fields.SERVER_ID).toString().toLowerCase().equals(id)) {
					included = true;
					break;
				}
			}
			if (!included) {
				persistor.insert(RecordUtils.getRecordServer(persistor.getDefaultRecord(), server));
			}
		}
	}

	/**
	 * Configure the menu.
	 * 
	 * @param menu The menu.
	 * @throws Exception If any error occurs.
	 */
	private static void configureMenu(JPanelTreeMenu menu) throws Exception {

		Session session = menu.getSession();

		// Broker servers.
		TreeMenuItem itemServers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServers"));
		menu.addMenuItem(itemServers);

		// One menu item for each supported server.
		List<Server> servers = ServerFactory.getSupportedServers();
		for (Server server : servers) {
			String name = server.getName();
			String title = server.getTitle();
			String id = server.getId();

			// KeyServer options.
			TreeMenuItem itemServer = TreeMenuItem.getMenuItem(session, name, title, id);
			itemServer.setLaunchArg(LaunchArgs.SERVER, server);
			menu.addMenuItem(itemServers, itemServer);

			// Synchronize available instruments
			TreeMenuItem itemSrvSyncInst =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersSynchronizeInstruments"));
			itemSrvSyncInst.setActionClass(ActionSynchronizeServerInstruments.class);
			itemSrvSyncInst.setLaunchArg(LaunchArgs.SERVER, server);
			menu.addMenuItem(itemServer, itemSrvSyncInst);

			// Available instruments
			TreeMenuItem itemSrvAvInst = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersAvInst"));
			itemSrvAvInst.setActionClass(ActionAvailableInstruments.class);
			itemSrvAvInst.setLaunchArg(LaunchArgs.SERVER, server);
			menu.addMenuItem(itemServer, itemSrvAvInst);

			// Tickers
			TreeMenuItem itemSrvTickers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickers"));
			menu.addMenuItem(itemServer, itemSrvTickers);

			// Tickers define and download.
			TreeMenuItem itemSrvTickersDef =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickersDefine"));
			itemSrvTickersDef.setActionClass(ActionTickers.class);
			itemSrvTickersDef.setLaunchArg(LaunchArgs.SERVER, server);
			menu.addMenuItem(itemSrvTickers, itemSrvTickersDef);

			// Tickers statistics.
			TreeMenuItem itemSrvTickersStats =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickersStatistics"));
			itemSrvTickersStats.setLaunchArg(LaunchArgs.SERVER, server);
			itemSrvTickersStats.setActionClass(ActionStatistics.class);
			menu.addMenuItem(itemSrvTickers, itemSrvTickersStats);

		}

		menu.refreshTree();
	}

}
