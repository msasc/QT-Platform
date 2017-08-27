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

package com.qtplaf.utilities.oracle;

/**
 * Data of search and replace.
 *
 * @author Miquel Sas
 */
public class Data {

	/**
	 * Returns the bytes of the string as an int[]
	 * 
	 * @param s The string.
	 * @return The list of bytes.
	 */
	private static int[] getBytes(String s) {
		byte[] bytes = s.getBytes();
		int[] ints = new int[bytes.length];
		for (int i = 0; i < ints.length; i++) {
			ints[i] = bytes[i];
		}
		return ints;
	}

	/** Search string. */
	private String searchString;
	/** Search bytes. */
	private int[] searchBytes;
	/** Replace string. */
	private String replaceString;
	/** Replace bytes. */
	private int[] replaceBytes;
	/** NOENC string. */
	private String noencString;
	/** NOENC bytes. */
	private int[] noencBytes;

	/**
	 * A pair of search and replace.
	 * 
	 * @param searchString The search string.
	 * @param replaceString The replace string.
	 * @param noencString The noenc string.
	 */
	public Data(String searchString, String replaceString, String noencString) {
		super();
		this.searchString = searchString;
		this.searchBytes = getBytes(searchString);
		this.replaceString = replaceString;
		this.replaceBytes = getBytes(replaceString);
		this.noencString = noencString;
		this.noencBytes = getBytes(noencString);
	}

	/**
	 * Return the search string.
	 * 
	 * @return The search string.
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * Return the search bytes.
	 * 
	 * @return The search bytes.
	 */
	public int[] getSearchBytes() {
		return searchBytes;
	}

	/**
	 * Return the replace string.
	 * 
	 * @return The replace string.
	 */
	public String getReplaceString() {
		return replaceString;
	}

	/**
	 * Return the replace bytes.
	 * 
	 * @return The replace bytes.
	 */
	public int[] getReplaceBytes() {
		return replaceBytes;
	}

	/**
	 * Return the noenc string.
	 * 
	 * @return The noenc string.
	 */
	public String getNoencString() {
		return noencString;
	}

	/**
	 * Return the noenc bytes.
	 * 
	 * @return The noenc bytes.
	 */
	public int[] getNoencBytes() {
		return noencBytes;
	}

	/**
	 * Return a string representation.
	 */
	@Override
	public String toString() {
		return "replace: " + replaceString + " search: " + searchString;
	}
}
