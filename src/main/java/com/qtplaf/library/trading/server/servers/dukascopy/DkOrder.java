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

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.ICloseOrder;
import com.dukascopy.api.IFillOrder;
import com.dukascopy.api.IOrder;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Order;
import com.qtplaf.library.trading.server.OrderCommand;
import com.qtplaf.library.trading.server.OrderHistory;
import com.qtplaf.library.trading.server.OrderState;
import com.qtplaf.library.trading.server.ServerException;

/**
 * Dukascopy implementation of the <i>Order</i> interface.
 * 
 * @author Miquel Sas
 */
public class DkOrder implements Order {

	/**
	 * Dukascopy server.
	 */
	private DkServer server;
	/**
	 * Dukascopy <i>Iorder</i>.
	 */
	private IOrder order;

	/**
	 * Constructor assigning the Dukascopy order.
	 * 
	 * @param server The server.
	 * @param order The Dukascopy order.
	 */
	public DkOrder(DkServer server, IOrder order) {
		super();
		this.server = server;
		this.order = order;
	}

	/**
	 * Sends a request to fully close position by market price or cancel entry order. If order has both pending and
	 * filled parts, both will be closed/canceled.
	 * 
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void close() throws ServerException {
		try {
			order.close();
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Sends a request to close the position with specified amount, by market price and default slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void close(double amount) throws ServerException {
		try {
			order.close(amount);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Sends a request to close the position with specified amount and price, and default slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @param price The desired price.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void close(double amount, double price) throws ServerException {
		try {
			order.close(amount, price);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Sends a request to close the position with specified, price and slippage.
	 * 
	 * @param amount The requested amount to close.
	 * @param price The desired price.
	 * @param slippage The desired slippage.
	 * @throws ServerException If a server error occurs.
	 */
	@Override
	public void close(double amount, double price, double slippage) throws ServerException {
		try {
			order.close(amount, price, slippage);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Returns amount of the order. The filled amount can be different from the requested amount.
	 * 
	 * @return The amount of the order.
	 */
	@Override
	public double getAmount() {
		return order.getAmount();
	}

	/**
	 * Returns the ordered history of order closing.
	 * 
	 * @return The ordered history of order closing.
	 */
	@Override
	public List<OrderHistory> getCloseHistory() {
		List<ICloseOrder> closeHistory = order.getCloseHistory();
		List<OrderHistory> orderHistory = new ArrayList<>();
		for (ICloseOrder closeOrder : closeHistory) {
			OrderHistory oh = new DkOrderHistory(closeOrder);
			orderHistory.add(oh);
		}
		return orderHistory;
	}

	/**
	 * Returns the close price or 0 if the order was not closed or partially closed.
	 * 
	 * @return The close price or 0.
	 */
	@Override
	public double getClosePrice() {
		return order.getClosePrice();
	}

	/**
	 * Returns the time when the broker closed the order.
	 * 
	 * @return The time when the broker closed the order.
	 */
	@Override
	public long getCloseTime() {
		return order.getCloseTime();
	}

	/**
	 * Returns comment that was set when the order was submitted
	 * 
	 * @return comment The comment.
	 */
	@Override
	public String getComment() {
		return order.getComment();
	}

	/**
	 * Returns the position comission in the account currency.
	 * 
	 * @return The position comission in the account currency.
	 */
	@Override
	public double getCommission() {
		return order.getCommission();
	}

	/**
	 * Returns the position comission in USD.
	 * 
	 * @return The position comission in USD.
	 */
	@Override
	public double getCommissionInUSD() {
		return order.getCommissionInUSD();
	}

	/**
	 * Returns the time when the order was accepted by the broker.
	 * 
	 * @return The time when the order was accepted by the broker.
	 */
	@Override
	public long getCreationTime() {
		return order.getCreationTime();
	}

	/**
	 * Returns the history of order filling.
	 * 
	 * @return The history of order filling.
	 */
	@Override
	public List<OrderHistory> getFillHistory() {
		List<IFillOrder> fillHistory = order.getFillHistory();
		List<OrderHistory> orderHistory = new ArrayList<>();
		for (IFillOrder fillOrder : fillHistory) {
			OrderHistory oh = new DkOrderHistory(fillOrder);
			orderHistory.add(oh);
		}
		return orderHistory;
	}

	/**
	 * Returns time when the order was filled.
	 * 
	 * @return time of the order fill
	 */
	@Override
	public long getFillTime() {
		return order.getFillTime();
	}

	/**
	 * Returns time when order will be cancelled or 0 if order is "good till cancel"
	 * 
	 * @return cancel time or 0
	 */
	@Override
	public long getExpirationTime() {
		return order.getGoodTillTime();
	}

	/**
	 * Returns the order identifier.
	 * 
	 * @return The order identifier.
	 */
	@Override
	public String getId() {
		return order.getId();
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	@Override
	public Instrument getInstrument() {
		return server.getDkConverter().fromDkInstrument(order.getInstrument());
	}

	/**
	 * Returns the label set at creation time.
	 * 
	 * @return The label set at creation time.
	 */
	@Override
	public String getLabel() {
		return order.getLabel();
	}

	/**
	 * Returns entry level the price for conditional orders, or the price for filled or closed orders.
	 * 
	 * @return Te price.
	 */
	@Override
	public double getOpenPrice() {
		return order.getOpenPrice();
	}

	/**
	 * Returns the order command, or simply <i>Buy</i> or <i>Sell</i> when the order has been filled.
	 * 
	 * @return The order command.
	 */
	@Override
	public OrderCommand getOrderCommand() {
		return server.getDkConverter().fromDkOrderCommand(order.getOrderCommand(), isClosed());
	}

	/**
	 * Returns the current order state.
	 * 
	 * @return The current order state.
	 */
	@Override
	public OrderState getOrderState() {
		return server.getDkConverter().fromDkOrderState(order.getState());
	}

	/**
	 * Returns original constant amount of the order. The value is set on order submit and cannot be changed later.
	 * 
	 * @return The original amount of the order.
	 */
	@Override
	public double getOriginalAmount() {
		return order.getOriginalAmount();
	}

	/**
	 * Returns the profit/loss in the account currency.
	 *
	 * @return The profit/loss in the account currency.
	 */
	@Override
	public double getProfitLoss() {
		return order.getProfitLossInAccountCurrency();
	}

	/**
	 * Returns the profit/loss in pips.
	 *
	 * @return The profit/loss in pips.
	 */
	@Override
	public double getProfitLossInPips() {
		return order.getProfitLossInPips();
	}

	/**
	 * Returns the profit/loss in USD.
	 *
	 * @return The profit/loss in USD.
	 */
	@Override
	public double getProfitLossInUSD() {
		return order.getProfitLossInUSD();
	}

	/**
	 * Returns the requested amount.
	 * 
	 * @return The amount requested.
	 */
	@Override
	public double getRequestedAmount() {
		return order.getRequestedAmount();
	}

	/**
	 * Returns the price of stop loss condition or 0 if stop loss condition is not set. Orders submitted with stop loss
	 * condition, will have this price set only after server accepts order.
	 * 
	 * @return The stop loss price or 0
	 */
	@Override
	public double getStopLossPrice() {
		return order.getStopLossPrice();
	}

	/**
	 * Returns the side used to check the stop loss condition.
	 * 
	 * @return The side used to check the stop loss condition.
	 */
	@Override
	public OfferSide getStopLossSide() {
		return server.getDkConverter().fromDkOfferSide(order.getStopLossSide());
	}

	/**
	 * Returns the price of take profit condition or 0 if the take profit condition is not set.
	 * 
	 * @return The price of take profit condition or 0 if the take profit condition is not set.
	 */
	@Override
	public double getTakeProfitPrice() {
		return order.getTakeProfitPrice();
	}

	/**
	 * Returns the current trailing step or 0 if no trailing step is set.
	 * 
	 * @return The current trailing step.
	 */
	@Override
	public double getTrailingStep() {
		return order.getTrailingStep();
	}

	/**
	 * Returns a boolean indicating if the order is closed.
	 * 
	 * @return A boolean indicating if the order is closed.
	 */
	@Override
	public boolean isClosed() {
		return getOrderState().equals(OrderState.Closed);
	}

	/**
	 * Returns a boolean that indicates whether this is a long or short order.
	 * 
	 * @return A boolean that indicates whether this is a long or short order.
	 */
	@Override
	public boolean isLong() {
		return order.isLong();
	}

	/**
	 * Returns a boolean that indicates whether this is a long or short order.
	 * 
	 * @return A boolean that indicates whether this is a long or short order.
	 */
	@Override
	public boolean isShort() {
		return !isLong();
	}
}
