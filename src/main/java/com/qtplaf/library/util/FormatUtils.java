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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;

/**
 * General formatting utilities.
 * 
 * @author Miquel Sas
 */
public class FormatUtils {
	/** Format index. */
	private static int propertyIndex = 0;
	/** The normalized date format key. */
	private static final int NORMALIZED_DATE_FORMAT = propertyIndex++;
	/** The normalized time format key. */
	private static final int NORMALIZED_TIME_FORMAT = propertyIndex++;
	/** The normalized timestamp format. */
	private static final int NORMALIZED_TIMESTAMP_FORMAT = propertyIndex++;

	/**
	 * Date, time and timestamp mask chars.
	 */
	public static final String TIMESTAMP_MASK_CHARS = "dMyHmsS";
	/**
	 * The map that will store properties per locale.
	 */
	private static Map<Locale, Map<Integer, Object>> properties = new HashMap<>();

	/**
	 * Returns the normalized date pattern for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized date pattern.
	 */
	public static String getNormalizedDatePattern(Locale locale) {
		return getNormalizedDateFormat(locale).toPattern();
	}

	/**
	 * Returns the list of separators in a normalized date, time or timestamp pattern.
	 * 
	 * @param pattern the pattern.
	 * @return The list of separators.
	 */
	public static List<Character> getNormalizedPatternSeparators(String pattern) {
		List<Character> separators = new ArrayList<>();
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			// Skip spaces
			if (c == ' ') {
				continue;
			}
			// Skip characters in the timestamp mask chars
			if (TIMESTAMP_MASK_CHARS.indexOf(c) >= 0) {
				continue;
			}
			// Add the character as a separator
			separators.add(c);
		}
		return separators;
	}

	/**
	 * Returns the normalized simple date format for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized date format.
	 */
	public static SimpleDateFormat getNormalizedDateFormat(Locale locale) {
		return (SimpleDateFormat) getProperties(locale).get(NORMALIZED_DATE_FORMAT);
	}

	/**
	 * Returns the normalized time pattern for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized time pattern.
	 */
	public static String getNormalizedTimePattern(Locale locale) {
		return getNormalizedTimeFormat(locale).toPattern();
	}

	/**
	 * Returns the normalized simple time format for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized time format.
	 */
	public static SimpleDateFormat getNormalizedTimeFormat(Locale locale) {
		return (SimpleDateFormat) getProperties(locale).get(NORMALIZED_TIME_FORMAT);
	}

	/**
	 * Returns the normalized time stamp pattern for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized time stamp pattern.
	 */
	public static String getNormalizedTimestampPattern(Locale locale) {
		return getNormalizedTimestampFormat(locale).toPattern();
	}

	/**
	 * Returns the normalized simple timestamp format for the argument locale.
	 * 
	 * @param locale The applying locale.
	 * @return The normalized timestamp format.
	 */
	public static SimpleDateFormat getNormalizedTimestampFormat(Locale locale) {
		return (SimpleDateFormat) getProperties(locale).get(NORMALIZED_TIMESTAMP_FORMAT);
	}

	/**
	 * Returns the normalized pattern for a date or time.
	 * <p>
	 * The normalized pattern for a date, or the date part of a time stamp, is always two digit long for days and months
	 * and four digit long for the year.
	 * <p>
	 * The normalized pattern for a time, or the time part of a time stamp, is always two digit long for the hour, the
	 * minute and the second.
	 * <p>
	 * In a time or time stamp, the millisecond part is always three digit long.
	 * <p>
	 * The hour is always converted to the 0-23 format (H).
	 * 
	 * @return The normalized pattern.
	 * @param pattern The original pattern.
	 */
	public static String getNormalizedPattern(String pattern) {
		StringBuilder b = new StringBuilder();
		int index = 0;
		int length = pattern.length();
		while (index < length) {
			char c = pattern.charAt(index);
			char origChar = c;
			if (c == 'a') {
				index++;
				continue;
			}
			if (c == 'k' || c == 'K' || c == 'h') {
				c = 'H';
			}
			if (c == 'd' || c == 'M' || c == 'y' || c == 'H' || c == 'm' || c == 's' || c == 'S') {
				switch (c) {
				case 'd':
					b.append("dd");
					break;
				case 'M':
					b.append("MM");
					break;
				case 'y':
					b.append("yyyy");
					break;
				case 'H':
					b.append("HH");
					break;
				case 'm':
					b.append("mm");
					break;
				case 's':
					b.append("ss");
					break;
				case 'S':
					b.append("SSS");
					break;
				default:
					throw new IllegalArgumentException();
				}
				while (index < length && pattern.charAt(index) == origChar) {
					index++;
				}
				continue;
			}
			b.append(c);
			index++;
		}
		return b.toString().trim();
	}


	/**
	 * Returns the set of properties given the locale. If the properties has not already been set it sets them, thus
	 * always returning a valid map of properties.
	 * 
	 * @param locale The locale.
	 * @return A map with the properties.
	 */
	synchronized private static Map<Integer, Object> getProperties(Locale locale) {
		Map<Integer, Object> localeProperties = properties.get(locale);
		if (localeProperties == null) {
			localeProperties = new HashMap<>();

			// Normalized date format and pattern
			SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
			String datePattern = getNormalizedPattern(dateFormat.toPattern());
			dateFormat = new SimpleDateFormat(datePattern, DateFormatSymbols.getInstance(locale));
			localeProperties.put(NORMALIZED_DATE_FORMAT, dateFormat);

			// Normalized time format and pattern
			SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
			String timePattern = getNormalizedPattern(timeFormat.toPattern());
			timeFormat = new SimpleDateFormat(timePattern, DateFormatSymbols.getInstance(locale));
			localeProperties.put(NORMALIZED_TIME_FORMAT, timeFormat);

			// Normalized timestamp format and pattern
			SimpleDateFormat timestampFormat =
				(SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
			String timestampPattern = getNormalizedPattern(timestampFormat.toPattern());
			timestampFormat = new SimpleDateFormat(timestampPattern, DateFormatSymbols.getInstance(locale));
			localeProperties.put(NORMALIZED_TIMESTAMP_FORMAT, timestampFormat);

			// Store the locale properties
			properties.put(locale, localeProperties);
		}

		return localeProperties;
	}

	/**
	 * Returns a default number formatter.
	 * 
	 * @param type The type, must be a number type.
	 * @param length The length, -1 no limit.
	 * @param decimals The number of decimal places, -1 no limit.
	 * @param locale The applying locale.
	 * @return The default number formatting.
	 * @throws ParseException Is any parsing error occurs.
	 */
	public static JFormattedTextField.AbstractFormatter getFormatterNumber(
		Types type,
		int length,
		int decimals,
		Locale locale)
		throws ParseException {

		if (!type.isNumber()) {
			throw new IllegalArgumentException("Type is not a number type: " + type);
		}

		StringBuilder pattern = new StringBuilder("#,##0");
		if (decimals > 0) {
			pattern.append(".0#");
		} else {
			// Case double any decimals
			if (decimals < 0 && type.isDouble()) {
				pattern.append(".0#");
			}
		}
		DecimalFormat format = new DecimalFormat(pattern.toString(), DecimalFormatSymbols.getInstance(locale));

		// Set exactly the number of integer and fractional positions.
		if (length > 0) {
			int integerDigits = length;
			if (decimals > 0) {
				integerDigits = length - decimals - 1;
			}
			format.setMaximumIntegerDigits(integerDigits);
			format.setMinimumIntegerDigits(1);
		}
		if (decimals >= 0) {
			int fractionalDigits = decimals;
			format.setMaximumFractionDigits(fractionalDigits);
			format.setMinimumFractionDigits(fractionalDigits);
		}

		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setAllowsInvalid(false);
		// Case double any decimals
		if (decimals < 0 && type.isDouble()) {
			formatter.setAllowsInvalid(true);
		}

		return formatter;
	}

	/**
	 * Convert from a <i>BigDecimal</i> forcing the scale.
	 * 
	 * @return A string.
	 * @param number A number as a <code>java.math.BigDecimal</code>
	 * @param scale The scale.
	 * @param locale The desired locale.
	 */
	public static String formattedFromBigDecimal(BigDecimal number, int scale, Locale locale) {
		NumberFormat formatted = NumberFormat.getNumberInstance(locale);
		formatted.setMaximumFractionDigits(scale);
		formatted.setMinimumFractionDigits(scale);
		return formatted.format(number.doubleValue());
	}

	/**
	 * Convert from a <i>BigDecimal</i> forcing the scale.
	 * 
	 * @return A string.
	 * @param number A number as a <code>java.math.BigDecimal</code>
	 * @param locale The desired locale.
	 */
	public static String formattedFromBigDecimal(BigDecimal number, Locale locale) {
		return formattedFromBigDecimal(number, number.scale(), locale);
	}

	/**
	 * Returns the formatted string representation of a boolean.
	 * 
	 * @param bool The boolean value
	 * @param locale The locale to use
	 * @return The formatted string representation
	 */
	public static String formattedFromBoolean(boolean bool, Locale locale) {
		if (bool) {
			return TextServer.getString("tokenYes", locale);
		}
		return TextServer.getString("tokenNo", locale);
	}

	/**
	 * Convert from a <i>Date</i>.
	 * 
	 * @return A string.
	 * @param date The <i>Date</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromDate(java.sql.Date date, Locale locale) {
		if (date == null) {
			return "";
		}
		return getNormalizedDateFormat(locale).format(date);
	}

	/**
	 * Convert from a <i>Timestamp</i>.
	 * 
	 * @return A string.
	 * @param timestamp The <i>Timestamp</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromTimestamp(java.sql.Timestamp timestamp, Locale locale) {
		if (timestamp == null) {
			return "";
		}
		return getNormalizedTimestampFormat(locale).format(timestamp);
	}

	/**
	 * Convert from a <i>Time</i>.
	 * 
	 * @return A string.
	 * @param time The <i>Time</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromTime(java.sql.Time time, Locale locale) {
		if (time == null) {
			return "";
		}
		return getNormalizedTimeFormat(locale).format(time);
	}

	/**
	 * Convert from a <i>double</i>.
	 * 
	 * @return A string.
	 * @param d The <i>double</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromDouble(double d, Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(d);
	}

	/**
	 * Convert from a <i>BigDecimal</i> forcing the scale.
	 * 
	 * @return A string.
	 * @param d The <i>double</i> to convert.
	 * @param scale The scale.
	 * @param locale The desired locale.
	 */
	public static String formattedFromDouble(double d, int scale, Locale locale) {
		NumberFormat formatted = NumberFormat.getNumberInstance(locale);
		formatted.setMaximumFractionDigits(scale);
		formatted.setMinimumFractionDigits(scale);
		return formatted.format(d);
	}

	/**
	 * Convert from an <i>int</i>.
	 * 
	 * @return A string.
	 * @param i The <i>int</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromInteger(int i, Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(i);
	}

	/**
	 * Convert from an <i>long</i>.
	 * 
	 * @return A string.
	 * @param l The <i>long</i> to convert.
	 * @param locale The locale to apply.
	 */
	public static String formattedFromLong(long l, Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(l);
	}

	/**
	 * Convert from a <i>Value</i>.
	 * 
	 * @return A string.
	 * @param value The <i>Value</i> to convert.
	 * @param locale The locale to apply.
	 */
	@SuppressWarnings("incomplete-switch")
	public static String formattedFromValue(Value value, Locale locale) {
		switch (value.getType()) {
		case Boolean:
			return formattedFromBoolean(value.getBoolean(), locale);
		case Decimal:
			return formattedFromBigDecimal(value.getBigDecimal(), value.getDecimals(), locale);
		case Date:
			return formattedFromDate(value.getDate(), locale);
		case Double:
			return formattedFromDouble(value.getDouble(), locale);
		case Integer:
			return formattedFromInteger(value.getInteger(), locale);
		case Long:
			return formattedFromLong(value.getLong(), locale);
		case String:
			return value.getString();
		case Time:
			return formattedFromTime(value.getTime(), locale);
		case Timestamp:
			return formattedFromTimestamp(value.getTimestamp(), locale);
		/** Types that do not support a formated conversion. */
		case ByteArray:
		case Object:
		case Value:
		case ValueArray:
		}
		return value.toString();
	}

	/**
	 * Convert to <i>BigDecimal</i> from a formatted string.
	 * 
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return A <i>BigDecimal</i>
	 * @throws ParseException If such exception occurs.
	 */
	public static BigDecimal formattedToBigDecimal(String str, Locale locale) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return new BigDecimal(NumberFormat.getNumberInstance(locale).parse(str).toString());
	}

	/**
	 * Convert to <i>Boolean</i> from a formatted string.
	 * 
	 * @return A <i>Boolean</i>
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return The parsed Boolean.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static Boolean formattedToBoolean(String str, Locale locale) throws ParseException {
		String yes = TextServer.getString("tokenYes", locale);
		if (str.toLowerCase().equals(yes.toLowerCase())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Convert to <i>Date</i> from a formatted string.
	 * 
	 * @return A <i>Date</i>
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return The parsed date.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static Date formattedToDate(String str, Locale locale) throws ParseException {
		try {
			java.util.Date date = getNormalizedDateFormat(locale).parse(str);
			return new Date(date.getTime());
		} catch (ParseException exc) {
			return null;
		}
	}

	/**
	 * Convert to <i>double</i> from a formatted string.
	 * 
	 * @return A <i>double</i>
	 * @param str The formatted string to convert.
	 * @param loc The locale to apply.
	 * @return The parsed double.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static double formattedToDouble(String str, Locale loc) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(loc).parse(str).doubleValue();
	}

	/**
	 * Convert to <i>int</i> from a formatted string.
	 * 
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return An <i>int</i>
	 * @throws ParseException If such exception occurs.
	 */
	public static int formattedToInteger(String str, Locale locale) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(locale).parse(str).intValue();
	}

	/**
	 * Convert to <i>long</i> from a formatted string.
	 * 
	 * @return A <i>long</i>
	 * @param str The formatted string to convert.
	 * @param loc The locale to apply.
	 * @return The parsed long.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static long formattedToLong(String str, Locale loc) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(loc).parse(str).longValue();
	}

	/**
	 * Convert to <i>Time</i> from a formatted string.
	 * 
	 * @return A <i>Time</i>
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return The time.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static Time formattedToTime(String str, Locale locale) throws ParseException {
		java.util.Date date = getNormalizedTimeFormat(locale).parse(str);
		return new Time(date.getTime());
	}

	/**
	 * Convert to <i>Timestamp</i>
	 * 
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return The time stamp.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static Timestamp formattedToTimestamp(String str, Locale locale) throws ParseException {
		java.util.Date date = getNormalizedTimestampFormat(locale).parse(str);
		return new Timestamp(date.getTime());
	}

	/**
	 * Convert to <i>Value</i>
	 * 
	 * @param type The type of the formatted string.
	 * @param str The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return The parsed value.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	@SuppressWarnings("incomplete-switch")
	public static Value formattedToValue(Types type, String str, Locale locale) throws ParseException {
		switch (type) {
		case Boolean:
			return new Value(formattedToBoolean(str, locale));
		case Decimal:
			return new Value(formattedToBigDecimal(str, locale));
		case Date:
			return new Value(formattedToDate(str, locale));
		case Double:
			return new Value(formattedToDouble(str, locale));
		case Integer:
			return new Value(formattedToInteger(str, locale));
		case Long:
			return new Value(formattedToLong(str, locale));
		case String:
			return new Value(str);
		case Time:
			return new Value(formattedToTime(str, locale));
		case Timestamp:
			return new Value(formattedToTimestamp(str, locale));
		/** Types that do not support a formated conversion. */
		case ByteArray:
		case Object:
		case Value:
		case ValueArray:
		}
		return new Value(str);
	}

	/**
	 * Convert a <i>BigDecimal</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param n A <i>BigDecimal</i>.
	 */
	public static String unformattedFromBigDecimal(BigDecimal n) {
		if (n == null) {
			return "";
		}
		return n.toPlainString();
	}

	/**
	 * Convert a <i>boolean</i> to the unformatted form (true/false).
	 * 
	 * @return A string.
	 * @param b A <i>boolean</i>.
	 */
	public static String unformattedFromBoolean(boolean b) {
		return (b ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
	}

	/**
	 * Convert a <i>Date</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param d A <i>Date</i>.
	 */
	public static String unformattedFromDate(java.sql.Date d) {
		if (d == null) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat ef = new SimpleDateFormat("G");
		String sdate = df.format(d);
		String sera = ef.format(d);
		return (sera.equals("BC") ? "-" + sdate : sdate);
	}

	/**
	 * Convert a <i>double</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param n A <i>double</i>.
	 */
	public static String unformattedFromDouble(double n) {
		return Double.toString(n);
	}

	/**
	 * Convert a <i>double</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param n A <i>double</i>.
	 * @param decimals The number of decimal places.
	 */
	public static String unformattedFromDouble(double n, int decimals) {
		return unformattedFromBigDecimal(new BigDecimal(n).setScale(decimals, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * Convert an <i>int</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param n An <i>int</i>.
	 */
	public static String unformattedFromInteger(int n) {
		return Integer.toString(n);
	}

	/**
	 * Convert a <i>long</i> to the unformatted form.
	 * 
	 * @return A string.
	 * @param n A <i>long</i>.
	 */
	public static String unformattedFromLong(long n) {
		return Long.toString(n);
	}

	/**
	 * Convert from a <i>Time</i> to an unlocalized string with the format <i>hhmmss</i>.
	 * 
	 * @return The string.
	 * @param t A <i>Time</i>.
	 */
	public static String unformattedFromTime(java.sql.Time t) {
		return unformattedFromTime(t, false);
	}

	/**
	 * Convert from a <i>Time</i> to an unlocalized string with the format <i>hhmmss</i> or <i>hhmmssnnn</i>.
	 * 
	 * @return The string.
	 * @param time A <i>Time</i>.
	 * @param millis A <i>boolean</i> to include milliseconds.
	 */
	public static String unformattedFromTime(java.sql.Time time, boolean millis) {
		if (time == null) {
			return "";
		}
		StringBuilder pattern = new StringBuilder();
		pattern.append("HHmmss");
		if (millis) {
			pattern.append("SSS");
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern.toString());
		return df.format(time);
	}

	/**
	 * Convert from a <i>Timestamp</i> to an unlocalized string with the format <b>yyyymmddhhmmss</b>.
	 * 
	 * @return The string.
	 * @param t A <i>Timestamp</i>.
	 */
	public static String unformattedFromTimestamp(java.sql.Timestamp t) {
		return unformattedFromTimestamp(t, true, false);
	}

	/**
	 * Convert from a <i>Timestamp</i> to an unlocalized string with the format <b>yyyymmddhhmmss</b> or
	 * <b>yyyymmddhhmmssnnn</b>
	 * 
	 * @return The string.
	 * @param timestamp A <code>Timestamp</code>.
	 * @param millis A <code>boolean</code> to include milliseconds.
	 */
	public static String unformattedFromTimestamp(java.sql.Timestamp timestamp, boolean millis) {
		return unformattedFromTimestamp(timestamp, millis, false);
	}

	/**
	 * Convert from a <i>Timestamp</i> to an unlocalized string with the format <b>yyyymmddhhmmss</b> or
	 * <b>yyyymmddhhmmssnnn</b>
	 * 
	 * @return The string.
	 * @param timestamp A <code>Timestamp</code>.
	 * @param millis A <code>boolean</code> to include milliseconds.
	 * @param separators A boolean to include standard separators
	 */
	public static String unformattedFromTimestamp(java.sql.Timestamp timestamp, boolean millis, boolean separators) {
		return unformattedFromTimestamp(timestamp, true, true, true, true, true, true, millis, separators);
	}

	/**
	 * Convert from a <i>Timestamp</i> to an unlocalized string with the format <b>yyyymmddhhmmss</b> or
	 * <b>yyyymmddhhmmssnnn</b>
	 * 
	 * @return The string.
	 * @param timestamp A <code>Timestamp</code>.
	 * @param year A <code>boolean</code> to include year.
	 * @param month A <code>boolean</code> to include month.
	 * @param day A <code>boolean</code> to include day.
	 * @param hour A <code>boolean</code> to include hour.
	 * @param minute A <code>boolean</code> to include minute.
	 * @param second A <code>boolean</code> to include second.
	 * @param millis A <code>boolean</code> to include milliseconds.
	 * @param separators A boolean to include standard separators
	 */
	public static String unformattedFromTimestamp(
		java.sql.Timestamp timestamp,
		boolean year,
		boolean month,
		boolean day,
		boolean hour,
		boolean minute,
		boolean second,
		boolean millis,
		boolean separators) {
		if (timestamp == null) {
			return "";
		}
		StringBuilder pattern = new StringBuilder();
		if (year) {
			pattern.append("yyyy");
		}
		if (month) {
			if (separators && pattern.length() != 0) {
				pattern.append("-");
			}
			pattern.append("MM");
		}
		if (day) {
			if (separators && pattern.length() != 0) {
				pattern.append("-");
			}
			pattern.append("dd");
		}
		if (hour) {
			if (separators && pattern.length() != 0) {
				pattern.append(" ");
			}
			pattern.append("HH");
		}
		if (minute) {
			if (separators && pattern.length() != 0) {
				pattern.append(":");
			}
			pattern.append("mm");
		}
		if (second) {
			if (separators && pattern.length() != 0) {
				pattern.append(":");
			}
			pattern.append("ss");
		}
		if (millis) {
			if (separators && pattern.length() != 0) {
				pattern.append(".");
			}
			pattern.append("SSS");
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern.toString());
		return df.format(timestamp);
	}

	/**
	 * Convert an unformatted string to <code>BigDecimal</code>.
	 * 
	 * @return A <code>BigDecimal</code>
	 * @param str The string to convert.
	 */
	public static BigDecimal unformattedToBigDecimal(String str) {
		if (str == null || str.length() == 0) {
			str = "0";
		}
		BigDecimal b = new BigDecimal(str);
		return b.setScale(b.scale(), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Convert a unformatted string to a <code>boolean</code>.
	 * 
	 * @return A <code>boolean</code>.
	 * @param str The string to convert.
	 */
	public static boolean unformattedToBoolean(String str) {
		return (str.toLowerCase().equals("true") ? true : false);
	}

	/**
	 * Convert an unformatted string to <code>Date</code>.
	 * 
	 * @return A <code>Date</code>
	 * @param str The string to convert.
	 */
	public static Date unformattedToDate(String str) {
		try {
			java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
			return new Date(fmt.parse(str).getTime());
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Convert an unformatted string to <code>double</code>.
	 * 
	 * @return A <code>double</code>
	 * @param str The string to convert.
	 */
	public static double unformattedToDouble(String str) {
		try {
			return Double.valueOf(str).doubleValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Convert an unformatted string to <code>int</code>.
	 * 
	 * @return An <code>int</code>
	 * @param str The string to convert.
	 */
	public static int unformattedToInteger(String str) {
		try {
			return Integer.valueOf(str).intValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Convert an unformatted string to <code>long</code>.
	 * 
	 * @return A <code>long</code>
	 * @param str The string to convert.
	 */
	public static long unformattedToLong(String str) {
		try {
			return Long.valueOf(str).longValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Convert a <code>Time</code> from an unlocalized string with the format <code>hhmmss</code>.
	 * 
	 * @return The <code>Time</code>.
	 * @param str The string to convert.
	 */
	public static Time unformattedToTime(String str) {
		return unformattedToTime(str, false);
	}

	/**
	 * Convert a <code>Time</code> from an unlocalized string with the format <code>hhmmss</code> or
	 * <code>hhmmssnnn</code>.
	 * 
	 * @return The <code>Time</code>.
	 * @param str The string to convert.
	 * @param millis A <code>boolean</code> indicating if the string contains millisecond data.
	 */
	public static Time unformattedToTime(String str, boolean millis) {
		try {
			int hour = Integer.parseInt(str.substring(0, 2));
			int minute = Integer.parseInt(str.substring(2, 4));
			if (millis) {
				int sec = Integer.parseInt(str.substring(4, 6));
				int msec = Integer.parseInt(str.substring(6, 9));
				Calendar calendar = new Calendar();
				calendar.setHour(hour);
				calendar.setMinute(minute);
				calendar.setSecond(sec);
				calendar.setMilliSecond(msec);
				return calendar.toTime();
			}
			Calendar calendar = new Calendar();
			calendar.setHour(hour);
			calendar.setMinute(minute);
			int sec = Integer.parseInt(str.substring(4));
			calendar.setSecond(sec);
			return calendar.toTime();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid unformatted time " + str);
		}
	}

	/**
	 * Convert a <code>Timestamp</code> from an unlocalized string with the format <code>yyyymmddhhmmss</code> or
	 * <code>yyyymmddhhmmssnnn</code> or <code>yyyy-mm-dd hh:mm:ss</code> or <code>yyyy-mm-dd hh:mm:ss.nnn</code>.
	 * 
	 * @return The <code>Timestamp</code>.
	 * @param str The string to convert.
	 */
	public static Timestamp unformattedToTimestamp(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			if (str.length() == 23) {
				int year = Integer.parseInt(str.substring(0, 4));
				int month = Integer.parseInt(str.substring(5, 7));
				int day = Integer.parseInt(str.substring(8, 10));
				int hour = Integer.parseInt(str.substring(11, 13));
				int min = Integer.parseInt(str.substring(14, 16));
				int sec = Integer.parseInt(str.substring(17, 19));
				int msec = Integer.parseInt(str.substring(20, 23));
				Calendar calendar = new Calendar(year, month, day, hour, min, sec, msec);
				return calendar.toTimestamp();
			} else if (str.length() == 19) {
				int year = Integer.parseInt(str.substring(0, 4));
				int month = Integer.parseInt(str.substring(5, 7));
				int day = Integer.parseInt(str.substring(8, 10));
				int hour = Integer.parseInt(str.substring(11, 13));
				int min = Integer.parseInt(str.substring(14, 16));
				int sec = Integer.parseInt(str.substring(17, 19));
				Calendar calendar = new Calendar(year, month, day, hour, min, sec);
				return calendar.toTimestamp();
			} else if (str.length() == 17) {
				int year = Integer.parseInt(str.substring(0, 4));
				int month = Integer.parseInt(str.substring(4, 6));
				int day = Integer.parseInt(str.substring(6, 8));
				int hour = Integer.parseInt(str.substring(8, 10));
				int min = Integer.parseInt(str.substring(10, 12));
				int sec = Integer.parseInt(str.substring(12, 14));
				int msec = Integer.parseInt(str.substring(14, 17));
				Calendar calendar = new Calendar(year, month, day, hour, min, sec, msec);
				return calendar.toTimestamp();
			} else if (str.length() == 14) {
				int year = Integer.parseInt(str.substring(0, 4));
				int month = Integer.parseInt(str.substring(4, 6));
				int day = Integer.parseInt(str.substring(6, 8));
				int hour = Integer.parseInt(str.substring(8, 10));
				int min = Integer.parseInt(str.substring(10, 12));
				int sec = Integer.parseInt(str.substring(12, 14));
				Calendar calendar = new Calendar(year, month, day, hour, min, sec);
				return calendar.toTimestamp();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid unformatted timestamp " + str);
		}
	}
}
