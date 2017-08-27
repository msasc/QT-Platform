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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuKeyEvent;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.event.KeyHandler;
import com.qtplaf.library.swing.event.MenuKeyHandler;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * An option dialog in the fashion of the <code>JOptionPane</code>, more friendly and under the total control of this
 * library.
 * 
 * @author Miquel Sas
 */
public class JOptionDialog extends JDialogSession {
	
	/**
	 * Menu key listener to avoid closing all when exiting with escape a popup menu.
	 */
	class MenuKeyAdapter extends MenuKeyHandler {
		@Override
		public void menuKeyPressed(MenuKeyEvent e) {
			int keyCode = e.getKeyCode();
			int modifiers = e.getModifiers();
			if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
				escapeFromPopupMenu = true;
			}
		}
	}
	
	/**
	 * Mouse adapter to show the popup menu if available.
	 */
	class PopupTrigger extends MouseHandler {
		@Override
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}
		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JPopupMenu popupMenu = getPanelButtons().getPopupMenu();
				SwingUtils.installMenuKeyListener(popupMenu, new MenuKeyAdapter());
				if (!SwingUtils.isEmpty(popupMenu)) {
					popupMenu.show(e.getComponent(),e.getX(),e.getY());
				}
			}
		}
	}


	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowGainedFocus(WindowEvent e) {

			// Set focus to the initial option.
			Action initialAction = actions.get(0);
			if (initialOption != null) {
				for (Action action : actions) {
					if (ActionUtils.getSourceName(action).equals(initialOption)) {
						initialAction = action;
						break;
					}
				}
			}
			JButton button = ActionUtils.getButton(initialAction);
			button.requestFocus();
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// First look for the default close action.
			for (Action action : actions) {
				if (ActionUtils.isDefaultCloseAction(action)) {
					ActionUtils.getButton(action).doClick();
					return;
				}
			}
			// Not found, strait close.
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Action to close and set the selected option.
	 */
	class ActionClose extends AbstractAction {

		/**
		 * Constructor.
		 */
		ActionClose() {
			super();
		}

		/**
		 * Perform the action, just close the window.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			selectedOption = ActionUtils.getSourceName(this);
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Key adapter.
	 */
	class KeyAdapter extends KeyHandler {
		/**
		 * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a
		 * key released event.
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			
			// If escaped from popup menu do not continue.
			if (escapeFromPopupMenu) {
				escapeFromPopupMenu = false;
				return;
			}

			// Key data.
			int keyCode = e.getKeyCode();
			int modifiers = e.getModifiers();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);

			// Get all buttons to process the key on them.
			List<JButton> buttons = SwingUtils.getAllButtons(JOptionDialog.this);

			// Accelerator key.
			for (JButton button : buttons) {
				Action action = button.getAction();
				if (action != null) {
					KeyStroke acceleratorKey = ActionUtils.getAcceleratorKey(action);
					if (acceleratorKey != null && acceleratorKey.equals(keyStroke)) {
						e.consume();
						button.doClick();
						return;
					}
				}
			}

			// Escape.
			if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
				e.consume();
				setVisible(false);
				dispose();
				return;
			}

			// Enter.
			if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
				e.consume();
				for (JButton button : buttons) {
					if (button.hasFocus()) {
						button.doClick();
						break;
					}
				}
				return;
			}
		}
	}

	/**
	 * Optional icon.
	 */
	private Icon icon;
	/**
	 * The component to show.
	 */
	private JComponent component;
	/**
	 * The list of actions.
	 */
	private List<Action> actions = new ArrayList<>();
	/**
	 * Initial option.
	 */
	private String initialOption;
	/**
	 * Selected option.
	 */
	private String selectedOption;
	/**
	 * A boolean to check if escaped from the popup menu.
	 */
	private boolean escapeFromPopupMenu = false;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JOptionDialog(Session session) {
		super(session);
		setWindowHandler(new WindowAdapter());
		setModal(true);
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param owner The window that owns this dialog.
	 */
	public JOptionDialog(Session session, Window owner) {
		super(session, owner);
		setWindowHandler(new WindowAdapter());
		setModal(true);
	}

	/**
	 * Add an option to the list of options.
	 * 
	 * @param option The option to add.
	 */
	public void addOption(String option) {
		addOption(option, null, null, null, false);
	}

	/**
	 * Add an option to the list of options.
	 * 
	 * @param option The option to add.
	 * @param defaultClose A boolean that indicates if the option is the default close option.
	 */
	public void addOption(String option, boolean defaultClose) {
		addOption(option, null, null, null, defaultClose);
	}

	/**
	 * Adds an option.
	 * 
	 * @param option The option name or string.
	 * @param description The optional option description.
	 * @param smallIcon The optional option small icon.
	 * @param acceleratorKey The optional option accelerator key.
	 * @param defaultClose A boolean that indicates if the option is the default close option.
	 */
	public void addOption(
		String option,
		String description,
		Icon smallIcon,
		KeyStroke acceleratorKey,
		boolean defaultClose) {

		if (option == null) {
			throw new NullPointerException("The option can not be null");
		}
		Action action = new ActionClose();
		ActionUtils.setSourceName(action, option);
		if (description != null) {
			ActionUtils.setShortDescription(action, description);
		}
		if (smallIcon != null) {
			ActionUtils.setSmallIcon(action, smallIcon);
		}
		if (acceleratorKey != null) {
			ActionUtils.setAcceleratorKey(action, acceleratorKey);
		}
		ActionUtils.setSession(action, getSession());
		ActionUtils.setActionGroup(action, ActionGroup.EDIT);
		ActionUtils.setDefaultCloseAction(action, defaultClose);

		actions.add(action);
	}

	/**
	 * Add an option in the for of an action that is expected to have a name or source name at least. It can also have
	 * the common attributes, short description, accelerator key, action group.
	 * <p>
	 * This option dialog is set as the user object, so it can be retrieved when the action is executed.
	 * 
	 * @param action The action. One of them should be the default close action.
	 */
	public void addOption(Action action) {
		ActionUtils.setSession(action, getSession());
		ActionUtils.setUserObject(action, this);
		actions.add(action);
	}

	/**
	 * Returns the optional icon.
	 * 
	 * @return The optional icon.
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Sets the optional icon.
	 * 
	 * @param icon The optional icon.
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * Returns the initial option.
	 * 
	 * @return The initial option.
	 */
	public String getInitialOption() {
		return initialOption;
	}

	/**
	 * Sets the initial option.
	 * 
	 * @param initialOption The initial option.
	 */
	public void setInitialOption(String initialOption) {
		this.initialOption = initialOption;
	}

	/**
	 * Returns the component to be shown.
	 * 
	 * @return The component to be shown.
	 */
	public JComponent getComponent() {
		return component;
	}

	/**
	 * Sets the component to be shown.
	 * 
	 * @param component The component to be shown.
	 */
	public void setComponent(JComponent component) {
		this.component = component;
	}

	/**
	 * Sets the message to be shown in a <code>JTextArea</code>.
	 * 
	 * @param message The message.
	 */
	public void setMessage(String message) {
		setMessage(message, 0, false);
	}

	/**
	 * Sets the message to be shown in a <code>JTextArea</code>.
	 * 
	 * @param message The message.
	 * @param columns The number of columns of the text area.
	 * @param lineWrap The line wrap policy.
	 */
	public void setMessage(String message, int columns, boolean lineWrap) {
		JTextArea textArea = new JTextArea();
		if (columns > 0) {
			if (message.length() > columns) {
				textArea.setColumns(columns);
			} else {
				textArea.setColumns(message.length());
			}
		}
		textArea.setLineWrap(lineWrap);
		textArea.setWrapStyleWord(true);
		textArea.setText(message);
		textArea.setOpaque(false);
		textArea.setEditable(false);
		textArea.setFont(new Font(textArea.getFont().getName(), Font.BOLD, textArea.getFont().getSize()));
		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.component = scrollPane;
	}

	/**
	 * Returns the message if the component is a simple message.
	 * 
	 * @return The message.
	 */
	public String getMessage() {
		if (component instanceof JScrollPane) {
			JScrollPane scrollPane = (JScrollPane) component;
			if (scrollPane.getViewport().getView() instanceof JTextArea) {
				JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
				return textArea.getText();
			}
		}
		return null;
	}

	/**
	 * RunTickers the dialog and return the selected option or null if the dialog was closed without selecting any option.
	 * 
	 * @return The selected option.
	 */
	public String showDialog() {
		return showDialog(false);
	}

	/**
	 * Returns the panel buttons.
	 * 
	 * @return The panel buttons.
	 */
	public JPanelButtons getPanelButtons() {
		List<Component> components = SwingUtils.getAllComponents(this);
		for (Component component : components) {
			if (component instanceof JPanelButtons) {
				return (JPanelButtons) component;
			}
		}
		throw new IllegalStateException("Panel buttons not set.");
	}

	/**
	 * RunTickers the dialog and return the selected option or null if the dialog was closed without selecting any option.
	 * 
	 * @param resizable A boolean that indicates if the dialog is resizable.
	 * @return The selected option.
	 */
	public String showDialog(boolean resizable) {

		// Validate that component and actions has been set.
		if (component == null || actions.isEmpty()) {
			throw new IllegalStateException("Component/message and actions must be set.");
		}

		// Content pane layout.
		JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		// Icon if set.
		if (icon != null) {
			GridBagConstraints constraintsIcon = new GridBagConstraints();
			constraintsIcon.anchor = GridBagConstraints.CENTER;
			constraintsIcon.fill = GridBagConstraints.NONE;
			constraintsIcon.gridheight = 1;
			constraintsIcon.gridwidth = 1;
			constraintsIcon.gridx = 0;
			constraintsIcon.gridy = 0;
			constraintsIcon.insets = new Insets(10, 5, 0, 0);
			constraintsIcon.weightx = 1;
			constraintsIcon.weighty = 1;

			JLabel labelIcon = new JLabel(icon);
			labelIcon.setIconTextGap(0);

			add(labelIcon, constraintsIcon);
		}

		// Component.
		GridBagConstraints constraintsComponent = new GridBagConstraints();
		constraintsComponent.anchor = GridBagConstraints.CENTER;
		constraintsComponent.fill = GridBagConstraints.BOTH;
		constraintsComponent.gridheight = 1;
		constraintsComponent.gridwidth = 1;
		constraintsComponent.gridx = (icon == null ? 0 : 1);
		constraintsComponent.gridy = 0;
		constraintsComponent.insets = new Insets(5, 5, 5, (icon != null ? 15 : 5));
		constraintsComponent.weightx = 1;
		constraintsComponent.weighty = 1;

		add(component, constraintsComponent);

		// Options (buttons).
		GridBagConstraints constraintsButtons = new GridBagConstraints();
		constraintsButtons.anchor = GridBagConstraints.NORTH;
		constraintsButtons.fill = GridBagConstraints.HORIZONTAL;
		constraintsButtons.gridheight = 1;
		constraintsButtons.gridwidth = (icon == null ? 1 : 2);
		constraintsButtons.gridx = 0;
		constraintsButtons.gridy = 1;
		constraintsButtons.insets = new Insets(0, 5, 5, 5);
		constraintsButtons.weightx = 1;
		constraintsButtons.weighty = 0;

		JPanelButtons panelButtons = new JPanelButtons();
		for (Action action : actions) {
			panelButtons.add(action);
		}

		add(panelButtons, constraintsButtons);

		// Install key listener.
		KeyAdapter keyAdapter = new KeyAdapter();
		SwingUtils.installKeyListener(this, keyAdapter, true);

		// Pack if not a suitable size defined and center on screen.
		if (getSize().getWidth() == 0 || getSize().getHeight() == 0) {
			pack();
		}
		SwingUtils.centerOnScreen(this);

		// RunTickers it.
		setResizable(resizable);
		setVisible(true);
		requestFocus();

		return selectedOption;
	}
}
