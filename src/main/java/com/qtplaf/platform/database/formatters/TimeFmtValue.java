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

package com.qtplaf.platform.database.formatters;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField.AbstractFormatter;

import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Unit;
import com.qtplaf.library.util.Timestamp;
import com.qtplaf.platform.database.Fields;

/**
 * A formatter for Bar time that adapts to the period.
 * 
 * @author Miquel Sas
 */
public class TimeFmtValue extends AbstractFormatter implements Calculator {
	
	/** Date format. */
	SimpleDateFormat format;

	/**
	 * Constructor.
	 * 
	 * @param unit The unit.
	 */
	public TimeFmtValue(Unit unit) {
		super();
		
		// Build a convenient date format using the period.
		String pattern = null;
		switch (unit) {
		case Millisecond:
			pattern = "yyyy-MM-dd HH:mm:ss.SSS";
			break;
		case Second:
			pattern = "yyyy-MM-dd HH:mm:ss";
			break;
		case Minute:
			pattern = "yyyy-MM-dd HH:mm";
			break;
		case Hour:
			pattern = "yyyy-MM-dd HH";
			break;
		case Day:
			pattern = "yyyy-MM-dd";
			break;
		case Week:
			pattern = "yyyy-MM-dd";
			break;
		case Month:
			pattern = "yyyy-MM";
			break;
		case Year:
			pattern = "yyyy";
			break;
		default:
			throw new IllegalArgumentException();
		}
		format = new SimpleDateFormat(pattern);
	}


	@Override
	public Object stringToValue(String text) throws ParseException {
		return null;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value instanceof Value) {
			Value time = (Value) value;
			return format.format(new Timestamp(time.getLong()));
		}
		return value.toString();
	}

	@Override
	public Value getValue(Record record) {
		return record.getValue(Fields.TIME);
	}
}
