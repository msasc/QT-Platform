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

package com.qtplaf.platform.statistics.action;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.chart.drawings.Line;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.States;

/**
 * Navigate recently clustered patterns to visually determine the proper output.
 *
 * @author Miquel Sas
 */
public class ActionNavigateClusteredPatterns extends ActionNavigate {

	/**
	 * Action clear drawings.
	 */
	class ActionClearDrawings extends AbstractAction {
		ActionClearDrawings() {
			ActionUtils.setSession(this, getSession());
			ActionUtils.setName(this, "Clear drawings");
			ActionUtils.setShortDescription(this, "Clear drawings");
			ActionUtils.setActionGroup(this, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JChart chart = ActionUtils.getChart(this);
			PlotData plotData = chart.getPlotDataList().get(0);
			plotData.getDrawings().clear();
			chart.propagateFrameChanges(plotData);
		}
	}

	/**
	 * Action move to pattern.
	 */
	class ActionMoveTo extends AbstractAction {

		ActionMoveTo() {
			ActionUtils.setSession(this, getSession());
			ActionUtils.setName(this, "Move to selected pattern");
			ActionUtils.setShortDescription(this, "Move to selected pattern");
			ActionUtils.setActionGroup(this, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTableRecord table = ActionUtils.getTableRecordPanel(this).getTableRecord();
			Record record = table.getSelectedRecord();
			if (record == null) {
				return;
			}
			int index = record.getValue(Fields.INDEX).getInteger();

			JChart chart = ActionUtils.getChart(this);
			PlotData plotData = chart.getPlotDataList().get(0);
			DataList dataList = plotData.get(0);
			Data data = dataList.get(index);
			int i1 = index;
			int i2 = index;
			double v1 = Data.getHigh(data) + (2 * getInstrument().getPipValue());
			double v2 = v1 + (5 * getInstrument().getPipValue());
			Line line = new Line(i1, v1, i2, v2);
			line.getParameters().setColor(Color.RED);
			line.getParameters().setStroke(new BasicStroke(2));
			plotData.addDrawing(line);

			plotData.move(index);
			chart.propagateFrameChanges(plotData);
		}
	}

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger();

	/** Pattern info. */
	private PatternInfo patternInfo;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics
	 * @param titleSuffix The title suffix.
	 */
	public ActionNavigateClusteredPatterns(States states, String titleSuffix) {
		super(states, titleSuffix);
	}

	/**
	 * Set the pattern info.
	 * 
	 * @param patternInfo The pattern info.
	 */
	public void setPatternInfo(PatternInfo patternInfo) {
		this.patternInfo = patternInfo;
	}

	/**
	 * Return the underlying states statistics.
	 * 
	 * @return The underlying states statistics.
	 */
	protected States getStates() {
		return (States) getStatistics();
	}

	/**
	 * Return the list of plot datas.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		List<PlotData> plotDataList = new ArrayList<>();
		plotDataList.add(getStates().getPlotDataMain(getStates().getDataListStates()));
		return plotDataList;
	}

	/**
	 * Return the recordset to browse.
	 * 
	 * @return The recordset to browse.
	 */
	@Override
	public RecordSet getRecordSet() {

		Table table = getStates().getTablePattern(patternInfo);
		Persistor persistor = table.getPersistor();

		Field fCluster = persistor.getField(Fields.CLUSTER);
		Value vCluster = new Value(0);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldGT(fCluster, vCluster));

		Order order = new Order();
		order.add(persistor.getField(Fields.INDEX));

		RecordSet recordSet = null;
		try {
			recordSet = persistor.select(criteria, order);
		} catch (Exception exc) {
			LOGGER.catching(exc);
		}
		return recordSet;
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		addActionToTable(new ActionMoveTo());
		addActionToTable(new ActionClearDrawings());
		super.actionPerformed(e);
	}
}
