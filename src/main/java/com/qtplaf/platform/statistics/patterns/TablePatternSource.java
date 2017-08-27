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

package com.qtplaf.platform.statistics.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.util.map.CacheMap;

/**
 * Source of patterns from a table (created with a pattern info). The primary key of the persistor must be the index.
 *
 * @author Miquel Sas
 */
public class TablePatternSource extends PatternSource {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The pattern info. */
	private PatternInfo patternInfo;
	/** Underlying persistor. */
	private Persistor persistor;
	/** A map to cache retrieved records by relative index. */
	private CacheMap<Integer, Record> map = new CacheMap<>();
	/** Mapping of pattern source indexes to persistor indexes. */
	private List<Integer> indexes = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param patternInfo The pattern info.
	 * @param persistor The persistor.
	 */
	public TablePatternSource(PatternInfo patternInfo, Persistor persistor) {
		super();
		this.patternInfo = patternInfo;
		this.persistor = persistor;
	}

	/**
	 * Returns the underlying persistor.
	 * 
	 * @return The underlying persistor.
	 */
	public Persistor getPersistor() {
		return persistor;
	}

	/**
	 * Return the pattern info.
	 * 
	 * @return The pattern info.
	 */
	public PatternInfo getPatternInfo() {
		return patternInfo;
	}

	/**
	 * Set the cache size.
	 * 
	 * @param cacheSize The cache size.
	 */
	public void setCacheSize(int cacheSize) {
		map.setCacheSize(cacheSize);
	}

	/**
	 * Returns the pattern at the given index.
	 * 
	 * @param index The index.
	 * @return The pattern.
	 */
	@Override
	public Pattern get(int index) {
		int persistorIndex = indexes.get(index);
		Record record = map.get(persistorIndex);
		if (record == null) {
			record = getRecord(persistorIndex);
			map.put(persistorIndex, record);
		}
		return Patterns.getPattern(patternInfo, record);
	}

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	@Override
	public int size() {
		return indexes.size();
	}

	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @param size The number of pattern batches.
	 */
	@Override
	public List<PatternSource> getBatches() {
		return getBatches(Runtime.getRuntime().availableProcessors());
	}

	/**
	 * Returns a list of size pattern batches used to process patterns concurrently.
	 * 
	 * @param size The number of pattern batches.
	 * @return The list of sub-pattern sources.
	 */
	@Override
	public List<PatternSource> getBatches(int size) {
		return null;
	}

	/**
	 * Returns the record at the given index from the underlying persistor.
	 * 
	 * @param index The record index.
	 * @return The record.
	 */
	private Record getRecord(int index) {
		Record record = null;
		try {
			record = persistor.getRecord(new Value(index));
		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
		return record;
	}

	/**
	 * Returns the number of records in the persistor.
	 * 
	 * @return The total number of records.
	 */
	private int getRecordCount() {
		long count = -1;
		try {
			count = persistor.count(new Criteria());
		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
		return Long.valueOf(count).intValue();
	}

	/**
	 * Fill the list of indexes to retrieve randomly within the persistor.
	 * 
	 * @param size The size to fill.
	 */
	public void fillRandomly(int size) {
		int count = getRecordCount();
		if (size > count) {
			size = count;
		}
		Random random = new Random();
		Map<Integer, Integer> mapIndexes = new HashMap<>();
		indexes.clear();
		while (indexes.size() < size) {
			int index = random.nextInt(count);
			if (!mapIndexes.containsKey(index)) {
				mapIndexes.put(index, index);
				indexes.add(index);
			}
		}
	}
}
