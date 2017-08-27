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

/**
 * Simple status bar interface to monitor fast tasks or processes.
 * 
 * @author Miquel Sas
 */
public interface StatusBar {
	/**
	 * Set the status string showing only the label.
	 * 
	 * @param status The status text.
	 */
	void setStatus(String status);

	/**
	 * Set the status message showing the progress bar with the current and maximum values. Current values range from
	 * zero to maximum.
	 * 
	 * @param status The status text.
	 * @param value The current progress value.
	 * @param maximum The maximum value.
	 */
	void setStatus(String status, int value, int maximum);

	/**
	 * Set the status string with the progress bar indeterminate.
	 * 
	 * @param status The status text.
	 */
	void setStatusIndeterminate(String status);

	/**
	 * Clearthe status text.
	 */
	void clearStatus();
}
