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
package com.qtplaf.library.swing.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * A flow layout that wraps components when they do not fit in the row.
 * 
 * @author Miquel Sas
 */
public class WrapLayout extends FlowLayout {

	/**
	 * The previous width of the parent of this owner panel.
	 */
	private int previousPanelParentWidth = 0;
	/**
	 * The minimum and calculated dimension.
	 */
	private Dimension minimumLayoutSize = null;

	/**
	 * Default constructor.
	 */
	public WrapLayout() {
		super();
	}

	/**
	 * Constructor assigning the alignment.
	 * 
	 * @param align The alignment.
	 */
	public WrapLayout(int align) {
		super(align);
	}

	/**
	 * Constructor assigning alignment and gaps.
	 * 
	 * @param align Alignment.
	 * @param hgap Horizontal gap.
	 * @param vgap Vertical gap.
	 */
	public WrapLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	/**
	 * Returns the minimum dimensions for this layout.
	 * 
	 * @return The minimum dimensions for this layout.
	 */
	@Override
	public Dimension minimumLayoutSize(Container target) {
		return minimumLayoutSize;
	}

	/**
	 * Returns the preferred dimensions for this layout.
	 * 
	 * @return The preferred dimensions for this layout.
	 */
	@Override
	public Dimension preferredLayoutSize(Container target) {
		minimumLayoutSize = getDimension(target, true);
		return minimumLayoutSize;
	}

	/**
	 * Calculates and returns the desired dimension.
	 * 
	 * @param target This wrapper container.
	 * @param minimum A flag to calculate minimum or preferred size.
	 * @return The dimension.
	 */
	private Dimension getDimension(Container target, boolean minimum) {
		synchronized (target.getTreeLock()) {
			int currentPanelParentWidth = target.getParent().getWidth();
			int increase = currentPanelParentWidth - previousPanelParentWidth;
			previousPanelParentWidth = currentPanelParentWidth;
			int width = target.getWidth() + increase;
			Insets insets = target.getInsets();
			int count = target.getComponentCount();
			int height = 0;
			int lineHeight = 0;
			int lineWidth = 0;
			boolean first = true;
			for (int i = 0; i < count; i++) {
				Component cmp = target.getComponent(i);
				if (cmp.isVisible()) {
					Dimension dim = null;
					if (minimum) {
						dim = cmp.getMinimumSize();
					} else {
						dim = cmp.getPreferredSize();
					}
					int sum = 0;
					if (!first) {
						sum += getHgap();
					}
					sum += dim.width;
					if (width > 0 && lineWidth + sum >= width - 1) {
						height += getVgap() + lineHeight;
						lineHeight = 0;
						lineWidth = 0;
					}
					lineHeight = Math.max(lineHeight, dim.height);
					if (!first) {
						lineWidth += getHgap();
					}
					first = false;
					lineWidth += dim.width;
				}
			}
			if (lineHeight > 0) {
				height += getVgap() + lineHeight;
			}
			height += getVgap();
			height += insets.top + insets.bottom;
			if (width == 0) {
				width = lineWidth + 25;
			}
			return new Dimension(width, height);
		}
	}
}
