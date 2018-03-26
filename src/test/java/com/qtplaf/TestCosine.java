package com.qtplaf;

import com.qtplaf.library.util.math.Matrix;

public class TestCosine {

	public static void main(String[] args) {
		double[] a = new double[] { 2, 1, 1, 2 };
		double[] b = new double[] { 1, 1, 1, 1 };
		System.out.println(Matrix.cosineSimilarity(a, b));
		System.out.println(Matrix.distanceEuclidean(a, b));
	}

}
