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

package com.qtplaf.library.ai.neural;

/**
 * Enumerate neuron types.
 * 
 * @author Miquel Sas
 */
public enum NeuronType {
	/** Input neuron. */
	Input,
	/** Hidden neuron. */
	Hidden,
	/** Context neuron. */
	Context,
	/** Output neuron. */
	Output;

	/**
	 * Return the id, the first letter.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return name().substring(0, 1);
	}
}