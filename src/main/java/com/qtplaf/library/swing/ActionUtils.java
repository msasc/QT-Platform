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

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.StatusBar;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.chart.JChartPlotter;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.library.util.Properties;

/**
 * Action utilities.
 * 
 * @author Miquel Sas
 */
public class ActionUtils {

	/** A key for generic user properties. */
	private static final String PROPERTIES = "Properties";

	/** Integer key value. */
	private static int index = 0;

	/** The current working session. */
	private static final Integer SESSION = index++;
	/** The key for the action group list of buttons. */
	private static final Integer ACTION_GROUP = index++;
	/**
	 * Original or source for the name, useful if the name is formatted HTML and a mnemonic is set to the associated
	 * button.
	 */
	private static final Integer SOURCE_NAME = index++;
	/** Menu item text if different from the name or there if no name for the action (small icon buttons). */
	private static final Integer MENUITEM_SOURCE_TEXT = index++;
	/** Original or source tooltip for the short description. */
	private static final Integer TOOLTIP = index++;
	/** Key for the previous value, used by value actions to indicate the value prior to be changed. */
	private static final Integer PREVIOUS_VALUE = index++;
	/** Key for the current value, used by value actions to indicate the value after the change. */
	private static final Integer CURRENT_VALUE = index++;
	/** Key for the edit context. */
	private static final Integer EDIT_CONTEXT = index++;
	/** Key for the edit mode. */
	private static final Integer EDIT_MODE = index++;
	/** The button container of the action. */
	private static final Integer BUTTON = index++;
	/** A sort key index to sort actions in a buttons panel or in any other means. */
	private static final Integer SORT_INDEX = index++;
	/** A list of selected records, mainly used on a lookup action. */
	private static final Integer SELECTED_RECORDS = index++;
	/** A boolean to indicate that a lookup action is multiple selection. */
	private static final Integer MULTIPLE_SELECTION = index++;
	/** Form record key. When an action is added to a record form, the form is accessible through this key. */
	private static final Integer FORM_RECORD = index++;
	/** A boolean that indicates if the action (button action) is visible in the buttons panel. Default is true. */
	private static final Integer VISIBLE_IN_BUTTONS_PANEL = index++;
	/** A boolean that indicates if the action (button action) is visible in a popup menu. Default is true. */
	private static final Integer VISIBLE_IN_POPUP_MENU = index++;
	/** A boolean to indicate that the action is the default close action in dialog and frames. */
	private static final Integer DEFAULT_CLOSE_ACTION = index++;
	/** A an object used as launch arguments fromm the menu. */
	private static final Integer LAUNCH_ARGS = index++;
	/** A list of tasks, used in the progress manager to pass the tasks to actions. */
	private static final Integer TASKS = index++;
	/** A reference to a JChart. */
	private static final Integer CHART = index++;
	/** A reference to a JChartPlotter. */
	private static final Integer CHART_PLOTTER = index++;
	/** The mouse point. */
	private static final Integer MOUSE_POINT = index++;
	/** The panel table record for actions installed in such a panel. */
	private static final Integer PANEL_TABLE_RECORD = index++;

	/**
	 * A status bar, normally installed in a frame or menu frame, useful to monitor fast tasks, passed to the lauched
	 * action.
	 */
	private static final Integer STATUS_BAR = index++;
	/** A generic user object. */
	private static final Integer USER_OBJECT = index++;

	/**
	 * Returns the sort index value.
	 * 
	 * @param action The action.
	 * @return The sort index value.
	 */
	public static int getSortIndex(Action action) {
		return getProperties(action).getInteger(SORT_INDEX, -1);
	}

	/**
	 * Returns the visible in popup menu value.
	 * 
	 * @param action The action.
	 * @return The visible in popup menu value.
	 */
	public static boolean isVisibleInPopupMenu(Action action) {
		return getProperties(action).getBoolean(VISIBLE_IN_POPUP_MENU, true);
	}

	/**
	 * Returns the user properties value.
	 * 
	 * @param action The action.
	 * @return The user properties.
	 */
	public static Properties getProperties(Action action) {
		Properties properties = (Properties) action.getValue(PROPERTIES);
		if (properties == null) {
			properties = new Properties();
			action.putValue(PROPERTIES, properties);
		}
		return properties;
	}

