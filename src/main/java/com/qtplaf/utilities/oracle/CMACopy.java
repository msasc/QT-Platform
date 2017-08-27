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

package com.qtplaf.utilities.oracle;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.swing.core.JFileChooser;
import com.qtplaf.library.util.TextServer;

/**
 * Change codes in an Oracle dump.
 *
 * @author Miquel Sas
 */
public class CMACopy {

	/** Logger configuration. */
	static {
		System.setProperty("log4j.configurationFile", "LoggerQTPlatform.xml");
	}
	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

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
			String message = "Exit CMA Recode?";
			if (MessageBox.question(session, message, MessageBox.YES_NO, MessageBox.NO) == MessageBox.YES) {
				System.exit(0);
			}
			throw new IllegalStateException();
		}

	}
	
	private static File getFile(Session session, boolean open) {
		JFileChooser chooserSource = new JFileChooser(session);
		chooserSource.setDialogTitle(open ? "Source file" : "Destination file");
		chooserSource.setDialogType(open ? JFileChooser.OPEN_DIALOG : JFileChooser.SAVE_DIALOG);
		chooserSource.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooserSource.setAcceptAllFileFilterUsed(true);
		File file = null;
		if (chooserSource.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooserSource.getSelectedFile();
		}
		return file;
	}

	/**
	 * @param args Startup args.
	 */
	public static void main(String[] args) {

		// Strings and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		Locale.setDefault(Locale.UK);

		try {
			
			// Source file.
			File src = getFile(session, true);
			if (src == null) {
				System.exit(0);
				return;
			}
			
			// Destination file.
			File dst = getFile(session, false);
			if (dst == null) {
				System.exit(0);
				return;
			}

			// Progress manager.
			ProgressManager progressManager = new ProgressManager(session);
			progressManager.setTitle("CMA Copy");
			progressManager.setPanelProgressWidth(1000);
			progressManager.addTask(new TaskCopy(session, src, dst));
			progressManager.addPreCloseAction(new ActionClose(session));
			progressManager.setProgressDecimals(4);
			progressManager.showFrame();

		} catch (Exception exc) {
			LOGGER.catching(exc);
		}

	}

}
