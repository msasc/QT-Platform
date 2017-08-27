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
package com.qtplaf.library.trading.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qtplaf.library.trading.chart.JChartPlotter;
import com.qtplaf.library.trading.chart.drawings.Drawing;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.chart.plotter.data.DataPlotter;
import com.qtplaf.library.util.NumberUtils;

/**
 * A container for the data to plot in a <i>JChartContainer</i>.
 * 
 * @author Miquel Sas
 */
public class PlotData implements Iterable<DataList>, DataListListener {

	/** The number of bars to show at start when start and end indexes are not defined. */
	private int startNumberOfBars = 2000;

	/** A list of data lists. */
	private List<DataList> dataLists = new ArrayList<>();
	/** List of drawings. */
	private List<Drawing> drawings = new ArrayList<>();

	/** The start index to plot, can be negative, less that the min index. */
	private int startIndex = NumberUtils.MIN_INTEGER;
	/** The end index to plot, can greater than the max index. */
	private int endIndex = NumberUtils.MIN_INTEGER;
	/** The maximum value to plot (retrieving dataBag from start index to end index). */
	private double maximumValue = NumberUtils.MIN_DOUBLE;
	/** The minimum value to plot (retrieving dataBag from start index to end index). */
	private double minimumValue = NumberUtils.MAX_DOUBLE;

	/** The scale to plot the data. */
	private PlotScale plotScale = PlotScale.Linear;
	/** The plotter context. */
	private PlotterContext plotterContext;

	/** Optional name of this plot data. */
	private String name;

	/**
	 * Default constructor.
	 */
	public PlotData() {
		super();
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		if (name == null) {
			return toString();
		}
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the plotter context (set by the chart plotter).
	 * 
	 * @return The plotter context.
	 */
	public PlotterContext getPlotterContext() {
		return plotterContext;
	}

	/**
	 * Set the plotter context (set by the chart plotter.
	 * 
	 * @param chartPlotter The chart plotter.
	 */
	public void setPlotterContext(JChartPlotter chartPlotter) {
		plotterContext = new PlotterContext(chartPlotter, this);
		for (int i = 0; i < size(); i++) {
			get(i).setPlotterContext(plotterContext);
		}
	}

	/**
	 * Add a drawing to the list of drawings.
	 * 
	 * @param drawing The drawing.
	 */
	public void addDrawing(Drawing drawing) {
		drawings.add(drawing);
	}

	/**
	 * Returns the list of drawings.
	 * 
	 * @return The list of drawings.
	 */
	public List<Drawing> getDrawings() {
		return drawings;
	}

	/**
	 * Returns the size of this plot data, the number of data lists.
	 * 
	 * @return The number of data lists.
	 */
	public int size() {
		return dataLists.size();
	}

	/**
	 * Returns a boolean indicating if this plot data is empty.
	 * 
	 * @return Aboolean indicating if this plot data is empty.
	 */
	public boolean isEmpty() {
		return dataLists.isEmpty();
	}

	/**
	 * Adds a data list to this plot data. Data lists must be added in the correct order.
	 * <ul>
	 * <li>If the container is the main price container, the the price data list must be the first added, and no volume
	 * data list can be added. A subsequent indicator data list is considered an on chart indicator.</li>
	 * <li>If the container is the volume container, the volume data list must be the first, no price data list can be
	 * added, and subsequent indocator lists are considered on chart indicators.</li>
	 * <li>If the container is an indicator container, the indicator must be first added and only indicators can be
	 * added.</li>
	 * </ul>
	 * 
	 * @param dataList The data list.
	 * @return A boolean indicating if it was added.
	 */
	public boolean add(DataList dataList) {
		// Validate subsequent lists.
		if (!isEmpty()) {
			// The period must be the same.
			DataList firstList = get(0);
			if (!firstList.getDataInfo().getPeriod().equals(dataList.getDataInfo().getPeriod())) {
				throw new IllegalArgumentException("Data lists in the same plot data must have the same period.");
			}
			// Merge data lists so all have the same size.
			mergeDataLists();
		}
		boolean added = dataLists.add(dataList);
		dataList.addListener(this);
		setStartAndEndIndexes();
		return added;
	}

	/**
	 * Returns the list of first level indicator data lists to plot from scratch.
	 * 
	 * @return The list of first level indicator data lists to plot from scratch.
	 */
	public List<DataList> getDataListsIndicatorToPlotFromScratch() {
		List<DataList> fromScratch = new ArrayList<>();
		List<IndicatorDataList> firstLevel = DataList.getIndicatorDataLists(dataLists);
		for (IndicatorDataList dataList : firstLevel) {
			if (dataList.isPlotFromScratch()) {
				fromScratch.add(dataList);
			}
		}
		return fromScratch;
	}

	/**
	 * Returns the list of first level indicator data lists to plot the clip area.
	 * 
	 * @return The list of first level indicator data lists to plot the clip area.
	 */
	public List<DataList> getDataListsIndicatorToPlotClip() {
		List<DataList> fromScratch = new ArrayList<>();
		List<IndicatorDataList> firstLevel = DataList.getIndicatorDataLists(dataLists);
		for (IndicatorDataList dataList : firstLevel) {
			if (!dataList.isPlotFromScratch()) {
				fromScratch.add(dataList);
			}
		}
		return fromScratch;
	}

	/**
	 * Return the data lists that are not indicator data lists.
	 * 
	 * @return The data lists.
	 */
	public List<DataList> getDataListsNonIndicator() {
		List<DataList> nonIndicatorDataLists = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			DataList dataList = get(i);
			if (!(dataList instanceof IndicatorDataList)) {
				nonIndicatorDataLists.add(dataList);
			}
		}
		return nonIndicatorDataLists;
	}

