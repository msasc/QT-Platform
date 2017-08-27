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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.qtplaf.library.app.Session;

/**
 * Default tree cell renderer for the tree menu.
 * 
 * @author Miquel Sas
 */
public class TreeMenuItemCellRenderer implements TreeCellRenderer {

	/** Default tree font */
	private Font treeFont = UIManager.getFont("Tree.font");

	/** Icon used to show non-leaf nodes that aren't expanded. */
	private Icon closedIcon = UIManager.getIcon("Tree.closedIcon");
	/** Icon used to show non-leaf nodes that are expanded. */
	private Icon openIcon = UIManager.getIcon("Tree.openIcon");
	/** Icon used to show leaf nodes. */
	private Icon leafIcon = UIManager.getIcon("Tree.leafIcon");

	/** Default color to use for the foreground for selected nodes. */
	private Color selectionForegroundColor = UIManager.getColor("Tree.selectionForeground");
	/** Default color to use for the foreground for non-selected nodes. */
//	private Color textForegroundColor = UIManager.getColor("Tree.textForeground");
	private Color textForegroundColor = new Color(57,105,138);
	/** Default color to use for the background when a node is selected. */
	private Color selectionBackgroundColor = UIManager.getColor("Tree.selectionBackground");
	/** Default color to use for the background when the node isn't selected. */
	private Color textBackgroundColor = UIManager.getColor("Tree.textBackground");
	/** Default color to use for the selection border. */
	private Color borderColor = UIManager.getColor("Tree.dropLineColor");

	/** The label for the icon. */
	private JLabel labelIcon;
	/** The label for the level. */
	private JLabel labelLevel;
	/** The list of labels for the list of labels of the tree menu item. */
	private List<JLabel> labels;
	/** The label for the accelerator key. */
	private JLabel labelAcceleratorKey;
	/** The render panel. */
	private JPanel panelRender;

	/** The working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public TreeMenuItemCellRenderer(Session session) {
		super();
		this.session = session;
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
	 * Returns the default tree font.
	 * 
	 * @return The tree font.
	 */
	public Font getTreeFont() {
		return treeFont;
	}

	/**
	 * Sets the default tree font.
	 * 
	 * @param treeFont The default tree font.
	 */
	public void setTreeFont(Font treeFont) {
		this.treeFont = treeFont;
	}

	/**
	 * Returns the closed icon.
	 * 
	 * @return The closed icon.
	 */
	public Icon getClosedIcon() {
		return closedIcon;
	}

	/**
	 * Sets the closed icon.
	 * 
	 * @param closedIcon The closed icon.
	 */
	public void setClosedIcon(Icon closedIcon) {
		this.closedIcon = closedIcon;
	}

	/**
	 * Returns the open icon.
	 * 
	 * @return The open icon.
	 */
	public Icon getOpenIcon() {
		return openIcon;
	}

	/**
	 * Sets the open icon.
	 * 
	 * @param openIcon The open icon.
	 */
	public void setOpenIcon(Icon openIcon) {
		this.openIcon = openIcon;
	}

	/**
	 * Returns the leaf icon.
	 * 
	 * @return The leaf icon.
	 */
	public Icon getLeafIcon() {
		return leafIcon;
	}

	/**
	 * Sets the leaf icon.
	 * 
	 * @param leafIcon The leaf icon.
	 */
	public void setLeafIcon(Icon leafIcon) {
		this.leafIcon = leafIcon;
	}

	/**
	 * Returns the selection foreground color.
	 * 
	 * @return The selection foreground color.
	 */
	public Color getSelectionForegroundColor() {
		return selectionForegroundColor;
	}

	/**
	 * Sets the selection foreground color.
	 * 
	 * @param selectionForegroundColor The selection foreground color.
	 */
	public void setSelectionForegroundColor(Color selectionForegroundColor) {
		this.selectionForegroundColor = selectionForegroundColor;
	}

	/**
	 * Returns the normal text foregroung color.
	 * 
	 * @return The normal text foregroung color.
	 */
	public Color getTextForegroundColor() {
		return textForegroundColor;
	}

	/**
	 * Sets the normal text foregroung color.
	 * 
	 * @param textForegroundColor The normal text foregroung color.
	 */
	public void setTextForegroundColor(Color textForegroundColor) {
		this.textForegroundColor = textForegroundColor;
	}

