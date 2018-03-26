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

package com.qtplaf.library.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simplifies calls to <tt>ThreadLocalRandom.current()</tt>.
 *
 * @author Miquel Sas
 */
public class Random {
	/**
	 * Return the next integer.
	 * 
	 * @param bound The bounds.
	 * @return The next integer.
	 */
	public static int nextInt(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	/**
	 * Return a random double between 0 and 1.
	 * 
	 * @return The random double.
	 */
	public static double nextDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	/**
	 * Return a random gaussian.
	 * 
	 * @return The random gaussian.
	 */
	public static double nextGaussian() {
		return ThreadLocalRandom.current().nextGaussian();
	}
}
