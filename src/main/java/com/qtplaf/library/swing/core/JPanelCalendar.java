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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.FormatUtils;

/**
 * A date selection panel.
 * 
 * @author Miquel Sas
 */
public class JPanelCalendar extends JPanel {

	/** Date clicked property constant id. */
	public static final String DATE_CLICKED = "date_clicked";

	/** Default values. */
	private static Border defaultBorder = null;
	private static Color defaultFgColor = null;
	private static Color defaultBgColor = null;
	private static Font defaultFont = null;

	/** Set default values. */
	static {
		javax.swing.JButton button = new javax.swing.JButton();
		defaultBorder = button.getBorder();
		defaultBgColor = button.getBackground();
		defaultFgColor = button.getForeground();
		defaultFont = button.getFont();
	}

	/**
	 * Structure to configure any date.
	 */
	class DateCfg {
		public Color bgColor = null;
		public boolean enabled = true;
		public Color fgColor = null;
		public String label = null;

		DateCfg(Color bgColor, Color fgColor, boolean enabled, String label) {
			this.bgColor = bgColor;
			this.fgColor = fgColor;
			this.enabled = enabled;
			this.label = label;
		}
	}

	/**
	 * A JButton class that contains the number of the day of the month.
	 */
	class JButton extends javax.swing.JButton {
		/** The day of the month. */
		private int day = -1;

		/**
		 * Default constructor.
		 */
		public JButton() {
			super();
			addKeyListener(keyHandler);
		}

		/**
		 * Get the day.
		 * 
		 * @return The day.
		 */
		public int getDay() {
			return day;
		}

		/**
		 * Set the day.
		 * 
		 * @param day The day number.
		 */
		public void setDay(int day) {
			this.day = day;
		}
	}

