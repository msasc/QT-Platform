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
package com.qtplaf.library.database;

import java.util.ArrayList;
import java.util.List;

/**
 * A criteria to filter entities.
 *
 * @author Miquel Sas
 */
public class Criteria extends ArrayList<Criteria.Segment> {

	/**
	 * OR logical operator.
	 */
	public static final boolean OR = false;

	/**
	 * AND logical operator.
	 */
	public static final boolean AND = true;

	/**
	 * A criteria segment is a list of conditions related with an operator, either OR or AND.
	 */
	public static class Segment {

		/**
		 * The list of conditions.
		 */
		private final List<Condition> conditions = new ArrayList<>();
		/**
		 * Operator (and/or)
		 */
		private boolean and = AND;
		/**
		 * Full criteria
		 */
		private Criteria criteria = null;
		/**
		 * Negate (criteria)
		 */
		private boolean negate = false;

		/**
		 * Generic constructor.
		 *
		 * @param conditions The list of conditions.
		 * @param and The operator.
		 */
		public Segment(List<Condition> conditions, boolean and) {
			super();
			this.conditions.addAll(conditions);
			this.and = and;
		}

		/**
		 * Constructor.
		 *
		 * @param and The operator.
		 */
		public Segment(boolean and) {
			super();
			this.and = and;
		}

		/**
		 * Constructor assigning a full criteria to the segment.
		 *
		 * @param criteria Full criteria
		 */
		public Segment(Criteria criteria) {
			super();
			this.and = AND;
			this.criteria = criteria;
		}

		/**
		 * Constructor assigning a full negate criteria to the segment.
		 *
		 * @param criteria Full criteria
		 * @param negate Negate flag
		 */
		public Segment(Criteria criteria, boolean negate) {
			super();
			this.and = AND;
			this.criteria = criteria;
			this.negate = negate;
		}

		/**
		 * Copy constructor.
		 *
		 * @param segment The segment.
		 */
		public Segment(Segment segment) {
			super();
			this.conditions.addAll(segment.conditions);
			this.and = segment.and;
			if (segment.criteria != null) {
				this.criteria = new Criteria(segment.criteria);
			}
		}

		/**
		 * Check is the segment is a negate segment (criterias)
		 *
		 * @return A boolean indicating if its a negate segment
		 */
		public boolean isNegate() {
			return negate;
		}

		/**
		 * Add a condition to the list of conditions.
		 *
		 * @param condition The condition to add.
		 */
		public void addCondition(Condition condition) {
			conditions.add(condition);
		}

		/**
		 * Get the number of conditions.
		 *
		 * @return The number of conditions.
		 */
		public int getConditionCount() {
			return conditions.size();
		}

		/**
		 * Get a condition given its index.
		 *
		 * @param index The index of the condition.
		 * @return The condition.
		 */
		public Condition getCondition(int index) {
			return conditions.get(index);
		}

		/**
		 * Get the list of conditions.
		 *
		 * @return The list of conditions.
		 */
		public List<Condition> getConditions() {
			return conditions;
		}

		/**
		 * Check if the segment is build with the AND operator.
		 *
		 * @return A boolean.
		 */
		public boolean isAnd() {
			return and;
		}

		/**
		 * Check if the segment is build with the OR operator.
		 *
		 * @return A boolean.
		 */
		public boolean isOr() {
			return !and;
		}

		/**
		 * Check if this segment is an entire criteria.
		 *
		 * @return A boolean.
		 */
		public boolean isCriteria() {
			return criteria != null;
		}

		/**
		 * Check if the segment is empty.
		 *
		 * @return A boolean.
		 */
		public boolean isEmpty() {
			if (isCriteria()) {
				return getCriteria().isEmpty();
			}
			return conditions.isEmpty();
		}

		/**
		 * Returns a string representation of this value.
		 *
		 * @return A string
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			if (getCriteria() != null) {
				if (isNegate()) {
					b.append(" NOT ");
				}
				b.append("(");
				b.append(getCriteria().toString());
				b.append(")");
			} else if (getConditionCount() > 0) {
				b.append("(");
				for (int i = 0; i < getConditionCount(); i++) {
					if (i > 0) {
						b.append(and ? " AND " : " OR ");
					}
					b.append(getCondition(i).toString());
				}
				b.append(")");
			}
			return b.toString();
		}

		/**
		 * Returns the full criteria of this segment. s
		 *
		 * @return Returns the criteria.
		 */
		public Criteria getCriteria() {
			return criteria;
		}

		/**
		 * Set the full criteria of this segment.
		 *
		 * @param criteria The criteria to set.
		 */
		public void setCriteria(Criteria criteria) {
			this.criteria = criteria;
		}

		/**
		 * Returns a list with the conditions that apply to the field with the given alias. s
		 *
		 * @param alias The alias of the field.
		 * @return The list of conditions that apply to the field.
		 */
		public List<Condition> getConditions(String alias) {
			List<Condition> aliasConditions = new ArrayList<>();
			if (conditions.size() > 0) {
				for (Condition condition : conditions) {
					Field field = condition.getField();
					if (field != null && field.getAlias().equals(alias)) {
						aliasConditions.add(condition);
					}
				}
			} else if (criteria != null) {
				aliasConditions.addAll(criteria.getConditions(alias));
			}
			return aliasConditions;
		}

		/**
		 * Return the hash code for this object.
		 *
		 * @return The has code
		 */
		@Override
		public int hashCode() {
			int hash = 0;
			hash ^= conditions.hashCode();
			hash ^= Boolean.valueOf(and).hashCode();
			if (criteria != null) {
				hash ^= criteria.hashCode();
			}
			return hash;
		}

