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

package com.qtplaf.platform.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.data.info.InputInfo;
import com.qtplaf.library.ai.data.info.OutputInfo;
import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Domains;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldDataInstr;
import com.qtplaf.platform.database.fields.FieldDataValue;
import com.qtplaf.platform.database.fields.FieldIndex;
import com.qtplaf.platform.database.fields.FieldPeriod;
import com.qtplaf.platform.database.fields.FieldTime;
import com.qtplaf.platform.database.fields.FieldTimeFmt;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.TickValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.statistics.action.ActionBrowsePatterns;
import com.qtplaf.platform.statistics.action.ActionBrowseRanges;
import com.qtplaf.platform.statistics.action.ActionBrowseStates;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionChart;
import com.qtplaf.platform.statistics.action.ActionNavigateClusteredPatterns;
import com.qtplaf.platform.statistics.action.ActionNavigateStates;
import com.qtplaf.platform.statistics.patterns.Patterns;
import com.qtplaf.platform.statistics.patterns.TablePatternSource;
import com.qtplaf.platform.statistics.task.TaskClusterKMeans;
import com.qtplaf.platform.statistics.task.TaskNormalize;
import com.qtplaf.platform.statistics.task.TaskPatterns;
import com.qtplaf.platform.statistics.task.TaskRanges;
import com.qtplaf.platform.statistics.task.TaskStates;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * States on averages.
 *
 * @author Miquel Sas
 */
public class States extends Averages {

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/** Action group: calculate. */
	private static final ActionGroup ACTION_GROUP_CALCULATE = new ActionGroup("Calculate", 1000);
	/** Action group: clustering. */
	private static final ActionGroup ACTION_GROUP_CLUSTERING = new ActionGroup("Clustering", 1001);
	/** Action group: browse. */
	private static final ActionGroup ACTION_GROUP_BROWSE = new ActionGroup("Browse", 1002);
	/** Action group: navigate. */
	private static final ActionGroup ACTION_GROUP_NAVIGATE = new ActionGroup("Navigate", 1003);
	/** Action group: default chart. */
	private static final ActionGroup ACTION_GROUP_CHART = new ActionGroup("Chart", 1010);

	/**
	 * Field calculator to view the normal distribution index.
	 */
	class NormalIndex implements Calculator {

		/** Stddev times. */
		private double stddevs;

		NormalIndex(double stddevs) {
			this.stddevs = stddevs;
		}

		@Override
		public Value getValue(Record record) {
			@SuppressWarnings("unchecked")
			List<Double> values = (List<Double>) record.getProperties().getObject("values");
			Value normalIndex = (Value) record.getProperties().getObject(stddevs);
			if (normalIndex == null) {
				double average = record.getValue(Fields.AVERAGE).getDouble();
				double stddev = record.getValue(Fields.STDDEV).getDouble();
				double min = average - (stddev * stddevs);
				double max = average + (stddev * stddevs);
				int count = 0;
				for (Double value : values) {
					if (value >= min && value <= max) {
						count++;
					}
				}
				double index = 0;
				if (!values.isEmpty()) {
					index = 100.0 * Double.valueOf(count) / Double.valueOf(values.size());
				}
				normalIndex = new Value(index);
				record.getProperties().setObject(stddevs, normalIndex);
			}
			return normalIndex;
		}

	}

	/** Table states. */
	private Table tableStates;
	/** Table ranges to calculate min-max values. */
	private Table tableRanges;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public States(Session session) {
		super(session);
	}

	/**
	 * Set the configuration.
	 * 
	 * @param id Configuration id.
	 * @param averages List of averages.
	 */
	public void setConfiguration(String id, Average... averages) {
		setId(id);
		for (Average average : averages) {
			addAverage(average);
		}
	}

	/**
	 * Returns the list of tables where statistic results are stored.
	 * 
	 * @return The list of result tables.
	 */
	@Override
	public List<Table> getTables() {
		List<Table> tables = new ArrayList<>();
		tables.add(getTableStates());
		tables.add(getTableRanges());
		tables.add(getTablePattern(Patterns.getInfoCandle()));
		return tables;
	}

