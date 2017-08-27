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

import java.util.ArrayList;
import java.util.List;

/**
 * A node of the graph.
 *
 * @author Miquel Sas
 */
public class Node {

	/** List of input edges. */
	private List<Edge> inputs = new ArrayList<>();
	/** List of output edges. */
	private List<Edge> outputs = new ArrayList<>();
	/** Operation. */
	private Operation operation;

	/**
	 * 
	 */
	public Node() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return the list of input edges.
	 * 
	 * @return The list of input edges.
	 */
	public List<Edge> getInputs() {
		return inputs;
	}

	/**
	 * Return the list of output edges.
	 * 
	 * @return The list of output edges.
	 */
	public List<Edge> getOutputs() {
		return outputs;
	}

	/**
	 * Return the operation.
	 * 
	 * @return The operation.
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * Set the operation.
	 * 
	 * @param operation The operation.
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

}
