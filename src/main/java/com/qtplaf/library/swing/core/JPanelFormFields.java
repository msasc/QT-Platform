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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.qtplaf.library.app.AccessMode;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldGroup;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Relation;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.action.ActionLookup;
import com.qtplaf.library.swing.action.ActionSearchAndRefreshDB;
import com.qtplaf.library.swing.event.ComponentHandler;

/**
 * A panel that holds a grid form of fields from a record. Domains are layed out in tabs by field group if there are
 * field groups, and optionally in sub-panels defined by (gridx, gridy) values.
 * <p>
 * Usage:
 * <ul>
 * <li>Instantiate the panel.</li>
 * <li>Set the edit mode.</li>
 * <li>Set the master record.</li>
 * <li>Add fields optionally indicating the grid coordinates of the sub-panel.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class JPanelFormFields extends JPanel {

	/**
	 * The component event adapter to handle focus.
	 */
	class ComponentAdapter extends ComponentHandler {
		@Override
		public void componentShown(ComponentEvent e) {
			List<Component> components = SwingUtils.getAllComponents(JPanelFormFields.this);
			for (Component component : components) {
				if (component.isShowing() && component.isFocusable() && component.isEnabled()) {
					component.requestFocus();
				}
			}
		}
	}

	/**
	 * Grid item structure.
	 */
	class GridItem {
		int gridx;
		int gridy;
		List<Field> fields = new ArrayList<>();

		GridItem(int gridx, int gridy) {
			this.gridx = gridx;
			this.gridy = gridy;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof GridItem) {
				GridItem gridItem = (GridItem) o;
				return (gridx == gridItem.gridx && gridy == gridItem.gridy);
			}
			return false;
		}
	}

	/**
	 * Group item structure.
	 */
	class GroupItem implements Comparable<GroupItem> {
		FieldGroup fieldGroup;
		List<GridItem> gridItems = new ArrayList<>();

		GroupItem(FieldGroup fieldGroup) {
			this.fieldGroup = fieldGroup;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof GroupItem) {
				GroupItem groupItem = (GroupItem) o;
				return fieldGroup.equals(groupItem.fieldGroup);
			}
			return false;
		}

		@Override
		public int compareTo(GroupItem groupItem) {
			return fieldGroup.compareTo(groupItem.fieldGroup);
		}
	}

	/**
	 * The working session.
	 */
	private Session session;
	/**
	 * Edit mode.
	 */
	private EditMode editMode = EditMode.NoRestriction;
	/**
	 * The record.
	 */
	private Record record;
	/**
	 * The list of group items.
	 */
	private List<GroupItem> groupItems = new ArrayList<>();
	/**
	 * The default border for grid items.
	 */
	private Border gridItemBorder = new EtchedBorder();
	/**
	 * A boolean that indicates if all labels in a column should have the same width.
	 */
	private boolean sameWidthForColumLabels = true;
	/**
	 * A boolean that indicates if main group panels should be scrolled, by default not.
	 */
	private boolean scrollGroupPanels = false;
	/**
	 * A boolean that indicates whether the layout has been made. After the layout, no more field can be added.
	 */
	private boolean layoutDone = false;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public JPanelFormFields(Session session) {
		super();
		this.session = session;
		addComponentListener(new ComponentAdapter());
	}

	/**
	 * Returns the record.
	 * 
	 * @return The record.
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * Sets the record.
	 * 
	 * @param record The record.
	 */
	public void setRecord(Record record) {
		this.record = Record.copyDataAndFields(record);
	}

	/**
	 * Add a field to the default (0, 0) sub-panel.
	 * 
	 * @param alias The field alias.
	 */
	public void addField(String alias) {
		addField(alias, 0, 0);
	}

	/**
	 * Add a field setting its grid coordinates. Grid coordinates are relative to the field group of the field, that is,
	 * the tab where the field group will be assigned. Grid coordinates are recomended to be set sequentially.
	 * 
	 * @param alias The field alias.
	 * @param gridx The grid x coordinate.
	 * @param gridy The grid y coordinate.
	 */
	public void addField(String alias, int gridx, int gridy) {
		checkRecord();
		if (layoutDone) {
			throw new IllegalStateException("No fields can be added after the layout is done.");
		}
		if (gridx < 0) {
			throw new IllegalArgumentException("Invalid grid x coordinate.");
		}
		if (gridy < 0) {
			throw new IllegalArgumentException("Invalid grid y coordinate.");
		}

		// The field group.
		Field field = getRecord().getField(alias);
		FieldGroup fieldGroup = field.getFieldGroup();

		// The group item of the field group.
		GroupItem groupItem = getGroupItem(fieldGroup);
		if (groupItem == null) {
			groupItem = new GroupItem(fieldGroup);
			groupItems.add(groupItem);
		}

		// The grid item of the coordinates.
		GridItem gridItem = getGridItem(groupItem, gridx, gridy);
		if (gridItem == null) {
			gridItem = new GridItem(gridx, gridy);
			groupItem.gridItems.add(gridItem);
		}

		// Add a copy of the field.
		gridItem.fields.add(new Field(field));

		// Sort the list of group items.
		groupItems.sort(null);
	}

	/**
	 * Returns the group item of the argument field group.
	 * 
	 * @param fieldGroup The field group.
	 * @return The group item or null.
	 */
	private GroupItem getGroupItem(FieldGroup fieldGroup) {
		for (GroupItem groupItem : groupItems) {
			if (groupItem.fieldGroup.equals(fieldGroup)) {
				return groupItem;
			}
		}
		return null;
	}

	/**
	 * Returns the grid item in the group item or null.
	 * 
	 * @param groupItem The grid item.
	 * @param gridx Grid x coordinate.
	 * @param gridy Grid y coordinate.
	 * @return The grid item in the group item or null.
	 */
	private GridItem getGridItem(GroupItem groupItem, int gridx, int gridy) {
		for (GridItem gridItem : groupItem.gridItems) {
			if (gridItem.gridx == gridx && gridItem.gridy == gridy) {
				return gridItem;
			}
		}
		return null;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the edit mode.
	 * 
	 * @return The edit mode.
	 */
	public EditMode getEditMode() {
		return editMode;
	}

	/**
	 * Sets the edit mode.
	 * 
	 * @param editMode The edit mode.
	 */
	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}

	/**
	 * Returns the border for grid items.
	 * 
	 * @return The border for grid items.
	 */
	public Border getGridItemBorder() {
		return gridItemBorder;
	}

	/**
	 * Sets the border for grid items.
	 * 
	 * @param gridItemBorder The border for grid items.
	 */
	public void setGridItemBorder(Border gridItemBorder) {
		this.gridItemBorder = gridItemBorder;
	}

	/**
	 * Returns a boolean that indicates if all labels in a column should have the same width.
	 * 
	 * @return A boolean that indicates if all labels in a column should have the same width.
	 */
	public boolean isSameWidthForColumLabels() {
		return sameWidthForColumLabels;
	}

	/**
	 * Sets a boolean that indicates if all labels in a column should have the same width.
	 * 
	 * @param sameWidthForColumLabels A boolean that indicates if all labels in a column should have the same width.
	 */
	public void setSameWidthForColumLabels(boolean sameWidthForColumLabels) {
		this.sameWidthForColumLabels = sameWidthForColumLabels;
	}

	/**
	 * Check if group panels should be scrolled.
	 * 
	 * @return A boolean.
	 */
	public boolean isScrollGroupPanels() {
		return scrollGroupPanels;
	}

	/**
	 * Set if group panels should be scrolled.
	 * 
	 * @param scrollGroupPanels A boolean.
	 */
	public void setScrollGroupPanels(boolean scrollGroupPanels) {
		this.scrollGroupPanels = scrollGroupPanels;
	}

	/**
	 * Auto-layout fields, attending at the group and grid definition.
	 */
	public void layoutFields() {

		// Check layout done.
		if (layoutDone) {
			return;
		}

		// Set the layout.
		setLayout(new GridBagLayout());

		// Get the list of field groups.
		List<FieldGroup> fieldGroups = getFieldGroups();

		// List of components (JPanel or JScrollPane) that will be further added.
		List<JComponent> groupComponents = new ArrayList<>();

		for (FieldGroup fieldGroup : fieldGroups) {
			JPanel panelGroup = getGroupItemPanel(getGroupItem(fieldGroup));
			if (isScrollGroupPanels()) {
				JScrollPane scrollPane =
					new JScrollPane(
						panelGroup,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				groupComponents.add(scrollPane);
			} else {
				groupComponents.add(panelGroup);
			}
		}

		// If we have only one field-group used in this panel, we will add a single panel to this form panel, with all
		// the fields of the field-group conveniently gridded. Otherwise, we will add a tabbed pane with a tab for every
		// field-group. In any case, a single field-group will provide a single gridded panel.
		if (fieldGroups.size() == 1) {
			add(groupComponents.get(0), getConstraintsPanelGroup());
		} else {
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
			for (int i = 0; i < groupComponents.size(); i++) {
				FieldGroup fieldGroup = fieldGroups.get(i);
				JComponent component = groupComponents.get(i);
				String title = fieldGroup.getDisplayTitle();
				String toolTip = fieldGroup.getDisplayDescription();
				tabbedPane.addTab(title, null, component, toolTip);
			}
			add(tabbedPane, getConstraintsPanelGroup());
		}

		// Layout if done.
		layoutDone = true;
		
		// Last, apply edit mode.
		applyEditMode();
	}

	/**
	 * Apply the current edit mode to edit fie�ds.
	 */
	private void applyEditMode() {
		List<EditField> editFields = getEditFields();
		for (EditField editField : editFields) {
			applyEditMode(editField);
		}
	}

	/**
	 * Apply the edit mode to a given edit field.
	 * 
	 * @param editField The edit field.
	 */
	private void applyEditMode(EditField editField) {
		EditContext editContext = editField.getEditContext();
		Field field = editContext.getField();
		if (!field.isEditable() || field.isVirtual()) {
			editField.setEnabled(false);
			return;
		}
		switch (editMode) {
		case Insert:
		case Filter:
		case NoRestriction:
			editField.setEnabled(true);
			break;
		case Update:
			editField.setEnabled(!field.isPrimaryKey());
			break;
		case Delete:
		case ReadOnly:
			editField.setEnabled(false);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the group item panel.
	 * 
	 * @param groupItem The group item.
	 * @return The panel.
	 */
	private JPanel getGroupItemPanel(GroupItem groupItem) {

		JPanel panelGroup = new JPanel();
		panelGroup.setLayout(new GridBagLayout());

		int columns = getGroupItemColumns(groupItem);

		boolean anyColumnWithNonFixedWidthFields = false;
		for (int column = 0; column < columns; column++) {
			int rows = getGroupItemRows(groupItem, column);
			for (int row = 0; row < rows; row++) {
				GridItem gridItem = getGridItem(groupItem, column, row);
				if (gridItem == null) {
					continue;
				}
				if (anyNonFixedWidthField(gridItem)) {
					anyColumnWithNonFixedWidthFields = true;
					break;
				}
			}
			if (anyColumnWithNonFixedWidthFields) {
				break;
			}
		}

		for (int column = 0; column < columns; column++) {
			JPanel panelColumn = new JPanel();
			panelColumn.setLayout(new GridBagLayout());
			int rows = getGroupItemRows(groupItem, column);
			boolean anyNonFixedWidthField = false;
			for (int row = 0; row < rows; row++) {
				GridItem gridItem = getGridItem(groupItem, column, row);
				if (gridItem == null) {
					continue;
				}
				JPanel panelRow = getGridItemPanel(gridItem);
				int top = (row == 0 ? 0 : 1);
				int left = (column == 0 ? 3 : 1);
				int bottom = 0;
				int right = (column == columns - 1 ? 2 : 0);
				Insets insets = new Insets(top, left, bottom, right);
				panelColumn.add(panelRow, getConstraintsPanelRow(row, insets));
				if (anyNonFixedWidthField(gridItem)) {
					anyNonFixedWidthField = true;
				}
			}

			if (isSameWidthForColumLabels()) {
				setSameWidthForColumnLabels(panelColumn);
			}

			boolean notFixedWidth = (anyColumnWithNonFixedWidthFields ? anyNonFixedWidthField : true);
			panelGroup.add(panelColumn, getConstraintsPanelColumn(column, notFixedWidth));
		}

		return panelGroup;
	}

	/**
	 * Set the same width (preferred size) for label in the column panel.
	 * 
	 * @param panelColumn The column panel.
	 */
	private void setSameWidthForColumnLabels(JPanel panelColumn) {
		List<Component> components = SwingUtils.getAllComponents(panelColumn);
		Dimension size = null;
		for (Component component : components) {
			if (component instanceof JLabelField) {
				JLabelField label = (JLabelField) component;
				Dimension preferredSize = label.getPreferredSize();
				if (size == null) {
					size = preferredSize;
				} else {
					if (preferredSize.width > size.width) {
						size = preferredSize;
					}
				}
			}
		}
		if (size != null) {
			for (Component component : components) {
				if (component instanceof JLabelField) {
					JLabelField label = (JLabelField) component;
					label.setPreferredSize(size);
					label.setMinimumSize(size);
					label.setMaximumSize(size);
				}
			}
		}
	}

	/**
	 * Check if there is any non fixed width field in the grid item.
	 * 
	 * @param gridItem The grid item.
	 * @return A boolean.
	 */
	private boolean anyNonFixedWidthField(GridItem gridItem) {
		for (Field field : gridItem.fields) {
			if (!field.isFixedWidth()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of rows in a group item.
	 * 
	 * @param groupItem The group item.
	 * @param column The column.
	 * @return The number of rows.
	 */
	private int getGroupItemRows(GroupItem groupItem, int column) {
		int rows = 0;
		List<GridItem> gridItems = getGridItems(groupItem, column);
		for (GridItem gridItem : gridItems) {
			rows = Math.max(rows, gridItem.gridy);
		}
		return rows + 1;
	}

	/**
	 * Returns the number of columns in a group item.
	 * 
	 * @param groupItem The group item.
	 * @return The number of columns.
	 */
	private int getGroupItemColumns(GroupItem groupItem) {
		int columns = 0;
		for (GridItem gridItem : groupItem.gridItems) {
			columns = Math.max(columns, gridItem.gridx);
		}
		return columns + 1;
	}

	/**
	 * Returns the list of grid items from a group item, to locate in the argument column.
	 * 
	 * @param groupItem The group item.
	 * @param column The column.
	 * @return The list of grid items.
	 */
	private List<GridItem> getGridItems(GroupItem groupItem, int column) {
		List<GridItem> columnGridItems = new ArrayList<>();
		for (GridItem gridItem : groupItem.gridItems) {
			if (gridItem.gridx == column) {
				columnGridItems.add(gridItem);
			}
		}
		return columnGridItems;
	}

	/**
	 * Returns the list of defined/used field groups.
	 * 
	 * @return The list of defined/used field groups.
	 */
	private List<FieldGroup> getFieldGroups() {
		List<FieldGroup> fieldGroups = new ArrayList<>();
		for (GroupItem groupItem : groupItems) {
			FieldGroup fieldGroup = groupItem.fieldGroup;
			if (!fieldGroups.contains(fieldGroup)) {
				fieldGroups.add(fieldGroup);
			}
		}
		return fieldGroups;
	}

	// public List<Field>

	/**
	 * Returns the list of accessible or layout fields from an origin list of fields.
	 * 
	 * @param fields The source list of fields.
	 * @return The list of accessible fields.
	 */
	private List<Field> getLayoutFields(List<Field> fields) {
		List<Field> accessibleFields = new ArrayList<>();
		for (Field field : fields) {
			// Check if access to the current field is denied and if so skip it. By default fields with access mode not
			// defined are accepted.
			AccessMode accessMode = getSession().getAccessMode(field.getNameSecurity());
			if (accessMode.equals(AccessMode.Denied)) {
				continue;
			}
			accessibleFields.add(field);
		}
		return accessibleFields;
	}

	/**
	 * Check if all the local fields of the relation are present in the list of fields.
	 * 
	 * @param relation The relation.
	 * @param fields The source list of fiels.
	 * @return A boolean.
	 */
	private boolean allLocalFieldsIncluded(Relation relation, List<Field> fields) {
		for (Relation.Segment segment : relation) {
			if (!fields.contains(segment.getLocalField())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the field scanned is the last field of the relation.
	 * 
	 * @param field The scanned field.
	 * @param relation The relation.
	 * @return A boolean.
	 */
	private boolean isTheLastField(Field field, Relation relation) {
		if (!relation.get(relation.size() - 1).getLocalField().equals(field)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the relation where all the local fields are present in the list of fields, and the field scanned is the
	 * last field of the relation.
	 * 
	 * @param field The scanned field.
	 * @param fields The source list of fiels.
	 * @return The required relation or null.
	 */
	private Relation getRelation(Field field, List<Field> fields) {
		List<Relation> relations = field.getRelations();
		for (Relation relation : relations) {
			if (allLocalFieldsIncluded(relation, fields)) {
				if (isTheLastField(field, relation)) {
					return relation;
				}
			}
		}
		return null;
	}

	/**
	 * Fill the lists of local and foreign key fields and foreign refresh fields.
	 * 
	 * @param fields The master list of fields.
	 * @param relation The relation.
	 * @param localKeyFields List of local key fields to fill.
	 * @param foreignKeyFields List of foreign key fields to fill.
	 * @param foreignRefreshFields List of foreign refresh fields to fill.
	 */
	private void fillKeyAndRefreshFields(
		List<Field> fields,
		Relation relation,
		List<Field> localKeyFields,
		List<Field> foreignKeyFields,
		List<Field> foreignRefreshFields) {

		// Local and foreign key fields.
		for (Relation.Segment segment : relation) {
			localKeyFields.add(segment.getLocalField());
			foreignKeyFields.add(segment.getForeignField());
		}
		// Temporary list of refresh fields: those foreign not in the key.
		List<Field> foreignRefreshFieldsTmp = new ArrayList<>();
		for (Field field : fields) {
			AccessMode accessMode = getSession().getAccessMode(field.getNameSecurity());
			if (accessMode.equals(AccessMode.Denied)) {
				continue;
			}
			if (field.getParentTable().equals(relation.getForeignTable())) {
				if (!foreignKeyFields.contains(field)) {
					foreignRefreshFieldsTmp.add(field);
				}
			}
		}
		// Add first the main descriptions.
		for (Field field : foreignRefreshFieldsTmp) {
			if (field.isMainDescription()) {
				if (!foreignRefreshFields.contains(field)) {
					foreignRefreshFields.add(field);
				}
			}
		}
		// Second the lookup fields.
		for (Field field : foreignRefreshFieldsTmp) {
			if (field.isLookup()) {
				if (!foreignRefreshFields.contains(field)) {
					foreignRefreshFields.add(field);
				}
			}
		}
		// Then the rest.
		for (Field field : foreignRefreshFieldsTmp) {
			if (!foreignRefreshFields.contains(field)) {
				foreignRefreshFields.add(field);
			}
		}
	}

	/**
	 * Returns the list of clear edit field names. If the field is a segment of another multi-segment relation, add
	 * clear fields for the rest of the multi-segment fields.
	 * 
	 * @param field Scan field.
	 * @param fields List of fields.
	 * @param skipRelation Relation to skip.
	 * @return The list of field names.
	 */
	private List<String> getClearEditFieldNames(Field field, List<Field> fields, Relation skipRelation) {

		// All present relations.
		List<Relation> relations = Field.getRelations(fields);

		// The list of clear editfields.
		List<String> clearEditFieldNames = new ArrayList<>();

		// Sacan relations.
		for (Relation relation : relations) {

			// Skip the relation used to build the search and refresh action.
			if (relation.equals(skipRelation)) {
				continue;
			}

			// If all local fields of the relation are not included in the list of fields, skip it.
			if (!allLocalFieldsIncluded(relation, fields)) {
				continue;
			}

			// If the relation does not contains the field as a local field, skip it.
			if (!relation.containsLocalField(field)) {
				continue;
			}

			// Get the field position in the list of local fields.
			int index = relation.getLocalFieldIndex(field);

			// Add the rest of local fields, if not already added.
			for (int i = index + 1; i < relation.size(); i++) {
				Field clearField = relation.get(i).getLocalField();
				String clearEditFieldName = EditContext.getEditFieldName(clearField);
				if (!clearEditFieldNames.contains(clearEditFieldName)) {
					clearEditFieldNames.add(clearEditFieldName);
				}
			}
		}

		return clearEditFieldNames;
	}

	/**
	 * Returns the panel that corresponds to a grid item, with its fields vertically laid out.
	 * 
	 * @param gridItem The grid item.
	 * @return The panel.
	 */
	private JPanel getGridItemPanel(GridItem gridItem) {
		JPanel panel = new JPanel(new GridBagLayout());
		if (getGridItemBorder() != null) {
			panel.setBorder(getGridItemBorder());
		}

		// Row number.
		int row = -1;

		// List of fields to layout.
		List<Field> fields = getLayoutFields(gridItem.fields);
		
		// List of already laid out fields as foreign refresh fields.
		List<Field> usedForeignRefreshFields = new ArrayList<>();
		
		for (Field field : fields) {
			
			// Skip used foreign refresh fields.
			if (usedForeignRefreshFields.contains(field)) {
				continue;
			}

			// The row number.
			row++;

			// Scan the relations to see if all the local fields are present in the list of fields, and the field
			// scanned is the last field of he relation, in which case we should build a lookup. If the field has not a
			// parent view but has a parent table, scan the relations created from the foreign keys.
			Relation relation = getRelation(field, fields);

			// Local and foreign key fields and refresh fields. Foreign refresh fields are all the fields from the
			// foreign table that are not key fields, adding first those that are main description, second those that
			// are lookup, and then the rest.
			List<Field> localKeyFields = new ArrayList<>();
			List<Field> foreignKeyFields = new ArrayList<>();
			List<Field> foreignRefreshFields = new ArrayList<>();

			// If the relation is not null and is a lookup relation...
			if (relation != null && relation.getType().equals(Relation.Type.Lookup)) {
				fillKeyAndRefreshFields(fields, relation, localKeyFields, foreignKeyFields, foreignRefreshFields);
			}
			
			// Register used foreign refresh fields.
			usedForeignRefreshFields.addAll(foreignRefreshFields);

			// Define the edit context.
			EditContext editContext = new EditContext(getSession());
			editContext.setRecord(record);
			editContext.setAlias(field.getAlias());
			if (editMode.equals(EditMode.Filter) || editMode.equals(EditMode.NoRestriction)) {
				editContext.setRequired(false);
			} else {
				editContext.setRequired(field.isRequired());
			}

			// The search record belongs to the foreign table.
			Record searchRecord = null;
			if (relation != null) {
				searchRecord = relation.getForeignTable().getDefaultRecord();
			}

			// Check lookup action with local and foreign key fields and perhaps refresh fields).
			if (!localKeyFields.isEmpty()) {
				ActionLookup actionLookup = new ActionLookup();
				actionLookup.configure(searchRecord, localKeyFields, foreignKeyFields);
				editContext.setActionLookup(actionLookup);
			}

			// If there are refresh fields, we must build a search and refresh action, and include those refresh
			// fields.
			boolean refresh = !foreignRefreshFields.isEmpty();
			if (refresh) {
				ActionSearchAndRefreshDB actionSR = new ActionSearchAndRefreshDB();
				actionSR.setSearchRecord(searchRecord);

				// Local key fields are all present as edit controls.
				for (int i = 0; i < localKeyFields.size(); i++) {
					Field localKeyField = localKeyFields.get(i);
					Field foreignKeyField = foreignKeyFields.get(i);
					actionSR.addKeyEditFieldName(EditContext.getEditFieldName(localKeyField));
					actionSR.addKeyAlias(foreignKeyField.getAlias());
				}

				// Add only the first refresh field.
				Field refreshField = foreignRefreshFields.get(0);
				actionSR.addRefreshEditFieldName(EditContext.getEditFieldName(refreshField));
				actionSR.addRefreshAlias(refreshField.getAlias());

				// If this field is a segment of another multi-segment lookup, add clear fields for the rest of the
				// multi-segment lookup.
				List<String> clearEditFieldNames = getClearEditFieldNames(field, fields, relation);
				for (String clearEditFieldName : clearEditFieldNames) {
					actionSR.addClearEditFieldName(clearEditFieldName);
				}

				// Add the action to he context.
				editContext.addValueAction(actionSR);
			}

			// Edit label.
			panel.add(editContext.getEditLabel().getComponent(), getConstraintsLabel(0, row));

			// Edit field.
			boolean fixed = field.isFixedWidth();
			int fill = (fixed ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL);
			int gridWidth = (fixed ? 1 : (refresh ? 1 : 2));
			panel.add(editContext.getEditField().getComponent(), getConstraintsField(1, row, fill, gridWidth));

			// Refresh field if applicable.
			if (refresh) {
				Field refreshField = foreignRefreshFields.get(0);
				String refreshAlias = refreshField.getAlias();
				EditContext refreshContext = new EditContext(getSession());
				refreshContext.setRecord(record);
				refreshContext.setAlias(refreshAlias);
				refreshContext.getField().setEditable(false);
				fill = (fixed ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL);
				panel.add(refreshContext.getEditField().getComponent(), getConstraintsField(2, row, fill, 1));
			}

		}

		return panel;
	}

	/**
	 * Returns the constraints for a column panel.
	 * 
	 * @param gridx Grid y coordinate.
	 * @param notFixedWidth A boolean that indicates if th panel is not fixed width.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsPanelColumn(int gridx, boolean notFixedWidth) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = gridx;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = (notFixedWidth ? 1 : 0);
		constraints.weighty = 1;
		return constraints;
	}

	/**
	 * Returns the constraints for a group panel.
	 * 
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsPanelGroup() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		return constraints;
	}

	/**
	 * Returns the constraints for a row panel.
	 * 
	 * @param gridy Grid y coordinate.
	 * @param insets The insets.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsPanelRow(int gridy, Insets insets) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = gridy;
		constraints.insets = insets;
		constraints.weightx = 1;
		constraints.weighty = 1;
		return constraints;
	}

	/**
	 * Returns the constraints for an edit field.
	 * 
	 * @param gridx Grid x coordinate.
	 * @param gridy Grid y coordinate.
	 * @param fill Fill.
	 * @param gridwidth Grid width.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsField(int gridx, int gridy, int fill, int gridwidth) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = fill;
		constraints.gridheight = 1;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 1;
		constraints.weighty = 0;
		return constraints;
	}

	/**
	 * Returns the constraints for a label.
	 * 
	 * @param gridx The grid x.
	 * @param gridy The grid y.
	 * @return The constraints.
	 */
	private GridBagConstraints getConstraintsLabel(int gridx, int gridy) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 0;
		constraints.weighty = 0;
		return constraints;
	}

	/**
	 * Update the edit fields with the values from the record.
	 */
	public void updateEditFields() {
		List<EditField> editFields = getEditFields();
		for (EditField editField : editFields) {
			String alias = editField.getEditContext().getAlias();
			Value value = getRecord().getValue(alias);
			editField.setValue(value);
		}
	}

	/**
	 * Update the record values from the edit fields.
	 */
	public void updateRecord() {
		List<EditField> editFields = getEditFields();
		for (EditField editField : editFields) {
			Value value = editField.getValue();
			String alias = editField.getEditContext().getAlias();
			getRecord().setValue(alias, value);
		}
	}

	/**
	 * Returns a list with all edit fields.
	 * 
	 * @return A list with all edit fields.
	 */
	public List<EditField> getEditFields() {
		if (!layoutDone) {
			layoutFields();
		}
		return SwingUtils.getEditFields(this);
	}

	/**
	 * Returns the edit field for the given alias or null.
	 * 
	 * @param alias The field alias.
	 * @return The edit field.
	 */
	public EditField getEditField(String alias) {
		List<EditField> editFields = getEditFields();
		for (EditField editField : editFields) {
			if (editField.getName().equals(EditContext.getEditFieldName(alias))) {
				return editField;
			}
		}
		return null;
	}

	/**
	 * Returns a list with all fields in the panel.
	 * 
	 * @return A list with all fields in the panel.
	 */
	public List<Field> getFields() {
		List<Field> fields = new ArrayList<>();
		for (GroupItem groupItem : groupItems) {
			for (GridItem gridItem : groupItem.gridItems) {
				fields.addAll(gridItem.fields);
			}
		}
		return fields;
	}

	/**
	 * Check that the record has been set.
	 */
	private void checkRecord() {
		if (getRecord() == null) {
			throw new IllegalStateException("The record to edit must be set.");
		}
	}
}