	/**
	 * Returns the list of tasks to calculate the results. Tasks are expected to be executed sequentially.
	 * 
	 * @return The list of tasks.
	 */
	@Override
	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(new TaskStates(this));
		tasks.add(new TaskRanges(this));
		tasks.add(new TaskNormalize(this));
		tasks.add(new TaskPatterns(this, Patterns.getInfoCandle()));
		return tasks;
	}

	/**
	 * Return the action to calculate states.
	 * 
	 * @return The action.
	 */
	private Action getActionCalculateStates() {
		ActionCalculate action = new ActionCalculate(this, new TaskStates(this));
		ActionUtils.setName(action, "Calculate states");
		ActionUtils.setShortDescription(action, "Calculate states from scratch");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CALCULATE);
		return action;
	}

	/**
	 * Return the action to calculate min-max ranges.
	 * 
	 * @return The action.
	 */
	private Action getActionCalculateRanges() {
		ActionCalculate action = new ActionCalculate(this, new TaskRanges(this));
		ActionUtils.setName(action, "Calculate min-max ranges");
		ActionUtils.setShortDescription(action, "Calculate min-max ranges");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CALCULATE);
		return action;
	}

	/**
	 * Return the action to normalize values.
	 * 
	 * @return The action.
	 */
	private Action getActionCalculateNormalValues() {
		ActionCalculate action = new ActionCalculate(this, new TaskNormalize(this));
		ActionUtils.setName(action, "Normalize raw values");
		ActionUtils.setShortDescription(action, "Normalize raw values");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CALCULATE);
		return action;
	}

	/**
	 * Return the action to generate single candle patterns.
	 * 
	 * @return The action.
	 */
	private Action getActionCalculatePatternsCandleSingle() {
		PatternInfo patternInfo = Patterns.getInfoCandle();
		ActionCalculate action = new ActionCalculate(this, new TaskPatterns(this, patternInfo));
		ActionUtils.setName(action, "Patterns: single candle");
		ActionUtils.setShortDescription(action, "Generate pattern records for single candle patterns.");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CALCULATE);
		return action;
	}

	/**
	 * Returns the clustering action, KMeans candle single.
	 * 
	 * @return The action.
	 */
	private Action getActionClusteringKMeansCandleSingle() {
		PatternInfo patternInfo = Patterns.getInfoCandle();
		Persistor patternPersistor = getTablePattern(patternInfo).getPersistor();
		TablePatternSource patternSource = new TablePatternSource(patternInfo, patternPersistor);
		ActionCalculate action = new ActionCalculate(this, new TaskClusterKMeans(this, patternSource));
		ActionUtils.setName(action, "KMeans: single candle");
		ActionUtils.setShortDescription(action, "KMeans for single candle patterns.");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CLUSTERING);
		return action;
	}

	/**
	 * Returns the action to browse states.
	 * 
	 * @return The action.
	 */
	private Action getActionBrowseStates() {
		ActionBrowseStates action = new ActionBrowseStates(this, getTableStates().getName());
		ActionUtils.setName(action, "Browse states");
		ActionUtils.setShortDescription(action, "Browse states data");
		ActionUtils.setActionGroup(action, ACTION_GROUP_BROWSE);
		return action;
	}

	/**
	 * Returns the action to browse min-max ranges.
	 * 
	 * @return The action.
	 */
	private Action getActionBrowseRanges() {
		ActionBrowseRanges action = new ActionBrowseRanges(this, getTableRanges().getName());
		ActionUtils.setName(action, "Browse min-max ranges");
		ActionUtils.setShortDescription(action, "Browse min-max ranges");
		ActionUtils.setActionGroup(action, ACTION_GROUP_BROWSE);
		return action;
	}

	/**
	 * Returns the action to browse patterns, candle single.
	 * 
	 * @return The action.
	 */
	private Action getActionBrowsePatternsCandleSingle() {
		PatternInfo patternInfo = Patterns.getInfoCandle();
		Table tableCandle = getTablePattern(patternInfo);
		ActionBrowsePatterns action = new ActionBrowsePatterns(this, tableCandle.getName());
		action.setPatternInfo(patternInfo);
		ActionUtils.setName(action, "Browse single candle patterns");
		ActionUtils.setShortDescription(action, "Browse single candle patterns");
		ActionUtils.setActionGroup(action, ACTION_GROUP_BROWSE);
		return action;
	}

	/**
	 * Returns the action to navigate the states chart.
	 * 
	 * @return The action.
	 */
	private Action getActionNavigateStates() {
		ActionNavigateStates action = new ActionNavigateStates(this, getTableStates().getName());
		ActionUtils.setName(action, "Navigate states");
		ActionUtils.setShortDescription(action, "Navigates states table");
		ActionUtils.setActionGroup(action, ACTION_GROUP_NAVIGATE);
		return action;
	}

	private Action getActionNavigatePatternsClusterCandleSingle() {
		PatternInfo patternInfo = Patterns.getInfoCandle();
		Table table = getTablePattern(patternInfo);
		ActionNavigateClusteredPatterns action = new ActionNavigateClusteredPatterns(this, table.getName());
		action.setPatternInfo(patternInfo);
		ActionUtils.setName(action, "Navigate clustering, candle single");
		ActionUtils.setShortDescription(action, "Navigate clustering, candle single");
		ActionUtils.setActionGroup(action, ACTION_GROUP_NAVIGATE);
		return action;
	}

	/**
	 * Returns the action to view the states chart.
	 * 
	 * @return The action.
	 */
	private Action getActionChartStates() {
		ActionChart action = new ActionChart(this, getPlotDataListDefault(), "default chart");
		ActionUtils.setName(action, "Default chart");
		ActionUtils.setShortDescription(action, "View default chart");
		ActionUtils.setActionGroup(action, ACTION_GROUP_CHART);
		return action;
	}

	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	@Override
	public List<Action> getActions() {
		List<Action> actions = new ArrayList<>();

		// Calculate actions.
		actions.add(getActionCalculateStates());
		actions.add(getActionCalculateRanges());
		actions.add(getActionCalculateNormalValues());
		actions.add(getActionCalculatePatternsCandleSingle());

		// Clustering actions.
		actions.add(getActionClusteringKMeansCandleSingle());

		// Browse actions.
		actions.add(getActionBrowseStates());
		actions.add(getActionBrowseRanges());
		actions.add(getActionBrowsePatternsCandleSingle());

		// Navigate actions.
		actions.add(getActionNavigateStates());
		actions.add(getActionNavigatePatternsClusterCandleSingle());

		// Chart actions.
		actions.add(getActionChartStates());

		return actions;
	}

	/**
	 * Returns the states table.
	 * 
	 * @return The states table.
	 */
	public Table getTableStates() {
		if (tableStates == null) {

			tableStates = new Table();

			Server server = getServer();
			Instrument instrument = getInstrument();
			Period period = getPeriod();

			// Statistics, configuration and table ids.
			String id = getId().toLowerCase() + "_st_src";
			tableStates.setName(Tables.ticker(instrument, period, id));
			tableStates.setSchema(Schemas.server(server));

			// Index and time.
			tableStates.addField(new FieldIndex(getSession(), Fields.INDEX));
			tableStates.addField(new FieldTime(getSession(), Fields.TIME));

			// Time formatted.
			tableStates.addField(new FieldTimeFmt(getSession(), Fields.TIME_FMT));

			// Open, high, low, close.
			tableStates.addField(new FieldDataInstr(getSession(), instrument, Fields.OPEN, "Open", "Open value"));
			tableStates.addField(new FieldDataInstr(getSession(), instrument, Fields.HIGH, "Open", "High value"));
			tableStates.addField(new FieldDataInstr(getSession(), instrument, Fields.LOW, "Low", "Low value"));
			tableStates.addField(new FieldDataInstr(getSession(), instrument, Fields.CLOSE, "Close", "Close value"));

			// Averages fields.
			tableStates.addFields(getFieldListAverages());

			// Raw:
			// - Range
			// - Slopes of averages.
			// - Spread WCP-fast-avg.
			// - Spreads among averages.
			// - Difference between current bar center and previous bar center.
			String range_raw = Fields.suffix(Fields.RANGE, Fields.Suffix.RAW);
			tableStates.addField(new FieldDataValue(getSession(), range_raw, "Range raw"));
			tableStates.addFields(getFieldListSlopes(Fields.Suffix.RAW));
			String wcp_raw = Fields.spread(Fields.WCP, getAverages().get(0), Fields.Suffix.RAW);
			tableStates.addField(new FieldDataValue(getSession(), wcp_raw, "Spread WCP-fast raw"));
			tableStates.addFields(getFieldListSpreads(Fields.Suffix.RAW));
			String center_diff_raw = Fields.suffix(Fields.CENTER_DIFF, Fields.Suffix.RAW);
			tableStates.addField(new FieldDataValue(getSession(), center_diff_raw, "Center diff curr/prev raw"));

			// Nrm:
			// - Range
			// - Slopes of averages.
			// - Spread WCP-fast-avg.
			// - Spreads among averages.
			// - Difference between current bar center and previous bar center.
			String range_nrm = Fields.suffix(Fields.RANGE, Fields.Suffix.NRM);
			tableStates.addField(new FieldDataValue(getSession(), range_nrm, "Range nrm"));
			tableStates.addFields(getFieldListSlopes(Fields.Suffix.NRM));
			String wcp_nrm = Fields.spread(Fields.WCP, getAverages().get(0), Fields.Suffix.NRM);
			tableStates.addField(new FieldDataValue(getSession(), wcp_nrm, "Spread WCP-fast nrm"));
			tableStates.addFields(getFieldListSpreads(Fields.Suffix.NRM));
			String center_diff_nrm = Fields.suffix(Fields.CENTER_DIFF, Fields.Suffix.NRM);
			tableStates.addField(new FieldDataValue(getSession(), center_diff_nrm, "Center diff curr/prev nrm"));

			// Primary key on Index.
			tableStates.getField(Fields.INDEX).setPrimaryKey(true);

			// Unique index on Time.
			Index indexOnIndex = new Index();
			indexOnIndex.add(tableStates.getField(Fields.TIME));
			indexOnIndex.setUnique(true);
			tableStates.addIndex(indexOnIndex);

			Persistor persistor = PersistorUtils.getPersistor(tableStates.getSimpleView());
			TimeFmtValue timeFmt = new TimeFmtValue(period.getUnit());
			persistor.getField(Fields.TIME_FMT).setFormatter(timeFmt);
			persistor.getField(Fields.TIME_FMT).setCalculator(timeFmt);
			persistor.getField(Fields.OPEN).setFormatter(new TickValue(getSession(), instrument));
			persistor.getField(Fields.HIGH).setFormatter(new TickValue(getSession(), instrument));
			persistor.getField(Fields.LOW).setFormatter(new TickValue(getSession(), instrument));
			persistor.getField(Fields.CLOSE).setFormatter(new TickValue(getSession(), instrument));
			tableStates.setPersistor(persistor);
		}
		return tableStates;
	}

	/**
	 * Returns the ranges table.
	 * 
	 * @return The states table.
	 */
	public Table getTableRanges() {
		if (tableRanges == null) {

			tableRanges = new Table();

			Server server = getServer();
			Instrument instrument = getInstrument();
			Period period = getPeriod();

			// Statistics, configuration and table ids.
			String id = getId().toLowerCase() + "_st_rng";
			tableRanges.setName(Tables.ticker(instrument, period, id));
			tableRanges.setSchema(Schemas.server(server));

			// Name of the field
			Field name = Domains.getString(getSession(), Fields.NAME, 60, "Name", "Name");
			tableRanges.addField(name);
			// Min/Max indicator.
			Field min_max = Domains.getString(getSession(), Fields.MIN_MAX, 3, "Min/Max", "Min/Max");
			tableRanges.addField(min_max);

			tableRanges.addField(new FieldPeriod(getSession(), Fields.PERIOD));
			tableRanges.addField(new FieldDataValue(getSession(), Fields.VALUE, "Value", "Value"));
			tableRanges.addField(new FieldIndex(getSession(), Fields.INDEX));
			tableRanges.addField(new FieldTime(getSession(), Fields.TIME));

			// Non unique index on name, minmax, period.
			Index index = new Index();
			index.add(tableRanges.getField(Fields.NAME));
			index.add(tableRanges.getField(Fields.MIN_MAX));
			index.add(tableRanges.getField(Fields.PERIOD));
			index.setUnique(false);
			tableRanges.addIndex(index);

			tableRanges.setPersistor(PersistorUtils.getPersistor(tableRanges.getSimpleView()));
		}
		return tableRanges;
	}

	/**
	 * Returns the table for the given pattern info.
	 * 
	 * @param patternInfo The pattern info
	 * @return The table.
	 */
	public Table getTablePattern(PatternInfo patternInfo) {

		Table table = new Table();

		// Store the info for further use.
		table.getProperties().setObject("pattern_info", patternInfo);

		Server server = getServer();
		Instrument instrument = getInstrument();
		Period period = getPeriod();

		// Statistics, configuration and table ids.
		String id = getId().toLowerCase() + "_pt_" + patternInfo.getId();
		table.setName(Tables.ticker(instrument, period, id));
		table.setSchema(Schemas.server(server));

		// Index and time.
		table.addField(new FieldIndex(getSession(), Fields.INDEX));
		table.addField(new FieldTime(getSession(), Fields.TIME));

		// Time formatted.
		table.addField(new FieldTimeFmt(getSession(), Fields.TIME_FMT));

		// Inputs: one double per input. Expect correct names for database fields.
		for (int i = 0; i < patternInfo.getInputCount(); i++) {
			InputInfo input = patternInfo.getInput(i);
			String name = input.getId();
			String label = input.getDescription();
			Field field = new FieldDataValue(getSession(), name, label);
			table.addField(field);
		}

		// Outputs: one-of-any -> one double per each possible output label.
		for (int i = 0; i < patternInfo.getOutputCount(); i++) {
			OutputInfo output = patternInfo.getOutput(i);
			String name = output.getId();
			String label = output.getDescription();
			Field field = new FieldDataValue(getSession(), name, label);
			table.addField(field);
		}

		// The output label.
		Field fieldLabel = Domains.getString(getSession(), Fields.LABEL, 60, "Label", "Output label");
		table.addField(fieldLabel);

		// Temporary cluster field.
		Field fieldCluster = Domains.getInteger(getSession(), Fields.CLUSTER, "Cluster", "Cluster");
		table.addField(fieldCluster);

		// Primary key on Index.
		table.getField(Fields.INDEX).setPrimaryKey(true);

		// Unique index on Time.
		Index indexOnIndex = new Index();
		indexOnIndex.add(table.getField(Fields.TIME));
		indexOnIndex.setUnique(true);
		table.addIndex(indexOnIndex);

		Persistor persistor = PersistorUtils.getPersistor(table.getSimpleView());
		TimeFmtValue timeFmt = new TimeFmtValue(period.getUnit());
		persistor.getField(Fields.TIME_FMT).setFormatter(timeFmt);
		persistor.getField(Fields.TIME_FMT).setCalculator(timeFmt);
		table.setPersistor(persistor);

		return table;
	}

	/**
	 * Returns the list of field names to calculate min-max ranges to normalize.
	 * 
	 * @param suffix The field suffix.
	 * @return The list of field names to calculate min-max ranges to normalize.
	 */
	public List<String> getFieldsToNormalize(String suffix) {

		List<String> names = new ArrayList<>();

		// Slopes.
		for (int i = 0; i < getAverages().size(); i++) {
			Average average = getAverages().get(i);
			names.add(Fields.slopeName(average, suffix));
		}

		// Spread wcp
		names.add(Fields.spread(Fields.WCP, getAverages().get(0), suffix));

		// Spreads among averages.
		for (int i = 0; i < getAverages().size(); i++) {
			Average fast = getAverages().get(i);
			for (int j = i + 1; j < getAverages().size(); j++) {
				Average slow = getAverages().get(j);
				names.add(Fields.spreadName(fast, slow, suffix));
			}
		}

		// Center difference.
		names.add(Fields.suffix(Fields.CENTER_DIFF, suffix));

		// Range (no min value)
		names.add(Fields.suffix(Fields.RANGE, suffix));

		return names;
	}

	/**
	 * Returns the persistor data list for this states statistics.
	 * 
	 * @return The persistor data list.
	 */
	public PersistorDataList getDataListStates() {

		DataPersistor persistor = new DataPersistor(getTableStates().getPersistor());

		DataInfo info = new DataInfo(getSession());
		info.setName("States");
		info.setDescription("States data info");
		info.setInstrument(getInstrument());
		info.setPeriod(getPeriod());
		DataPersistor.setDataInfoOutput(info, persistor);

		PersistorDataList dataList = new PersistorDataList(getSession(), info, persistor);
		return dataList;
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @param includePeriod A boolean that indicates whether the period should be included.
	 * @return The recordset to browse the statistic results.
	 */
	public RecordSet getRecordSetRanges(boolean includePeriod) {

		Table table = getTableRanges();

		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Group by fields
		view.addField(table.getField(Fields.NAME));
		view.addField(table.getField(Fields.MIN_MAX));
		if (includePeriod) {
			view.addField(table.getField(Fields.PERIOD));
		}

		// Aggregate function count.
		Field count = Domains.getInteger(getSession(), Fields.COUNT);
		count.setPersistent(false);
		count.setFunction("count(*)");
		view.addField(count);

		// Aggregate function minimum.
		Field minimum = Domains.getDouble(getSession(), Fields.MINIMUM, "Minimum", "Minimum value");
		minimum.setPersistent(false);
		minimum.setFunction("min(value)");
		minimum.setFormatter(new DataValue(getSession(), 10));
		view.addField(minimum);

		// Aggregate function maximum.
		Field maximum = Domains.getDouble(getSession(), Fields.MAXIMUM, "Maximum", "Maximum value");
		maximum.setPersistent(false);
		maximum.setFunction("max(value)");
		maximum.setFormatter(new DataValue(getSession(), 10));
		view.addField(maximum);

		// Aggregate function average.
		Field average = Domains.getDouble(getSession(), Fields.AVERAGE);
		average.setPersistent(false);
		average.setFunction("avg(value)");
		average.setFormatter(new DataValue(getSession(), 10));
		view.addField(average);

		// Aggregate function stddev.
		Field stddev = Domains.getDouble(getSession(), Fields.STDDEV, "Std Dev", "Standard deviation value");
		stddev.setPersistent(false);
		stddev.setFunction("stddev(value)");
		stddev.setFormatter(new DataValue(getSession(), 10));
		view.addField(stddev);

		// Index +- n * stddev
		double stddevs1 = 2.0;
		Field avgStd1 =
			Domains.getDouble(getSession(), "avgstd_1", "AvgStd (" + stddevs1 + ")", "AvgStd (" + stddevs1 + ")");
		avgStd1.setPersistent(false);
		avgStd1.setCalculator(new NormalIndex(stddevs1));
		avgStd1.setFormatter(new DataValue(getSession(), 4));
		view.addField(avgStd1);

		double stddevs2 = 3.0;
		Field avgStd2 =
			Domains.getDouble(getSession(), "avgstd_2", "AvgStd (" + stddevs2 + ")", "AvgStd (" + stddevs2 + ")");
		avgStd2.setPersistent(false);
		avgStd2.setCalculator(new NormalIndex(stddevs2));
		avgStd2.setFormatter(new DataValue(getSession(), 4));
		view.addField(avgStd2);

		// Group by.
		view.addGroupBy(table.getField(Fields.NAME));
		view.addGroupBy(table.getField(Fields.MIN_MAX));
		if (includePeriod) {
			view.addGroupBy(table.getField(Fields.PERIOD));
		}

		// Order by.
		view.addOrderBy(table.getField(Fields.NAME));
		view.addOrderBy(table.getField(Fields.MIN_MAX));
		if (includePeriod) {
			view.addOrderBy(table.getField(Fields.PERIOD));
		}

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));

		RecordSet recordSet = null;
		try {
			recordSet = view.getPersistor().select(null);
			Persistor persistor = PersistorUtils.getPersistor(view.getMasterTable().getSimpleView());
			for (int i = 0; i < recordSet.size(); i++) {
				setValues(persistor, recordSet.get(i), includePeriod);
			}
		} catch (PersistorException exc) {
			LOGGER.catching(exc);
		}

		return recordSet;
	}

	/**
	 * Set the list of values of the key.
	 * 
	 * @param persistor The persistor.
	 * @param record The record.
	 * @param includePeriod A boolean that indicates whether the period should be included.
	 * @throws PersistorException If any persistence error occurs.
	 */
	private void setValues(Persistor persistor, Record record, boolean includePeriod) throws PersistorException {

		Field fName = persistor.getField(Fields.NAME);
		Field fMinMax = persistor.getField(Fields.MIN_MAX);
		Field fPeriod = persistor.getField(Fields.PERIOD);
		Field fValue = persistor.getField(Fields.VALUE);

		Value vName = record.getValue(Fields.NAME);
		Value vMinMax = record.getValue(Fields.MIN_MAX);
		Value vPeriod = record.getValue(Fields.PERIOD);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fName, vName));
		criteria.add(Condition.fieldEQ(fMinMax, vMinMax));
		if (includePeriod) {
			criteria.add(Condition.fieldEQ(fPeriod, vPeriod));
		}

		List<Double> values = new ArrayList<>();
		RecordIterator iter = persistor.iterator(criteria);
		while (iter.hasNext()) {
			Record rc = iter.next();
			double value = rc.getValue(fValue.getName()).getDouble();
			values.add(value);
		}
		iter.close();

		record.getProperties().setObject("values", values);
	}

	/**
	 * Returns the list of plot datas for the default chart.
	 * 
	 * @return The list of plot datas.
	 */
	public List<PlotData> getPlotDataListDefault() {
		List<PlotData> plotDataList = new ArrayList<>();
		plotDataList.add(getPlotDataMain(getDataListStates()));
		plotDataList.add(getPlotData("Slopes", getDataListStates(), getFieldListSlopes(Fields.Suffix.NRM)));
		plotDataList.add(getPlotData("Spreads", getDataListStates(), getFieldListSpreads(Fields.Suffix.NRM)));
		return plotDataList;
	}
}
