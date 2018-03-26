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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordComparator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;

/**
 * A table model aimed to work with sets of records.
 * 
 * @author Miquel Sas
 */
public class TableModelRecord extends AbstractTableModel {

	/**
	 * Small structure to set which columns, rows or single cells are editable.
	 */
	class EditCell {
		/**
		 * The row, -1 indicates that all rows are editable.
		 */
		private int row = -1;
		/**
		 * The fieldIndex, -1 indicates that all fields are editable.
		 */
		private int fieldIndex = -1;

		/**
		 * Constructor assigning row and fieldIndex.
		 * 
		 * @param row The row.
		 * @param fieldIndex The field index.
		 */
		EditCell(int row, int fieldIndex) {
			super();
			this.row = row;
			this.fieldIndex = fieldIndex;
		}

		/**
		 * Returns an integer indicating the hash code.
		 * 
		 * @return The hash code.
		 */
		@Override
		public int hashCode() {
			return row + fieldIndex;
		}

		/**
		 * Returns a boolean indicating if the argument object is equal to this object.
		 * 
		 * @return A boolean indicating if the argument object is equal to this object.
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EditCell)) {
				return false;
			}
			EditCell ec = (EditCell) obj;
			return (ec.row == row && ec.fieldIndex == fieldIndex);
		}

		/**
		 * Returns the row.
		 * 
		 * @return The row.
		 */
		public int getRow() {
			return row;
		}

		/**
		 * Returns the field index.
		 * 
		 * @return The field index.
		 */
		public int getFieldIndex() {
			return fieldIndex;
		}

	}

	/**
	 * The master record used to configure the model even if the <code>RecordSet</code> has not been set or is empty.
	 */
	private Record masterRecord;
	/**
	 * The list of field indexes of the record to show as columns.
	 */
	private List<Integer> fieldIndexes = new ArrayList<>();
	/**
	 * The <code>RecordSet</code>.
	 */
	private RecordSet recordSet;
	/**
	 * The record comparator used to sort the results.
	 */
	private RecordComparator comparator;
	/**
	 * A map to manage which cells are editable.
	 */
	private HashMap<EditCell, Boolean> editMap = new HashMap<>();
	/**
	 * The working session.
	 */
	private Session session;
	/**
	 * The list of table record listeners.
	 */
	private List<TableRecordListener> tableRecordListeners = new ArrayList<>();

	/**
	 * Constructor assigning the master record.
	 * 
	 * @param session The working session.
	 * @param masterRecord The master record.
	 */
	public TableModelRecord(Session session, Record masterRecord) {
		super();
		this.session = session;
		this.masterRecord = masterRecord;
		this.recordSet = new RecordSet();
		this.recordSet.setFieldList(masterRecord.getFieldList());
	}

