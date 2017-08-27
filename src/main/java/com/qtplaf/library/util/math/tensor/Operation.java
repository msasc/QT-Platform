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

import java.util.List;

/**
 * An operation in a node of a graph. An operation takes zero or more input <tt>Tensor</tt>'s and produces zero or more
 * output <tt>Tensor</tt>'s.
 *
 * @author Miquel Sas
 */
public interface Operation {
	/**
	 * Compute the opration. An operations accepts a list of input data and produces a list of output data. The nature
	 * of the input and output data is an exclusive responsibility of the operation.
	 * 
	 * @param inputs List of inputs, mainly multi dimensional arrays, but can be anything.
	 * @return List of outputs.
	 */
	List<Object> compute(List<Object> inputs);
}