	/**
	 * A key listener to handle key events.
	 */
	class KeyHandler extends com.qtplaf.library.swing.event.KeyHandler {
		@Override
		public void keyPressed(KeyEvent e) {
			// Key return: select current date.
			if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == 0) {
				int day = getCurrentDay();
				for (JButton button : buttons) {
					if (button.getDay() == day) {
						button.doClick();
						return;
					}
				}
			}
			// Key right: add a day.
			if (e.getKeyCode() == KeyEvent.VK_RIGHT && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addDays(date, 1));
				return;
			}
			// Key left: subtract a day.
			if (e.getKeyCode() == KeyEvent.VK_LEFT && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addDays(date, -1));
				return;
			}
			// Key ctrl right: add a month.
			if (e.getKeyCode() == KeyEvent.VK_RIGHT && e.getModifiers() == KeyEvent.CTRL_MASK) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addMonths(date, 1));
				return;
			}
			// Key ctrl left: subtract a month.
			if (e.getKeyCode() == KeyEvent.VK_LEFT && e.getModifiers() == KeyEvent.CTRL_MASK) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addMonths(date, -1));
				return;
			}
			// Key page down: add a month.
			if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addMonths(date, 1));
				return;
			}
			// Key page up: subtract a month.
			if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addMonths(date, -1));
				return;
			}
			// Key down: add 7 days.
			if (e.getKeyCode() == KeyEvent.VK_DOWN && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addDays(date, 7));
				return;
			}
			// Key up: subtract 7 days.
			if (e.getKeyCode() == KeyEvent.VK_UP && e.getModifiers() == 0) {
				Date date = getCurrentDate();
				setCurrentDate(Calendar.addDays(date, -7));
				return;
			}
		}
	}

	/** The key handler. */
	private KeyHandler keyHandler = new KeyHandler();

	/**
	 * ActionEvent handler.
	 */
	class ButtonActionHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleButtonAction(e);
		}
	}

	/** The button action handler. */
	private ButtonActionHandler actionHandler = new ButtonActionHandler();

	/** Short week days text. */
	private String[] shortWeekDays = null;
	/** Labels for every week day. */
	private JLabel[] labelsWeekDays = new JLabel[7];
	/** Month label. */
	private JLabel labelMonth = null;
	/** A map to configure dates. */
	private HashMap<Date, DateCfg> dateCfgMap = new HashMap<>();
	/** Default week day configurations. */
	private DateCfg[] dayOfWeekCfg = new DateCfg[] { null, null, null, null, null, null, null };
	/** The current date. */
	private Calendar currentDate = null;
	/** Button to decrease months. */
	private JButton buttonMonthDecrease = null;
	/** Button to increase months. */
	private JButton buttonMonthIncrease = null;
	/** Month panel. */
	private JPanel panelMonth = null;
	/** Panel for days. */
	private JPanel panelDays = null;
	/** An array of buttons for every day. */
	private JButton[] buttons = null;
	/** The working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JPanelCalendar(Session session) {
		super();
		this.session = session;
		initializeMainPanel();
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
	 * Handle button events.
	 * 
	 * @param e The action event.
	 */
	private void handleButtonAction(ActionEvent e) {
		if (e.getSource() == getButtonMonthIncrease()) {
			int year = getCurrentYear();
			int month = getCurrentMonth();
			int day = getCurrentDay();
			month++;
			if (month > 12) {
				year++;
				month = 1;
			}
			int lastDay = Calendar.getDaysOfMonth(year, month);
			if (day > lastDay) {
				day = lastDay;
			}
			setCurrentDate(Calendar.createDate(year, month, day));
			return;
		}

		if (e.getSource() == getButtonMonthDecrease()) {
			int year = getCurrentYear();
			int month = getCurrentMonth();
			int day = getCurrentDay();
			month--;
			if (month < 1) {
				year--;
				month = 12;
			}
			int lastDay = Calendar.getDaysOfMonth(year, month);
			if (day > lastDay) {
				day = lastDay;
			}
			setCurrentDate(Calendar.createDate(year, month, day));
			return;
		}

		for (int i = 0; i < buttons.length; i++) {
			if (e.getSource() == buttons[i]) {
				if (buttons[i].getDay() == 0 || !buttons[i].isEnabled()) {
					return;
				}
				setCurrentDate(Calendar.createDate(getCurrentYear(), getCurrentMonth(), buttons[i].getDay()));
				firePropertyChange(DATE_CLICKED, null, currentDate);
				return;
			}
		}
	}

	/**
	 * Configure the calendar settings.
	 */
	private void configure() {

		getLabelMonth()
			.setText(Calendar.getLongMonth(getSession().getLocale(), true, getCurrentMonth()) + " " + getCurrentYear());
		for (int i = 0; i < 7; i++) {
			getLabelWeekDay(i).setFont(defaultFont);
		}
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setFont(defaultFont);
		}
		Calendar firstDayOfMonth = new Calendar(getCurrentYear(), getCurrentMonth(), 1);
		int firstDayOfWeek = firstDayOfMonth.getDayOfWeek() - 1;
		firstDayOfWeek -= getCalendar().getFirstDayOfWeek() - 1;
		if (firstDayOfWeek < 0)
			firstDayOfWeek += 7;
		int day = 0;
		for (int i = 0; i < firstDayOfWeek; i++) {
			buttons[i].setText("..");
			buttons[i].setEnabled(false);
			buttons[i].setDay(0);
			day++;
		}
		int lastDay = Calendar.getDaysOfMonth(getCurrentYear(), getCurrentMonth());
		int count = day;
		for (int i = day; i < lastDay + day; i++) {
			buttons[i].setDay(i - day + 1);
			Date date = Calendar.createDate(getCurrentYear(), getCurrentMonth(), buttons[i].getDay());
			DateCfg cfg = dateCfgMap.get(date);
			String label = FormatUtils.unformattedFromInteger(buttons[i].getDay());
			if (cfg != null && cfg.label != null) {
				label += " " + cfg.label;
			}
			buttons[i].setText(label);
			if ((buttons[i].getDay()) == getCurrentDay()) {
				buttons[i].setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
			} else {
				buttons[i].setBorder(defaultBorder);
			}
			buttons[i].setEnabled(true);
			Calendar today = new Calendar(date);
			int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

			boolean configured = false;
			DateCfg dowCfg = dayOfWeekCfg[dayOfWeek - 1];
			if (dowCfg != null) {
				Color bgColor = dowCfg.bgColor;
				Color fgColor = dowCfg.fgColor;
				boolean enabled = dowCfg.enabled;
				configured = true;
				if (buttons[i].isEnabled()) {
					if (!enabled) {
						if (buttons[i].getDay() == getCurrentDay()) {
							buttons[i].setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
						} else {
							Border iborder =
								BorderFactory.createEtchedBorder(new Color(200, 200, 200), new Color(200, 200, 200));
							Border oborder =
								BorderFactory.createEtchedBorder(new Color(200, 200, 200), new Color(153, 153, 153));
							Border border = BorderFactory.createCompoundBorder(oborder, iborder);
							buttons[i].setBorder(border);
						}
					}
				}
				if (bgColor != null) {
					buttons[i].setBackground(bgColor);
				} else {
					buttons[i].setBackground(defaultBgColor);
				}
				if (fgColor != null) {
					buttons[i].setForeground(fgColor);
				} else {
					buttons[i].setForeground(defaultFgColor);
				}
			}
			if (cfg != null) {
				configured = true;
				if (buttons[i].isEnabled()) {
					if (!cfg.enabled) {
						if ((buttons[i].getDay()) == getCurrentDay()) {
							buttons[i].setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
						} else {
							Border iborder =
								BorderFactory.createEtchedBorder(new Color(200, 200, 200), new Color(200, 200, 200));
							Border oborder =
								BorderFactory.createEtchedBorder(new Color(200, 200, 200), new Color(153, 153, 153));
							Border border = BorderFactory.createCompoundBorder(oborder, iborder);
							buttons[i].setBorder(border);
						}
					}
				}
				if (cfg.bgColor != null) {
					buttons[i].setBackground(cfg.bgColor);
				} else {
					buttons[i].setBackground(defaultBgColor);
				}
				if (cfg.fgColor != null) {
					buttons[i].setForeground(cfg.fgColor);
				} else {
					buttons[i].setForeground(defaultFgColor);
				}
			}
			if (!configured) {
				buttons[i].setBackground(defaultBgColor);
				buttons[i].setForeground(defaultFgColor);
			}
			count++;
		}
		for (int i = count; i < buttons.length; i++) {
			buttons[i].setText("..");
			buttons[i].setBorder(defaultBorder);
			buttons[i].setBackground(defaultBgColor);
			buttons[i].setForeground(defaultFgColor);
			buttons[i].setEnabled(false);
			buttons[i].setDay(0);
		}
		getButtonMonthDecrease().setEnabled(true);
		getButtonMonthIncrease().setEnabled(true);
	}

	/**
	 * Inititalizes the main panel.
	 */
	private void initializeMainPanel() {
		setName("JPanelCalendar");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = null;

		// Month panel
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(4, 4, 2, 4);
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(getPanelMonth(), constraints);

		// Days panel
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(2, 4, 4, 4);
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(getPanelDays(), constraints);

		getButtonMonthDecrease().addActionListener(actionHandler);
		getButtonMonthIncrease().addActionListener(actionHandler);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].addActionListener(actionHandler);
		}
	}

	/**
	 * Returns the button to decrease months.
	 * 
	 * @return The button.
	 */
	private JButton getButtonMonthDecrease() {
		if (buttonMonthDecrease == null) {
			buttonMonthDecrease = new JButton();
			buttonMonthDecrease.setName("JButtonMonthDecrease");
			buttonMonthDecrease.setText("<<");
			buttonMonthDecrease.setMargin(new Insets(2, 4, 2, 4));
		}
		return buttonMonthDecrease;
	}

	/**
	 * Returns the button to increase months.
	 * 
	 * @return The button.
	 */
	private JButton getButtonMonthIncrease() {
		if (buttonMonthIncrease == null) {
			buttonMonthIncrease = new JButton();
			buttonMonthIncrease.setName("JButtonMonthIncrease");
			buttonMonthIncrease.setText(">>");
			buttonMonthIncrease.setMargin(new Insets(2, 4, 2, 4));
		}
		return buttonMonthIncrease;
	}

	public Calendar getCalendar() {
		return currentDate;
	}

	/**
	 * Get the current date.
	 * 
	 * @return The current date.
	 */
	public Date getCurrentDate() {
		return currentDate.toDate();
	}

	/**
	 * Returns the current day of the month.
	 * 
	 * @return The current day of the month.
	 */
	public int getCurrentDay() {
		return currentDate.getDay();
	}

	/**
	 * Returns the current month.
	 * 
	 * @return The current month.
	 */
	public int getCurrentMonth() {
		return currentDate.getMonth();
	}

	/**
	 * Returns the current year.
	 * 
	 * @return The current year.
	 */
	public int getCurrentYear() {
		return currentDate.getYear();
	}

	/**
	 * Return the label.
	 * 
	 * @return The label.
	 */
	private JLabel getLabelMonth() {
		if (labelMonth == null) {
			labelMonth = new JLabel();
			labelMonth.setName("JLabelMonth");
			labelMonth.setText("");
			labelMonth.setHorizontalTextPosition(SwingConstants.CENTER);
			labelMonth.setHorizontalAlignment(SwingConstants.CENTER);
			labelMonth.setFont(defaultFont);
		}
		return labelMonth;
	}

	/**
	 * Returns the label for week day 0.
	 * 
	 * @param day The day of the week.
	 * @return The label.
	 */
	private JLabel getLabelWeekDay(int day) {
		if (labelsWeekDays[day] == null) {
			labelsWeekDays[day] = new JLabel();
			labelsWeekDays[day].setName("JLabelWeekDay" + day);
			labelsWeekDays[day].setBorder(new EtchedBorder());
			labelsWeekDays[day].setText(getShortWeekDays()[day + 1]);
			labelsWeekDays[day].setHorizontalAlignment(SwingConstants.CENTER);
			labelsWeekDays[day].setFont(defaultFont);
		}
		return labelsWeekDays[day];
	}

	private JPanel getPanelDays() {
		if (panelDays == null) {

			panelDays = new JPanel(new GridBagLayout());
			panelDays.setName("JPanelDays");

			// Day labels
			FontMetrics metrics = getFontMetrics(defaultFont);
			int height = metrics.getHeight();
			int width = 0;
			for (int i = 0; i < 7; i++) {
				width = Math.max(width, metrics.stringWidth(getLabelWeekDay(i).getText()));
			}
			for (int i = 0; i < 7; i++) {
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.anchor = GridBagConstraints.NORTHWEST;
				constraints.insets = new Insets(4, 2, 4, 2);
				constraints.gridx = i;
				constraints.gridy = 0;
				getLabelWeekDay(i).setPreferredSize(new Dimension(width + 10, height + 10));
				panelDays.add(getLabelWeekDay(i), constraints);
			}

			// 6 rows of 7 buttons -> 7 cols * 6 rows = 42 buttons
			buttons = new JButton[42];
			int index = 0;
			for (int row = 1; row < 7; row++) {
				for (int col = 0; col < 7; col++) {
					// Define the button
					JButton button = new JButton();
					button.setName("JButtonDay_" + col + "_" + row);
					button.setText("..");
					button.setMargin(new Insets(2, 4, 2, 4));
					button.setFont(defaultFont);
					// Set the constraints
					GridBagConstraints constraints = new GridBagConstraints();
					constraints.fill = GridBagConstraints.BOTH;
					constraints.anchor = GridBagConstraints.NORTHWEST;
					constraints.insets = new Insets(2, 2, 2, 2);
					constraints.gridx = col;
					constraints.gridy = row;
					// Add the button to the panel
					panelDays.add(button, constraints);
					// Assign the button to the array of buttons
					buttons[index] = button;
					index++;
				}
			}
		}
		return panelDays;
	}

	/**
	 * Returns the month panel.
	 * 
	 * @return The month panel.
	 */
	private JPanel getPanelMonth() {
		if (panelMonth == null) {

			panelMonth = new JPanel(new GridBagLayout());
			panelMonth.setName("JPanelMonth");

			GridBagConstraints constraints = null;

			// Decrease button
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(1, 4, 1, 4);
			constraints.gridx = 0;
			constraints.gridy = 0;
			panelMonth.add(getButtonMonthDecrease(), constraints);

			// Label month
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(1, 4, 1, 4);
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			constraints.gridx = 1;
			constraints.gridy = 0;
			panelMonth.add(getLabelMonth(), constraints);

			// Increase button
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.EAST;
			constraints.insets = new Insets(1, 4, 1, 4);
			constraints.gridx = 2;
			constraints.gridy = 0;
			panelMonth.add(getButtonMonthIncrease(), constraints);
		}
		return panelMonth;
	}

	/**
	 * Returns the array of short week days.
	 * 
	 * @return The array of names.
	 */
	private String[] getShortWeekDays() {
		if (shortWeekDays == null) {
			String[] days = Calendar.getShortDays(getSession().getLocale(), true);
			shortWeekDays = new String[days.length];
			int fdow = Calendar.getInstance().getFirstDayOfWeek();
			for (int i = 1; i < days.length; i++) {
				int n = i + fdow - 1;
				if (n > 7)
					n -= 7;
				shortWeekDays[i] = days[n];
			}
		}
		return shortWeekDays;
	}

	/**
	 * Configure a date.
	 * 
	 * @param date The date to configure.
	 * @param bgColor Background color.
	 * @param fgColor Foreground color.
	 * @param enabled A boolean to enable/disable the date.
	 * @param label A label.
	 */
	public void setDateCfg(Date date, Color bgColor, Color fgColor, boolean enabled, String label) {
		dateCfgMap.put(date, new DateCfg(bgColor, fgColor, enabled, label));
	}

	/**
	 * Sets the default configuration for a day of the week.
	 * 
	 * @param day The day of the week.
	 * @param bgColor Background color.
	 * @param fgColor Foreground color.
	 * @param enabled A boolean to enable/disable the date.
	 */
	public void setDayOfWeekCfg(int day, Color bgColor, Color fgColor, boolean enabled) {
		if (day < 1 || day > 7) {
			throw new IllegalArgumentException("Invalid day");
		}
		dayOfWeekCfg[day - 1] = new DateCfg(bgColor, fgColor, enabled, null);
	}

	/**
	 * Set the current date.
	 * 
	 * @param date The current date.
	 */
	public void setCurrentDate(Date date) {
		currentDate = new Calendar(date == null ? new Date() : date);
		configure();
	}
}
