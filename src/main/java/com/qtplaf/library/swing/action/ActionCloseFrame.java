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

package com.qtplaf.library.swing.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;

/**
 * Close a frame.
 *
 * @author Miquel Sas
 */
public class ActionCloseFrame extends AbstractAction {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public ActionCloseFrame(Session session) {
		super();
		ActionUtils.configureClose(session, this);
		ActionUtils.setDefaultCloseAction(this, true);
	}

	/**
	 * Perform the action, the user object is set as the frame to close.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFrame frame = (JFrame) ActionUtils.getUserObject(this);
		frame.setVisible(false);
		frame.dispose();
	}
}
