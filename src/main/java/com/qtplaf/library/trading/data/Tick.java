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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.Timestamp;

/**
 * Tick data.
 * 
 * @author Miquel Sas
 */
public class Tick {

	/**
	 * A small structure to pack a value with its volume.
	 */
	public class Pair {
		/**
		 * The value.
		 */
		private double value;
		/**
		 * The volume.
		 */
		private double volume;

		/**
		 * Constructor assigning fields.
		 * 
		 * @param value The value.
		 * @param volume The volume.
		 */
		public Pair(double value, double volume) {
			super();
			this.value = value;
			this.volume = volume;
		}

		/**
		 * Returns the value.
		 * 
		 * @return The value.
		 */
		public double getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 * 
		 * @param value The value.
		 */
		protected void setValue(double value) {
			this.value = value;
		}

		/**
		 * Sets the value.
		 * 
		 * @return The value.
		 */
		public double getVolume() {
			return volume;
		}

		/**
		 * @param volume the volume to set
		 */
		protected void setVolume(double volume) {
			this.volume = volume;
		}

		/**
		 * Returns a string representation of this value/volume pair.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("Val: ");
			b.append(getValue());
			b.append(", ");
			b.append("Vol: ");
			b.append(getVolume());
			return b.toString();
		}
	}

	/**
	 * The list of ASK pairs.
	 */
	private List<Pair> asks = new ArrayList<>();
	/**
	 * The list of BID pairs.
	 */
	private List<Pair> bids = new ArrayList<>();
	/**
	 * The start time in millis.
	 */
	private long time;

	/**
	 * Default constructor.
	 */
	public Tick() {
		super();
	}

	/**
	 * Adds a value-volume pair to the ask list.
	 * 
	 * @param value The value.
	 * @param volume The volume.
	 */
	public void addAsk(double value, double volume) {
		asks.add(new Pair(value, volume));
	}

	/**
	 * Adds a value-volume pair to the bid list.
	 * 
	 * @param value The value.
	 * @param volume The volume.
	 */
	public void addBid(double value, double volume) {
		bids.add(new Pair(value, volume));
	}

	/**
	 * Returns the size of the ASK list.
	 * 
	 * @return The size of he ASK list.
	 */
	public int askSize() {
		return asks.size();
	}

	/**
	 * Returns the size of the BID list.
	 * 
	 * @return The size of he BID list.
	 */
	public int bidSize() {
		return bids.size();
	}

	/**
	 * Returns the pair value-volume of the ASK list at the given index.
	 * 
	 * @param index The index.
	 * @return The ASK pair.
	 */
	public Pair getAsk(int index) {
		return asks.get(index);
	}

	/**
	 * Returns the pair value-volume of the BID list at the given index.
	 * 
	 * @param index The index.
	 * @return The BID pair.
	 */
	public Pair getBid(int index) {
		return bids.get(index);
	}

	/**
	 * Returns the best pair value-volume of the ASK list at the given index.
	 * 
	 * @return The ASK pair.
	 */
	public Pair getAsk() {
		return asks.get(0);
	}

	/**
	 * Returns the best pair value-volume of the BID list at the given index.
	 * 
	 * @return The BID pair.
	 */
	public Pair getBid() {
		return bids.get(0);
	}

	/**
	 * Returns the total ask volume.
	 * 
	 * @return The total ask volume.
	 */
	public double getTotalAskVolume() {
		double volume = 0;
		for (Pair ask : asks) {
			volume += ask.volume;
		}
		return volume;
	}

	/**
	 * Returns the total bid volume.
	 * 
	 * @return The total bid volume.
	 */
	public double getTotalBidVolume() {
		double volume = 0;
		for (Pair bid : bids) {
			volume += bid.volume;
		}
		return volume;
	}

	/**
	 * Returns the start time in millis.
	 * 
	 * @return The start time in millis.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the start time in millis.
	 * 
	 * @param time The start time in millis.
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Returns a string representation of this tick.
	 * 
	 * @return A string representation of this tick.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(new Timestamp(getTime()));
		b.append(", ");
		b.append("B: ");
		b.append(getBid());
		b.append(", ");
		b.append("A: ");
		b.append(getAsk());
		return b.toString();
	}
}
