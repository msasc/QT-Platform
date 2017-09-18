package com.qtplaf;

import com.qtplaf.library.util.math.Matrix;

public class TestSimilarity {

	public static void main(String[] args) {
		double[] a = { 1.0, 1.0, 1.0, 0.0 };
		double[] b = { -1.0, -1.0, -1.0, 0.0 };
		System.out.println(Matrix.similarity(a, b));
		System.out.println(Matrix.distanceEuclidean(a, b));
	}
}
