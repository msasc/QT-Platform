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

package com.qtplaf.library.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.core.JCheckBoxField;
import com.qtplaf.library.swing.core.JComboBoxField;
import com.qtplaf.library.swing.core.JLabelField;
import com.qtplaf.library.swing.core.JMaskedField;
import com.qtplaf.library.swing.core.JMaskedFieldButton;
import com.qtplaf.library.swing.core.JMaskedFieldDate;
import com.qtplaf.library.swing.core.JPassword;

/**
 * A context that contains the necessary items to edit a record field.
 * 
 * @author Miquel Sas
 */
public class EditContext {

	/**
	 * Label prefix for edit label names created with the field alias.
	 */
	public static final String LABEL_PREFIX = "Label-";
	/**
	 * Field prefix for edit field names created with the field alias.
	 */
	public static final String FIELD_PREFIX = "Field-";

	/**
	 * The record that is being edited.
	 */
	private Record record;
	/**
	 * The alias of the field being edited.
	 */
	private String alias;
	/**
	 * The list of actions that are triggered when the edited value changes. The previous value, current value and this
	 * edit context are set in the action propoerties.
	 */
	private List<Action> valueActions = new ArrayList<>();
	/**
	 * A lookup action to be associated with theedit field. A lookup action will be used to retrieve the value for the
	 * current field or those related to the current edition, like a multi-value primary key.
	 */
	private Action actionLookup;
	/**
	 * The edit field related to this context.
	 */
	private EditField editField;
	/**
	 * The edit label related to this context.
	 */
	private EditLabel editLabel;
	/**
	 * A boolean that indicates if the edition of the related edit field is required. This overwrites the field required
	 * property.
	 */
	private boolean required = false;
	/**
	 * A boolean that indicates if this context will be used in a filter form.
	 */
	private boolean filter = false;
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public EditContext(Session session) {
		super();
		this.session = session;
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
	 * Returns a boolean that indicates if the edition of the related edit field is required.
	 * 
	 * @return A boolean that indicates if the edition of the related edit field is required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets a boolean that indicates if the edition of the related edit field is required.
	 * 
	 * @param required A boolean that indicates if the edition of the related edit field is required.
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Returns a boolean indicating if this context is used in a filter form.
	 * 
	 * @return A boolean
	 */
	public boolean isFilter() {
		return filter;
	}

	/**
	 * Sets a boolean indicating if this context is used in a filter form.
	 * 
	 * @param filter A boolean.s
	 */
	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	/**
	 * Returns the edit field related to this context.
	 * 
	 * @return The edit field.
	 */
	public EditField getEditField() {
		if (editField == null) {
			if (getField().isPassword()) {
				editField = new JPassword(this);
			} else if (getField().isBoolean()) {
				if (getField().isEditBooleanInCheckBox()) {
					editField = new JCheckBoxField(this);
				} else {
					editField = new JComboBoxField(this);
				}
			} else if (getField().isDate()) {
				editField = new JMaskedFieldDate(this);
			} else if (getField().isPossibleValues()) {
				editField = new JComboBoxField(this);
			} else if (getActionLookup() != null) {
				editField = new JMaskedFieldButton(this);
			} else {
				editField = new JMaskedField(this);
			}
			editField.setName(getEditFieldName(getField()));
		}
		return editField;
	}

	/**
	 * Returns the related edit field name.
	 * 
	 * @param alias The field alias.
	 * @return The related edit field name.
	 */
	public static String getEditFieldName(String alias) {
		return FIELD_PREFIX + alias;
	}

	/**
	 * Returns the related edit field name.
	 * 
	 * @param field The field.
	 * @return The related edit field name.
	 */
	public static String getEditFieldName(Field field) {
		return getEditFieldName(field.getAlias());
	}

	/**
	 * Returns the related edit label name.
	 * 
	 * @param alias The field alias.
	 * @return The related edit label name.
	 */
	public static String getEditLabelName(String alias) {
		return LABEL_PREFIX + alias;
	}

	/**
	 * Returns the related edit label name.
	 * 
	 * @param field The field.
	 * @return The related edit label name.
	 */
	public static String getEditLabelName(Field field) {
		return getEditLabelName(field.getAlias());
	}

	/**
	 * Returns the related edit label.
	 * 
	 * @return The related edit label.
	 */
	public EditLabel getEditLabel() {
		if (editLabel == null) {
			editLabel = new JLabelField(this);
		}
		return editLabel;
	}

	/**
	 * Returns the field being edited.
	 * 
	 * @return The field being edited.
	 */
	public Field getField() {
		return getRecord().getField(getAlias());
	}

	/**
	 * Returns the alias of the field being edited.
	 * 
	 * @return The alias of the field being edited.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias of the field being edited.
	 * 
	 * @param alias The alias of the field being edited.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Returns the record being edited.
	 * 
	 * @return The record.
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * Returns the related record value.
	 * 
	 * @return The value in the record.
	 */
	public Value getValue() {
		return getRecord().getValue(getAlias());
	}

	/**
	 * Set the record to edit.
	 * 
	 * @param record The record to edit.
	 */
	public void setRecord(Record record) {
		this.record = record;
	}

	/**
	 * Adds an action to the list of actions to be triggered when the associated component changes its value. When value
	 * actions are fired, the previous and the current value and this edit context are set as properties.
	 * 
	 * @param action The value action.
	 */
	public void addValueAction(Action action) {
		ActionUtils.setSession(action, getSession());
		ActionUtils.setEditContext(action, this);
		valueActions.add(action);
	}

	/**
	 * Removes the argument action from the list of value actions if it already is in.
	 * 
	 * @param action The action to remove.
	 */
	public void removeValueAction(Action action) {
		valueActions.remove(action);
	}

	/**
	 * Clear the list of value actions.
	 */
	public void clearValueActions() {
		valueActions.clear();
	}

	/**
	 * Fire value actions.
	 * 
	 * @param source The source component.
	 * @param previousValue The previous value.
	 * @param currentValue The current value.
	 */
	public void fireValueActions(Component source, Value previousValue, Value currentValue) {
		for (Action action : valueActions) {
			ActionUtils.setPreviousValue(action, previousValue);
			ActionUtils.setCurrentValue(action, currentValue);
			ActionUtils.setEditContext(action, this);
			action.actionPerformed(new ActionEvent(source, 0, ""));
		}
	}

	/**
	 * Returns the lookup action. The lookup action can come from the field or be build from a lookup relation.
	 * 
	 * @return The lookup action.
	 */
	public Action getActionLookup() {
		if (actionLookup == null) {
			actionLookup = getField().getActionLookup();
		}
		return actionLookup;
	}

	/**
	 * Sets the lookup action.
	 * 
	 * @param actionLookup The lookup action.
	 */
	public void setActionLookup(Action actionLookup) {
		this.actionLookup = actionLookup;
		ActionUtils.setEditContext(actionLookup, this);
		ActionUtils.setSession(actionLookup, getSession());
	}
}
