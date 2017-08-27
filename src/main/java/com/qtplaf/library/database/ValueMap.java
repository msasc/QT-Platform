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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A map of values, that are indexed or keyed either by:
 * <ul>
 * <li>A string when referred by alias, storing pairs of strings and values.</li>
 * <li>An integer when referred by index, storing pairs of integers and values.</li>
 * <li>A field when referred by field, storing pairs of fields and values.</li>
 * </ul>
 * A value map can only be of one of the three enumerated types.
 * 
 * @author Miquel Sas
 */
public class ValueMap {

	/**
	 * Returns the value map.
	 * 
	 * @param indexes List of indexes.
	 * @param values List of values.
	 * @return The value map.
	 */
	public static ValueMap getIndexesMap(List<Integer> indexes, List<Value> values) {
		if (indexes.size() != values.size()) {
			throw new IllegalArgumentException("Same sizes required for keys and values");
		}
		ValueMap map = new ValueMap();
		for (int i = 0; i < indexes.size(); i++) {
			map.put(indexes.get(i), values.get(i));
		}
		return map;
	}

	/**
	 * Returns the value map.
	 * 
	 * @param aliases List of indexes.
	 * @param values List of values.
	 * @return The value map.
	 */
	public static ValueMap getAliasesMap(List<String> aliases, List<Value> values) {
		if (aliases.size() != values.size()) {
			throw new IllegalArgumentException("Same sizes required for keys and values");
		}
		ValueMap map = new ValueMap();
		for (int i = 0; i < aliases.size(); i++) {
			map.put(aliases.get(i), values.get(i));
		}
		return map;
	}

	/**
	 * Returns the value map.
	 * 
	 * @param fields List of indexes.
	 * @param values List of values.
	 * @return The value map.
	 */
	public static ValueMap getFieldsMap(List<Field> fields, List<Value> values) {
		if (fields.size() != values.size()) {
			throw new IllegalArgumentException("Same sizes required for keys and values");
		}
		ValueMap map = new ValueMap();
		for (int i = 0; i < fields.size(); i++) {
			map.put(fields.get(i), values.get(i));
		}
		return map;
	}

	/**
	 * Map types.
	 */
	private enum Type {
		Index,
		Alias,
		Field
	}

	/**
	 * Alias pair structure.
	 */
	public class AliasPair {
		public String alias;
		public Value value;
	}

	/**
	 * Index pair structure.
	 */
	public class IndexPair {
		public int index;
		public Value value;
	}

	/**
	 * Field pair structure.
	 */
	public class FieldPair {
		public Field field;
		public Value value;
	}

	/**
	 * Internal map.
	 */
	private HashMap<Object, Value> map = new HashMap<>();
	/**
	 * Map type.
	 */
	private Type type;

	/**
	 * Default constructor.
	 */
	public ValueMap() {
		super();
	}

	/**
	 * Check if the map is empty.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Check if the map is type index.
	 * 
	 * @return A boolean.
	 */
	public boolean isTypeIndex() {
		return type == Type.Index;
	}

	/**
	 * Check if the map is type alias.
	 * 
	 * @return A boolean.
	 */
	public boolean isTypeAlias() {
		return type == Type.Alias;
	}

	/**
	 * Check if the map is type field.
	 * 
	 * @return A boolean.
	 */
	public boolean isTypeField() {
		return type == Type.Field;
	}

	/**
	 * Returns the value stored with the given alias.
	 * 
	 * @param alias The alias.
	 * @return The value stored with the key or null.
	 */
	public Value get(String alias) {
		return map.get(alias);
	}

	/**
	 * Returns the value stored with the given index.
	 * 
	 * @param index The index.
	 * @return The value stored with the key or null.
	 */
	public Value get(int index) {
		return map.get(index);
	}

	/**
	 * Returns the value stored with the given field.
	 * 
	 * @param field The field.
	 * @return The value stored with the key or null.
	 */
	public Value get(Field field) {
		return map.get(field);
	}

	/**
	 * Store a value.
	 * 
	 * @param alias The alias key.
	 * @param value The value.
	 */
	public void put(String alias, Value value) {
		if (type == null) {
			type = Type.Alias;
		}
		if (type != Type.Alias) {
			throw new IllegalStateException("Invalid map type");
		}
		map.put(alias, value);
	}

	/**
	 * Store a value.
	 * 
	 * @param index The index key.
	 * @param value The value.
	 */
	public void put(int index, Value value) {
		if (type == null) {
			type = Type.Index;
		}
		if (type != Type.Index) {
			throw new IllegalStateException("Invalid map type");
		}
		map.put(index, value);
	}

	/**
	 * Store a value.
	 * 
	 * @param field The field key.
	 * @param value The value.
	 */
	public void put(Field field, Value value) {
		if (type == null) {
			type = Type.Field;
		}
		if (type != Type.Field) {
			throw new IllegalStateException("Invalid map type");
		}
		map.put(field, value);
	}

	/**
	 * Returns the list of alias pairs.
	 * 
	 * @return The list of alias pairs.
	 */
	public List<AliasPair> getAliasPairs() {
		List<AliasPair> pairs = new ArrayList<>();
		Set<Object> keys = map.keySet();
		for (Object key : keys) {
			if (key instanceof String) {
				AliasPair pair = new AliasPair();
				pair.alias = (String) key;
				pair.value = map.get(key);
				pairs.add(pair);
			}
		}
		return pairs;
	}

	/**
	 * Returns the list of index pairs.
	 * 
	 * @return The list of index pairs.
	 */
	public List<IndexPair> getIndexPairs() {
		List<IndexPair> pairs = new ArrayList<>();
		Set<Object> keys = map.keySet();
		for (Object key : keys) {
			if (key instanceof Integer) {
				IndexPair pair = new IndexPair();
				pair.index = (Integer) key;
				pair.value = map.get(key);
				pairs.add(pair);
			}
		}
		return pairs;
	}

	/**
	 * Returns the list of field pairs.
	 * 
	 * @return The list of field pairs.
	 */
	public List<FieldPair> getFieldPairs() {
		List<FieldPair> pairs = new ArrayList<>();
		Set<Object> keys = map.keySet();
		for (Object key : keys) {
			if (key instanceof Field) {
				FieldPair pair = new FieldPair();
				pair.field = (Field) key;
				pair.value = map.get(key);
				pairs.add(pair);
			}
		}
		return pairs;
	}
}
