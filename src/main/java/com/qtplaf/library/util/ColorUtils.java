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

import java.awt.Color;

/**
 * Utilities to manage colors.
 * 
 * @author Miquel Sas
 */
public class ColorUtils {

	/** Color AQUA */
	public static final Color AQUA = new Color(51, 204, 204);
	/** Color BLACK */
	public static final Color BLACK = new Color(0, 0, 0);
	/** Color BLUE */
	public static final Color BLUE = new Color(0, 0, 255);
	/** Color BLUE_GREY */
	public static final Color BLUE_GREY = new Color(102, 102, 153);
	/** Color BRIGHT_GREEN */
	public static final Color BRIGHT_GREEN = new Color(0, 255, 0);
	/** Color BROWN */
	public static final Color BROWN = new Color(153, 51, 0);
	/** Color CORAL */
	public static final Color CORAL = new Color(255, 128, 128);
	/** Color CORNFLOWER_BLUE */
	public static final Color CORNFLOWER_BLUE = new Color(153, 153, 255);
	/** Color DARK_BLUE */
	public static final Color DARK_BLUE = new Color(0, 0, 128);
	/** Color DARK_GREEN */
	public static final Color DARK_GREEN = new Color(0, 51, 0);
	/** Color DARK_RED */
	public static final Color DARK_RED = new Color(128, 0, 0);
	/** Color DARK_TEAL */
	public static final Color DARK_TEAL = new Color(0, 51, 102);
	/** Color DARK_YELLOW */
	public static final Color DARK_YELLOW = new Color(128, 128, 0);
	/** Color GOLD */
	public static final Color GOLD = new Color(255, 204, 0);
	/** Color GREEN */
	public static final Color GREEN = new Color(0, 128, 0);
	/** Color GREY_25_PERCENT */
	public static final Color GREY_25_PERCENT = new Color(192, 192, 192);
	/** Color GREY_40_PERCENT */
	public static final Color GREY_40_PERCENT = new Color(150, 150, 150);
	/** Color GREY_50_PERCENT */
	public static final Color GREY_50_PERCENT = new Color(128, 128, 128);
	/** Color GREY_80_PERCENT */
	public static final Color GREY_80_PERCENT = new Color(51, 51, 51);
	/** Color INDIGO */
	public static final Color INDIGO = new Color(51, 51, 153);
	/** Color LAVENDER */
	public static final Color LAVENDER = new Color(204, 153, 255);
	/** Color LEMON_CHIFFON */
	public static final Color LEMON_CHIFFON = new Color(255, 255, 204);
	/** Color LIGHT_BLUE */
	public static final Color LIGHT_BLUE = new Color(51, 102, 255);
	/** Color LIGHT_CORNFLOWER_BLUE */
	public static final Color LIGHT_CORNFLOWER_BLUE = new Color(204, 204, 255);
	/** Color LIGHT_GREEN */
	public static final Color LIGHT_GREEN = new Color(204, 255, 204);
	/** Color LIGHT_ORANGE */
	public static final Color LIGHT_ORANGE = new Color(255, 153, 0);
	/** Color LIGHT_TURQUOISE */
	public static final Color LIGHT_TURQUOISE = new Color(204, 255, 255);
	/** Color LIGHT_YELLOW */
	public static final Color LIGHT_YELLOW = new Color(255, 255, 153);
	/** Color LIME */
	public static final Color LIME = new Color(153, 204, 0);
	/** Color MAROON */
	public static final Color MAROON = new Color(127, 0, 0);
	/** Color OLIVE_GREEN */
	public static final Color OLIVE_GREEN = new Color(51, 51, 0);
	/** Color ORANGE */
	public static final Color ORANGE = new Color(255, 102, 0);
	/** Color ORCHID */
	public static final Color ORCHID = new Color(102, 0, 102);
	/** Color PALE_BLUE */
	public static final Color PALE_BLUE = new Color(153, 204, 255);
	/** Color PINK */
	public static final Color PINK = new Color(255, 0, 255);
	/** Color PLUM */
	public static final Color PLUM = new Color(153, 51, 102);
	/** Color RED */
	public static final Color RED = new Color(255, 0, 0);
	/** Color ROSE */
	public static final Color ROSE = new Color(255, 153, 204);
	/** Color ROYAL_BLUE */
	public static final Color ROYAL_BLUE = new Color(0, 102, 204);
	/** Color SEA_GREEN */
	public static final Color SEA_GREEN = new Color(51, 153, 102);
	/** Color SKY_BLUE */
	public static final Color SKY_BLUE = new Color(0, 204, 255);
	/** Color TAN */
	public static final Color TAN = new Color(255, 204, 153);
	/** Color TEAL */
	public static final Color TEAL = new Color(0, 128, 128);
	/** Color TURQUOISE */
	public static final Color TURQUOISE = new Color(0, 255, 255);
	/** Color VIOLET */
	public static final Color VIOLET = new Color(128, 0, 128);
	/** Color WHITE */
	public static final Color WHITE = new Color(255, 255, 255);
	/** Color YELLOW */
	public static final Color YELLOW = new Color(255, 255, 0);

