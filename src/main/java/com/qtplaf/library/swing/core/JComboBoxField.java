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
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;

/**
 * A combo box aimed to edit fields with possible values.
 * 
 * @author Miquel Sas
 */
public class JComboBoxField extends JComboBox<JComboBoxField.Item> implements EditField {

	/**
	 * The associated edit context.
	 */
	private EditContext editContext;

	/**
	 * Items of the combo box.
	 */
	class Item extends Value {

		/**
		 * Constructor.
		 * 
		 * @param v The origin value.
		 */
		Item(Value v) {
			super(v);
		}

		/**
		 * Returns the string representation.
		 * 
		 * @return The string representation.
		 */
		@Override
		public String toString() {
			if (getLabel() != null) {
				return getLabel();
			}
			return toStringFormatted(getSession().getLocale());
		}

		/**
		 * Get the origin value.
		 */
		@Override
		public Value getValue() {
			return new Value(this);
		}

		/**
		 * Check if the argument object isequal to this item.
		 * 
		 * @return A boolean indicating if the argument object isequal to this item.
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Value))
				return false;
			Value v = (Value) o;
			return super.equals(v);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JComboBoxField(EditContext editContext) {
		super();
		this.editContext = editContext;

		Field field = new Field(editContext.getField());
		if (field.isBoolean()) {
			field.addPossibleValues(getBooleanValues(getEditContext().isFilter()));
		}
		List<Value> possibleValues = field.getPossibleValues();
		removeAll();
		for (Value possibleValue : possibleValues) {
			addItem(new Item(possibleValue));
		}
		setToolTipText(field.getToolTip());
		setValue(editContext.getValue(), false);
		setPreferredSize(getPreferredDisplaySize());
		setMaximumSize(getPreferredDisplaySize());
		setMinimumSize(getPreferredDisplaySize());

		setEditable(false);
		setBackground(Color.WHITE);

		updateUI();
	}

	/**
	 * Returns this edit control preferred display size, according to the field properties.
	 * 
	 * @return The preferred display size.
	 */
	public Dimension getPreferredDisplaySize() {
		Dimension size = new Dimension();
		Field field = editContext.getField();
		Locale locale = getSession().getLocale();

		// Get the height from the parent JComboBox.
		size.height = getPreferredSize().height;

		// Get the maximum length of the list of values.
		String maxText = "";
		for (int i = 0; i < getItemCount(); i++) {
			Item item = getItemAt(i);
			String text = item.toStringFormatted(locale);
			if (text.length() > maxText.length()) {
				maxText = text;
			}
		}

		FontMetrics metrics = getFontMetrics(getFont());
		size.width = SwingUtils.getPreferredFieldWidth(metrics, field, locale) + 50;

		return size;
	}

	/**
	 * Returns the list of values when the field is boolean.
	 * 
	 * @param filter A boolean that indicates if this edit field is used in a filter form.
	 * @return The list of values.
	 */
	private List<Value> getBooleanValues(boolean filter) {
		List<Value> values = new ArrayList<>();

		if (filter) {
			Value valueEmpty = new Value((Boolean) null);
			valueEmpty.setLabel("");
			values.add(valueEmpty);
		}

		Value valueYes = new Value(true);
		valueYes.setLabel(getTokenYes());
		values.add(valueYes);

		Value valueNo = new Value(false);
		valueNo.setLabel(getTokenNo());
		values.add(valueNo);

		return values;
	}

	/**
	 * Returns the <i>yes</i> token.
	 * 
	 * @return The token.
	 */
	private String getTokenYes() {
		return getSession().getString("tokenYes");
	}

	/**
	 * Returns the <i>no</i> token.
	 * 
	 * @return The token.
	 */
	private String getTokenNo() {
		return getSession().getString("tokenNo");
	}

	/**
	 * Returns the associated edit context.
	 * 
	 * @return The edit context.
	 */
	@Override
	public EditContext getEditContext() {
		return editContext;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return editContext.getSession();
	}

	/**
	 * Clear the control with its default data.
	 */
	@Override
	public void clear() {
		setValue(getEditContext().getField().getDefaultValue());
	}

	/**
	 * Get the value from the component, not the record.
	 * 
	 * @return The value.
	 */
	@Override
	public Value getValue() {
		Item item = (Item) getSelectedItem();
		Value value;
		if (item == null) {
			if (getEditContext().getField().isBoolean()) {
				value = getEditContext().getField().getDefaultValue();
				if (getEditContext().isFilter()) {
					value.setNull();
				}
			} else {
				value = getEditContext().getField().getDefaultValue();
			}
		} else {
			if (getEditContext().getField().isBoolean()) {
				if (item.equals("")) {
					value = getEditContext().getField().getDefaultValue();
					value.setNull();
				} else if (item.equals(getTokenNo())) {
					value = new Value(false);
				} else {
					value = new Value(true);
				}
			} else {
				value = item.getValue();
			}
		}
		return value;
	}

	/**
	 * Update the value in the component, not the record.
	 * 
	 * @param value The value to set.
	 */
	@Override
	public void setValue(Value value) {
		setValue(value, true);
	}

	@Override
	public void setValue(Value value, boolean fireValueActions) {
		Value previousValue = getValue();
		// Set the selected item.
		Item item;
		if (getEditContext().getField().isBoolean()) {
			if (value.isNull()) {
				item = new Item(new Value(""));
			} else if (value.getBoolean()) {
				item = new Item(new Value(getTokenYes()));
			} else {
				item = new Item(new Value(getTokenNo()));
			}
		} else {
			item = new Item(value);
		}
		boolean selectedIndexSet = false;
		for (int i = 0; i < getItemCount(); i++) {
			if (item.equals(getItemAt(i))) {
				setSelectedIndex(i);
				selectedIndexSet = true;
				break;
			}
		}
		if (getItemCount() > 0 && !selectedIndexSet) {
			setSelectedIndex(0);
		}
		// Fire value actions if required.
		if (fireValueActions) {
			getEditContext().fireValueActions(this, previousValue, value);
		}
	}
	
	/**
	 * Fire state changed.
	 */
	@Override
	protected void fireItemStateChanged(ItemEvent e) {
		super.fireItemStateChanged(e);
		if (e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
			Item previousItem = null;
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				previousItem = (Item)e.getItem();
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Item currentItem = (Item)e.getItem();
				getEditContext().fireValueActions(this,previousItem,currentItem);
			}
		}
	}

	/**
	 * Returns the component that will process the events of this edit field.
	 * 
	 * @return The {@link Component}.
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Returns a string representation of this edit field.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		return SwingUtils.toString(this);
	}
}
