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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.trading.chart.plotter.data.BufferedLinePlotter;
import com.qtplaf.library.trading.chart.plotter.data.CandlestickPlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.fields.FieldDataInstr;
import com.qtplaf.platform.database.fields.FieldDataValue;

/**
 * Ticker statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class Averages extends TickerStatistics {

	/** List of averages. */
	private List<Average> averages = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session
	 */
	public Averages(Session session) {
		super(session);
	}

	/**
	 * Add an average.
	 * 
	 * @param average The average.
	 */
	public void addAverage(Average average) {
		averages.add(average);
	}

	/**
	 * Returns the list of averages.
	 * 
	 * @return The list of averages.
	 */
	public List<Average> getAverages() {
		return averages;
	}

	/**
	 * Returns the list of average fields.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListAverages() {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Fields.averageName(average);
			String header = Fields.averageHeader(average);
			String label = Fields.averageLabel(average);
			Field field = new FieldDataInstr(getSession(), getInstrument(), name, header, label);
			field.setProperty(Fields.Properties.AVERAGE, average);
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Returns the list of slope fields, raw values
	 * 
	 * @param suffix The suffix.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSlopes(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Fields.slopeName(average, suffix);
			String header = Fields.slopeHeader(average, suffix);
			String label = Fields.slopeLabel(average, suffix);
			fields.add(new FieldDataValue(getSession(), name, header, label));
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @param suffix The suffix.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreads(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < averages.size(); i++) {
			Average fast = averages.get(i);
			for (int j = i + 1; j < averages.size(); j++) {
				Average slow = averages.get(j);
				String name = Fields.spreadName(fast, slow, suffix);
				String header = Fields.spreadHeader(fast, slow, suffix);
				String label = Fields.spreadLabel(fast, slow, suffix);
				fields.add(new FieldDataValue(getSession(), name, header, label));
			}
		}
		return fields;
	}

	/**
	 * Returns the main plot data for the price and averages.
	 * 
	 * @param dataList The source list.
	 * @return The plot data.
	 */
	public PlotData getPlotDataMain(PersistorDataList dataList) {

		// First data list: price and indicators.
		DataInfo info = dataList.getDataInfo();
		info.setInstrument(getInstrument());
		info.setName("OHLC");
		info.setDescription("OHLC values");
		info.setPeriod(getPeriod());

		// Candlestick on price: info
		info.addOutput("Open", "O", dataList.getDataIndex(Fields.OPEN), "Open data value");
		info.addOutput("High", "H", dataList.getDataIndex(Fields.HIGH), "High data value");
		info.addOutput("Low", "L", dataList.getDataIndex(Fields.LOW), "Low data value");
		info.addOutput("Close", "C", dataList.getDataIndex(Fields.CLOSE), "Close data value");

		// Candlestick on price: plotter.
		CandlestickPlotter plotterCandle = new CandlestickPlotter();
		plotterCandle.setIndexes(new int[] {
			dataList.getDataIndex(Fields.OPEN),
			dataList.getDataIndex(Fields.HIGH),
			dataList.getDataIndex(Fields.LOW),
			dataList.getDataIndex(Fields.CLOSE) });
		dataList.addDataPlotter(plotterCandle);

		// Line plotter for each average.
		List<Field> averageFields = getFieldListAverages();
		for (Field field : averageFields) {
			String name = field.getName();
			String label = field.getLabel();
			String header = field.getHeader();
			int index = dataList.getDataIndex(name);

			// Output info.
			info.addOutput(name, header, index, label);

			// Plotter.
			BufferedLinePlotter plotterAvg = new BufferedLinePlotter();
			plotterAvg.setIndex(index);
			dataList.addDataPlotter(plotterAvg);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);
		return plotData;
	}

	/**
	 * Returns the plot data for the list of fields.
	 * 
	 * @param dataName The name of the data list.
	 * @param sourceList The source list.
	 * @param fields The list of fields.
	 * @return The plot data.
	 */
	public PlotData getPlotData(String dataName, PersistorDataList sourceList, List<Field> fields) {

		// Data info.
		DataInfo info = new DataInfo(getSession());
		info.setInstrument(getInstrument());
		if (dataName == null) {
			dataName = getInstrument().getId();
		}
		info.setName(dataName);
		info.setDescription(getInstrument().getDescription());
		info.setPeriod(getPeriod());

		// Data list.
		DataList dataList = new DelegateDataList(getSession(), info, sourceList);

		for (Field field : fields) {
			String name = field.getName();
			String header = field.getHeader();
			String label = field.getLabel();
			int index = sourceList.getDataIndex(name);

			// Output info.
			info.addOutput(name, header, index, label);

			// Plotter.
			BufferedLinePlotter plotter = new BufferedLinePlotter();
			plotter.setIndex(index);
			dataList.addDataPlotter(plotter);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);

		return plotData;
	}
}
