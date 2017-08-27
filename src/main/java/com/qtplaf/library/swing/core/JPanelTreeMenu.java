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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.event.KeyHandler;
import com.qtplaf.library.swing.event.MouseHandler;

/**
 * A panel to manage tree menu items.
 * 
 * @author Miquel Sas
 */
public class JPanelTreeMenu extends JPanel {

	/**
	 * Key adapter.
	 */
	class KeyAdapter extends KeyHandler {
		/**
		 * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a
		 * key released event.
		 */
		@Override
		public void keyReleased(KeyEvent e) {

			// Key stroke.ç
			int keyCode = e.getKeyCode();
			int modifiers = e.getModifiers();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);

			// Accelerator key.
			TreeMenuNode nodeAccKey = findNode(keyStroke);
			if (nodeAccKey != null) {
				TreePath path = getTreePath(nodeAccKey);
				getTree().setSelectionPath(path);
				getTree().expandPath(path);
				ActionEvent accEv = new ActionEvent(e.getSource(), 0, null, System.currentTimeMillis(), 0);
				nodeAccKey.getMenuItem().execute(accEv, getStatusBar());
				return;
			}

			// Enter: try expand/collapse/execute.
			if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
				if (isProcessExecute()) {
					processExecute();
				}
				return;
			}
		}
	}

	/**
	 * Mouse adapter.
	 */
	class MouseAdapter extends MouseHandler {
		/**
		 * Invoked when the mouse button has been clicked (pressed and released) on a component.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				TreePath path = getTree().getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					TreeMenuNode node = (TreeMenuNode) path.getLastPathComponent();
					if (node == getSelectedNode()) {
						execute(node);
						return;
					}
				}
			}
		}
	}

	/**
	 * The tree.
	 */
	private JTree tree;
	/**
	 * The working session.
	 */
	private Session session = null;
	/**
	 * A boolean to control if process execute should be performed. This would normally be set to false if a parent like
	 * a frame menu manages execute.
	 */
	private boolean processExecute = true;
	/**
	 * A status bar that will be passed to actions to optionally be used to monitor not very long tasks.
	 */
	private StatusBar statusBar;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public JPanelTreeMenu(Session session) {
		super();
		this.session = session;

		// Set the layout and add the main tree.
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(new JScrollPane(getTree()), constraints);

		// Set mouse and key adapters.
		MouseAdapter mouseAdapter = new MouseAdapter();
		getTree().addMouseListener(mouseAdapter);
		KeyAdapter keyAdapter = new KeyAdapter();
		getTree().addKeyListener(keyAdapter);
	}

	/**
	 * Returns a boolean indicating if execute will be processed with the normal VK_ENTER.
	 * 
	 * @return A boolean indicating if execute will be processed with the normal VK_ENTER.
	 */
	public boolean isProcessExecute() {
		return processExecute;
	}

	/**
	 * Sets a boolean indicating if execute will be processed with the normal VK_ENTER.
	 * 
	 * @param processExecute A boolean indicating if execute will be processed with the normal VK_ENTER.
	 */
	public void setProcessExecute(boolean processExecute) {
		this.processExecute = processExecute;
	}

	/**
	 * Proce execute (VK_ENTER), expanding, collapsing or executing the option.
	 */
	public void processExecute() {
		TreeMenuNode node = getSelectedNode();
		if (node != null) {
			if (node.isLeaf()) {
				execute(node);
			} else {
				TreePath path = getTreePath(node);
				if (getTree().isExpanded(path)) {
					getTree().collapsePath(path);
				} else {
					getTree().expandPath(path);
				}
			}
		}
	}

	/**
	 * Execute the tree menu item in the argument node if it is leaf and selected.
	 * 
	 * @param node The node.
	 */
	private void execute(TreeMenuNode node) {
		if (node.isLeaf()) {
			TreeMenuItem treeMenuItem = getSelectedMenuItem();
			if (treeMenuItem != null) {
				ActionEvent accEv = new ActionEvent(getTree(), 0, null, System.currentTimeMillis(), 0);
				treeMenuItem.execute(accEv, getStatusBar());
			}
		}
	}

	/**
	 * Adds a child menu item to the root node.
	 * 
	 * @param childMenuItem The menu item to add.
	 */
	public void addMenuItem(TreeMenuItem childMenuItem) {
		addMenuItem((TreeMenuItem) null, childMenuItem);
	}

	/**
	 * Add a chil menu item to the node with the argument level. If no node is found at the level, the item is added to
	 * the root.
	 * 
	 * @param level The search level.
	 * @param childMenuItem The menu item to add.
	 */
	public void addMenuItem(String level, TreeMenuItem childMenuItem) {
		TreeMenuNode parentNode = findNode(level);
		TreeMenuItem parentMenuItem = null;
		if (parentNode != null) {
			parentMenuItem = parentNode.getMenuItem();
		}
		addMenuItem(parentMenuItem, childMenuItem);
	}

	/**
	 * Adds a child menu item without triggering a refresh.
	 * 
	 * @param parentMenuItem The parent menu item.
	 * @param childMenuItem The child menu item.
	 */
	public void addMenuItem(TreeMenuItem parentMenuItem, TreeMenuItem childMenuItem) {
		addMenuItem(parentMenuItem, childMenuItem, false);
	}

	/**
	 * Adds a child menu item to the node that contains the parent menu item.
	 * 
	 * @param parentMenuItem The parent menu item.
	 * @param childMenuItem The child menu item.
	 * @param refresh A boolean that indicates if the tree node should be refreshed.
	 */
	public void addMenuItem(TreeMenuItem parentMenuItem, TreeMenuItem childMenuItem, boolean refresh) {

		// Find the parent node.
		TreeMenuNode parentNode;
		if (parentMenuItem == null) {
			parentNode = getRootNode();
			parentMenuItem = getRootMenuItem();
		} else {
			parentNode = findNode(parentMenuItem);
		}

		// Add if parent node found.
		if (parentNode != null) {

			// Check that menu item is a valid sibling.
			List<TreeMenuItem> siblings = parentMenuItem.getChildren();
			if (!siblings.isEmpty()) {
				TreeMenuItem sibling = siblings.get(0);
				// Must have the same number of labels and the same display level flag.
				if (sibling.getLabelCount() != childMenuItem.getLabelCount()
					|| sibling.isDisplayLevel() != childMenuItem.isDisplayLevel()) {
					throw new IllegalArgumentException("Menu item not valid for siblings");
				}
			}

			// Do add the child.
			TreeMenuNode childNode = new TreeMenuNode(childMenuItem);
			parentNode.add(childNode);
			if (refresh) {
				getTreeModel().nodeStructureChanged(parentNode);
			}
		}
	}

	/**
	 * Refresh the tree.
	 */
	public void refreshTree() {
		getTreeModel().nodeStructureChanged(getRootNode());
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the tree.
	 * 
	 * @return The tree.
	 */
	private JTree getTree() {
		if (tree == null) {
			TreeMenuItem rootMenuItem = new TreeMenuItem(getSession());
			TreeMenuNode rootNode = new TreeMenuNode(rootMenuItem);
			DefaultTreeModel model = new DefaultTreeModel(rootNode);
			tree = new JTree(model);
			tree.setCellRenderer(new TreeMenuItemCellRenderer(getSession()));
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);
		}
		return tree;
	}

	/**
	 * Returns the tree cell renderer to be able to configure it.
	 * 
	 * @return The tree menu cell renderer.
	 */
	public TreeMenuItemCellRenderer getTreeCellRenderer() {
		return (TreeMenuItemCellRenderer) getTree().getCellRenderer();
	}

	/**
	 * Returns the tree model.
	 * 
	 * @return The tree model.
	 */
	public DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel) getTree().getModel();
	}

	/**
	 * Returns the root node.
	 * 
	 * @return The root node.
	 */
	private TreeMenuNode getRootNode() {
		return (TreeMenuNode) getTreeModel().getRoot();
	}

	/**
	 * Returns the root menu item.
	 * 
	 * @return The root menu item.
	 */
	private TreeMenuItem getRootMenuItem() {
		return getRootNode().getMenuItem();
	}

	/**
	 * Returns the selected tree node or null.
	 * 
	 * @return The selected tree node or null.
	 */
	public TreeMenuNode getSelectedNode() {
		TreePath selectionPath = getTree().getSelectionPath();
		if (selectionPath != null) {
			return (TreeMenuNode) selectionPath.getLastPathComponent();
		}
		return null;
	}

	/**
	 * Returns the selected menu item or null.
	 * 
	 * @return The selected menu item or null.
	 */
	public TreeMenuItem getSelectedMenuItem() {
		TreeMenuNode selectedNode = getSelectedNode();
		if (selectedNode != null) {
			return selectedNode.getMenuItem();
		}
		return null;
	}

	/**
	 * Returns the node containing the menu item or null if not found.
	 * 
	 * @param menuItem The source menu item.
	 * @return The tree node or null.
	 */
	private TreeMenuNode findNode(TreeMenuItem menuItem) {
		Enumeration<TreeMenuNode> enumeration = getEnumeration();
		while (enumeration.hasMoreElements()) {
			TreeMenuNode node = enumeration.nextElement();
			if (node.getMenuItem() == menuItem) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Find a node with a menu item which level is the argument level. Levels have the form 1.4.12.21
	 * 
	 * @param level The seach level.
	 * @return The the node of the level or null.
	 */
	private TreeMenuNode findNode(String level) {
		Enumeration<TreeMenuNode> enumeration = getEnumeration();
		while (enumeration.hasMoreElements()) {
			TreeMenuNode node = enumeration.nextElement();
			if (node.getMenuItem().getLevel().equals(level)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Find a node with a menu item which level is the argument level. Levels have the form 1.4.12.21
	 * 
	 * @param keyStroke The accelerator key stroke of the node.
	 * @return The the node of the level or null.
	 */
	private TreeMenuNode findNode(KeyStroke keyStroke) {
		Enumeration<TreeMenuNode> enumeration = getEnumeration();
		while (enumeration.hasMoreElements()) {
			TreeMenuNode node = enumeration.nextElement();
			TreeMenuItem menuItem = node.getMenuItem();
			if (menuItem.getAcceleratorKey() != null && menuItem.getAcceleratorKey().equals(keyStroke)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Returns the enumeration to traverse all nodes.
	 * 
	 * @return The enumeration.
	 */
	@SuppressWarnings("unchecked")
	private Enumeration<TreeMenuNode> getEnumeration() {
		return getRootNode().breadthFirstEnumeration();
	}

	/**
	 * Returns the path to the node.
	 * 
	 * @param node The target node.
	 * @return The path.
	 */
	public TreePath getTreePath(TreeNode node) {
		List<TreeNode> path = new ArrayList<>();
		while (node != null) {
			path.add(0, node);
			node = node.getParent();
		}
		return new TreePath(path.toArray(new TreeNode[path.size()]));
	}

	/**
	 * Returns the status bar.
	 * 
	 * @return The status bar.
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * Sets the status bar.
	 * 
	 * @param statusBar The status bar.
	 */
	public void setStatusBar(StatusBar statusBar) {
		this.statusBar = statusBar;
	}
}
