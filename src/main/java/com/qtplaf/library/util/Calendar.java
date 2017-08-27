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
package com.qtplaf.library.util;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * A Calendar implements a more natural use of a <code>GregorianCalendar</code>.
 *
 * @author Miquel Sas
 */
public class Calendar extends GregorianCalendar {

	/**
	 * Returns a GTM calendar.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(int year, int month, int day) {
		return getGTMCalendar(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Returns a GTM calendar.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(int year, int month, int day, int hour) {
		return getGTMCalendar(year, month, day, hour, 0, 0, 0);
	}

	/**
	 * Returns a GTM calendar.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(int year, int month, int day, int hour, int minute) {
		return getGTMCalendar(year, month, day, hour, minute, 0, 0);
	}

	/**
	 * Returns a GTM calendar.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(int year, int month, int day, int hour, int minute, int second) {
		return getGTMCalendar(year, month, day, hour, minute, second, 0);
	}

	/**
	 * Returns a GTM calendar.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @param millis The millisecond
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(int year, int month, int day, int hour, int minute, int second, int millis) {
		return getCalendar(TimeZone.getTimeZone("GTM"), year, month, day, hour, minute, second, millis);
	}

	/**
	 * Returns a GTM calendar.
	 *
	 * @param time The time in millis.
	 * @return The calendar.
	 */
	public static Calendar getGTMCalendar(long time) {
		Calendar calendar = new Calendar(time);
		calendar.setTimeZone(TimeZone.getTimeZone("GTM"));
		return calendar;
	}

	/**
	 * Returns the calendar for the given time zone.
	 *
	 * @param timeZone The time zone.
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @param millis The millisecond
	 * @return The calendar.
	 */
	public static Calendar getCalendar(
		TimeZone timeZone,
		int year,
		int month,
		int day,
		int hour,
		int minute,
		int second,
		int millis) {
		Calendar calendar = new Calendar(year, month, day, hour, minute, second, millis);
		calendar.setTimeZone(timeZone);
		return calendar;
	}

	/**
	 * Add days to a date.
	 *
	 * @return The new date.
	 * @param date The origin date.
	 * @param days The number of days to add.
	 */
	public static Date addDays(Date date, int days) {
		Calendar calendar = new Calendar(date);
		calendar.addDays(days);
		return calendar.toDate();
	}

	/**
	 * Add months to a date.
	 *
	 * @return The new date.
	 * @param date The origin date.
	 * @param months The number of months to add.
	 */
	public static Date addMonths(Date date, int months) {
		Calendar calendar = new Calendar(date);
		calendar.addMonths(months);
		return calendar.toDate();
	}

	/**
	 * Adds hours minutes and seconds to a time.
	 *
	 * @param time Time starting time
	 * @param hours Number of hours to add
	 * @param minutes Number of minutes to add
	 * @param seconds Number of seconds to add
	 * @return The new time
	 */
	public static Time add(Time time, int hours, int minutes, int seconds) {
		Calendar calendar = new Calendar(time);
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		return calendar.toTime();
	}

	/**
	 * Creates a date.
	 *
	 * @return java.sql.Date
	 * @param year The year
	 * @param month The month
	 * @param day The day
	 */
	public static Date createDate(int year, int month, int day) {
		return new Date(new Calendar(year, month, day).getTimeInMillis());
	}

	/**
	 * Returns a boolean indicating if the year is leap.
	 * 
	 * @param year The year.
	 * @return A boolean indicating if the year is leap.
	 */
	public static boolean isLeap(int year) {
		Calendar calendar = new Calendar(year, 1, 1);
		return calendar.isLeapYear(year);
	}

	/**
	 * Returns the number of days of a year.
	 * 
	 * @param year The year.
	 * @return The number of days of a year.
	 */
	public static int getDaysOfYear(int year) {
		return (isLeap(year) ? 366 : 365);
	}

	/**
	 * Returns the number of days of a month.
	 *
	 * @param year The year
	 * @param month The month
	 * @return The number of days of the month
	 */
	public static int getDaysOfMonth(int year, int month) {
		if (month == 1) {
			return 31;
		}
		if (month == 2) {
			Calendar calendar = new Calendar(year, month, 1);
			return (calendar.isLeapYear(year) ? 29 : 28);
		}
		if (month == 3) {
			return 31;
		}
		if (month == 4) {
			return 30;
		}
		if (month == 5) {
			return 31;
		}
		if (month == 6) {
			return 30;
		}
		if (month == 7) {
			return 31;
		}
		if (month == 8) {
			return 31;
		}
		if (month == 9) {
			return 30;
		}
		if (month == 10) {
			return 31;
		}
		if (month == 11) {
			return 30;
		}
		if (month == 12) {
			return 31;
		}
		return 0;
	}

