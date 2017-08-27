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

package com.qtplaf.library.database;

import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;

/**
 * A group of fields within a table or view. Domains of the same group are expected to same a related meaning.
 * 
 * @author Miquel Sas
 */
public class FieldGroup implements Comparable<FieldGroup> {

	/**
	 * The final empty field group used when no field group has been set.
	 */
	public static final FieldGroup emptyFieldGroup = getEmptyFieldGroup();

	/**
	 * Returns an empty field group useful to layout fields.
	 * 
	 * @return An empty field group.
	 */
	private static final FieldGroup getEmptyFieldGroup() {
		FieldGroup fieldGroup = new FieldGroup();
		fieldGroup.index = NumberUtils.MIN_INTEGER;
		fieldGroup.name = "";
		fieldGroup.title = "";
		fieldGroup.description = "";
		return fieldGroup;
	}

	/**
	 * The name of the group.
	 */
	private String name;
	/**
	 * The title of the group. The title can be used in a tabbed pane as the tab title.
	 */
	private String title;
	/**
	 * The description of the group.
	 */
	private String description;
	/**
	 * The index or order in a presentation of fields inside field groups.
	 */
	private int index = 0;

	/**
	 * Default constructor.
	 */
	public FieldGroup() {
		super();
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param index The sort index.
	 * @param name The name.
	 * @param title The title.
	 * @param description The description.
	 */
	public FieldGroup(int index, String name, String title, String description) {
		super();
		if (index < 0) {
			throw new IllegalArgumentException("Field group index can not be negative");
		}
		this.index = index;
		this.name = name;
		this.title = title;
		this.description = description;
	}

	/**
	 * Do not admit modify the empty field group.
	 */
	private void checkModifyEmpty() {
		if (index < 0) {
			throw new UnsupportedOperationException("The unique  empty field group can not be modified");
		}
	}

	/**
	 * Get the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		checkModifyEmpty();
		this.name = name;
	}

	/**
	 * Get the title.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		if (title == null) {
			return getDescription();
		}
		return title;
	}

	/**
	 * Set the title.
	 * 
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		checkModifyEmpty();
		this.title = title;
	}

	/**
	 * Get this group description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set this group description.
	 * 
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		checkModifyEmpty();
		this.description = description;
	}

	/**
	 * Returns the index or order in a presentation of fields inside field groups.
	 * 
	 * @return The index or order in a presentation of fields inside field groups.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index or order in a presentation of fields inside field groups. Can not be negative.
	 * 
	 * @param index The index or order in a presentation of fields inside field groups.
	 */
	public void setIndex(int index) {
		checkModifyEmpty();
		if (index < 0) {
			throw new IllegalArgumentException("Field group index can not be negative");
		}
		this.index = index;
	}

	/**
	 * Returns the display title.
	 * 
	 * @return The display title.
	 */
	public String getDisplayTitle() {
		return StringUtils.getFirstNotNull(getTitle(), getName(), getDescription());
	}

	/**
	 * Returns the display description.
	 * 
	 * @return The display description.
	 */
	public String getDisplayDescription() {
		return StringUtils.getFirstNotNull(getDescription(), getTitle(), getName());
	}

	/**
	 * Returns the hash code.
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Returns a boolean indicating if the argument object is equal to this field group. Field groups are compared by
	 * name.
	 * 
	 * @return A boolean indicating if the argument object is equal to this field group. Field groups are compared by
	 *         name
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldGroup) {
			FieldGroup fg = (FieldGroup) obj;
			return name.equals(fg.name);
		}
		return false;
	}

	/**
	 * Compares for sorting this field group to the argument field group.
	 * 
	 * @param fieldGroup The field group to compare with.
	 */
	@Override
	public int compareTo(FieldGroup fieldGroup) {
		if (getIndex() < fieldGroup.getIndex()) {
			return -1;
		}
		if (getIndex() > fieldGroup.getIndex()) {
			return 1;
		}
		return 0;
	}

	/**
	 * Returns a string representation.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getName());
		b.append(" - ");
		b.append(getTitle());
		return b.toString();
	}
}
