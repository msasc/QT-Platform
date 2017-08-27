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

import java.io.File;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JFileChooser;
import com.qtplaf.library.util.TextServer;

/**
 * A file encryptor.
 * 
 * @author Miquel Sas
 */
public class FileEncrypt {

	/**
	 * @param args Startup args.
	 */
	public static void main(String[] args) {

		// Strings and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		Locale.setDefault(Locale.UK);


		// Select the source file.
		JFileChooser chooserSource = new JFileChooser(session);
		chooserSource.setDialogTitle("Please, select the source file to encrypt");
		chooserSource.setDialogType(JFileChooser.OPEN_DIALOG);
		chooserSource.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooserSource.setAcceptAllFileFilterUsed(true);
		File fileSource = null;
		if (chooserSource.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			fileSource = chooserSource.getSelectedFile();
		}
		if (fileSource == null) {
			System.exit(0);
			return;
		}
		
		
		
		System.out.println(fileSource);
		
	}

}
