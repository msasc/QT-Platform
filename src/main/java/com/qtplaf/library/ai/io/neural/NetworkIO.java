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
import com.qtplaf.library.ai.function.activation.ActivationBipolarSigmoid;
import com.qtplaf.library.ai.function.activation.ActivationTANH;
import com.qtplaf.library.ai.function.activation.ActivationSigmoid;
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
				int neurons = Integer.parseInt(attributes.getValue("neurons"));
				layers.add(neurons);
				return;
			}

			// network/activations/a
			if (path.equals("network/activations/a")) {
				String activationId = attributes.getValue("v");
				Activation activation = getActivation(activationId);
				activations.add(activation);
				return;
			}

			// network/biases/b
			if (path.equals("network/biases/b")) {
				double bias = Double.parseDouble(attributes.getValue("v"));
				biases.add(bias);
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
				return;
			}

			// network/activations
			if (path.equals("network/activations")) {
				Activation[] activations = this.activations.toArray(new Activation[this.activations.size()]);
				NetworkUtils.setActivations(network, activations);
				return;
			}

			// network/biases
			if (path.equals("network/biases")) {
				double[] biases = ListUtils.toDoubleArray(this.biases);
				NetworkUtils.setBiases(network, biases);
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
			wr.printTag("layer", neurons);
		}

		// End structure.
		wr.decreaseTabLevel();
		wr.printTagEnd();

		// -------------------------------------------------------------------------------------------------------------
		// All activations.
		wr.printTagStart("activations");
		wr.increaseTabLevel();

		Activation[] activations = NetworkUtils.getActivations(network);
		for (int i = 0; i < activations.length; i++) {
			XMLAttribute activation = new XMLAttribute("v", getActivationId(activations[i]));
			if (i % 10 == 0) {
				if (i != 0) {
					wr.println();
				}
				wr.printTabs();
			}
			wr.print("a", activation);
		}

		// End activations.
		wr.decreaseTabLevel();
		wr.println();
		wr.printTagEnd();

		// -------------------------------------------------------------------------------------------------------------
		// All biases.
		wr.printTagStart("biases");
		wr.increaseTabLevel();

		double[] biases = NetworkUtils.getBiases(network);
		for (int i = 0; i < biases.length; i++) {
			XMLAttribute bias = new XMLAttribute("v", Double.toString(biases[i]));
			if (i % 10 == 0) {
				if (i != 0) {
					wr.println();
				}
				wr.printTabs();
			}
			wr.print("b", bias);
		}

		// End biases.
		wr.decreaseTabLevel();
		wr.println();
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

	/** Hyperbolic tangent activation. */
	private Activation hyperbolic = new ActivationTANH();
	/** Sigmoid activation. */
	private Activation sigmoid = new ActivationSigmoid();
	/** Bipolar sigmoid. */
	private Activation bipolar = new ActivationBipolarSigmoid();

	/**
	 * Returns the activation function given the id.
	 * 
	 * @param id The activation id.
	 * @return The activation function.
	 */
	private Activation getActivation(String id) {
		if (id.equals("H")) {
			return hyperbolic;
		}
		if (id.equals("S")) {
			return sigmoid;
		}
		if (id.equals("B")) {
			return bipolar;
		}
		return null;
	}

	/**
	 * Returns the activation index of the neuron activation in the list of activations.
	 * 
	 * @param activation The activation.
	 * @return The id of the activation or an empty string if the neuron has no activation.
	 */
	private String getActivationId(Activation activation) {
		if (activation != null) {
			if (activation instanceof ActivationTANH) {
				return "H";
			}
			if (activation instanceof ActivationSigmoid) {
				return "S";
			}
			if (activation instanceof ActivationBipolarSigmoid) {
				return "B";
			}
		}
		return "";
	}
}
