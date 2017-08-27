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
package com.qtplaf.library.swing.formatters;

import java.sql.Date;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.MaskFormatter;

import com.qtplaf.library.util.FormatUtils;

/**
 * A date formatter that acts as expected.
 * 
 * @author Miquel Sas
 */
public class DateFormatter extends MaskFormatter {

	/**
	 * The locale.
	 */
	private Locale locale = null;
	/**
	 * The original date mask used.
	 */
	private String mask;

	/**
	 * Constructor assigning the locale.
	 * 
	 * @param locale The locale to use.
	 * @throws ParseException If a parse error occurs.
	 */
	public DateFormatter(Locale locale) throws ParseException {
		super();
		this.locale = locale;
		this.mask = FormatUtils.getNormalizedDatePattern(locale);
		// Set the appropriate mask to the MaskFormatter.
		setMask(getDateMask(mask));
	}

	/**
	 * Convert the string to a value.
	 */
	@Override
	public Object stringToValue(String value) throws ParseException {
		return FormatUtils.formattedToDate(value, locale);
	}

	/**
	 * Convert the value to a string.
	 */
	@Override
	public String valueToString(Object value) throws ParseException {
		String str = FormatUtils.formattedFromDate((Date) value, locale);
		if (str.trim().length() == 0) {
			return super.valueToString(value);
		}
		return str;
	}

	/**
	 * Returns the mask that this mask formatter must use.
	 * 
	 * @param datePattern The date pattern.
	 * @return The mask.
	 */
	private String getDateMask(String datePattern) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < datePattern.length(); i++) {
			char c = datePattern.charAt(i);
			switch (c) {
			case 'd':
			case 'M':
			case 'y':
			case 'H':
			case 'm':
			case 's':
				b.append("#");
				break;
			default:
				b.append(c);
				break;
			}
		}
		return b.toString();
	}

}
