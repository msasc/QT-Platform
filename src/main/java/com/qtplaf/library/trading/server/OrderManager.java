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
 * Interface responsible to manage orders.
 * 
 * @author Miquel Sas
 */
public interface OrderManager {

	/**
	 * Returns an order identified by the id.
	 * 
	 * @param id The identifier.
	 * @return The order if not an error occurs.
	 * @throws ServerException If a server error occurs.
	 */
	Order getOrderById(String id) throws ServerException;

	/**
	 * Returns an order identified by the label.
	 * 
	 * @param label The label.
	 * @return The order if not an error occurs.
	 * @throws ServerException If a server error occurs.
	 */
	Order getOrderByLabel(String label) throws ServerException;

	/**
	 * Returns a list with all currently active orders.
	 * 
	 * @return The list of orders.
	 * @throws ServerException If a server error occurs.
	 */
	List<Order> getOrders() throws ServerException;

	/**
	 * Returns a list with all currently active orders for the given instrument.
	 * 
	 * @param instrument The instrument to check.
	 * @return The list of orders.
	 * @throws ServerException If a server error occurs.
	 */
	List<Order> getOrders(Instrument instrument) throws ServerException;

	/**
	 * Submits a request for an order at market price.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount) throws ServerException;

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price) throws ServerException;

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage) throws ServerException;

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice) throws ServerException;

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice) throws ServerException;

	/**
	 * Submits a request for an order with full parameters, except the comment.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @param expirationTime A long indicating how long the order should be alive if not executed. For market orders must
	 *        be zero.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice,
		long expirationTime) throws ServerException;

	/**
	 * Submits a request for an order with full parameters.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @param expirationTime A long indicating how long the order should be alive if not executed. For market orders must
	 *        be zero.
	 * @param comment An optional comment.
	 * @return The order if succefully created.
	 * @throws ServerException If a server error occurs.
	 */
	Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice,
		long expirationTime,
		String comment) throws ServerException;

}
