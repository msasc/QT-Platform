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
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Internal SAX parser handler that is installed in the parser to handle events and dispatch them to the more convenient
 * <i>ParserHandler</i> class.
 * 
 * @author Miquel Sas
 */
public class InternalParserHandler extends DefaultHandler {

	/**
	 * The <i>ParserHandler</i> used to dispatch events.
	 */
	private ParserHandler handler;
	/**
	 * A stack to manage element names, allowing to know the current element being processed and also get a complete
	 * path to it.
	 */
	private Deque<String> elementStack = new ArrayDeque<>();

	/**
	 * Default constructor.
	 * 
	 * @param handler The handler.
	 */
	public InternalParserHandler(ParserHandler handler) {
		super();
		this.handler = handler;
	}

	/**
	 * Receive notification of the beginning of the document. Calls the handler <i>documentStart</i> method.
	 *
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startDocument
	 */
	@Override
	public void startDocument() throws SAXException {
		handler.documentStart();
	}

	/**
	 * Receive notification of the end of the document. Call the handler <i>documentEnd</i> method.
	 *
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endDocument
	 */
	@Override
	public void endDocument() throws SAXException {
		handler.documentEnd();
	}

	/**
	 * Receive notification of the start of an element. Pushes the element name onto the stack and calls the handler
	 * <i>elementStart</i>.
	 *
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing
	 *        is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing is not being
	 *        performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @param attributes The attributes attached to the element. If there are no attributes, it shall be an empty
	 *        Attributes object.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String element = qName;
		elementStack.push(element);
		handler.elementStart(getElementPrefix(element), getElementName(element), getCurrentPath(), attributes);
	}

	/**
	 * Receive notification of the end of an element. Calls the handler <i>elementEnd</i> method and after that pops the
	 * element name from the stack.
	 *
	 * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing
	 *        is not being performed.
	 * @param localName The local name (without prefix), or the empty string if Namespace processing is not being
	 *        performed.
	 * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String element = qName;
		handler.elementEnd(getElementPrefix(element), getElementName(element), getCurrentPath());
		elementStack.pop();
	}

	/**
	 * Receive notification of character data inside an element. Peeks the current element name and calls the handler
	 * <i>elementBody</i> method.
	 *
	 * @param ch The characters.
	 * @param start The start position in the character array.
	 * @param length The number of characters to use from the character array.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ContentHandler#characters
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String element = elementStack.peek();
		handler.elementBody(
			getElementPrefix(element),
			getElementName(element),
			getCurrentPath(),
			getText(ch, start, length));
	}

	/**
	 * Receive notification of a parser warning.
	 * <p>
	 * The default implementation does nothing. Application writers may override this method in a subclass to take
	 * specific actions for each warning, such as inserting the message in a log file or printing it to the console.
	 * </p>
	 *
	 * @param e The warning information encoded as an exception.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#warning
	 * @see org.xml.sax.SAXParseException
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		handler.warning(e);
	}

	/**
	 * Receive notification of a recoverable parser error.
	 * <p>
	 * The default implementation does nothing. Application writers may override this method in a subclass to take
	 * specific actions for each error, such as inserting the message in a log file or printing it to the console.
	 * </p>
	 *
	 * @param e The error information encoded as an exception.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#warning
	 * @see org.xml.sax.SAXParseException
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		handler.error(e);
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
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#fatalError
	 * @see org.xml.sax.SAXParseException
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		handler.fatalError(e);
	}

	/**
	 * Returns the prefix (namespace) of an element.
	 * 
	 * @param element The XML element.
	 * @return The namespace or prefix part.
	 */
	private String getElementPrefix(String element) {
		int index = element.indexOf(":");
		if (index < 0) {
			return "";
		}
		return element.substring(0, index);
	}

	/**
	 * Returns the element name.
	 * 
	 * @param element The XML element.
	 * @return The name part.
	 */
	private String getElementName(String element) {
		int index = element.indexOf(":");
		if (index < 0) {
			return element;
		}
		return element.substring(index + 1);
	}

	/**
	 * Returns the current path at a given moment in the parsing process.
	 * 
	 * @return The path.
	 */
	private String getCurrentPath() {
		StringBuilder path = new StringBuilder();
		boolean first = true;
		Iterator<String> i = elementStack.descendingIterator();
		while (i.hasNext()) {
			if (first) {
				first = false;
			} else {
				path.append("/");
			}
			String element = i.next();
			path.append(element);
		}
		return path.toString();
	}

	/**
	 * Returns the array part as a string.
	 * 
	 * @param ch The array of characters.
	 * @param start The start index.
	 * @param length The length of the string.
	 * @return The array part as a string.
	 */
	private String getText(char[] ch, int start, int length) {
		StringBuilder text = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			text.append(ch[i]);
		}
		return text.toString();
	}

}
