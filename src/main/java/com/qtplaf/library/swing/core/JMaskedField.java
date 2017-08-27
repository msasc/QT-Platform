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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.formatters.DateFormatter;
import com.qtplaf.library.swing.formatters.TimeFormatter;
import com.qtplaf.library.swing.formatters.TimestampFormatter;
import com.qtplaf.library.util.Alignment;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * A <i>JFormattedTextField</i> that is comfortable working <i>Field</i>'s and <i>Value</i>'s. To avoid using directly
 * <i>JFormattedTextField</i>, although if provides access to the formatted text field, it extends a <i>JPanel</i> that
 * contains the formatted text field, thus showing a more clear interface.
 * 
 * @author Miquel Sas
 */
public class JMaskedField extends JPanel implements EditField, FocusListener {

	/**
	 * The selector that will select all invoked later.
	 */
	class SelectAll implements Runnable {
		/**
		 * Run it: select all the text in the field.
		 */
		@Override
		public void run() {
			getTextField().selectAll();
		}
	}

	/**
	 * The selector that will select nothing invoked later.
	 */
	class SelectNothing implements Runnable {
		/**
		 * Run it: select nothing in the field.
		 */
		@Override
		public void run() {
			getTextField().select(0, 0);
		}
	}

	/**
	 * Plain string document to handle field properties.
	 */
	class StringDocument extends PlainDocument {

		/**
		 * A boolean that indicates if the text will be uppercase.
		 */
		private boolean uppercase = false;
		/**
		 * The maximum text length.
		 */
		private int maxLength = 0;

		/**
		 * Constructor.
		 * 
		 * @param maxLength The maximum length.
		 * @param uppercase A flag to indicate that content is uppercase.
		 */
		public StringDocument(int maxLength, boolean uppercase) {
			super();
			this.maxLength = maxLength;
			this.uppercase = uppercase;
		}

