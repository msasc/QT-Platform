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

package com.qtplaf.library.util.list;

import java.util.ArrayList;
import java.util.List;

/**
 * A dual input/output list used to dispatch outputs from input. Normal usage would be a thread adding inputs and
 * another periodically retrieving the output and processing it. Useful for queued tasks.
 *
 * @author Miquel Sas
 */
@SuppressWarnings("javadoc")
public class IOList<E> {

	/** Input list. */
	private List<E> input = new ArrayList<>();
	/** Output list. */
	private List<E> output = new ArrayList<>();
	/** Input lock. */
	private Object inpuLock = new Object();

	/**
	 * Default constructor.
	 */
	public IOList() {
		super();
	}

	/**
	 * Add an input element.
	 * 
	 * @param e The element to add.
	 */
	public void addInput(E e) {
		synchronized (inpuLock) {
			input.add(e);
		}
	}

	/**
	 * Returns the input buffer size.
	 * 
	 * @return The input buffer size.
	 */
	public int getInputSize() {
		synchronized (inpuLock) {
			return input.size();
		}
	}

	/**
	 * Check if input is empty.
	 * 
	 * @return A boolean.
	 */
	public boolean isInputEmpty() {
		return getInputSize() == 0;
	}

	/**
	 * Check if output is empty.
	 * 
	 * @return A boolean.
	 */
	public boolean isOutputEmpty() {
		return getOutput().isEmpty();
	}

	/**
	 * Transfer input data to output.
	 */
	public void transfer() {
		synchronized (inpuLock) {
			while (!input.isEmpty()) {
				output.add(input.remove(0));
			}
		}
	}

	/**
	 * Returns the output list to be processed. Prior to retrieve the list, <tt>trasnfer</tt> should be called.
	 * 
	 * @return The output list to be processed.
	 */
	public List<E> getOutput() {
		return output;
	}

}
