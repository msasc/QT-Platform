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
package com.qtplaf.library.trading.server.feed;

import java.util.List;

/**
 * Listener of feed data subscribed.
 * 
 * @author Miquel Sas
 */
public interface FeedListener {

	/**
	 * Returns the list of current instrument data this listener is subscribed on.
	 * 
	 * @return The list of current this listener is subscribed on.
	 */
	List<DataSubscription> getCurrentDataSubscriptions();

	/**
	 * Returns the list of instrument data this listener is subscribed on.
	 * 
	 * @return The list of this listener is subscribed on.
	 */
	List<DataSubscription> getDataSubscriptions();

	/**
	 * Returns the list instrument ticks this listener is subscribed on.
	 * 
	 * @return The list instrument ticks this listener is subscribed on.
	 */
	List<TickSubscription> getTickSubscriptions();

	/**
	 * Notifies the current forming data for the subscribed instruments.
	 * 
	 * @param dataEvent The data event.
	 */
	void onCurrentData(DataEvent dataEvent);

	/**
	 * Notifies the completed data for the subscribed instruments.
	 * 
	 * @param dataEvent The data event.
	 */
	void onData(DataEvent dataEvent);

	/**
	 * Notifies every tick data event for the subscribed instruments.
	 * 
	 * @param tickEvent The tick event.
	 */
	void onTick(TickEvent tickEvent);
}
