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

import java.util.Comparator;

import javax.swing.Action;

/**
 * A comparator to sort actions by action group sort index and then the action sort index.
 * 
 * @author Miquel Sas
 */
public class ActionComparator implements Comparator<Action> {

	/**
	 * Default constructor.
	 */
	public ActionComparator() {
	}

	/**
	 * Compares the two actions.
	 */
	@Override
	public int compare(Action a1, Action a2) {
		if (a1 == null && a2 == null) {
			return 0;
		}
		if (a1 != null && a2 == null) {
			return -1;
		}
		if (a1 == null && a2 != null) {
			return 1;
		}

		ActionGroup g1 = ActionUtils.getActionGroup(a1);
		ActionGroup g2 = ActionUtils.getActionGroup(a2);

		int gs1 = g1.getSortIndex();
		int gs2 = g2.getSortIndex();

		int compare = Integer.compare(gs1, gs2);
		if (compare != 0) {
			return compare;
		}

		int s1 = ActionUtils.getSortIndex(a1);
		int s2 = ActionUtils.getSortIndex(a2);

		return Integer.compare(s1, s2);
	}

}
