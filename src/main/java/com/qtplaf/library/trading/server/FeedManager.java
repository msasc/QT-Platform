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

import com.qtplaf.library.trading.server.feed.FeedListener;

/**
 * Interface responsible to manage live feeds.
 * 
 * @author Miquel Sas
 */
public interface FeedManager {

	/**
	 * Add a feed listener to receive notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to add.
	 */
	void addFeedListener(FeedListener listener);

	/**
	 * Removes a feed listener from receiving notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to remove.
	 */
	void removeFeedListener(FeedListener listener);
}
