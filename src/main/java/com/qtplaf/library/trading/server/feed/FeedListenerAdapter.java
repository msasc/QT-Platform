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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miquel Sas
 *
 */
public class FeedListenerAdapter implements FeedListener {

	/**
	 * List of current subscriptions.
	 */
	List<DataSubscription> currentDataSubscriptions = new ArrayList<>();
	/**
	 * List of completed subscriptions.
	 */
	List<DataSubscription> dataSubscriptions = new ArrayList<>();
	/**
	 * List of tick subscriptions.
	 */
	List<TickSubscription> tickSubscriptions = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public FeedListenerAdapter() {
		super();
	}

	/**
	 * Adds a subscription to current data.
	 * 
	 * @param subscription The subscription.
	 */
	public void addCurrentDataSubscription(DataSubscription subscription) {
		currentDataSubscriptions.add(subscription);
	}

	/**
	 * Adds a subscription to current data.
	 * 
	 * @param subscription The subscription.
	 */
	public void addDataSubscription(DataSubscription subscription) {
		dataSubscriptions.add(subscription);
	}

	/**
	 * Add a tick subscription.
	 * 
	 * @param subscription The subscription.
	 */
	public void addTickSubscription(TickSubscription subscription) {
		tickSubscriptions.add(subscription);
	}

	/**
	 * Returns the list of current instrument data this listener is subscribed on.
	 * 
	 * @return The list of current data this listener is subscribed on.
	 */
	@Override
	public List<DataSubscription> getCurrentDataSubscriptions() {
		return currentDataSubscriptions;
	}

	/**
	 * Returns the list of instrument data this listener is subscribed on.
	 * 
	 * @return The list of datas this listener is subscribed on.
	 */
	@Override
	public List<DataSubscription> getDataSubscriptions() {
		return dataSubscriptions;
	}

	/**
	 * Returns the list instrument ticks this listener is subscribed on.
	 * 
	 * @return The list instrument ticks this listener is subscribed on.
	 */
	@Override
	public List<TickSubscription> getTickSubscriptions() {
		return tickSubscriptions;
	}

	/**
	 * Notifies the current forming data for the subscribed instruments.
	 * 
	 * @param dataEvent The data event.
	 */
	@Override
	public void onCurrentData(DataEvent dataEvent) {
	}

	/**
	 * Notifies the completed data for the subscribed instruments.
	 * 
	 * @param dataEvent The data event.
	 */
	@Override
	public void onData(DataEvent dataEvent) {
	}

	/**
	 * Notifies every tick data event for the subscribed instruments.
	 * 
	 * @param tickEvent The tick event.
	 */
	@Override
	public void onTick(TickEvent tickEvent) {
	}

}
