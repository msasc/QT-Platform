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
package com.qtplaf.library.trading.server;

/**
 * Contains the partial order close/fill event when an order has been partially closed/filled.
 * 
 * @author Miquel Sas
 */
public interface OrderHistory {
	/**
	 * Returns the amount of the closed part.
	 * 
	 * @return The amount of the closed part.
	 */
	double getAmount();

	/**
	 * Returns the close price.
	 * 
	 * @return The close price.
	 */
	double getPrice();

	/**
	 * Returns the time when the order was closed.
	 * 
	 * @return The time when the order was closed.
	 */
	long getTime();
}
