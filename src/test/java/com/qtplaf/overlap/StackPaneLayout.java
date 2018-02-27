/*
 * Copyright (C) 2017 Miquel Sas
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

package com.qtplaf.overlap;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.JPanel;

/**
 * The layout manager for the stack pane. It is aimed to be used to paint stock charts by layers.
 *
 * @author Miquel Sas
 */
public class StackPaneLayout implements LayoutManager2 {

	/**
	 * Constructor.
	 */
	public StackPaneLayout() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			for (int i = 0; i < parent.getComponentCount(); i++) {
				Component component = parent.getComponent(i);
				int x = insets.left;
				int y = insets.top;
				int width = parent.getWidth() - (insets.left + insets.right);
				int height = parent.getHeight() - (insets.top + insets.bottom);
				component.setBounds(x, y, width, height);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.0f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.0f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidateLayout(Container target) {
	}

}
