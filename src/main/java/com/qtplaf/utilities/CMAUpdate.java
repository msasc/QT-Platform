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

package com.qtplaf.utilities;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileCopy;
import com.qtplaf.library.util.list.ListUtils;

/**
 * This is an utility class to update a project (CMA), quality and production, that is external from QT-Platform.
 * 
 * @author Miquel Sas
 */
public class CMAUpdate {

	/** Logger configuration. */
	static {
		System.setProperty("log4j.configurationFile", "LoggerQTPlatform.xml");
	}
	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();
	
	enum Environment {
		Production,
		ProductionAD,
		Quality,
		Quality64,
		Incubator;
	}

	/**
	 * Action close.
	 */
	static class ActionClose extends AbstractAction {
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
			Session session = ActionUtils.getSession(this);
			String message = "Exit CMA Update?";
			if (MessageBox.question(session, message, MessageBox.YES_NO, MessageBox.NO) == MessageBox.YES) {
				System.exit(0);
			}
			throw new IllegalStateException();
		}

	}

	/**
	 * @param args Startup args.
	 */
	public static void main(String[] args) {

		// System text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);

		// Command line argument: environment
		// - quality
		// - production
		Argument argEnvironment =
			new Argument("environment", "Environment: quality/production/incubator", true, false, "quality", "quality64", "production", "production-ad", "incubator");

		// Command line argument: target
		// - local (local image)
		// - remote (remote Citrix image)
		Argument argTarget =
			new Argument("target", "Target: local/remote", true, false, "local", "remote");

		// Command line argument: modules (multiple values)
		// - central
		// - dictionary
		// - local
		Argument argModules =
			new Argument("modules", "Modules: central/dictionary/local", true, true, "central", "dictionary", "local");

		// Command line argument: purge (flag)
		Argument argPurge =
			new Argument("purge", "Purge destination directories", false, false, false);

		// Argument manager.
		ArgumentManager argMngr = new ArgumentManager(argEnvironment, argTarget, argModules, argPurge);
		if (!argMngr.parse(args)) {
			for (String error : argMngr.getErrors()) {
				System.out.println(error);
			}
			System.exit(1);
		}

		try {
			// The list of tasks.
			List<Task> tasks = getTaskList(session, argMngr);
	
			// Progress manager.
			ProgressManager progressManager = new ProgressManager(session);
			progressManager.setTitle("CMA Updater");
			progressManager.setPanelProgressWidth(1000);
			for (Task task : tasks) {
				progressManager.addTask(task);
			}
			progressManager.addPreCloseAction(new ActionClose(session));
			progressManager.showFrame();
		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
	}

	/**
	 * Returns the list of tasks to update.
	 * 
	 * @param session The working session.
	 * @param argMngr The argument manager.
	 * @return The list of tasks.
	 */
	private static List<Task> getTaskList(Session session, ArgumentManager argMngr) {
		// A task per central/dictionary/local.
		List<Task> tasks = new ArrayList<>();

		// Environment and target.
		boolean purge = argMngr.isPassed("purge");
		boolean local = argMngr.getValue("target").equals("local");
		boolean remote = !local;
		Environment env = null;
		if (argMngr.getValue("environment").equals("production")) {
			env = Environment.Production;
		}
		if (argMngr.getValue("environment").equals("production-ad")) {
			env = Environment.ProductionAD;
		}
		if (argMngr.getValue("environment").equals("quality")) {
			env = Environment.Quality;
		}
		if (argMngr.getValue("environment").equals("quality64")) {
			env = Environment.Quality64;
		}
		if (argMngr.getValue("environment").equals("incubator")) {
			env = Environment.Incubator;
		}

		// Local: copy from workspace to execution image.
		if (local) {

			// Central.
			if (argMngr.getValues("modules").contains("central")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(env, local, "C"));
				fc.setDescription(getDescription(env, local, "central"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, env, "CMA_Central");
				addLocalModuleBudgetDictionary(fc, env, "CMA_Central");
				addLocalModuleBudgetLocal(fc, env, "CMA_Central", false);
				addLocalModuleMarginsCentral(fc, env, "CMA_Central", true);
				addLocalModuleMarginsDictionary(fc, env, "CMA_Central", false);
				addLocalModuleMarginsLibrary(fc, env, "CMA_Central");
				addLocalModuleMarginsLocal(fc, env, "CMA_Central");
				addLocalModuleStrategicPlanCentral(fc, env, "CMA_Central");
				addLocalModuleStrategicPlanLocal(fc, env, "CMA_Central");
				addLocalModuleWorkingCapitalCentral(fc, env, "CMA_Central");
				addLocalModuleWorkingCapitalLocal(fc, env, "CMA_Central");
				addLocalModuleSecurity(fc, env, "CMA_Central");
				tasks.add(fc);
			}

			// Dictionary
			if (argMngr.getValues("modules").contains("dictionary")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(env, local, "D"));
				fc.setDescription(getDescription(env, local, "dictionary"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, env, "CMA_Dictionary");
				addLocalModuleBudgetDictionary(fc, env, "CMA_Dictionary");
				addLocalModuleMarginsCentral(fc, env, "CMA_Dictionary", false);
				addLocalModuleMarginsDictionary(fc, env, "CMA_Dictionary", true);
				addLocalModuleMarginsLibrary(fc, env, "CMA_Dictionary");
				addLocalModuleMarginsLocal(fc, env, "CMA_Dictionary");
				addLocalModuleSecurity(fc, env, "CMA_Dictionary");
				tasks.add(fc);
			}

			// Local.
			if (argMngr.getValues("modules").contains("local")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(env, local, "L"));
				fc.setDescription(getDescription(env, local, "local"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, env, "CMA_Local");
				addLocalModuleBudgetDictionary(fc, env, "CMA_Local");
				addLocalModuleBudgetLocal(fc, env, "CMA_Local", true);
				addLocalModuleMarginsCentral(fc, env, "CMA_Local", false);
				addLocalModuleMarginsDictionary(fc, env, "CMA_Local", false);
				addLocalModuleMarginsLibrary(fc, env, "CMA_Local");
				addLocalModuleMarginsLocal(fc, env, "CMA_Local");
				addLocalModuleStrategicPlanLocal(fc, env, "CMA_Local");
				addLocalModuleWorkingCapitalCentral(fc, env, "CMA_Local");
				addLocalModuleWorkingCapitalLocal(fc, env, "CMA_Local");
				addLocalModuleSecurity(fc, env, "CMA_Local");
				tasks.add(fc);
			}
		}

		// Remote: copy from execution image to destination drives.
		if (remote) {

			// The list of destination drives.
			List<String> drives = new ArrayList<>();
			if (env == Environment.Production) {
				drives.addAll(ListUtils.asList("U", "V", "W", "X", "Y", "Z"));
			} else if (env == Environment.ProductionAD) {
				drives.addAll(ListUtils.asList("L", "M", "N", "O", "P", "Q", "R", "S", "T"));
			} else if (env == Environment.Quality || env == Environment.Quality64) {
				drives.addAll(ListUtils.asList("T"));
			}

			// Central.
			if (argMngr.getValues("modules").contains("central")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(env, local, "C"));
					fc.setDescription(getDescription(env, local, "central") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "library", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_budget_local", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_security", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_stplan_central", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_stplan_local", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_wcapital_central", drive);
					addRemoteDirs(fc, env, "CMA_Central\\mads", "module_wcapital_local", drive);
					addRemoteFiles(fc, env, "CMA_Central", "CMA_Central.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Central", "JLoad.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Central", "JLoad_res.cmd", drive);
					tasks.add(fc);
				}
			}

			// Dictionary.
			if (argMngr.getValues("modules").contains("dictionary")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(env, local, "D"));
					fc.setDescription(getDescription(env, local, "dictionary") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "library", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, env, "CMA_Dictionary\\mads", "module_security", drive);
					addRemoteFiles(fc, env, "CMA_Dictionary", "CMA_Dictionary.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Dictionary", "JLoad.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Central", "JLoad_res.cmd", drive);
					tasks.add(fc);
				}
			}

			// Local.
			if (argMngr.getValues("modules").contains("local")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(env, local, "L"));
					fc.setDescription(getDescription(env, local, "local") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "library", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_budget_local", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_security", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_stplan_local", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_wcapital_central", drive);
					addRemoteDirs(fc, env, "CMA_Local\\mads", "module_wcapital_local", drive);
					addRemoteFiles(fc, env, "CMA_Local", "CMA_Local.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Local", "JLoad.cmd", drive);
					addRemoteFiles(fc, env, "CMA_Central", "JLoad_res.cmd", drive);
					tasks.add(fc);
				}
			}
		}

		return tasks;
	}

	/**
	 * Add the local library copy task
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalLibrary(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Lib";
		String dstParent = module + "\\mads\\library";
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		addLocalDirs(fc, env, srcParent, dstParent, "res");
		addLocalDirs(fc, env, srcParent, dstParent, "xsd");
		addLocalDirs(fc, env, srcParent, dstParent, "xml");
	}

	/**
	 * Add the local margins central module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indicate if the menu file should be copied.
	 */
	private static void addLocalModuleMarginsCentral(FileCopy fc, Environment env, String module, boolean menu) {
		String srcParent = "XVR COM Module Margins Central";
		String dstParent = module + "\\mads\\module_margins_central";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Central_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Central_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Central_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Central_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Central_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Central_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Central_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Central_Strings.xml");
		if (menu) {
			addLocalFiles(fc, env, srcParent, dstParent, "xml\\CMA_Central_Menu.xml");
		}
	}

	/**
	 * Add the local budget local module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indicate if the menu file should be copied.
	 */
	private static void addLocalModuleBudgetLocal(FileCopy fc, Environment env, String module, boolean menu) {
		String srcParent = "XVR COM Module Budget Local";
		String dstParent = module + "\\mads\\module_budget_local";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Local_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Local_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Local_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Local_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Local_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Local_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Local_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Local_Strings.xml");
		if (menu) {
			addLocalFiles(fc, env, srcParent, dstParent, "xml\\CMA_Local_Menu.xml");
		}
	}

	/**
	 * Add the local margins dictionary module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indicate if the menu file should be copied.
	 */
	private static void addLocalModuleMarginsDictionary(FileCopy fc, Environment env, String module, boolean menu) {
		String srcParent = "XVR COM Module Margins Dictionary";
		String dstParent = module + "\\mads\\module_margins_dictionary";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Dictionary_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Dictionary_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Dictionary_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Dictionary_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Dictionary_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Dictionary_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Dictionary_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Dictionary_Strings.xml");
		if (menu) {
			addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Dictionary_Menu.xml");
		}
	}

	/**
	 * Add the local margins library module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleMarginsLibrary(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module Margins Library";
		String dstParent = module + "\\mads\\module_margins_library";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Library_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Library_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Library_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Library_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Library_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Library_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Library_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Library_Strings.xml");
	}

	/**
	 * Add the local margins local module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleMarginsLocal(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module Margins Local";
		String dstParent = module + "\\mads\\module_margins_local";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Local_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Local_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Local_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Margins_Local_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Local_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Local_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Local_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Margins_Local_Strings.xml");
	}

	/**
	 * Add the local working capital central module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleWorkingCapitalCentral(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module WorkingCapital Central";
		String dstParent = module + "\\mads\\module_wcapital_central";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Central_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Central_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Central_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Central_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Central_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Central_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Central_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Central_Strings.xml");
	}

	/**
	 * Add the local working capital local module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleWorkingCapitalLocal(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module WorkingCapital Local";
		String dstParent = module + "\\mads\\module_wcapital_local";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Local_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Local_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Local_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\WorkingCapital_Local_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Local_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Local_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Local_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\WorkingCapital_Local_Strings.xml");
	}

	/**
	 * Add the local strategic plan central module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleStrategicPlanCentral(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module StrategicPlan Central";
		String dstParent = module + "\\mads\\module_stplan_central";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Central_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Central_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Central_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Central_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Central_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Central_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Central_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Central_Strings.xml");
	}

	/**
	 * Add the local strategic plan local module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleStrategicPlanLocal(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module StrategicPlan Local";
		String dstParent = module + "\\mads\\module_stplan_local";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Local_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Local_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Local_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\StrategicPlan_Local_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Local_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Local_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Local_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\StrategicPlan_Local_Strings.xml");
	}

	/**
	 * Add the local budget dictionary module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleBudgetDictionary(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module Budget Dictionary";
		String dstParent = "CMA_Central\\mads\\module_budget_dictionary";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Dictionary_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Dictionary_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Dictionary_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Budget_Dictionary_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Dictionary_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Dictionary_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Dictionary_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Budget_Dictionary_Strings.xml");
	}

	/**
	 * Add the local security module copy task.
	 * 
	 * @param fc File copy.
	 * @param env Environment
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleSecurity(FileCopy fc, Environment env, String module) {
		String srcParent = "XVR COM Module Seguridad";
		String dstParent = module + "\\mads\\module_security";
		// bin
		addLocalDirs(fc, env, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_DBSchema_en.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_Descriptor_en.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_Strings.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Module_Seguridad_Strings_en.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_DBSchema.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_DBSchema_en.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_DBSchema_es.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_Descriptor.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_Descriptor_en.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_Descriptor_es.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_Domains.txt");
		addLocalFiles(fc, env, srcParent, dstParent, "res\\Seguridad_Strings.txt");
		// xml
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Module_Seguridad_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Seguridad_DBSchema.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Seguridad_Descriptor.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Seguridad_Domains.xml");
		addLocalFiles(fc, env, srcParent, dstParent, "xml\\Seguridad_Strings.xml");
	}

	/**
	 * Returns a suitable name.
	 * 
	 * @param env Environment
	 * @param local Local/Remote
	 * @param module Module (C/D/L)
	 * @return The name.
	 */
	private static String getName(Environment env, boolean local, String module) {
		StringBuilder b = new StringBuilder();
		b.append("CP");
		if (env == Environment.Production || env == Environment.ProductionAD) {
			b.append("EP");
		}
		if (env == Environment.Quality) {
			b.append("ED");
		}
		if (env == Environment.Incubator) {
			b.append("EI");
		}
		if (local) {
			b.append("TL");
		} else {
			b.append("TR");
		}
		b.append("M" + module);
		return b.toString();
	}

	/**
	 * Returns a suitable description.
	 * 
	 * @param env Environment
	 * @param local Local/Remote
	 * @param module Module (central/dictionary/local)
	 * @return The name.
	 */
	private static String getDescription(Environment env, boolean local, String module) {
		StringBuilder b = new StringBuilder();
		b.append("Copy task");
		if (env == Environment.Production) {
			b.append(" environment:production");
		}
		if (env == Environment.Quality) {
			b.append(" environment:quality");
		}
		if (env == Environment.Incubator) {
			b.append(" environment:incubator");
		}
		if (local) {
			b.append(" target:local");
		} else {
			b.append(" target:remote");
		}
		b.append(" module:" + module);
		return b.toString();
	}

	/**
	 * Add directories for a remote copy task.
	 * 
	 * @param fc The file copy.
	 * @param env Environment
	 * @param parent Source (for instance CMA_Central\\mads)
	 * @param name Last directory name.
	 * @param drive Destination drive.
	 */
	private static void addRemoteDirs(FileCopy fc, Environment env, String parent, String name, String drive) {
		File fileSrcRoot = new File(getSrcRootRemote(env));
		File fileSrcParent = new File(fileSrcRoot, parent);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstParent = new File(drive + ":" + ((env == Environment.Production) ? "\\CMA\\" : "\\") + parent);
		File fileDst = new File(fileDstParent, name);
		fc.addDirectories(fileSrc, fileDst);
	}

	/**
	 * Add files for a remote copy task.
	 * 
	 * @param fc The file copy.
	 * @param env Environment
	 * @param parent Source (for instance CMA_Central\\mads)
	 * @param name Last directory name.
	 * @param drive Destination drive.
	 */
	private static void addRemoteFiles(FileCopy fc, Environment env, String parent, String name, String drive) {
		File fileSrcRoot = new File(getSrcRootRemote(env));
		File fileSrcParent = new File(fileSrcRoot, parent);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstParent = new File(drive + ":" + ((env == Environment.Production) ? "\\CMA\\" : "\\") + parent);
		File fileDst = new File(fileDstParent, name);
		fc.addFiles(fileSrc, fileDst);
	}

	/**
	 * Add local directories to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param env Environment
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocalDirs(FileCopy fc, Environment env, String src, String dst, String name) {
		addLocal(fc, env, true, src, dst, name);
	}

	/**
	 * Add local files to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param env Environment
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocalFiles(FileCopy fc, Environment env, String src, String dst, String name) {
		addLocal(fc, env, false, src, dst, name);
	}

	/**
	 * Add local directories/files to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param env Environment
	 * @param dirs Directories/Files.
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocal(FileCopy fc, Environment env, boolean dirs, String src, String dst, String name) {
		File fileSrcRoot = new File(getSrcRootLocal(env));
		File fileSrcParent = new File(fileSrcRoot, src);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstRoot = new File(getDstRootLocal(env));
		File fileDstParent = new File(fileDstRoot, dst);
		File fileDst = new File(fileDstParent, name);
		if (dirs) {
			fc.addDirectories(fileSrc, fileDst);
		} else {
			fc.addFiles(fileSrc, fileDst);
		}
	}

	/**
	 * Returns the source root for a local target task.
	 * 
	 * @param env Environment
	 * @return The source root.
	 */
	private static String getSrcRootLocal(Environment env) {
		String srcRoot = null;
		if (env == Environment.Production || env == Environment.ProductionAD) {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-head";
		}
		if (env == Environment.Quality) {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-development";
		}
		if (env == Environment.Quality64) {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-development-64";
		}
		if (env == Environment.Incubator) {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-incubator";
		}
		return srcRoot;
	}

	/**
	 * Returns the destination root for a local target task.
	 * 
	 * @param env Environment
	 * @return The source root.
	 */
	private static String getDstRootLocal(Environment env) {
		String dstRoot = null;
		if (env == Environment.Production || env == Environment.ProductionAD) {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-head";
		}
		if (env == Environment.Quality) {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-development";
		}
		if (env == Environment.Quality64) {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-development-64";
		}
		if (env == Environment.Incubator) {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-incubator";
		}
		return dstRoot;
	}

	/**
	 * Returns the source root for a remote target task.
	 * 
	 * @param env Environment
	 * @return The source root.
	 */
	private static String getSrcRootRemote(Environment env) {
		return getDstRootLocal(env);
	}
}
