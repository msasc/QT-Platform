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
import com.qtplaf.library.util.list.ListUtils;

/**
 * A pattern, with an inputs and an outputs vector.
 * 
 * @author Miquel Sas
 */
public class Pattern {

	/**
	 * Return the list of inputs for a list of patterns.
	 * 
	 * @param patterns The source list of patterns.
	 * @return The list of inputs.
	 */
	public static List<double[]> getInputs(List<Pattern> patterns) {
		List<double[]> inputs = new ArrayList<>();
		for (Pattern pattern : patterns) {
			inputs.add(pattern.getInputs());
		}
		return inputs;
	}

	/** Patter inputs. */
	private double[] inputs;
	/** Pattern outputs. */
	private double[] outputs;
	/** Pattern label. */
	private String label;

	/** Optional properties. */
	private Properties properties;

	/**
	 * Default constructor.
	 */
	public Pattern() {
		super();
	}

	/**
	 * Return the pattern inputs.
	 * 
	 * @return The pattern inputs.
	 */
	public double[] getInputs() {
		return inputs;
	}

	/**
	 * Set the pattern inputs.
	 * 
	 * @param inputs The pattern inputs.
	 */
	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}

	/**
	 * Return the pattern outputs.
	 * 
	 * @return The pattern outputs.
	 */
	public double[] getOutputs() {
		return outputs;
	}

	/**
	 * Set the pattern outputs.
	 * 
	 * @param outputs The pattern outputs.
	 */
	public void setOutputs(double[] outputs) {
		this.outputs = outputs;
	}

	/**
	 * Return the label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label The label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Check if the pattern has a label.
	 * 
	 * @return A boolean.
	 */
	public boolean isLabel() {
		return getLabel() != null;
	}

	/**
	 * Check whether the pattern has outputs.
	 * 
	 * @return A boolean.
	 */
	public boolean isOutputs() {
		return getOutputs() != null;
	}

	/**
	 * Return a string representation.
	 * 
	 * @return A string.
	 */
	@Override
	public String toString() {
		if (isLabel()) {
			return getLabel();
		}
		if (isOutputs()) {
			return ListUtils.asList(getOutputs()).toString();
		}
		return super.toString();
	}

	/**
	 * Return the additional properties.
	 * 
	 * @return The additional properties.
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

}