	/**
	 * Returns the last week of a year.
	 *
	 * @param year The year
	 * @return The last week
	 */
	public static int getLastWeekOfYear(int year) {
		Calendar c = new Calendar(year, 12, 20);
		int lastWeek = c.getWeek();
		while (c.getYear() == year) {
			c.add(WEEK_OF_YEAR, 1);
			int week = c.getWeek();
			if (week > lastWeek) {
				lastWeek = week;
			}
		}
		return lastWeek;
	}

	/**
	 * Returns an array of localized names of week days.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 * @param day The day of the week, use Calendar.MONDAY ...
	 */
	public static String getLongDay(Locale locale, boolean capitalized, int day) {
		return getLongDays(locale, capitalized)[day];
	}

	/**
	 * Returns an array of localized names of week days.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 */
	public static String[] getLongDays(Locale locale, boolean capitalized) {

		DateFormatSymbols sysd = new DateFormatSymbols(locale);
		String[] dsc = sysd.getWeekdays();
		if (capitalized) {
			for (int i = 0; i < dsc.length; i++) {
				dsc[i] = StringUtils.capitalize(dsc[i]);
			}
		}
		return dsc;
	}

	/**
	 * Returns an array of localized names of months.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 * @param month The month, use Calendar.JANUARY ...
	 */
	public static String getLongMonth(Locale locale, boolean capitalized, int month) {
		return getLongMonths(locale, capitalized)[month - 1];
	}

	/**
	 * Returns an array of localized names of months.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 */
	public static String[] getLongMonths(Locale locale, boolean capitalized) {

		DateFormatSymbols sysd = new DateFormatSymbols(locale);
		String[] dsc = sysd.getMonths();
		if (capitalized) {
			for (int i = 0; i < dsc.length; i++) {
				dsc[i] = StringUtils.capitalize(dsc[i]);
			}
		}
		return dsc;
	}

	/**
	 * Returns an array of localized names of week days.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 * @param day The day of the week, use Calendar.MONDAY ...
	 */
	public static String getShortDay(Locale locale, boolean capitalized, int day) {
		return getShortDays(locale, capitalized)[day];
	}

	/**
	 * Returns an array of localized names of week days.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 */
	public static String[] getShortDays(Locale locale, boolean capitalized) {

		DateFormatSymbols sysd = new DateFormatSymbols(locale);
		String[] dsc = sysd.getShortWeekdays();
		if (capitalized) {
			for (int i = 0; i < dsc.length; i++) {
				dsc[i] = StringUtils.capitalize(dsc[i]);
			}
		}
		return dsc;
	}

	/**
	 * Returns an array of localized names of months.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 * @param month The month, use Calendar.JANUARY ...
	 */
	public static String getShortMonth(Locale locale, boolean capitalized, int month) {
		return getShortMonths(locale, capitalized)[month];
	}

	/**
	 * Returns an array of localized names of months.
	 *
	 * @return An array of names.
	 * @param locale The desired locale.
	 * @param capitalized A boolean to capitalize the name.
	 */
	public static String[] getShortMonths(Locale locale, boolean capitalized) {

		DateFormatSymbols sysd = new DateFormatSymbols(locale);
		String[] dsc = sysd.getShortMonths();
		if (capitalized) {
			for (int i = 0; i < dsc.length; i++) {
				dsc[i] = StringUtils.capitalize(dsc[i]);
			}
		}
		return dsc;
	}

	/**
	 * Returns the number of weeks elapsed.
	 *
	 * @param date0 Start date
	 * @param date1 End date
	 * @return The number of weeks elapsed.
	 */
	public static int weeksElapsed(Date date0, Date date1) {
		Calendar c0 = new Calendar(date0);
		Calendar c1 = new Calendar(date1);
		int year0 = c0.getYear();
		int week0 = c0.getWeek();
		int year1 = c1.getYear();
		int week1 = c1.getWeek();
		return weeksElapsed(year0, week0, year1, week1);
	}

