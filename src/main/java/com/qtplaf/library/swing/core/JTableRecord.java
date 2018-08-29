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
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.PageRecordSet;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Extends <code>JTable</code> to support lists of records. If the model set is not a <code>TableModelRecord</code>, it
 * throws a <code>ClassCastException</code>.
 * <p>
 * When <b>sorting</b> is enabled, clicking the header out of the resize area and without the control mask sorts the
 * table by the column, starting ascending and int subsequent clicks switching between ascending and descending. Using
 * the control mask, adds the column as an order segment to the current order if not present in the order, and
 * subsequent clicks with the control mask switch the column between ascending and descending.
 * 
 * @author Miquel Sas
 */
public class JTableRecord extends JTable {

	/**
	 * The mouse handler to handle mouse events on the header.
	 */
	class HeaderMouseHandler extends MouseHandler {

		/** Margin to accept the point to be in the resize area of the column header. */
		private int resizeMargin = 4;

		/**
		 * Invoked when the mouse button has been clicked (pressed and released) on a component.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {

			// Button 1 actions.
			if (e.getButton() == MouseEvent.BUTTON1) {

				// When the point is in the resize area.
				if (isResize(e.getPoint())) {
					if (e.getClickCount() == 2) {
						int column = getColumnToResize(e.getPoint());
						if (column >= 0) {
							adjustColumnSize(column);
						}
					}
					return;
				}

				// Not in the resize area, check sorting.
				if (isSortingEnabled()) {
					boolean controlMask = ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0);
					int column = getTableHeader().columnAtPoint(e.getPoint());
					sort(column, controlMask);
				}
			}
		}

		/**
		 * Returns the number of the column to resize given the point of the mouse, by checking if the point is in the
		 * limit of two subsequent columns.
		 * 
		 * @param p The mouse point.
		 * @return The column number to resize or -1 if none applicable.
		 */
		private int getColumnToResize(Point p) {
			int column = getTableHeader().columnAtPoint(p);
			if (column == -1) {
				return -1;
			} else if (column == 0) {
				if (isResize(p, column)) {
					return column;
				}
			} else {
				if (isResize(p, column - 1)) {
					return column - 1;
				} else if (isResize(p, column)) {
					return column;
				}
			}
			return -1;
		}

		/**
		 * Returns a boolean indicating if the point is in the resize area.
		 * 
		 * @param p The mouse event point.
		 * @return A boolean indicating if the point is in the resize area.
		 */
		private boolean isResize(Point p) {
			int column = getTableHeader().columnAtPoint(p);
			Rectangle r = getTableHeader().getHeaderRect(column);
			if (p.x < r.x + resizeMargin || p.x > (r.x + r.width) - resizeMargin) {
				return true;
			}
			return false;
		}

		/**
		 * Returns a boolean indicating if the point is in the resize area in the right of the column.
		 * 
		 * @param p The point.
		 * @param column The column number.
		 * @return A boolean.
		 */
		private boolean isResize(Point p, int column) {
			Rectangle r = getTableHeader().getHeaderRect(column);
			int xRight = r.x + r.width;
			int x = p.x;
			if (x > xRight - resizeMargin && x < xRight + resizeMargin) {
				return true;
			}
			return false;
		}
	}

