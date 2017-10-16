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
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;

/**
 * Compound control to manage dates with a calendar panel.
 * 
 * @author Miquel Sas
 */
public class JMaskedFieldDate extends JMaskedFieldButton {
	/**
	 * Event handler.
	 */
	class EventHandler implements PropertyChangeListener, AncestorListener, KeyListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			handlePropertyChange(event);
		}
		@Override
		public void ancestorAdded(AncestorEvent event) {
			handleAncestor(event);
		}
		@Override
		public void ancestorMoved(AncestorEvent event) {
			handleAncestor(event);
		}
		@Override
		public void ancestorRemoved(AncestorEvent event) {
			handleAncestor(event);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			handleKey(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			handleKey(e);
		}
		@Override
		public void keyTyped(KeyEvent e) {
			handleKey(e);
		}
	}

	private EventHandler eventHandler = new EventHandler();

	/**
	 * Event handler.
	 */
	class ButtonAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getButton()) {
				if (getWindowCalendar().isVisible()) {
					closeWindowCalendar();
				} else {
					if (!getEditContext().getValue().isNull()) {
						getPanelCalendar().setCurrentDate(getEditContext().getValue().getDate());
					} else {
						getPanelCalendar().setCurrentDate(JMaskedFieldDate.this.getValue().getDate());
					}
					openWindowCalendar();
				}
			}
			if (e.getSource() == getMaskedField().getTextField()) {
				getPanelCalendar().setCurrentDate(getEditContext().getValue().getDate());
			}
		}
	}

	/** Calendar pane. */
	private JPanelCalendar panelCalendar = null;
	/** Calendar window. */
	private JWindow windowCalendar = null;

	/**
	 * Constructor.
	 * 
	 * @param editContext The edit context.
	 */
	public JMaskedFieldDate(EditContext editContext) {
		super(editContext);
		editContext.setActionLookup(new ButtonAction());
		getButton().addAncestorListener(eventHandler);
	}

	/**
	 * Returns the calendar panel.
	 * 
	 * @return The calendar panel
	 */
	public JPanelCalendar getPanelCalendar() {
		if (panelCalendar == null) {
			panelCalendar = new JPanelCalendar(getSession());
			panelCalendar.setName("PaneCalendarInCombo");
			panelCalendar.setBorder(BorderFactory.createRaisedBevelBorder());
			panelCalendar.addPropertyChangeListener(eventHandler);
			List<Component> components = SwingUtils.getAllComponents(panelCalendar);
			for (Component component : components) {
				component.addKeyListener(eventHandler);
			}
		}
		return panelCalendar;
	}

	/**
	 * Returns the calendar window.
	 * 
	 * @return The calendar window.
	 */
	public JWindow getWindowCalendar() {
		if (windowCalendar == null) {
			Window owner = null;
			Component parent = this;
			while (parent != null) {
				if (parent instanceof Window) {
					owner = (Window) parent;
					break;
				}
				if (parent.getParent() == parent) {
					break;
				}
				parent = parent.getParent();
			}
			windowCalendar = new JWindow(owner);
			windowCalendar.setName("WindowCalendar");
			// windowCalendar.setLayout(new GridBagLayout());

			JPanel contentPane = new JPanel(new GridBagLayout());
			contentPane.setName("JWindowCalendarContentPane");
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.anchor = GridBagConstraints.CENTER;
			contentPane.add(getPanelCalendar(), constraints);

			windowCalendar.setContentPane(contentPane);

			Dimension d = new Dimension(200, 200);
			windowCalendar.setSize(d);
		}
		return windowCalendar;
	}

	/**
	 * Handles the event.
	 * 
	 * @param e The key event.
	 */
	private void handleKey(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (getWindowCalendar().isVisible()) {
				closeWindowCalendar();
			}
		}
	}

	/**
	 * Handles the event.
	 * 
	 * @param e The ancestor event.
	 */
	private void handleAncestor(AncestorEvent e) {
		if (e.getSource() == getButton()) {
			if (getWindowCalendar().isVisible()) {
				openWindowCalendar();
			}
		}
	}

	/**
	 * Handles the event.
	 * 
	 * @param e The property change event.
	 */
	private void handlePropertyChange(PropertyChangeEvent e) {
		if (e.getSource() == getPanelCalendar()) {
			if (e.getNewValue() == null) {
			}
		}
		if (e.getPropertyName() == JPanelCalendar.DATE_CLICKED) {
			setValue(new Value(getPanelCalendar().getCurrentDate()));
			closeWindowCalendar();
		}
	}

	/**
	 * Opens the calendar window.
	 */
	private void openWindowCalendar() {
		getWindowCalendar().pack();
		double butHeight = getButton().getBounds().getHeight();

		double scrHeight = SwingUtils.getScreenSize(getWindowCalendar()).getHeight();
		double scrWidth = SwingUtils.getScreenSize(getWindowCalendar()).getWidth();
		double wndHeight = getWindowCalendar().getSize().getHeight();
		double wndWidth = getWindowCalendar().getSize().getWidth();

		Point pt = this.getButton().getLocationOnScreen();

		// By default left/down the button.
		double y = pt.getY() + butHeight;
		double x = pt.getX();

		if (x + wndWidth > scrWidth) {
			x = scrWidth - wndWidth;
		}
		if (x < 0) {
			x = 0;
		}
		if (y + wndHeight > scrHeight) {
			y = scrHeight - wndHeight;
		}
		if (y < 0) {
			y = 0;
		}
		getWindowCalendar().setLocation((int) x, (int) y);

		getWindowCalendar().pack();
		getWindowCalendar().setVisible(true);
		((IconArrow) getButton().getIcon()).setDirection(IconArrow.Direction.UP);
		getButton().invalidate();
		getButton().repaint();
	}

	/**
	 * Closes the calendar window.
	 */
	private void closeWindowCalendar() {
		((IconArrow) getButton().getIcon()).setDirection(IconArrow.Direction.DOWN);
		getButton().invalidate();
		getButton().repaint();
		getWindowCalendar().setVisible(false);
	}

	/**
	 * Update the value in the component, not the record.
	 * 
	 * @param value The value to set.
	 */
	@Override
	public void setValue(Value value) {
		if (!value.isDate()) {
			throw new IllegalArgumentException("Invalid value type");
		}
		if (!value.isNull()) {
			getPanelCalendar().setCurrentDate(value.getDate());
		}
		super.setValue(value);
	}
}
