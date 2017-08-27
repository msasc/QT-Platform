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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.trading.chart.parameters.InformationPlotParameters;
import com.qtplaf.library.util.Icons;

/**
 * A panel located at the top of the container, aimed to contain an info panel and necessary controls like the close
 * button.
 * 
 * @author Miquel Sas
 */
public class JChartInfo extends JPanel {

	/**
	 * The info panel.
	 */
	class JPanelInfo extends JPanel {

		/**
		 * Default constructor.
		 */
		JPanelInfo() {
			super();
		}

		/**
		 * Paint this info panel.
		 * 
		 * @param g The graphics object.
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);

			// If no info items to paint, do nothing.
			if (infoItems.isEmpty()) {
				return;
			}

			Graphics2D g2 = (Graphics2D) g;

			// Info plot params.
			InformationPlotParameters plotParameters = chartContainer.getChart().getInfoPlotParameters();

			// Save the current color and font.
			Color saveColor = g2.getColor();
			Font saveFont = g2.getFont();

			// Set the font.
			g2.setFont(plotParameters.getInfoTextFont());

			// Retrieve the font metrics to calculate the font total height.
			FontMetrics fm = g2.getFontMetrics();

			// Starting X and Y
			int x = plotParameters.getInfoTextInsets().left;
			int y = plotParameters.getInfoTextInsets().top + fm.getMaxAscent();

			// Separator control flag.
			boolean separator = false;

			// Iterate groups.
			Iterator<InfoItem> i = infoItems.iterator();
			while (i.hasNext()) {
				InfoItem infoItem = i.next();

				// If the item is not active skip it.
				if (!infoItem.isActive()) {
					continue;
				}

				// If the item is empty, skip it.
				if (infoItem.isEmpty()) {
					continue;
				}

				// If necessary paint the separator.
				if (separator) {
					g2.setColor(plotParameters.getInfoSeparatorColor());
					String separatorStrings = plotParameters.getInfoSeparatorString();
					g2.drawString(separatorStrings, x, y);
					x += fm.stringWidth(separatorStrings);
				}

				// Paint the item.
				String text = infoItem.getText();
				Color color = infoItem.getColor();
				if (infoItem.getStyle() != plotParameters.getInfoTextFont().getStyle()) {
					Font itemFont = new Font(
						plotParameters.getInfoTextFont().getName(),
						infoItem.getStyle(),
						plotParameters.getInfoTextFont().getSize());
					g2.setFont(itemFont);
				}
				g2.setColor(color);
				g2.drawString(text, x, y);
				x += fm.stringWidth(text);

				// From now the separator is needed.
				separator = true;

				// Restore the working font if necessary.
				if (infoItem.getStyle() != plotParameters.getInfoTextFont().getStyle()) {
					g2.setFont(plotParameters.getInfoTextFont());
				}
			}

			// Restore the color and font.
			g2.setColor(saveColor);
			g2.setFont(saveFont);
		}
	}

	/**
	 * The action class for the close button.
	 */
	class ActionCloseButton extends AbstractAction {

		/**
		 * Default constructor.
		 */
		ActionCloseButton() {
			super();
			ActionUtils.setSmallIcon(this, Icons.chart_16x16_titlebar_close_tab);
		}

		/**
		 * Perform the action, remove this chart container.
		 * 
		 * @param e The action event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			chartContainer.getChart().removeChartContainer(chartContainer);
		}
	}

	/**
	 * An information item that is a text with a color.
	 */
	class InfoItem {
		/**
		 * The string identifier.
		 */
		private String id;
		/**
		 * The info text.
		 */
		private String text;
		/**
		 * The info color.
		 */
		private Color color;
		/**
		 * The font style.
		 */
		private int style = Font.PLAIN;
		/**
		 * A boolean that indicates if the info item is active.
		 */
		private boolean active = true;

		/**
		 * Constructor assigning text, color and style.
		 * 
		 * @param id The info identifier.
		 */
		InfoItem(String id) {
			super();
			this.id = id;
		}

		/**
		 * Returns the string identifier.
		 * 
		 * @return The string identifier.
		 */
		public String getId() {
			return id;
		}

		/**
		 * Returns the text.
		 * 
		 * @return The text.
		 */
		public String getText() {
			return text;
		}

		/**
		 * Sets the text.
		 * 
		 * @param text The text.
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 * Returns the color.
		 * 
		 * @return The color.
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * Sets the color.
		 * 
		 * @param color The color.
		 */
		public void setColor(Color color) {
			this.color = color;
		}