	/**
	 * Merge data lists so all have the same size.
	 */
	private void mergeDataLists() {
		int maxSize = 0;
		for (DataList dataList : dataLists) {
			maxSize = Math.max(maxSize, dataList.size());
		}
		for (DataList dataList : dataLists) {
			int length = dataList.get(0).size();
			for (int i = dataList.size(); i < maxSize; i++) {
				Data data = new Data();
				data.setData(new double[length]);
				data.setValid(false);
				dataList.add(data);
			}
		}
	}

	/**
	 * Set the start and end indexes if they are not set.
	 */
	private void setStartAndEndIndexes() {
		if (dataLists.isEmpty()) {
			startIndex = endIndex = NumberUtils.MIN_INTEGER;
			return;
		}
		if (startIndex != NumberUtils.MIN_INTEGER && endIndex != NumberUtils.MIN_INTEGER) {
			return;
		}
		int size = dataLists.get(0).size();
		if (size > startNumberOfBars) {
			endIndex = size - 1;
			startIndex = endIndex - startNumberOfBars + 1;
		} else {
			endIndex = size - 1;
			startIndex = 0;
		}
	}

	/**
	 * Clear this plot data.
	 */
	public void clear() {
		dataLists.clear();
		setStartAndEndIndexes();
	}

	/**
	 * Returns the data list inthe argument index.
	 * 
	 * @param index The index.
	 * @return The data list.
	 */
	public DataList get(int index) {
		return dataLists.get(index);
	}

	/**
	 * Returns the data of the data list.
	 * 
	 * @param dataListIndex Tha data list index.
	 * @param dataIndex The index of the data in the data list.
	 * @return The data object.
	 */
	public Data getData(int dataListIndex, int dataIndex) {
		return get(dataListIndex).get(dataIndex);
	}

	/**
	 * Return the iterator.
	 * 
	 * @return The iterator.
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<DataList> iterator() {
		return dataLists.iterator();
	}

	/**
	 * Removes the data list at the given index.
	 * 
	 * @param index The idex.
	 * @return The removed data list.
	 */
	public DataList remove(int index) {
		setStartAndEndIndexes();
		return dataLists.remove(index);
	}

	/**
	 * Returns the data period.
	 * 
	 * @return The data period.
	 */
	public Period getPeriod() {
		if (isEmpty()) {
			return null;
		}
		return get(0).getDataInfo().getPeriod();
	}

	/**
	 * Returns the scale to plot this data.
	 * 
	 * @return The scale.
	 */
	public PlotScale getPlotScale() {
		return plotScale;
	}

	/**
	 * Sets the scale to plot this data.
	 * 
	 * @param plotScale The scale.
	 */
	public void setPlotScale(PlotScale plotScale) {
		this.plotScale = plotScale;
	}

	/**
	 * Set the initial start and end indexes to show the argument number of periods.
	 * 
	 * @param periods The number of periods to show.
	 */
	public void setInitialStartAndEndIndexes(int periods) {
		if (!isEmpty()) {
			int size = get(0).size();
			int endIndex = size - 1;
			int startIndex = endIndex - periods + 1;
			if (startIndex < 0) {
				startIndex = 0;
			}
			setStartIndex(startIndex);
			setEndIndex(endIndex);
		}
	}

