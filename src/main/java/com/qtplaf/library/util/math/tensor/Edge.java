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

package com.qtplaf.library.util.math.tensor;

/**
 * An edge of the graph.
 *
 * @author Miquel Sas
 */
public class Edge {

	/** Internal data, normally an array. */
	private Object data;

	/**
	 * 
	 */
	public Edge() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return the internal data used by operations.
	 * 
	 * @return The data.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Set the data used by operations.
	 * 
	 * @param data The data used by operations.
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
