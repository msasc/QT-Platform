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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Number utilities extended from Apache Commons Lang.
 *
 * @author Miquel Sas
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
	
	/**
	 * Maximum positive double.
	 */
	public static final double MAX_DOUBLE = Double.MAX_VALUE;
	/**
	 * Minimum negative double.
	 */
	public static final double MIN_DOUBLE = -Double.MAX_VALUE;
	/**
	 * Maximum positive integer.
	 */
	public static final int MAX_INTEGER = Integer.MAX_VALUE;
	/**
	 * Minimum negative integer.
	 */
	public static final int MIN_INTEGER = -Integer.MAX_VALUE;

	/**
	 * Hexadecimal chars.
	 */
	public static final String HEX_CHARS = "0123456789ABCDEF";

	/**
	 * Converts a byte to an HEX string of two bytes (FF).
	 * 
	 * @param b The byte to convert.
	 * @return The HEX string.
	 */
	public static String toHexString(byte b) {
		return StringUtils.leftPad(Integer.toHexString(b & 0xff).toUpperCase(), 2, "0");
	}

	/**
	 * Check if the number is even.
	 * 
	 * @param l The number.
	 * @return A boolean.
	 */
	public static boolean isEven(long l) {
		return (l % 2 == 0);
	}

	/**
	 * Check if the number is odd.
	 * 
	 * @param l The number.
	 * @return A boolean.
	 */
	public static boolean isOdd(long l) {
		return !isEven(l);
	}

	/**
	 * Parse a two char byte hex string.
	 * 
	 * @param hex The hex string.
	 * @return The byte.
	 */
	public static byte parseByte(String hex) {
		return (byte) Integer.parseInt(hex, 16);
	}

	/**
	 * Round a number (in mode that most of us were taught in grade school).
	 * 
	 * @param value The value to round.
	 * @param decimals The number of decimal places.
	 * @return The rounded value.
	 */
	public static double round(double value, int decimals) {
		double p = java.lang.Math.pow(10, decimals);
		double v = value * p;
		long l = java.lang.Math.round(v);
		double r = l / p;
		// Ensure exact decimals because sometimes floating point operations yield numbers like 0.49999999999 for 0.5
		return new BigDecimal(r).setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * Returns the default string number with the given decimal places. For instance, returns '0.00000' for 5 decimal
	 * places.
	 * 
	 * @param decimals The number of decimal places.
	 * @return The default string number.
	 */
	public static String defaultStringNumber(int decimals) {
		StringBuilder b = new StringBuilder();
		b.append("0");
		if (decimals > 0) {
			b.append(".");
			b.append(StringUtils.repeat("0", decimals));
		}
		return b.toString();
	}

	/**
	 * Returns the big decimal for the value and scale.
	 * 
	 * @param value The value.
	 * @param decimals The number of decimal places.
	 * @return The big decimal.
	 */
	public static BigDecimal getBigDecimal(double value, int decimals) {
		return new BigDecimal(value).setScale(decimals, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Verifies and corrects numeric format.
	 *
	 * @return The result string.
	 * @param str The source string.
	 */
	public static String numberFormat(String str) {
		// Strip spaces at both ends.
		str = str.trim();
		// Sign can be on the left and on the right. if its on the left, there can be
		// spaces between the sign and the number itself.
		int len = str.length();
		boolean sign = str.charAt(0) == '-'
			|| str.charAt(0) == '+'
			|| str.charAt(len - 1) == '-'
			|| str.charAt(len - 1) == '+';
		// If there is a sign, put it in the right place.
		if (sign) {
			if (str.charAt(0) == '-' || str.charAt(0) == '+') {
				if (str.charAt(1) == ' ') {
					str = str.substring(0, 1) + str.substring(1).trim();
				}
			} else {
				str = str.substring(len - 1, len) + str.substring(0, len - 1);
			}
			// Strip positive sign.
			if (str.charAt(0) == '+') {
				str = str.substring(1);
			}
		}
		return str;
	}

	/**
	 * Returns the remainder of the division of two integers.
	 * 
	 * @param numerator The numerator.
	 * @param denominator The denominator.
	 * @return The remainder.
	 */
	public static int remainder(int numerator, int denominator) {
		return numerator % denominator;
	}

	/**
	 * Returns a boolean indicating if the number is leap.
	 * 
	 * @param number The integer to check.
	 * @return A boolean that indicates if the number is leap.
	 */
	public static boolean isLeap(int number) {
		return remainder(number, 2) == 0;
	}

	/**
	 * Returns a boolean indicating if the number is odd.
	 * 
	 * @param number The integer to check.
	 * @return A boolean that indicates if the number is odd.
	 */
	public static boolean isOdd(int number) {
		return !isLeap(number);
	}

	/**
	 * Returns the number of integer digits of a number.
	 * 
	 * @param number The number to check.
	 * @return The number of integer digits.
	 */
	public static int getDigits(double number) {
		String str = new BigDecimal(number).toPlainString();
		int index = str.indexOf('.');
		if (index <= 0) {
			return str.length();
		}
		return index;
	}

	/**
	 * Returns a list of increases to apply.
	 * 
	 * @param integerDigits The number of integer digits.
	 * @param decimalDigits The numbeer of decimal digits.
	 * @param multipliers The list of multipliers.
	 * @return The list of increases.
	 */
	public static List<BigDecimal> getIncreases(int integerDigits, int decimalDigits, int... multipliers) {
		List<BigDecimal> increaments = new ArrayList<>();
		int upperScale = decimalDigits;
		int lowerScale = (integerDigits - 1) * (-1);
		for (int scale = upperScale; scale >= lowerScale; scale--) {
			for (int multiplier : multipliers) {
				BigDecimal value = NumberUtils.getBigDecimal(Math.pow(10, -scale), scale);
				BigDecimal multiplicand = new BigDecimal(multiplier).setScale(0, BigDecimal.ROUND_HALF_UP);
				increaments.add(value.multiply(multiplicand));
			}
		}
		return increaments;
	}

	/**
	 * Returns the floor number to the given decimal places. The decimal places can be negative.
	 * 
	 * @param number The source number.
	 * @param decimals The number of decimal places.
	 * @return The floor.
	 */
	public static double floor(double number, int decimals) {
		double pow = number * Math.pow(10, decimals);
		double floor = Math.floor(pow);
		double value = floor / Math.pow(10, decimals);
		return value;
	}

	/**
	 * Returns a string key by concatenating the list of values, applying the scale and sign (1 for positive, 0 for
	 * negative).
	 * 
	 * @param values The list of values.
	 * @param scale The scale.
	 * @return The string key.
	 */
	public static String getStringKey(double[] values, int scale) {
		StringBuilder b = new StringBuilder();
		for (double value : values) {
			String sign = (value >= 0 ? "1" : "0");
			b.append(sign);
			b.append(StringUtils.remove(getBigDecimal(Math.abs(value), scale).toPlainString(), '.'));
		}
		return b.toString();
	}

	/**
	 * Check if the number is in the list of numbers.
	 * 
	 * @param number The number to check.
	 * @param numbers The list of numbers.
	 * @return A boolean.
	 */
	public static boolean in(Number number, Number... numbers) {
		for (Number n : numbers) {
			if (n.equals(number)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the division or zero if the denominator is zero.
	 * 
	 * @param numerator The numerator.
	 * @param denominator The denominator.
	 * @return The division or zero if the denominator is zero.
	 */
	public static double zeroDiv(double numerator, double denominator) {
		return (denominator == 0 ? 0 : numerator / denominator);
	}
}
