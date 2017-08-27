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
package com.qtplaf.library.swing;

import com.qtplaf.library.util.NumberUtils;

/**
 * Action ggroups are used to group actions.
 * 
 * @author Miquel Sas
 */
public class ActionGroup implements Comparable<ActionGroup> {

	/**
	 * Predefined default action group.
	 */
	public static final ActionGroup DEFAULT = new ActionGroup("Default", NumberUtils.MAX_INTEGER - 100);
	/**
	 * Predefined edit action group (New, Modify, Delete...)
	 */
	public static final ActionGroup EDIT = new ActionGroup("Edit", 100);
	/**
	 * Predefine configure action group.
	 */
	public static final ActionGroup CONFIGURE = new ActionGroup("Configure", 200);
	/**
	 * Predefined action group for intput actions.
	 */
	public static final ActionGroup INPUT = new ActionGroup("Intput", 300);
	/**
	 * Predefined action group for output actions.
	 */
	public static final ActionGroup OUTPUT = new ActionGroup("Output", 400);
	/**
	 * Predefined action group for detail actions.
	 */
	public static final ActionGroup DETAIL = new ActionGroup("Detail", 500);
	/**
	 * Predefined action group for lookups.
	 */
	public static final ActionGroup LOOKUP = new ActionGroup("Lookup", 600);
	/**
	 * Predefined action group for undetermined operations.
	 */
	public static final ActionGroup OPERATION = new ActionGroup("Lookup", 700);
	/**
	 * Predefined exit action group.
	 */
	public static final ActionGroup EXIT = new ActionGroup("Exit", NumberUtils.MAX_INTEGER);

	/**
	 * The name or identifier of the group.
	 */
	private String name;
	/**
	 * The index to sort the group within a list of groups.
	 */
	private int sortIndex = -1;

	/**
	 * Constructor assigning the name.
	 * 
	 * @param name This group name.
	 * @param sortIndex The sort index.
	 */
	public ActionGroup(String name, int sortIndex) {
		super();
		this.name = name;
		this.sortIndex = sortIndex;
	}

	/**
	 * Get this group name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set this group name.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Check wheter the argument object is equal to this action group.
	 *
	 * @param obj The object to compare
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ActionGroup)) {
			return false;
		}
		ActionGroup group = (ActionGroup) obj;
		return getName().equals(group.getName());
	}

	/**
	 * Returns the hash code for this field.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Gets a string representation of the field.
	 * 
	 * @return A string representation of this field.
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns the sort index.
	 * 
	 * @return The sort index.
	 */
	public int getSortIndex() {
		return sortIndex;
	}

	/**
	 * Sets the sort index.
	 * 
	 * @param sortIndex The sort index to set.
	 */
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the
	 * specified value.
	 * <p>
	 * A field is considered to be equal to another field if the alias, type, length and decimals are the same.
	 *
	 * @param actionGroup The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(ActionGroup actionGroup) {
		return new Integer(getSortIndex()).compareTo(actionGroup.getSortIndex());
	}

}
