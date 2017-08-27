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

package com.qtplaf.library.util.html;

import com.qtplaf.library.util.Alignment;

/**
 * HTML utilities.
 *
 * @author Miquel Sas
 */
public class HTMLUtils {

	/**
	 * Returns a table header, HTML format, multi-line, alignment center.
	 * 
	 * @param totalLines Number of lines of all headers.
	 * @param lines Header lines.
	 * @return The html header.
	 */
	public static String getTableHeader(int totalLines, String... lines) {
		return getTableHeader(totalLines, Alignment.Center, lines);
	}

	/**
	 * Returns a table header, HTML format, multi-line.
	 * 
	 * @param totalLines Number of lines of all headers.
	 * @param alignment Alinement.
	 * @param lines Header lines.
	 * @return The html header.
	 */
	public static String getTableHeader(int totalLines, Alignment alignment, String... lines) {
		StringBuilder b = new StringBuilder();
		b.append("<html>");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.isEmpty()) {
				line = "&nbsp;";
			}
			switch (alignment) {
			case Left:
				b.append("<left>");
				break;
			case Right:
				b.append("<right>");
				break;
			case Center:
				b.append("<center>");
				break;
			default:
				b.append("<center>");
				break;
			}
			b.append(line);
			if (i < lines.length - 1) {
				b.append("<br>");
			}
		}
		if (lines.length < totalLines) {
			for (int i = lines.length; i < totalLines; i++) {
				b.append("<br>&nbsp;");
			}
		}
		b.append("</html>");
		return b.toString();
	}

}
