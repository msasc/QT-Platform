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

import java.util.ArrayDeque;
import java.util.Deque;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Miquel Sas
 */
public class ParserHandler {

	/**
	 * The deque with the different objects.
	 */
	private Deque<Object> deque = new ArrayDeque<>();

	/**
	 * Default constructor.
	 */
	public ParserHandler() {
		super();
	}

	/**
	 * Returns the deque used to push, peek and pop objects.
	 * 
	 * @return The deque.
	 */
	public Deque<Object> getDeque() {
		return deque;
	}

	/**
	 * Receive notification of the beginning of the document.
	 *
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void documentStart() throws SAXException {
	}

	/**
	 * Receive notification of the end of the document.
	 *
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void documentEnd() throws SAXException {
	}

	/**
	 * Receive notification of the start of an element.
	 *
	 * @param namespace The name space if present.
	 * @param elementName The name of the element, without the prefix.
	 * @param path The path from the root to the current element.
	 * @param attributes The attributes attached to the element. If there are no attributes, they are empty.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void elementStart(String namespace, String elementName, String path, Attributes attributes) throws SAXException {
	}

	/**
	 * Receive notification about the body text of an element.
	 * 
	 * @param namespace The name space if present.
	 * @param elementName The name of the element, without the prefix.
	 * @param path The path from the root to the current element.
	 * @param text The text in the body.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void elementBody(String namespace, String elementName, String path, String text) throws SAXException {
	}

	/**
	 * Receive notification about the end of an element.
	 * 
	 * @param namespace The name space if present.
	 * @param elementName The name of the element, without the prefix.
	 * @param path The path from the root to the current element.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void elementEnd(String namespace, String elementName, String path) throws SAXException {
	}

	/**
	 * Receive notification of a parser warning.
	 * <p>
	 * The default implementation does nothing. Application writers may override this method in a subclass to take
	 * specific actions for each warning, such as inserting the message in a log file or printing it to the console.
	 * </p>
	 *
	 * @param e The warning information encoded as an exception.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void warning(SAXParseException e) throws SAXException {
	}

	/**
	 * Receive notification of a recoverable parser error.
	 * <p>
	 * The default implementation does nothing. Application writers may override this method in a subclass to take
	 * specific actions for each error, such as inserting the message in a log file or printing it to the console.
	 * </p>
	 *
	 * @param e The error information encoded as an exception.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void error(SAXParseException e) throws SAXException {
	}

	/**
	 * Report a fatal XML parsing error.
	 * <p>
	 * The default implementation throws a SAXParseException. Application writers may override this method in a subclass
	 * if they need to take specific actions for each fatal error (such as collecting all of the errors into a single
	 * report): in any case, the application must stop all regular processing when this method is invoked, since the
	 * document is no longer reliable, and the parser may no longer report parsing events.
	 * </p>
	 *
	 * @param e The error information encoded as an exception.
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}
}
