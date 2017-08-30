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

package com.qtplaf.library.ai.io.neural;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.ai.function.Activation;
import com.qtplaf.library.ai.io.DataIO;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.ParserHandler;
import com.qtplaf.library.util.xml.XMLAttribute;
import com.qtplaf.library.util.xml.XMLWriter;

/**
 * Utility to save and restore network data.
 *
 * @author Miquel Sas
 */
public class NetworkIO extends DataIO {
	/**
	 * XML parser handler to read a network.
	 */
	class Handler extends ParserHandler {

		/** Layer sizes. */
		List<Integer> layers = new ArrayList<>();
		/** Activations. */
		List<Activation> activations = new ArrayList<>();
		/** Biases. */
		List<Double> biases = new ArrayList<>();
		/** Weights. */
		List<Double> weights = new ArrayList<>();

		/**
		 * Called to notify an element start.
		 */
		@Override
		public void elementStart(String namespace, String elementName, String path, Attributes attributes)
			throws SAXException {

			// network/structure/layer
			if (path.equals("network/structure/layer")) {
				String sneurons = attributes.getValue("neurons");
				String sactivation = attributes.getValue("activation");
				String sbias = attributes.getValue("bias");

				layers.add(Integer.parseInt(sneurons));
				if (sactivation != null) {
					activations.add(NetworkUtils.getActivation(sactivation));
				}
				if (sbias != null) {
					biases.add(Double.parseDouble(sbias));
				}

				return;
			}

			// network/weights/w
			if (path.equals("network/weights/w")) {
				double weight = Double.parseDouble(attributes.getValue("v"));
				weights.add(weight);
				return;
			}

		}

		/**
		 * Called to notify an element end.
		 */
		@Override
		public void elementEnd(String namespace, String elementName, String path) throws SAXException {

			// network/structure
			if (path.equals("network/structure")) {
				int[] sizes = new int[layers.size()];
				for (int i = 0; i < layers.size(); i++) {
					sizes[i] = layers.get(i);
				}
				network.createStructure(sizes);
				for (int i = 1; i < layers.size(); i++) {
					network.setActivation(i, activations.get(i - 1));
					network.setBias(i, biases.get(i - 1));
				}
				return;
			}

			// network/weights
			if (path.equals("network/weights")) {
				double[] weights = ListUtils.toDoubleArray(this.weights);
				NetworkUtils.setWeights(network, weights);
				return;
			}
		}
	}

	/** The network. */
	private Network network;

	/**
	 * Constructor.
	 * 
	 * @param network The network.
	 */
	public NetworkIO(Network network) {
		super();
		this.network = network;
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

		// Network tag.
		wr.printTagStart("network");
		wr.increaseTabLevel();

		// -------------------------------------------------------------------------------------------------------------
		// Structure tag.
		wr.printTagStart("structure");
		wr.increaseTabLevel();

		// Layers.
		for (int layer = 0; layer < network.getLayers(); layer++) {
			XMLAttribute neurons = new XMLAttribute("neurons", network.getNeurons(layer));
			// Input layer is done.
			if (layer == 0) {
				wr.printTag("layer", neurons);
				continue;
			}
			XMLAttribute activation =
				new XMLAttribute("activation", NetworkUtils.getActivationId(network.getActivation(layer)));
			XMLAttribute bias = new XMLAttribute("bias", network.getBias(layer));
			wr.printTag("layer", neurons, activation, bias);
		}

		// End structure.
		wr.decreaseTabLevel();
		wr.printTagEnd();

		// -------------------------------------------------------------------------------------------------------------
		// All weights.
		wr.printTagStart("weights");
		wr.increaseTabLevel();

		double[] weights = NetworkUtils.getWeights(network);
		for (int i = 0; i < weights.length; i++) {
			XMLAttribute weight = new XMLAttribute("v", Double.toString(weights[i]));
			if (i % 10 == 0) {
				if (i != 0) {
					wr.println();
				}
				wr.printTabs();
			}
			wr.print("w", weight);
		}

		// End weights.
		wr.decreaseTabLevel();
		wr.println();
		wr.printTagEnd();

		// End network.
		wr.decreaseTabLevel();
		wr.printTagEnd();

		// Close.
		wr.close();
	}

}