		/**
		 * Insert the string at the given offset.
		 */
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (uppercase) {
				str = str.toUpperCase();
			}
			String text = getText(0, getLength());
			if (text.length() > 0 && text.length() + str.length() > maxLength) {
				if (offs > maxLength) {
					return;
				}
				super.remove(offs, str.length());
			}
			super.insertString(offs, str, a);
		}
	}

	/**
	 * The associated edit context.
	 */
	private EditContext editContext;
	/**
	 * The underlying formatted text field.
	 */
	private JFormattedTextField textField;
	/**
	 * A label to indicate that this field has multiple values.
	 */
	private JLabel multipleValuesLabel = null;
	/**
	 * Current value. It can be a single value or an array of values.
	 */
	private Value currentValue = null;
	/**
	 * Previous value to control when to fire value actions.
	 */
	private Value previousValue = null;
	/**
	 * The previous text value. Is a text representation of the previous value.
	 */
	private String previousTextValue = null;
	/**
	 * The initial size of this component. The size changes when the component switches to multipe values.
	 */
	private Dimension initialSize = null;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JMaskedField(EditContext editContext) {
		super();
		this.editContext = editContext;
		previousValue = editContext.getField().getDefaultValue();

		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints constraintsField = new GridBagConstraints();
		constraintsField.gridx = 0;
		constraintsField.gridy = 0;
		constraintsField.fill = GridBagConstraints.HORIZONTAL;
		constraintsField.weightx = 1.0;
		constraintsField.weighty = 1.0;
		add(getTextField(), constraintsField);

		GridBagConstraints constraintsLabel = new GridBagConstraints();
		constraintsLabel.gridx = 1;
		constraintsLabel.gridy = 0;
		constraintsLabel.insets.left = 3;
		constraintsLabel.insets.bottom = 1;
		constraintsLabel.insets.right = 3;
		constraintsLabel.fill = GridBagConstraints.NONE;
		constraintsLabel.weightx = 0;
		add(getMultipleValuesLabel(), constraintsLabel);

		try {
			configure();
		} catch (ParseException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * If not initialized, initializes the multple values label, and returns it.
	 * 
	 * @return The multiple values label.
	 */
	public JLabel getMultipleValuesLabel() {
		if (multipleValuesLabel == null) {
			Icon multipleValuesLabelIcon = null;
			try {
				multipleValuesLabelIcon = ImageIconUtils.getImageIcon(Icons.app_16x16_list);
			} catch (Exception exc) {
				exc.printStackTrace();
			}

			multipleValuesLabel = new JLabel(multipleValuesLabelIcon);
			multipleValuesLabel.setVisible(false);
		}
		return multipleValuesLabel;
	}

	/**
	 * Returns the text field.
	 * 
	 * @return The text field component.
	 */
	public JFormattedTextField getTextField() {
		if (textField == null) {
			textField = new JFormattedTextField();
			textField.addFocusListener(this);
		}
		return textField;
	}

	/**
	 * Adds a key listener to the component.
	 * 
	 * @param l The key listener.
	 */
	@Override
	synchronized public void addKeyListener(KeyListener l) {
		getTextField().addKeyListener(l);
	}

	/**
	 * Request focus.
	 */
	@Override
	public void requestFocus() {
		getTextField().requestFocus();
	}

	/**
	 * Returns a boolean indicating if the underlying component is focus owner.
	 */
	@Override
	public boolean isFocusOwner() {
		return getTextField().isFocusOwner();
	}

	/**
	 * When focus gained select the entire text.
	 */
	@Override
	public void focusGained(FocusEvent e) {
		EventQueue.invokeLater(new SelectAll());
	}

	/**
	 * Normally, when focus lost, one would expect to validate the field and launch actions related to value changes.
	 */
	@Override
	public void focusLost(FocusEvent e) {
		EventQueue.invokeLater(new SelectNothing());
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
		return getEditContext().getSession();
	}

	/**
	 * Configure this masked field applying its field properties.
	 * 
	 * @throws ParseException If a parse error occurs.
	 */
	protected void configure() throws ParseException {
		Field field = getEditContext().getField();

		// Allow all text entered.
		getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT);

		// Generic behaviour for alignment.
		getTextField().setHorizontalAlignment(getHorizontalAlignment(field.getHorizontalAlignment()));

		// Set the formatter
		JFormattedTextField.AbstractFormatter formatter = field.getFormatter();
		if (formatter == null) {
			if (field.isNumber()) {
				int length = field.getLength();
				int decimals = field.getDecimals();
				StringBuilder pattern = new StringBuilder("#,##0");
				if (decimals > 0) {
					pattern.append(".0#");
				} else {
					// Case double any decimals
					if (decimals < 0 && field.isDouble()) {
						pattern.append(".0#");
					}
				}
				DecimalFormat format =
					new DecimalFormat(pattern.toString(), DecimalFormatSymbols.getInstance(getSession().getLocale()));

				// Set exactly the number of integer and fractional positions.
				if (length > 0) {
					int integerDigits = length;
					if (decimals > 0) {
						integerDigits = length - decimals - 1;
					}
					format.setMaximumIntegerDigits(integerDigits);
					format.setMinimumIntegerDigits(1);
				}
				if (decimals >= 0) {
					int fractionalDigits = decimals;
					format.setMaximumFractionDigits(fractionalDigits);
					format.setMinimumFractionDigits(fractionalDigits);
				}

				NumberFormatter numberFormatter = new NumberFormatter(format);
				numberFormatter.setAllowsInvalid(false);
				// Case double any decimals
				if (decimals < 0 && field.isDouble()) {
					numberFormatter.setAllowsInvalid(true);
				}
				formatter = numberFormatter;
			} else if (field.isDate()) {
				formatter = new DateFormatter(getSession().getLocale());
			} else if (field.isTime()) {
				formatter = new TimeFormatter(getSession().getLocale(), field.isEditSeconds());
			} else if (field.isTimestamp()) {
				formatter = new TimestampFormatter(getSession().getLocale(), field.isEditSeconds());
			} else if (field.isString()) {
				getTextField().setDocument(new StringDocument(field.getDisplayLength(), field.isUppercase()));
			}
		}
		if (formatter != null) {
			getTextField().setFormatterFactory(new DefaultFormatterFactory(formatter));
		}

		// Set the tool tip.
		getTextField().setToolTipText(field.getToolTip());

		// Set the size.
		Dimension size = new Dimension();
		// Get the height from the underlying JFormattedTextField
		size.height = getTextField().getPreferredSize().height;
		FontMetrics metrics = getFontMetrics(getFont());
		if (field.isAutoSize()) {
			size.width = SwingUtils.getPreferredFieldWidth(metrics, field, getSession().getLocale());
			setPreferredSize(size);
			if (field.isFixedWidth()) {
				setMaximumSize(size);
				setMinimumSize(size);
			}
		} else {
			if (field.getMinimumWidth() > 0) {
				size.width = field.getMinimumWidth();
				setMinimumSize(size);
			}
			if (field.getMaximumWidth() > 0) {
				size.width = field.getMaximumWidth();
				setMaximumSize(size);
			}
			if (field.getPreferredWidth() > 0) {
				size.width = field.getPreferredWidth();
				setPreferredSize(size);
			}
		}
		initialSize = size;
	}

	/**
	 * Returns the horizontal alignment from the field horizontal <i>Alignment</i>.
	 * 
	 * @param alignment The field horizontal <i>Alignment</i>.
	 * @return The <i>JtextField</i> horizontal alignment.
	 */
	private int getHorizontalAlignment(Alignment alignment) {
		switch (alignment) {
		case Left:
			return JTextField.LEFT;
		case Right:
			return JTextField.RIGHT;
		case Center:
			return JTextField.CENTER;
		default:
			throw new IllegalArgumentException("Illegal alignment: " + alignment);
		}
	}

	/**
	 * Returns a boolean indicating if the underlying component is enabled.
	 * 
	 * @return A boolean indicating if the underlying component is enabled.
	 */
	@Override
	public boolean isEnabled() {
		return getTextField().isEnabled();
	}

	/**
	 * Sets a boolean indicating if the underlying component is enabled.
	 * 
	 * @param enabled A boolean indicating if the underlying component is enabled.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getTextField().setEnabled(enabled);
		getMultipleValuesLabel().setEnabled(enabled);
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
		
		if (getEditContext().getField().isNumber()) {
			if (getTextField().getText().equals("")) {
				return getEditContext().getField().getDefaultValue();
			}
		}
		
		if (!getTextField().getText().equals("") && currentValue != null && currentValue.isValueArray()) {
			return currentValue;
		}

		// Commit edit
		try {
			getTextField().commitEdit();
		} catch (ParseException exc) {
			exc.printStackTrace();
		}

		Value value = getEditContext().getField().getDefaultValue();
		JFormattedTextField.AbstractFormatter formatter = getEditContext().getField().getFormatter();
		String text = getTextField().getText();
		try {
			if (formatter != null) {
				text = formatter.stringToValue(getTextField().getText()).toString();
			}
			value.fromStringFormatted(text, getSession().getLocale());
		} catch (ParseException exc) {
			exc.printStackTrace();
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

	/**
	 * Update the value in the component, not the record, controlling whether value actions should be fired.
	 * 
	 * @param value The value to set.
	 * @param fireValueActions A boolean stating if value actions should be fired.
	 */
	@Override
	public void setValue(Value value, boolean fireValueActions) {
		previousValue = getValue();
		currentValue = new Value(value);
		if (!currentValue.equals(previousValue)) {
			setTextFieldValue();
		}
		if (fireValueActions) {
			getEditContext().fireValueActions(this, previousValue, currentValue);
			previousValue = getValue();
			previousTextValue = getTextField().getText();
		}
	}

	/**
	 * Re-define the component setting the multiples values label if the value is a value array.
	 */
	private void redefineComponent() {
		if (currentValue.isValueArray()) {
			multipleValuesLabel.setVisible(true);
			// Resize.
			Dimension size = new Dimension(initialSize);
			size.width += 20;
			setMinimumSize(size);
			setPreferredSize(size);

		} else {
			multipleValuesLabel.setVisible(false);
			// Resize.
			setMinimumSize(initialSize);
			setPreferredSize(initialSize);
		}
	}

	/**
	 * Internally set the text field value.
	 */
	private void setTextFieldValue() {
		previousTextValue = currentValue.toStringFormatted(getSession().getLocale());
		Field field = getEditContext().getField();
		JFormattedTextField.AbstractFormatter formatter = field.getFormatter();
		if (formatter != null) {
			try {
				previousTextValue = formatter.valueToString(currentValue);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (currentValue.isValueArray()) {
			multipleValuesLabel.setVisible(true);
			int length = field.getLength();
			if (previousTextValue.length() > length)
				previousTextValue = previousTextValue.substring(0, length) + "...";
		}
		getTextField().setText(previousTextValue);
		redefineComponent();
		validate();
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
