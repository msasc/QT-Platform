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
package com.qtplaf.library.trading.chart.plotter.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import com.qtplaf.library.trading.chart.parameters.HorizontalAxisPlotParameters;
import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.Timestamp;

/**
 * Horizontal axis plotter.
 * 
 * @author Miquel Sas
 */
public class HorizontalAxisPlotter extends Plotter {

	/**
	 * An enumeration of the time periods to show with their approximate millis.
	 */
	enum TimePeriod {
		fiveMinutes(1000L * 60L * 5L, "00:00"),
		fifteenMinutes(1000L * 60L * 15L, "00:00"),
		thirtyMinutes(1000L * 60L * 30L, "00:00"),
		oneHour(1000L * 60L * 60L, "00:00"),
		threeHours(1000L * 60L * 60L * 3L, "00:00"),
		sixHours(1000L * 60L * 60L * 6L, "00:00"),
		twelveHours(1000L * 60L * 60L * 12L, "00:00"),
		day(1000L * 60L * 60L * 24L, "0000-00-00"),
		week(1000L * 60L * 60L * 24L * 7L, "0000-00-00"),
		month(1000L * 60L * 60L * 24L * 30L, "0000-00"),
		quarter(1000L * 60L * 60L * 24L * 90L, "0000-00"),
		year(1000L * 60L * 60L * 24L * 365L, "0000"),
		quinquennium(1000L * 60L * 60L * 24L * 365L * 5L, "0000"),
		decade(1000L * 60L * 60L * 24L * 365L * 10L, "0000");

		private long millis;
		private String string;

		TimePeriod(long millis, String string) {
			this.millis = millis;
			this.string = string;
		}

		long getMillis() {
			return millis;
		}

		String getString() {
			return string;
		}

	}

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param context The plotter context.
	 */
	public HorizontalAxisPlotter(PlotterContext context) {
		super();
		setContext(context);
		setName("Horizontal axis");
	}

	/**
	 * Returns the time string to plot.
	 * 
	 * @param time The time in millis.
	 * @param timePeriod The time period.
	 * @return The string to plot.
	 */
	private String getStringToPlot(long time, TimePeriod timePeriod) {

		boolean year;
		boolean month;
		boolean day;
		boolean hour;
		boolean minute;
		boolean second = false;
		boolean millis = false;
		boolean separators = true;

		switch (timePeriod) {
		case fiveMinutes:
		case fifteenMinutes:
		case thirtyMinutes:
		case oneHour:
		case threeHours:
		case sixHours:
		case twelveHours:
			year = false;
			month = false;
			day = false;
			hour = true;
			minute = true;
			return getStringToPlot(time, year, month, day, hour, minute, second, millis, separators);
		case day:
		case week:
			year = true;
			month = true;
			day = true;
			hour = false;
			minute = false;
			return getStringToPlot(time, year, month, day, hour, minute, second, millis, separators);
		case month:
		case quarter:
			year = true;
			month = true;
			day = false;
			hour = false;
			minute = false;
			return getStringToPlot(time, year, month, day, hour, minute, second, millis, separators);
		case year:
		case quinquennium:
		case decade:
		default:
			year = true;
			month = false;
			day = false;
			hour = false;
			minute = false;
			return getStringToPlot(time, year, month, day, hour, minute, second, millis, separators);
		}
	}

	/**
	 * Returns the string to plot.
	 * 
	 * @param time The time.
	 * @param year Year flag
	 * @param month Month flag.
	 * @param day Day flag.
	 * @param hour Hour flag.
	 * @param minute Minute flag.
	 * @param second Second flag.
	 * @param millis Millis flag.
	 * @param separators Separators flag.
	 * @return The string to ploit.
	 */
	private static String getStringToPlot(
		long time,
		boolean year,
		boolean month,
		boolean day,
		boolean hour,
		boolean minute,
		boolean second,
		boolean millis,
		boolean separators) {
		return FormatUtils.unformattedFromTimestamp(
			new Timestamp(time),
			year,
			month,
			day,
			hour,
			minute,
			second,
			millis,
			separators);
	}

