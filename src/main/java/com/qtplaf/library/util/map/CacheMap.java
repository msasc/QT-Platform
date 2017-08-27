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

package com.qtplaf.library.util.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.qtplaf.library.util.NumberUtils;

/**
 * A useful map to cache objects with a cache size.
 *
 * @author Miquel Sas
 */
@SuppressWarnings("javadoc")
public class CacheMap<K, V> implements Map<K, V> {

	/** Internal map. */
	private Map<K, V> map = new TreeMap<>();
	/** Cache size. Less equal than zero, no cache. */
	private int cacheSize = 10000;
	/** Cache factor: 0.5, removes half of the cache. */
	private double cacheFactor = 0.5;

	/**
	 * Constructor.
	 */
	public CacheMap() {
		super();
	}

	/**
	 * Constructor assigning the cache size.
	 * 
	 * @param cacheSize The cache size.
	 */
	public CacheMap(int cacheSize) {
		super();
		this.cacheSize = cacheSize;
	}

	/**
	 * Returns the cache size.
	 * 
	 * @return The cache size.
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * Set the cache size.
	 * 
	 * @param cacheSize The cache size.
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * Returns the cache factor.
	 * 
	 * @return The cache factor.
	 */
	public double getCacheFactor() {
		return cacheFactor;
	}

	/**
	 * Set the cache factor.
	 * 
	 * @param cacheFactor The cache factor.
	 */
	public void setCacheFactor(double cacheFactor) {
		if (cacheFactor <= 0 || cacheFactor > 1) {
			throw new IllegalArgumentException();
		}
		this.cacheFactor = cacheFactor;
	}

	/**
	 * Returns the size of this map.
	 *
	 * @return The size of this map.
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * Check empty.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Check if the map contains the key.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * Check if the map contains the value.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * Returns the value with the given key or null.
	 * 
	 * @param key The key.
	 */
	@Override
	public V get(Object key) {
		return map.get(key);
	}

	/**
	 * Put the key pair value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	@Override
	public V put(K key, V value) {
		if (map.size() >= cacheSize) {
			List<K> keysToRemove = getRemoveKeys();
			for (K keyToRemove : keysToRemove) {
				map.remove(keyToRemove);
			}
		}
		return map.put(key, value);
	}

	/**
	 * Returns the list of keys to remove.
	 * 
	 * @return The list of keys to remove.
	 */
	private List<K> getRemoveKeys() {
		int count =
			Double.valueOf(NumberUtils.round(Double.valueOf(cacheSize).doubleValue() * cacheFactor, 0)).intValue();
		List<K> keys = new ArrayList<>();
		Iterator<K> iterator = map.keySet().iterator();
		while (keys.size() < count && iterator.hasNext()) {
			keys.add(iterator.next());
		}
		return keys;
	}

	/**
	 * Remove the given key.
	 * 
	 * @param key The key to remove.
	 */
	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	/**
	 * Not supported.
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Clear the map.
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * Return the key set.
	 * 
	 * @return The key set.
	 */
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	/**
	 * Returns the values collection.
	 * 
	 * @return The values collection.
	 */
	@Override
	public Collection<V> values() {
		return map.values();
	}

	/**
	 * Returns the entry set.
	 * 
	 * @return The entry set.
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
