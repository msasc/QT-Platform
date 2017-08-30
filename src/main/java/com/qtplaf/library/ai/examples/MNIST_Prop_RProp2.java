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

package com.qtplaf.library.ai.examples;

import java.io.File;
import java.math.BigDecimal;

import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.activation.ActivationTANH;
import com.qtplaf.library.ai.io.learning.ResilientPropagationIO;
import com.qtplaf.library.ai.io.neural.NetworkIO;
import com.qtplaf.library.ai.learning.propagation.ResilientPropagation;
import com.qtplaf.library.ai.mnist.NumberImage;
import com.qtplaf.library.ai.mnist.NumberImageUtils;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileUtils;

/**
 * Test resilient propagation.
 *
 * @author Miquel Sas
 */
public class MNIST_Prop_RProp2 {

	/**
	 * @param args Startup args.
	 * @throws Exception If any error occurs.
	 */
	public static void main(String[] args) throws Exception {

		// Text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");

		// Input, hidden and output layer sizes.
		int inputLayerSize = NumberImage.ROWS * NumberImage.COLUMNS;
		int hiddenLayerSize = 100;
		int outputLayerSize = 10;

		boolean deflate = false;

		File parentDir = FileUtils.getFileFromClassPathEntries("files/networks");
		String networkFileName = "NN2-" + hiddenLayerSize + (deflate ? ".dat" : ".xml");
		File networkFile = new File(parentDir, networkFileName);

		// Restore network if possible
		Network network = new Network();
		boolean restored = false;
		if (networkFile.exists()) {
			NetworkIO io = new NetworkIO(network);
			io.setDeflate(deflate);
			io.fromXML(networkFile);
			restored = true;
		} else {
			network.addLayer(inputLayerSize);
			network.addLayer(hiddenLayerSize, new ActivationTANH(), 1.0);
			network.addLayer(hiddenLayerSize, new ActivationTANH(), 1.0);
			network.addLayer(outputLayerSize, new ActivationTANH(), 1.0);
			NetworkUtils.randomizeWeights(network);
		}

		// Pattern sources.
		PatternSource trainSrc = NumberImageUtils.getPatternSourceTrain(true);
		PatternSource testSrc = NumberImageUtils.getPatternSourceTest(true);

		// Train
		ResilientPropagation train = new ResilientPropagation(network);
		train.setLearningData(trainSrc);
		train.setCheckData(testSrc);

		// Restore learning if possible
		String learningFileName = "RP2-" + hiddenLayerSize + (deflate ? ".dat" : ".xml");
		File learningFile = new File(parentDir, learningFileName);
		if (learningFile.exists()) {
			ResilientPropagationIO ioLearn = new ResilientPropagationIO(train);
			ioLearn.setDeflate(deflate);
			ioLearn.fromXML(learningFile);
		}

		// Start iterations.
		train.initialize();

		int epoch = 0;
		double trainError;
		double previousPerformance = -1;
		if (restored) {
			previousPerformance = NetworkUtils.getPerformance(network, testSrc, 4);
			System.out.println("Previous performance " + previousPerformance);
		}

		do {
			// Iteration.
			long startIter = System.currentTimeMillis();
			train.iteration();
			trainError = train.getLastError();
			BigDecimal error = NumberUtils.getBigDecimal(trainError, 8);
			long endIter = System.currentTimeMillis();
			double iterSeconds = (endIter - startIter) / 1000d;

			// Performance
			double performance = NetworkUtils.getPerformance(network, testSrc, 4);
			long startPerf = System.currentTimeMillis();
			BigDecimal perf = NumberUtils.getBigDecimal(performance, 4);
			long endPerf = System.currentTimeMillis();
			double perfSeconds = (endPerf - startPerf) / 1000d;

			// Save the network if performance has increased.
			if (performance > previousPerformance) {
				// Save network.
				NetworkIO ioNet = new NetworkIO(network);
				ioNet.setDeflate(deflate);
				ioNet.toXML(networkFile);
				// Save learning data.
				ResilientPropagationIO ioLearn = new ResilientPropagationIO(train);
				ioLearn.setDeflate(deflate);
				ioLearn.toXML(learningFile);

				previousPerformance = performance;
			}

			// Log
			StringBuilder b = new StringBuilder();
			b.append("Epoch ");
			b.append(epoch++);
			b.append(" (");
			b.append(NumberUtils.getBigDecimal(iterSeconds + perfSeconds, 2));
			b.append(") Error ");
			b.append(error);
			b.append(" Performance ");
			b.append(perf);
			System.out.println(b.toString());

			if (epoch % 10000 == 0) {
				if (MessageBox.question(Session.UK, "Continue?", MessageBox.YES_NO) == MessageBox.NO) {
					break;
				}
			}

		} while (trainError > 0.005);

		System.exit(0);
	}

}
