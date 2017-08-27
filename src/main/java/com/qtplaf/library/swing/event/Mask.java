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

import java.awt.event.InputEvent;

/**
 * Utility class to deal with key and mouse masks on key and mouse events.
 *
 * @author Miquel Sas
 */
public class Mask {

	/** Shift down. */
	public static final int SHIFT = InputEvent.SHIFT_DOWN_MASK;
	/** Control down. */
	public static final int CTRL = InputEvent.CTRL_DOWN_MASK;
	/** Meta down. */
	public static final int META = InputEvent.META_DOWN_MASK;
	/** Alt down. */
	public static final int ALT = InputEvent.ALT_DOWN_MASK;
	/** Button1 down. */
	public static final int BUTTON1 = InputEvent.BUTTON1_DOWN_MASK;
	/** Button2 down. */
	public static final int BUTTON2 = InputEvent.BUTTON2_DOWN_MASK;
	/** Button3 down. */
	public static final int BUTTON3 = InputEvent.BUTTON3_DOWN_MASK;
	/** Alt Graph down. */
	public static final int ALT_GRAPH = InputEvent.ALT_GRAPH_DOWN_MASK;

	/** All masks. */
	public static final int[] MASKS = new int[] { SHIFT, CTRL, META, ALT, BUTTON1, BUTTON2, BUTTON3, ALT_GRAPH };


	/**
	 * Check the input event for mask agreement, any othe mask off.
	 * 
	 * @param e The input event.
	 * @param masksOn The masks to be on, any other off.
	 * @return A boolean.
	 */
	public static boolean check(InputEvent e, int... masksOn) {
		return check(e, new Mask(masksOn));
	}
	
	/**
	 * Check the input event for mask agreement, any othe mask off.
	 * 
	 * @param e The input event.
	 * @param on On mask.
	 * @return A boolean.
	 */
	public static boolean check(InputEvent e, Mask on) {
		return check(e, on, new Mask(off(on.getMask())));
	}

	/**
	 * Check the input event for mask agreement.
	 * 
	 * @param e The input event.
	 * @param on On mask.
	 * @param off Off mask.
	 * @return A boolean.
	 */
	public static boolean check(InputEvent e, Mask on, Mask off) {
		return ((e.getModifiersEx() & (on.getMask() | off.getMask())) == on.getMask());
	}

	/**
	 * Returns the mask that must be off to be on only the on mask.
	 * 
	 * @param on The on mask
	 * @return The off mask.
	 */
	private static int off(int on) {
		int off = 0;
		for (int mask : MASKS) {
			if (!((on & mask) == mask)) {
				off |= mask;
			}
		}
		return off;
	}

	/**
	 * The mask.
	 */
	private int mask = 0;

	/**
	 * Constructor assingning a list of masks.
	 * 
	 * @param masks The list of masks.
	 */
	public Mask(int... masks) {
		super();
		for (int mask : masks) {
			this.mask |= mask;
		}
	}

	/**
	 * Returns the mask.
	 * 
	 * @return The mask.
	 */
	public int getMask() {
		return mask;
	}
}
