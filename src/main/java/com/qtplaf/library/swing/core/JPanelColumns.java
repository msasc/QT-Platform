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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldGroup;
import com.qtplaf.library.database.FieldProperties;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.event.MouseHandler;

/**
 * A panel designed to select/unselect the columns. Usage:
 * <ul>
 * <li>Instantiate the panel passing the session and the master record.</li>
 * <li>Optionally setup available fields.</li>
 * <li>Setup selected fields.</li>
 * <li>Setup the panel with a call to <code>setup()</code>.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class JPanelColumns extends JPanel {

	/**
	 * Enumerates the possible operations or modes.
	 */
	public enum Mode {
		/** Domains selection. */
		Selection,
		/** Order definition. */
		Order;
	}

	/**
	 * The action to move fields from right to left.
	 */
	class ActionLeft extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionLeft() {
			super();
			ActionUtils.setShortDescription(this, getSession().getString("actionLeftShortDesc"));
			ActionUtils.setSmallIcon(this, new IconArrow(IconArrow.Direction.Left));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the right button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			List<Record> records = rightTable.getSelectedRecords();
			for (Record record : records) {

				int rowRight = rightTable.getTableModelRecord().indexOf(record);
				rightTable.getTableModelRecord().deleteRecord(record);
				if (rowRight == rightTable.getTableModelRecord().getRowCount()) {
					rowRight = rightTable.getTableModelRecord().getRowCount() - 1;
				}
				if (rowRight >= 0) {
					rightTable.setRowSelectionInterval(rowRight, rowRight);
					rightTable.ensureRowVisible(rowRight);
				}

				leftTable.getTableModelRecord().insertRecord(record);
				leftTable.adjustColumnSizes();
				int rowLeft = leftTable.getTableModelRecord().indexOf(record);
				leftTable.setRowSelectionInterval(rowLeft, rowLeft);
				rightTable.ensureRowVisible(rowLeft);
			}
		}
	}

	/**
	 * The action to move fields from right to left.
	 */
	class ActionRight extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionRight() {
			super();
			ActionUtils.setShortDescription(this, getSession().getString("actionRightShortDesc"));
			ActionUtils.setSmallIcon(this, new IconArrow(IconArrow.Direction.Right));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the right button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			List<Record> records = leftTable.getSelectedRecords();
			for (Record record : records) {
				int rowLeft = leftTable.getTableModelRecord().indexOf(record);
				leftTable.getTableModelRecord().deleteRecord(record);
				if (rowLeft == leftTable.getTableModelRecord().getRowCount()) {
					rowLeft = leftTable.getTableModelRecord().getRowCount() - 1;
				}
				if (rowLeft >= 0) {
					leftTable.setRowSelectionInterval(rowLeft, rowLeft);
					leftTable.ensureRowVisible(rowLeft);
				}

				int rightRow = rightTable.getTableModelRecord().getRowCount();
				rightTable.getTableModelRecord().insertRecord(rightRow, record);
				rightTable.adjustColumnSizes();
				rightTable.setRowSelectionInterval(rightRow, rightRow);
				rightTable.ensureRowVisible(rightRow);
			}
		}
	}

	/**
	 * The action to move fields of the right table up.
	 */
	class ActionUp extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionUp() {
			super();
			ActionUtils.setShortDescription(this, getSession().getString("actionUpShortDesc"));
			ActionUtils.setSmallIcon(this, new IconArrow(IconArrow.Direction.Up));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the up button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			int rowStart = rightTable.getSelectedRow();
			if (rowStart <= 0) {
				return;
			}
			int rowEnd = rowStart - 1;
			Record record = rightTable.getTableModelRecord().getRecord(rowStart);
			rightTable.getTableModelRecord().deleteRecord(rowStart);
			rightTable.getTableModelRecord().insertRecord(rowEnd, record);
			rightTable.setRowSelectionInterval(rowEnd, rowEnd);
		}
	}

	/**
	 * The action to move fields of the right table down.
	 */
	class ActionDown extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionDown() {
			super();
			ActionUtils.setShortDescription(this, getSession().getString("actionDownShortDesc"));
			ActionUtils.setSmallIcon(this, new IconArrow(IconArrow.Direction.Down));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the up button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			int rowStart = rightTable.getSelectedRow();
			if (rowStart == rightTable.getRowCount() - 1) {
				return;
			}
			int rowEnd = rowStart + 1;
			Record record = rightTable.getTableModelRecord().getRecord(rowStart);
			rightTable.getTableModelRecord().deleteRecord(rowStart);
			rightTable.getTableModelRecord().insertRecord(rowStart + 1, record);
			rightTable.setRowSelectionInterval(rowEnd, rowEnd);
		}
	}

	/**
	 * The action to set the selected field/s ascending.
	 */
	class ActionAsc extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionAsc() {
			super();
			ActionUtils.setName(this, getSession().getString("actionAscendingName"));
			ActionUtils.setShortDescription(this, getSession().getString("actionAscendingName"));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the up button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			setAscending(true);
		}
	}

	/**
	 * The action to set the selected field/s descending.
	 */
	class ActionDesc extends AbstractAction {
		/**
		 * Constructor.
		 */
		ActionDesc() {
			super();
			ActionUtils.setName(this, getSession().getString("actionDescendingName"));
			ActionUtils.setShortDescription(this, getSession().getString("actionDescendingName"));
			ActionUtils.setSession(this, getSession());
		}

		/**
		 * Called when the up button is clicked.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			setAscending(false);
		}
	}

	/**
	 * Set ascending/descending the selected records of the right table.
	 * 
	 * @param ascending A boolean.
	 */
	private void setAscending(boolean ascending) {
		List<Record> selectedRecords = rightTable.getSelectedRecords();
		if (!selectedRecords.isEmpty()) {
			String strAscending = getSession().getString(ascending ? "tokenAsc" : "tokenDesc");
			for (Record selectedRecord : selectedRecords) {
				selectedRecord.getValue(FieldProperties.ASCENDING).setString(strAscending);
			}
			rightTable.getTableModelRecord().fireTableDataChanged();
			rightTable.setSelectedRecords(selectedRecords);
		}
	}

	/**
	 * Mouse handle to handle doble cliks in left and right tables.
	 */
	class DoubleClickHandler extends MouseHandler {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (e.getSource().equals(rightTable)) {
					getButton(ActionLeft.class).doClick();
				}
				if (e.getSource().equals(leftTable)) {
					getButton(ActionRight.class).doClick();
				}
			}
		}

	}

	/**
	 * The master record.
	 */
	private Record masterRecord;
	/**
	 * List of available field aliases. By default, all the fields of the master record can be selected.
	 */
	private List<String> availableFields = new ArrayList<>();
	/**
	 * List of not selected fields.
	 */
	private List<String> notSelectedFields = new ArrayList<>();
	/**
	 * List of selected fields.
	 */
	private List<String> selectedFields = new ArrayList<>();
	/**
	 * A map with the ascending/descending flags of the selected fields, if applicable.
	 */
	private Map<String, Boolean> ascendingMap = new HashMap<>();
	/**
	 * Current working session.
	 */
	private Session session;
	/**
	 * Field properties manager.
	 */
	private FieldProperties fieldProperties;

	/**
	 * The left table with the list of not selected fields.
	 */
	private JTableRecord leftTable;
	/**
	 * The left table with the list of selected fields.
	 */
	private JTableRecord rightTable;
	/**
	 * Operation mode.
	 */
	private Mode mode;
	/**
	 * The list with all possible fields (properties).
	 */
	private List<Record> possibleFields = new ArrayList<>();

	/**
	 * Constructor assigning the master record.
	 * 
	 * @param session The working session.
	 * @param masterRecord The master record.
	 * @param mode The mode, selection or order.
	 */
	public JPanelColumns(Session session, Record masterRecord, Mode mode) {
		super();
		this.session = session;
		this.masterRecord = masterRecord;
		this.fieldProperties = new FieldProperties(session);
		this.mode = mode;
	}

	/**
	 * Optionally define the available fields.
	 * 
	 * @param alias The field alias.
	 */
	public void addAvailableField(String alias) {
		if (!masterRecord.getFieldList().containsField(alias)) {
			throw new IllegalArgumentException("Invalid field alias: " + alias);
		}
		availableFields.add(alias);
	}

	/**
	 * Define initially selected fields.
	 * 
	 * @param alias The field alias.
	 */
	public void addSelectedField(String alias) {
		if (!masterRecord.getFieldList().containsField(alias)) {
			throw new IllegalArgumentException("Invalid field alias: " + alias);
		}
		selectedFields.add(alias);
	}

	/**
	 * Define initially selected fields, use it for order selection.
	 * 
	 * @param alias The field alias.
	 * @param ascending A boolean that indicates if the field is ascending.
	 */
	public void addSelectedField(String alias, boolean ascending) {
		if (!masterRecord.getFieldList().containsField(alias)) {
			throw new IllegalArgumentException("Invalid field alias: " + alias);
		}
		ascendingMap.put(alias, ascending);
		selectedFields.add(alias);
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Setup the panel.
	 */
	public void setup() {

		// First build the list of possible fields.
		for (int i = 0; i < masterRecord.getFieldCount(); i++) {
			Field field = masterRecord.getField(i);
			possibleFields.add(fieldProperties.getProperties(field, i, true));
		}

		// If no available fields where defined, add all.
		if (availableFields.isEmpty()) {
			for (int i = 0; i < masterRecord.getFieldCount(); i++) {
				availableFields.add(masterRecord.getField(i).getAlias());
			}
		}

		// At least, the selected fields must be available.
		for (String alias : selectedFields) {
			if (!availableFields.contains(alias)) {
				availableFields.add(alias);
			}
		}

		// The list of not selected fields.
		for (String alias : availableFields) {
			if (!selectedFields.contains(alias)) {
				notSelectedFields.add(alias);
			}
		}

		// Constraints
		GridBagConstraints constraints = null;

		// Set the layout.
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new LineBorderSides(Color.GRAY, 1, true, false, false, false));

		// The field properties master record.
		Record masterProperties = fieldProperties.getProperties();

		// Double click handler.
		DoubleClickHandler doubleClickHandler = new DoubleClickHandler();

		// Left panel to hold the label and the left table.
		JPanel leftPanel = new JPanel(new GridBagLayout());

		// Left label
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		leftPanel.add(new JLabel(getSession().getString("panelColumnsAvailableFields")));

		// Define the left table and add it to the layout.
		leftTable = new JTableRecord(getSession());
		leftTable.addMouseListener(doubleClickHandler);
		JPanelTableRecord leftTablePanel = new JPanelTableRecord(leftTable, false);
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		leftPanel.add(leftTablePanel, constraints);

		// Add the left panel.
		add(leftPanel);

		// Define the buttons panel and add it to the layout.
		JPanel panelButtons = new JPanel(new GridBagLayout());

		// Button left
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 0;
		constraints.weighty = 0;
		JButton buttonLeft = new JButton(new ActionLeft());
		buttonLeft.setMargin(new Insets(2, 2, 2, 2));
		panelButtons.add(buttonLeft, constraints);

		// Button right
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 0;
		constraints.weighty = 0;
		JButton buttonRight = new JButton(new ActionRight());
		buttonRight.setMargin(new Insets(2, 2, 2, 2));
		panelButtons.add(buttonRight, constraints);

		// Button up
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 0;
		constraints.weighty = 0;
		JButton buttonUp = new JButton(new ActionUp());
		buttonUp.setMargin(new Insets(2, 2, 2, 2));
		panelButtons.add(buttonUp, constraints);

		// Button down
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 0;
		constraints.weighty = 0;
		JButton buttonDown = new JButton(new ActionDown());
		buttonDown.setMargin(new Insets(2, 2, 2, 2));
		panelButtons.add(buttonDown, constraints);

		// Buttons ascending/descending
		if (mode.equals(Mode.Order)) {

			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.fill = GridBagConstraints.NONE;
			constraints.gridheight = 1;
			constraints.gridwidth = 1;
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.insets = new Insets(1, 2, 1, 2);
			constraints.weightx = 0;
			constraints.weighty = 0;
			JButton buttonAsc = new JButton(new ActionAsc());
			buttonAsc.setMargin(new Insets(2, 2, 2, 2));
			panelButtons.add(buttonAsc, constraints);

			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.fill = GridBagConstraints.NONE;
			constraints.gridheight = 1;
			constraints.gridwidth = 1;
			constraints.gridx = 0;
			constraints.gridy = 5;
			constraints.insets = new Insets(1, 2, 1, 2);
			constraints.weightx = 0;
			constraints.weighty = 0;
			JButton buttonDesc = new JButton(new ActionDesc());
			buttonDesc.setMargin(new Insets(1, 1, 1, 1));
			panelButtons.add(buttonDesc, constraints);
		}

		panelButtons.setPreferredSize(new Dimension(50, 200));
		panelButtons.setMaximumSize(new Dimension(50, 200));

		// Add the buttons panel.
		add(panelButtons);

		// Left panel to hold the label and the left table.
		JPanel rightPanel = new JPanel(new GridBagLayout());

		// Left label
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		rightPanel.add(new JLabel(getSession().getString("panelColumnsSelectedFields")));

		// Define the right table and add it to the layout.
		rightTable = new JTableRecord(getSession());
		rightTable.addMouseListener(doubleClickHandler);
		JPanelTableRecord rightTablePanel = new JPanelTableRecord(rightTable, false);
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		rightPanel.add(rightTablePanel, constraints);

		// Add the left panel.
		add(rightPanel);

		// Set the initial records to the left table (not selected).
		TableModelRecord leftModel = new TableModelRecord(getSession(), masterProperties);
		if (anyFieldGroupDefined()) {
			leftModel.addColumn(FieldProperties.GROUP);
		}
		leftModel.addColumn(FieldProperties.ALIAS);
		leftModel.addColumn(FieldProperties.HEADER);
		leftModel.addColumn(FieldProperties.TITLE);
		leftModel.addColumn(FieldProperties.TYPE);
		leftModel.addColumn(FieldProperties.LENGTH);
		leftModel.addColumn(FieldProperties.DECIMALS);
		leftModel.setRecordSet(getRecordSetNotSelected());
		leftTable.setModel(leftModel);

		// Set the initial records to the right table (selected).
		TableModelRecord rightModel = new TableModelRecord(getSession(), masterProperties);
		if (anyFieldGroupDefined()) {
			rightModel.addColumn(FieldProperties.GROUP);
		}
		rightModel.addColumn(FieldProperties.ALIAS);
		rightModel.addColumn(FieldProperties.HEADER);
		rightModel.addColumn(FieldProperties.TITLE);
		rightModel.addColumn(FieldProperties.TYPE);
		rightModel.addColumn(FieldProperties.LENGTH);
		rightModel.addColumn(FieldProperties.DECIMALS);
		if (mode.equals(Mode.Order)) {
			rightModel.addColumn(FieldProperties.ASCENDING);
		}
		rightModel.setRecordSet(getRecordSetSelected());
		rightTable.setModel(rightModel);
	}

	/**
	 * Check if any field group has been defined within the available fields.
	 * 
	 * @return A boolean.
	 */
	private boolean anyFieldGroupDefined() {
		for (String alias : availableFields) {
			Record properties = getProperties(alias);
			if (fieldProperties.getPropertyGroupIndex(properties) != FieldGroup.emptyFieldGroup.getIndex()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the properties record of the given alias.
	 * 
	 * @param alias The alias.
	 * @return The properties record.
	 */
	private Record getProperties(String alias) {
		for (Record properties : possibleFields) {
			if (properties.getValue(FieldProperties.ALIAS).equals(alias)) {
				boolean ascending = isAscending(alias);
				String strAscending = getSession().getString(ascending ? "tokenAsc" : "tokenDesc");
				properties.getValue(FieldProperties.ASCENDING).setString(strAscending);
				return properties;
			}
		}
		return null;
	}

	/**
	 * Check if the field with the argument alias is ascending.
	 * 
	 * @param alias The field alias.
	 * @return A boolean.
	 */
	private boolean isAscending(String alias) {
		Boolean ascending = ascendingMap.get(alias);
		return (ascending == null ? true : ascending);
	}

	/**
	 * Returns the record set of not selected fields.
	 * 
	 * @return The record set of not selected fields.
	 */
	private RecordSet getRecordSetNotSelected() {
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldProperties.getFieldList());
		for (String alias : notSelectedFields) {
			recordSet.add(getProperties(alias));
		}
		return recordSet;
	}

	/**
	 * Returns the record set of selected fields.
	 * 
	 * @return The record set of selected fields.
	 */
	private RecordSet getRecordSetSelected() {
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldProperties.getFieldList());
		for (String alias : selectedFields) {
			recordSet.add(getProperties(alias));
		}
		return recordSet;
	}

	/**
	 * Returns the order defined by the list of selected fields.
	 * 
	 * @return The order defined by the list of selected fields.
	 */
	public Order getOrder() {
		Order order = new Order();
		RecordSet recordSet = rightTable.getTableModelRecord().getRecordSet();
		for (Record properties : recordSet) {
			Field field = fieldProperties.getPropertiesSourceField(properties);
			boolean ascending = fieldProperties.getPropertyAscending(properties);
			order.add(field, ascending);
		}
		return order;
	}

	/**
	 * Returns the list of selected fields.
	 * 
	 * @return The list of selected fields.
	 */
	public List<Field> getSelectedFields() {
		List<Field> fields = new ArrayList<>();
		RecordSet recordSet = rightTable.getTableModelRecord().getRecordSet();
		for (Record properties : recordSet) {
			Field field = fieldProperties.getPropertiesSourceField(properties);
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Returns the button that has the action.
	 * 
	 * @param actionClass The action class.
	 * @return The button.
	 */
	private JButton getButton(Class<? extends Action> actionClass) {
		List<Component> components = SwingUtils.getAllComponents(this, JButton.class);
		for (Component component : components) {
			JButton button = (JButton) component;
			if (button.getAction() == null) {
				continue;
			}
			if (actionClass.isInstance(button.getAction())) {
				return button;
			}
		}
		return null;
	}
}