	/**
	 * Add a table record listener.
	 * 
	 * @param listener The listener.
	 */
	public void addTableRecordListener(TableRecordListener listener) {
		if (!tableRecordListeners.contains(listener)) {
			tableRecordListeners.add(listener);
		}
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
	 * Returns the master record of this model.
	 * 
	 * @return The master record.
	 */
	public Record getMasterRecord() {
		return masterRecord;
	}

	/**
	 * Add a fieldIndex.
	 * 
	 * @param index The field index in the master record.
	 */
	public void addColumn(int index) {
		if (index < 0 || index >= masterRecord.getFieldCount()) {
			throw new IllegalArgumentException("Invalid field index");
		}
		fieldIndexes.add(index);
		fireTableStructureChanged();
	}

	/**
	 * Add a fieldIndex.
	 * 
	 * @param alias The field alias.
	 */
	public void addColumn(String alias) {
		int index = masterRecord.getFieldList().getFieldIndex(alias);
		addColumn(index);
	}

	/**
	 * Returns a copy of the internal record set. Changes of the copy will not affect the table model.
	 * 
	 * @return A copy of the internal record set.
	 */
	public RecordSet getRecordSet() {
		return recordSet;
	}

	/**
	 * Returns the record at the given row.
	 * 
	 * @param row The row.
	 * @return The record.
	 */
	public Record getRecord(int row) {
		if (row < 0 || row >= getRowCount()) {
			throw new IllegalArgumentException("Invalid row index " + row);
		}
		return recordSet.get(row);
	}

	/**
	 * Returns the field index given the fieldIndex index.
	 * 
	 * @param column The column.
	 * @return The field index.
	 */
	public int getFieldIndex(int column) {
		if (column < 0 || column >= fieldIndexes.size()) {
			throw new IllegalArgumentException("Invalid fieldIndex index " + column);
		}
		return fieldIndexes.get(column);
	}

	/**
	 * Returns the field at given fieldIndex.
	 * 
	 * @param column The column index.
	 * @return The field at given fieldIndex.
	 */
	public Field getField(int column) {
		return masterRecord.getField(getFieldIndex(column));
	}

	/**
	 * Returns the number of rows.
	 * 
	 * @return The number of rows.
	 */
	@Override
	public int getRowCount() {
		if (recordSet == null) {
			return 0;
		}
		return recordSet.size();
	}

	/**
	 * Returns the number of columns.
	 * 
	 * @return The number of columns.
	 */
	@Override
	public int getColumnCount() {
		return fieldIndexes.size();
	}

	/**
	 * Returns the record comparator used to sort the results. If the comparator is not set, it creates one with the
	 * primary key fields.
	 * 
	 * @return The comparator.
	 */
	public RecordComparator getComparator() {
		if (comparator == null) {
			comparator = new RecordComparator(masterRecord.getPrimaryOrder());
		}
		return comparator;
	}

	/**
	 * Sets the record comparator used to sort the results.
	 * 
	 * @param comparator The record comparator used to sort the results.
	 */
	public void setComparator(RecordComparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * Returns the value at the given row and fieldIndex.
	 * 
	 * @param rowIndex The row index.
	 * @param columnIndex The fieldIndex index.
	 * @return The value at the given row and fieldIndex.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Record record = getRecord(rowIndex);
		Field field = getField(columnIndex);
		Value value = record.getValue(getFieldIndex(columnIndex));

		// TODO Implement passwords and images.

		// Boolean values.
		if (field.isBoolean()) {
			return value.getValue();
		}

		// Possible values.
		if (field.isPossibleValues()) {
			String label = field.getPossibleValueLabel(value);
			if (label != null) {
				return label;
			}
		}

		// Other values
		JFormattedTextField.AbstractFormatter formatter = field.getFormatter();
		if (formatter != null) {
			try {
				return formatter.valueToString(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return value.toStringFormatted(getSession().getLocale());
	}

	/**
	 * Set the value at the given row and fieldIndex.
	 *
	 * @param aValue value to assign to cell
	 * @param rowIndex row of cell
	 * @param columnIndex fieldIndex of cell
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue instanceof String) {
			String strFmt = (String) aValue;
			Record record = getRecord(rowIndex);
			Value value = record.getValue(getFieldIndex(columnIndex));
			try {
				value.fromStringFormatted(strFmt, getSession().getLocale());
			} catch (ParseException exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * Sort data based on the order.
	 * 
	 * @param order The order.
	 */
	public void sort(Order order) {
		setComparator(new RecordComparator(order));
		sort();
	}

	/**
	 * Sort this table model based on the installed comparator. If a total record exists at the end of the list, its
	 * position is preserved.
	 */
	public void sort() {
		Record totalRecord = null;
		if (getRowCount() > 0) {
			Record lastRecord = getRecord(getRowCount() - 1);
			if (lastRecord.getProperties().getObject(Record.TOTAL) != null) {
				totalRecord = lastRecord;
			}
		}
		if (totalRecord != null) {
			recordSet.remove(recordSet.size() - 1);
		}
		recordSet.sort(getComparator());
		if (totalRecord != null) {
			recordSet.add(totalRecord);
		}
		fireTableDataChanged();
	}

	/**
	 * Remove all columns. Note that when all columns are removed, the editable configuration of all cells is not lost,
	 * so if the columns are added in a different order the configuration remains active.
	 */
	public void removeAllColumns() {
		fieldIndexes.clear();
		fireTableStructureChanged();
	}

	/**
	 * Remove a given fieldIndex.
	 * 
	 * @param column The column index.
	 */
	public void removeColumn(int column) {
		if (column < 0 || column >= fieldIndexes.size()) {
			throw new IllegalArgumentException("Invalid fieldIndex index " + column);
		}
		fieldIndexes.remove(column);
		fireTableStructureChanged();
	}

	/**
	 * Set the record set without any persistence execution involved.
	 * 
	 * @param recordSet The record set.
	 */
	public void setRecordSet(RecordSet recordSet) {
		// Set the recordset
		this.recordSet = recordSet;
		fireTableChanged(
			new TableModelEvent(this, 0, getRowCount(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	/**
	 * Find the index of the given record.
	 *
	 * @param record The record to find its index.
	 * @return The index of the given record.
	 */
	public int indexOf(Record record) {
		return recordSet.indexOf(record);
	}

	/**
	 * Insert a record at the given row.
	 * 
	 * @param row The row.
	 * @param record The record to insert.
	 */
	public void insertRecord(int row, Record record) {
		if (row < 0 || row > getRowCount()) {
			throw new IllegalArgumentException("Invalid row index " + row);
		}
		recordSet.add(row, record);
		fireTableChanged(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
		// Fire record inserted.
		TableRecordEvent e = new TableRecordEvent(this, record);
		for (TableRecordListener listener : tableRecordListeners) {
			listener.insert(e);
		}
	}

	/**
	 * Insert a record.
	 * 
	 * @param record The record.
	 * @return the insert index.
	 */
	public int insertRecord(Record record) {
		int index = recordSet.getInsertIndex(record);
		insertRecord(index, record);
		return index;
	}

	/**
	 * Insert a record at the position given by the order.
	 * 
	 * @param record The record.
	 * @param order The order.
	 * @return the insert index.
	 */
	public int insertRecord(Record record, Order order) {
		int index = recordSet.getInsertIndex(record, order);
		insertRecord(index, record);
		return index;
	}

	/**
	 * Delete the record at the given row.
	 * 
	 * @param row The row.
	 */
	public void deleteRecord(int row) {
		Record record = recordSet.remove(row);
		fireTableRowsDeleted(row, row);
		// Fire record deleted.
		TableRecordEvent e = new TableRecordEvent(this, record);
		for (TableRecordListener listener : tableRecordListeners) {
			listener.delete(e);
		}
	}

	/**
	 * Delete the record.
	 * 
	 * @param record The record to delete.
	 */
	public void deleteRecord(Record record) {
		int row = recordSet.indexOf(record);
		if (row >= 0) {
			deleteRecord(row);
		}
	}

	/**
	 * Update the record at the given row.
	 * 
	 * @param row The row.
	 * @param record The record.
	 */
	public void updateRecord(int row, Record record) {
		if (row < 0 || row >= getRowCount()) {
			throw new IllegalArgumentException("Invalid row index " + row);
		}
		recordSet.set(row, record);
		fireTableChanged(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}

	/**
	 * Update the record.
	 * 
	 * @param record The record.
	 */
	public void updateRecord(Record record) {
		int row = recordSet.indexOf(record);
		if (row >= 0) {
			updateRecord(row, record);
		}
		// Fire record updated.
		TableRecordEvent e = new TableRecordEvent(this, record);
		for (TableRecordListener listener : tableRecordListeners) {
			listener.update(e);
		}
	}

	/**
	 * Returns the order used to sort this list.
	 * 
	 * @return The order used to sort this list.
	 */
	public Order getOrder() {
		return getComparator().getOrder(masterRecord);
	}

	/**
	 * Returns a boolean indicating if the cell is editable.
	 * 
	 * @param rowIndex The row index.
	 * @param columnIndex The fieldIndex index.
	 * @return A boolean indicating if the cell is editable.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		int fieldIndex = getFieldIndex(columnIndex);
		Boolean editable = editMap.get(new EditCell(rowIndex, fieldIndex));
		// If not specified the cell, check for the column.
		if (editable == null) {
			editable = editMap.get(new EditCell(-1, fieldIndex));
		}
		// Check for all columns
		if (editable == null) {
			editable = editMap.get(new EditCell(rowIndex, -1));
		}
		// Check for all cells
		if (editable == null) {
			editable = editMap.get(new EditCell(-1, -1));
		}
		// Then...
		if (editable == null) {
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * Set the cell editable.
	 * 
	 * @param rowIndex The row, if -1 all rows are editable.
	 * @param fieldIndex The field index, -1 all fields are editable.
	 */
	public void setCellEditable(int rowIndex, int fieldIndex) {
		editMap.put(new EditCell(rowIndex, fieldIndex), true);
	}

	/**
	 * Set the cell editable.
	 * 
	 * @param rowIndex The row, if -1 all rows are editable.
	 * @param fieldIndex The field index, -1 all fields are editable.
	 * @param editable A boolean that indicates if the cell is editable.
	 */
	public void setCellEditable(int rowIndex, int fieldIndex, boolean editable) {
		editMap.put(new EditCell(rowIndex, fieldIndex), Boolean.valueOf(editable));
	}
}
