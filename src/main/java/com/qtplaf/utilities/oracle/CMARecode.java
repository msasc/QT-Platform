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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.swing.core.JFileChooser;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.ParserHandler;

/**
 * Change codes in an Oracle dump.
 *
 * @author Miquel Sas
 */
public class CMARecode {

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
	
	/**
	 * XML parser handler to read a articles to recode.
	 */
	static class Handler extends ParserHandler {
		// List of replacements to process.
		List<Data> datas = new ArrayList<>();
		
		/**
		 * Called to notify an element start.
		 */
		@Override
		public void elementStart(String namespace, String elementName, String path, Attributes attributes)
			throws SAXException {
			
			if (path.equals("articles/art")) {
				String search = attributes.getValue("dict");
				String noenc = attributes.getValue("noenc");
				if (noenc.equals("null")) {
					noenc = search;
				}
				String replace = attributes.getValue("val");
				String process = attributes.getValue("proc");
				if (!search.isEmpty() && process.equals("true")) {
					datas.add(new Data(search, replace, noenc));
				}
				return;
			}
			
		}
		
	}
	
	private static JFileChooser chooser;
	private static JFileChooser getFileChooser(Session session) {
		if (chooser == null) {
			chooser = new JFileChooser(session);
		}
		return chooser;
	}
	
	private static File getFile(Session session, boolean open) {
		JFileChooser chooser = getFileChooser(session);
		chooser.setDialogTitle(open ? "Source file" : "Destination file");
		chooser.setDialogType(open ? JFileChooser.OPEN_DIALOG : JFileChooser.SAVE_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(null);
		chooser.setAcceptAllFileFilterUsed(true);
		File file = null;
		if (chooser.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	private static File getXMLFile(Session session) {
		JFileChooser chooser = getFileChooser(session);
		chooser.setDialogTitle("XML file");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
		chooser.setAcceptAllFileFilterUsed(true);
		File file = null;
		if (chooser.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	/**
	 * @param args Startup args
	 */
	public static void main(String[] args) {

		// Strings and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		Locale.setDefault(Locale.UK);

		try {
			
			// XML file.
			File xml = getXMLFile(session);
			if (xml == null) {
				System.exit(0);
				return;
			}
			Handler handler = new Handler();
			Parser parser = new Parser();
			parser.parse(xml, handler);
			
			// List of replacements.
			List<Data> datas = handler.datas;
			
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
			progressManager.setTitle("CMA Recode");
			progressManager.setPanelProgressWidth(1000);
			progressManager.addTask(new TaskScanner(session, src, dst, datas));
			progressManager.addPreCloseAction(new ActionClose(session));
			progressManager.setProgressDecimals(4);
			progressManager.showFrame();

		} catch (Exception exc) {
			LOGGER.catching(exc);
		}

	}

}
