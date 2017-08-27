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

package com.qtplaf.library.swing;

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JDialogSession;
import com.qtplaf.library.swing.core.JFrameSession;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.event.KeyHandler;
import com.qtplaf.library.util.list.ListUtils;

/**
 * The window manager keeps track of dialogs and frames that extend <tt>JDialogSession</tt> or <tt>JFrameSession</tt>.
 * 
 * @author Miquel Sas
 */
public class WindowManager {

	/**
	 * Windows key.
	 */
	public static final KeyStroke KEY_WINDOWS =
		KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK);

	/**
	 * Key listener to show the windows list.
	 */
	static class KeyAdapter extends KeyHandler {
		@Override
		public void keyReleased(KeyEvent e) {
			if (KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers()).equals(KEY_WINDOWS)) {
				StringBuilder b = new StringBuilder();
				Session session = null;
				for (int i = 0; i < windows.size(); i++) {
					Window window = windows.get(i);
					if (window instanceof JFrameSession) {
						JFrameSession frame = (JFrameSession) window;
						session = frame.getSession();
						b.append(frame.getTitle() + "\n");
					}
					if (window instanceof JDialogSession) {
						JDialogSession dialog = (JDialogSession) window;
						session = dialog.getSession();
						b.append(dialog.getTitle() + "\n");
					}
				}
				MessageBox.info(session, b.toString());
			}
		}

	}

	/**
	 * The list of windows.
	 */
	private static List<Window> windows = new ArrayList<>();
	/**
	 * Key listener.
	 */
	private static KeyAdapter keyListener = new KeyAdapter();

	/**
	 * Add a window.
	 * 
	 * @param window The window to add.
	 */
	synchronized public static void add(Window window) {
		SwingUtils.installKeyListener(window, keyListener);
		windows.add(window);
	}

	/**
	 * Remove a window.
	 * 
	 * @param window The window to remove.
	 */
	synchronized public static void remove(Window window) {
		windows.remove(window);
	}
	
	synchronized public static Window getLast() {
		return ListUtils.getLast(windows);
	}
	
	/**
	 * Only static methods.
	 */
	private WindowManager() {
	}
}
