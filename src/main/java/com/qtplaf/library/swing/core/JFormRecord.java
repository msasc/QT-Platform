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
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * Root class for record dialogs. By default, a form has a master record, a fields panel and a buttons panel.
 * 
 * @author Miquel Sas
 */
public class JFormRecord extends JDialogSession {

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			SwingUtils.executeButtonAction(JFormRecord.this, ActionCancel.class);
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
			panelFormFields.updateRecord();
			if (!validateForm()) {
				return;
			}
			cancelled = false;
			setVisible(false);
			dispose();
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
	 * The panel with form fields.
	 */
	private JPanelFormFields panelFormFields;
	/**
	 * The panel with buttons (actions).
	 */
	private JPanelButtons panelButtons;
	/**
	 * A boolean that indicates that the edition has been cancelled.
	 */
	private boolean cancelled = false;
	/**
	 * List of additional actions.
	 */
	private List<Action> actions = new ArrayList<>();
	/**
	 * A boolean to indicate if default action accept should be included.
	 */
	private boolean includeActionAccept = true;
	/**
	 * A boolean to indicate if default action cancel should be included.
	 */
	private boolean includeActionCancel = true;
	/**
	 * Optional form customizer.
	 */
	private JFormRecordCustomizer customizer;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JFormRecord(Session session) {
		super(session);
		setWindowHandler(new WindowAdapter());
		initializeContentPane();
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param owner The owner window.
	 */
	public JFormRecord(Session session, Window owner) {
		super(session, owner);
		setWindowHandler(new WindowAdapter());
		initializeContentPane();
	}

	/**
	 * Returns the record to edit/view.
	 * 
	 * @return The record.
	 */
	public Record getRecord() {
		return panelFormFields.getRecord();
	}

	/**
	 * Sets the record to edit/view. A copy is stored.
	 * 
	 * @param record The record.
	 */
	public void setRecord(Record record) {
		panelFormFields.setRecord(record);
	}

	/**
	 * Returns the border for grid items.
	 * 
	 * @return The border for grid items.
	 */
	public Border getGridItemBorder() {
		return panelFormFields.getGridItemBorder();
	}

	/**
	 * Sets the border for grid items.
	 * 
	 * @param gridItemBorder The border for grid items.
	 */
	public void setGridItemBorder(Border gridItemBorder) {
		panelFormFields.setGridItemBorder(gridItemBorder);
	}

	/**
	 * Initialize the content pane.
	 */
	private void initializeContentPane() {
		setContentPane(new JPanel(new GridBagLayout()));

		// The panel with fields.

		panelFormFields = new JPanelFormFields(getSession());

		GridBagConstraints constraintsFields = new GridBagConstraints();
		constraintsFields.anchor = GridBagConstraints.NORTH;
		constraintsFields.fill = GridBagConstraints.BOTH;
		constraintsFields.gridheight = 1;
		constraintsFields.gridwidth = 1;
		constraintsFields.gridx = 0;
		constraintsFields.gridy = 0;
		constraintsFields.insets = new Insets(0, 0, 0, 0);
		constraintsFields.weightx = 1;
		constraintsFields.weighty = 1;

		add(panelFormFields, constraintsFields);

		// The panel with buttons.

		panelButtons = new JPanelButtons();

		GridBagConstraints constraintsButtons = new GridBagConstraints();
		constraintsButtons.anchor = GridBagConstraints.NORTH;
		constraintsButtons.fill = GridBagConstraints.HORIZONTAL;
		constraintsButtons.gridheight = 1;
		constraintsButtons.gridwidth = 1;
		constraintsButtons.gridx = 0;
		constraintsButtons.gridy = 1;
		constraintsButtons.insets = new Insets(0, 0, 0, 0);
		constraintsButtons.weightx = 1;
		constraintsButtons.weighty = 0;

		add(panelButtons, constraintsButtons);
	}

	/**
	 * Check if default action accept should be included.
	 * 
	 * @return A boolean that indicates if default action accept should be included.
	 */
	public boolean isIncludeActionAccept() {
		return includeActionAccept;
	}

	/**
	 * Set if default action accept should be included.
	 * 
	 * @param includeActionAccept A boolean that indicates if default action accept should be included.
	 */
	public void setIncludeActionAccept(boolean includeActionAccept) {
		this.includeActionAccept = includeActionAccept;
	}

	/**
	 * Check if default action cancel should be included.
	 * 
	 * @return A boolean that indicates if default action cancel should be included.
	 */
	public boolean isIncludeActionCancel() {
		return includeActionCancel;
	}

	/**
	 * Set if default action cancel should be included.
	 * 
	 * @param includeActionCancel A boolean that indicates if default action cancel should be included.
	 */
	public void setIncludeActionCancel(boolean includeActionCancel) {
		this.includeActionCancel = includeActionCancel;
	}

	/**
	 * Adds an additional action to the list of actions.
	 * 
	 * @param action The action to add.
	 */
	public void addAction(Action action) {
		if (!actions.contains(action)) {
			ActionUtils.setFormRecord(action, this);
			actions.add(action);
		}
	}

	/**
	 * Add a field to the default (0, 0) sub-panel.
	 * 
	 * @param alias The field alias.
	 */
	public void addField(String alias) {
		addField(alias, 0, 0);
	}

	/**
	 * Add a field setting its grid coordinates. Grid coordinates are relative to the field group of the field, that is,
	 * the tab where the field group will be assigned. Grid coordinates are recomended to be set sequentially.
	 * 
	 * @param alias The field alias.
	 * @param gridx The grid x coordinate.
	 * @param gridy The grid y coordinate.
	 */
	public void addField(String alias, int gridx, int gridy) {
		checkRecord();
		panelFormFields.addField(alias, gridx, gridy);
	}

	/**
	 * Returns a list with all edit fields.
	 * 
	 * @return A list with all edit fields.
	 */
	public List<EditField> getEditFields() {
		return panelFormFields.getEditFields();
	}

	/**
	 * Returns the edit field for the given alias or null.
	 * 
	 * @param alias The field alias.
	 * @return The edit field.
	 */
	public EditField getEditField(String alias) {
		return panelFormFields.getEditField(alias);
	}

	/**
	 * Returns the edit mode.
	 * 
	 * @return The edit mode.
	 */
	public EditMode getEditMode() {
		return panelFormFields.getEditMode();
	}

	/**
	 * Sets the edit mode.
	 * 
	 * @param editMode The edit mode.
	 */
	public void setEditMode(EditMode editMode) {
		panelFormFields.setEditMode(editMode);
	}

	/**
	 * Returns a boolean that indicates if all labels in a column should have the same width.
	 * 
	 * @return A boolean that indicates if all labels in a column should have the same width.
	 */
	public boolean isSameWidthForColumLabels() {
		return panelFormFields.isSameWidthForColumLabels();
	}

	/**
	 * Sets a boolean that indicates if all labels in a column should have the same width.
	 * 
	 * @param sameWidthForColumLabels A boolean that indicates if all labels in a column should have the same width.
	 */
	public void setSameWidthForColumLabels(boolean sameWidthForColumLabels) {
		panelFormFields.setSameWidthForColumLabels(sameWidthForColumLabels);
	}

	/**
	 * Check if group panels should be scrolled.
	 * 
	 * @return A boolean.
	 */
	public boolean isScrollGroupPanels() {
		return panelFormFields.isScrollGroupPanels();
	}

	/**
	 * Set if group panels should be scrolled.
	 * 
	 * @param scrollGroupPanels A boolean.
	 */
	public void setScrollGroupPanels(boolean scrollGroupPanels) {
		panelFormFields.setScrollGroupPanels(scrollGroupPanels);
	}

	/**
	 * Returns the form customizer.
	 * 
	 * @return The form customizer.
	 */
	public JFormRecordCustomizer getCustomizer() {
		return customizer;
	}

	/**
	 * Set the form customizer.
	 * 
	 * @param customizer The form customizer.
	 */
	public void setCustomizer(JFormRecordCustomizer customizer) {
		this.customizer = customizer;
	}

	/**
	 * Edit the form and return a boolean indicating if the edition was cancelled.
	 * 
	 * @return A boolean.
	 */
	public boolean edit() {
		return edit(true);
	}

	/**
	 * Edit the form and return a boolean indicating if the edition was cancelled.
	 * 
	 * @param modal A boolean that indiicates if the dialog should be modal.
	 * @return A boolean.
	 */
	public boolean edit(boolean modal) {

		// Check that the record has been set.
		checkRecord();

		// Check that fields have been added.
		checkFields();

		// Add actions to the buttons panel.
		if (isIncludeActionAccept()) {
			if (!getEditMode().equals(EditMode.ReadOnly)) {
				panelButtons.add(new ActionAccept());
			}
		}
		if (isIncludeActionCancel()) {
			panelButtons.add(new ActionCancel());
		}
		for (Action action : actions) {
			panelButtons.add(action);
		}

		// Final configuration of the fields panel.
		panelFormFields.setEditMode(getEditMode());
		panelFormFields.layoutFields();
		panelFormFields.updateEditFields();

		// Install accelerator key listenerers
		SwingUtils.installAcceleratorKeyListener(this);

		// Pack and center on screen.
		pack();
		SwingUtils.centerOnScreen(this);

		// RunTickers it.
		setModal(modal);
		setVisible(true);
		requestFocus();

		return !cancelled;
	}

	/**
	 * Returns a boolean indicating if the form values are valid.
	 * 
	 * @return A boolean.
	 */
	public boolean validateForm() {

		// If the record has a validator, first check with it, supposing it accepts the edit mode as operation.
		if (getRecord().getValidator() != null) {
			boolean valid = getRecord().getValidator().validate(getRecord(), getEditMode());
			if (!valid) {
				String message = getRecord().getValidator().getMessage(getSession(), getRecord(), getEditMode());
				if (message != null) {
					MessageBox.error(getSession(), message);
				}
				return valid;
			}
		}

		// Check customizer. The customizer manages it own validation messages.
		if (getCustomizer() != null) {
			return getCustomizer().validateForm(this);
		}

		return true;
	}

	/**
	 * Check that fields have been added.
	 */
	private void checkFields() {
		if (panelFormFields.getFields().isEmpty()) {
			throw new IllegalStateException("No fields have been added to the form.");
		}
	}

	/**
	 * Check that the record has been set.
	 */
	private void checkRecord() {
		if (getRecord() == null) {
			throw new IllegalStateException("The record to edit must be set.");
		}
	}
}
