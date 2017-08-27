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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.Random;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Generic static calculator methods currently not in use.
 *
 * @author Miquel Sas
 */
public class Incubator {

	/**
	 * Returns the argument matrix transposed.
	 * 
	 * @param matrix The matrix to transpose.
	 * @return The transposed matrix.
	 */
	public static double[][] transpose(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] transposed = new double[columns][rows];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				transposed[column][row] = matrix[row][column];
			}
		}
		return transposed;
	}

	/**
	 * Returns the total of the list of values.
	 * 
	 * @param values The list of Double values.
	 * @return The total.
	 */
	public static double total(List<Double> values) {
		return Incubator.total(ListUtils.toDoubleArray(values));
	}

	/**
	 * Returns the total of the list of values.
	 * 
	 * @param values The array of double values.
	 * @return The total.
	 */
	public static double total(double[] values) {
		double total = 0;
		for (double value : values) {
			total += value;
		}
		return total;
	}

	/**
	 * Returns a string representation of a matrix (for debug purposes)
	 * 
	 * @param matrix The argument matrix.
	 * @return The string representation.
	 */
	public static String toString(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		StringBuilder b = new StringBuilder();
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				b.append(matrix[row][column]);
				b.append(" ");
			}
			b.append("\n");
		}
		return b.toString();
	}

	/**
	 * Normalize the list of values based on the mean and the standard deviation.
	 * 
	 * @param values The list of values.
	 * @param mean The mean.
	 * @param stddev The standard deviation.
	 * @return The list normalized.
	 */
	public static List<Double> normalizeSign(List<Double> values, double mean, double stddev) {
		return Incubator.toList(Incubator.normalizeSign(ListUtils.toDoubleArray(values), mean, stddev));
	}

	/**
	 * Returns the list of normalized values, values that are &gt;= 0 and &lt;= 1.
	 * 
	 * @param values The list of value to normalize.
	 * @return The list of values normalized
	 */
	public static List<Double> normalizeSign(List<Double> values) {
		return Incubator.toList(Incubator.normalizeSign(ListUtils.toDoubleArray(values)));
	}

	/**
	 * Ortho normalize the vector (the euclidean norm to be 1).
	 * 
	 * @param v The vector.
	 * @return The ortho normalized vector.
	 */
	public static double[] orthoNormal(double[] v) {
		int size = Incubator.size(v);
		double[] n = new double[size];
		double norm = Incubator.euclideanNorm(v);
		for (int i = 0; i < size; i++) {
			n[i] = (norm == 0 ? 0.0 : v[i] / norm);
		}
		return n;
	}

	/**
	 * Normalize the list of values based on the mean and the standard deviation.
	 * 
	 * @param values The list of values.
	 * @param mean The mean.
	 * @param stddev The standard deviation.
	 * @return The list normalized.
	 */
	public static double[] normalizeSign(double[] values, double mean, double stddev) {
		int size = Incubator.size(values);
		double[] normalized = new double[size];
		for (int i = 0; i < size; i++) {
			double value = values[i];
			if (stddev == 0) {
				normalized[i] = 0;
			} else {
				normalized[i] = (value - mean) / stddev;
			}
		}
		return normalized;
	}

	/**
	 * Returns the list of normalized values, values that are &gt;= 0 and &lt;= 1.
	 * 
	 * @param values The list of value to normalize.
	 * @return The list of values normalized
	 */
	public static double[] normalizeSign(double[] values) {
		double maximum = NumberUtils.MIN_DOUBLE;
		double minimum = NumberUtils.MAX_DOUBLE;
		for (double value : values) {
			if (value > maximum) {
				maximum = value;
			}
			if (value < minimum) {
				minimum = value;
			}
		}
		int size = Incubator.size(values);
		double[] normalized = new double[size];
		for (int i = 0; i < size; i++) {
			normalized[i] = Incubator.normalizeSign(values[i], maximum, minimum);
		}
		return normalized;
	}

	/**
	 * Normalizes the value in a range of maximum/minimum values with a sign. If both the maximum and the minimum are
	 * positive, normalizes to the range [1.0, 0.0]. If both are negative, normalizes in the range [0.0, -1.0]. If the
	 * maximum is positive and the minimum is negative, normalizes in the range [1.0, -1.0].
	 * 
	 * @param value The value to normalize.
	 * @param maximum The maximum.
	 * @param minimum The minimum.
	 * @return The normalized value.
	 */
	public static double normalizeSign(double value, double maximum, double minimum) {
	
		// Ensure maximum and minimum, swap if necessary..
		if (minimum > maximum) {
			double tmp = maximum;
			maximum = minimum;
			minimum = tmp;
		}
	
		// Both maximum and minimu are zero.
		if (maximum == 0 && minimum == 0) {
			return 0;
		}
	
		// Ensure in the range.
		value = Math.min(value, maximum);
		value = Math.max(value, minimum);
	
		// Both maximum and minimum are positive, normalize [1.0, 0.0]
		if (maximum >= 0 && minimum >= 0) {
			return (value - minimum) / (maximum - minimum);
		}
		// Both maximum and minimum are negative, normalize [0.0, -1.0]
		if (maximum <= 0 && minimum <= 0) {
			return ((value - minimum) / (maximum - minimum)) - 1.0;
		}
		// Maximum positive and minimum negative.
		if (maximum >= 0 && minimum <= 0) {
			if (value >= 0) {
				return (value - 0.0) / (maximum - 0.0);
			}
			return ((value - minimum) / (0.0 - minimum)) - 1.0;
		}
		// Never should come here.
		throw new IllegalStateException("Fatal error while normalizing");
	}

	/**
	 * Returns the list given the array of values.
	 * 
	 * @param values The array of values.
	 * @return The list of values.
	 */
	public static List<Double> toList(double[] values) {
		List<Double> list = new ArrayList<>();
		for (double value : values) {
			list.add(value);
		}
		return list;
	}

	/**
	 * Returns the abs vector
	 * 
	 * @param vector The argument vector.
	 * @return The absolute vector.
	 */
	public static double[] abs(double[] vector) {
		double[] abs = new double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			abs[i] = Math.abs(vector[i]);
		}
		return abs;
	}

	/**
	 * Subtract the values of vector b from vector a, absolute values.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The result of subtracting the values.
	 */
	public static double[] absSubtract(double[] a, double[] b) {
		Incubator.checkVectorsSizes(a, b);
		int size = Incubator.size(a);
		double[] r = new double[size];
		for (int i = 0; i < size; i++) {
			r[i] = Math.abs(a[i] - b[i]);
		}
		return r;
	}

	/**
	 * Returns the subtraction (a-b) of the two matrices.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The subtraction matrix.
	 */
	public static double[][] absSubtract(double[][] a, double[][] b) {
		Incubator.checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		double[][] subtraction = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				subtraction[row][column] = Math.abs(a[row][column] - b[row][column]);
			}
		}
		return subtraction;
	}

	/**
	 * Returns the addition of the two matrices.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The addition matrix.
	 */
	public static double[][] add(double[][] a, double[][] b) {
		Incubator.checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		double[][] addition = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				addition[row][column] = a[row][column] + b[row][column];
			}
		}
		return addition;
	}

	/**
	 * Returns the output vector translated to minimize the mean squared error.
	 * 
	 * @param output Initial output vector.
	 * @param input Input vector, or reference.
	 * @param learningFactor Learnin factor.
	 * @param maximumError The minimum error to break the loop.
	 * @param maximumIterations The maximum number of iterations.
	 * @return The output vector translated to minimize the mean squared error.
	 */
	public static double[] meanSquaredMinimum(
		double[] output,
		double[] input,
		double learningFactor,
		double maximumError,
		int maximumIterations) {
	
		// Size and result.
		int size = Incubator.size(output);
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = output[i];
		}
	
		// Current iteration.
		int iteration = 0;
	
		// Previous mean squared.
		double meanSquaredPrevious = Incubator.meanSquared(result, input);
	
		// Error.
		double error = NumberUtils.MAX_DOUBLE;
	
		// The sign.
		double sign = 1d;
	
		// Main loop.
		while (true) {
	
			// Break maximum iterations.
			if (iteration >= maximumIterations) {
				break;
			}
	
			// Apply the translation.
			double translation = meanSquaredPrevious * learningFactor * sign;
			Incubator.addAssign(translation, result);
	
			// Current mean squared.
			double meanSquaredCurrent = Incubator.meanSquared(result, input);
	
			// Error.
			error = Math.abs(meanSquaredCurrent - meanSquaredPrevious);
	
			// Break if error is less than minimum error.
			if (error < maximumError) {
				break;
			}
	
			// If there is an increase, change the sign and reduce the learning factor.
			if (meanSquaredCurrent > meanSquaredPrevious) {
				sign *= -1d;
				learningFactor *= 0.5;
			}
	
			// Move mean square.
			meanSquaredPrevious = meanSquaredCurrent;
	
			// Increase the iteration counter.
			iteration++;
		}
	
		return result;
	}

	/**
	 * Returns the Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Euclidean distance.
	 */
	public static double meanSquared(double[] a, double[] b) {
		double size = Incubator.size(a);
		return Incubator.euclideanDistance(a, b) * 2 / size;
	}

	/**
	 * Assigns the tranlated or added vector.
	 * 
	 * @param value The value to add.
	 * @param vector The source vector.
	 */
	public static void addAssign(double value, double[] vector) {
		int size = Incubator.size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] = value + vector[i];
		}
	}

	/**
	 * Add and assign matrix b to matrix a.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b
	 */
	public static void addAssign(double[][] a, double[][] b) {
		Incubator.checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				a[row][column] += b[row][column];
			}
		}
	}

	/**
	 * Check if two matrices are equal rounding the values at the argument precision.
	 * 
	 * @param a Matrix a
	 * @param b Matrix b
	 * @param precision Rounding precision.
	 * @return A boolean
	 */
	public static boolean areEqual(double[][] a, double[][] b, int precision) {
	
		// Check dimensions
		if (rows(a) != rows(b) || columns(a) != columns(b)) {
			return false;
		}
	
		// Check every item
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				double value_a = NumberUtils.round(a[row][column], precision);
				double value_b = NumberUtils.round(b[row][column], precision);
				if (value_a != value_b) {
					return false;
				}
			}
		}
	
		return true;
	}

	/**
	 * Returns the average of a list of values.
	 *
	 * @param values The list of double values.
	 * @return The average
	 */
	public static double average(double... values) {
		return Incubator.mean(values);
	}

	/**
	 * Returns the centroid of a list of vectors.
	 * 
	 * @param vectors The list of vectors.
	 * @return The centroid.
	 */
	public static double[] centroid(double[]... vectors) {
		int size = vectors[0].length;
		double divisor = size;
		double[] centroid = new double[size];
		for (double[] vector : vectors) {
			for (int i = 0; i < size; i++) {
				centroid[i] += (vector[i] / divisor);
			}
		}
		return centroid;
	}

	/**
	 * Returns the centroid for a list of matrices.
	 * 
	 * @param matrices The list of matrices.
	 * @return The centroid.
	 */
	public static double[][] centroidMatrices(List<double[][]> matrices) {
		int rows = rows(matrices.get(0));
		int cols = columns(matrices.get(0));
		double[][] centroid = new double[rows][cols];
		double divisor = matrices.size();
		for (double[][] matrix : matrices) {
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					centroid[r][c] += (matrix[r][c] / divisor);
				}
			}
		}
		return centroid;
	}

	/**
	 * Returns a copy of the matrix.
	 * 
	 * @param matrix The matrix to copy.
	 * @return The copy.
	 */
	public static double[][] copy(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] copy = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				copy[row][column] = matrix[row][column];
			}
		}
		return copy;
	}

	/**
	 * Creates and returns a diagonal matrix.
	 * 
	 * @param values The list of values of the diagonal.
	 * @return The diagonal matrix.
	 */
	public static double[][] diagonalMatrix(double[] values) {
		int size = Incubator.size(values);
		double[][] diagonal = new double[size][size];
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (r == c) {
					diagonal[r][c] = values[r];
				} else {
					diagonal[r][c] = 0;
				}
			}
		}
		return diagonal;
	}

	/**
	 * Divide a vector by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vector The vector.
	 * @return The division vector.
	 */
	public static double[] divide(double value, double[] vector) {
		int size = Incubator.size(vector);
		double[] product = new double[size];
		for (int i = 0; i < size; i++) {
			product[i] = vector[i] / value;
		}
		return product;
	}

	/**
	 * Divide a matrix by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 * @return The divide matrix.
	 */
	public static double[][] divide(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] product = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				product[row][column] = matrix[row][column] / value;
			}
		}
		return product;
	}

	/**
	 * Divide a vector by a scalar value assinging it to the vector.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vector The vector.
	 */
	public static void divideAssign(double value, double[] vector) {
		int size = Incubator.size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] /= value;
		}
	}

	/**
	 * Returns the Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Euclidean distance.
	 */
	public static double euclideanDistance(double[] a, double[] b) {
		return Math.sqrt(Incubator.squaredEuclideanDistance(a, b));
	}

	/**
	 * Returns the Euclidean norm of a vector or list of values.
	 * 
	 * @param v The vector.
	 * @return The Euclidean norm.
	 */
	public static double euclideanNorm(double[] v) {
		return Math.sqrt(Incubator.squaredEuclideanNorm(v));
	}

	/**
	 * Returns the identity matrix for the given dimensions.
	 * 
	 * @param rows Number of rows
	 * @param columns Number of columns.
	 * @return The identity matrix.
	 */
	public static double[][] identity(int rows, int columns) {
		double[][] identity = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				identity[row][column] = (row == column ? 1.0 : 0.0);
			}
		}
		return identity;
	}

	/**
	 * Initializes the matrix with random values between lower and upper limits.
	 * 
	 * @param matrix The matrix to initialize.
	 * @param lowerLimit The lower limit.
	 * @param upperLimit The upper limit.
	 */
	public static void initialize(double[][] matrix, double lowerLimit, double upperLimit) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = lowerLimit + ((upperLimit - lowerLimit) * Random.nextDouble());
			}
		}
	}

	/**
	 * Returns the Manhattan distance.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Manhattan distance.
	 */
	public static double manhattanDistance(double[] a, double[] b) {
		if (Incubator.size(a) != Incubator.size(b)) {
			throw new IllegalArgumentException("Vector lengths must be the same.");
		}
		int size = Incubator.size(a);
		double distance = 0;
		for (int i = 0; i < size; i++) {
			distance += Math.abs(a[i] - b[i]);
		}
		return distance;
	}

	/**
	 * Multiply a vector by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vector The vector.
	 * @return The product vector.
	 */
	public static double[] multiply(double value, double[] vector) {
		int size = Incubator.size(vector);
		double[] product = new double[size];
		for (int i = 0; i < size; i++) {
			product[i] = vector[i] * value;
		}
		return product;
	}

	/**
	 * Multiply a matrix by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 * @return The product matrix.
	 */
	public static double[][] multiply(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] product = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				product[row][column] = matrix[row][column] * value;
			}
		}
		return product;
	}

	/**
	 * Linear algebraic matrix multiplication.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The matrix product a * b
	 */
	public static double[][] multiply(double[][] a, double[][] b) {
		if (rows(b) != columns(a)) {
			throw new IllegalArgumentException(
				"The number of rows of the b matrix must be equal to the number of columns of the a matrix.");
		}
	
		int rows_a = rows(a);
		int columns_a = columns(a);
		int columns_b = columns(b);
	
		double[][] product = new double[rows_a][columns_b];
		for (int row_a = 0; row_a < rows_a; row_a++) {
			for (int column_b = 0; column_b < columns_b; column_b++) {
				double value = 0;
				for (int column_a = 0; column_a < columns_a; column_a++) {
					value += (a[row_a][column_a] * b[column_a][column_b]);
				}
				product[row_a][column_b] = value;
			}
		}
	
		return product;
	}

	/**
	 * Multiply a vector by a scalar value and assign it to the vector values.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vector The vector.
	 */
	public static void multiplyAssign(double value, double[] vector) {
		int size = Incubator.size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] *= value;
		}
	}

	/**
	 * Multiply a matrix by a scalar value and assign it.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 */
	public static void multiplyAssign(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] *= value;
			}
		}
	}

	/**
	 * Returns the Gaussian radial basis function.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @param sigma Sigma parameter.
	 * @return The radial basis.
	 */
	public static double radialBasis(double[] a, double[] b, double sigma) {
		double sd = Incubator.squaredEuclideanDistance(a, b);
		double rb = Math.exp(-sd / (2 * Math.pow(sigma, 2)));
		return rb;
	}

	/**
	 * Returns the sigmoid derivative of a value.
	 * 
	 * @param value The value.
	 * @return The sigmoid derivative.
	 */
	public static double sigmoidDerivative(double value) {
		double output = Incubator.sigmoid(value);
		return output * (1 - output);
	}

	/**
	 * Returns the sigmoid of a value.
	 * 
	 * @param value The value.
	 * @return The sigmoid.
	 */
	public static double sigmoid(double value) {
		return 1 / (1 + Math.exp(-(value)));
	}

	/**
	 * Returns the squared Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The squared Euclidean distance.
	 */
	public static double squaredEuclideanDistance(double[] a, double[] b) {
		if (Incubator.size(a) != Incubator.size(b)) {
			throw new IllegalArgumentException("Vector lengths must be the same.");
		}
		int size = Incubator.size(a);
		double distance = 0;
		for (int i = 0; i < size; i++) {
			distance += Math.pow(a[i] - b[i], 2);
		}
		return distance;
	}

	/**
	 * Returns the squared Euclidean norm of a vector or list of values.
	 * 
	 * @param v The vector.
	 * @return The squared Euclidean norm.
	 */
	public static double squaredEuclideanNorm(double[] v) {
		int size = Incubator.size(v);
		double norm = 0;
		for (int i = 0; i < size; i++) {
			norm += Math.pow(v[i], 2);
		}
		return norm;
	}

	/**
	 * Returns the subtraction of the value from the vector.
	 * 
	 * @param value The value to subtract.
	 * @param vector The vector.
	 * @return The subtraction vector.
	 */
	public static double[] subtract(double value, double[] vector) {
		int size = Incubator.size(vector);
		double[] subtraction = new double[size];
		for (int i = 0; i < size; i++) {
			subtraction[i] = vector[i] - value;
		}
		return subtraction;
	}

	/**
	 * Returns the subtraction (a-b) of the two matrices.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The subtraction matrix.
	 */
	public static double[][] subtract(double[][] a, double[][] b) {
		Incubator.checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		double[][] subtraction = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				subtraction[row][column] = a[row][column] - b[row][column];
			}
		}
		return subtraction;
	}

	/**
	 * Subtract and assign a value from matrix vector.
	 * 
	 * @param value The value to subtract.
	 * @param vector The vector.
	 */
	public static void subtractAssign(double value, double[] vector) {
		int size = Incubator.size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] -= value;
		}
	}

	/**
	 * Subtract and assign matrix b from matrix a.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b
	 */
	public static void subtractAssign(double[][] a, double[][] b) {
		Incubator.checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				a[row][column] -= b[row][column];
			}
		}
	}

	/**
	 * Check that matrices dimensions agree.
	 *
	 * @param a Matrix a
	 * @param b Matrix b
	 */
	static void checkMatricesDimensions(double[][] a, double[][] b) {
		if (rows(a) != rows(b) || columns(a) != columns(b)) {
			throw new IllegalArgumentException("Matrices dimensions must agree");
		}
	}

	/**
	 * Check that the sizes of the vectors are the same.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 */
	static void checkVectorsSizes(double[] a, double[] b) {
		if (Incubator.size(a) != Incubator.size(b)) {
			throw new IllegalArgumentException("Vectors sizes must agree");
		}
	}

	/**
	 * Returns the centroid of a list of vectors.
	 * 
	 * @param vectors The list of vectors.
	 * @return The centroid.
	 */
	public static double[] centroidVectors(List<double[]> vectors) {
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
	 * Gaussian probability density function (PDF).
	 * 
	 * @param x Value.
	 * @param m Mu (mean).
	 * @param s Sigma (standard deviation)
	 * @return The Gaussian PDF.
	 */
	public static double gaussianPDF(double x, double m, double s) {
		return Math.exp((-1.0) * Math.pow(x - m, 2.0) / (2.0 * Math.pow(s, 2.0))) / (s * Math.sqrt(2.0 * Math.PI));
	}

	/**
	 * Randomly merge the list of arrays of data.
	 * 
	 * @param datas The list of source arrays of data coefficients.
	 * @return The merged array of data coefficients.
	 */
	public static double[] mergeDatas(List<double[]> datas) {
		int length = datas.get(0).length;
		double[] data = new double[length];
		for (int i = 0; i < length; i++) {
			int index = Random.nextInt(datas.size());
			data[i] = datas.get(index)[i];
		}
		return data;
	}

	/**
	 * Return a randomized vector with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 * 
	 * @param length The vector length.
	 * @return The randomized data.
	 */
	public static double[] randomizeGaussian(int length) {
		double[] data = new double[length];
		for (int i = 0; i < data.length; i++) {
			data[i] = Random.nextGaussian();
		}
		return data;
	}

	/**
	 * Return the source data perturbed.
	 * 
	 * @param data The source data.
	 * @param perturbFactor A factor that indicates the proportion of the value to perturb.
	 * @param perturbSize A factor that indicates the proportion of values to perturb.
	 * @return The perturbed data.
	 */
	public static double[] randomPerturb(double[] data, double perturbFactor, double perturbSize) {
		StdNormalizer normalizer = new StdNormalizer();
		normalizer.setDataHigh(1.0);
		normalizer.setDataLow(0.0);
		double[] perturbed = Incubator.clone(data);
		if (perturbSize < 1.0) {
			int size = Double.valueOf(Double.valueOf(data.length) * perturbSize).intValue();
			for (int i = 0; i < size; i++) {
				int index = Random.nextInt(data.length);
				double value = data[index];
				double normalizedHigh = Math.abs(value) * Math.abs(perturbFactor);
				double normalizedLow = (-1.0) * normalizedHigh;
				normalizer.setNormalizedHigh(normalizedHigh);
				normalizer.setNormalizedLow(normalizedLow);
				double delta = normalizer.normalize(Random.nextDouble());
				perturbed[index] += delta;
			}
		} else {
			for (int i = 0; i < data.length; i++) {
				double value = data[i];
				double normalizedHigh = Math.abs(value) * Math.abs(perturbFactor);
				double normalizedLow = (-1.0) * normalizedHigh;
				normalizer.setNormalizedHigh(normalizedHigh);
				normalizer.setNormalizedLow(normalizedLow);
				double delta = normalizer.normalize(Random.nextDouble());
				perturbed[i] += delta;
			}
		}
		return perturbed;
	}

	/**
	 * Shuffle the argument data with the number of flips.
	 * 
	 * @param data The data to shuffle.
	 * @param flips The number of flips.
	 * @return The shuffled data.
	 */
	public static double[] randomShuffle(double[] data, int flips) {
		double[] shuffled = Incubator.clone(data);
		for (int flip = 0; flip < flips; flip++) {
			int i1 = Random.nextInt(data.length);
			int i2 = Random.nextInt(data.length);
			double d1 = shuffled[i1];
			shuffled[i1] = shuffled[i2];
			shuffled[i2] = d1;
		}
		return shuffled;
	}

	/**
	 * Returns the standard deviation for a list of values.
	 * 
	 * @param values The list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(double[] values) {
		return Incubator.stddev(values, Incubator.mean(values));
	}

	/**
	 * Returns the standard deviation for a list of values.
	 * 
	 * @param values The list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(List<Double> values) {
		return Incubator.stddev(values, Incubator.mean(values));
	}

	/**
	 * Returns the standard deviation for a list of values and its mean.
	 * 
	 * @param values The list of values.
	 * @param mean The mean of the list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(List<Double> values, double mean) {
		return Incubator.stddev(ListUtils.toDoubleArray(values), mean);
	}

	/**
	 * Returns the average of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The average.
	 */
	public static double average(List<Double> values) {
		return Incubator.mean(values);
	}

	/**
	 * Return the clone of the argument data.
	 * 
	 * @param data The data.
	 * @return The clone.
	 */
	public static double[] clone(double[] data) {
		double[] clone = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			clone[i] = data[i];
		}
		return clone;
	}

	/**
	 * Returns the mean of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The mean.
	 */
	public static double mean(List<Double> values) {
		return Incubator.mean(ListUtils.toDoubleArray(values));
	}

	/**
	 * Returns the standard deviation for a list of values and its mean.
	 * 
	 * @param values The list of values.
	 * @param mean The mean of the list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(double[] values, double mean) {
		if (Incubator.size(values) <= 1) {
			return 0;
		}
		double variance = 0;
		for (double value : values) {
			double difference = value - mean;
			variance += (difference * difference);
		}
		variance /= (Double.valueOf(Incubator.size(values)).doubleValue() - 1);
		return Math.sqrt(variance);
	}

	/**
	 * Returns the size of a vector.
	 * 
	 * @param vector The vector.
	 * @return The size.
	 */
	public static int size(double[] vector) {
		return vector.length;
	}

	/**
	 * Returns the mean of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The mean.
	 */
	public static double mean(double[] values) {
		if (values.length == 0) {
			return 0;
		}
		double mean = 0;
		for (double value : values) {
			mean += value;
		}
		mean /= Double.valueOf(values.length).doubleValue();
		return mean;
	}

	/**
	 * Returns the maximum.
	 * 
	 * @param values The list of values.
	 * @return The maximum.
	 */
	public static double maximum(double[] values) {
		double maximum = NumberUtils.MIN_DOUBLE;
		for (int i = 0; i < values.length; i++) {
			maximum = Math.max(maximum, values[i]);
		}
		return maximum;
	}

	/**
	 * Returns the minimum.
	 * 
	 * @param values The list of values.
	 * @return The minimum.
	 */
	public static double minimum(double[] values) {
		double minimum = NumberUtils.MAX_DOUBLE;
		for (int i = 0; i < values.length; i++) {
			minimum = Math.min(minimum, values[i]);
		}
		return minimum;
	}

	/**
	 * Returns the tranlated or added vector.
	 * 
	 * @param value The value to add.
	 * @param vector The source vector.
	 * @return The translated vector.
	 */
	public static double[] add(double value, double[] vector) {
		int size = size(vector);
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = value + vector[i];
		}
		return result;
	}

	/**
	 * Return the sum of a vector.
	 * 
	 * @param a The source vector.
	 * @return The sum.
	 */
	public static double sum(double[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		return sum;
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
}
