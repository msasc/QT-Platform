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
package com.qtplaf.library.app;

/**
 * An access restriction to a menu option, action button,  table columns or any defined string key.
 * 
 * @author Miquel Sas
 */
public enum AccessMode {
	/** Access denied. */
	DENIED,
	/** Read-only access. */
	READ_ONLY,
	/** Read-write access. */
	READ_WRITE,
	/** Not defined. */
	NOT_DEFINED;
}