		/**
		 * Returns the font style.
		 * 
		 * @return The font style.
		 */
		public int getStyle() {
			return style;
		}

		/**
		 * Sets the font style.
		 * 
		 * @param style The font style.
		 */
		public void setStyle(int style) {
			this.style = style;
		}

		/**
		 * Returns a boolean indicating if this info item is active.
		 * 
		 * @return A boolean indicating if this info item is active.
		 */
		public boolean isActive() {
			return active;
		}

		/**
		 * Sets a boolean indicating if this info item is active.
		 * 
		 * @param active A boolean indicating if this info item is active.
		 */
		public void setActive(boolean active) {
			this.active = active;
		}

		/**
		 * Check if this info item is empty.
		 * 
		 * @return A boolean that indicates that the info is empty.
		 */
		public boolean isEmpty() {
			return text == null || text.isEmpty();
		}

		/**
		 * Check for equality.
		 * 
		 * @param o The object to compare.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof InfoItem) {
				InfoItem item = (InfoItem) o;
				return getId().equals(item.getId());
			}
			return false;
		}
	}

	/**
	 * A set of info items indexed by a string identifier.
	 */
	class InfoItemSet {
		/**
		 * The set of info items.
		 */
		private Set<InfoItem> set = new LinkedHashSet<>();
		/**
		 * The map to index them by identifier.
		 */
		private HashMap<String, InfoItem> map = new HashMap<>();

		/**
		 * Default constructor.
		 */
		InfoItemSet() {
			super();
		}

		/**
		 * Add an item indexed by id.
		 * 
		 * @param item Theinfo item.
		 */
		public void add(InfoItem item) {
			set.add(item);
			map.put(item.getId(), item);
		}

		/**
		 * Returns the info item with the give id or null if not exists.
		 * 
		 * @param id The id.
		 * @return The info item with the give id or null if not exists.
		 */
		public InfoItem get(String id) {
			return map.get(id);
		}

		/**
		 * Removes the info item with the given id.
		 * 
		 * @param id The id.
		 */
		public void remove(String id) {
			InfoItem item = get(id);
			if (item != null) {
				set.remove(item);
				map.remove(id);
			}
		}

		/**
		 * Clear the info item set.
		 */
		public void clear() {
			set.clear();
			map.clear();
		}

		/**
		 * Check for emptyness.
		 * 
		 * @return A boolean.
		 */
		public boolean isEmpty() {
			return set.isEmpty();
		}

		/**
		 * Returns an iterator on the info items.
		 * 
		 * @return The iterator.
		 */
		public Iterator<InfoItem> iterator() {
			return set.iterator();
		}
	}

	/**
	 * The parent chart container.
	 */
	private JChartContainer chartContainer;
	/**
	 * An information JPanel. A panel is used instead of a label to have the hability to better control fonts and colors
	 * for each part of the text.
	 */
	private JPanelInfo panelInfo;
	/**
	 * Close button.
	 */
	private JButton buttonClose;
	/**
	 * The set of info items indexes by id.
	 */
	private InfoItemSet infoItems = new InfoItemSet();

	/**
	 * Constructor assigning the parent chart container.
	 * 
	 * @param chartContainer The parent chart container.
	 */
	public JChartInfo(JChartContainer chartContainer) {
		super();
		this.chartContainer = chartContainer;
		
		// Info plot params.
		InformationPlotParameters parameters = chartContainer.getChart().getInfoPlotParameters();

		// Set the backgroud color.
		setBackground(parameters.getInfoBackgroundColor());

		// Layout
		setLayout(new GridBagLayout());

		// Layout panels.
		layoutPanels();

	}

