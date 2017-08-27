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

package com.qtplaf.library.ai.fuzzy.function;

import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.ai.fuzzy.Function;

/**
 * Linear function.
 *
 * @author Miquel Sas
 */
public class Linear implements Function {

	/**
	 * Returns the factor of the argument value within the range of the segment.
	 * <p>
	 * If sign == 0, then the factor must be:
	 * <ul>
	 * <li>1 if value == (maximum-minimum)/2</li>
	 * <li>0 if value &lt;= minimum</li>
	 * <li>0 if value &gt;= maximum</li>
	 * </ul>
	 * If sign == 1, then the factor must be:
	 * <ul>
	 * <li>0 if value &lt;= minimum</li>
	 * <li>1 if value &gt;= maximum</li>
	 * </ul>
	 * If sign == -1, then the factor must be:
	 * <ul>
	 * <li>1 if value &lt;= minimum</li>
	 * <li>0 if value &gt;= maximum</li>
	 * </ul>
	 * 
	 * @param value The value to compute.
	 * @param maximum The maximum value.
	 * @param minimum The minimum value.
	 * @param sign The sign, -1, 0 or 1.
	 * @return The factor.
	 */
	@Override
	public double getFactor(double value, double maximum, double minimum, int sign) {
		if (sign == 1) {
			return new StdNormalizer(maximum, minimum).normalize(value);
		} else if (sign == -1) {
			return 1 - new StdNormalizer(maximum, minimum).normalize(value);
		}

		// Sign == 0
		double mid = 0;
		if ((maximum > 0 && minimum >= 0) || (minimum < 0 && maximum <= 0)) {
			mid = (maximum - minimum) / 2;
		}
		if (value == mid) {
			return 1;
		}
		if (value < mid) {
			return 1 - new StdNormalizer(mid, minimum).normalize(value);
		}
		return new StdNormalizer(maximum, mid).normalize(value);
	}
}
