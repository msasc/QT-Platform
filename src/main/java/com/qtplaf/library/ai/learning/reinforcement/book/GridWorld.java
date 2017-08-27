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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Grid-world dynamic programming.
 *
 * @author Miquel Sas
 */
public class GridWorld {

	static final double SMALL_ENOUGH = 1e-3; // threshold for convergence

	/**
	 * Position (state) in the grid.
	 */
	static class State {
		int i;
		int j;

		State(int i, int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof State) {
				State s = (State) o;
				return i == s.i && j == s.j;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = i;
			return hash ^= (j * 31);
		}

		@Override
		public String toString() {
			return "(" + i + ", " + j + ")";
		}
	}

	/**
	 * Grid environment.
	 */
	static class Grid {
		int width;
		int height;
		Map<State, Double> values;
		Map<State, char[]> actions;
		State s;

		Grid(int width, int height, State start) {
			this.width = width;
			this.height = height;
			this.s = start;
		}

		void set(Map<State, Double> values, Map<State, char[]> actions) {
			this.values = values;
			this.actions = actions;
		}

		void setState(State s) {
			this.s = s;
		}

		State getState() {
			return s;
		}

		boolean isTerminal(State s) {
			return !actions.containsKey(s);
		}

		boolean valid(char action) {
			if (isTerminal(s)) {
				return false;
			}
			char[] actions = this.actions.get(s);
			for (char a : actions) {
				if (action == a) {
					return true;
				}
			}
			return false;
		}

		double move(char a) {
			if (!isTerminal(s) && ListUtils.in(a, actions.get(s))) {
				if (a == 'U') {
					s.i--;
				} else if (a == 'D') {
					s.i++;
				} else if (a == 'R') {
					s.j++;
				} else if (a == 'L') {
					s.j--;
				}
			}
			return getValue(s, values);
		}

		void undo(char a) {
			if (a == 'U') {
				s.i++;
			} else if (a == 'D') {
				s.i--;
			} else if (a == 'R') {
				s.j--;
			} else if (a == 'L') {
				s.j++;
			}
			if (!getAllStates().contains(s)) {
				throw new IllegalStateException();
			}
		}

		boolean gameOver() {
			return !actions.containsKey(s);
		}

		List<State> getAllStates() {
			List<State> positions = new ArrayList<>();
			positions.addAll(values.keySet());
			positions.addAll(actions.keySet());
			return positions;
		}
	}

	/**
	 * define a grid that describes the reward for arriving at each state
	 * 
	 * . . . 1 . x . -1 s . . .
	 * 
	 * @return The grid.
	 */
	static Grid standardGrig() {
		Grid g = new Grid(3, 4, new State(2, 0));

		Map<State, Double> values = new HashMap<>();
		put(values, 0, 3, 1.0);
		put(values, 1, 3, -1.0);

		Map<State, char[]> actions = new HashMap<>();
		put(actions, 0, 0, 'D', 'R');
		put(actions, 0, 1, 'L', 'R');
		put(actions, 0, 2, 'L', 'D', 'R');
		put(actions, 1, 0, 'U', 'D');
		put(actions, 1, 2, 'U', 'D', 'R');
		put(actions, 2, 0, 'U', 'R');
		put(actions, 2, 1, 'L', 'R');
		put(actions, 2, 2, 'L', 'R', 'U');
		put(actions, 2, 3, 'L', 'U');

		g.set(values, actions);
		return g;
	}

	/**
	 * Return a grid that penalizes every other move.
	 * 
	 * @param stepCost The step cost.
	 * @return The grid.
	 */
	static Grid negativeGrid(double stepCost) {
		Grid g = standardGrig();
		put(g.values, 0, 0, stepCost);
		put(g.values, 0, 1, stepCost);
		put(g.values, 0, 2, stepCost);
		put(g.values, 1, 0, stepCost);
		put(g.values, 1, 2, stepCost);
		put(g.values, 2, 0, stepCost);
		put(g.values, 2, 1, stepCost);
		put(g.values, 2, 2, stepCost);
		put(g.values, 2, 3, stepCost);
		return g;
	}

	static double getValue(State s, Map<State, Double> values) {
		if (values.containsKey(s)) {
			return values.get(s);
		}
		return 0;
	}

	static char[] getActions(State pos, Map<State, char[]> actions) {
		if (actions.containsKey(pos)) {
			return actions.get(pos);
		}
		return new char[] {};
	}

