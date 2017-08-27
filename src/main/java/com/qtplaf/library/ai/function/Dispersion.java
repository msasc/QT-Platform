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

package com.qtplaf.library.ai.function;

import java.util.List;

import com.qtplaf.library.ai.data.Pattern;

/**
 * Dispersion function.
 *
 * @author Miquel Sas
 */
public interface Dispersion {
	/**
	 * Calculates the dispersion of the list of patterns.
	 * 
	 * @param patterns The list of patterns.
	 * @return The dispersion.
	 */
	double calculate(List<Pattern> patterns);
}
