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
package com.qtplaf.library.ai.mnist;

/**
 * MINIST sample image data: a 28*28 byte image matrix and its number (0 to 9)
 * 
 * @author Miquel Sas
 */
public class NumberImage {

	/** Image rows. */
	public static final int ROWS = 28;
	/** Image columns. */
	public static final int COLUMNS = 28;

	/** The byte array of 28*28 = 784 elements. */
	private int[][] image;

	/** The number. */
	private int number;

	/**
	 * Constructor assigning the number and the bytes.
	 * 
	 * @param number The represented number
	 * @param bytes The raw bytes list
	 */
	public NumberImage(int number, byte[] bytes) {
		super();
		if (number < 0 || number > 9) {
			throw new IllegalArgumentException("Invalid number " + number);
		}
		if (bytes.length != ROWS * COLUMNS) {
			throw new IllegalArgumentException("Invalid number of bytes per image " + bytes.length);
		}
		this.number = number;
		image = new int[ROWS][COLUMNS];
		int row = 0;
		int column = 0;
		for (int i = 0; i < bytes.length; i++) {
			image[row][column] = 255 - Byte.toUnsignedInt(bytes[i]);
			column++;
			if (column == COLUMNS) {
				column = 0;
				row++;
			}
		}
	}

	/**
	 * Constructor assigning the number and the bytes.
	 * 
	 * @param number The represented number
	 * @param bytes The raw bytes list
	 */
	public NumberImage(int number, int[] bytes) {
		super();
		if (number < 0 || number > 9) {
			throw new IllegalArgumentException("Invalid number " + number);
		}
		if (bytes.length != ROWS * COLUMNS) {
			throw new IllegalArgumentException("Invalid number of bytes per image " + bytes.length);
		}
		this.number = number;
		image = new int[ROWS][COLUMNS];
		int row = 0;
		int column = 0;
		for (int i = 0; i < bytes.length; i++) {
			image[row][column] = bytes[i];
			column++;
			if (column == COLUMNS) {
				column = 0;
				row++;
			}
		}
	}

	/**
	 * Constructor assigning the number and the bytes.
	 * 
	 * @param number The represented number
	 * @param bytes The raw bytes list
	 */
	public NumberImage(int number, double[][] bytes) {
		super();
		if (number < 0 || number > 9) {
			throw new IllegalArgumentException("Invalid number " + number);
		}
		this.number = number;
		image = new int[ROWS][COLUMNS];
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				image[row][col] = Double.valueOf(bytes[row][col]).intValue();
			}
		}
	}

	/**
	 * Returns the number.
	 * 
	 * @return The number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the image as a two dimension byte array.
	 * 
	 * @return The image
	 */
	public int[][] getImage() {
		return image;
	}

	/**
	 * Returns the string representation.
	 */
	@Override
	public String toString() {
		return Integer.toString(getNumber());
	}
}