	/**
	 * Layout panels and buttons.
	 */
	private void layoutPanels() {
		removeAll();
		// Constraints info panel.
		GridBagConstraints constraintsPanelInfo = new GridBagConstraints();
		constraintsPanelInfo.anchor = GridBagConstraints.WEST;
		constraintsPanelInfo.fill = GridBagConstraints.HORIZONTAL;
		constraintsPanelInfo.gridheight = 1;
		constraintsPanelInfo.gridwidth = 1;
		constraintsPanelInfo.weightx = 1;
		constraintsPanelInfo.weighty = 1;
		constraintsPanelInfo.gridx = 0;
		constraintsPanelInfo.gridy = 0;
		constraintsPanelInfo.insets = new Insets(0, 0, 0, 0);
		
		// Info plot params.
		InformationPlotParameters plotParameters = chartContainer.getChart().getInfoPlotParameters();

		// Calculate the size based on the desired font and insets. The graphics context needs to be that of the upper
		// level chart, because it is the only one displayed at this time.
		FontMetrics fm = SwingUtils.getFontMetrics(plotParameters.getInfoTextFont());
		int size =
			plotParameters.getInfoTextInsets().top +
				fm.getMaxAscent() +
				fm.getMaxDescent() +
				plotParameters.getInfoTextInsets().bottom;

		// Define and add the info panel.
		panelInfo = new JPanelInfo();
		panelInfo.setMinimumSize(new Dimension(0, size));
		panelInfo.setMaximumSize(new Dimension(0, size));
		panelInfo.setPreferredSize(new Dimension(0, size));
		add(panelInfo, constraintsPanelInfo);

		// Constraints close button.
		GridBagConstraints constraintsButtonClose = new GridBagConstraints();
		constraintsButtonClose.anchor = GridBagConstraints.EAST;
		constraintsPanelInfo.fill = GridBagConstraints.BOTH;
		constraintsButtonClose.gridheight = 1;
		constraintsButtonClose.gridwidth = 1;
		constraintsButtonClose.weightx = 0;
		constraintsButtonClose.weighty = 0;
		constraintsButtonClose.gridx = 1;
		constraintsButtonClose.gridy = 0;
		constraintsButtonClose.insets = new Insets(0, 0, 0, 0);

		// Defina and add the close button.
		buttonClose = new JButton(new ActionCloseButton());
		buttonClose.setMinimumSize(new Dimension(size, size));
		buttonClose.setMaximumSize(new Dimension(size, size));
		buttonClose.setPreferredSize(new Dimension(size, size));
		add(buttonClose, constraintsButtonClose);

	}

	/**
	 * Sets the panel iinfo background color.
	 * 
	 * @param color The color.
	 */
	public void setPanelInfoBackgorund(Color color) {
		panelInfo.setBackground(color);
	}

	/**
	 * Set the properties to the info item.
	 * 
	 * @param id The info item id.
	 * @param text The text.
	 * @param color The color.
	 * @param repaint A boolean that idicates if the info panel should immediatly be repainted.
	 */
	public void setInfo(String id, String text, Color color, boolean repaint) {
		setInfo(id, text, color, -1, repaint);
	}

	/**
	 * Set the properties to the info item.
	 * 
	 * @param id The info item id.
	 * @param text The text.
	 * @param color The color.
	 * @param style The font style.
	 * @param repaint A boolean that idicates if the info panel should immediatly be repainted.
	 */
	public void setInfo(String id, String text, Color color, int style, boolean repaint) {
		InfoItem infoItem = infoItems.get(id);
		if (infoItem == null) {
			infoItem = new InfoItem(id);
			infoItems.add(infoItem);
		}
		if (text != null) {
			infoItem.setText(text);
		}
		if (color != null) {
			infoItem.setColor(color);
		}
		if (style >= 0) {
			infoItem.setStyle(style);
		}
		infoItem.setActive(true);
		if (repaint) {
			panelInfo.repaint();
		}
	}

	/**
	 * Returns the info item of the given identifier.
	 * 
	 * @param id The identifier.
	 * @return The corrsponding info item.
	 */
	public InfoItem getInfo(String id) {
		return infoItems.get(id);
	}

	/**
	 * Activates the info.
	 * 
	 * @param id The info id.
	 */
	public void activateInfo(String id) {
		if (getInfo(id) != null) {
			getInfo(id).setActive(true);
		}
	}

	/**
	 * Dectivates the info.
	 * 
	 * @param id The info id.
	 */
	public void deactivateInfo(String id) {
		if (getInfo(id) != null) {
			getInfo(id).setActive(false);
		}
	}

	/**
	 * Repaint info items.
	 */
	public void repaintInfo() {
		panelInfo.repaint();
	}

	/**
	 * Remove the info item with the given id.
	 * 
	 * @param id The id.
	 */
	public void removeInfo(String id) {
		infoItems.remove(id);
	}

	/**
	 * Returns the parent container.
	 * 
	 * @return The parent container.
	 */
	public JChartContainer getChartContainer() {
		return chartContainer;
	}
}
