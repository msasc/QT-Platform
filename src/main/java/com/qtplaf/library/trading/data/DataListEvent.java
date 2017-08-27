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

import java.util.EventObject;

/**
 * Events launched by data lists.
 * 
 * @author Miquel Sas
 */
public class DataListEvent extends EventObject {
	
	/**
	 * Enumerates the operations on the data list that are notified, mainly those that modify the list.
	 */
	public enum Operation {
		/**
		 * Add an element.
		 */
		Add,
		/**
		 * Clear the list.
		 */
		Clear,
		/**
		 * Remove an element.
		 */
		Remove,
		/**
		 * Set an element.
		 */
		Set;
	}

	/**
	 * The data added, removed or set or null if not applicable.
	 */
	private Data data;
	/**
	 * The index of the data added, remove or set or -1 if not applicable.
	 */
	private int index = -1;
	/**
	 * An integer that indicates the operation.
	 */
	private Operation operation;

	/**
	 * Constructor.
	 * 
	 * @param source The source data list.
	 * @param data The data added.
	 * @param index The index of the data set.
	 * @param operation The operation, add, set, remove or clear.
	 */
	public DataListEvent(DataList source, Data data, int index, Operation operation) {
		super(source);
		this.data = data;
		this.index = index;
		this.operation = operation;
	}

	/**
	 * Returns the source datalist.
	 * 
	 * @return The source datalist.
	 */
	public DataList getDataList() {
		return (DataList) getSource();
	}

	/**
	 * Returns the data or null if not applicable. If the operation was add, the data is the data added, if it was
	 * remove, it's the data removed, if it was set, it's the data previous, and if it was clear it'snull.
	 * 
	 * @return the data The data.
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Returns the index of the data added, remove or set or -1 if not applicable.
	 * 
	 * @return the index The index of the data added, remove or set or -1 if not applicable.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the operation.
	 * 
	 * @return The operation.
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * Returns boolean indicating if the operation was add.
	 * 
	 * @return A boolean indicating if the operation was add.
	 */
	public boolean isAdd() {
		return operation.equals(Operation.Add);
	}

	/**
	 * Returns boolean indicating if the operation was set.
	 * 
	 * @return A boolean indicating if the operation was set.
	 */
	public boolean isSet() {
		return operation.equals(Operation.Set);
	}

	/**
	 * Returns a boolean indicating if the operation was remove.
	 * 
	 * @return A boolean indicating if the operation was remove.
	 */
	public boolean isRemoved() {
		return operation.equals(Operation.Remove);
	}

	/**
	 * Returns true if tha data list was cleared.
	 * 
	 * @return A boolean.
	 */
	public boolean isCleared() {
		return operation.equals(Operation.Clear);
	}
}
