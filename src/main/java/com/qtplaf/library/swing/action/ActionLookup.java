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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.RecordSetCustomizer;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueArray;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.core.JLookupRecords;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.util.Alignment;
import com.qtplaf.library.util.Icons;

/**
 * An action to lookup a list of records, select one of them and update the referred components.
 * <p>
 * Normally to define an action lookup it is required to follow the next steps:
 * <ul>
 * <li>1. Define the master record, used to display the possible values.
 * <li>2. Define the columns to be displayed.
 * <li>3. Define the link between the the screen context (using the field keys) and the fields of the displayed records.
 * <li>4. Define the way to populate data:
 * <ul>
 * <li>Letting the action to automatically populate data. In this case you can create an optional criteria based on
 * special search criteria. When collecting data also can be set the order and distinct flag.
 * <li>Setting the record set.
 * </ul>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class ActionLookup extends AbstractAction {

	/**
	 * The master record.
	 */
	private Record masterRecord;
	/**
	 * The list of aliases of the fields to be displayed.
	 */
	private List<String> fieldAliases = new ArrayList<>();
	/**
	 * The optional recordset so we do not have to look for it.
	 */
	private RecordSet recordSet;
	/**
	 * The list of names of key edit field controls to be updated.
	 */
	private List<String> keyEditFieldNames = new ArrayList<>();
	/**
	 * The list of aliases of key fields that relate to the key edit controls.
	 */
	private List<String> keyAliases = new ArrayList<>();
	/**
	 * An optional criteria to further filter the recordset, either when retrieved from a database or when set directly.
	 */
	private Criteria criteria;
	/**
	 * An optional customizer to further adapt the recordset.
	 */
	private RecordSetCustomizer recordSetCustomizer;
	/**
	 * An optional selection or presentation order.
	 */
	private Order order;
	/**
	 * An optional title for this lookup.
	 */
	private String title;

	/**
	 * Default constructor.
	 */
	public ActionLookup() {
		super();
		ActionUtils.setSmallIcon(this, Icons.app_16x16_select_all);
	}

	/**
	 * Returns the working session, retrieved from the edit context in which this action lookup has been set.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		EditContext editContext = ActionUtils.getEditContext(this);
		if (editContext != null) {
			return editContext.getSession();
		}
		return null;
	}

	/**
	 * Configure the action.
	 * 
	 * @param masterRecord The master record.
	 * @param localKeyFields The list of local key fields.
	 * @param foreignKeyFields The list of foreign key fields.
	 */
	public void configure(Record masterRecord, List<Field> localKeyFields, List<Field> foreignKeyFields) {
		
		// Set the master record.
		setMasterRecord(masterRecord);
		
		// Add the links between key edit controls and foreign key fields.
		for (int i = 0; i < localKeyFields.size(); i++) {
			Field localKeyField = localKeyFields.get(i);
			Field foreignKeyField = foreignKeyFields.get(i);
			String keyEditName = EditContext.getEditFieldName(localKeyField);
			String keyAlias = foreignKeyField.getAlias();
			addKeyLink(keyEditName, keyAlias);
		}
		
		// Add the field aliases to be displayed: foreign key fields and fields from the foreign table that are main
		// description and lookup.
		List<String> lookupFields = new ArrayList<>();
		for (Field foreignKeyField : foreignKeyFields) {
			lookupFields.add(foreignKeyField.getAlias());
		}
		for (int i = 0; i < masterRecord.getFieldCount(); i++) {
			Field foreignField = masterRecord.getField(i);
			if (foreignField.isMainDescription()) {
				String alias = foreignField.getAlias();
				if (!lookupFields.contains(alias)) {
					lookupFields.add(alias);
				}
			}
		}
		for (int i = 0; i < masterRecord.getFieldCount(); i++) {
			Field foreignField = masterRecord.getField(i);
			if (foreignField.isLookup()) {
				String alias = foreignField.getAlias();
				if (!lookupFields.contains(alias)) {
					lookupFields.add(alias);
				}
			}
		}
		for (String alias : lookupFields) {
			addField(alias);
		}
	}

	/**
	 * Add a field alias to the list of field aliases.
	 * 
	 * @param alias The alias of the field.
	 */
	public void addField(String alias) {
		fieldAliases.add(alias);
	}

	/**
	 * Adds a key link between an edit field and a field alias.
	 * 
	 * @param keyEditField The key edit field name.
	 * @param keyAlias The key field alias.
	 */
	public void addKeyLink(String keyEditField, String keyAlias) {
		keyEditFieldNames.add(keyEditField);
		keyAliases.add(keyAlias);
	}

	/**
	 * Returns the master record.
	 * 
	 * @return The master record.
	 */
	public Record getMasterRecord() {
		return masterRecord;
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
	 * Returns the recordset.
	 * 
	 * @return The recordset.
	 */
	public RecordSet getRecordSet() {
		return recordSet;
	}

	/**
	 * Sets the recordset.
	 * 
	 * @param recordSet The recordset.
	 */
	public void setRecordSet(RecordSet recordSet) {
		this.recordSet = recordSet;
	}

	/**
	 * Returns the optional filteer criteria.
	 * 
	 * @return The optional filteer criteria.
	 */
	public Criteria getCriteria() {
		return criteria;
	}

	/**
	 * Sets the optional filter criteria.
	 * 
	 * @param criteria The optional filter criteria.
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Returns the recordset customizer.
	 * 
	 * @return The recordset customizer.
	 */
	public RecordSetCustomizer getRecordSetCustomizer() {
		return recordSetCustomizer;
	}

	/**
	 * Sets the recordset customizer.
	 * 
	 * @param recordSetCustomizer The recordset customizer.
	 */
	public void setRecordSetCustomizer(RecordSetCustomizer recordSetCustomizer) {
		this.recordSetCustomizer = recordSetCustomizer;
	}

	/**
	 * Returns the optional title.
	 * 
	 * @return The optional title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the optional title.
	 * 
	 * @param title The optional title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @return The list of selected records.
	 */
	public List<Record> getSelectedRecords() {
		return ActionUtils.getSelectedRecords(this);
	}

	/**
	 * Set the list of selected records.
	 * 
	 * @param selectedRecords The list of selected records.
	 */
	public void setSelectedRecords(List<Record> selectedRecords) {
		ActionUtils.setSelectedRecords(this, selectedRecords);
	}

	/**
	 * Sets the multiple selection flag. By default the lookup action is single selection.
	 * 
	 * @param b A boolean.
	 */
	public void setMultipleSelection(boolean b) {
		ActionUtils.setMultipleSelection(this, b);
	}

	/**
	 * Check if this lookup action is multiple selection. By default the lookup action is single selection.
	 * 
	 * @return A boolean.
	 */
	public boolean isMultipleSelection() {
		return ActionUtils.isMultipleSelection(this);
	}

	/**
	 * Returns the presentation order.
	 * 
	 * @return The order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Sets the presentation order.
	 * 
	 * @param order The order.
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * Invoked when normally from a masked field button.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Check minimal necessary configuration
		if (masterRecord == null) {
			throw new IllegalStateException("The master record must be set.");
		}
		if (fieldAliases.isEmpty()) {
			throw new IllegalStateException("The list of fields must be set.");
		}
		if (keyAliases.isEmpty()) {
			throw new IllegalStateException("The list of key aliases and edit fields must be set.");
		}

		// Check that all display-able fields exist.
		for (String alias : fieldAliases) {
			if (masterRecord.getField(alias) == null) {
				throw new IllegalStateException("Invalid field alias: " + alias);
			}
		}

		// Check that all key aliases and related edit fields exist.
		Component source = (Component) e.getSource();
		Window topWindow = SwingUtils.getWindowAncestor(source);
		Map<String, Component> componentMap = SwingUtils.getComponentMap(topWindow);
		for (int i = 0; i < keyAliases.size(); i++) {
			String alias = keyAliases.get(i);
			if (masterRecord.getField(alias) == null) {
				throw new IllegalStateException("Invalid key field alias: " + alias);
			}
			String name = keyEditFieldNames.get(i);
			Component component = componentMap.get(name);
			if (component == null) {
				throw new IllegalStateException("Invalid key edit field name: " + name);
			}
			if (!(component instanceof EditField)) {
				throw new IllegalStateException("Key edit field " + name + " is not an Edit Field");
			}
		}

		// Build the list of records if not already built.
		RecordSet workingRecordSet;
		if (recordSet == null) {

			// The record set has not been previously set. First check if there is a persistor to retrieve the data.
			if (getMasterRecord().getPersistor() == null) {
				throw new IllegalStateException("A Persistor is required if the record set is not previously set.");
			}

			// Build a working criteria to retrieve the data.
			Criteria workingCriteria = new Criteria(Criteria.AND);

			// Append the optional criteria if present.
			if (criteria != null) {
				workingCriteria.add(criteria);
			}

			// Append the criteria for key fields. If the field is string, then a partial value is admitted through a
			// LikeLeft condition.
			for (int i = 0; i < keyAliases.size(); i++) {
				String alias = keyAliases.get(i);
				String name = keyEditFieldNames.get(i);
				Field field = masterRecord.getField(alias);
				Component component = componentMap.get(name);
				EditField editField = (EditField) component;
				Value value = editField.getValue();
				if (value.isValueArray()) {
					workingCriteria.add(Condition.inList(field, value.getValueArray()));
				} else {
					if (field.isString()) {
						workingCriteria.add(Condition.likeLeft(field, value));
					} else {
						workingCriteria.add(Condition.fieldEQ(field, value));
					}
				}
			}

			// The selection order. If not set, build it with the key fields.
			Order selectionOrder;
			if (order == null) {
				if (getMasterRecord().getPersistor().getView().getOrderBy() != null) {
					selectionOrder = getMasterRecord().getPersistor().getView().getOrderBy();
				} else {
					selectionOrder = new Order();
					for (int i = 0; i < keyAliases.size(); i++) {
						String alias = keyAliases.get(i);
						Field field = masterRecord.getField(alias);
						selectionOrder.add(field);
					}
				}
			} else {
				selectionOrder = order;
			}

			// Try filling the working record set.
			try {
				workingRecordSet = getMasterRecord().getPersistor().select(workingCriteria, selectionOrder);
			} catch (PersistorException exc) {
				exc.printStackTrace();
				return;
			}
		} else {
			// The record set was already set. Create one applying the optional criteria.
			workingRecordSet = recordSet.getRecordSet(criteria);
			// If an order was set, sort it.
			if (order != null) {
				workingRecordSet.sort(order);
			}
		}

		// Apply the customizer if set.
		if (recordSetCustomizer != null) {
			recordSetCustomizer.customize(workingRecordSet);
		}

		// Configure the lookup.
		JLookupRecords lookup;
		if (topWindow != null) {
			lookup = new JLookupRecords(getSession(), topWindow, masterRecord);
		} else {
			lookup = new JLookupRecords(getSession(), masterRecord);
		}
		// Set the selection mode.
		if (isMultipleSelection()) {
			lookup.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			lookup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		// Get the source component that triggered this action and set the dialog anchor trying to leave the component
		// visible.
		if (source.getLocation().getX() < SwingUtils.getScreenSize(topWindow).getWidth() / 2) {
			lookup.setHorizontalAlignment(Alignment.Right);
		} else {
			lookup.setHorizontalAlignment(Alignment.Left);
		}
		// Add fields (columns).
		for (String alias : fieldAliases) {
			lookup.addColumn(alias);
		}
		// Set the title. If the optional title is not set, build one using the key fields headers.
		if (title == null) {
			StringBuilder b = new StringBuilder();
			b.append(getSession().getString("tokenSelect"));
			for (int i = 0; i < keyAliases.size(); i++) {
				String alias = keyAliases.get(i);
				Field field = masterRecord.getField(alias);
				if (i > 0) {
					b.append(", ");
				}
				b.append(field.getDisplayHeader());
			}
			title = b.toString();
		}
		lookup.setTitle(title);

		// Perform the lookup.
		List<Record> selectedRecords = lookup.lookupRecords(workingRecordSet);

		// Save the selected records.
		ActionUtils.setSelectedRecords(this, selectedRecords);

		// If no records selected, just return.
		if (selectedRecords.isEmpty()) {
			return;
		}

		// Set values to key edit fields.
		for (int i = 0; i < keyAliases.size(); i++) {
			String alias = keyAliases.get(i);
			String name = keyEditFieldNames.get(i);
			Component component = componentMap.get(name);
			EditField editField = (EditField) component;
			editField.setValue(getValue(alias, selectedRecords));
		}
	}

	/**
	 * Returns the value related to the field alias and list of records.
	 * 
	 * @param alias The field alias.
	 * @param selectedRecords The list of selected records.
	 * @return The related value.
	 */
	private Value getValue(String alias, List<Record> selectedRecords) {
		Value value;
		if (selectedRecords.size() == 1) {
			Record record = selectedRecords.get(0);
			value = new Value(record.getValue(alias));
		} else {
			ValueArray valueArray = new ValueArray();
			for (Record record : selectedRecords) {
				valueArray.add(new Value(record.getValue(alias)));
			}
			value = new Value(valueArray);
		}
		return value;
	}

}
