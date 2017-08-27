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
package com.qtplaf.library.trading.server.servers.dukascopy;

import com.dukascopy.api.ICloseOrder;
import com.dukascopy.api.IFillOrder;
import com.qtplaf.library.trading.server.OrderHistory;

/**
 * Dukascopy implementation of the <i>OrderHistory</i> interface.
 * 
 * @author Miquel Sas
 */
public class DkOrderHistory implements OrderHistory {

	/**
	 * IClose order reference.
	 */
	private ICloseOrder closeOrder;
	/**
	 * IFillOrder reference.
	 */
	private IFillOrder fillOrder;

	/**
	 * Constructor assigning the ICloseOrder.
	 * 
	 * @param closeOrder The ICloseOrder.
	 */
	public DkOrderHistory(ICloseOrder closeOrder) {
		super();
		this.closeOrder = closeOrder;
	}

	/**
	 * Constructor assigning the IFillOrder.
	 * 
	 * @param fillOrder The IFillOrder.
	 */
	public DkOrderHistory(IFillOrder fillOrder) {
		super();
		this.fillOrder = fillOrder;
	}

	/**
	 * Returns the amount of the closed part.
	 * 
	 * @return The amount of the closed part.
	 */
	@Override
	public double getAmount() {
		return (closeOrder != null ? closeOrder.getAmount() : fillOrder.getAmount());
	}

	/**
	 * Returns the close price.
	 * 
	 * @return The close price.
	 */
	@Override
	public double getPrice() {
		return (closeOrder != null ? closeOrder.getPrice() : fillOrder.getPrice());
	}

	/**
	 * Returns the time when the order was closed.
	 * 
	 * @return The time when the order was closed.
	 */
	@Override
	public long getTime() {
		return (closeOrder != null ? closeOrder.getTime() : fillOrder.getTime());
	}
}
