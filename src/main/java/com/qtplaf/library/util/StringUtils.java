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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;

/**
 * String utilities extended from Apache Commons Lang.
 *
 * @author Miquel Sas
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * Sample list of digits to generate random digits.
	 */
	public static final String DIGITS = "0123456789";
	/**
	 * Sample list of letters to generate random letters.
	 */
	public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * Returns a random char within the source string.
	 * 
	 * @param source The source string.
	 * @return The random char.
	 */
	public static char getRandomChar(String source) {
		int index = Random.nextInt(source.length());
		return source.charAt(index);
	}

	/**
	 * Returns the stack trace as a string.
	 * 
	 * @param exc The exception.
	 * @return The stack trace as a string.
	 */
	public static String getStackTrace(Exception exc) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exc.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * Separates with a blank the different tokens that can compose a normal class or method name, like for instance
	 * doSomething into Do something.
	 *
	 * @param str The string to separate.
	 * @return The separated string.
	 */
	public static String separeTokens(String str) {
		StringBuilder b = new StringBuilder();
		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				if (i == 0) {
					b.append(Character.toUpperCase(str.charAt(i)));
				} else {
					if (Character.isLowerCase(str.charAt(i - 1)) && Character.isUpperCase(str.charAt(i))) {
						b.append(' ');
						if ((i < str.length() - 1) && Character.isUpperCase(str.charAt(i + 1))) {
							b.append(str.charAt(i));
						} else {
							b.append(Character.toLowerCase(str.charAt(i)));
						}
					} else {
						b.append(str.charAt(i));
					}
				}
			}
		}
		return b.toString();
	}

	/**
	 * Parse a comma delimited "XX","XX","XX" string, and return an array with its elements.
	 *
	 * @param strToParse The String to parse.
	 * @return An array of strings that are the parts of the comma delimited string to parse.
	 */
	public static String[] parseCommaDelimitedString(String strToParse) {
		if (strToParse.length() == 0) {
			return new String[0];
		}
		int start = 0;
		while (strToParse.charAt(start) != '\"') {
			start++;
		}
		int end = strToParse.length() - 1;
		while (strToParse.charAt(end) != '\"') {
			end--;
		}
		strToParse = strToParse.substring(++start, end);
		ArrayList<String> list = new ArrayList<>();
		start = 0;
		while (true) {
			end = strToParse.indexOf("\",\"", start);
			if (end == -1) {
				list.add(strToParse.substring(start));
				break;
			}
			list.add(strToParse.substring(start, end));
			start = end + 3;
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Utility to parse command line arguments in the form of arg=value. Returns a map with the arguments as pairs of
	 * keys/values.
	 *
	 * @param arguments The array of command line arguments.
	 * @return A map with pairs of argument/value.
	 */
	public static HashMap<String, String> parseArgs(String[] arguments) {
		HashMap<String, String> map = new HashMap<>();
		if (arguments != null) {
			for (String argument : arguments) {
				String[] parts = split(argument, "=");
				if (parts.length == 0) {
					continue;
				}
				String arg = parts[0];
				String val = arg;
				if (parts.length > 1) {
					val = parts[1];
				}
				map.put(arg, val);
			}
		}
		return map;
	}

	/**
	 * Parses a string of the form "en-US,es-ES,fr,FR" or "en_US,es_ES,fr_FR" into an array of locales.
	 *
	 * @param str The argument string.
	 * @return The parsed array of locales.
	 */
	public static Locale[] parseLocales(String str) {
		ArrayList<Locale> locales = new ArrayList<>();
		String[] strLoc = split(str, ",");
		for (String strLoc1 : strLoc) {
			Locale locale = parseLocale(strLoc1);
			if (locale == null) {
				continue;
			}
			locales.add(locale);
		}
		return locales.toArray(new Locale[locales.size()]);
	}

	/**
	 * Parse a locale in the form "en-US" or "en_US"
	 *
	 * @param str The locale string
	 * @return The locale or null.
	 */
	public static Locale parseLocale(String str) {
		String[] parts = split(str.trim(), "_-");
		if (parts.length == 1) {
			return new Locale(parts[0].trim());
		}
		if (parts.length >= 2) {
			return new Locale(parts[0].trim(), parts[1].trim());
		}
		return null;
	}

	/**
	 * Parse a comma separated list of strings "S1, S2, S3".
	 *
	 * @param string The string to be tokenized.
	 * @return the array of tokens.
	 */
	public static String[] parseCommaSeparatedStrings(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, ",");
		ArrayList<String> list = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().trim());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Parse a string
	 * 
	 * @param string The string to parse.
	 * @param separator The separator.
	 * @return the array of tokens
	 */
	public static String[] parse(String string, String separator) {
		StringTokenizer tokenizer = new StringTokenizer(string, separator);
		ArrayList<String> list = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().trim());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Converts a String to a new String using a selected charset.
	 *
	 * @param in The input String
	 * @param charset The charset to convert.
	 * @return A String.
	 * @throws UnsupportedEncodingException If the char set does not exist.
	 */
	public static String encodeString(String in, String charset) throws UnsupportedEncodingException {
		return new String(in.getBytes(charset));
	}

	/**
	 * This method unescapes a message. It is useful to decode the request arguments in a HTTP Query String or a HTTP
	 * form URL encoded request.
	 *
	 * @param msg The String to be unescaped.
	 * @param charsetName The charset to be used.
	 * @return the unescaped String
	 * @throws UnsupportedEncodingException If the char set does not exist.
	 */
	public static String toUnescaped(String msg, String charsetName) throws UnsupportedEncodingException {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (c == '%') {
				String hex = msg.substring(i + 1, i + 3);
				list.add(Integer.valueOf(hex, 16));
				i += 2;
			} else {
				list.add(Integer.valueOf((byte) c));
			}
		}
		byte[] buff = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			buff[i] = list.get(i).byteValue();
		}
		return new String(buff, charsetName);
	}

	/**
	 * This method unescapes a message. It is useful to decode the request arguments in a HTTP Query String or a HTTP
	 * form URL encoded request.
	 *
	 * @param bytes The byte array to be unescaped.
	 * @param charsetName The charset to be used.
	 * @return the unescaped String
	 * @throws UnsupportedEncodingException If the char set does not exist.
	 */
	public static String toUnescaped(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
		return toUnescaped(new String(bytes), charsetName);
	}

	/**
	 * This method escapes a message. It is useful to encode the request arguments in a HTTP GET request method.
	 *
	 * @param msg The String to be escaped.
	 * @param charsetName The charset to be used.
	 * @return the escaped String
	 * @throws UnsupportedEncodingException In msg.getBytes(charsetName)
	 */
	public static String toEscaped(String msg, String charsetName) throws UnsupportedEncodingException {
		byte[] buff = msg.getBytes(charsetName);
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < buff.length; i++) {
			int c = buff[i];
			if (((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c < 'z'))) {
				b.append((char) c);
			} else {
				b.append('%');
				b.append(Integer.toHexString(c & 0xff).toUpperCase());
			}
		}
		return b.toString();
	}

	/**
	 * Returns the first string not null, or an empty string.
	 * 
	 * @param strings The list of strings.
	 * @return The first string not null, or an empty string.
	 */
	public static String getFirstNotNull(String... strings) {
		StringBuilder b = new StringBuilder();
		for (String s : strings) {
			if (s != null) {
				b.append(s);
				break;
			}
		}
		return b.toString();
	}

	/**
	 * Returns Levenshtein distance between two strings, useful to mesure the similarity of those strings.
	 * 
	 * @param s Start string.
	 * @param t Target string.
	 * @return An integer that mesures the Levenshtein distance.
	 */
	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new NullPointerException();
		}

		/*
		 * The difference between this implementation and the previous is that, rather than creating and retaining a
		 * matrix of size s.length()+1 by t.length()+1, we maintain two single-dimensional arrays of length
		 * s.length()+1. The first, d, is the 'current working' distance array that maintains the newest distance cost
		 * counts as we iterate through the characters of String s. Each time we increment the index of String t we are
		 * comparing, d is copied to p, the second int[]. Doing so allows us to retain the previous cost counts as
		 * required by the algorithm (taking the minimum of the cost count to the left, up one, and diagonally up and to
		 * the left of the current cost count being calculated). (Note that the arrays aren't really copied anymore,
		 * just switched...this is clearly much better than cloning an array or doing a System.arraycopy() each time
		 * through the outer loop.)
		 * 
		 * Effectively, the difference between the two implementations is this one does not cause an out of memory
		 * condition when calculating the LD over two very large strings.
		 */
		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n + 1]; // 'previous' cost array, horizontally
		int d[] = new int[n + 1]; // cost array, horizontally
		int _d[]; // placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}

	/**
	 * Returns a string representation of a key stroke.
	 * 
	 * @param keyStroke The key stroke.
	 * @return The string representation.
	 */
	public static String toString(KeyStroke keyStroke) {
		String[] tokens = split(keyStroke.toString(), " ");
		StringBuilder b = new StringBuilder();
		for (String token : tokens) {
			if (token.equals("typed") || token.equals("released") || token.equals("pressed")) {
				continue;
			}
			if (b.length() > 0) {
				b.append(" ");
			}
			b.append(capitalize(token.toLowerCase()));
		}
		return b.toString();
	}

	/**
	 * Check if the string is contained in the list of options.
	 * 
	 * @param string The source string.
	 * @param options The list of options.
	 * @return A boolean.
	 */
	public static boolean in(String string, String... options) {
		for (String option : options) {
			if (option.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the string is contained in the list of options, case insensitive.
	 * 
	 * @param string The source string.
	 * @param options The list of options.
	 * @return A boolean.
	 */
	public static boolean inNoCase(String string, String... options) {
		string = string.toLowerCase();
		for (String option : options) {
			if (option.toLowerCase().equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Append if not null.
	 * 
	 * @param b The string builder.
	 * @param o The object to add.
	 */
	public static void append(StringBuilder b, Object o) {
		if (o != null) {
			b.append(o);
		}
	}

	/**
	 * Append separated if not null, inserting the separator if the buffer is not empty.
	 * 
	 * @param b The string builder.
	 * @param o The object to add.
	 * @param sep The separator.
	 */
	public static void appendSep(StringBuilder b, Object o, String sep) {
		if (o != null) {
			if (b.length() > 0) {
				b.append(sep);
			}
			b.append(o);
		}
	}

	/**
	 * Parse and capitalize.
	 * 
	 * @param srcStr Source string.
	 * @param srcSep Source separator.
	 * @param dstSep Destination separator.
	 * @return The capitalized string.
	 */
	public static String parseCapitalize(String srcStr, String srcSep, String dstSep) {
		StringBuilder b = new StringBuilder();
		String[] words = StringUtils.parse(srcStr, srcSep);
		for (int i = 0; i < words.length; i++) {
			if (i > 0) {
				b.append(dstSep);
			}
			b.append(StringUtils.capitalize(words[i]));
		}
		return b.toString();
	}

	/**
	 * Returns the suffix given the separator.
	 * 
	 * @param src The source string.
	 * @param sep The seprator.
	 * @return The suffix.
	 */
	public static String getSuffix(String src, String sep) {
		int index = src.lastIndexOf(sep);
		return src.substring(index + 1);
	}

	/**
	 * Returns the time information string (seconds,minutes or hours).
	 * 
	 * @param session Working session.
	 * @param time The time in millis.
	 * @return The time info.
	 */
	public static String getTimeString(Session session, double time) {
		int decimals = 1;
		double seconds = (time / 1000.0);
		if (seconds < 60) {
			StringBuilder b = new StringBuilder();
			b.append(FormatUtils.formattedFromDouble(seconds, decimals, session.getLocale()));
			b.append(" ");
			b.append(session.getString("tokenSeconds").toLowerCase());
			return b.toString();
		}
		double minutes = (time / (1000.0 * 60.0));
		if (minutes < 60) {
			StringBuilder b = new StringBuilder();
			b.append(FormatUtils.formattedFromDouble(minutes, decimals, session.getLocale()));
			b.append(" ");
			b.append(session.getString("tokenMinutes").toLowerCase());
			return b.toString();
		}
		double hours = (time / (1000.0 * 60.0 * 60.0));
		StringBuilder b = new StringBuilder();
		b.append(FormatUtils.formattedFromDouble(hours, decimals, session.getLocale()));
		b.append(" ");
		b.append(session.getString("tokenHours").toLowerCase());
		return b.toString();
	}

	/**
	 * Return the string representation of the vector.
	 * 
	 * @param a The vector.
	 * @return The string representation.
	 */
	public static String toString(char[] a) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(a[i]);
		}
		return b.toString();
	}

	/**
	 * Return the string representation of the vector.
	 * 
	 * @param a The vector.
	 * @return The string representation.
	 */
	public static String toString(double[] a) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(a[i]);
		}
		return b.toString();
	}

}
