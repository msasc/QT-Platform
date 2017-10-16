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

import javax.swing.UIManager;

import com.qtplaf.library.app.Session;

/**
 * Message box with standard options.
 * 
 * @author Miquel Sas
 */
public class MessageBox {
	/**
	 * Message box types.
	 */
	public static enum Type {
		PLAIN,
		INFORMATION,
		WARNING,
		ERROR,
		QUESTION
	}
	/**
	 * The possible message box options.
	 */
	public static enum Option {
		ACCEPT,
		OK,
		YES,
		CANCEL,
		NO,
		RETRY,
		IGNORE;

		/**
		 * Check if it is a cancel option to use when closing the window.
		 * 
		 * @return A boolean
		 */
		public boolean isCancel() {
			if (equals(CANCEL) || equals(NO)) {
				return true;
			}
			return false;
		}

		/**
		 * Returns this option label.
		 * 
		 * @param session The working session.
		 * @return The label.
		 */
		public String getLabel(Session session) {
			switch (this) {
			case ACCEPT:
				return session.getString("messageBoxOptionAccept");
			case OK:
				return session.getString("messageBoxOptionOk");
			case YES:
				return session.getString("messageBoxOptionYes");
			case CANCEL:
				return session.getString("messageBoxOptionCancel");
			case NO:
				return session.getString("messageBoxOptionNo");
			case RETRY:
				return session.getString("messageBoxOptionRetry");
			case IGNORE:
				return session.getString("messageBoxOptionIgnore");
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static final Option ACCEPT = Option.ACCEPT;
	public static final Option OK = Option.OK;
	public static final Option YES = Option.YES;
	public static final Option CANCEL = Option.CANCEL;
	public static final Option NO = Option.NO;
	public static final Option RETRY = Option.RETRY;
	public static final Option IGNORE = Option.IGNORE;
	public static final Option[] YES_NO = new Option[] { Option.YES, Option.NO };
	public static final Option[] ACCEPT_CANCEL = new Option[] { Option.ACCEPT, Option.CANCEL };

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message) {
		return error(session, message, Option.ACCEPT);
	}

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message, Option... options) {
		return error(session, message, session.getString("messageBoxTitleError"), options);
	}

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message, Option[] options, Option initialOption) {
		return error(session, message, session.getString("messageBoxTitleError"), options, initialOption);
	}

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message, String title, Option... options) {
		return error(session, message, title, options, options[options.length - 1]);
	}

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message, String title, Option[] options, Option initialOption) {
		return showOptionDialog(session, message, title, Type.ERROR, options, initialOption);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message) {
		return info(session, message, Option.ACCEPT);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message, Option... options) {
		return info(session, message, session.getString("messageBoxTitleInformation"), options);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message, Option[] options, Option initialOption) {
		return info(session, message, session.getString("messageBoxTitleInformation"), options, initialOption);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message, String title, Option... options) {
		return info(session, message, title, options, options[options.length - 1]);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message, String title, Option[] options, Option initialOption) {
		return showOptionDialog(session, message, title, Type.INFORMATION, options, initialOption);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option warning(Session session, String message) {
		return warning(session, message, Option.ACCEPT);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option warning(Session session, String message, Option... options) {
		return warning(session, message, session.getString("messageBoxTitleWarning"), options);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option warning(Session session, String message, Option[] options, Option initialOption) {
		return warning(session, message, session.getString("messageBoxTitleWarning"), options, initialOption);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option warning(Session session, String message, String title, Option... options) {
		return warning(session, message, title, options, options[options.length - 1]);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static
		Option
		warning(Session session, String message, String title, Option[] options, Option initialOption) {
		return showOptionDialog(session, message, title, Type.WARNING, options, initialOption);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option question(Session session, String message) {
		return question(session, message, Option.ACCEPT);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option question(Session session, String message, Option... options) {
		return question(session, message, session.getString("messageBoxTitleQuestion"), options);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option question(Session session, String message, Option[] options, Option initialOption) {
		return question(session, message, session.getString("messageBoxTitleQuestion"), options, initialOption);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option question(Session session, String message, String title, Option... options) {
		return question(session, message, title, options, options[options.length - 1]);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @param title The title.
	 * @param options The array of options.
	 * @param initialOption The initial option.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static
		Option
		question(Session session, String message, String title, Option[] options, Option initialOption) {
		return showOptionDialog(session, message, title, Type.QUESTION, options, initialOption);
	}

	/**
	 * Returns the option that corresponds to the label.
	 * 
	 * @param session The working session.
	 * @param label The label of the option.
	 * @return The option.
	 */
	private static Option getOption(Session session, String label) {
		Option[] options = Option.values();
		for (Option option : options) {
			if (option.getLabel(session).equals(label)) {
				return option;
			}
		}
		return null;
	}

	/**
	 * Shows the option dialog.
	 * 
	 * @param session The working session.
	 * @param message The message
	 * @param title The title
	 * @param type The JOptionPane message type
	 * @param options The array of options
	 * @param initialOption The initial option
	 * @return The option selected
	 */
	private static Option showOptionDialog(
		Session session,
		String message,
		String title,
		Type type,
		Option[] options,
		Option initialOption) {
		
		OptionDialog dialog = new OptionDialog(session);
		if (title != null) {
			dialog.setTitle(title);
		}
		dialog.setMessage(message);
		for (Option option : options) {
			dialog.addOption(option.getLabel(session));
		}
		if (initialOption != null) {
			dialog.setInitialOption(initialOption.toString());
		}
		if (type.equals(Type.INFORMATION)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
		}
		if (type.equals(Type.WARNING)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		}
		if (type.equals(Type.ERROR)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		}
		if (type.equals(Type.QUESTION)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.questionIcon"));
		}

		String value = dialog.showDialog();
		if (value != null) {
			return getOption(session, value);
		}

		for (Option option : options) {
			if (option.isCancel()) {
				return option;
			}
		}

		return null;
	}

	/**
	 * Private constructor.
	 */
	private MessageBox() {
	}

}
