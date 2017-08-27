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

package com.qtplaf.library.ai.learning.reinforcement.book;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.Random;

/**
 * Play tic-tac-toe.
 *
 * @author Miquel Sas
 */
public class TicTacToe {

	/** Board length. */
	static final int LENGTH = 3;
	/** X on the boartd. */
	static final int X = -1;
	/** O on the boartd. */
	static final int O = 1;

	/**
	 * Agent.
	 */
	static class Agent {

		/** Epsilon: probability of choosing random action instead of greedy. */
		double epsilon;
		/** Learning rate. */
		double alpha;
		/** State history. */
		List<Integer> states = new ArrayList<>();
		/** All values. */
		List<Double> values;
		/** Symbol (X/O) */
		int symbol;

		Agent(int symbol, double epsilon, double alpha) {
			super();
			this.symbol = symbol;
			this.epsilon = epsilon;
			this.alpha = alpha;
		}
		
		void setValues(List<Double> values) {
			this.values = values;
		}

		void takeAction(Environment env) {
			// Choose an action based on epsilon-greedy strategy.
			double random = Random.nextDouble();
			int move_i = 0;
			int move_j = 0;
			if (random < epsilon) {
				// Take a random action.
				List<int[]> possibleMoves = new ArrayList<>();
				for (int i = 0; i < LENGTH; i++) {
					for (int j = 0; j < LENGTH; j++) {
						if (env.board[i][j] == 0) {
							possibleMoves.add(new int[] { i, j });
						}
					}
				}
				int move = Random.nextInt(possibleMoves.size());
				move_i = possibleMoves.get(move)[0];
				move_j = possibleMoves.get(move)[1];
			} else {
				// Choose the best action based on current values of states
				// loop through all possible moves, get their values
				// keep track of the best value
				double bestValue = -1;
				for (int i = 0; i < LENGTH; i++) {
					for (int j = 0; j < LENGTH; j++) {
						if (env.board[i][j] == 0) {
							// What is the state if we made this move?
							env.board[i][j] = symbol;
							int state = env.getState();
							env.board[i][j] = 0;
							if (values.get(state) > bestValue) {
								bestValue = values.get(state);
								move_i = i;
								move_j = j;
							}
						}
					}
				}
			}

			// Do move.
			env.board[move_i][move_j] = symbol;
		}

		void resetHistory() {
			states.clear();
		}

		void updateStateHistory(int state) {
			states.add(state);
		}

		void update(Environment env) {
			double target = env.reward(symbol);
			for (int i = states.size() - 1; i >= 0; i--) {
				double value = values.get(i) + alpha * (target - values.get(i));
				values.set(i, value);
			}
			resetHistory();
		}

	}

	/**
	 * Environment.
	 */
	static class Environment {

		/** Board. */
		int[][] board = new int[LENGTH][LENGTH];
		/** Winner agent (symbol). */
		int winner;
		/** Game ended flag. */
		boolean ended = false;

		public Environment() {
			super();
		}
		
		void reset() {
			for (int j = 0; j < LENGTH; j++) {
				for (int i = 0; i < LENGTH; i++) {
					board[i][j] = 0;
				}
			}
			winner = 0;
			ended = false;
		}

		public int reward(int symbol) {
			if (!gameOver(false)) {
				return 0;
			}
			return (winner == symbol ? 1 : 0);
		}

		private boolean checkEnded(int... sums) {
			for (int sum : sums) {
				if (Math.abs(sum) == LENGTH) {
					winner = (sum < 0 ? X : O);
					ended = true;
					return true;
				}
			}
			return false;
		}

