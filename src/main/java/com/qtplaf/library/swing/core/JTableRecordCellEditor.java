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

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * A cell editor for the <code>JTableRecord</code> component, aimed to work with <code>EditField</code> edit components.
 * 
 * @author Miquel Sas
 */
public class JTableRecordCellEditor extends AbstractCellEditor implements TableCellEditor {

	/**
	 * 
	 */
	public JTableRecordCellEditor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the value contained in the editor.
	 * 
	 * @return the value contained in the editor
	 */
	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets an initial <code>value</code> for the editor. This will cause the editor to <code>stopEditing</code> and
	 * lose any partially edited value if the editor is editing when this method is called.
	 * <p>
	 * * Returns the component that should be added to the client's <code>Component</code> hierarchy. Once installed in
	 * the client's hierarchy this component will then be able to draw and receive user input.
	 *
	 * @param table the <code>JTable</code> that is asking the editor to edit; can be <code>null</code>
	 * @param value the value of the cell to be edited; it is up to the specific editor to interpret and draw the value.
	 *        For example, if value is the string "true", it could be rendered as a string or it could be rendered as a
	 *        check box that is checked. <code>null</code> is a valid value
	 * @param isSelected true if the cell is to be rendered with highlighting
	 * @param row the row of the cell being edited
	 * @param column the column of the cell being edited
	 * @return the component for editing
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return null;
	}

}
