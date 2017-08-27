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

import com.qtplaf.library.util.Properties;

/**
 * Pattern input info.
 *
 * @author Miquel Sas
 */
public class InputInfo {

	/** Id. */
	private String id;
	/** Description. */
	private String description;
	/** Normalized high. */
	private double normalizedHigh;
	/** Normalized low. */
	private double normalizedLow;
	/** User properties. */
	private Properties properties;

	/**
	 * Constructor with (1,-1) normalization.
	 * 
	 * @param id Id.
	 * @param description Description.
	 */
	public InputInfo(String id, String description) {
		super();
		this.id = id;
		this.description = description;
		this.normalizedHigh = 1.0;
		this.normalizedLow = -1.0;
	}

	/**
	 * Return the id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
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
	 * Return the normalized high.
	 * 
	 * @return The normalized high.
	 */
	public double getNormalizedHigh() {
		return normalizedHigh;
	}

	/**
	 * Return the normalized low.
	 * 
	 * @return The normalized low.
	 */
	public double getNormalizedLow() {
		return normalizedLow;
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
