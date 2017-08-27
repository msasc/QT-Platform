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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordComparator;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JDialogColumns;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * Action to define an order and sort a <code>JtableRecord</code>.
 * 
 * @author Miquel Sas
 */
public class ActionSortTable extends AbstractAction {

	/**
	 * The <code>JtableRecord</code>.
	 */
	private JTableRecord tableRecord;
	/**
	 * List of available field aliases. By default, all the fields of the master record can be selected.
	 */
	private List<String> availableFields = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param tableRecord The table record.
	 */
	public ActionSortTable(JTableRecord tableRecord) {
		super();
		this.tableRecord = tableRecord;
		ActionUtils.setSourceName(this, tableRecord.getSession().getString("actionSortName"));
		ActionUtils.setShortDescription(this, tableRecord.getSession().getString("actionSortName"));
		ActionUtils.setAcceleratorKey(this, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		ActionUtils.setSession(this, tableRecord.getSession());
		ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_sort));
	}

	/**
	 * Optionally define the available fields.
	 * 
	 * @param alias The field alias.
	 */
	public void addAvailableField(String alias) {
		availableFields.add(alias);
	}

	/**
	 * Peerform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Get the window ancestor.
		Window windowAncestor = null;
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();
			windowAncestor = SwingUtils.getWindowAncestor(component);
		}
		Session session = tableRecord.getSession();
		JDialogColumns dialogColumns = new JDialogColumns(session, windowAncestor);
		TableModelRecord model = tableRecord.getTableModelRecord();
		Record masterRecord = model.getMasterRecord();
		dialogColumns.setMasterRecord(masterRecord);
		for (String alias : availableFields) {
			dialogColumns.addAvailableField(alias);
		}

		Order order = dialogColumns.getOrder(model);
		if (order != null) {
			model.setComparator(new RecordComparator(order));
			model.sort();
		}
	}

}
