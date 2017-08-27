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
package com.qtplaf.library.util.xml;

/**
 * Static XML utilities.
 * 
 * @author Miquel Sas
 */
public class XMLUtils {
	/**
	 * Returns the argument input string conveniently escaped.
	 * 
	 * @param in The input string.
	 * @return The escaped output string.
	 */
	public static String getEscaped(String in) {
		StringBuilder out = new StringBuilder();
		int length = in.length();
		for (int i = 0; i < length; i++) {
			char c = in.charAt(i);
			switch (c) {
			case '&':
				out.append("&amp;");
				break;
			case '<':
				out.append("&lt;");
				break;
			case '>':
				out.append("&gt;");
				break;
			case '"':
				out.append("&quot;");
				break;
			case '\'':
				out.append("&apos;");
				break;
			default:
				out.append(c);
				break;
			}
		}
		return out.toString();
	}
}
