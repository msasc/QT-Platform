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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A mouse event handler or adapter.
 * 
 * @author Miquel Sas
 */
public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	/**
	 * Default constructor.
	 */
	public MouseHandler() {
	}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
	 * continue to be delivered to the component where the drag originated until the mouse button is released
	 * (regardless of whether the mouse position is within the bounds of the component).
	 * <p>
	 * Due to platform-dependent Drag&amp;Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
	 * during a native Drag&amp;Drop operation.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse wheel is rotated.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}
}
