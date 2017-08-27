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

package com.qtplaf.library.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JOptionDialog;

/**
 * A generic option dialog.
 *
 * @author Miquel Sas
 */
public class OptionDialog {

	/** Underlying <tt>JOptionDialog</tt>. */
	private JOptionDialog optionDialog;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public OptionDialog(Session session) {
		super();
		optionDialog = new JOptionDialog(session, WindowManager.getLast());
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return optionDialog.getSession();
	}

	/**
	 * Returns the optional icon.
	 * 
	 * @return The optional icon.
	 */
	public Icon getIcon() {
		return optionDialog.getIcon();
	}

	/**
	 * Returns the initial option.
	 * 
	 * @return The initial option.
	 */
	public String getInitialOption() {
		return optionDialog.getInitialOption();
	}

	/**
	 * Sets the initial option.
	 * 
	 * @param initialOption The initial option.
	 */
	public void setInitialOption(String initialOption) {
		optionDialog.setInitialOption(initialOption);
	}

	/**
	 * Returns the component to be shown.
	 * 
	 * @return The component to be shown.
	 */
	public JComponent getComponent() {
		return optionDialog.getComponent();
	}

	/**
	 * Sets the component to be shown.
	 * 
	 * @param component The component to be shown.
	 */
	public void setComponent(JComponent component) {
		optionDialog.setComponent(component);
	}

	/**
	 * Returns the message if the component is a simple message.
	 * 
	 * @return The message.
	 */
	public String getMessage() {
		return optionDialog.getMessage();
	}

	/**
	 * RunTickers the dialog and return the selected option or null if the dialog was closed without selecting any
	 * option.
	 * 
	 * @return The selected option.
	 */
	public String showDialog() {
		return optionDialog.showDialog();
	}

	/**
	 * RunTickers the dialog and return the selected option or null if the dialog was closed without selecting any
	 * option.
	 * 
	 * @param resizable A boolean that indicates if the dialog is resizable.
	 * @return The selected option.
	 */
	public String showDialog(boolean resizable) {
		return optionDialog.showDialog(resizable);
	}

	/**
	 * Add an option to the list of options.
	 * 
	 * @param option The option to add.
	 */
	public void addOption(String option) {
		optionDialog.addOption(option);
	}

	/**
	 * Add an option to the list of options.
	 * 
	 * @param option The option to add.
	 * @param defaultClose A boolean that indicates if the option is the default close option.
	 */
	public void addOption(String option, boolean defaultClose) {
		optionDialog.addOption(option, defaultClose);
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
		optionDialog.addOption(option, description, smallIcon, acceleratorKey, defaultClose);
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
		optionDialog.addOption(action);
	}

	/**
	 * Sets the optional icon.
	 * 
	 * @param icon The optional icon.
	 */
	public void setIcon(Icon icon) {
		optionDialog.setIcon(icon);
	}

	/**
	 * Sets the message to be shown in a <code>JTextArea</code>.
	 * 
	 * @param message The message.
	 */
	public void setMessage(String message) {
		optionDialog.setMessage(message);
	}

	/**
	 * Sets the message to be shown in a <code>JTextArea</code>.
	 * 
	 * @param message The message.
	 * @param columns The number of columns of the text area.
	 * @param lineWrap The line wrap policy.
	 */
	public void setMessage(String message, int columns, boolean lineWrap) {
		optionDialog.setMessage(message, columns, lineWrap);
	}

	/**
	 * Set the title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		optionDialog.setTitle(title);
	}

}
