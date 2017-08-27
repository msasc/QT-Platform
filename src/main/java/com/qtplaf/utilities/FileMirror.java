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

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.FrameMenu;
import com.qtplaf.library.util.TextServer;

/**
 * A file mirror utility based on an xml configuration file.
 * 
 * @author Miquel Sas
 */
public class FileMirror {

	/**
	 * Main parameter is the configuration file.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		
		// System text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);

		// Frame menu.
		FrameMenu frameMenu = new FrameMenu(session);
		frameMenu.setTitle("Mirror utility");
		frameMenu.setLocation(20, 20);
		frameMenu.setSize(0.25, 0.65);
		
		// Re-direct out and err.
		System.setOut(frameMenu.getConsole().getPrintStream());
		System.setErr(frameMenu.getConsole().getPrintStream());
		
		// RunTickers the menu.
		frameMenu.setVisible(true);
		
		System.out.println("Hello console");
	}

}