	static void printValues(Map<State, Double> values, Grid g) {
		System.out.println("------------------------------");
		for (int i = 0; i < g.width; i++) {
			for (int j = 0; j < g.height; j++) {
				double value = getValue(new State(i, j), values);
				StringBuilder b = new StringBuilder();
				b.append(i + ", " + j + ", ");
				b.append(StringUtils.leftPad(NumberUtils.getBigDecimal(value, 1).toString(), 4, " "));
				System.out.println(b.toString());
			}
		}
	}

	static Map<State, Double> getInitialRewards(List<State> states) {
		Map<State, Double> rewards = new HashMap<>();
		for (State pos : states) {
			rewards.put(pos, 0.0);
		}
		return rewards;
	}

	static void printPolicies(Map<State, char[]> actions, Grid g) {
		for (int i = 0; i < g.width; i++) {
			System.out.println("------------------------------");
			for (int j = 0; j < g.height; j++) {
				char[] a = getActions(new State(i, j), actions);
				StringBuilder b = new StringBuilder();
				b.append(i + ", " + j + ", ");
				if (a != null) {
					b.append(StringUtils.toString(a));
				}
				System.out.println(b.toString());
			}
		}
	}

	static void put(Map<State, char[]> actions, int i, int j, char... cs) {
		actions.put(new State(i, j), cs);
	}

	static void put(Map<State, Double> values, int i, int j, double value) {
		values.put(new State(i, j), value);
	}

	/**
	 * Iterative policy evaluation. Given a policy, let's find it's value function V(s). We will do this for both a
	 * uniform random policy and fixed policy.
	 * <p>
	 * Note: there are 2 sources of randomness.
	 * <p>
	 * p(a|s) - deciding what action to take given the state
	 * <p>
	 * p(s',r|s,a) - the next state and reward given your action-state pair
	 */
	static void iterativePolicyEvaluation() {
		// Grid
		Grid g = standardGrig();
		// All valid positions (states)
		List<State> states = g.getAllStates();
		// A values/rewards map empty.
		Map<State, Double> values = getInitialRewards(states);

		// Discount factor.
		double gamma = 1.0;

		// Repeat until convergence.
		while (true) {
			double biggest_change = 0;
			for (State s : states) {
				double old_v = getValue(s, values);

				// V(s) only has value if it's not a terminal state
				if (g.actions.containsKey(s)) {
					double new_v = 0.0; // we will accumulate the answer
					double p_a = 1.0; // each action has equal probability
					char[] actions = g.actions.get(s);
					for (char a : actions) {
						g.setState(s);
						double r = g.move(a);
						new_v += p_a * (r + gamma * getValue(g.getState(), values));
					}
					values.put(s, new_v);
					biggest_change = Math.max(biggest_change, Math.abs(old_v - new_v));
				}
			}

			// Check convergence.
			if (biggest_change < SMALL_ENOUGH) {
				break;
			}
		}

		// Print info.
		System.out.println("values for uniformly random actions:");
		printValues(values, g);
		System.out.println();
		System.out.println();

		// Fixed policy
		Map<State, char[]> policy = new HashMap<>();
		put(policy, 2, 0, 'U');
		put(policy, 1, 0, 'U');
		put(policy, 0, 0, 'R');
		put(policy, 0, 1, 'R');
		put(policy, 0, 2, 'R');
		put(policy, 1, 2, 'R');
		put(policy, 2, 1, 'R');
		put(policy, 2, 2, 'R');
		put(policy, 2, 3, 'U');
		printPolicies(policy, g);

		// Initialize values to zero.
		values = getInitialRewards(states);

		// let's see how V(s) changes as we get further away from the reward
		gamma = 0.9; // discount factor
		// Repeat until convergence.
		while (true) {
			double biggest_change = 0;
			for (State s : states) {
				double old_v = getValue(s, values);
				// V(s) only has value if it's not a terminal state
				if (policy.containsKey(s)) {
					char[] as = policy.get(s);
					if (as != null && as.length > 0) {
						char a = as[0];
						g.setState(s);
						double r = g.move(a);
						double new_v = r + gamma * getValue(g.getState(), values);
						values.put(s, new_v);
						biggest_change = Math.max(biggest_change, Math.abs(old_v - new_v));
					}
				}
			}

			// Check convergence.
			if (biggest_change < SMALL_ENOUGH) {
				break;
			}
		}

		// Print info.
		System.out.println("values for fixed policy:");
		printValues(values, g);
	}

	/**
	 * @param args Start arguments.
	 */
	public static void main(String[] args) {
		iterativePolicyEvaluation();
	}

}
