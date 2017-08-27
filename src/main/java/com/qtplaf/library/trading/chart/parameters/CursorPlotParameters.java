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

package com.qtplaf.library.trading.chart.parameters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;

import com.qtplaf.library.trading.chart.CursorType;

/**
 * Cursor plot parameters.
 *
 * @author Miquel Sas
 */
public class CursorPlotParameters {

	/**
	 * ChartCross cursor circle radius.
	 */
	private int chartCrossCursorCircleRadius = 0;
	/**
	 * Chart plotter: cross cursor color.
	 */
	private Color chartCrossCursorColor = Color.GRAY;
	/**
	 * Chart plotter: cross cursor height.
	 */
	private int chartCrossCursorHeight = -1;
	/**
	 * Chart plotter: cross cursor stroke.
	 */
	private Stroke chartCrossCursorStroke = new BasicStroke(
		0.0f,
		BasicStroke.CAP_ROUND,
		BasicStroke.JOIN_MITER,
		3.0f,
		new float[] { 3.0f },
		0.0f);
	/**
	 * Chart plotter: cross cursor width.
	 */
	private int chartCrossCursorWidth = -1;
	/**
	 * The cursot type to use.
	 */
	private CursorType chartCursorType = CursorType.Custom;
	/**
	 * An integer that denotes the system cursor that applies, default is cross hair..
	 */
	private int chartCursorTypePredefined = Cursor.DEFAULT_CURSOR;

	/**
	 * Constructor assigning the parent chart.
	 */
	public CursorPlotParameters() {
		super();
	}

	/**
	 * Returns the cross cursor circle radius.
	 * 
	 * @return The cross cursor circle radius.
	 */
	public int getChartCrossCursorCircleRadius() {
		return chartCrossCursorCircleRadius;
	}

	/**
	 * Returns the cross cursor color.
	 * 
	 * @return The cross cursor color.
	 */
	public Color getChartCrossCursorColor() {
		return chartCrossCursorColor;
	}

	/**
	 * Returns the cross cursor height.
	 * 
	 * @return The cross cursor height.
	 */
	public int getChartCrossCursorHeight() {
		return chartCrossCursorHeight;
	}

	/**
	 * Returns the cross cursor stroke.
	 * 
	 * @return The cross cursor stroke.
	 */
	public Stroke getChartCrossCursorStroke() {
		return chartCrossCursorStroke;
	}

	/**
	 * Returns the cross cursor width.
	 * 
	 * @return The cross cursor width.
	 */
	public int getChartCrossCursorWidth() {
		return chartCrossCursorWidth;
	}

	/**
	 * Returns the default chart cursor type to use.
	 * 
	 * @return The default chart cursor type to use.
	 */
	public CursorType getChartCursorType() {
		return chartCursorType;
	}

	/**
	 * Returns the chart cursor type predefined.
	 * 
	 * @return The chart cursor type predefined.
	 */
	public int getChartCursorTypePredefined() {
		return chartCursorTypePredefined;
	}

	/**
	 * Sets the cross cursor circle radius, less or equal zero paints no circle.
	 * 
	 * @param chartCrossCursorCircleRadius The cross cursor circle radius, less or equal zero paints no circle.
	 */
	public void setChartCrossCursorCircleRadius(int chartCrossCursorCircleRadius) {
		this.chartCrossCursorCircleRadius = chartCrossCursorCircleRadius;
	}

	/**
	 * Sets the cross cursor color.
	 * 
	 * @param chartCrossCursorColor The cross cursor color.
	 */
	public void setChartCrossCursorColor(Color chartCrossCursorColor) {
		this.chartCrossCursorColor = chartCrossCursorColor;
	}

	/**
	 * Sets the cross cursor height.
	 * 
	 * @param chartCrossCursorHeight The cross cursor height.
	 */
	public void setChartCrossCursorHeight(int chartCrossCursorHeight) {
		this.chartCrossCursorHeight = chartCrossCursorHeight;
	}

	/**
	 * Sets the cross cursor stroke.
	 * 
	 * @param chartCrossCursorStroke The cross cursor stroke.
	 */
	public void setChartCrossCursorStroke(Stroke chartCrossCursorStroke) {
		this.chartCrossCursorStroke = chartCrossCursorStroke;
	}

	/**
	 * Sets the cross cursor width.
	 * 
	 * @param chartCrossCursorWidth The cross cursor width.
	 */
	public void setChartCrossCursorWidth(int chartCrossCursorWidth) {
		this.chartCrossCursorWidth = chartCrossCursorWidth;
	}

	/**
	 * Sets the default cursor type to use.
	 * 
	 * @param chartCursorType The default cursor type to use.
	 */
	public void setChartCursorType(CursorType chartCursorType) {
		this.chartCursorType = chartCursorType;
	}

	/**
	 * Sets the chart cursor type.
	 * 
	 * @param chartCursorTypePredefined The chart cursor type.
	 */
	public void setChartCursorTypePredefined(int chartCursorTypePredefined) {
		if (chartCursorTypePredefined < -1 || chartCursorTypePredefined > Cursor.MOVE_CURSOR) {
			throw new IllegalArgumentException("Illegal cursor type");
		}
		this.chartCursorTypePredefined = chartCursorTypePredefined;
	}

}
