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

package com.qtplaf.library.ai.data;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.Properties;

/**
 * A pattern, with inputs and optional expected label and outputs.
 *
 * @author Miquel Sas
 */
public interface Pattern {

	/**
	 * Return the list of inputs for a list of patterns.
	 * 
	 * @param patterns The source list of patterns.
	 * @return The list of inputs.
	 */
	static List<double[]> getInputs(List<Pattern> patterns) {
		List<double[]> inputs = new ArrayList<>();
		for (Pattern pattern : patterns) {
			inputs.add(pattern.getInputs());
		}
		return inputs;
	}

	/**
	 * Return the pattern inputs.
	 * 
	 * @return The pattern inputs.
	 */
	double[] getInputs();

	/**
	 * Return the optional pattern outputs.
	 * 
	 * @return The pattern outputs.
	 */
	double[] getOutputs();

	/**
	 * Return the errors given the network outputs. If the pattern has expected outputs, the errors will normally be a
	 * subtraction. In reinforcement learning, where actions can yield encourage or discourage gradients, the errors
	 * will not take into account the network outputs and will be conditioned by the overall episode reward.
	 * 
	 * @param networkOutputs The list of network outputs.
	 * @return The errors.
	 */
	double[] getErrors(double[] networkOutputs);

	/**
	 * Return the optional label.
	 * 
	 * @return The label.
	 */
	String getLabel();

	/**
	 * Return the additional properties.
	 * 
	 * @return The additional properties.
	 */
	Properties getProperties();
}
