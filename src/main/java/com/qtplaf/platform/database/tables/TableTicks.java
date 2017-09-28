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
package com.qtplaf.platform.database.tables;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Tick;

/**
 * A table to store tick elements.
 * 
 * @author Miquel Sas
 */
public class TableTicks extends Table {

	/**
	 * Returns a list of values given a tick data element.
	 * 
	 * @param tick The tick data element.
	 * @return The list of values.
	 */
	public static List<Value> getValues(Tick tick) {
		List<Value> values = new ArrayList<>();
		values.add(new Value(tick.getTime()));
		values.add(new Value(tick.getAsk().getValue()));
		values.add(new Value(tick.getAsk().getVolume()));
		values.add(new Value(tick.getBid().getValue()));
		values.add(new Value(tick.getBid().getVolume()));
		return values;
	}

	/**
	 * Returns a tick data element given the corresponding list of values.
	 * 
	 * @param values The list of values.
	 * @return The tick data element
	 */
	public static Tick getData(List<Value> values) {
		if (values.size() != 5) {
			throw new IllegalArgumentException("Invalid list of values: size must be 5");
		}
		if (!values.get(0).getType().equals(Types.LONG)) {
			throw new IllegalArgumentException("Invalid list of values: type of element 0  must be Long");
		}
		for (int i = 1; i < values.size(); i++) {
			if (!values.get(i).getType().equals(Types.DOUBLE)) {
				throw new IllegalArgumentException(
					"Invalid list of values: type of first elements 1 to 4 must be Double");
			}
		}
		Tick tick = new Tick();
		tick.setTime(values.get(0).getLong());
		tick.addAsk(values.get(1).getDouble(), values.get(2).getDouble());
		tick.addBid(values.get(3).getDouble(), values.get(4).getDouble());
		return tick;
	}

	/**
	 * Instrument.
	 */
	private Instrument instrument;

	/**
	 * Constructor using the fields.
	 * 
	 * @param instrument The instrument.
	 */
	public TableTicks(Instrument instrument) {
		super();

		// Rgister the parameters.
		this.instrument = instrument;

		// Set the standard name base on the parameters.
		// setName(Names.getTickName(instrument));

		// Configure the segments.

		// Tick time.
		Field time = new Field();
		time.setName("TIME");
		time.setAlias("TIME");
		time.setTitle("Time");
		time.setLabel("Time");
		time.setHeader("Time");
		time.setType(Types.LONG);
		time.setPrimaryKey(true);
		addField(time);

		// Ask price.
		Field askPrice = new Field();
		askPrice.setName("ASKPRICE");
		askPrice.setAlias("ASKPRICE");
		askPrice.setTitle("Ask price");
		askPrice.setLabel("Ask price");
		askPrice.setHeader("Ask price");
		askPrice.setType(Types.DOUBLE);
		addField(askPrice);

		// Ask volume.
		Field askVolume = new Field();
		askVolume.setName("ASKVOLUME");
		askVolume.setAlias("ASKVOLUME");
		askVolume.setTitle("Ask volume");
		askVolume.setLabel("Ask volume");
		askVolume.setHeader("Ask volume");
		askVolume.setType(Types.DOUBLE);
		addField(askVolume);

		// Bid price.
		Field bidPrice = new Field();
		bidPrice.setName("BIDPRICE");
		bidPrice.setAlias("BIDPRICE");
		bidPrice.setTitle("Bid price");
		bidPrice.setLabel("Bid price");
		bidPrice.setHeader("Bid price");
		bidPrice.setType(Types.DOUBLE);
		addField(bidPrice);

		// Bid volume.
		Field bidVolume = new Field();
		bidVolume.setName("BIDVOLUME");
		bidVolume.setAlias("BIDVOLUME");
		bidVolume.setTitle("Bid volume");
		bidVolume.setLabel("Bid volume");
		bidVolume.setHeader("Bid volume");
		bidVolume.setType(Types.DOUBLE);
		addField(bidVolume);
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Returns a record given a tick data element.
	 * 
	 * @param tick The tick data element.
	 * @return The corresponding record.
	 */
	public Record getRecord(Tick tick) {
		Record record = getFieldList().getRecord(getValues(tick));
		record.setPersistor(getPersistor());
		return record;
	}
}
