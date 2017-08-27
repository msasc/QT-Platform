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

package com.qtplaf.library.ai.learning.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.qtplaf.library.ai.learning.LearningMethod;
import com.qtplaf.library.ai.neural.Network;
import com.qtplaf.library.ai.neural.NetworkUtils;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.library.util.task.Executor;

/**
 * A genetic learning method.
 *
 * @author Miquel Sas
 */
public class Genetic extends LearningMethod {

	/**
	 * Mutate task.
	 */
	class TaskMutate extends RecursiveAction {

		/** Selector. */
		Selector selector;
		/** Chooser. */
		Chooser chooser;
		/** Mutator. */
		Mutator mutator;

		/** Selected genome. */
		Genome selected;
		/** Mutated genome. */
		Genome mutated;

		/**
		 * Constructor.
		 */
		TaskMutate() {
			super();
		}

		/**
		 * Compute the task
		 */
		@Override
		protected void compute() {
			List<Genome> sample = selector.select(genomes);
			selected = chooser.choose(sample);
			mutated = mutator.mutate(selected);
			mutated.setScore(scoreFunction.calculate(mutated));

			boolean minimize = scoreFunction.isMinimize();
			while (ScoreUtils.compare(selected.getScore(), mutated.getScore(), minimize) > 0) {
				Genome tmp = propagateMutation(selected, mutated);
				tmp.setScore(scoreFunction.calculate(tmp));
				if (ScoreUtils.compare(mutated.getScore(), tmp.getScore(), minimize) > 0) {
					selected = mutated;
					mutated = tmp;
				} else {
					break;
				}
			}
		}

	}

	/** List of choosers. */
	private List<Chooser> choosers = new ArrayList<>();
	/** List of selectors. */
	private List<Selector> selectors = new ArrayList<>();
	/** List of mutators. */
	private List<Mutator> mutators = new ArrayList<>();
	/** List of genomes. */
	private List<Genome> genomes = new ArrayList<>();
	/** A factor to propagate a successful mutation. */
	private double mutationFactor = 0.5;
	/** Elitism factor. */
	private double elitismFactor = 1.0;
	/** Randomize factor. */
	private double randomizeFactor = 0.0;

	/** Score function. */
	private Score scoreFunction;

	/**
	 * Constructor.
	 */
	public Genetic() {
		super();
	}

	/**
	 * Add a chooser.
	 * 
	 * @param chooser The chooser.
	 */
	public void addChooser(Chooser chooser) {
		choosers.add(chooser);
	}

	/**
	 * Add a mutator.
	 * 
	 * @param mutator The mutator.
	 */
	public void addMutator(Mutator mutator) {
		mutators.add(mutator);
	}

	/**
	 * Add a selector.
	 * 
	 * @param selector The selector.
	 */
	public void addSelector(Selector selector) {
		selectors.add(selector);
	}

	/**
	 * Initialize with a list of known (perhaps randomized) networks.
	 * 
	 * @param initialGenomes The list of initial networks.
	 */
	public void initialize(List<Genome> initialGenomes) {
		genomes.addAll(initialGenomes);
		scoreNetworks();
	}

	/**
	 * Re-score the networks and sort.
	 */
	public void scoreNetworks() {
		for (Genome network : genomes) {
			network.setScore(scoreFunction.calculate(network));
		}
		genomes.sort(new ScoreUtils.Comparator(scoreFunction.isMinimize()));
	}

	/**
	 * Set the score function.
	 * 
	 * @param scoreFunction The score function.
	 */
	public void setScoreFunction(Score scoreFunction) {
		this.scoreFunction = scoreFunction;
	}

	/**
	 * Set the elitism factor.
	 * 
	 * @param elitismFactor The elitism factor.
	 */
	public void setElitismFactor(double elitismFactor) {
		this.elitismFactor = elitismFactor;
	}

	/**
	 * Set the randomize factor.
	 * 
	 * @param randomizeFactor The randomize factor.
	 */
	public void setRandomizeFactor(double randomizeFactor) {
		this.randomizeFactor = randomizeFactor;
	}

	/**
	 * Propagate a successful mutation.
	 * 
	 * @param selected The selected genome.
	 * @param mutated The mutated genome.
	 * @return A new result genome.
	 */
	private Genome propagateMutation(Genome selected, Genome mutated) {

		Genome result = new Genome(mutated.getNetwork().clone());

		// Subsequent layers.
		for (int layer = 1; layer < result.getNetwork().getLayers(); layer++) {

			// Weights.
			double[][] sweights = selected.getNetwork().getWeights(layer);
			double[][] mweights = mutated.getNetwork().getWeights(layer);
			double[][] rweights = result.getNetwork().getWeights(layer);

			// Rows (neurons layer out)
			int rows = result.getNetwork().getNeurons(layer);
			// Columns (neurons layer in)
			int cols = result.getNetwork().getNeurons(layer - 1);

			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					double delta = mutationFactor * (mweights[row][col] - sweights[row][col]);
					rweights[row][col] += delta;
				}
			}
		}

		return result;
	}

	/**
	 * Return the best score.
	 * 
	 * @return The best score.
	 */
	public double getBestScore() {
		return getBestSolution().getScore();
	}

	/**
	 * Return the best genome.
	 * 
	 * @return The best genome.
	 */
	public Genome getBestSolution() {
		return ScoreUtils.getWinner(genomes, scoreFunction.isMinimize());
	}

	/**
	 * Return the list of networks.
	 * 
	 * @return The list of networks.
	 */
	public List<Network> getNetworks() {
		List<Network> networks = new ArrayList<>();
		for (Genome genome : genomes) {
			networks.add(genome.getNetwork());
		}
		return networks;
	}

	/**
	 * Perform an iteration. An iteration produces a generation of solutions that performs at least as well as the
	 * previous generation.
	 */
	@Override
	protected void performIteration() {

		// Create the list of tasks. All tasks will use the same configuration in this iteration.
		List<RecursiveAction> tasks = new ArrayList<>();
		for (int i = 0; i < genomes.size(); i++) {
			TaskMutate task = new TaskMutate();
			task.selector = ListUtils.randomGet(selectors);
			task.chooser = ListUtils.randomGet(choosers);
			task.mutator = ListUtils.randomGet(mutators);
			tasks.add(task);
		}

		// Do execute the list of tasks.
		ForkJoinPool.commonPool().invoke(new Executor(tasks));

		// Create the lists of mutated genomes.
		List<Genome> mutated = new ArrayList<>();
		for (int i = 0; i < tasks.size(); i++) {
			TaskMutate tm = (TaskMutate) tasks.get(i);
			mutated.add(tm.mutated);
		}

		// Perform elitism.
		List<Genome> generation = new ArrayList<>();
		generation.addAll(mutated);
		int eliteCount = (int) (genomes.size() * elitismFactor);
		for (int i = 0; i < eliteCount; i++) {
			generation.add(genomes.get(i));
		}
		generation.sort(new ScoreUtils.Comparator(scoreFunction.isMinimize()));
		while (generation.size() > genomes.size()) {
			ListUtils.removeLast(generation);
		}

		int countRandomize = (int) (generation.size() * randomizeFactor);
		int index = generation.size() - 1;
		while (countRandomize-- > 0) {
			NetworkUtils.randomizeWeights(generation.get(index--).getNetwork());
		}

		// Accept the new generation.
		genomes = generation;
	}

}
