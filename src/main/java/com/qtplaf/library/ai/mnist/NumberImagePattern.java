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

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.function.normalize.StdNormalizer;

/**
 * A MNIST number image pattern.
 *
 * @author Miquel Sas
 */
public class NumberImagePattern extends Pattern {

	/** Normalizer. */
	private StdNormalizer normalizer;
	/** Underlying number image. */
	private NumberImage image;
	
	/**
	 * Constructor.
	 * 
	 * @param parent The parent number image pattern.
	 */
	protected NumberImagePattern(NumberImagePattern parent) {
		this(parent.image, parent.normalizer);
	}

	/**
	 * Constructor.
	 * 
	 * @param image The number image.
	 */
	public NumberImagePattern(NumberImage image) {
		this(image, new StdNormalizer(255, 0, 1, -1));
	}

	/**
	 * Constructor.
	 * 
	 * @param image The number image.
	 * @param normalizer The normalizer.
	 */
	public NumberImagePattern(NumberImage image, StdNormalizer normalizer) {
		super();
		this.image = image;
		this.normalizer = normalizer;
		setLabel(Integer.toString(image.getNumber()));
	}

	/**
	 * Return the pattern input.
	 * 
	 * @return The pattern input.
	 */
	@Override
	public double[] getInputs() {
		if (super.getInputs() == null) {
			double[] inputs = new double[NumberImage.ROWS * NumberImage.COLUMNS];
			int index = 0;
			for (int row = 0; row < NumberImage.ROWS; row++) {
				for (int column = 0; column < NumberImage.COLUMNS; column++) {
					double imageByte = image.getImage()[row][column];
					inputs[index++] = normalizer.normalize(imageByte);
				}
			}
			setInputs(inputs);
		}
		return super.getInputs();
	}

	/**
	 * Return the pattern output.
	 * 
	 * @return The pattern output.
	 */
	@Override
	public double[] getOutputs() {
		if (super.getOutputs() == null) {
			StdNormalizer normalizer = new StdNormalizer(1, 0, 1, -1);
			int number = image.getNumber();
			double[] outputs = new double[10];
			int index = 0;
			for (int i = 0; i < number; i++) {
				outputs[index++] = normalizer.normalize(0.0);
			}
			outputs[index++] = normalizer.normalize(1.0);
			for (int i = number + 1; i < 10; i++) {
				outputs[index++] = normalizer.normalize(0.0);
			}
			setOutputs(outputs);
		}
		return super.getOutputs();
	}

	/**
	 * Returns the number image.
	 * 
	 * @return The number image.
	 */
	public NumberImage getImage() {
		return image;
	}
}
