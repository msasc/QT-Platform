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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Wraps the <i>SAXParser</i> to handler events with the more simple and convenient <i>ParserHandler</i>.
 * 
 * @author Miquel Sas
 */
public class Parser {

	/**
	 * Default constructor.
	 */
	public Parser() {
	}

	/**
	 * Parse the file handling event with the argument parser handler.
	 * 
	 * @param file The XML file to parse.
	 * @param handler The parser handler.
	 * @throws ParserConfigurationException If a config error occurs.
	 * @throws SAXException If a parser error occurs.
	 * @throws IOException If an IO error occurs.
	 */
	public void parse(File file, ParserHandler handler)
		throws ParserConfigurationException, SAXException, IOException {
		parse(new FileInputStream(file), handler);
	}

	/**
	 * Parse the file handling event with the argument parser handler.
	 * 
	 * @param is The input stream.
	 * @param handler The parser handler.
	 * @throws ParserConfigurationException If a config error occurs.
	 * @throws SAXException If a parser error occurs.
	 * @throws IOException If an IO error occurs.
	 */
	public void parse(InputStream is, ParserHandler handler)
		throws ParserConfigurationException, SAXException, IOException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		InternalParserHandler internalHandler = new InternalParserHandler(handler);
		try {
			parser.parse(is, internalHandler);
		} finally {
			is.close();
		}
	}

}
