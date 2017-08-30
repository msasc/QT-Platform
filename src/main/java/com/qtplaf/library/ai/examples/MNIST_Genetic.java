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
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.data.PatternSource;
import com.qtplaf.library.ai.function.activation.ActivationTANH;
import com.qtplaf.library.ai.io.neural.NetworkIO;
import com.qtplaf.library.ai.learning.genetic.Genetic;
import com.qtplaf.library.ai.learning.genetic.Genome;
import com.qtplaf.library.ai.learning.genetic.Score;
import com.qtplaf.library.ai.learning.genetic.choosers.RandomChooser;
import com.qtplaf.library.ai.learning.genetic.choosers.RouletteWheelChooser;
import com.qtplaf.library.ai.learning.genetic.choosers.WinnerChooser;
import com.qtplaf.library.ai.learning.genetic.mutators.PerturbMutator;
import com.qtplaf.library.ai.learning.genetic.selectors.RandomSelector;
import com.qtplaf.library.ai.learning.genetic.selectors.RouletteWheelSelector;
import com.qtplaf.library.ai.learning.genetic.selectors.TournamentSelector;
import com.qtplaf.library.ai.learning.genetic.selectors.TruncationSelector;
import com.qtplaf.library.ai.mnist.NumberImage;
import com.qtplaf.library.ai.mnist.NumberImageUtils;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileUtils;

/**
 * Test the regression learning method with the MNIST database.
 *
 * @author Miquel Sas
 */
public class MNIST_Genetic {

	private static Network createNetwork() {
		int inputLayerSize = NumberImage.ROWS * NumberImage.COLUMNS;
		int hiddenLayerSize = 100;
		int outputLayerSize = 10;
		Network network = new Network();
		network.addLayer(inputLayerSize);
		network.addLayer(hiddenLayerSize, new ActivationTANH(), 1.0);
		network.addLayer(outputLayerSize, new ActivationTANH(), 1.0);
		NetworkUtils.randomizeWeights(network);
		return network;
	}

	/**
	 * Score function.
	 */
	static class ScoreMNIST implements Score {

		/** Test pattern source. */
		private PatternSource patternSource;

		/**
		 * Constructor.
		 */
		public ScoreMNIST() {
			super();
		}

		/**
		 * Return a boolean indicating if this score function best scores are the minimum ones.
		 * 
		 * @return A boolean.
		 */
		@Override
		public boolean isMinimize() {
			return false;
		}

		/**
		 * Set the pattenr source to score.
		 * 
		 * @param patternSource The pattenr source.
		 */
		public void setPatternSource(PatternSource patternSource) {
			this.patternSource = patternSource;
		}

		/**
		 * Calculate the score the network.
		 * 
		 * @param genome The network.
		 * @return The score.
		 */
		@Override
		public double calculate(Genome genome) {
			return NetworkUtils.getPerformance(genome.getNetwork(), patternSource, 4);
		}
	}

	private static String getFileName(int index, int size, boolean deflate) {
		StringBuilder b = new StringBuilder();
		b.append("GN-");
		b.append(size);
		b.append("-");
		b.append(StringUtils.leftPad(Integer.toString(index), 2, "0"));
		b.append(deflate ? ".dat" : ".xml");
		return b.toString();
	}

	private static PatternSource getSource(List<NumberImage> srcImg, int size) throws Exception {
		List<NumberImage> images = NumberImageUtils.getNumberImages(srcImg, size);
		PatternSource source = NumberImageUtils.getPatternSource(images, true);
		return source;
	}

	/**
	 * @param args Startup args.
	 * @throws Exception If any error occurs.
	 */
	public static void main(String[] args) throws Exception {

		// Text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");

		// Pattern source to score.
		List<NumberImage> srcImg = NumberImageUtils.getNumberImagesTest();
		int sourceSize = 100;
		ScoreMNIST scoreFunction = new ScoreMNIST();
		scoreFunction.setPatternSource(getSource(srcImg, sourceSize));

		// Configure regression.
		Genetic g = new Genetic();
		g.setScoreFunction(scoreFunction);

		// Selectors.
		g.addSelector(new RandomSelector(0.8));
		g.addSelector(new TournamentSelector(0.8, 10, scoreFunction.isMinimize()));
		g.addSelector(new TruncationSelector(0.8, scoreFunction.isMinimize()));
		g.addSelector(new RouletteWheelSelector(0.6, scoreFunction.isMinimize()));

		// Choosers.
		g.addChooser(new WinnerChooser(scoreFunction.isMinimize()));
		g.addChooser(new RandomChooser());
		g.addChooser(new RouletteWheelChooser(scoreFunction.isMinimize()));

		// Mutators.
		g.addMutator(new PerturbMutator(0.1, 0.2));
		g.addMutator(new PerturbMutator(0.1, 0.4));
		g.addMutator(new PerturbMutator(0.1, 0.6));
		g.addMutator(new PerturbMutator(0.1, 0.8));
//		g.addMutator(new ShuffleMutator(100));
//		g.addMutator(new ShuffleMutator(1000));

		boolean deflate = false;
		File parentDir = FileUtils.getFileFromClassPathEntries("files/networks");

		// Populate.
		int savedNetworks = 10;
		int population = 10;
		List<Genome> genomes = new ArrayList<>();

		for (int i = 0; i < savedNetworks; i++) {
			File networkFile = new File(parentDir, getFileName(i, 100, deflate));
			if (networkFile.exists()) {
				Network network = new Network();
				NetworkIO io = new NetworkIO(network);
				io.setDeflate(deflate);
				io.fromXML(networkFile);
				genomes.add(new Genome(network));
			}
		}
		while (genomes.size() < population) {
			genomes.add(new Genome(createNetwork()));
		}

		g.initialize(genomes);

		// Iterate until the best score is GT 70%
		int iteration = 0;
		while (true) {
			g.iteration();
			iteration++;
			StringBuilder b = new StringBuilder();
			b.append("I: " + iteration);
			b.append(" S: " + sourceSize);
			b.append(" - " + NumberUtils.getBigDecimal(g.getBestScore(), 2));
			System.out.println(b.toString());
			if (g.getBestScore() > 0.9) {
				break;
			}
			if (iteration % 1000 == 0) {
				List<Network> networks = g.getNetworks();
				for (int i = 0; i < savedNetworks; i++) {
					Network network = networks.get(i);
					File networkFile = new File(parentDir, getFileName(i, 100, deflate));
					NetworkIO ioNet = new NetworkIO(network);
					ioNet.setDeflate(deflate);
					ioNet.toXML(networkFile);
				}
			}
			if (sourceSize < 10000) {
				if (g.getBestScore() > 0.6) {
					sourceSize = Math.min(10000, sourceSize * 2);
					scoreFunction.setPatternSource(getSource(srcImg, sourceSize));
					g.scoreNetworks();
				}
			}
		}

		System.exit(0);
	}

}
