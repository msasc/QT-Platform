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
package com.qtplaf.library.trading.data;

import java.util.Locale;

import com.qtplaf.library.util.TextServer;

/**
 * Units used to define periods of aggregate incoming quotes.
 * 
 * @author Miquel Sas
 */
public enum Unit {
	Millisecond("MS"),
	Second("SC"),
	Minute("MN"),
	Hour("HR"),
	Day("DY"),
	Week("WK"),
	Month("MT"),
	Year("YR");

	/**
	 * Returns the unit of the given id.
	 * 
	 * @param id The unit id.
	 * @return The unit.
	 */
	public static Unit parseId(String id) {
		Unit[] units = values();
		for (Unit unit : units) {
			if (unit.getId().equals(id.toUpperCase())) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Invalid unit id: " + id);
	}

	/** 2 char id. */
	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id Two char id.
	 */
	private Unit(String id) {
		this.id = id;
	}

	/**
	 * Returns the two char id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the short description.
	 * 
	 * @return The short description.
	 */
	public String getShortDescription() {
		switch (this) {
		case Millisecond:
			return TextServer.getString("tradeUnitMillisecondDescription", Locale.UK);
		case Second:
			return TextServer.getString("tradeUnitSecondDescription", Locale.UK);
		case Minute:
			return TextServer.getString("tradeUnitMinuteDescription", Locale.UK);
		case Hour:
			return TextServer.getString("tradeUnitHourDescription", Locale.UK);
		case Day:
			return TextServer.getString("tradeUnitDayDescription", Locale.UK);
		case Week:
			return TextServer.getString("tradeUnitWeekDescription", Locale.UK);
		case Month:
			return TextServer.getString("tradeUnitMonthDescription", Locale.UK);
		case Year:
			return TextServer.getString("tradeUnitYearDescription", Locale.UK);
		default:
			throw new IllegalArgumentException();
		}
	}
}
