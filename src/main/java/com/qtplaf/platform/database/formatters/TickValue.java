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

import javax.swing.JFormattedTextField.AbstractFormatter;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.util.NumberUtils;

/**
 * A formatter for data tick value that adapts to the tick scale.
 * 
 * @author Miquel Sas
 */
public class TickValue extends AbstractFormatter {
	
	/** Session. */
	private Session session;
	/** Tick scale. */
	private int tickScale;
	
	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param instrument The instrument.
	 */
	public TickValue(Session session, Instrument instrument) {
		super();
		this.session = session;
		this.tickScale = instrument.getTickScale();
	}


	@Override
	public Object stringToValue(String text) throws ParseException {
		return null;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value instanceof Value) {
			Value pip = (Value) value;
			Value fmt = new Value(NumberUtils.getBigDecimal(pip.getDouble(), tickScale));
			return fmt.toStringFormatted(session.getLocale());
		}
		return value.toString();
	}
}