	/**
	 * The selection listener to notify record selections.
	 */
	class SelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			List<Record> records = getSelectedRecords();
			if (records.isEmpty()) {
				return;
			}
			TableRecordEvent tableRecordEvent = new TableRecordEvent(e.getSource(), records);
			for (TableRecordListener listener : tableRecordListeners) {
				listener.select(tableRecordEvent);
			}
		}
	}

	/**
	 * The working session.
	 */
	private Session session;
	/**
	 * The list of table record listeners.
	 */
	private List<TableRecordListener> tableRecordListeners = new ArrayList<>();
	/**
	 * The header mouse handler.
	 */
	private HeaderMouseHandler headerMouseHandler = new HeaderMouseHandler();
	/**
	 * A boolean that indicates if sorting clicking the header is enabled.
	 */
	private boolean sortingEnabled = true;
	/**
	 * The number of rows to process to adjust column sizes.
	 */
	private int adjustColumnSizesRows = 100;

	/**
	 * Constructor with default list selection mode multiple interval.
	 * 
	 * @param session The working session.
	 */
	public JTableRecord(Session session) {
		this(session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param selectionMode The list selection mode.
	 */
	public JTableRecord(Session session, int selectionMode) {
		super();
		this.session = session;

		// Default selection model.
		setSelectionMode(selectionMode);
		// Selection listener.
		getSelectionModel().addListSelectionListener(new SelectionHandler());

		// Auto-resize mode.
		setAutoResizeMode(AUTO_RESIZE_OFF);
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
	 * Returns a boolean that indicates if sorting clicking the header is enabled.
	 * 
	 * @return A boolean that indicates if sorting clicking the header is enabled.
	 */
	public boolean isSortingEnabled() {
		return sortingEnabled;
	}

	/**
	 * Sets a boolean that indicates if sorting clicking the header is enabled.
	 * 
	 * @param sortingEnabled A boolean.
	 */
	public void setSortingEnabled(boolean sortingEnabled) {
		this.sortingEnabled = sortingEnabled;
	}

	/**
	 * Returns the number of rows processed to adjust column sizes. -1 all rows.
	 * 
	 * @return The number of rows processed to adjust column sizes.
	 */
	public int getAdjustColumnSizesRows() {
		return adjustColumnSizesRows;
	}

	/**
	 * Set the number of rows processed to adjust column sizes. -1 all.
	 * 
	 * @param adjustColumnSizesRows The number of rows processed to adjust column sizes.
	 */
	public void setAdjustColumnSizesRows(int adjustColumnSizesRows) {
		this.adjustColumnSizesRows = adjustColumnSizesRows;
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
	 * Set the table model. If the model set is not a <code>TableModelRecord</code>, it throws a
	 * <code>ClassCastException</code>.
	 * 
	 * @param tableModel The table model.
	 */
	@Override
	public void setModel(TableModel tableModel) {
		// Check that the table model is an instance of TableModelRecord.
		if (!(tableModel instanceof TableModelRecord)) {
			if (getModel() == null) {
				return;
			}
			throw new ClassCastException("The table model must be an instance of TableModelRecord");
		}
		super.setModel(tableModel);

		// Working with the table model record.
		TableModelRecord tableModelRecord = (TableModelRecord) tableModel;

		// Add table record listeners.
		tableRecordListeners.clear();
		for (TableRecordListener listener : tableRecordListeners) {
			tableModelRecord.addTableRecordListener(listener);
		}

		// Configure renderers and editors.
		for (int column = 0; column < tableModelRecord.getColumnCount(); column++) {
			Field field = tableModelRecord.getField(column);
			TableColumn tableColumn = getColumnModel().getColumn(column);
			tableColumn.setModelIndex(column);

			// Set the cell renderer
			JTableRecordCellRenderer cellRenderer = new JTableRecordCellRenderer(getSession(), field);
			tableColumn.setCellRenderer(cellRenderer);

			// Set the header.
			tableColumn.setHeaderValue(field.getDisplayHeader());

			// Set the cell editor.
			// EditContext editContext = new EditContext(getSession());
			// editContext.setRecord(tableModelRecord.getMasterRecord());
			// editContext.setAlias(field.getAlias());

			// DefaultCellEditor cellEditor = null;
			// if (editContext.getField().isBoolean()) {
			// cellEditor = new DefaultCellEditor((JCheckBoxField) editContext.getEditField().getComponent());
			// } else if (editContext.getField().isPossibleValues()) {
			// cellEditor = new DefaultCellEditor((JComboBoxField) editContext.getEditField().getComponent());
			// } else {
			// cellEditor = new DefaultCellEditor((JFormattedTextField) editContext.getEditField().getComponent());
			// }
			// cellEditor.setClickCountToStart(1);
			// tableColumn.setCellEditor(cellEditor);
		}

		// Set the listener to the header if the model is not Page.
		getTableHeader().removeMouseListener(headerMouseHandler);
		if (!(tableModelRecord.getRecordSet() instanceof PageRecordSet)) {
			getTableHeader().addMouseListener(headerMouseHandler);
		}

		// Adjust the column sizes to correctly display all data.
		adjustColumnSizes();

		// By default select the first column.
		if (tableModelRecord.getRowCount() > 0) {
			getSelectionModel().setSelectionInterval(0, 0);
		}

	}

	/**
	 * Returns the cell renderer properly configurated.
	 * 
	 * @param row The row.
	 * @param column The column.
	 * @return The cell renderer.
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableModelRecord model = getTableModelRecord();
		Value value = null;
		try {
			value = model.getRecord(row).getValue(column);
		} catch (Exception exc) {
		}
		if (value != null) {
			Color backgroundColor = value.getBackgroundColor();
			Color foregroundColor = value.getForegroundColor();
			if (backgroundColor != null || foregroundColor != null) {
				JTableRecordCellRenderer renderer = new JTableRecordCellRenderer(getSession(), model.getField(column));
				if (value.getBackgroundColor() != null) {
					renderer.setBackground(value.getBackgroundColor());
				}
				if (value.getForegroundColor() != null) {
					renderer.setForeground(value.getForegroundColor());
				}
				return renderer;
			}
		}
		return super.getCellRenderer(row, column);
	}

	/**
	 * Returns the table model record.
	 * 
	 * @return The table model record.
	 */
	public TableModelRecord getTableModelRecord() {
		return (TableModelRecord) getModel();
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @return The list of selected records.
	 */
	public List<Record> getSelectedRecords() {
		List<Record> selectedRecords = new ArrayList<>();
		int[] rows = getSelectedRows();
		TableModelRecord tableModelRecord = getTableModelRecord();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			Record record = tableModelRecord.getRecord(row);
			selectedRecords.add(record);
		}
		return selectedRecords;
	}

	/**
	 * Returns the first selected record or null if none is selected.
	 * 
	 * @return The first selected record or null if none is selected.
	 */
	public Record getSelectedRecord() {
		List<Record> selectedRecords = getSelectedRecords();
		if (!selectedRecords.isEmpty()) {
			return selectedRecords.get(0);
		}
		return null;
	}

	/**
	 * Adjust the columns sizes used when filtering, setting the entities, or changing the model.
	 */
	public void adjustColumnSizes() {
		int columnCount = getTableModelRecord().getColumnCount();
		for (int column = 0; column < columnCount; column++) {
			adjustColumnSize(column);
		}
	}

	/**
	 * Adjust the column size.
	 * 
	 * @param column The column number.
	 */
	public void adjustColumnSize(int column) {
		Field field = getTableModelRecord().getField(column);
		TableColumn tableColumn = getColumnModel().getColumn(column);
		DefaultTableCellRenderer cellRenderer = (DefaultTableCellRenderer) tableColumn.getCellRenderer();

		boolean widthSet = false;

		// If the field is not auto-size, check minimum, maximum and preferred sizes.
		if (!field.isAutoSize()) {
			if (field.getMinimumWidth() > 0) {
				tableColumn.setMinWidth(field.getMinimumWidth());
				widthSet = true;
			}
			if (field.getMaximumWidth() > 0) {
				tableColumn.setMaxWidth(field.getMaximumWidth());
				widthSet = true;
			}
			if (field.getPreferredWidth() > 0) {
				tableColumn.setPreferredWidth(field.getPreferredWidth());
				widthSet = true;
			}
		}

		// Otherwise, compute the with from the header and the values of the table.
		if (!widthSet || field.isAutoSize()) {
			Font font = cellRenderer.getFont();
			if (font != null) {
				FontMetrics metrics = getFontMetrics(font);

				// Reasonable insets for the header and the data.
				int headerInsets = 30;
				int dataInsets = 10;

				// Compute the width of the header with a reasonable margin.
				String header = field.getDisplayHeader();
				if (header == null) {
					header = "";
				}
				int headerWidth = 0;
				if (header.indexOf("<html>") >= 0) {
					String[] headers = splitHeader(header);
					for (String s : headers) {
						int width = metrics.stringWidth(s);
						if (width > headerWidth) {
							headerWidth = width;
						}
					}
					headerWidth += headerInsets;
				} else {
					headerWidth = metrics.stringWidth(header) + headerInsets;
				}

				// Compute the width of the data column.
				int dataWidth = 0;
				int count = 0;
				if (getRowCount() > adjustColumnSizesRows && adjustColumnSizesRows > 0) {
					count = adjustColumnSizesRows;
				} else if (getRowCount() > 0) {
					count = getRowCount();
				}
				for (int row = 0; row < count; row++) {
					Object value = getValueAt(row, column);
					if (value instanceof String) {
						String svalue = (String) value;
						dataWidth = Math.max(dataWidth, metrics.stringWidth(svalue) + dataInsets);
					}
				}

				// Set the preferred width as the maximum of both widths.
				tableColumn.setPreferredWidth(Math.max(dataWidth, headerWidth));
			}
		}
	}

	/**
	 * Set the argument record as the selected record.
	 * 
	 * @param record The record to select.
	 */
	public void setSelectedRecord(Record record) {
		setSelectedRecords(ListUtils.asList(record));
	}

	/**
	 * Set the argument list of records as the list of selected records.
	 * 
	 * @param records The list of records to select.
	 */
	public void setSelectedRecords(List<Record> records) {
		ListSelectionModel selectionModel = getSelectionModel();
		selectionModel.clearSelection();
		for (Record record : records) {
			int index = getTableModelRecord().indexOf(record);
			if (index >= 0) {
				selectionModel.addSelectionInterval(index, index);
			}
		}
		ensureSelectionVisible();
	}

	/**
	 * Set the row as selected.
	 * 
	 * @param row The row to select.
	 */
	public void setSelectedRow(int row) {
		if (row < 0) {
			row = 0;
		}
		if (row >= getTableModelRecord().getRowCount()) {
			row = getTableModelRecord().getRowCount() - 1;
		}
		setSelectedRows(row);
	}

	/**
	 * Set the list of rows as the selected rows.
	 * 
	 * @param rows The list of rows.
	 */
	public void setSelectedRows(int... rows) {
		setSelectedRows(ListUtils.asList(rows));
	}

	/**
	 * Set the list of rows as the selected rows.
	 * 
	 * @param rows The list of rows.
	 */
	public void setSelectedRows(List<Integer> rows) {
		ListSelectionModel selectionModel = getSelectionModel();
		selectionModel.clearSelection();
		for (Integer row : rows) {
			selectionModel.addSelectionInterval(row, row);
		}
		ensureSelectionVisible();
	}

	/**
	 * Ensure that the selection is visible.
	 */
	private void ensureSelectionVisible() {
		ListSelectionModel selectionModel = getSelectionModel();
		int index = selectionModel.getMinSelectionIndex();
		if (index >= 0) {
			selectionModel.setAnchorSelectionIndex(index);
			ensureRowVisible(index);
		}
	}

	/**
	 * Check if a row is visible.
	 * 
	 * @param rowIndex The index position of the row.
	 * @return <code>true</code> if the row is visible.
	 */
	public boolean isRowVisible(int rowIndex) {
		Container parent = getParent();
		if (!(parent instanceof JViewport)) {
			return true;
		}
		JViewport viewport = (JViewport) parent;
		Rectangle rect = getCellRect(rowIndex, 0, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		rect.x = 0;
		rect.width = 1;
		return new Rectangle(viewport.getExtentSize()).contains(rect);
	}

	/**
	 * Ensures that a given row is visible.
	 * 
	 * @param rowIndex The index position of the row.
	 */
	public void ensureRowVisible(int rowIndex) {
		if (isRowVisible(rowIndex)) {
			return;
		}
		Rectangle rect = getCellRect(rowIndex, 0, true);
		scrollRectToVisible(rect);
	}

	/**
	 * Splits the header into an array of lines.
	 * 
	 * @param header The header to split.
	 * @return An array of strings
	 */
	private String[] splitHeader(String header) {
		int index = header.indexOf("<html>");
		if (index >= 0) {
			header = header.substring(index + 6);
		}
		index = header.indexOf("</html>");
		if (index >= 0) {
			header = header.substring(0, index);
		}
		return StringUtils.split(header, "<br>");
	}

	/**
	 * Perform sorting when the header is clicked.
	 * 
	 * @param column The column.
	 * @param controlMask A boolean that indicates if the control mask applies.
	 */
	private void sort(int column, boolean controlMask) {
		if (!getTableModelRecord().getRecordSet().isSortable()) {
			return;
		}
		Field field = getTableModelRecord().getField(getColumnModel().getColumn(column).getModelIndex());
		Order order = getTableModelRecord().getOrder();

		// Not control mask: set the order from the column or switch asc/desc.
		if (!controlMask) {
			if (order.size() == 1 && order.contains(field)) {
				order.set(field, !order.get(field).isAsc());
			} else {
				order.clear();
				order.add(field, true);
			}
		}

		// Control mask: add the column to the order or switch asc/desc.
		if (controlMask) {
			if (order.contains(field)) {
				order.set(field, !order.get(field).isAsc());
			} else {
				order.add(field, true);
			}
		}

		getTableModelRecord().sort(order);
	}

}
