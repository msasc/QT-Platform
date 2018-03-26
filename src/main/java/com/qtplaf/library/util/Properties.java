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

package com.qtplaf.library.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A useful and quite generic properties table with typed accessors for most used objects. Using a map to store the
 * properties of an object has several advantages, like for instance a natural copy mechanism.
 * 
 * @author Miquel Sas
 */
public class Properties {

	/**
	 * The properties map.
	 */
	private Map<Object, Object> properties = new HashMap<>();

	/**
	 * Constructor.
	 */
	public Properties() {
		super();
	}

	/**
	 * Clear this properties.
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * Fill this properties with the argument properties.
	 * 
	 * @param properties The properties used to fill this properties.
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties.properties);
	}

	/**
	 * Returns a stored boolean value, returning <code>false</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored boolean value.
	 */
	public boolean getBoolean(Object key) {
		return getBoolean(key, false);
	}

	/**
	 * Returns a stored boolean value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored boolean value.
	 */
	public boolean getBoolean(Object key, boolean defaultValue) {
		Boolean value = (Boolean) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a boolean value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setBoolean(Object key, boolean value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored string value.
	 */
	public String getString(Object key) {
		return getString(key, null);
	}

	/**
	 * Returns a stored string value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored string value.
	 */
	public String getString(Object key, String defaultValue) {
		String value = (String) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a string value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setString(Object key, String value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored integer value, returning <code>0</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored integer value.
	 */
	public int getInteger(Object key) {
		return getInteger(key, 0);
	}

	/**
	 * Returns a stored integer value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored integer value.
	 */
	public int getInteger(Object key, int defaultValue) {
		Integer value = (Integer) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an integer value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setInteger(Object key, int value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored double value, returning <code>0</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored double value.
	 */
	public double getDouble(Object key) {
		return getDouble(key, 0);
	}

	/**
	 * Returns a stored double value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored double value.
	 */
	public double getDouble(Object key, double defaultValue) {
		Double value = (Double) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a double value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setDouble(Object key, double value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored object, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored object.
	 */
	public Object getObject(Object key) {
		return getObject(key, null);
	}

	/**
	 * Returns a stored object, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored object.
	 */
	public Object getObject(Object key, Object defaultValue) {
		Object value = properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a object.
	 * 
	 * @param key The key.
	 * @param value The object.
	 */
	public void setObject(Object key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Remove the property at key.
	 * 
	 * @param key The key.
	 */
	public void remove(Object key) {
		properties.remove(key);
	}
}
