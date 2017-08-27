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

package com.qtplaf.platform.database;

import com.qtplaf.platform.statistics.Average;

/**
 * Field names.
 *
 * @author Miquel Sas
 */
public class Fields {

	/**
	 * Fields additional properties names.
	 */
	public interface Properties {
		String AVERAGE = "average";
		String CALCULATION = "calculation";
		String SPREAD = "spread";
		String SLOPE = "slope";
	}

	/**
	 * Suffixes of field names.
	 */
	public interface Suffix {
		String RAW = "raw"; // Raw
		String NRM = "nrm"; // Normalize continuous
		String DSC = "dsc"; // Normalize discrete
		String IN = "in"; // Input
		String OUT = "out"; // Output
		String SPREAD = "spread"; // Spread
		String SLOPE = "slope"; // Slope
	}

	/**
	 * Families of calculations.
	 */
	public interface Family {
		String DEFAULT = "default";
		String STATE = "state";
	}

	public static final String AVERAGE = "average";
	public static final String AVGSTD1 = "avgstd1";
	public static final String AVGSTD2 = "avgstd2";
	public static final String CENTER_DIFF = "center_diff";
	public static final String CLOSE = "close";
	public static final String CLUSTER = "cluster";
	public static final String COUNT = "count";
	public static final String DATA_FILTER = "data_filter";
	public static final String HIGH = "high";
	public static final String INDEX = "index";
	public static final String INDEX_GROUP = "index_group";
	public static final String INDEX_IN = "index_in";
	public static final String INDEX_OUT = "index_out";
	public static final String INSTRUMENT_ID = "instr_id";
	public static final String INSTRUMENT_DESC = "instr_desc";
	public static final String INSTRUMENT_PIP_VALUE = "instr_pipv";
	public static final String INSTRUMENT_PIP_SCALE = "instr_pips";
	public static final String INSTRUMENT_PRIMARY_CURRENCY = "instr_currp";
	public static final String INSTRUMENT_SECONDARY_CURRENCY = "instr_currs";
	public static final String INSTRUMENT_TICK_VALUE = "instr_tickv";
	public static final String INSTRUMENT_TICK_SCALE = "instr_ticks";
	public static final String INSTRUMENT_VOLUME_SCALE = "instr_vols";
	public static final String LABEL = "label";
	public static final String LOW = "low";
	public static final String MAXIMUM = "maximum";
	public static final String MINIMUM = "minimum";
	public static final String MIN_MAX = "min_max";
	public static final String NAME = "name";
	public static final String OFFER_SIDE = "offer_side";
	public static final String OPEN = "open";
	public static final String PATTERN_FAMILY = "pattern_fam";
	public static final String PATTER_ID = "pattern_id";
	public static final String PERIOD = "period";
	public static final String PERIOD_ID = "period_id";
	public static final String PERIOD_NAME = "period_name";
	public static final String PERIOD_SIZE = "period_size";
	public static final String PERIOD_UNIT_INDEX = "period_unit_index";
	public static final String RANGE = "range";
	public static final String SERVER_ID = "server_id";
	public static final String SERVER_NAME = "server_name";
	public static final String SERVER_TITLE = "server_title";
	public static final String STATE = "state";
	public static final String STATE_IN = "state_in";
	public static final String STATE_OUT = "state_out";
	public static final String STATISTICS_ID = "stats_id";
	public static final String STDDEV = "stddev";
	public static final String TABLE_NAME = "table_name";
	public static final String TIME = "time";
	public static final String TIME_FMT = "time_fmt";
	public static final String VALUE = "value";
	public static final String VOLUME = "volume";
	public static final String WCP = "wcp";

	/**
	 * Returns the field name for an average.
	 * 
	 * @param average The average.
	 * @return The name.
	 */
	public static String averageName(Average average) {
		return "average_" + average.getPeriod();
	}

	/**
	 * Returns the field header for an average.
	 * 
	 * @param average The average.
	 * @return The header.
	 */
	public static String averageHeader(Average average) {
		return "Avg-" + average.getPeriod();
	}

	/**
	 * Returns the field label for an average.
	 * 
	 * @param average The average.
	 * @return The label.
	 */
	public static String averageLabel(Average average) {
		StringBuilder b = new StringBuilder();
		b.append("Average (");
		b.append(average.getPeriod());
		for (int smooth : average.getSmooths()) {
			b.append(", ");
			b.append(smooth);
		}
		b.append(")");
		return b.toString();
	}

	public static String spread(String fieldName, Average average, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append("spread");
		b.append("_");
		b.append(fieldName);
		b.append("_");
		b.append(average.getPeriod());
		b.append("_");
		b.append(suffix);
		return b.toString();
	}

	/**
	 * Returns the field name for a slope.
	 * 
	 * @param average The average.
	 * @param suffix The suffix.
	 * @return The field name.
	 */
	public static String slopeName(Average average, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append("slope");
		b.append("_");
		b.append(average.getPeriod());
		b.append("_");
		b.append(suffix);
		return b.toString();
	}

	/**
	 * Returns the field header for a slope.
	 * 
	 * @param average The average.
	 * @param suffix The suffix.
	 * @return The field header.
	 */
	public static String slopeHeader(Average average, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append("Slope");
		b.append("-");
		b.append(average.getPeriod());
		b.append("-");
		b.append(suffix);
		return b.toString();
	}

	/**
	 * Returns the field label for a slope.
	 * 
	 * @param average The average.
	 * @param suffix The suffix.
	 * @return The field label.
	 */
	public static String slopeLabel(Average average, String suffix) {
		return slopeHeader(average, suffix);
	}

	/**
	 * Returns the field name for a spread of averages.
	 * 
	 * @param fast The fast average.
	 * @param slow The slow average.
	 * @param suffix The suffix.
	 * @return The field name.
	 */
	public static String spreadName(Average fast, Average slow, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append("spread");
		b.append("_");
		b.append(fast.getPeriod());
		b.append("_");
		b.append(slow.getPeriod());
		b.append("_");
		b.append(suffix);
		return b.toString();
	}

	/**
	 * Returns the field header for a spread of averages.
	 * 
	 * @param fast The fast average.
	 * @param slow The slow average.
	 * @param suffix The suffix.
	 * @return The field header.
	 */
	public static String spreadHeader(Average fast, Average slow, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append("Spread-");
		b.append("-");
		b.append(fast.getPeriod());
		b.append("-");
		b.append(slow.getPeriod());
		b.append("-");
		b.append(suffix);
		return b.toString();
	}

	/**
	 * Returns the field label for a spread of averages.
	 * 
	 * @param fast The fast average.
	 * @param slow The slow average.
	 * @param suffix The suffix.
	 * @return The field label.
	 */
	public static String spreadLabel(Average fast, Average slow, String suffix) {
		return spreadHeader(fast, slow, suffix);
	}

	public static String suffix(String name, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append(name);
		b.append("_");
		b.append(suffix);
		return b.toString();
	}
}
