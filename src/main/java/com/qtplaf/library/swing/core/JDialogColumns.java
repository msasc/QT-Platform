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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * A dialog to selected columns (fields) either to set the visible columns in a <code>JTableRecord</code> or to set an
 * order to sort it. Usage:
 * <ul>
 * <li>Instantiate the dialog.</li>
 * <li>Set the master record.</li>
 * <li>Optionally set the list of available field aliases.</li>
 * <li>Request the order or the list of selected fields. Note that the selected request sets the
 * <code>JPanelColumns.Mode</code>.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class JDialogColumns extends JDialogSession {

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			SwingUtils.executeButtonAction(JDialogColumns.this, ActionCancel.class);
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
			cancelled = true;
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Accept action.
	 */
	class ActionAccept extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionAccept() {
			super();
			ActionUtils.configureAccept(getSession(), this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			cancelled = false;
			setVisible(false);
			dispose();
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
	 * List of selected fields.
	 */
	private List<String> selectedFields = new ArrayList<>();
	/**
	 * A boolean that indicates that the edition has been cancelled.
	 */
	private boolean cancelled = false;
	/**
	 * The <code>JPanelColumns</code>.
	 */
	private JPanelColumns panelColumns;
	/**
	 * The mode.
	 */
	private JPanelColumns.Mode mode;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JDialogColumns(Session session) {
		super(session);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Constructor assigning the parent owner.
	 * 
	 * @param session The working session.
	 * @param owner The parent window owner.
	 */
	public JDialogColumns(Session session, Window owner) {
		super(session, owner);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Check that the master record has been set.
	 * 
	 * @throws IllegalStateException If the master record has not been set.
	 */
	private void checkMasterRecord() {
		if (masterRecord == null) {
			throw new IllegalStateException("The master record must be set prior to any other operation.");
		}
	}

	/**
	 * Optionally define the available fields. The master record must be set.
	 * 
	 * @param alias The field alias.
	 */
	public void addAvailableField(String alias) {
		// Check that the master record has been set.
		checkMasterRecord();
		// Check valid alias.
		if (!masterRecord.getFieldList().containsField(alias)) {
			throw new IllegalArgumentException("Invalid field alias: " + alias);
		}
		// Do add.
		availableFields.add(alias);
	}

	/**
	 * Define initially selected fields.
	 * 
	 * @param alias The field alias.
	 */
	public void addSelectedField(String alias) {
		// Check that the master record has been set.
		checkMasterRecord();
		// Check valid alias.
		if (!masterRecord.getFieldList().containsField(alias)) {
			throw new IllegalArgumentException("Invalid field alias: " + alias);
		}
		selectedFields.add(alias);
	}

	/**
	 * Sets the master record.
	 * 
	 * @param masterRecord The master record.
	 */
	public void setMasterRecord(Record masterRecord) {
		this.masterRecord = masterRecord;
	}

	/**
	 * Returns the list of selected fields or null if the operation has been cancelled.
	 * 
	 * @return The list of selected fields or null if the operation has been cancelled.
	 */
	public List<Field> getSelectedFields() {

		// Check that the master record has been set.
		checkMasterRecord();

		// Set the mode.
		mode = JPanelColumns.Mode.Selection;

		// Set the title.
		setTitle(getSession().getString("dialogColumnsSelectTableFields"));

		// Layout components, set the size and position the window.
		layoutComponents();

		// RunTickers it
		setModal(true);
		setVisible(true);

		if (cancelled) {
			return null;
		}
		return getPanelColumns().getSelectedFields();
	}

	/**
	 * Returns the order for a sort on a table model record, or null if the operation has been cancelled.
	 * 
	 * @param model The table model record.
	 * @return The order for a sort on a table model record, or null if the operation has been cancelled.s
	 */
	public Order getOrder(TableModelRecord model) {

		// Set the title.
		setTitle(getSession().getString("dialogColumnsDefineTableOrder"));

		// The master record.
		masterRecord = model.getMasterRecord();

		// Set the mode.
		mode = JPanelColumns.Mode.Order;

		// Set the selected fields from the order of the model. Do not use the selected fields of this action.
		selectedFields.clear();
		Order order = model.getOrder();
		for (Order.Segment segment : order) {
			String alias = segment.getField().getAlias();
			boolean asc = segment.isAsc();
			getPanelColumns().addSelectedField(alias, asc);
		}

		// Layout components, set the size and position the window.
		layoutComponents();

		// RunTickers it
		setModal(true);
		setVisible(true);

		if (cancelled) {
			return null;
		}
		return getPanelColumns().getOrder();
	}

	/**
	 * Returns the columns panel.
	 * 
	 * @return The columns panel.
	 */
	private JPanelColumns getPanelColumns() {
		if (mode == null) {
			throw new IllegalStateException("Te mode must be set.");
		}
		if (panelColumns == null) {
			panelColumns = new JPanelColumns(getSession(), masterRecord, mode);
		}
		return panelColumns;
	}

	/**
	 * Layout components prior to show the dialog.
	 */
	private void layoutComponents() {

		// Set the content pane to be a layout panel.
		setContentPane(new JPanel(new GridBagLayout()));
		GridBagConstraints constraints;

		// Configure the columns panel and add it.
		for (String alias : availableFields) {
			getPanelColumns().addAvailableField(alias);
		}
		for (String alias : selectedFields) {
			getPanelColumns().addSelectedField(alias);
		}
		getPanelColumns().setup();
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 2, 1, 2);
		constraints.weightx = 1;
		constraints.weighty = 1;
		getContentPane().add(getPanelColumns(), constraints);

		// Add a button panel
		JPanelButtons panelButtons = new JPanelButtons();
		panelButtons.add(new ActionAccept());
		panelButtons.add(new ActionCancel());
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

		// Set size and location.
		SwingUtils.setSizeAndCenterOnSreen(this, 0.8, 0.8);
	}
}
