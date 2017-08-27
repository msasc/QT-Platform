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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditField;

/**
 * @author Miquel Sas
 *
 */
public class ActionSearchAndRefreshDB extends ActionSearchAndRefresh {

	/**
	 * The search record, must have a persistor and belong to the search table.
	 */
	private Record searchRecord;
	/**
	 * The list of aliases of key fields in the search record (table).
	 */
	private List<String> keyAliases = new ArrayList<>();
	/**
	 * The list of aliases of refresh fields in the search record (table).
	 */
	private List<String> refreshAliases = new ArrayList<>();

	/**
	 * List of aliases
	 */

	/**
	 * Default constructor.
	 */
	public ActionSearchAndRefreshDB() {
		super();
	}

	/**
	 * Constructor assigning the search record.
	 * 
	 * @param searchRecord The search record.
	 */
	ActionSearchAndRefreshDB(Record searchRecord) {
		super();
		this.searchRecord = searchRecord;
	}

	/**
	 * Add a key alias.
	 * 
	 * @param alias The alias.
	 */
	public void addKeyAlias(String alias) {
		keyAliases.add(alias);
	}

	/**
	 * Add a refresh alias.
	 * 
	 * @param alias The alias.
	 */
	public void addRefreshAlias(String alias) {
		refreshAliases.add(alias);
	}

	/**
	 * Returns the search record.
	 * 
	 * @return The search record.
	 */
	public Record getSearchRecord() {
		return searchRecord;
	}

	/**
	 * Sets the search record.
	 * 
	 * @param searchRecord The search record.
	 */
	public void setSearchRecord(Record searchRecord) {
		this.searchRecord = searchRecord;
	}

	/**
	 * Returns the list of refresh values, in the same order as the refresh edit fields.
	 * 
	 * @param keyEditFields The list of key edit fields.
	 * @param refreshEditFields The refresh of key edit fields.
	 * @return The list of refresh values.
	 */
	@Override
	protected List<Value> getRefreshValues(List<EditField> keyEditFields, List<EditField> refreshEditFields) {

		// If the search persistor has not been set, try to find a valid one.
		if (getSearchRecord() == null) {
			for (EditField editField : refreshEditFields) {
				Field field = editField.getEditContext().getField();
				if (field.getParentTable() != null && field.getParentTable().getPersistor() != null) {
					setSearchRecord(field.getParentTable().getDefaultRecord());
					break;
				}
			}
			if (getSearchRecord() == null) {
				throw new IllegalStateException("No valid search record has been set.");
			}
		}

		// Check lengths of lists.
		if (keyAliases.size() != keyEditFields.size()) {
			throw new IllegalStateException("The number of key aliases and key edit fields must be the same.");
		}
		if (refreshAliases.size() != refreshEditFields.size()) {
			throw new IllegalStateException("The number of refresh aliases and refresh edit fields must be the same.");
		}

		// Build a criteria with the search entity and the key components.
		Criteria criteria = new Criteria(Criteria.AND);
		for (int i = 0; i < keyEditFields.size(); i++) {
			String alias = keyAliases.get(i);
			Field field = getSearchRecord().getField(alias);
			Value value = keyEditFields.get(i).getValue();
			if (value.isValueArray()) {
				criteria.add(Condition.inList(field, value.getValueArray()));
			} else {
				criteria.add(Condition.fieldEQ(field, value));
			}
		}
		
		// Retrieve the possible list of records.
		RecordSet recordSet = null;
		try {
			Persistor persistor = getSearchRecord().getPersistor();
			recordSet = persistor.select(criteria);
		} catch (PersistorException exc) {
			exc.printStackTrace();
		}
		if (recordSet == null || recordSet.isEmpty()) {
			return null;
		}
		Record refreshRecord = recordSet.get(0);
		List<Value> values = new ArrayList<>();
		for (String alias : refreshAliases) {
			values.add(refreshRecord.getValue(alias));
		}

		return values;
	}

}