	/**
	 * Set the start and end indexes from the argument plot data.
	 * 
	 * @param plotData The source plot data.
	 */
	public void setStartAndEndIndexesFrom(PlotData plotData) {
		setStartIndex(plotData.getStartIndex());
		setEndIndex(plotData.getEndIndex());
	}

	/**
	 * Returns the start index to plot.
	 * 
	 * @return The start index.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Sets the start index to plot.
	 * 
	 * @param startIndex The start index t plot.
	 */
	private void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * Returns the end index to plot.
	 * 
	 * @return The end index.
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * Sets the end index to plot.
	 * 
	 * @param endIndex The end index.
	 */
	private void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * Returns the maximum value to plot.
	 * 
	 * @return The maximum value.
	 */
	public double getMaximumValue() {
		return maximumValue;
	}

	/**
	 * Sets the maximum value to plot.
	 * 
	 * @param maximumValue The maximum value.
	 */
	public void setMaximumValue(double maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * Returns the minimum value to plot.
	 * 
	 * @return The minimum value.
	 */
	public double getMinimumValue() {
		return minimumValue;
	}

	/**
	 * Sets the minimum value to plot.
	 * 
	 * @param minimumValue The minimum value.
	 */
	public void setMinimumValue(double minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * Returns the pip scale to use.
	 * 
	 * @return The pip scale.
	 * @throws UnsupportedOperationException If the pip scale can not be resolved.
	 */
	public int getPipScale() {
		if (!isEmpty()) {
			int pipScale = -1;
			for (int i = 0; i < size(); i++) {
				pipScale = Math.max(pipScale, get(i).getDataInfo().getPipScale());
			}
			return pipScale;
		}
		throw new UnsupportedOperationException("Pip scale can not be resolved");
	}

	/**
	 * Returns the tick scale to use.
	 * 
	 * @return The tick scale.
	 * @throws UnsupportedOperationException If the tick scale can not be resolved.
	 */
	public int getTickScale() {
		if (!isEmpty()) {
			int tickScale = -1;
			for (int i = 0; i < size(); i++) {
				tickScale = Math.max(tickScale, get(i).getDataInfo().getTickScale());
			}
			return tickScale;
		}
		throw new UnsupportedOperationException("Tick scale can not be resolved");
	}

	/**
	 * Returns the volume scale.
	 * 
	 * @return The volume scale.
	 * @throws UnsupportedOperationException If the tick scale can not be resolved.
	 */
	public int getVolumeScale() {
		if (!isEmpty()) {
			int volumeScale = -1;
			for (int i = 0; i < size(); i++) {
				volumeScale = Math.max(volumeScale, get(i).getDataInfo().getVolumeScale());
			}
			return volumeScale;
		}
		throw new UnsupportedOperationException("Volume scale can not be resolved");
	}

	/**
	 * Check if the maximum and minimum values have been calculated.
	 * 
	 * @return A boolean that indicates if the maximum and minimum values have been calculated.
	 */
	public boolean areMaximumAndMinimumValuesCalculated() {
		return maximumValue != NumberUtils.MIN_DOUBLE && minimumValue != NumberUtils.MAX_DOUBLE;
	}

	/**
	 * Ensure that indicators are calculated from look backward up to the start index minus one.
	 */
	private void ensureIndicatorsCalculated() {
		// Index 0, no need to do nothing.
		if (startIndex == 0) {
			return;
		}

		// If no indicator data lists...
		List<IndicatorDataList> indicatorDataLists = DataList.getIndicatorDataListsToCalculate(dataLists);
		if (indicatorDataLists.isEmpty()) {
			return;
		}

		// Get the maximum look backward.
		int lookBackward = 0;
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			Indicator indicator = indicatorDataList.getIndicator();
			lookBackward = Math.max(lookBackward, indicator.getIndicatorInfo().getLookBackward());
		}

		// Remove calculated from start to end, and calculate again.
		int start = Math.max(0, startIndex - lookBackward + 1);
		int end = startIndex - 1;
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			for (int index = start; index <= end; index++) {
				indicatorDataList.remove(index);
			}
		}
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			for (int index = start; index <= end; index++) {
				indicatorDataList.calculate(index);
			}
		}
	}

	/**
	 * Calculates plot frame based on start and end index: minimum and maximum values, start end end time.
	 */
	public void calculateFrame() {

		// Check that there is data to calculate the frame.
		if (isEmpty()) {
			throw new IllegalStateException();
		}

		// Ensure that indicators are calculated up to the start index minus one.
		ensureIndicatorsCalculated();

		int dataSize = get(0).size();
		double maxValue = NumberUtils.MIN_DOUBLE;
		double minValue = NumberUtils.MAX_DOUBLE;
		for (int i = startIndex; i < endIndex; i++) {
			if (i < 0 || i >= dataSize) {
				continue;
			}
			for (DataList dataList : dataLists) {
				Data data = dataList.get(i);
				if (data == null || !data.isValid()) {
					continue;
				}
				List<DataPlotter> dataPlotters = dataList.getDataPlotters();
				for (DataPlotter dataPlotter : dataPlotters) {
					double[] values = dataPlotter.getValues(data);
					for (double value : values) {
						if (value > maxValue) {
							maxValue = value;
						}
						if (value < minValue) {
							minValue = value;
						}
					}
				}
			}
		}

		// Assign calculated min and max values.
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	/**
	 * Mode to the index or period, centering it on screen, with the current number of shown bars.
	 * 
	 * @param index The index to move to.
	 */
	public void move(int index) {
		int minIndex = 0;
		int maxIndex = get(0).size() - 1;
		if (index < minIndex || index > maxIndex) {
			return;
		}
		int indexes = endIndex - startIndex + 1;
		startIndex = index - indexes / 2;
		if (startIndex < minIndex) {
			startIndex = minIndex;
		}
		endIndex = index + indexes / 2;
		if (endIndex > maxIndex) {
			endIndex = maxIndex;
		}
	}

	/**
	 * Center start and end indexes.
	 * 
	 * @param start Start index.
	 * @param end End index.
	 */
	public void center(int start, int end) {

		// Min/max indexes.
		int minIndex = 0;
		int maxIndex = get(0).size() - 1;

		// Current start and end indexes.
		int startIndex = this.startIndex;
		int endIndex = this.endIndex;

		// Remainder.
		int remainder = (endIndex - startIndex + 1) - (end - start + 1);

		// If remainder is greater than zero, just center.
		if (remainder > 0) {
			startIndex = start - remainder / 2;
			if (startIndex < minIndex) {
				startIndex = minIndex;
			}
			endIndex = end + remainder / 2;
			if (endIndex > maxIndex) {
				endIndex = maxIndex;
			}
		} else {
			startIndex = start;
			if (startIndex < minIndex) {
				startIndex = minIndex;
			}
			endIndex = end;
			if (endIndex > maxIndex) {
				endIndex = maxIndex;
			}
		}

		// Assign.
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/**
	 * Scroll a certain number of periods or bars.
	 * <ul>
	 * <li>If the number of periods to scroll is negative, the <i>startIndex</i> and <i>endIndex</i> decrease, moving
	 * the plot to the right.</li>
	 * <li>If the number is positive, reverse the policy.</li>
	 * <li>The limit to scroll is to leave at least one visible bar. If <i>periods</i> is negative, <i>endIndex</i> has
	 * to be greater or equal to zero.</li>
	 * <li>If <i>periods</i> is positive, <i>startIndex</i> has to less than the data size.</li>
	 * </ul>
	 * 
	 * @param periods The number of periods to scroll.
	 */
	public void scroll(int periods) {
		if (isEmpty()) {
			return;
		}
		if (periods == 0) {
			return;
		}
		if (get(0).isEmpty()) {
			return;
		}
		int dataSize = get(0).size();
		int periodsToScroll;
		if (periods < 0) {
			periodsToScroll = (-1) * Math.min(endIndex, Math.abs(periods));
		} else {
			periodsToScroll = Math.min(periods, dataSize - startIndex - 1);
		}
		startIndex += periodsToScroll;
		endIndex += periodsToScroll;
	}

	/**
	 * Zoom a certain number of periods.
	 * <ul>
	 * <li>If the number of periods is negative, then zoom out increasing the number of bars shown by the number of
	 * periods.</li>
	 * <li>If the number of periods is positive, then zoom in decreasing the number of bars shown by the number of
	 * periods, leaving at least one bar visible, that is, <i>startIndex</i> and <i>endIndex</i> are the same and in the
	 * range of data.</li>
	 * <li>If zoom out and both <i>startIndex</i> and <i>endIndex</i> are in the range of data, then decrease
	 * <i>startIndex</i> and increase <i>endIndex</i> by the same number of periods.</li>
	 * <li>If zoom out and only one of the indexes is out of range, then move the othe one accordingly.</li>
	 * <li>If zoom out and both indexes are out of range, do not zoom.</li>
	 * </ul>
	 * 
	 * @param periods Thenumber of periods or bars to zoom.
	 */
	public void zoom(int periods) {
		if (isEmpty()) {
			return;
		}
		if (periods == 0) {
			return;
		}
		if (get(0).isEmpty()) {
			return;
		}
		int dataSize = get(0).size();
		boolean zoomOut = (periods < 0);
		boolean zoomIn = !zoomOut;
		periods = Math.abs(periods);

		// Zoom out
		if (zoomOut) {

			// If both indexes are out of range, do not zoom.
			if (startIndex < 0 && endIndex >= dataSize) {
				return;
			}

			// If both indexes are in the range of data, decrease the start index and increase the end index.
			if (startIndex >= 0 && endIndex < dataSize) {
				startIndex -= periods;
				endIndex += periods;
				return;
			}

			// If only startIndex is in the range...
			if (startIndex >= 0 && endIndex >= dataSize) {
				startIndex -= periods;
				return;
			}

			// If only endIndex is in the range...
			if (startIndex < 0 && endIndex < dataSize) {
				endIndex += periods;
				return;
			}
		}

		// Zoom in: always zoom, with the limit that indexes do not overlap and leaving at least one visible bar.
		if (zoomIn) {

			// If start and end indexes are the same, do nothing.
			if (startIndex == endIndex - 1) {
				return;
			}

			// If both indexes are out of range will zoom in the same way as if only the end index is out of range, that
			// is, zoom in the left of the chart maintaining as possible the right blank proportion.
			if (endIndex >= dataSize) {
				double startPeriods = endIndex - startIndex + 1;
				double blankPeriods = endIndex - dataSize;
				double blankFactor = blankPeriods / startPeriods;
				double endPeriods = startPeriods - periods;
				int endBlankPeriods = (int) (endPeriods * blankFactor);
				// Since there were blank periods, leave at list one.
				if (endBlankPeriods == 0) {
					endBlankPeriods = 1;
				}
				endIndex = dataSize + endBlankPeriods;
				startIndex = endIndex - (int) endPeriods + 1;
				checkIndexes();
				return;
			}

			// If both indexes are in the range zoom both sides
			if (startIndex >= 0 && endIndex < dataSize) {
				startIndex += periods;
				endIndex -= periods;
				checkIndexes();
				return;
			}

			// If only endIndex is in the range, zoom the right of the chart maintaining as possible the left blank
			// proportion.
			if (startIndex < 0 && endIndex < dataSize) {
				double startPeriods = endIndex - startIndex + 1;
				double blankPeriods = Math.abs(startIndex);
				double blankFactor = blankPeriods / startPeriods;
				double endPeriods = startPeriods - periods;
				int endBlankPeriods = (int) (endPeriods * blankFactor);
				// Since there were blank periods, leave at list one.
				if (endBlankPeriods == 0) {
					endBlankPeriods = 1;
				}
				startIndex = -endBlankPeriods;
				endIndex = startIndex + (int) endPeriods - 1;
				checkIndexes();
				return;
			}
		}
	}

	/**
	 * Check that indexes do not overlap.
	 */
	private void checkIndexes() {
		int dataSize = get(0).size();
		if (endIndex < startIndex) {
			endIndex = startIndex;
		}
		if (startIndex < 0 && endIndex < 0) {
			startIndex = endIndex = 0;
		}
		if (startIndex >= dataSize && endIndex >= dataSize) {
			startIndex = endIndex = dataSize - 1;
		}
		if (startIndex >= 0 && endIndex < dataSize) {
			if (endIndex == startIndex) {
				if (endIndex < dataSize - 1) {
					endIndex += 1;
				} else {
					startIndex -= 1;
				}
			}
		}
	}

	/**
	 * Called to notify changes in a data list.
	 * 
	 * @param e The data list event.
	 */
	@Override
	public void dataListChanged(DataListEvent e) {
	}

	/**
	 * Returns a string representation of this plota data.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		Instrument instrument = null;
		Period period = null;
		List<String> names = new ArrayList<>();
		for (DataList dataList : dataLists) {
			if (instrument == null) {
				instrument = dataList.getDataInfo().getInstrument();
			}
			if (period == null) {
				period = dataList.getDataInfo().getPeriod();
			}
			names.add(dataList.getDataInfo().getName());
		}
		StringBuilder b = new StringBuilder();
		b.append(instrument);
		b.append(", ");
		b.append(period);
		for (String name : names) {
			b.append(", ");
			b.append(name);
		}
		return b.toString();
	}
}
