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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.event.MouseHandler;

/**
 * A panel that holds a <code>JTableRecord</code>.
 * 
 * @author Miquel Sas
 */
public class JPanelTableRecord extends JPanel {

	/**
	 * Mouse adapter.
	 */
	class MouseAdapter extends MouseHandler {
		/**
		 * Invoked when a mouse button has been pressed on a component.
		 * 
		 * @param e The mouse event.
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (triggerPopupMenu(e)) {
				return;
			}
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 * 
		 * @param e The mouse event.
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (triggerPopupMenu(e)) {
				return;
			}
		}

		/**
		 * Check and trigger the popup menu.
		 * 
		 * @param e The mouse event.
		 * @return A boolean that indicates if processing the event should stop.
		 */
		private boolean triggerPopupMenu(MouseEvent e) {
			if (e.isPopupTrigger()) {
				for (Action action : actions) {
					ActionUtils.setTableRecordPanel(action, JPanelTableRecord.this);
					ActionUtils.setMousePoint(action, e.getPoint());
				}
				JPopupMenu popupMenu = new JPopupMenu();
				SwingUtils.addMenuItems(popupMenu, actions);
				if (!SwingUtils.isEmpty(popupMenu)) {
					int x = e.getX();
					int y = e.getY();
					popupMenu.show(getTableRecord(), x, y);
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * The listener to handle selection events.
	 */
	class SelectionAdapter implements ListSelectionListener {
		/** The "Line of lines" message. */
		private String lineOfLinesMessage;

		/**
		 * RunTickers the selected line number and number of lines in the status label.
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {

			// RunTickers the selected line number and number of lines in the status label.
			if (tableRecord != null) {
				if (lineOfLinesMessage == null) {
					lineOfLinesMessage = getSession().getString("lineOfLines");
				}
				int row = tableRecord.getSelectedRow();
				int count = tableRecord.getRowCount();
				tableRecordStatusPanel
					.getStatusLabel()
					.setText(MessageFormat.format(lineOfLinesMessage, row + 1, count));
			}
		}
	}

	/** The <code>JTableRecord</code>. */
	private JTableRecord tableRecord;
	/** The <code>JTableRecordStatusPanel</code>. */
	private JTableRecordStatusPanel tableRecordStatusPanel = new JTableRecordStatusPanel();
	/** A boolean that indicates if the status panel should be included. */
	private boolean includeStatusPanel = true;
	/** List of table actions. */
	private List<Action> actions = new ArrayList<>();

	/** The working session. */
	private Session session;

	/**
	 * Constructor assigning the table.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 */
	public JPanelTableRecord(JTableRecord tableRecord) {
		this(tableRecord, true);
	}

	/**
	 * Constructor assigning the table.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 * @param includeStatusPanel A boolean that indicates if the status panel is to be included.
	 */
	public JPanelTableRecord(JTableRecord tableRecord, boolean includeStatusPanel) {
		super(new GridBagLayout());
		this.session = tableRecord.getSession();
		this.includeStatusPanel = includeStatusPanel;
		setTableRecord(tableRecord);
		SwingUtils.installMouseListener(this, new MouseAdapter());
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
	 * Add an action to the list of actions.
	 * 
	 * @param action The action to add.
	 */
	public void addAction(Action action) {
		ActionUtils.setTableRecordPanel(action, this);
		actions.add(action);
	}

	/**
	 * Returns the <code>JTableRecord</code>.
	 * 
	 * @return The <code>JTableRecord</code>.
	 */
	public JTableRecord getTableRecord() {
		return tableRecord;
	}

	/**
	 * Set the <code>JTableRecord</code>.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 */
	public void setTableRecord(JTableRecord tableRecord) {
		this.tableRecord = tableRecord;

		// Set the table listeners.
		SelectionAdapter selectionHandler = new SelectionAdapter();
		this.tableRecord.getSelectionModel().addListSelectionListener(selectionHandler);

		// Install the components in the panel.
		removeAll();
		JScrollPane scrollPane =
			new JScrollPane(
				this.tableRecord,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		super.add(scrollPane, getTableRecordConstraints());
		if (includeStatusPanel) {
			super.add(tableRecordStatusPanel, getStatusPanelConstraints());
		}
	}

	/**
	 * Returns the constraints of the <code>JTableRecord</code>.
	 * 
	 * @return he constraints of the <code>JTableRecord</code>.
	 */
	private GridBagConstraints getTableRecordConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		return constraints;
	}

	/**
	 * Returns the status panel constraints.
	 * 
	 * @return The status panel constraints.
	 */
	private GridBagConstraints getStatusPanelConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(2, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		return constraints;
	}

}