	/**
	 * Returns the number of weeks elapsed.
	 *
	 * @param year0 Start year
	 * @param week0 Start week
	 * @param year1 End year
	 * @param week1 End week
	 * @return The number of weeks
	 */
	public static int weeksElapsed(int year0, int week0, int year1, int week1) {
		int yearStart;
		int weekStart;
		int yearEnd;
		int weekEnd;
		if (year0 == year1) {
			if (week0 <= week1) {
				yearStart = year0;
				weekStart = week0;
				yearEnd = year1;
				weekEnd = week1;
			} else {
				yearStart = year1;
				weekStart = week1;
				yearEnd = year0;
				weekEnd = week0;
			}
		} else if (year0 < year1) {
			yearStart = year0;
			weekStart = week0;
			yearEnd = year1;
			weekEnd = week1;
		} else {
			yearStart = year1;
			weekStart = week1;
			yearEnd = year0;
			weekEnd = week0;
		}
		int weeks;
		if (yearStart == yearEnd) {
			weeks = weekEnd - weekStart;
		} else {
			int lastWeek = getLastWeekOfYear(yearStart);
			weeks = (lastWeek - weekStart) + weekEnd;
		}
		return weeks;
	}

	/**
	 * Default constructor.
	 */
	public Calendar() {
		super();
	}

