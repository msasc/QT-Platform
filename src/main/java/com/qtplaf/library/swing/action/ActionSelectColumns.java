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
import javax.swing.table.TableColumnModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JDialogColumns;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * Action to select columns from a <code>JtableRecord</code>.
 * 
 * @author Miquel Sas
 */
public class ActionSelectColumns extends AbstractAction {

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
	public ActionSelectColumns(JTableRecord tableRecord) {
		super();
		this.tableRecord = tableRecord;
		ActionUtils.setSourceName(this, tableRecord.getSession().getString("actionColumnsName"));
		ActionUtils.setShortDescription(this, tableRecord.getSession().getString("actionColumnsName"));
		ActionUtils.setAcceleratorKey(this, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		ActionUtils.setSession(this, tableRecord.getSession());
		ActionUtils.setActionGroup(this, ActionGroup.OPERATION);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_columns));
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
		TableColumnModel columnModel = tableRecord.getColumnModel();
		TableModelRecord model = tableRecord.getTableModelRecord();
		Record masterRecord = model.getMasterRecord();
		dialogColumns.setMasterRecord(masterRecord);
		for (String alias : availableFields) {
			dialogColumns.addAvailableField(alias);
		}
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			int modelIndex = columnModel.getColumn(i).getModelIndex();
			dialogColumns.addSelectedField(model.getField(modelIndex).getAlias());
		}

		List<Field> selectedFields = dialogColumns.getSelectedFields();
		if (selectedFields != null) {
			List<Record> selectedRecords = tableRecord.getSelectedRecords();
			model.removeAllColumns();
			for (Field field : selectedFields) {
				model.addColumn(field.getAlias());
			}
			tableRecord.setModel(model);
			tableRecord.setSelectedRecords(selectedRecords);
		}
	}

}