	/**
	 * Returns the selection background color.
	 * 
	 * @return The normal text foregroung color.
	 */
	public Color getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}

	/**
	 * Sets the normal text foregroung color.
	 * 
	 * @param selectionBackgroundColor The normal text foregroung color.
	 */
	public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
		this.selectionBackgroundColor = selectionBackgroundColor;
	}

	/**
	 * Returns the normal text background color.
	 * 
	 * @return The normal text background color.
	 */
	public Color getTextBackgroundColor() {
		return textBackgroundColor;
	}

	/**
	 * Sets the normal text background color.
	 * 
	 * @param textBackgroundColor The normal text background color.
	 */
	public void setTextBackgroundColor(Color textBackgroundColor) {
		this.textBackgroundColor = textBackgroundColor;
	}

	/**
	 * Returns the border color for the selected row.
	 * 
	 * @return The border color for the selected row.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the border color for the selected row.
	 * 
	 * @param borderColor The border color for the selected row.
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Returns the label for the icon.
	 * 
	 * @return The icon label.
	 */
	private JLabel getLabelIcon() {
		if (labelIcon == null) {
			labelIcon = new JLabel();
			labelIcon.setBackground(getTextBackgroundColor());
		}
		return labelIcon;
	}

	/**
	 * Returns the level label.
	 * 
	 * @return The level label.
	 */
	private JLabel getLabelLevel() {
		if (labelLevel == null) {
			labelLevel = new JLabel();
			labelLevel.setFont(getTreeFont());
			labelLevel.setBackground(getTextBackgroundColor());
		}
		return labelLevel;
	}

	/**
	 * Returns the accelerator key label.
	 * 
	 * @return The accelerator key label.
	 */
	private JLabel getLabelAcceleratorKey() {
		if (labelAcceleratorKey == null) {
			labelAcceleratorKey = new JLabel();
			labelAcceleratorKey.setFont(getTreeFont());
			labelAcceleratorKey.setForeground(Color.GRAY);
			labelAcceleratorKey.setBackground(getTextBackgroundColor());
		}
		return labelAcceleratorKey;
	}

	/**
	 * Returns the render panel.
	 * 
	 * @return The render panel.
	 */
	private JPanel getPanelRender() {
		if (panelRender == null) {
			panelRender = new JPanel(new GridBagLayout());
			panelRender.setBackground(getTextBackgroundColor());
		}
		return panelRender;
	}

	/**
	 * Returns the list of labels for the list of labels of the rendered tree menu item.
	 * 
	 * @return The list of labels.
	 */
	private List<JLabel> getLabels() {
		if (labels == null) {
			labels = new ArrayList<>();
		}
		return labels;
	}

	/**
	 * Returns the constraints to add items to the render panel.
	 * 
	 * @param gridx Grid x position.
	 * @param leftMargin Left margin to separate from the previous item.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraints(int gridx, int leftMargin) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(0, leftMargin, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridx = gridx;
		constraints.gridy = 0;
		return constraints;
	}

	/**
	 * Calculate the widths of the level, the labels and the accelerator key.
	 * 
	 * @param menuItem The source menu item.
	 */
	private void calculateWidths(TreeMenuItem menuItem) {
		// Use a font bold and font metrics.
		Font font = new Font(getTreeFont().getName(), Font.BOLD, getTreeFont().getSize());
		FontMetrics fm = (new JLabel()).getFontMetrics(font);

		// Inititalize the array of widths and set a margin.
		int labelCount = menuItem.getLabelCount();
		int[] widths = new int[labelCount];
		int widthLevel = 0;
		int widthAcceleratorKey = 0;
		int margin = 10;

		// Iterate through siblings.
		List<TreeMenuItem> siblings = menuItem.getParent().getChildren();
		for (TreeMenuItem sibling : siblings) {
			// Level.
			String level = sibling.getLevel();
			widthLevel = Math.max(widthLevel, fm.stringWidth(level) + margin);
			// Labels.
			for (int i = 0; i < labelCount; i++) {
				String label = sibling.getLabel(i);
				widths[i] = Math.max(widths[i], fm.stringWidth(label) + margin);
			}
			// Accelerator key.
			if (sibling.getAcceleratorKey() != null) {
				String accKey = SwingUtils.translate(sibling.getAcceleratorKey(), getSession().getLocale());
				widthAcceleratorKey = Math.max(widthAcceleratorKey, fm.stringWidth(accKey) + margin);
			}
		}

		// Base size.
		Dimension size = SwingUtils.getLabelPreferredSize(new JLabel());

		// Level size.
		Dimension sizeLevel = new Dimension(widthLevel, size.height);
		getLabelLevel().setPreferredSize(sizeLevel);
		getLabelLevel().setMinimumSize(sizeLevel);

		// Labels.
		getLabels().clear();
		for (int i = 0; i < labelCount; i++) {
			Dimension sizeLabel = new Dimension(widths[i], size.height);
			JLabel label = new JLabel();
			label.setFont(getTreeFont());
			label.setBackground(getTextBackgroundColor());
			label.setOpaque(true);
			label.setPreferredSize(sizeLabel);
			label.setMinimumSize(sizeLabel);
			getLabels().add(label);
		}

		// Accelerator key.
		Dimension sizeAcceleratorKey = new Dimension(widthAcceleratorKey, size.height);
		getLabelAcceleratorKey().setPreferredSize(sizeAcceleratorKey);
		getLabelAcceleratorKey().setMinimumSize(sizeAcceleratorKey);
	}

	/**
	 * Return the component to render the tree row.
	 * 
	 * @param tree The <code>JTree</code>.
	 * @param value The value, here a <code>DefaultMutableTreeNode</code> containing a <code>TreeMenuItem</code>.
	 * @param selected A boolean that indicates if the row is selected.
	 * @param expanded A boolean that indicates if the row is expanded.
	 * @param leaf A boolean that indicates if the row is a leaf row.
	 * @param row The in the path.
	 * @param hasFocus A boolean that indicates if the row has focus.
	 * @return The render component.
	 */
	@Override
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean selected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {

		// Node and menu item.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		TreeMenuItem menuItem = (TreeMenuItem) node.getUserObject();

		// Special tree initialization state.
		if (node.isRoot()) {
			getPanelRender().removeAll();
			return getPanelRender();
		}

		// Calculate widths.
		calculateWidths(menuItem);

		// Add panels to the render panel.
		getPanelRender().removeAll();
		int gridx = 0;
		// Icon.
		getPanelRender().add(getLabelIcon(), getConstraints(gridx++, 0));
		// Level.
		getPanelRender().add(getLabelLevel(), getConstraints(gridx++, 5));
		// Labels.
		for (JLabel label : getLabels()) {
			getPanelRender().add(label, getConstraints(gridx++, 5));
		}
		// Accelerator key.
		getPanelRender().add(getLabelAcceleratorKey(), getConstraints(gridx++, 5));

		// Leaf/not-leaf attributes
		if (leaf) {
			if (menuItem.getLeafIcon() != null) {
				getLabelIcon().setIcon(menuItem.getLeafIcon());
			} else {
				getLabelIcon().setIcon(getLeafIcon());
			}
			for (JLabel label : getLabels()) {
				label.setFont(getTreeFont());
			}
		} else {
			if (expanded) {
				if (menuItem.getOpenIcon() != null) {
					getLabelIcon().setIcon(menuItem.getOpenIcon());
				} else {
					getLabelIcon().setIcon(getOpenIcon());
				}
			} else {
				if (menuItem.getClosedIcon() != null) {
					getLabelIcon().setIcon(menuItem.getClosedIcon());
				} else {
					getLabelIcon().setIcon(getClosedIcon());
				}
			}
			for (JLabel label : getLabels()) {
				label.setFont(new Font(getTreeFont().getName(), Font.BOLD, getTreeFont().getSize()));
			}
		}

		// Set texts.
		getLabelLevel().setText(menuItem.getLevel());
		for (int i = 0; i < getLabels().size(); i++) {
			getLabels().get(i).setText(menuItem.getLabel(i));
		}
		KeyStroke accKey = menuItem.getAcceleratorKey();
		if (accKey != null) {
			getLabelAcceleratorKey().setText(SwingUtils.translate(accKey, getSession().getLocale()));
			getLabelAcceleratorKey().setVisible(true);
		} else {
			getLabelAcceleratorKey().setVisible(false);
		}

		// Selected/not-selected attributes. The level is not affected by selection.
		if (selected) {
			for (JLabel label : getLabels()) {
				label.setBackground(getSelectionBackgroundColor());
				label.setForeground(getSelectionForegroundColor());
				label.setBorder(new LineBorder(getBorderColor(), 1));
			}
			getLabelAcceleratorKey().setBorder(new LineBorder(getBorderColor(), 1));
		} else {
			for (JLabel label : getLabels()) {
				label.setBackground(getTextBackgroundColor());
				label.setForeground(getTextForegroundColor());
				label.setBorder(new EmptyBorder(1,1,1,1));
			}
			getLabelAcceleratorKey().setBorder(new LineBorder(getBorderColor(), 1));
		}

		// Display level if required.
		if (menuItem.isDisplayLevel()) {
			getLabelLevel().setVisible(true);
		} else {
			getLabelLevel().setVisible(false);
		}

		// Return the render panel as the render component.
		return getPanelRender();
	}

}
