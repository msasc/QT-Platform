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

package com.qtplaf.library.ai.fuzzy;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.function.normalize.StdNormalizer;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A fuzzy control.
 *
 * @author Miquel Sas
 */
public class Control {

	/**
	 * Enum comparison conditions.
	 */
	private enum Condition {
		GT, GE, EQ, LE, LT
	}

	/**
	 * The list of control segments.
	 */
	private List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param segments The list of segments.
	 */
	public Control(List<Segment> segments) {
		super();
		validateSegments(segments);
		this.segments.addAll(segments);
	}

	/**
	 * Check if the value is in the range of the label or GT.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean checkGT(double value, String label) {
		return check(value, label, Condition.GT);
	}

	/**
	 * Check if the value is in the range of the label or GE.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean checkGE(double value, String label) {
		return check(value, label, Condition.GE);
	}

	/**
	 * Check if the value is in the range of the label or EQ.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean checkEQ(double value, String label) {
		return check(value, label, Condition.EQ);
	}

	/**
	 * Check if the value is in the list of labels.
	 * 
	 * @param value The value to check.
	 * @param labels The list of labels.
	 * @return A boolean.
	 */
	public boolean checkIn(double value, String... labels) {
		for (String label : labels) {
			if (checkEQ(value, label)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the value is in the range of the label or LE.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean checkLE(double value, String label) {
		return check(value, label, Condition.LE);
	}

	/**
	 * Check if the value is in the range of the label or LT.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean checkLT(double value, String label) {
		return check(value, label, Condition.LT);
	}

	/**
	 * Check if the value is in the range of the label applying the condition (GT, GE, EQ, LE, LT)
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @param condition The comparison condition.
	 * @return A boolean.
	 */
	private boolean check(double value, String label, Condition condition) {
		int labelIndex = getIndex(label);
		List<Integer> valueIndexes = getIndexes(value);
		switch (condition) {
		case GT:
			if (ListUtils.getFirst(valueIndexes) > labelIndex) {
				return true;
			}
			break;
		case GE:
			if (ListUtils.getFirst(valueIndexes) >= labelIndex) {
				return true;
			}
			break;
		case EQ:
			if (ListUtils.getFirst(valueIndexes) == labelIndex || ListUtils.getLast(valueIndexes) == labelIndex) {
				return true;
			}
			break;
		case LE:
			if (ListUtils.getLast(valueIndexes) <= labelIndex) {
				return true;
			}
			break;
		case LT:
			if (ListUtils.getLast(valueIndexes) < labelIndex) {
				return true;
			}
			break;
		default:
			throw new IllegalStateException();
		}
		return false;
	}

	/**
	 * Returns the segment of the label.
	 * 
	 * @param label The label.
	 * @return The segment of the label (or throws an IllegalArgumentException)
	 */
	public Segment getSegment(String label) {
		return segments.get(getIndex(label));
	}

	/**
	 * Returns the index of the label (or throws an IllegalArgumentException).
	 * 
	 * @param label The label to check.
	 * @return The index of the label.
	 */
	public int getIndex(String label) {
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getLabel().equals(label)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the list of indexes of segments where the value should be included. The list must contain at least one
	 * index and maximum two.
	 * 
	 * @param value The value.
	 * @return The list of indexes of segments where the value should be included.
	 */
	public List<Integer> getIndexes(double value) {
		List<Integer> indexes = new ArrayList<>();
		if (value < getMinimum()) {
			indexes.add(0);
		}
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).inRange(value)) {
				indexes.add(i);
			}
		}
		if (value > getMaximum()) {
			indexes.add(segments.size() - 1);
		}
		return indexes;
	}

	/**
	 * Returns the global factor.
	 * 
	 * @param value The value to check.
	 * @return The global factor.
	 */
	public double getFactor(double value) {
		return new StdNormalizer(getMaximum(), getMinimum()).normalize(value);
	}

	/**
	 * Returns the minimum value in the list of segments.
	 * 
	 * @return The minimum value.
	 */
	public double getMinimum() {
		return ListUtils.getFirst(segments).getMinimum();
	}

	/**
	 * Returns the minimum of a given label.
	 * 
	 * @param label The label.
	 * @return The minimum of the label.
	 */
	public double getMinimum(String label) {
		return getSegment(label).getMinimum();
	}

	/**
	 * Returns the maximum value in the list of segments.
	 * 
	 * @return The maximum value.
	 */
	public double getMaximum() {
		return ListUtils.getLast(segments).getMaximum();
	}

	/**
	 * Returns the maximum of a given label.
	 * 
	 * @param label The label.
	 * @return The maximum of the label.
	 */
	public double getMaximum(String label) {
		return getSegment(label).getMaximum();
	}

	/**
	 * Validate the list of segments.
	 * 
	 * @param segments The list of segments.
	 */
	private void validateSegments(List<Segment> segments) {
		for (int i = 0; i < segments.size(); i++) {
			if (i > 0) {
				Segment prev = segments.get(i - 1);
				Segment curr = segments.get(i);
				if (curr.getMinimum() <= prev.getMinimum()) {
					throw new IllegalArgumentException();
				}
				if (prev.getMaximum() >= curr.getMaximum()) {
					throw new IllegalArgumentException();
				}
				double prevMax = prev.getMaximum();
				double prevNext = Math.nextUp(prevMax);
				if (curr.getMinimum() > prevNext) {
					throw new IllegalArgumentException();
				}
			}
		}

		// Sign 0 count, only one admitted.
		int countZeroSign = countZeroSign(segments);
		if (countZeroSign > 1) {
			throw new IllegalArgumentException();
		}
		if (countZeroSign == 1) {
			int index = getZeroSignIndex(segments);
			for (int i = 0; i < segments.size(); i++) {
				if (i < index && segments.get(i).getSign() != -1) {
					throw new IllegalArgumentException();
				}
				if (i > index && segments.get(i).getSign() != 1) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * Count the number of zero sign segments.
	 * 
	 * @param segments The list of segments.
	 * @return The number of segments with zero sign (must be 0 or 1)
	 */
	private int countZeroSign(List<Segment> segments) {
		int count = 0;
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSign() == 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the index of the zero sign.
	 * 
	 * @param segments The list of segments.
	 * @return The index of the zero sign.
	 */
	private int getZeroSignIndex(List<Segment> segments) {
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSign() == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		return segments.toString();
	}
}
