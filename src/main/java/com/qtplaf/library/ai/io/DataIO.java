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

package com.qtplaf.library.ai.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Learning method IO class to save/restore learning methods data.
 *
 * @author Miquel Sas
 */
public abstract class DataIO {

	/** A boolean that indicates that data should be deflated/inflated when writing/reading. */
	private boolean deflate = true;

	/**
	 * Constructor.
	 */
	public DataIO() {
		super();
	}

	/**
	 * Check whether data should be deflated/inflated when writing/reading.
	 * 
	 * @return A boolean
	 */
	public boolean isDeflate() {
		return deflate;
	}

	/**
	 * Set whether data should be deflated/inflated when writing/reading.
	 * 
	 * @param deflate A boolean.
	 */
	public void setDeflate(boolean deflate) {
		this.deflate = deflate;
	}

	/**
	 * Parse the XML input stream and fill the network.
	 * 
	 * @param file The input file.
	 * @throws FileNotFoundException If such an error occurs.
	 * @throws ParserConfigurationException If such an error occurs.
	 * @throws SAXException If such an error occurs.
	 * @throws IOException If such an error occurs.
	 */
	public void fromXML(File file)
		throws FileNotFoundException,
		ParserConfigurationException,
		SAXException,
		IOException {
		fromXML(new FileInputStream(file));
	}

	/**
	 * Parse the XML input stream and fill the network.
	 * 
	 * @param is The input stream.
	 * @throws ParserConfigurationException If such an error occurs.
	 * @throws SAXException If such an error occurs.
	 * @throws IOException If such an error occurs.
	 */
	public abstract void fromXML(InputStream is) throws ParserConfigurationException, SAXException, IOException;

	/**
	 * Parse the XML input stream and fill the network.
	 * 
	 * @param file The input file.
	 * @throws FileNotFoundException If such an error occurs.
	 * @throws ParserConfigurationException If such an error occurs.
	 * @throws SAXException If such an error occurs.
	 * @throws IOException If such an error occurs.
	 */
	public void toXML(File file)
		throws FileNotFoundException,
		ParserConfigurationException,
		SAXException,
		IOException {
		toXML(new FileOutputStream(file));
	}

	/**
	 * Write the network to an output stream in XML format.
	 * 
	 * @param os The output stream.
	 * @throws UnsupportedEncodingException If such an error occurs.
	 */
	public abstract void toXML(OutputStream os) throws UnsupportedEncodingException;

	/**
	 * Returns the appropriate input stream depending whether data should be deflated/inflated when writing/reading.
	 * 
	 * @param is The source input stream.
	 * @return The target input stream.
	 */
	protected InputStream getInputStream(InputStream is) {
		return (isDeflate() ? new InflaterInputStream(is) : is);
	}

	/**
	 * Returns the appropriate output stream depending whether data should be deflated/inflated when writing/reading.
	 * 
	 * @param os The source output stream.
	 * @return The target output stream.
	 */
	protected OutputStream getOutputStream(OutputStream os) {
		return (isDeflate() ? new DeflaterOutputStream(os) : os);
	}
}
