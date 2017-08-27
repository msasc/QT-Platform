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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Extends the <tt>DefaultMutableTreeNode</tt> to give additional support for menu actions.
 *
 * @author Miquel Sas
 */
public class TreeMenuNode extends DefaultMutableTreeNode {

	/**
	 * Constructor assigning the user object that is a tree menu item.
	 * 
	 * @param treeMenuItem The tree menu item user object.
	 */
	public TreeMenuNode(TreeMenuItem treeMenuItem) {
		super(treeMenuItem);
		treeMenuItem.setNode(this);
	}

	/**
	 * Returns the tree menu item stored in the user object.
	 * 
	 * @return The menu item.
	 */
	public TreeMenuItem getMenuItem() {
		return (TreeMenuItem) getUserObject();
	}
}
