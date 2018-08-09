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

/**
 * Utility to use flags within an integer. Only 31 (0 to 30) flags can be set.
 *
 * @author Miquel Sas
 */
public class Flags {

	public static long set(int flags, int flag, boolean b) {
        if(b) {
            flags |= (1 << flag);
        } else {
            flags &= ~(1 << flag);
        }
		return flags;
	}
    
	public static boolean get(int flags, int flag) {
        int mask = (1 << flag);
        return ((flags & mask) == mask);
    }
}