		/**
		 * Check if this segment is equal too the argument object.
		 *
		 * @param obj The object to compare with.
		 * @return A boolean indicating if the argument object is equal to this criteria segment
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Segment segment = (Segment) obj;
			if (!conditions.equals(segment.conditions)) {
				return false;
			}
			if (and != segment.and) {
				return false;
			}
			if (criteria != null && segment.criteria == null) {
				return false;
			}
			if (criteria == null && segment.criteria != null) {
				return false;
			}
			if (criteria != null && segment.criteria != null) {
				return criteria.equals(segment.criteria);
			}
			return true;
		}

		/**
		 * Check if a record agrees with this criteria segment.
		 * 
		 * @param record The record to check.
		 * @return True if the record agrees with the criteria segment.
		 */
		public boolean check(Record record) {
			if (getCriteria() != null) {
				return getCriteria().check(record);
			}
			boolean agreesSegment = true;
			int count = getConditionCount();
			for (int i = 0; i < count; i++) {
				boolean agreesCondition = getCondition(i).check(record);
				if (isAnd()) {
					if (!agreesCondition) {
						agreesSegment = false;
						break;
					}
					continue;
				}
				if (i == 0 && !agreesCondition) {
					agreesSegment = false;
					continue;
				}
				if (agreesCondition) {
					agreesSegment = true;
					continue;
				}
			}
			return agreesSegment;
		}
	}

	/**
	 * Operator (and/or)
	 */
	private boolean and = AND;

	/**
	 * Default constructor. By default segments are connected by AND operator.
	 */
	public Criteria() {
		super();
		this.and = AND;
	}

	/**
	 * Copy constructor.
	 *
	 * @param criteria A base criteria.
	 */
	public Criteria(Criteria criteria) {
		super();
		this.and = criteria.and;
		addAll(criteria);
	}

	/**
	 * Constructor assigning the and flag.
	 *
	 * @param and Indicates that the AND operator will be used to connect segments in this criteria.
	 */
	public Criteria(boolean and) {
		super();
		this.and = and;
	}

	/**
	 * Construct a criteria passing a condition.
	 *
	 * @param condition The condition to add.
	 */
	public Criteria(Condition condition) {
		super();
		this.and = AND;
		Segment segment = new Segment(AND);
		segment.addCondition(condition);
		add(segment);
	}

	/**
	 * Adds a segment that is a full criteria.
	 *
	 * @param criteria A criteria to add too this criteria.
	 */
	public void add(Criteria criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			add(new Segment(criteria));
		}
	}

	/**
	 * Add a segment to the list of segments.
	 *
	 * @param conditions The list of conditions.
	 * @param and The operator (and/or) to build the segment.
	 */
	public void add(List<Condition> conditions, boolean and) {
		add(new Segment(conditions, and));
	}

	/**
	 * Adds a condition to the last segment.
	 *
	 * @param condition The condition to add.
	 */
	public void add(Condition condition) {
		if (isEmpty()) {
			Segment segment = new Segment(AND);
			segment.addCondition(condition);
			add(segment);
		} else {
			get(size() - 1).addCondition(condition);
		}
	}

	/**
	 * Check if this criteria is build with the AND operator.
	 *
	 * @return A boolean.
	 */
	public boolean isAnd() {
		return and;
	}

	/**
	 * Check if this criteria is build with the OR operator.
	 *
	 * @return A boolean.
	 */
	public boolean isOr() {
		return !and;
	}

	/**
	 * Returns a string representation of this criteria.
	 *
	 * @return A string
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (!isEmpty()) {
			b.append("(");
			for (int i = 0; i < size(); i++) {
				if (i > 0) {
					b.append(and ? " AND " : " OR ");
				}
				b.append(get(i).toString());
			}
			b.append(")");
		}
		return b.toString();
	}

	/**
	 * Returns a list with the conditions that apply to the field with the given alias.
	 *
	 * @param alias The alias of the field.
	 * @return The list of conditions that apply to the field.
	 */
	public List<Condition> getConditions(String alias) {
		List<Condition> aliasConditions = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			aliasConditions.addAll(get(i).getConditions(alias));
		}
		return aliasConditions;
	}

	/**
	 * Return the hash code for this object.
	 *
	 * @return The has code
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < size(); i++) {
			hash ^= get(i).hashCode();
		}
		return hash;
	}

	/**
	 * Check if the argument object is equal to this object.
	 *
	 * @param obj The object to compare.
	 * @return A boolean indicating if the argument object is equal to this criteria
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Criteria other = (Criteria) obj;
		if (and != other.and) {
			return false;
		}
		if (size() != other.size()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(other.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the argument record agrees with the criteria.
	 * 
	 * @param record The record to check.
	 * @return A boolean indicating if the argument record agrees with the criteria.
	 */
	public boolean check(Record record) {
		if (isEmpty()) {
			return true;
		}
		boolean agreesCriteria = true;
		int count = size();
		for (int i = 0; i < count; i++) {
			boolean agreesSegment = get(i).check(record);
			if (isAnd()) {
				if (!agreesSegment) {
					agreesCriteria = false;
					break;
				}
				continue;
			}
			if (i == 0 && !agreesSegment) {
				agreesCriteria = false;
				continue;
			}
			if (agreesSegment) {
				agreesCriteria = true;
				continue;
			}
		}
		return agreesCriteria;
	}
}