	/**
	 * Plot the horizontal axis.
	 * 
	 * @param g2 The graphics context.
	 */
	public void plotAxis(Graphics2D g2) {

		// The first data list of the plot data is enough to get the lis of times.
		PlotData plotData = getContext().getPlotData();
		DataList dataList = plotData.get(0);

		// Set start and end time indexes.
		int startTimeIndex = plotData.getStartIndex();
		if (startTimeIndex < 0) {
			startTimeIndex = 0;
		}
		int endTimeIndex = plotData.getEndIndex();
		if (endTimeIndex >= dataList.size()) {
			endTimeIndex = dataList.size() - 1;
		}
		if (startTimeIndex >= endTimeIndex) {
			return;
		}

		// Calculate the available width to plot.
		int startX = getContext().getCoordinateX(startTimeIndex);
		int endX = getContext().getCoordinateX(endTimeIndex);
		int availableWidth = endX - startX + 1;

		// Start and end time.
		long startTime = dataList.get(startTimeIndex).getTime();
		long endTime = dataList.get(endTimeIndex).getTime();
		long timeElapsed = endTime - startTime;

		// The time period to plot.
		TimePeriod timePeriod = getTimePeriodThatFits(g2, timeElapsed, availableWidth);

		// Save font color and stroke.
		Font saveFont = g2.getFont();
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Parameters.
		HorizontalAxisPlotParameters plotParameters = getPlotParameters();

		// The text insets, font, color and stroke.
		Insets insets = plotParameters.getHorizontalAxisTextInsets();
		Font font = plotParameters.getHorizontalAxisTextFont();
		Color color = plotParameters.getHorizontalAxisColor();
		Stroke stroke = plotParameters.getHorizontalAxisLineStroke();

		// The font metrics.
		FontMetrics fm = g2.getFontMetrics(font);

		// Set font, color and stroke.
		g2.setFont(font);
		g2.setColor(color);
		g2.setStroke(stroke);

		// Necessary width to check overlaps.
		int necessaryWidth = insets.left + fm.stringWidth(timePeriod.getString()) + insets.right + 1;

		// Iterate from start index to end index.
		int lastX = 0;
		for (int index = startTimeIndex + 1; index <= endTimeIndex; index++) {

			// Current and previous times.
			long timeCurrent = dataList.get(index).getTime();
			long timePrevious = dataList.get(index - 1).getTime();

			// Check if the index is the start of the time period and if not do nothing.
			boolean startPeriod = isStartTimePeriod(timeCurrent, timePrevious, timePeriod);
			if (!startPeriod) {
				continue;
			}

			// Get the string and plot it.
			String stringToPlot = getStringToPlot(timeCurrent, timePeriod);
			int lineX = getContext().getCoordinateX(index);
			int x = lineX + insets.left;

			// Check overlap.
			if (x - lastX < necessaryWidth) {
				continue;
			}
			lastX = x;

			// Draw the vertical line.

			// Draw the string.
			int y = insets.top + fm.getAscent();
			g2.drawString(stringToPlot, x, y);
		}

		// Restore font color and stroke.
		g2.setFont(saveFont);
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}

