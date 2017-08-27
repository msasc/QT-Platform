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
package com.qtplaf.library.database;

import java.util.Comparator;

/**
 * A comparator useful to compare records and sort them within a record set or list.
 *
 * @author Miquel Sas
 */
public class RecordComparator implements Comparator<Record> {

	/**
	 * The order.
	 */
	private Order order;

	/**
	 * Constructor using an order to get the key pointers.
	 * 
	 * @param order The order.
	 */
	public RecordComparator(Order order) {
		super();
		this.order = order;
	}

	/**
	 * Compares two records based on the order key.
	 *
	 * @param r1 Record 1.
	 * @param r2 Record 2.
	 * @return -1 if r1 is less than r2, 0 if equal, and 1 if greater than.
	 */
	@Override
	public int compare(Record r1, Record r2) {
		OrderKey k1 = r1.getOrderKey(order);
		OrderKey k2 = r2.getOrderKey(order);
		return k1.compareTo(k2);
	}

	/**
	 * Returns the corresponding order.
	 * 
	 * @param record The base record.
	 * @return An order that corresponds to the key pointers.
	 */
	public Order getOrder(Record record) {
		return order;
	}
}
