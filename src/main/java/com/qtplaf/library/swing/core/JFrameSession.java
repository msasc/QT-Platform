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

package com.qtplaf.library.swing.core;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.WindowManager;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * Base frame class that basically handles the working session.
 * 
 * @author Miquel Sas
 */
public class JFrameSession extends JFrame {

	/**
	 * Invoke later set visible.
	 */
	class SetVisible implements Runnable {
		private boolean b;

		SetVisible(boolean b) {
			this.b = b;
		}

		@Override
		public void run() {
			JFrameSession.super.setVisible(b);
		}
	}

	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JFrameSession(Session session) {
		super();
		this.session = session;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the size based on factor of the screen size.
	 * 
	 * @param widthFactor The width factor relative to the screen.
	 * @param heightFactor The height factor relative to the screen.
	 */
	public void setSize(double widthFactor, double heightFactor) {
		setSize(SwingUtils.factorScreenDimension(this, widthFactor, heightFactor));
	}

	/**
	 * Moves this dialog to the desired location.
	 * 
	 * @param widthFactor Width factor.
	 * @param heightFactor Height factor.
	 */
	public void setLocation(double widthFactor, double heightFactor) {
		setLocation(SwingUtils.moveWindowOnScreen(this, widthFactor, heightFactor));
	}

	/**
	 * Set the window handler or adapter.
	 * 
	 * @param handler The window handler or adapter.
	 */
	protected void setWindowHandler(WindowHandler handler) {
		addWindowFocusListener(handler);
		addWindowListener(handler);
		addWindowStateListener(handler);
	}

	/**
	 * Set this frame visible.
	 * 
	 * @param b A boolean.
	 */
	@Override
	public void setVisible(boolean b) {
		if (b) {
			WindowManager.add(this);
		} else {
			WindowManager.remove(this);
		}
		SwingUtils.invokeLater(new SetVisible(b));
	}

	/**
	 * Set the frame visible immediately.
	 * 
	 * @param b A boolean.
	 */
	public void setVisibleImmediately(boolean b) {
		if (b) {
			WindowManager.add(this);
		} else {
			WindowManager.remove(this);
		}
		super.setVisible(b);
	}
}