	/**
	 * Check if the given time is the start time of the time period.
	 * 
	 * @param timeCurrent The current time to check.
	 * @param timePrevious The previous time to check.
	 * @param timePeriod The reference time period.
	 * @return A boolean that indicates if the given time is the start time of the time period.
	 */
	private boolean isStartTimePeriod(long timeCurrent, long timePrevious, TimePeriod timePeriod) {

		// The necessary calendars.
		Calendar calendarCurrent = new Calendar(timeCurrent);
		Calendar calendarPrevious = new Calendar(timePrevious);

		// Do check.
		switch (timePeriod) {
		case fiveMinutes:
			if (calendarCurrent.getMinute() == calendarPrevious.getMinute()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getMinute(), 5) == 0) {
				return true;
			}
			return false;
		case fifteenMinutes:
			if (calendarCurrent.getMinute() == calendarPrevious.getMinute()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getMinute(), 15) == 0) {
				return true;
			}
			return false;
		case thirtyMinutes:
			if (calendarCurrent.getMinute() == calendarPrevious.getMinute()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getMinute(), 30) == 0) {
				return true;
			}
			return false;
		case oneHour:
			if (calendarCurrent.getHour() == calendarPrevious.getHour()) {
				return false;
			}
			return true;
		case threeHours:
			if (calendarCurrent.getHour() == calendarPrevious.getHour()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getHour(), 3) == 0) {
				return true;
			}
			return false;
		case sixHours:
			if (calendarCurrent.getHour() == calendarPrevious.getHour()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getHour(), 6) == 0) {
				return true;
			}
			return false;
		case twelveHours:
			if (calendarCurrent.getHour() == calendarPrevious.getHour()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getHour(), 12) == 0) {
				return true;
			}
			return false;
		case day:
			if (calendarCurrent.getDay() == calendarPrevious.getDay()) {
				return false;
			}
			return true;
		case week:
			if (calendarCurrent.getWeek() == calendarPrevious.getWeek()) {
				return false;
			}
			return true;
		case month:
			if (calendarCurrent.getMonth() == calendarPrevious.getMonth()) {
				return false;
			}
			return true;
		case quarter:
			if (calendarCurrent.getMonth() == calendarPrevious.getMonth()) {
				return false;
			}
			if (calendarCurrent.getMonth() == 1) {
				return true;
			}
			if (calendarCurrent.getMonth() == 4) {
				return true;
			}
			if (calendarCurrent.getMonth() == 7) {
				return true;
			}
			if (calendarCurrent.getMonth() == 10) {
				return true;
			}
			return false;
		case year:
			if (calendarCurrent.getYear() == calendarPrevious.getYear()) {
				return false;
			}
			return true;
		case quinquennium:
			if (calendarCurrent.getYear() == calendarPrevious.getYear()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getYear(), 5) == 0) {
				return true;
			}
			return false;
		case decade:
			if (calendarCurrent.getYear() == calendarPrevious.getYear()) {
				return false;
			}
			if (NumberUtils.remainder(calendarCurrent.getYear(), 10) == 0) {
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	/**
	 * Returns the horizontal axis plot parameters.
	 * 
	 * @return The horizontal axis plot parameters.
	 */
	private HorizontalAxisPlotParameters getPlotParameters() {
		return getContext().getHorizontalAxisPlotParameters();
	}

	/**
	 * Returns the time period to plot that fits in the available width.
	 * 
	 * @param g2 The graphics context.
	 * @param timeElapsed The total time elapsed.
	 * @param availableWidth The available width.
	 * @return The time period that fits.
	 */
	private TimePeriod getTimePeriodThatFits(Graphics2D g2, long timeElapsed, int availableWidth) {

		// The font metrics to calculate text widths and the text insets.
		FontMetrics fm = g2.getFontMetrics(getPlotParameters().getHorizontalAxisTextFont());
		Insets insets = getPlotParameters().getHorizontalAxisTextInsets();

		// Iterate time periods.
		TimePeriod[] timePeriods = TimePeriod.values();
		for (TimePeriod timePeriod : timePeriods) {
			// The millis of the period.
			long millisPeriod = timePeriod.getMillis();
			// The number of periods within the time elapsed.
			double periods = timeElapsed / millisPeriod;
			// Available width per period.
			int availableWidthPerPeriod = (int) (availableWidth / periods);
			// The string to show the period.
			String string = timePeriod.getString();
			// The necessary width to show the period.
			int necessaryWidthPerPeriod = insets.left + fm.stringWidth(string) + insets.right + 1;
			// If the available width per period is greater than the necessary width per period, we are done.
			if (availableWidthPerPeriod > necessaryWidthPerPeriod) {
				return timePeriod;
			}
		}

		// Return a decade.
		return TimePeriod.decade;
	}
}