		public boolean gameOver(boolean forceRecalculate) {
			if (!forceRecalculate && ended) {
				return true;
			}
			// Check rows.
			for (int i = 0; i < LENGTH; i++) {
				int sum = 0;
				for (int j = 0; j < LENGTH; j++) {
					sum += board[i][j];
				}
				if (checkEnded(sum)) {
					return true;
				}
			}
			// Check columns.
			for (int j = 0; j < LENGTH; j++) {
				int sum = 0;
				for (int i = 0; i < LENGTH; i++) {
					sum += board[i][j];
				}
				if (checkEnded(sum)) {
					return true;
				}
			}
			// Check diagonals.
			int sum1 = 0;
			int sum2 = 0;
			for (int i = 0; i < LENGTH; i++) {
				sum1 += board[i][i];
				sum2 += board[LENGTH - i - 1][i];
			}
			if (checkEnded(sum1, sum2)) {
				return true;
			}
			// Check draw
			boolean draw = true;
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					if (board[i][j] == 0) {
						draw = false;
						break;
					}
				}
			}
			if (draw) {
				ended = true;
				winner = 0;
				return true;
			}
			return false;
		}

		public int getState() {
			// returns the current state, represented as an int
			// from 0...|S|-1, where S = set of all possible states
			// |S| = 3^(BOARD SIZE), since each cell can have 3 possible values - empty, x, o
			// some states are not possible, e.g. all cells are x, but we ignore that detail
			// this is like finding the integer represented by a base-3 number
			int k = 0;
			int h = 0;
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					int v = 0;
					if (board[i][j] == X) {
						v = 1;
					}
					if (board[i][j] == O) {
						v = 2;
					}
					h += ((int) Math.pow(3, k)) * v;
					k += 1;
				}
			}
			return h;
		}

		public boolean isDraw() {
			return ended && winner == 0;
		}

		public void drawBoard() {
			for (int i = 0; i < LENGTH; i++) {
				System.out.println("-------------");
				System.out.print("|");
				for (int j = 0; j < LENGTH; j++) {
					if (board[i][j] == -1) {
						System.out.print(" X ");
					} else if (board[i][j] == 1) {
						System.out.print(" O ");
					} else {
						System.out.print("   ");
					}
					System.out.print("|");
				}
				System.out.println();
			}
			System.out.println("-------------");
		}
	}

	static class State {
		int state;
		int winner;
		boolean ended;

		State(int state, int winner, boolean ended) {
			this.state = state;
			this.winner = winner;
			this.ended = ended;
		}
		
		@Override
		public String toString() {
			return "(" + state + ", " + winner + ", " + ended + ")";
		}
	}

	/**
	 * recursive function that will return all possible states (as ints) and who the corresponding winner is for those
	 * states (if any) (i, j) refers to the next cell on the board to permute (we need to try -1, 0, 1) impossible games
	 * are ignored, i.e. 3x's and 3o's in a row simultaneously since that will never happen in a real game
	 * 
	 * @param env Environment.
	 * @return The states list.
	 */
	static List<State> getStateHashAndWinner(Environment env) {
		List<State> states = new ArrayList<>();
		getStateHashAndWinner(env, states, 0, 0);
		return states;
	}

	static void getStateHashAndWinner(Environment env, List<State> states, int i, int j) {
		int[] symbols = new int[] { 0, X, O };
		for (int symbol : symbols) {
			env.board[i][j] = symbol;
			if (j == 2) {
				// j goes back to 0, increase i, unless i = 2, then we are done
				if (i == 2) {
					// the board is full, collect results and return.
					int state = env.getState();
					boolean ended = env.gameOver(true);
					int winner = env.winner;
					states.add(new State(state, winner, ended));
				} else {
					getStateHashAndWinner(env, states, i + 1, 0);
				}
			} else {
				// increment j, i stays the same
				getStateHashAndWinner(env, states, i, j + 1);
			}
		}
	}
	
	/**
	 * initialize state values as follows
	 * if wins, V(s) = 1
	 * if loses or draw, V(s) = 0
	 * otherwise, V(s) = 0.5
	 * 
	 * @param symbol Required symbol
	 * @param states List of states
	 * @return List of values.
	 */
	static List<Double> getInitialValues(int symbol, List<State> states) {
		List<Double> values = new ArrayList<>();
		for (State state : states) {
			if (state.ended) {
				if (state.winner == symbol) {
					values.add(1d);
				} else {
					values.add(0d);
				}
			} else {
				values.add(0.5);
			}
		}
		return values;
	}
	
	/**
	 * Play the game.
	 * 
	 * @param p1 Agent 1
	 * @param p2 Agent 2
	 * @param env Environment
	 */
	static void playGame(Agent p1, Agent p2, Environment env) {
		Agent player = null;
		while (!env.gameOver(false)) {
			// alternate between players
			// p1 always starts first
			if (player == p1) {
				player = p2;
			} else {
				player = p1;
			}
			
			// current player makes a move
			player.takeAction(env);
			
			// update state histories
			int state = env.getState();
			p1.updateStateHistory(state);
			p2.updateStateHistory(state);
		}
		
		// do the value function update
		p1.update(env);
		p2.update(env);
	}

	/**
	 * @param args Stratup args.
	 */
	public static void main(String[] args) {
		
		// Train the agent.
		Agent p1 = new Agent(X, 0.1, 0.5);
		Agent p2 = new Agent(O, 0.1, 0.5);
		
		// Environment.
		Environment env = new Environment();
		List<State> states = getStateHashAndWinner(env);
		
		// Set initial values.
		p1.setValues(getInitialValues(p1.symbol, states));
		p2.setValues(getInitialValues(p2.symbol, states));
		
		for (int i = 0; i < 10000; i++) {
			env.reset();
			playGame(p1, p2, env);
			if (i % 500 == 0) {
				env.drawBoard();
			}
		}
		
	}

}
