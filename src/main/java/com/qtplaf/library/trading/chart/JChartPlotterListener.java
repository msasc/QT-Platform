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
package com.qtplaf.library.trading.chart;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.qtplaf.library.swing.event.Mask;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.trading.chart.parameters.InformationPlotParameters;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.ColorUtils;

/**
 * Listener of event generated in the <i>JChartPlotter</i>.
 * 
 * @author Miquel Sas
 */
public class JChartPlotterListener extends MouseHandler {

	/**
	 * The parent <i>JChartPlotter</i>.
	 */
	private JChartPlotter chartPlotter;
	/**
	 * The wheel rotation factor. Wheel rotations are always 1, but rotation for scroll and zoom require a factor of the
	 * number of bars visible.
	 */
	private double wheelRotationFactor = 0.1;
	/**
	 * Mouse dragging control (left button) to scroll the chart.
	 */
	private boolean mouseDragging = false;
	/**
	 * The previous X coordinate when mouse dragging (always in the chart/component area).
	 */
	private int mouseDraggingX = 0;
	/**
	 * The saved cursor when mouse enters the component.
	 */
	private Cursor cursor;

	/**
	 * Constructor assigning the parent <i>JChartPlotter</i>.
	 * 
	 * @param chartPlotter The chart plotter.
	 */
	public JChartPlotterListener(JChartPlotter chartPlotter) {
		super();
		this.chartPlotter = chartPlotter;
	}

	/**
	 * Returns the wheel rotation factor used to calculate the rotation as a factor of the number of visible bars.
	 * 
	 * @return The wheel rotation factor.
	 */
	public double getWheelRotationFactor() {
		return wheelRotationFactor;
	}

	/**
	 * Sets the wheel rotation factor used to calculate the rotation as a factor of the number of visible bars.
	 * 
	 * @param wheelRotationFactor The wheel rotation factor.
	 */
	public void setWheelRotationFactor(double wheelRotationFactor) {
		this.wheelRotationFactor = wheelRotationFactor;
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// Check popup menu.
		if (triggerPopupMenu(e)) {
			return;
		}
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// Check popup menu.
		if (triggerPopupMenu(e)) {
			return;
		}
		// Release dragging (left button) if applicable
		if (e.getButton() == MouseEvent.BUTTON1 && mouseDragging) {
			mouseDragging = false;
			mouseDraggingX = -1;
		}
	}

