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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads number images and stores an array of 'NumberImage' instances. The files read are in MNIST DATABASE format.
 * 
 * @author Miquel Sas
 */
public class NumberImageReader {

	/** Image file. */
	private File imageFile;
	/** Label file. */
	private File labelFile;

	/**
	 * Constructor assigning the files.
	 * 
	 * @param labelFile The label file.
	 * @param imageFile The image file.
	 */
	public NumberImageReader(File labelFile, File imageFile) {
		super();
		this.labelFile = labelFile;
		this.imageFile = imageFile;
	}

	/**
	 * Read the images and stores them in a list of NumberImage instances.
	 * <p>
	 * Image file:
	 * <p>
	 * 32 bit Magic number 32 bit Integer: number of images 32 bit Integer: number of rows 32 bit Integer: number of
	 * columns unsigned byte...
	 * <p>
	 * Label file:
	 * <p>
	 * 32 bit Magic number 32 bit Integer: number of items unsigned byte...
	 * 
	 * @return The list of images.
	 * @throws IOException If an IO error occurs.
	 */
	public List<NumberImage> read() throws IOException {

		List<NumberImage> numberImages = new ArrayList<>();

		FileInputStream fisImg = new FileInputStream(imageFile);
		FileInputStream fisLbl = new FileInputStream(labelFile);
		BufferedInputStream bisImg = new BufferedInputStream(fisImg);
		BufferedInputStream bisLbl = new BufferedInputStream(fisLbl);

		// Process image file up to the first data byte...

		// Skip magic number
		bisImg.skip(4);

		// Read number of images
		byte[] bytesNumImg = new byte[4];
		bisImg.read(bytesNumImg, 0, 4);
		ByteBuffer byteBufferNumImg = ByteBuffer.wrap(bytesNumImg);
		int numImages = byteBufferNumImg.getInt();

		// Read number of rows
		byte[] bytesNumRows = new byte[4];
		bisImg.read(bytesNumRows, 0, 4);
		ByteBuffer byteBufferNumRows = ByteBuffer.wrap(bytesNumRows);
		int numRows = byteBufferNumRows.getInt();

		// Read number of columns
		byte[] bytesNumColumns = new byte[4];
		bisImg.read(bytesNumColumns, 0, 4);
		ByteBuffer byteBufferNumColumns = ByteBuffer.wrap(bytesNumColumns);
		int numColumns = byteBufferNumColumns.getInt();

		// Process label file up to the first data byte...

		// Skip magic number
		bisLbl.skip(4);

		// Read number of labels
		byte[] bytesNumLbl = new byte[4];
		bisLbl.read(bytesNumLbl, 0, 4);
		ByteBuffer byteBufferNumLbl = ByteBuffer.wrap(bytesNumLbl);
		int numLabels = byteBufferNumLbl.getInt();

		// Check number of images versus labels
		if (numImages != numLabels) {
			bisImg.close();
			bisLbl.close();
			throw new IOException(
				"The number of images " + numImages + " has to be equals to the numeber of labels " + numLabels);
		}

		// Read data and create number images
		int imageSize = numRows * numColumns;
		for (int i = 0; i < numImages; i++) {
			byte[] bytes = new byte[imageSize];
			bisImg.read(bytes, 0, imageSize);
			int number = bisLbl.read();
			NumberImage numberImage = new NumberImage(number, bytes);
			numberImages.add(numberImage);
		}

		bisImg.close();
		bisLbl.close();

		return numberImages;
	}
}
