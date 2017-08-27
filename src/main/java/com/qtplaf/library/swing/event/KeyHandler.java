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

package com.qtplaf.library.swing.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A key event handler or adapter.
 * 
 * @author Miquel Sas
 */
public class KeyHandler implements KeyListener {

	/**
	 * Default constructor.
	 */
	public KeyHandler() {
	}

	/**
	 * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
	 * event.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
	 * pressed event.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
	 * released event.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
}
