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

package com.qtplaf.library.ai.function.centroid;

import java.util.List;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.function.Centroid;
import com.qtplaf.library.util.math.Matrix;

/**
 * Basic centroid.
 *
 * @author Miquel Sas
 */
public class BasicCentroid implements Centroid {

	/**
	 * Constructor.
	 */
	public BasicCentroid() {
		super();
	}

	/**
	 * Calculates the centroid of the inputs of the list of patterns.
	 * 
	 * @param patterns The list of patterns.
	 * @return The centroid.
	 */
	@Override
	public double[] calculate(List<Pattern> patterns) {
		return Matrix.centroid(Pattern.getInputs(patterns));
	}

}
