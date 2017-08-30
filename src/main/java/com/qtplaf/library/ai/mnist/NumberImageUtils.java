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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.qtplaf.library.ai.data.ListPatternSource;
import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.util.file.FileUtils;

/**
 * Utilities to manage MNIST number images.
 *
 * @author Miquel Sas
 */
public class NumberImageUtils {

	/** Check labels file name. */
	public static final String TEST_LABELS = "t10k-labels.idx1-ubyte";
	/** Check images file name. */
	public static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
	/** Learn labels file name. */
	public static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
	/** Learn images file name. */
	public static final String TRAIN_IMAGES = "train-images.idx3-ubyte";

	/**
	 * Returns the list of number images given the labels and the images file names.
	 * 
	 * @param labelsFileName The labels file name.
	 * @param imagesFileName The images file name.
	 * @return The list of number images instances.
	 * @throws IOException If such an error occurs.
	 */
	public static List<NumberImage> getNumberImages(String labelsFileName, String imagesFileName)
		throws IOException {
		File fileLabel = FileUtils.getFileFromClassPathEntries(labelsFileName);
		File fileImage = FileUtils.getFileFromClassPathEntries(imagesFileName);
		NumberImageReader reader = new NumberImageReader(fileLabel, fileImage);
		return reader.read();
	}

	/**
	 * Returns the list of number images used to test.
	 * 
	 * @return The list of number images used to test.
	 * @throws IOException If such an error occurs.
	 */
	public static List<NumberImage> getNumberImagesTest() throws IOException {
		return getNumberImages(TEST_LABELS, TEST_IMAGES);
	}

	/**
	 * Returns the list of number images used to train.
	 * 
	 * @return The list of number images used to train.
	 * @throws IOException If such an error occurs.
	 */
	public static List<NumberImage> getNumberImagesTrain() throws IOException {
		return getNumberImages(TRAIN_LABELS, TRAIN_IMAGES);
	}

	/**
	 * From the origin list of images, return a list of size with equal number of images per number.
	 * 
	 * @param images The source list of images.
	 * @param size The result size.
	 * @return The list of size.
	 */
	public static List<NumberImage> getNumberImages(List<NumberImage> images, int size) {
		List<NumberImage> imagesSize = new ArrayList<>();
		int numberSize = size / 10;
		Random random = new Random();
		for (int number = 0; number < 10; number++) {
			List<NumberImage> imagesNumber = NumberImageUtils.getImagesOfNumber(images, number);
			for (int i = 0; i < numberSize; i++) {
				int index = random.nextInt(imagesNumber.size());
				imagesSize.add(imagesNumber.get(index));
			}
		}
		return imagesSize;
	}

	/**
	 * Returns the list of images of the number.
	 * 
	 * @param images The global list of images.
	 * @param number The number.
	 * @return The list of images of the number.
	 */
	public static List<NumberImage> getImagesOfNumber(List<NumberImage> images, int number) {
		List<NumberImage> numberImages = new ArrayList<>();
		for (NumberImage image : images) {
			if (image.getNumber() == number) {
				numberImages.add(image);
			}
		}
		return numberImages;
	}

	/**
	 * Returns the patter source of train images.
	 * 
	 * @param bipolar A boolean that indicates if normalization is bipolar.
	 * @return The pattern source.
	 * @throws IOException If such an error occurs.
	 */
	public static PatternSource getPatternSourceTrain(boolean bipolar) throws IOException {
		return getPatternSource(getNumberImagesTrain(), bipolar);
	}

	/**
	 * Returns the patter source of test images.
	 * 
	 * @param bipolar A boolean that indicates if normalization is bipolar.
	 * @return The pattern source.
	 * @throws IOException If such an error occurs.
	 */
	public static PatternSource getPatternSourceTest(boolean bipolar) throws IOException {
		return getPatternSource(getNumberImagesTest(), bipolar);
	}

	/**
	 * Returns the pattern source given the list of images.
	 * 
	 * @param images The list of number images.
	 * @param bipolar A boolean that indicates if normalization is bipolar.
	 * @return The patter source.
	 */
	public static PatternSource getPatternSource(List<NumberImage> images, boolean bipolar) {
		return new ListPatternSource(getPatternList(images, bipolar));
	}

	/**
	 * Returns the list of patterns given the list of images.
	 * 
	 * @param images The list of images.
	 * @param bipolar A boolean that indicates if normalization is bipolar.
	 * @return The list of patterns.
	 */
	public static List<Pattern> getPatternList(List<NumberImage> images, boolean bipolar) {
	List<Pattern> patterns = new ArrayList<>();
		for (NumberImage image : images) {
			patterns.add(new NumberImagePattern(image, bipolar));
		}
		return patterns;
	}

}