	/**
	 * Returns the darkness of the color as a factor between 0 and 1, where 1 is the maximum darkness.
	 * 
	 * @param color The check color.
	 * @return The darkness.
	 */
	public static double darkness(Color color) {
		double max = NumberUtils.max(color.getRed(), color.getGreen(), color.getBlue());
		return NumberUtils.round((255 - max) / 255, 1);
	}

	/**
	 * Returns the brightness of the color as a factor between 0 and 1, where 1 is the maximum brightness.
	 * 
	 * @param color The check color.
	 * @return The brightness.
	 */
	public static double brightness(Color color) {
		return 1 - darkness(color);
	}

	/**
	 * Returns a color that is brighter than the argument color by a given factor. The factor must be greater than 0 and
	 * less than 1, and is stronger as it increases. The color 255 - (red, green and blue) are multiplied by factor.
	 * 
	 * @param color The source color.
	 * @param factor The factor.
	 * @return The brighter color.
	 */
	public static Color brighter(Color color, double factor) {
		if (factor <= 0 || factor > 1) {
			throw new IllegalArgumentException("Factor must be > 0 and < 1");
		}
		double darker = Math.min(color.getRed(), Math.min(color.getBlue(), color.getGreen()));
		int increase = (int) NumberUtils.round((255 - darker) * factor, 0);
		int red = Math.min(color.getRed() + increase, 255);
		int green = Math.min(color.getGreen() + increase, 255);
		int blue = Math.min(color.getGreen() + increase, 255);
		return new Color(red, green, blue, color.getAlpha());
	}

	/**
	 * Returns a color that is darker than the argument color by a given factor. The factor must be greater than 0 and
	 * less than 1, and is stronger as it increases. The color red, green and blue are multiplied by (1 - factor).
	 * 
	 * @param color The source color.
	 * @param factor The factor.
	 * @return The darker color.
	 */
	public static Color darker(Color color, double factor) {
		if (factor <= 0 || factor > 1) {
			throw new IllegalArgumentException("Factor must be > 0 and < 1");
		}
		double brighter = Math.max(color.getRed(), Math.max(color.getBlue(), color.getGreen()));
		int decrease = (int) NumberUtils.round(brighter * factor, 0);
		int red = Math.max(color.getRed() - decrease, 0);
		int green = Math.max(color.getGreen() - decrease, 0);
		int blue = Math.max(color.getGreen() - decrease, 0);
		return new Color(red, green, blue, color.getAlpha());
	}

	/**
	 * Convert an hex string with the format FFFF:FFFF:FFFF (POI) into a color.
	 * 
	 * @param hexString An hex string with the format FFFF:FFFF:FFFF
	 * @return The color.
	 */
	public static Color getColor(String hexString) {
		String[] rgb = StringUtils.parse(hexString, ":");
		float max = Integer.decode("0xFFFF").intValue();
		float red = Integer.decode("0x" + rgb[0]).intValue() / max;
		float green = Integer.decode("0x" + rgb[1]).intValue() / max;
		float blue = Integer.decode("0x" + rgb[2]).intValue() / max;
		return new Color(red, green, blue);
	}

}