	/**
	 * Check and trigger the popup menu.
	 * 
	 * @param e The mouse event.
	 * @return A boolean that indicates if processing the event should stop.
	 */
	private boolean triggerPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			chartPlotter.getChartContainer().getChart().triggerPopupMenu(chartPlotter, e.getPoint());
			return true;
		}
		return false;
	}

	/**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

		// Save the cursor.
		cursor = chartPlotter.getCursor();

		// Tell the chart plotter to set the appropriate cursor.
		chartPlotter.setCursor();

		// Set the info background color.
		setInfoBackground();
	}

	/**
	 * Invoked when the mouse exits a component.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// Restore the cursor.
		if (cursor != null) {
			chartPlotter.setCursor(cursor);
		}
		chartPlotter.clearMousePoint(true);
		chartPlotter.getChartContainer().getChartVerticalAxis().clearMousePoint(true);
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// Only button1, scroll.
		if (Mask.check(e, Mask.BUTTON1)) {
			dragScroll(e);
		}
	}

	/**
	 * Scroll dragging with only button1 pressed.
	 * 
	 * @param e The mouse event.
	 */
	private void dragScroll(MouseEvent e) {
		PlotData plotData = chartPlotter.getChartContainer().getPlotData();
		if (plotData == null) {
			return;
		}
		if (!mouseDragging) {
			mouseDragging = true;
			mouseDraggingX = e.getX();
		}
		if (mouseDragging) {
			// No modifiers, do scroll.
			if ((e.getModifiersEx() & 0) == 0) {
				int x = e.getX();
				if (x >= 0 && x <= chartPlotter.getSize().getWidth()) {
					if (mouseDraggingX != x) {
						// The absolute width factor.
						double widthFactor =
							Math.abs((x - mouseDraggingX) / chartPlotter.getSize().getWidth());
						// Convert the width factor into index length.
						int startIndex = plotData.getStartIndex();
						int endIndex = plotData.getEndIndex();
						int indexScroll = Math.abs((int) ((endIndex - startIndex) * widthFactor));
						if (indexScroll < 1) {
							indexScroll = 1;
						}
						// Scroll plot data.
						if (x < mouseDraggingX) {
							plotData.scroll(indexScroll);
						} else {
							plotData.scroll(-indexScroll);
						}
						mouseDraggingX = x;

						// Set the mouse point to the chart plotter to paint the cursor as required.
						chartPlotter.setMousePoint(e.getPoint(), false);

						// Draw the value in the vertical axis.
						chartPlotter.getChartContainer().getChartVerticalAxis().setMousePoint(e.getPoint(), false);

						// Propagate changes.
						chartPlotter.getChartContainer().getChart().propagateFrameChanges(plotData);
					}
				}
			}
		}
	}

	/**
	 * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
	 * 
	 * @param e The mouse event.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		// This chart plotter container.
		JChartContainer chartContainer = chartPlotter.getChartContainer();

		// Set the info in the info panel of this chart plotter.
		chartContainer.setChartInfo(e.getX(), e.getY());

		// Set the mouse point to the chart plotter to paint the cursor as required.
		chartPlotter.setMousePoint(e.getPoint(), true);

		// Propagate to the rest of chart containers.
		JChart chart = chartContainer.getChart();
		Point point = new Point(e.getX(), -1);
		for (int i = 0; i < chart.getChartCount(); i++) {
			if (chart.getChartContainer(i).equals(chartContainer)) {
				continue;
			}
			chart.getChartContainer(i).setChartInfo(e.getX(), -1);
			chart.getChartContainer(i).getChartPlotter().setMousePoint(point, true);
		}

		// Draw the value in the vertical axis.
		chartContainer.getChartVerticalAxis().setMousePoint(e.getPoint(), true);
	}

	/**
	 * Invoked when the mouse wheel is rotated.
	 * 
	 * @param e The mouse wheel event.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		// PlotterOld data and start and end indexes.
		PlotData plotData = chartPlotter.getChartContainer().getPlotData();
		if (plotData == null) {
			return;
		}
		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();

		// Calculate the number of bars to scroll or zoom.
		int barsVisible = endIndex - startIndex + 1;
		int barsToScrollOrZoom = (int) (barsVisible * wheelRotationFactor);
		if (barsToScrollOrZoom < 1) {
			barsToScrollOrZoom = 1;
		}
		barsToScrollOrZoom *= e.getWheelRotation();

		// If the control key is down, zoom.
		if (Mask.check(e, Mask.CTRL)) {
			plotData.zoom(barsToScrollOrZoom);
		}

		// Only wheel, scroll.
		if (Mask.check(e, 0)) {
			plotData.scroll(barsToScrollOrZoom);
		}

		// Set the mouse point to the chart plotter to paint the cursor as required.
		chartPlotter.setMousePoint(e.getPoint(), false);

		// Propagate changes.
		chartPlotter.getChartContainer().getChart().propagateFrameChanges(plotData);
	}

	/**
	 * Sets the info background color.
	 */
	private void setInfoBackground() {

		JChartContainer chartContainer = chartPlotter.getChartContainer();
		JChart chart = chartContainer.getChart();
		InformationPlotParameters plotParameters = chart.getInfoPlotParameters();

		Color colorFocusGained = plotParameters.getInfoBackgroundColor();
		double brightnessFactor = plotParameters.getInfoBackgroundBrightnessFactor();
		Color colorFocusLost = ColorUtils.brighter(colorFocusGained, brightnessFactor);

		chartContainer.getChartInfo().setPanelInfoBackgorund(colorFocusGained);
		chartContainer.getChartInfo().repaintInfo();

		int chartCount = chart.getChartCount();
		for (int i = 0; i < chartCount; i++) {
			if (chart.getChartContainer(i).equals(chartContainer)) {
				continue;
			}
			chart.getChartContainer(i).getChartInfo().setPanelInfoBackgorund(colorFocusLost);
			chart.getChartContainer(i).getChartInfo().repaintInfo();
		}
	}
}
