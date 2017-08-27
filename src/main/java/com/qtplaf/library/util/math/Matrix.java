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

package com.qtplaf.library.util.math;

import java.util.List;

import com.qtplaf.library.util.NumberUtils;

/**
 * Centralizes static operations on vectors and matrices.
 *
 * @author Miquel Sas
 */
public class Matrix {

	/**
	 * Add the values of vectors a and b (must have the same length).
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The result of adding the values.
	 */
	public static double[] add(double[] a, double[] b) {
		int length = a.length;
		double[] r = new double[length];
		for (int i = 0; i < length; i++) {
			r[i] = a[i] + b[i];
		}
		return r;
	}

	/**
	 * Check if two vectors are equal rounding the values at the argument precision.
	 * 
	 * @param a Vector a
	 * @param b Vector b
	 * @param precision Rounding precision.
	 * @return A boolean
	 */
	public static boolean areEqual(double[] a, double[] b, int precision) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			double value_a = NumberUtils.round(a[i], precision);
			double value_b = NumberUtils.round(b[i], precision);
			if (value_a != value_b) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the cumulative average.
	 * 
	 * @param a The source vector.
	 * @return The cumulative average.
	 */
	public static double[] avgCumulative(double[] a) {
		double avg = 0;
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			avg += a[i];
			c[i] = avg / (i + 1);
		}
		return c;
	}

	/**
	 * Returns the centroid of a list of vectors.
	 * 
	 * @param vectors The list of vectors.
	 * @return The centroid.
	 */
	public static double[] centroid(List<double[]> vectors) {
		int size = vectors.get(0).length;
		double divisor = vectors.size();
		double[] centroid = new double[size];
		for (double[] vector : vectors) {
			for (int i = 0; i < size; i++) {
				centroid[i] += (vector[i] / divisor);
			}
		}
		return centroid;
	}

	/**
	 * Returns the number of columns of a matrix.
	 * 
	 * @param matrix The argument matrix.
	 * @return The number of columns.
	 */
	private static int columns(double[][] matrix) {
		if (rows(matrix) != 0) {
			return matrix[0].length;
		}
		return 0;
	}

	/**
	 * Return the Eucliden distance between two vectors.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Eucliden distance.
	 */
	public static double distanceEuclidean(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException();
		}
		int length = a.length;
		double distance = 0;
		for (int i = 0; i < length; i++) {
			distance += Math.pow(a[i] - b[i], 2);
		}
		distance = Math.pow(distance, 0.5);
		return distance;
	}

	/**
	 * Set the matrix with a scalar value.
	 * 
	 * @param matrix The matrix to initialize.
	 * @param value The value to assign.
	 */
	public static void set(double[][] matrix, double value) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = value;
			}
		}
	}

	/**
	 * Returns the number of rows of a matrix.
	 * 
	 * @param matrix The argument matrix.
	 * @return The number of rows.
	 */
	private static int rows(double[][] matrix) {
		return matrix.length;
	}

	/**
	 * Subtract the values of vector b from vector a (must have the same length).
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The result of subtracting the values.
	 */
	public static double[] subtract(double[] a, double[] b) {
		double[] r = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			r[i] = a[i] - b[i];
		}
		return r;
	}

	/**
	 * Copy the source array into the destination array. Both must have the same length.
	 * 
	 * @param src The source array.
	 * @param dst The destination array.
	 */
	public static void copy(double[] src, double[] dst) {
		for (int i = 0; i < src.length; i++) {
			dst[i] = src[i];
		}
	}

	/**
	 * Cumulate the source array into the destination. Both must have the same dimensions.
	 * 
	 * @param src The source.
	 * @param dst The destination.
	 */
	public static void cumulate(double[][] src, double[][] dst) {
		int rows = rows(src);
		int cols = columns(src);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				dst[row][col] += src[row][col];
			}
		}
	}
}
