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
import com.qtplaf.library.ai.io.neural.NetworkIO;
import com.qtplaf.library.ai.learning.propagation.BackPropagation;
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
 * Test BackPropagationOnline.
 *
 * @author Miquel Sas
 */
public class MNIST_Prop_BProp {

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

		File parentDir = FileUtils.getFileFromClassPathEntries("files/networks");
		String fileName = "BPD-" + hiddenLayerSize + ".xml";
		File networkFile = new File(parentDir, fileName);

		// NetworkBad
		boolean deflate = false;

		Network network = new Network();
		boolean restored = false;
		if (networkFile.exists()) {
			NetworkIO io = new NetworkIO(network);
			io.setDeflate(deflate);
			io.fromXML(networkFile);
			restored = true;
		} else {
			network.addInputLayer(inputLayerSize);
			network.addHiddenLayer(hiddenLayerSize, new ActivationTANH(), 1.0);
			network.addOutputLayer(outputLayerSize, new ActivationTANH(), 1.0);
			NetworkUtils.randomizeWeights(network);
		}

		// Pattern sources.
		PatternSource trainSrc = NumberImageUtils.getPatternSourceTrain();
		PatternSource testSrc = NumberImageUtils.getPatternSourceTest();

		// Train
		BackPropagation train = new BackPropagation(network);
		train.setLearningData(trainSrc);
		train.setCheckData(testSrc);
		train.setBatchMode(true);

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
			long endIter = System.currentTimeMillis();
			long iterSeconds = (endIter - startIter) / 1000;
			trainError = train.getLastError();
			BigDecimal error = NumberUtils.getBigDecimal(trainError, 8);

			// Performance
			double performance = NetworkUtils.getPerformance(network, testSrc, 4);
			long startPerf = System.currentTimeMillis();
			BigDecimal perf = NumberUtils.getBigDecimal(performance, 4);
			long endPerf = System.currentTimeMillis();
			long perfSeconds = (endPerf - startPerf) / 1000;

			// Save the network if performance has increased.
			if (performance > previousPerformance) {
				// Save network.
				NetworkIO ioNet = new NetworkIO(network);
				ioNet.setDeflate(deflate);
				ioNet.toXML(networkFile);
				previousPerformance = performance;
			}

			// Log
			StringBuilder b = new StringBuilder();
			b.append("Epoch ");
			b.append(epoch++);
			b.append(" (");
			b.append(iterSeconds);
			b.append(") Error ");
			b.append(error);
			b.append(" Performance ");
			b.append(perf);
			b.append(" (");
			b.append(perfSeconds);
			b.append(")");
			System.out.println(b.toString());

			if (epoch % 100 == 0) {
				if (MessageBox.question(Session.UK, "Continue?", MessageBox.YES_NO) == MessageBox.NO) {
					break;
				}
			}

		} while (trainError > 0.005);

		System.exit(0);
	}

}
