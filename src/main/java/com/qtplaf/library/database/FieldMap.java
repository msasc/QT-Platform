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
package com.qtplaf.library.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A FieldMap maps fields by named alias to their indexes in a list. Is an unique container for the lists of fields of
 * tables and views.
 *
 * @author Miquel Sas
 */
public class FieldMap {

	/**
	 * The container of all field maps.
	 */
	private static final Map<String, Map<String, Integer>> maps = new HashMap<>();

	/**
	 * Remove a field map from the static map container.
	 *
	 * @param map The map to remove.
	 */
	public static void removeMap(FieldMap map) {
		if (map != null) {
			synchronized (maps) {
				maps.remove(map.getKey());
			}
		}
	}

	/**
	 * Creates a key to have a unique map.
	 * 
	 * @param fields The list of fields..
	 * @return The key
	 */
	public static String createKey(List<? extends Field> fields) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			Field field = fields.get(i);
			b.append(field.getAlias().toUpperCase());
		}
		return b.toString();
	}

	/**
	 * This field map key.
	 */
	private String key = null;
	/**
	 * Field map.
	 */
	private Map<String, Integer> map = null;

	/**
	 * Create a new field map.
	 *
	 * @param fields The list of fields to map.
	 */
	public FieldMap(List<? extends Field> fields) {
		super();
		synchronized (maps) {
			key = createKey(fields);
			if (!maps.containsKey(key)) {
				HashMap<String, Integer> tmpMap = new HashMap<>();
				for (int i = 0; i < fields.size(); i++) {
					String alias = fields.get(i).getAlias().toUpperCase();
					if (!tmpMap.containsKey(alias)) {
						tmpMap.put(alias, i);
					}
				}
				maps.put(key, tmpMap);
			}
			map = maps.get(key);
		}
	}

	/**
	 * Returns this map key.
	 *
	 * @return The key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Get a field index by named alias.
	 * <p>
	 * Domains are mapped using the getAlias() method of the field.
	 * <p>
	 * 
	 * @return The field index or -1 if not found.
	 * @param alias The field alias.
	 */
	public int getFieldIndex(String alias) {
		if (alias == null) {
			return -1;
		}
		Integer index = map.get(alias.toUpperCase());
		if (index == null) {
			return -1;
		}
		return index;
	}
}
