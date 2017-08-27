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
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * Normal <code>TableCellRenderer</code> for a <code>JTableRecord</code>.
 * 
 * @author Miquel Sas
 */
public class JTableRecordCellRenderer extends DefaultTableCellRenderer {

	/**
	 * The image icon used to display images.
	 */
	private final ImageIcon icon = new ImageIcon();
	/**
	 * Icon checked for boolean values.
	 */
	private ImageIcon iconChecked;
	/**
	 * Icon unchecked for boolean values.
	 */
	private ImageIcon iconUnchecked;
	/**
	 * Color selection foreground.
	 */
	private transient Color selectionForeground;
	/**
	 * Color selection background.
	 */
	private transient Color selectionBackground;
	/**
	 * Color focused foreground.
	 */
	private transient Color focusedForeground;
	/**
	 * Color focused background.
	 */
	private transient Color focusedBackground;
	/**
	 * The underlying field.
	 */
	private Field field;
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Default constructor.
	 * 
	 * @param session The working session.
	 * @param field The parent field.
	 */
	public JTableRecordCellRenderer(Session session, Field field) {
		super();
		this.session = session;
		this.field = field;
		setHorizontalAlignment(field.getHorizontalAlignment().getSwingAlignment());
		this.iconChecked = ImageIconUtils.getImageIcon(Icons.app_16x16_checked);
		this.iconUnchecked = ImageIconUtils.getImageIcon(Icons.app_16x16_unchecked);
	}

	/**
	 * Returns the component used for drawing the cell.
	 * 
	 * @param table the <code>JTable</code> that is asking the renderer to draw; can be <code>null</code>
	 * @param value the value of the cell to be rendered. It is up to the specific renderer to interpret and draw the
	 *        value. For example, if <code>value</code> is the string "true", it could be rendered as a string or it
	 *        could be rendered as a check box that is checked. <code>null</code> is a valid value
	 * @param isSelected true if the cell is to be rendered with the selection highlighted; otherwise false
	 * @param hasFocus if true, render cell appropriately. For example, put a special border on the cell, if the cell
	 *        can be edited, render in the color used to indicate editing
	 * @param row the row index of the cell being drawn. When drawing the header, the value of <code>row</code> is -1
	 * @param column the column index of the cell being drawn
	 */
	@Override
	public Component getTableCellRendererComponent(
		JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (isSelected) {
			if (selectionForeground != null) {
				super.setForeground(selectionForeground);
			}
			if (selectionBackground != null) {
				super.setBackground(selectionBackground);
			}
			if (hasFocus) {
				if (focusedForeground != null) {
					super.setForeground(focusedForeground);
				}
				if (focusedBackground != null) {
					super.setBackground(focusedBackground);
				}
			}
		}

		if (value instanceof Image) {
			setText(null);
			final Image image = (Image) value;
			icon.setImage(image);
			setIcon(icon);
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);
			int rowHeight = image.getHeight(null);
			if (rowHeight > table.getRowHeight(row)) {
				table.setRowHeight(row, rowHeight);
			}

		} else if (value instanceof Boolean) {
			if (field.isEditBooleanInCheckBox()) {
				setText("");
				if ((Boolean) value)
					icon.setImage(iconChecked.getImage());
				else
					icon.setImage(iconUnchecked.getImage());
				setIcon(icon);
				setHorizontalAlignment(SwingConstants.CENTER);
				setVerticalAlignment(SwingConstants.CENTER);
			} else {
				Boolean b = (Boolean)value;
				if (b) {
					setText(session.getString("tokenYes"));
				} else {
					setText(null);
				}
			}
		} else {
			setIcon(null);
		}
		return this;
	}

	/**
	 * Sets the selection foreground color.
	 * 
	 * @param selectionForeground The color.
	 */
	public void setSelectionForeground(Color selectionForeground) {
		this.selectionForeground = selectionForeground;
	}

	/**
	 * Sets the selection background color.
	 * 
	 * @param selectionBackground The color.
	 */
	public void setSelectionBackground(Color selectionBackground) {
		this.selectionBackground = selectionBackground;
	}

	/**
	 * Sets the focused foreground color.
	 * 
	 * @param focusedForeground The color.
	 */
	public void setFocusedForeground(Color focusedForeground) {
		this.focusedForeground = focusedForeground;
	}

	/**
	 * Sets the focused background color.
	 * 
	 * @param focusedBackground The color.
	 */
	public void setFocusedBackground(Color focusedBackground) {
		this.focusedBackground = focusedBackground;
	}
}
