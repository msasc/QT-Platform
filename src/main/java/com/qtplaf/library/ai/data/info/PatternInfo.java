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

package com.qtplaf.library.ai.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.Properties;

/**
 * Pattern info.
 *
 * @author Miquel Sas
 */
public class PatternInfo {

	/** Id the info. */
	private String id;
	/** Description. */
	private String description;
	/** List of inputs. */
	private List<InputInfo> inputs = new ArrayList<>();
	/** List of outputs. */
	private List<OutputInfo> outputs = new ArrayList<>();
	/** User properties. */
	private Properties properties;

	/**
	 * Default constructor.
	 */
	public PatternInfo() {
		super();
	}

	/**
	 * Returns the patter info id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the pattern id.
	 * 
	 * @param id The id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Add an input.
	 * 
	 * @param id The id.
	 * @param description The description.
	 */
	public void addInput(String id, String description) {
		inputs.add(new InputInfo(id, description));
	}

	/**
	 * Add an input.
	 * 
	 * @param input The input.
	 */
	public void addInput(InputInfo input) {
		inputs.add(input);
	}

	/**
	 * Add an output.
	 * 
	 * @param label The label.
	 * @param description The description.
	 */
	public void addOutput(String label, String description) {
		outputs.add(new OutputInfo(label, description));
	}

	/**
	 * Add an output.
	 * 
	 * @param output The output info.
	 */
	public void addOuput(OutputInfo output) {
		outputs.add(output);
	}

	/**
	 * Returns the input info at the given index.
	 * 
	 * @param index The index.
	 * @return The input info.
	 */
	public InputInfo getInput(int index) {
		return inputs.get(index);
	}

	/**
	 * Return the number of inputs.
	 * 
	 * @return The number of inputs.
	 */
	public int getInputCount() {
		return inputs.size();
	}

	/**
	 * Returns the output info at the given index.
	 * 
	 * @param index The index.
	 * @return The output info.
	 */
	public OutputInfo getOutput(int index) {
		return outputs.get(index);
	}

	/**
	 * Return the number of outputs.
	 * 
	 * @return The number of outputs.
	 */
	public int getOutputCount() {
		return outputs.size();
	}

	/**
	 * Returns the user properties.
	 * 
	 * @return The user properties.
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}
}
