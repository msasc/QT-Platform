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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * A table column model handler or adatper.
 * 
 * @author Miquel Sas
 */
public class TableColumnModelHandler implements TableColumnModelListener {

	/**
	 * Constructor.
	 */
	public TableColumnModelHandler() {
		super();
	}

	/**
	 * Called a column was added to the model.
	 */
	@Override
	public void columnAdded(TableColumnModelEvent e) {
	}

	/**
	 * Called when a column was removed from the model.
	 */
	@Override
	public void columnRemoved(TableColumnModelEvent e) {
	}

	/**
	 * Called when a column was repositioned.
	 */
	@Override
	public void columnMoved(TableColumnModelEvent e) {
	}

	/**
	 * Called when a column was moved due to a margin change.
	 */
	@Override
	public void columnMarginChanged(ChangeEvent e) {
	}

	/**
	 * Called when the selection model of the TableColumnModel changed.
	 */
	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
	}

}
