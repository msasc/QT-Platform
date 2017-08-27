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

package com.qtplaf.library.ai.io.learning;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.ai.io.DataIO;
import com.qtplaf.library.ai.learning.propagation.ResilientPropagation;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.ParserHandler;
import com.qtplaf.library.util.xml.XMLAttribute;
import com.qtplaf.library.util.xml.XMLWriter;

/**
 * Resilient propagation IO class to save/restore learning data.
 *
 * @author Miquel Sas
 */
public class ResilientPropagationIO extends DataIO {
	/**
	 * XML parser handler to read a network.
	 */
	class Handler extends ParserHandler {

		int layerIndex;

		/**
		 * Called to notify an element start.
		 */
		@Override
		public void elementStart(String namespace, String elementName, String path, Attributes attributes)
			throws SAXException {
			try {

				// element: learning-data/type (not restored to preserve the parent rprop type)
				if (path.equals("learning-data/type")) {
//					String typeName = attributes.getValue("name");
//					RProp.Type type = RProp.Type.valueOf(typeName);
//					rprop.setType(type);
					return;
				}

				// element: layers
				if (path.equals("learning-data/layers")) {
					return;
				}

				// element: layers/layer
				if (path.equals("learning-data/layers/layer")) {
					layerIndex = Integer.parseInt(attributes.getValue("index"));
					return;
				}

				// element: layers/layer/last-gradients/lg
				if (path.equals("learning-data/layers/layer/last-gradients/lg")) {
					double lastGradient = Double.parseDouble(attributes.getValue("v"));
					int out = Integer.parseInt(attributes.getValue("o"));
					int in = Integer.parseInt(attributes.getValue("i"));
					rprop.getLastGradients(layerIndex)[out][in] = lastGradient;
					return;
				}

				// element: layers/layer/last-weight-changes/lc
				if (path.equals("learning-data/layers/layer/last-weight-changes/lc")) {
					double lastWeightChange = Double.parseDouble(attributes.getValue("v"));
					int out = Integer.parseInt(attributes.getValue("o"));
					int in = Integer.parseInt(attributes.getValue("i"));
					rprop.getLastWeightChanges(layerIndex)[out][in] = lastWeightChange;
					return;
				}

				// element: layers/layer/last-deltas/ld
				if (path.equals("learning-data/layers/layer/last-deltas/ld")) {
					double lastDelta = Double.parseDouble(attributes.getValue("v"));
					int out = Integer.parseInt(attributes.getValue("o"));
					int in = Integer.parseInt(attributes.getValue("i"));
					rprop.getLastDeltas(layerIndex)[out][in] = lastDelta;
					return;
				}

			} catch (NumberFormatException exc) {
				throw new SAXException(exc);
			}
		}
	}

	/** RPROP. */
	private ResilientPropagation rprop;

	/**
	 * Constructor.
	 * 
	 * @param rprop The resilient propagation learning method.
	 */
	public ResilientPropagationIO(ResilientPropagation rprop) {
		super();
		this.rprop = rprop;
	}

	/**
	 * Parse the XML input stream and fill the network.
	 * 
	 * @param is The input stream.
	 * @throws ParserConfigurationException If such an error occurs.
	 * @throws SAXException If such an error occurs.
	 * @throws IOException If such an error occurs.
	 */
	@Override
	public void fromXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		Handler handler = new Handler();
		Parser parser = new Parser();
		parser.parse(getInputStream(is), handler);
	}

	/**
	 * Write the network to an output stream in XML format.
	 * 
	 * @param os The output stream.
	 * @throws UnsupportedEncodingException If such an error occurs.
	 */
	@Override
	public void toXML(OutputStream os) throws UnsupportedEncodingException {
		XMLWriter wr = new XMLWriter(getOutputStream(os));

		// Header.
		wr.printHeader();

		// Learning-data tag
		wr.printTagStart("learning-data");
		wr.increaseTabLevel();

		// RPROP type, save only for information purposes, not restored, preserved the type of the parent resilient
		// propagation.
		XMLAttribute type = new XMLAttribute("name", rprop.getType().name());
		wr.printTag("type", type);

		// Number of layers. All data is for layer 1 and subsequents.
		int layers = rprop.getNetwork().getLayers();

		// Layers tag.
		wr.printTagStart("layers");
		wr.increaseTabLevel();

		int pos;
		for (int layer = 1; layer < layers; layer++) {
			XMLAttribute index = new XMLAttribute("index", layer);
			wr.printTagStart("layer", index);
			wr.increaseTabLevel();

			int neuronsOut = rprop.getNetwork().getNeurons(layer);
			int neuronsIn = rprop.getNetwork().getNeurons(layer - 1);

			// Last gradients
			{
				wr.printTagStart("last-gradients");
				wr.increaseTabLevel();

				// Iterate last gradients.
				double[][] lastGradients = rprop.getLastGradients(layer);
				pos = 0;
				for (int out = 0; out < neuronsOut; out++) {
					for (int in = 0; in < neuronsIn; in++) {
						XMLAttribute output = new XMLAttribute("o", out);
						XMLAttribute input = new XMLAttribute("i", in);
						XMLAttribute value = new XMLAttribute("v", lastGradients[out][in]);
						if (pos % 4 == 0) {
							if (pos != 0) {
								wr.println();
							}
							wr.printTabs();
						}
						wr.print("lg", output, input, value);
						pos++;
					}
				}

				// End last gradients.
				wr.println();
				wr.decreaseTabLevel();
				wr.printTagEnd();
			}

			// Last weight changes
			{
				wr.printTagStart("last-weight-changes");
				wr.increaseTabLevel();

				// Iterate last gradients.
				double[][] lastWeightChanges = rprop.getLastWeightChanges(layer);
				pos = 0;
				for (int out = 0; out < neuronsOut; out++) {
					for (int in = 0; in < neuronsIn; in++) {
						XMLAttribute output = new XMLAttribute("o", out);
						XMLAttribute input = new XMLAttribute("i", in);
						XMLAttribute value = new XMLAttribute("v", lastWeightChanges[out][in]);
						if (pos % 4 == 0) {
							if (pos != 0) {
								wr.println();
							}
							wr.printTabs();
						}
						wr.print("lc", output, input, value);
						pos++;
					}
				}

				// End last weight changes.
				wr.println();
				wr.decreaseTabLevel();
				wr.printTagEnd();
			}

			// Last deltas
			{
				wr.printTagStart("last-deltas");
				wr.increaseTabLevel();

				// Iterate last gradients.
				double[][] lastDeltas = rprop.getLastDeltas(layer);
				pos = 0;
				for (int out = 0; out < neuronsOut; out++) {
					for (int in = 0; in < neuronsIn; in++) {
						XMLAttribute output = new XMLAttribute("o", out);
						XMLAttribute input = new XMLAttribute("i", in);
						XMLAttribute value = new XMLAttribute("v", lastDeltas[out][in]);
						if (pos % 4 == 0) {
							if (pos != 0) {
								wr.println();
							}
							wr.printTabs();
						}
						wr.print("ld", output, input, value);
						pos++;
					}
				}

				// End last weight changes.
				wr.println();
				wr.decreaseTabLevel();
				wr.printTagEnd();
			}

			// End layer.
			wr.decreaseTabLevel();
			wr.printTagEnd();
		}

		// End layers.
		wr.decreaseTabLevel();
		wr.printTagEnd();

		// End learning data
		wr.decreaseTabLevel();
		wr.printTagEnd();

		// Finish writing
		wr.close();
	}

}
