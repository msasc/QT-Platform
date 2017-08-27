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

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractAction;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JOptionDialog;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;

/**
 * Action used in an option dialog or frame which main component is a <tt>JPanelTableRecord</tt>, giving access to the
 * table, the model and the selected records.
 * 
 * @author Miquel Sas
 */
public abstract class ActionTableOption extends AbstractAction {

	/**
	 * Constructor.
	 */
	public ActionTableOption() {
		super();
	}

	/**
	 * Returns the panel table.
	 * 
	 * @return The panel table.
	 */
	public JPanelTableRecord getPanelTableRecord() {
		Object object = ActionUtils.getUserObject(this);
		Component component = null;
		if (object instanceof JOptionFrame) {
			component = ((JOptionFrame) object).getComponent();
		}
		if (object instanceof JOptionDialog) {
			component = ((JOptionDialog) object).getComponent();
		}
		if (component == null) {
			throw new IllegalStateException("Illegal state in action.");
		}
		return (JPanelTableRecord) component;
	}

	/**
	 * Returns the table record.
	 * 
	 * @return The table record.
	 */
	public JTableRecord getTableRecord() {
		return getPanelTableRecord().getTableRecord();
	}

	/**
	 * Returns the table model.
	 * 
	 * @return The table model.
	 */
	public TableModelRecord getTableModel() {
		return getTableRecord().getTableModelRecord();
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @return The list of selected records.
	 */
	public List<Record> getSelectedRecords() {
		return getTableRecord().getSelectedRecords();
	}

	/**
	 * Returns the first selected record.
	 * 
	 * @return The first selected record.
	 */
	public Record getSelectedRecord() {
		return getTableRecord().getSelectedRecord();
	}

}