	/**
	 * Returns the visible in buttons panel value.
	 * 
	 * @param action The action.
	 * @return The visible in buttons panel value.
	 */
	public static boolean isVisibleInButtonsPanel(Action action) {
		return getProperties(action).getBoolean(VISIBLE_IN_BUTTONS_PANEL, true);
	}

	/**
	 * Returns the edit mode value.
	 * 
	 * @param action The action.
	 * @return The edit mode value.
	 */
	public static EditMode getEditMode(Action action) {
		return (EditMode) getProperties(action).getObject(EDIT_MODE);
	}

	/**
	 * Returns the form record value.
	 * 
	 * @param action The action.
	 * @return The form record value.
	 */
	public static JFormRecord getFormRecord(Action action) {
		return (JFormRecord) getProperties(action).getObject(FORM_RECORD);
	}

	/**
	 * Returns the current value.
	 * 
	 * @param action The action.
	 * @return The current value.
	 */
	public static Value getCurrentValue(Action action) {
		return (Value) getProperties(action).getObject(CURRENT_VALUE);
	}

	/**
	 * Returns the previous value.
	 * 
	 * @param action The action.
	 * @return The previous value.
	 */
	public static Value getPreviousValue(Action action) {
		return (Value) getProperties(action).getObject(PREVIOUS_VALUE);
	}

	/**
	 * Returns the multiple selection value.
	 * 
	 * @param action The action.
	 * @return The multiple selection value.
	 */
	public static boolean isMultipleSelection(Action action) {
		return getProperties(action).getBoolean(MULTIPLE_SELECTION);
	}

	/**
	 * Returns a boolean indicating whethe the action is a default close action.
	 * 
	 * @param action The action.
	 * @return A boolean.
	 */
	public static boolean isDefaultCloseAction(Action action) {
		return getProperties(action).getBoolean(DEFAULT_CLOSE_ACTION);
	}

	/**
	 * Returns the edit context value.
	 * 
	 * @param action The action.
	 * @return The edit context value.
	 */
	public static EditContext getEditContext(Action action) {
		return (EditContext) getProperties(action).getObject(EDIT_CONTEXT);
	}

