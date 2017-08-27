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
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.trading.data.DataType;

/**
 * Input source info for indicators and studies. Input sources can be of types <i>Price</i>, <i>Volume</i> or
 * <i>Indicator</i>.
 * 
 * @author Miquel Sas
 */
public class InputInfo {

	/**
	 * Small structure to define the accepted input sources.
	 */
	public class InputType {
		/**
		 * The data type, either <i>Price</i>, <i>Volume</i> or <i>Indicator</i>.
		 */
		private DataType dataType;
		/**
		 * The number of values to retrieve from the data type, if less equal than zero, the indicator decides the
		 * values to retrieve, if not the system will let the user select the value from the possible values of the
		 * type.
		 */
		private int values = 1;

		/**
		 * Constructor assigning type and number of values.
		 * 
		 * @param dataType The data type.
		 * @param values The number of values.
		 */
		InputType(DataType dataType, int values) {
			super();
			this.dataType = dataType;
			this.values = values;
		}

		/**
		 * Returns the data type.
		 * 
		 * @return The data type.
		 */
		public DataType getDataType() {
			return dataType;
		}

		/**
		 * Returns the number of values to provideif less equal than zero, the indicator decides the values to retrieve,
		 * if not the system will let the user select the value from the possible values of the type.
		 * 
		 * @return The number of values to provide.
		 */
		public int getValues() {
			return values;
		}

		/**
		 * Returns a boolean indicating if the argument object is equal to this input source.
		 * 
		 * @return A boolean indicating if the argument object is equal to this input source.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof InputType) {
				InputType input = (InputType) o;
				return getDataType().equals(input.getDataType()) && getValues() == input.getValues();
			}
			return false;
		}

		/**
		 * Returns a string representation of this input type.
		 * 
		 * @return A string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("[");
			b.append(getDataType());
			b.append(", ");
			b.append(values);
			b.append("]");
			return b.toString();
		}
	}

	/**
	 * The list of input source types with the number of values required from each type.
	 */
	private List<InputType> inputSources = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public InputInfo() {
		super();
	}

	/**
	 * Adds a possible input source for a given data type and number of values.
	 * 
	 * @param dataType The data type.
	 * @param values The number of values.
	 */
	public void addPossibleInputSource(DataType dataType, int values) {
		InputType inputType = new InputType(dataType, values);
		if (inputSources.contains(inputType)) {
			inputSources.add(inputType);
		}
	}

	/**
	 * Returns the number of input sources.
	 * 
	 * @return The number of possible input sources.
	 */
	public int getInputSourcesCount() {
		return inputSources.size();
	}

	/**
	 * Returns the input source at the given index.
	 * 
	 * @param index The index.
	 * @return The input source.
	 */
	public InputType getInputSource(int index) {
		return inputSources.get(index);
	}

	/**
	 * Check whether this input info is equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InputInfo) {
			InputInfo in = (InputInfo) obj;
			int count = getInputSourcesCount();
			if (count != in.getInputSourcesCount()) {
				return false;
			}
			for (int i = 0; i < count; i++) {
				if (!getInputSource(i).equals(in.getInputSource(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns a string representation of this input info.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < getInputSourcesCount(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(getInputSource(i));
		}
		return b.toString();
	}
}
