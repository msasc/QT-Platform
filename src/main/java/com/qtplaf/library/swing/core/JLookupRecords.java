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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.action.ActionSelectColumns;
import com.qtplaf.library.swing.action.ActionSortTable;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.swing.event.WindowHandler;
import com.qtplaf.library.util.Alignment;

/**
 * A dialog to lookup and select several entities from a list. First set the master record, then add columns (fields)
 * and finally set the list of records.
 * 
 * @author Miquel Sas
 */
public class JLookupRecords extends JDialogSession {

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			SwingUtils.executeButtonAction(JLookupRecords.this, ActionCancel.class);
		}
	}

	/**
	 * Cancel action.
	 */
	class ActionCancel extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionCancel() {
			super();
			ActionUtils.configureCancel(getSession(), this);
		}

		/**
		 * Perform the action, just close the window.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			selectedRecords.clear();
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Select action.
	 */
	class ActionSelect extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionSelect() {
			super();
			ActionUtils.configureSelect(getSession(), this);
		}

		/**
		 * Perform the action, register the selected records and close the window.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			selectedRecords = getTableRecord().getSelectedRecords();
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Mouse handler to handle double click.
	 */
	class SelectionMouseHandler extends MouseHandler {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				SwingUtils.executeButtonAction(JLookupRecords.this, ActionSelect.class);
			}
			super.mouseClicked(e);
		}

	}

	/**
	 * The table model record.
	 */
	private TableModelRecord tableModelRecord;
	/**
	 * The list of selected records.
	 */
	private List<Record> selectedRecords = new ArrayList<>();
	/**
	 * List selection model, default is multiple interval selection.
	 */
	private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	/**
	 * Horizontal alignment.
	 */
	private Alignment horizontalAlignment = Alignment.Center;
	/**
	 * Minimum number of columns to show a columns and sort and set a standard width.
	 */
	private int minimumColumns = 4;
	/**
	 * The minimum number of rows to set a standard height.
	 */
	private int minimumRows = 20;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param masterRecord The master record.
	 */
	public JLookupRecords(Session session, Record masterRecord) {
		super(session);
		tableModelRecord = new TableModelRecord(session, masterRecord);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param owner The owner window.
	 * @param masterRecord The master record.
	 */
	public JLookupRecords(Session session, Window owner, Record masterRecord) {
		super(session, owner);
		tableModelRecord = new TableModelRecord(session, masterRecord);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Add a column indicating the field alias.
	 * 
	 * @param alias The field alias.
	 */
	public void addColumn(String alias) {
		tableModelRecord.addColumn(alias);
	}

	/**
	 * Add a column indicating the field index.
	 * 
	 * @param index The field index whithin the master record.
	 */
	public void addColumn(int index) {
		tableModelRecord.addColumn(index);
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @return The list of selected records.
	 */
	public List<Record> getSelectedRecords() {
		return selectedRecords;
	}

	/**
	 * Returns the selection mode that will apply when selecting records.
	 * 
	 * @return The selection mode that will apply when selecting records.
	 */
	public int getSelectionMode() {
		return selectionMode;
	}

	/**
	 * Sets the selection modethat will apply when selecting records.
	 * 
	 * @param selectionMode The selection mode that will apply when selecting records.
	 */
	public void setSelectionMode(int selectionMode) {
		switch (selectionMode) {
		case ListSelectionModel.SINGLE_SELECTION:
		case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
		case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
			break;
		default:
			throw new IllegalArgumentException("Invalid list selection model: " + selectionMode);
		}
		this.selectionMode = selectionMode;
	}

	/**
	 * Returns the horizontal alignment.
	 * 
	 * @return The horizontal alignment.
	 */
	public Alignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Set the horizontal alignment.
	 * 
	 * @param horizontalAlignment The horizontal alignment.
	 */
	public void setHorizontalAlignment(Alignment horizontalAlignment) {
		if (!horizontalAlignment.isHorizontal()) {
			throw new IllegalArgumentException("The alignment must be an horizontal alignment: " + horizontalAlignment);
		}
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 * Set the list of prevously selected records.
	 * 
	 * @param records The list of selected records.
	 */
	public void setSelectedRecords(List<Record> records) {
		selectedRecords.addAll(records);
	}

	/**
	 * Show the list of records and let select a single record. Used normally with single selection mode.
	 * 
	 * @param recordSet The record set or list of records.
	 * @return The selected record or null.
	 */
	public Record lookupRecord(RecordSet recordSet) {
		List<Record> records = lookupRecords(recordSet);
		if (!records.isEmpty()) {
			return records.get(0);
		}
		return null;
	}

	/**
	 * Show the list of records and let lookup (select).
	 * 
	 * @param recordSet The record set or list of records.
	 * @return The selected records.
	 */
	public List<Record> lookupRecords(RecordSet recordSet) {

		// Check that columns have been defined
		if (tableModelRecord.getColumnCount() == 0) {
			throw new UnsupportedOperationException("Please, add columns before calling the lookup method.");
		}

		// First layout components, set the size and position the window.
		layoutComponents();

		// Assign the recordset to the model
		tableModelRecord.setRecordSet(recordSet);

		// Set the model
		getTableRecord().setModel(tableModelRecord);

		// Set the size and anchor.
		setSizeAndAnchor();

		// If the list of selected records is not empty, select them.
		if (!selectedRecords.isEmpty()) {
			getTableRecord().setSelectedRecords(selectedRecords);
		}

		// RunTickers it
		setModal(true);
		setVisible(true);

		return selectedRecords;
	}

	/**
	 * Set te size and anchor.
	 */
	private void setSizeAndAnchor() {

		// If the size has not been set, set it to the default 0.5/0.8
		boolean align = false;
		if (getSize().getHeight() == 0 || getSize().getWidth() == 0) {
			int columns = tableModelRecord.getColumnCount();
			int rows = tableModelRecord.getRowCount();
			if (columns > getMinimumColumns() && rows > getMinimumRows()) {
				setSize(0.5, 0.8);
				align = true;
			} else {
				pack();
			}
		}

		// Anchor (align) the window.
		Point point = SwingUtils.centerOnScreen(this);
		if (align) {
			if (getHorizontalAlignment().isRight()) {
				int x = point.x + getSize().width;
				point.x = point.x + (SwingUtils.getScreenSize(this).width - x);
			} else if (getHorizontalAlignment().isLeft()) {
				point.x = 0;
			}
		}
		setLocation(point);

	}

	/**
	 * Layout components prior to show the dialog.
	 */
	private void layoutComponents() {

		// Set the content pane to be a layout panel.
		setContentPane(new JPanel(new GridBagLayout()));
		GridBagConstraints constraints;

		// Add a JPanelTableEntity
		JTableRecord tableRecord = new JTableRecord(getSession());
		tableRecord.getSelectionModel().setSelectionMode(selectionMode);
		tableRecord.addMouseListener(new SelectionMouseHandler());
		JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 1;
		constraints.weighty = 1;
		getContentPane().add(panelTableRecord, constraints);

		// Add a button panel
		JPanelButtons panelButtons = new JPanelButtons();
		panelButtons.add(new ActionSelect());
		panelButtons.add(new ActionCancel());
		if (tableModelRecord.getColumnCount() >= getMinimumColumns()) {
			panelButtons.add(new ActionSelectColumns(tableRecord));
			panelButtons.add(new ActionSortTable(tableRecord));
		}
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 0;
		constraints.weighty = 0;
		getContentPane().add(panelButtons, constraints);

		// Install accelerator key listenerers
		SwingUtils.installAcceleratorKeyListener(this);
	}

	/**
	 * Returns the table record once installed as a component.
	 * 
	 * @return The table record.
	 */
	private JTableRecord getTableRecord() {
		List<Component> components = SwingUtils.getAllComponents(this, JTableRecord.class);
		if (!components.isEmpty()) {
			return (JTableRecord) components.get(0);
		}
		return null;
	}

	/**
	 * Returns the minimum number of columns to show the actions columns and sort.
	 * 
	 * @return The minimum number of columns to show the actions columns and sort.
	 */
	public int getMinimumColumns() {
		return minimumColumns;
	}

	/**
	 * Sets the minimum number of columns to show the actions columns and sort.
	 * 
	 * @param minimumColumns The minimum number of columns to show the actions columns and sort.
	 */
	public void setMinimumColumns(int minimumColumns) {
		this.minimumColumns = minimumColumns;
	}

	/**
	 * Returns the minimum number of rows to set a standard height.
	 * 
	 * @return The minimum number of rows to set a standard height.
	 */
	public int getMinimumRows() {
		return minimumRows;
	}

	/**
	 * Sets the minimum number of rows to set a standard height.
	 * 
	 * @param minimumRows The minimum number of rows to set a standard height.
	 */
	public void setMinimumRows(int minimumRows) {
		this.minimumRows = minimumRows;
	}
}