	/**
	 * Constructor assigning year, month and day.
	 *
	 * @param year The year.
	 * @param month The month from 1 to 12.
	 * @param day The day.
	 */
	public Calendar(int year, int month, int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Constructor assigning year, month, day, hour and minute.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 */
	public Calendar(int year, int month, int day, int hour, int minute) {
		this(year, month, day, hour, minute, 0, 0);
	}

	/**
	 * Constructor assigning year, month, day, hour and minute.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 */
	public Calendar(int year, int month, int day, int hour, int minute, int second) {
		this(year, month, day, hour, minute, second, 0);
	}

	/**
	 * Constructor assigning year, month, day, hour and minute.
	 *
	 * @param year The year
	 * @param month The month, from 1 to 12
	 * @param day The day
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @param millis The millisecond
	 */
	public Calendar(int year, int month, int day, int hour, int minute, int second, int millis) {
		super(year, month - 1, day, hour, minute, second);
		setMilliSecond(millis);
	}

	/**
	 * Constructor assigning a <code>Date</code>.
	 *
	 * @param date The <code>Date</code>.
	 */
	public Calendar(java.sql.Date date) {
		setTimeInMillis(date.getTime());
	}

	/**
	 * Constructor assigning a <code>Time</code>.
	 *
	 * @param time The <code>Time</code>.
	 */
	public Calendar(java.sql.Time time) {
		setTimeInMillis(time.getTime());
	}

	/**
	 * Constructor assigning a <code>Timestamp</code>.
	 *
	 * @param timestamp The <code>Timestamp</code>.
	 */
	public Calendar(java.sql.Timestamp timestamp) {
		setTimeInMillis(timestamp.getTime());
	}

	/**
	 * Constructor assigning the time in millis.
	 *
	 * @param timeInMillis The time in millis
	 */
	public Calendar(long timeInMillis) {
		super();
		setTimeInMillis(timeInMillis);
	}

	/**
	 * Add the argument number of years to this calendar.
	 * 
	 * @param years The number of years to add.
	 */
	public void addYears(int years) {
		add(YEAR, years);
	}

	/**
	 * Add the argument number of months to this calendar.
	 * 
	 * @param months The number of months to add.
	 */
	public void addMonths(int months) {
		add(MONTH, months);
	}

	/**
	 * Add the argument number of weeks to this calendar.
	 * 
	 * @param weeks The number of weeks to add.
	 */
	public void addWeeks(int weeks) {
		add(WEEK_OF_YEAR, weeks);
	}

	/**
	 * Add the argument number of days to this calendar.
	 * 
	 * @param days The number of days to add.
	 */
	public void addDays(int days) {
		add(DATE, days);
	}

	/**
	 * Add the argument number of hours to this calendar.
	 * 
	 * @param hours The number of hours to add.
	 */
	public void addHours(int hours) {
		add(HOUR, hours);
	}

	/**
	 * Add the argument number of minutes to this calendar.
	 * 
	 * @param minutes The number of minutes to add.
	 */
	public void addMinutes(int minutes) {
		add(MINUTE, minutes);
	}

	/**
	 * Add the argument number of seconds to this calendar.
	 * 
	 * @param seconds The number of seconds to add.
	 */
	public void addSeconds(int seconds) {
		add(SECOND, seconds);
	}

	/**
	 * Add the argument number of milliseconds to this calendar.
	 * 
	 * @param millis The number of milliseconds to add.
	 */
	public void addMillis(int millis) {
		add(MILLISECOND, millis);
	}

	/**
	 * Get the day.
	 *
	 * @return The day
	 */
	public int getDay() {
		return get(DAY_OF_MONTH);
	}

	/**
	 * Get the day of the week.
	 *
	 * @return The day of the week.
	 */
	public int getDayOfWeek() {
		return get(DAY_OF_WEEK);
	}

	/**
	 * Get the day of the year.
	 *
	 * @return The day of the year.
	 */
	public int getDayOfYear() {
		return get(DAY_OF_YEAR);
	}

	/**
	 * Get the hour.
	 *
	 * @return The hour.
	 */
	public int getHour() {
		return get(HOUR_OF_DAY);
	}

	/**
	 * Get the number of milliseconds.
	 *
	 * @return The number of milliseconds.
	 */
	public int getMilliSecond() {
		return get(MILLISECOND);
	}

	/**
	 * Get the minutes.
	 *
	 * @return The minutes.
	 */
	public int getMinute() {
		return get(MINUTE);
	}

	/**
	 * Get the month.
	 *
	 * @return he month.
	 */
	public int getMonth() {
		return get(MONTH) + 1;
	}

	/**
	 * Get The seconds.
	 *
	 * @return The seconds.
	 */
	public int getSecond() {
		return get(SECOND);
	}

	/**
	 * Get The week of the year.
	 *
	 * @return The week of the year.
	 */
	public int getWeek() {
		return get(WEEK_OF_YEAR);
	}

	/**
	 * Get the year.
	 *
	 * @return The year.
	 */
	public int getYear() {
		return get(YEAR);
	}

	/**
	 * Set the day.
	 *
	 * @param day The day.
	 */
	public void setDay(int day) {
		set(DAY_OF_MONTH, day);
	}

	/**
	 * Set the hour.
	 *
	 * @param hour The hour.
	 */
	public void setHour(int hour) {
		set(HOUR_OF_DAY, hour);
	}

	/**
	 * Set the time as a <b>long</b>.
	 *
	 * @param timeInMillis The time as a <b>long</b>.
	 */
	public void setLong(long timeInMillis) {
		setTimeInMillis(timeInMillis);
	}

	/**
	 * Set the milliseconds.
	 *
	 * @param milliSecond The milliseconds.
	 */
	public void setMilliSecond(int milliSecond) {
		set(MILLISECOND, milliSecond);
	}

	/**
	 * Set the minute.
	 *
	 * @param minute he minute.
	 */
	public void setMinute(int minute) {
		set(MINUTE, minute);
	}

	/**
	 * Set the month.
	 *
	 * @param month The month.
	 */
	public void setMonth(int month) {
		set(MONTH, month - 1);
	}

	/**
	 * Set the seconds.
	 *
	 * @param second The seconds.
	 */
	public void setSecond(int second) {
		set(SECOND, second);
	}

	/**
	 * Set the year.
	 *
	 * @param year The year.
	 */
	public void setYear(int year) {
		set(YEAR, year);
	}

	/**
	 * Set the week of the year.
	 *
	 * @param week The week of the year.
	 */
	public void setWeek(int week) {
		set(WEEK_OF_YEAR, week);
	}

	/**
	 * Convert this <code>Calendar</code> to a clean <code>Date</code>.
	 *
	 * @return The clean <code>Date</code>, that is, with hours, minutes, seconds and milliseconds set to zero.
	 */
	public Date toDate() {
		return new Date(getTimeInMillis());
	}

	/**
	 * Get this calendar as a time <code>Time</code>.
	 *
	 * @return The <code>Time</code>.
	 */
	public Time toTime() {
		return new Time(getTimeInMillis());
	}

	/**
	 * Get this calendar as a <code>Timestamp</code>.
	 *
	 * @return The <code>Timestamp</code>.
	 */
	public Timestamp toTimestamp() {
		return new Timestamp(getTimeInMillis());
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		return toTimestamp().toString();
	}
}
