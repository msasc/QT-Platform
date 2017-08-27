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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.event.KeyHandler;

/**
 * Combines a <code>JMaskedField</code> and a <code>JButton</code> on a <code>JPanel</code> to produce a component
 * that's an entry field with an associated action button.
 * 
 * @author Miquel Sas
 */
public class JMaskedFieldButton extends JPanel implements EditField {

	/**
	 * The key listener that fires key down on the edit control and the button.
	 */
	private class KeyDown extends KeyHandler {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				getButton().doClick();
			}
		}
	}

	/**
	 * The action listener that will listen the button to fire the lookup action.
	 */
	private class LookupActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (getEditContext().getActionLookup() != null) {
				getEditContext().getActionLookup().actionPerformed(e);
			}
		}
	}

	/**
	 * The <code>JMaskedField</code>.
	 */
	private JMaskedField maskedField;
	/**
	 * The <code>JButton</code>.
	 */
	private JButton button;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JMaskedFieldButton(EditContext editContext) {
		super();
		maskedField = new JMaskedField(editContext);

		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints constraintsField = new GridBagConstraints();
		constraintsField.gridx = 0;
		constraintsField.gridy = 0;
		constraintsField.fill = GridBagConstraints.HORIZONTAL;
		constraintsField.weightx = 1.0;
		add(getMaskedField(), constraintsField);

		GridBagConstraints constraintsButton = new GridBagConstraints();
		constraintsButton.gridx = 1;
		constraintsButton.gridy = 0;
		add(getButton(), constraintsButton);

		KeyDown keyDown = new KeyDown();
		getMaskedField().addKeyListener(keyDown);
	}

	/**
	 * Adds a key listener to both the masked field and the button.
	 * 
	 * @param l The listener.
	 */
	@Override
	synchronized public void addKeyListener(KeyListener l) {
		getMaskedField().addKeyListener(l);
		getButton().addKeyListener(l);
	}

	/**
	 * Returns the <code>JButton</code>.
	 * 
	 * @return The <code>JButton</code>.
	 */
	public JButton getButton() {
		if (button == null) {
			// Get the width/height from the underlying JFormattedTextField
			int height = getMaskedField().getTextField().getPreferredSize().height - 1;
			int width = height;
			Dimension size = new Dimension(width, height);
			button = new JButton();
			button.setIcon(new IconArrow(IconArrow.Direction.Down));
			button.setMinimumSize(size);
			button.setMaximumSize(size);
			button.setPreferredSize(size);
			button.setMargin(new Insets(0, 0, 0, 0));
			button.addActionListener(new LookupActionListener());
			button.setFocusable(false);
		}
		return button;
	}

	/**
	 * Returns the <code>JMaskedField</code>.
	 * 
	 * @return The <code>JMaskedField</code>.
	 */
	public JMaskedField getMaskedField() {
		return maskedField;
	}

	/**
	 * Returns the associated edit context.
	 * 
	 * @return The edit context.
	 */
	@Override
	public EditContext getEditContext() {
		return getMaskedField().getEditContext();
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
	 * Clear the control with its default data.
	 */
	@Override
	public void clear() {
		getMaskedField().clear();
	}

	/**
	 * Get the value from the component, not the record.
	 * 
	 * @return The value.
	 */
	@Override
	public Value getValue() {
		return getMaskedField().getValue();
	}

	/**
	 * Update the value in the component, not the record.
	 * 
	 * @param value The value to set.
	 */
	@Override
	public void setValue(Value value) {
		getMaskedField().setValue(value);
	}

	/**
	 * Update the value in the component, not the record, controlling whether value actions should be fired.
	 * 
	 * @param value The value to set.
	 * @param fireValueActions A boolean stating if value actions should be fired.
	 */
	@Override
	public void setValue(Value value, boolean fireValueActions) {
		getMaskedField().setValue(value, fireValueActions);
	}

	/**
	 * Returns a boolean indicating if the underlying component is enabled.
	 * 
	 * @return A boolean indicating if the underlying component is enabled.
	 */
	@Override
	public boolean isEnabled() {
		return getMaskedField().isEnabled();
	}

	/**
	 * Sets a boolean indicating if the underlying component is enabled.
	 * 
	 * @param enabled A boolean indicating if the underlying component is enabled.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getMaskedField().setEnabled(enabled);
		getButton().setEnabled(enabled);
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
