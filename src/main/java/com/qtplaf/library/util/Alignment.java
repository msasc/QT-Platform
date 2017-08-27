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
package com.qtplaf.library.util;

import java.text.MessageFormat;

import javax.swing.SwingConstants;

/**
 * Enumerates the accepted alignment options.
 *
 * @author Miquel Sas
 */
public enum Alignment {

	Top, Left, Bottom, Right, Center;

	/**
	 * Returns the swing alignment.
	 * 
	 * @return The swing alignment.
	 */
	public int getSwingAlignment() {
		switch (this) {
		case Top:
			return SwingConstants.TOP;
		case Left:
			return SwingConstants.LEFT;
		case Bottom:
			return SwingConstants.BOTTOM;
		case Right:
			return SwingConstants.RIGHT;
		case Center:
			return SwingConstants.CENTER;
		default:
			return SwingConstants.CENTER;
		}
	}

	/**
	 * Check if this alignment is top.
	 *
	 * @return A boolean.
	 */
	public boolean isTop() {
		return equals(Top);
	}

	/**
	 * Check if this alignment is left.
	 *
	 * @return A boolean
	 */
	public boolean isLeft() {
		return equals(Left);
	}

	/**
	 * Check if this alignment is bottom.
	 *
	 * @return A boolean.
	 */
	public boolean isBottom() {
		return equals(Bottom);
	}

	/**
	 * Check if this alignment is right.
	 *
	 * @return A boolean
	 */
	public boolean isRight() {
		return equals(Right);
	}

	/**
	 * Check if this alignment is center.
	 *
	 * @return A boolean
	 */
	public boolean isCenter() {
		return equals(Center);
	}

	/**
	 * Check if it's an horizontal alignment.
	 *
	 * @return A boolean
	 */
	public boolean isHorizontal() {
		return isLeft() || isCenter() || isRight();
	}

	/**
	 * Check if it's a vertical alignment.
	 *
	 * @return A boolean
	 */
	public boolean isVertical() {
		return isTop() || isCenter() || isBottom();
	}

	/**
	 * Returns the alignment given the name, not case sensitive.
	 * 
	 * @param alignmentName The alignment name.
	 * @return The alignment.
	 */
	public static Alignment parseAlignment(String alignmentName) {
		Alignment[] alignments = values();
		for (Alignment alignment : alignments) {
			if (alignment.name().toLowerCase().equals(alignmentName.toLowerCase())) {
				return alignment;
			}
		}
		throw new IllegalArgumentException(MessageFormat.format("Unsupported alignment name: {0}", alignmentName));
	}
}
