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
		Plain,
		Information,
		Warning,
		Error,
		Question
	}
	/**
	 * The possible message box options.
	 */
	public static enum Option {
		Accept,
		Ok,
		Yes,
		Cancel,
		No,
		Retry,
		Ignore;

		/**
		 * Check if it is a cancel option to use when closing the window.
		 * 
		 * @return A boolean
		 */
		public boolean isCancel() {
			if (equals(Cancel) || equals(No)) {
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
			case Accept:
				return session.getString("messageBoxOptionAccept");
			case Ok:
				return session.getString("messageBoxOptionOk");
			case Yes:
				return session.getString("messageBoxOptionYes");
			case Cancel:
				return session.getString("messageBoxOptionCancel");
			case No:
				return session.getString("messageBoxOptionNo");
			case Retry:
				return session.getString("messageBoxOptionRetry");
			case Ignore:
				return session.getString("messageBoxOptionIgnore");
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static final Option ACCEPT = Option.Accept;
	public static final Option OK = Option.Ok;
	public static final Option YES = Option.Yes;
	public static final Option CANCEL = Option.Cancel;
	public static final Option NO = Option.No;
	public static final Option RETRY = Option.Retry;
	public static final Option IGNORE = Option.Ignore;
	public static final Option[] YES_NO = new Option[] { Option.Yes, Option.No };
	public static final Option[] ACCEPT_CANCEL = new Option[] { Option.Accept, Option.Cancel };

	/**
	 * Error message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option error(Session session, String message) {
		return error(session, message, Option.Accept);
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
		return showOptionDialog(session, message, title, Type.Error, options, initialOption);
	}

	/**
	 * Information message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option info(Session session, String message) {
		return info(session, message, Option.Accept);
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
		return showOptionDialog(session, message, title, Type.Information, options, initialOption);
	}

	/**
	 * Warning message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option warning(Session session, String message) {
		return warning(session, message, Option.Accept);
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
		return showOptionDialog(session, message, title, Type.Warning, options, initialOption);
	}

	/**
	 * Question message box.
	 * 
	 * @param session The working session.
	 * @param message The message.
	 * @return The selected option or null if the box is closed with the close operation.
	 */
	public static Option question(Session session, String message) {
		return question(session, message, Option.Accept);
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
		return showOptionDialog(session, message, title, Type.Question, options, initialOption);
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
		if (type.equals(Type.Information)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
		}
		if (type.equals(Type.Warning)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		}
		if (type.equals(Type.Error)) {
			dialog.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		}
		if (type.equals(Type.Question)) {
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