	/**
	 * Returns the accelerator key value.
	 * 
	 * @param action The action.
	 * @return The accelerator key value.
	 */
	public static KeyStroke getAcceleratorKey(Action action) {
		return (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
	}

	/**
	 * Returns the session value.
	 * 
	 * @param action The action.
	 * @return The session value.
	 */
	public static Session getSession(Action action) {
		return (Session) getProperties(action).getObject(SESSION);
	}

	/**
	 * Returns the name.
	 * 
	 * @param action The action.
	 * @return The name.
	 */
	public static String getName(Action action) {
		return (String) action.getValue(Action.NAME);
	}

	/**
	 * Returns the short description.
	 * 
	 * @param action The action.
	 * @return The short description.
	 */
	public static String getShortDescription(Action action) {
		return (String) action.getValue(Action.SHORT_DESCRIPTION);
	}

	/**
	 * Returns the long description value.
	 * 
	 * @param action The action.
	 * @return The long description value.
	 */
	public static String getLongDescription(Action action) {
		return (String) action.getValue(Action.LONG_DESCRIPTION);
	}

	/**
	 * Returns the source name value.
	 * 
	 * @param action The action.
	 * @return The source name value.
	 */
	public static String getSourceName(Action action) {
		return getProperties(action).getString(SOURCE_NAME);
	}

	/**
	 * Returns the status bat if installed in the action, or null.
	 * 
	 * @param action The action.
	 * @return The status bar.
	 */
	public static StatusBar getStatusBar(Action action) {
		return (StatusBar) getProperties(action).getObject(STATUS_BAR);
	}

	/**
	 * Returns the user object installed in the action.
	 * 
	 * @param action The action.
	 * @return The user object or null.
	 */
	public static Object getUserObject(Action action) {
		return getProperties(action).getObject(USER_OBJECT);
	}

	/**
	 * Returns the launch argument object.
	 * 
	 * @param action The action.
	 * @return The launch argument object or null.
	 */
	public static Properties getLaunchArgs(Action action) {
		return (Properties) getProperties(action).getObject(LAUNCH_ARGS);
	}

	/**
	 * Returns the small icon.
	 * 
	 * @param action The action.
	 * @return The small icon.
	 */
	public static Icon getSmallIcon(Action action) {
		return (Icon) action.getValue(Action.SMALL_ICON);
	}

	/**
	 * Returns the menu item source text value.
	 * 
	 * @param action The action.
	 * @return The menu item source text value.
	 */
	public static String getMenuItemSourceText(Action action) {
		return getProperties(action).getString(MENUITEM_SOURCE_TEXT);
	}

	/**
	 * Returns the tool tip value.
	 * 
	 * @param action The action.
	 * @return The tool tip value.
	 */
	public static String getToolTip(Action action) {
		return getProperties(action).getString(TOOLTIP);
	}

	/**
	 * Returns the action group value.
	 * 
	 * @param action The action.
	 * @return The action group value.
	 */
	public static ActionGroup getActionGroup(Action action) {
		return (ActionGroup) getProperties(action).getObject(ACTION_GROUP);
	}

	/**
	 * Returns the button value.
	 * 
	 * @param action The action.
	 * @return The button value.
	 */
	public static JButton getButton(Action action) {
		return (JButton) getProperties(action).getObject(BUTTON);
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @param action The action.
	 * @return The list of selected records.
	 */
	@SuppressWarnings("unchecked")
	public static List<Record> getSelectedRecords(Action action) {
		return (List<Record>) getProperties(action).getObject(SELECTED_RECORDS);
	}

	/**
	 * Returns the list of tasks.
	 * 
	 * @param action The action.
	 * @return The list of tasks.
	 */
	@SuppressWarnings("unchecked")
	public static List<Task> getTasks(Action action) {
		return (List<Task>) getProperties(action).getObject(TASKS);
	}

	/**
	 * Set the sort index value.
	 * 
	 * @param action The action.
	 * @param sortIndex The sort index value.
	 */
	public static void setSortIndex(Action action, int sortIndex) {
		getProperties(action).setInteger(SORT_INDEX, sortIndex);
	}

	/**
	 * Set the edit mode value.
	 * 
	 * @param action The action.
	 * @param editMode The edit mode value.
	 */
	public static void setEditMode(Action action, EditMode editMode) {
		getProperties(action).setObject(EDIT_MODE, editMode);
	}

	/**
	 * Set the edit context value.
	 * 
	 * @param action The action.
	 * @param editContext The edit context value.
	 */
	public static void setEditContext(Action action, EditContext editContext) {
		getProperties(action).setObject(EDIT_CONTEXT, editContext);
	}

	/**
	 * Set the current value.
	 * 
	 * @param action The action.
	 * @param value The value.
	 */
	public static void setCurrentValue(Action action, Value value) {
		getProperties(action).setObject(CURRENT_VALUE, value);
	}

	/**
	 * Set the previous value.
	 * 
	 * @param action The action.
	 * @param value The value.
	 */
	public static void setPreviousValue(Action action, Value value) {
		getProperties(action).setObject(PREVIOUS_VALUE, value);
	}

	/**
	 * Set the session.
	 * 
	 * @param action The action.
	 * @param session The session.
	 */
	public static void setSession(Action action, Session session) {
		getProperties(action).setObject(SESSION, session);
	}

	/**
	 * Set the name.
	 * 
	 * @param action The action.
	 * @param name The name.
	 */
	public static void setName(Action action, String name) {
		action.putValue(Action.NAME, name);
	}

	/**
	 * Set the source name value.
	 * 
	 * @param action The action.
	 * @param sourceName The text value.
	 */
	public static void setSourceName(Action action, String sourceName) {
		getProperties(action).setString(SOURCE_NAME, sourceName);
	}

	/**
	 * Set a generic user object.
	 * 
	 * @param action The action.
	 * @param userObject The user object.
	 */
	public static void setUserObject(Action action, Object userObject) {
		getProperties(action).setObject(USER_OBJECT, userObject);
	}

	/**
	 * Set the status bar.
	 * 
	 * @param action The action.
	 * @param statusBar The status bar.
	 */
	public static void setStatusBar(Action action, StatusBar statusBar) {
		getProperties(action).setObject(STATUS_BAR, statusBar);
	}

	/**
	 * Sets the launch arguments object.
	 * 
	 * @param action The action.
	 * @param launchArgs The launch arguments object.
	 */
	public static void setLaunchArgs(Action action, Properties launchArgs) {
		getProperties(action).setObject(LAUNCH_ARGS, launchArgs);
	}

	/**
	 * Set the menu item source text value.
	 * 
	 * @param action The action.
	 * @param sourceText The text value.
	 */
	public static void setMenuItemSourceText(Action action, String sourceText) {
		getProperties(action).setString(MENUITEM_SOURCE_TEXT, sourceText);
	}

	/**
	 * PSetut the tool tip value.
	 * 
	 * @param action The action.
	 * @param toolTip The tool tip value.
	 */
	public static void setToolTip(Action action, String toolTip) {
		getProperties(action).setString(TOOLTIP, toolTip);
	}

	/**
	 * Put the short description.
	 * 
	 * @param action The action.
	 * @param shortDescription The short description.
	 */
	public static void setShortDescription(Action action, String shortDescription) {
		action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
	}

	/**
	 * Set the long description value.
	 * 
	 * @param action The action.
	 * @param longDescription The long description value.
	 */
	public static void setLongDescription(Action action, String longDescription) {
		action.putValue(Action.LONG_DESCRIPTION, longDescription);
	}

	/**
	 * Set the visible in buttons panel value.
	 * 
	 * @param action The action.
	 * @param visibleInButtonsPanel The visible in buttons panel value.
	 */
	public static void setVisibleInButtonsPanel(Action action, boolean visibleInButtonsPanel) {
		getProperties(action).setBoolean(VISIBLE_IN_BUTTONS_PANEL, visibleInButtonsPanel);
	}

	/**
	 * Set the visible in popup menu value.
	 * 
	 * @param action The action.
	 * @param visibleInPopupMenu The visible in popup menu value.
	 */
	public static void setVisibleInPopupMenu(Action action, boolean visibleInPopupMenu) {
		getProperties(action).setBoolean(VISIBLE_IN_POPUP_MENU, visibleInPopupMenu);
	}

	/**
	 * Set the multiple selection value.
	 * 
	 * @param action The action.
	 * @param multipleSelection The multiple selection value.
	 */
	public static void setMultipleSelection(Action action, boolean multipleSelection) {
		getProperties(action).setBoolean(MULTIPLE_SELECTION, multipleSelection);
	}

	/**
	 * Set the action as default close action..
	 * 
	 * @param action The action.
	 * @param defaultClose A boolean.
	 */
	public static void setDefaultCloseAction(Action action, boolean defaultClose) {
		getProperties(action).setBoolean(DEFAULT_CLOSE_ACTION, defaultClose);
	}

	/**
	 * Set the button value.
	 * 
	 * @param action The action.
	 * @param button The button value.
	 */
	public static void setButton(Action action, JButton button) {
		getProperties(action).setObject(BUTTON, button);
	}

	/**
	 * Put the accelerator key value.
	 * 
	 * @param action The action.
	 * @param acceleratorKey The accelerator key value.
	 */
	public static void setAcceleratorKey(Action action, KeyStroke acceleratorKey) {
		action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
	}

	/**
	 * Set the action group value.
	 * 
	 * @param action The action.
	 * @param actionGroup The action group value.
	 */
	public static void setActionGroup(Action action, ActionGroup actionGroup) {
		getProperties(action).setObject(ACTION_GROUP, actionGroup);
	}

	/**
	 * Set the form record value.
	 * 
	 * @param action The action.
	 * @param formRecord The form record value.
	 */
	public static void setFormRecord(Action action, JFormRecord formRecord) {
		getProperties(action).setObject(FORM_RECORD, formRecord);
	}

	/**
	 * Set the list of selected records value.
	 * 
	 * @param action The action.
	 * @param selectedRecords The list of selected records value.
	 */
	public static void setSelectedRecords(Action action, List<Record> selectedRecords) {
		getProperties(action).setObject(SELECTED_RECORDS, selectedRecords);
	}

	/**
	 * Set the list of tasks.
	 * 
	 * @param action The action.
	 * @param tasks The list of tasks.
	 */
	public static void setTasks(Action action, List<Task> tasks) {
		getProperties(action).setObject(TASKS, tasks);
	}

	/**
	 * Set the small icon value.
	 * 
	 * @param action The action.
	 * @param iconPath The icon path in the images jar files.
	 */
	public static void setSmallIcon(Action action, String iconPath) {
		setSmallIcon(action, ImageIconUtils.getImageIcon(iconPath));
	}

	/**
	 * Set the small icon value.
	 * 
	 * @param action The action.
	 * @param icon The icon.
	 */
	public static void setSmallIcon(Action action, Icon icon) {
		action.putValue(Action.SMALL_ICON, icon);
	}

	/**
	 * Returns the action name that should apply. If the action has a source name and a key stroke, an HTML name that
	 * informs of the key is builded.
	 * 
	 * @param action The action.
	 * @return The name.
	 */
	public static String getActionName(Action action) {
		Session session = getSession(action);
		Locale locale = (session == null ? Locale.UK : session.getLocale());

		StringBuilder b = new StringBuilder();
		String name = getName(action);
		String sourceName = getSourceName(action);
		KeyStroke keyStroke = getAcceleratorKey(action);
		if (sourceName != null) {
			if (keyStroke == null) {
				b.append(sourceName);
			} else {
				b.append("<html>");
				b.append(sourceName);
				b.append(" ");
				b.append("<font color=\"gray\">");
				// b.append("&lt;");
				b.append("(");
				b.append(SwingUtils.translate(keyStroke, locale));
				b.append(")");
				// b.append("&gt;");
				b.append("</font>");
				b.append("</html>");
			}
		} else {
			if (name != null) {
				b.append(name);
			}
		}
		return b.toString();
	}

	/**
	 * Set the name returned by <code>getActionName()</code> to the action.
	 * 
	 * @param action The action to setup.
	 */
	public static void setActionName(Action action) {
		setName(action, getActionName(action));
	}

	/**
	 * Returns the chart.
	 * 
	 * @param action The action where the chart should be installed.
	 * @return The chart.
	 */
	public static JChart getChart(Action action) {
		return (JChart) getProperties(action).getObject(CHART);
	}

	/**
	 * Set the chart.
	 * 
	 * @param action The action.
	 * @param chart The chart object.
	 */
	public static void setChart(Action action, JChart chart) {
		getProperties(action).setObject(CHART, chart);
	}

	/**
	 * Returns the chart plotter.
	 * 
	 * @param action The action where the chart should be installed.
	 * @return The chart plotteer.
	 */
	public static JChartPlotter getChartPlotter(Action action) {
		return (JChartPlotter) getProperties(action).getObject(CHART_PLOTTER);
	}

	/**
	 * Set the chart plotter.
	 * 
	 * @param action The action.
	 * @param chartPlotter The chart plotter object.
	 */
	public static void setChartPlotter(Action action, JChartPlotter chartPlotter) {
		getProperties(action).setObject(CHART_PLOTTER, chartPlotter);
	}

	/**
	 * Return the mouse point.
	 * 
	 * @param action The action.
	 * @return The mouse point.
	 */
	public static Point getMousePoint(Action action) {
		return (Point) getProperties(action).getObject(MOUSE_POINT);
	}

	/**
	 * Set the mouse point.
	 * 
	 * @param action The action.
	 * @param point The point.
	 */
	public static void setMousePoint(Action action, Point point) {
		getProperties(action).setObject(MOUSE_POINT, point);
	}

	/**
	 * Returns the table record panel.
	 * 
	 * @param action The action.
	 * @return The table record panel.
	 */
	public static JPanelTableRecord getTableRecordPanel(Action action) {
		return (JPanelTableRecord) getProperties(action).getObject(PANEL_TABLE_RECORD);
	}

	/**
	 * Set the table record panel.
	 * 
	 * @param action The action.
	 * @param panelTableRecord The table record panel.
	 */
	public static void setTableRecordPanel(Action action, JPanelTableRecord panelTableRecord) {
		getProperties(action).setObject(PANEL_TABLE_RECORD, panelTableRecord);
	}

	/**
	 * Configure the action as an action accept.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureAccept(Session session, Action action) {
		setSourceName(action, session.getString("actionAcceptName"));
		setShortDescription(action, session.getString("actionAcceptName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_accept));
	}

	/**
	 * Configure the action as an action browse.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureBrowse(Session session, Action action) {
		setSourceName(action, session.getString("actionBrowseName"));
		setShortDescription(action, session.getString("actionBrowseName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_browse));
	}

	/**
	 * Configure the action as an action calculate.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureCalculate(Session session, Action action) {
		setSourceName(action, session.getString("actionCalculateName"));
		setShortDescription(action, session.getString("actionCalculateName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_execute));
	}

	/**
	 * Configure the action as an action cancel.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureCancel(Session session, Action action) {
		setSourceName(action, session.getString("actionCancelName"));
		setShortDescription(action, session.getString("actionCancelName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_cancel));
	}

	/**
	 * Configure the action as an action chart.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureChart(Session session, Action action) {
		setSourceName(action, session.getString("actionChartName"));
		setShortDescription(action, session.getString("actionChartName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
	}

	/**
	 * Configure the action as an action clear.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureClear(Session session, Action action) {
		setSourceName(action, session.getString("actionClearName"));
		setShortDescription(action, session.getString("actionClearName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_clear));
	}

	/**
	 * Configure the action as an action close.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureClose(Session session, Action action) {
		setSession(action, session);
		setSourceName(action, session.getString("actionCloseName"));
		setShortDescription(action, session.getString("actionCloseName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EXIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_close));
	}

	/**
	 * Configure the action as an action create.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureCreate(Session session, Action action) {
		setSession(action, session);
		setSourceName(action, session.getString("actionCreateName"));
		setShortDescription(action, session.getString("actionCreateName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_create));
	}

	/**
	 * Configure the action as an action delete.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureDelete(Session session, Action action) {
		setSession(action, session);
		setSourceName(action, session.getString("actionDeleteName"));
		setShortDescription(action, session.getString("actionDeleteName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_delete));
	}

	/**
	 * Configure the action as an action download.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureDownload(Session session, Action action) {
		setSourceName(action, session.getString("actionDownloadName"));
		setShortDescription(action, session.getString("actionDownloadName"));
		setAcceleratorKey(
			action,
			KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_download));
	}

	/**
	 * Configure the action as an action execute.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureExecute(Session session, Action action) {
		setSourceName(action, session.getString("actionExecuteName"));
		setShortDescription(action, session.getString("actionExecuteName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_execute));
	}

	/**
	 * Configure the action as an action execute.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureExit(Session session, Action action) {
		setSourceName(action, session.getString("actionExitName"));
		setShortDescription(action, session.getString("actionExitName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EXIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_cancel));
	}

	/**
	 * Configure the action as an action purge.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configurePurge(Session session, Action action) {
		setSourceName(action, session.getString("actionPurgeName"));
		setShortDescription(action, session.getString("actionPurgeName"));
		setAcceleratorKey(
			action,
			KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_purge));
	}

	/**
	 * Configure the action as an action select.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureSelect(Session session, Action action) {
		setSourceName(action, session.getString("actionSelectName"));
		setShortDescription(action, session.getString("actionSelectName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.EDIT);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_accept));
	}

	/**
	 * Configure the action as an action start.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureStart(Session session, Action action) {
		setSourceName(action, session.getString("actionStartName"));
		setShortDescription(action, session.getString("actionStartName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_execute));
	}

	/**
	 * Configure the action as an action stop.
	 * 
	 * @param session Working session.
	 * @param action The action.
	 */
	public static void configureStop(Session session, Action action) {
		setSourceName(action, session.getString("actionStopName"));
		setShortDescription(action, session.getString("actionStopName"));
		setAcceleratorKey(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK));
		setSession(action, session);
		setActionGroup(action, ActionGroup.OPERATION);
		setSmallIcon(action, ImageIconUtils.getImageIcon(Icons.app_16x16_stop));
	}
}
