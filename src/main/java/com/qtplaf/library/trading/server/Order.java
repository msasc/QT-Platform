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

import java.util.List;

import com.qtplaf.library.trading.data.Instrument;

/**
 * Contains order data and allows order management.
 * 
 * @author Miquel Sas
 */
public interface Order {
	/**
	 * Sends a request to fully close position by market price or cancel entry order. If order has both pending and
	 * filled parts, both will be closed/canceled.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	void close() throws ServerException;

	/**
	 * Sends a request to close the position with specified amount, by market price and default slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @throws ServerException If a server error occurs.
	 */
	void close(double amount) throws ServerException;

	/**
	 * Sends a request to close the position with specified amount and price, and default slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @param price The desired price.
	 * @throws ServerException If a server error occurs.
	 */
	void close(double amount, double price) throws ServerException;

	/**
	 * Sends a request to close the position with specified, price and slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @param price The desired price.
	 * @param slippage The desired slippage.
	 * @throws ServerException If a server error occurs.
	 */
	void close(double amount, double price, double slippage) throws ServerException;

	/**
	 * Returns amount of the order. The filled amount can be different from the requested amount.
	 * 
	 * @return The amount of the order.
	 */
	double getAmount();

	/**
	 * Returns the ordered history of order closing.
	 * 
	 * @return The ordered history of order closing.
	 */
	List<OrderHistory> getCloseHistory();

	/**
	 * Returns the close price or 0 if the order was not closed or partially closed.
	 * 
	 * @return The close price or 0.
	 */
	double getClosePrice();

	/**
	 * Returns the time when the broker closed the order.
	 * 
	 * @return The time when the broker closed the order.
	 */
	long getCloseTime();

	/**
	 * Returns comment that was set when the order was submitted
	 * 
	 * @return comment The comment.
	 */
	String getComment();

	/**
	 * Returns the position comission in the account currency.
	 * 
	 * @return The position comission in the account currency.
	 */
	double getCommission();

	/**
	 * Returns the position comission in USD.
	 * 
	 * @return The position comission in USD.
	 */
	double getCommissionInUSD();

	/**
	 * Returns the time when the order was accepted by the broker.
	 * 
	 * @return The time when the order was accepted by the broker.
	 */
	long getCreationTime();

	/**
	 * Returns the history of order filling.
	 * 
	 * @return The history of order filling.
	 */
	List<OrderHistory> getFillHistory();

	/**
	 * Returns time when the order was filled.
	 * 
	 * @return time of the order fill
	 */
	long getFillTime();

	/**
	 * Returns time when order will be cancelled or 0 if order is "good till cancel"
	 * 
	 * @return cancel time or 0
	 */
	long getExpirationTime();

	/**
	 * Returns the order identifier.
	 * 
	 * @return The order identifier.
	 */
	String getId();

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	Instrument getInstrument();

	/**
	 * Returns the label set at creation time.
	 * 
	 * @return The label set at creation time.
	 */
	String getLabel();

	/**
	 * Returns entry level the price for conditional orders, or the price for filled or closed orders.
	 * 
	 * @return Te price.
	 */
	double getOpenPrice();

	/**
	 * Returns the order command, or simply <i>Buy</i> or <i>Sell</i> when the order has been filled.
	 * 
	 * @return The order command.
	 */
	OrderCommand getOrderCommand();

	/**
	 * Returns the current order state.
	 * 
	 * @return The current order state.
	 */
	OrderState getOrderState();

	/**
	 * Returns original constant amount of the order. The value is set on order submit and cannot be changed later.
	 * 
	 * @return The original amount of the order.
	 */
	double getOriginalAmount();

	/**
	 * Returns the profit/loss in the account currency.
	 *
	 * @return The profit/loss in the account currency.
	 */
	double getProfitLoss();

	/**
	 * Returns the profit/loss in pips.
	 *
	 * @return The profit/loss in pips.
	 */
	double getProfitLossInPips();

	/**
	 * Returns the profit/loss in USD.
	 *
	 * @return The profit/loss in USD.
	 */
	double getProfitLossInUSD();

	/**
	 * Returns the requested amount.
	 * 
	 * @return The amount requested.
	 */
	double getRequestedAmount();

	/**
	 * Returns the price of stop loss condition or 0 if stop loss condition is not set. Orders submitted with stop loss
	 * condition, will have this price set only after server accepts order.
	 * 
	 * @return The stop loss price or 0
	 */
	double getStopLossPrice();

	/**
	 * Returns the side used to check the stop loss condition.
	 * 
	 * @return The side used to check the stop loss condition.
	 */
	OfferSide getStopLossSide();

	/**
	 * Returns the price of take profit condition or 0 if the take profit condition is not set.
	 * 
	 * @return The price of take profit condition or 0 if the take profit condition is not set.
	 */
	double getTakeProfitPrice();

	/**
	 * Returns the current trailing step or 0 if no trailing step is set.
	 * 
	 * @return The current trailing step.
	 */
	double getTrailingStep();

	/**
	 * Returns a boolean indicating if the order is closed.
	 * 
	 * @return A boolean indicating if the order is closed.
	 */
	boolean isClosed();

	/**
	 * Returns a boolean that indicates whether this is a long or short order.
	 * 
	 * @return A boolean that indicates whether this is a long or short order.
	 */
	boolean isLong();

	/**
	 * Returns a boolean that indicates whether this is a long or short order.
	 * 
	 * @return A boolean that indicates whether this is a long or short order.
	 */
	boolean isShort();
}
