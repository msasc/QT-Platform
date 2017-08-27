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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.list.ListUtils;

/**
 * Filters are used to build complex <code>WHERE</code> clauses programmatic. Filters chain expressions with logical
 * conditions and brackets to form complex <code>WHERE</code> clauses.
 * <p>
 * <i>Note that the features in filters are not aimed to provide a full SQL compatible set, but to offer a useful enough
 * sub-set compatible with as much databases as possible.</i>
 *
 * @author Miquel Sas
 */
public class Filter extends ArrayList<Filter.Segment> {

	/**
	 * Enumerate the possible filter usages.
	 */
	public static enum Usage {

		SELECT, UPDATE, DELETE;
	}

	/**
	 * Enumerate the possible like conditions.
	 */
	public static enum Like {

		Left, Mid, Right;
	}

	/**
	 * A filter segment can be a filter itself, a stringor a list of values.
	 */
	public static class Segment implements Comparable<Object> {

		/**
		 * A complete fiter.
		 */
		private Filter filter = null;
		/**
		 * The string.
		 */
		private String string = null;
		/**
		 * The list of values.
		 */
		private List<Value> values = null;

		/**
		 * Constructor assigning a string.
		 *
		 * @param string The string.
		 */
		public Segment(String string) {
			this.string = string;
		}

		public Segment(Filter filter) {
			this.filter = filter;
		}

		/**
		 * Constructor assigning a string and a value.
		 *
		 * @param string The string
		 * @param value The value
		 */
		public Segment(String string, Value value) {
			this.string = string;
			this.values = new ArrayList<>();
			this.values.add(value);
		}

		/**
		 * Constructor assigning the string and a lst of valuess
		 *
		 * @param string The string
		 * @param values The list of values
		 */
		public Segment(String string, Value... values) {
			this.string = string;
			this.values = new ArrayList<>();
			this.values.addAll(ListUtils.asList(values));
		}

		/**
		 * Constructor assigning the string and a lst of valuess
		 *
		 * @param string The string
		 * @param values The list of values
		 */
		public Segment(String string, List<Value> values) {
			this.string = string;
			this.values = values;
		}

		/**
		 * Returns the list of values.
		 *
		 * @return The list of values.
		 */
		public List<Value> getValues() {
			if (values != null) {
				return values;
			}
			if (filter != null) {
				return filter.getValues();
			}
			return new ArrayList<>();
		}

		/**
		 * Returns a string representation of this segment.
		 *
		 * @return The segment as a string
		 */
		@Override
		public String toString() {
			if (string != null) {
				return string;
			}
			if (filter != null) {
				return filter.toString();
			}
			return new String();
		}

		/**
		 * Compares this segment with the argument object. Returns 0 if they are equal, -1 if this value is less than
		 * the argument, and 1 if it is greater.
		 *
		 * @return An integer.
		 * @param o The Object to compare with.
		 */
		@Override
		public int compareTo(Object o) {
			if (!(o instanceof Segment)) {
				throw new UnsupportedOperationException(
					MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
			}
			// Filter segments are not aimed to e compared inn order to be sorted.
			return 0;
		}
	}

	/**
	 * The usage of this filter.
	 */
	private Usage usage = Usage.SELECT;

	/**
	 * Default constructor.
	 */
	public Filter() {
		super();
	}

	/**
	 * Create a filter from an origin criteria.
	 * 
	 * @param criteria The high level criteria.
	 */
	public Filter(Criteria criteria) {
		super();
		setCriteria(criteria);
	}

	/**
	 * Assign an entire criteria to this filter.
	 *
	 * @param criteria The criteria to assign
	 */
	public void setCriteria(Criteria criteria) {
		if (criteria == null) {
			return;
		}
		int count = criteria.size();
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				if (criteria.isAnd()) {
					and();
				} else {
					or();
				}
			}
			Criteria.Segment segment = criteria.get(i);
			if (segment.getCriteria() != null) {
				if (segment.isNegate()) {
					not();
				}
				Filter filter = new Filter();
				filter.setCriteria(segment.getCriteria());
				condFilter(filter);
			} else {
				List<Condition> conditions = segment.getConditions();
				openBracket();
				for (int j = 0; j < conditions.size(); j++) {
					if (j > 0) {
						if (segment.isAnd()) {
							and();
						} else {
							or();
						}
					}
					if (count > 1) {
						openBracket();
					}
					setFilterCondition(conditions.get(j));
					if (count > 1) {
						closeBracket();
					}
				}
				closeBracket();
			}
		}
	}

	/**
	 * Applies an entity level condition to this database filter. The condition must refer to table or view fields.
	 *
	 * @param condition The condition.
	 */
	private void setFilterCondition(Condition condition) {
		Field field = condition.getField();
		List<Value> values = condition.getValues();
		Condition.Operator operator = condition.getOperator();
		if (operator == Condition.Operator.BETWEEN) {
			condBetween(field, values.get(0), values.get(1));
		}
		if (operator == Condition.Operator.BETWEEN_NOCASE) {
			condBetweenNoCase(field, values.get(0), values.get(0));
		}
		if (operator == Condition.Operator.NOT_BETWEEN) {
			condNotBetween(field, values.get(0), values.get(0));
		}
		if (operator == Condition.Operator.NOT_BETWEEN_NOCASE) {
			condNotBetweenNoCase(field, values.get(0), values.get(0));
		}
		if (operator == Condition.Operator.FIELD_EQ) {
			condSimple(field, "EQ", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_EQ_NOCASE) {
			condSimpleNoCase(field, "EQ", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_GE) {
			condSimple(field, "GE", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_GE_NOCASE) {
			condSimpleNoCase(field, "GE", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_GT) {
			condSimple(field, "GT", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_GT_NOCASE) {
			condSimpleNoCase(field, "GT", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_LE) {
			condSimple(field, "LE", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_LE_NOCASE) {
			condSimpleNoCase(field, "LE", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_LT) {
			condSimple(field, "LT", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_LT_NOCASE) {
			condSimpleNoCase(field, "LT", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_NE) {
			condSimple(field, "NE", values.get(0));
		}
		if (operator == Condition.Operator.FIELD_NE_NOCASE) {
			condSimpleNoCase(field, "NE", values.get(0));
		}
		if (operator == Condition.Operator.IN_LIST) {
			condIsMember(field, values);
		}
		if (operator == Condition.Operator.IN_LIST_NOCASE) {
			condIsMemberNoCase(field, values);
		}
		if (operator == Condition.Operator.NOT_IN_LIST) {
			condIsNotMember(field, values);
		}
		if (operator == Condition.Operator.NOT_IN_LIST_NOCASE) {
			condIsNotMemberNoCase(field, values);
		}
		if (operator == Condition.Operator.IS_NULL) {
			condIsNull(field);
		}
		if (operator == Condition.Operator.NOT_IS_NULL) {
			condIsNotNull(field);
		}
		if (operator == Condition.Operator.LIKE_LEFT) {
			condLikeLeft(field, values.get(0));
		}
		if (operator == Condition.Operator.LIKE_LEFT_NOCASE) {
			condLikeLeftNoCase(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_LEFT) {
			condNotLikeLeft(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_LEFT_NOCASE) {
			condNotLikeLeftNoCase(field, values.get(0));
		}
		if (operator == Condition.Operator.LIKE_MID) {
			condLikeMid(field, values.get(0));
		}
		if (operator == Condition.Operator.LIKE_MID_NOCASE) {
			condLikeMidNoCase(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_MID) {
			condNotLikeMid(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_MID_NOCASE) {
			condNotLikeMidNoCase(field, values.get(0));
		}
		if (operator == Condition.Operator.LIKE_RIGHT) {
			condLikeRight(field, values.get(0));
		}
		if (operator == Condition.Operator.LIKE_RIGHT_NOCASE) {
			condLikeRightNoCase(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_RIGHT) {
			condNotLikeRight(field, values.get(0));
		}
		if (operator == Condition.Operator.NOT_LIKE_RIGHT_NOCASE) {
			condNotLikeRightNoCase(field, values.get(0));
		}
	}

	/**
	 * Returns this filter usage.
	 *
	 * @return The usage
	 */
	public Usage getUsage() {
		return usage;
	}

	/**
	 * Set the filter usage.
	 *
	 * @param usage Th usage (SELECT, UPDATE, DELETE)
	 */
	public void setUsage(Usage usage) {
		if (usage == null) {
			throw new NullPointerException("Filter usages can not be null");
		}
		this.usage = usage;
	}

	/**
	 * Returns the array of values to assign to parameters.
	 *
	 * @return The array of values to assign to parameters.
	 */
	public List<Value> getValues() {
		List<Value> values = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			Filter.Segment item = get(i);
			values.addAll(item.getValues());
		}
		return values;
	}

	/**
	 * Return the field name to be used.
	 *
	 * @param field The field to retrieve the name.
	 * @return The name to be used.
	 */
	private String getFieldName(Field field) {
		if (field == null) {
			throw new NullPointerException("The field should never be null");
		}
		if (usage == Usage.SELECT) {
			return field.getNameWhere();
		}
		if (usage == Usage.UPDATE) {
			return field.getNameUpdate();
		}
		if (usage == Usage.DELETE) {
			return field.getNameDelete();
		}
		return null;
	}

	/**
	 * Add an AND logical operator to the filter.
	 */
	public void and() {
		add(new Segment(" AND "));
	}

	/**
	 * Add an OR logical operator to the filter.
	 */
	public void or() {
		add(new Segment(" OR "));
	}

	/**
	 * Add an individual NOT logical operator
	 */
	public void not() {
		add(new Segment(" NOT "));
	}

	/**
	 * Add an open parenthesis to the filter.
	 */
	public void openBracket() {
		add(new Segment("("));
	}

	/**
	 * Add a close parenthesis to the filter.
	 */
	public void closeBracket() {
		add(new Segment(")"));
	}

	/**
	 * Add a complete filter condition.
	 *
	 * @param filter The filter to add.
	 */
	public void condFilter(Filter filter) {
		openBracket();
		add(new Segment(filter));
		closeBracket();
	}

	/**
	 * Add a string condition. No validation is done.
	 *
	 * @param string The string filter condition.
	 */
	public void condString(String string) {
		openBracket();
		add(new Segment(string));
		closeBracket();
	}

	/**
	 * Add a simple comparison condition to the filter. Valid conditions are EQ, GT, GE, LT, LE or NE.
	 *
	 * @param field The left expression is a field.
	 * @param condition The condition, either EQ, GT, GE, LT, LE or NE.
	 * @param value The right expression is a single value.
	 */
	public void condSimple(Field field, String condition, Value value) {
		condSimple(field, condition, value, false);
	}

	/**
	 * Add a simple comparison condition to the filter. Valid conditions are EQ, GT, GE, LT, LE or NE.
	 *
	 * @param field The left expression is a field.
	 * @param condition The condition, either EQ, GT, GE, LT, LE or NE.
	 * @param value The right expression is a single value.
	 */
	public void condSimpleNoCase(Field field, String condition, Value value) {
		condSimple(field, condition, value, true);
	}

	/**
	 * Add a simple comparison condition to the filter. Valid conditions are EQ, GT, GE, LT, LE or NE.
	 *
	 * @param field The left expression is a field.
	 * @param condition The condition, either EQ, GT, GE, LT, LE or NE.
	 * @param value The right expression is a single value.
	 * @param nocase A boolean to indicate if the condition should be case insensitive.
	 */
	private void condSimple(Field field, String condition, Value value, boolean nocase) {

		if (nocase && (!field.isString() || !value.isString())) {
			throw new IllegalArgumentException("No case applies only to string fields and values");
		}

		StringBuilder b = new StringBuilder();
		if (nocase) {
			b.append("UPPER(");
		}
		b.append(getFieldName(field));
		if (nocase) {
			b.append(")");
		}

		switch (condition) {
		case "EQ":
			b.append(" = ?");
			break;
		case "GE":
			b.append(" >= ?");
			break;
		case "GT":
			b.append(" > ?");
			break;
		case "LE":
			b.append(" <= ?");
			break;
		case "LT":
			b.append(" < ?");
			break;
		case "NE":
			b.append(" != ?");
			break;
		default:
			throw new IllegalArgumentException("Invalid condition: not a simple comparison");
		}

		if (nocase) {
			add(new Segment(b.toString(), new Value(value
				.toString()
				.toUpperCase())));
		} else {
			add(new Segment(b.toString(), value));
		}
	}

	/**
	 * Add a group comparison condition to the filter, where the left expression is a field and the right expression is
	 * a value expression list. Valid conditions are EQ, GE, GT, LE, LT and NE. Valid group modifiers are ANY, SOME and
	 * ALL.
	 *
	 * @param field The left expression is a field.
	 * @param condition The condition, either EQ, GE, GT, LE, LT or NE.
	 * @param group The group modifier, either ANY, SOME or ALL.
	 * @param values The right expression is a value expression list.
	 */
	public void condGroup(Field field, String condition, String group, List<Value> values) {

		StringBuilder b = new StringBuilder(256);

		b.append(getFieldName(field));

		switch (condition) {
		case "EQ":
			b.append(" = ");
			break;
		case "GE":
			b.append(" >= ");
			break;
		case "GT":
			b.append(" > ");
			break;
		case "LE":
			b.append(" <= ");
			break;
		case "LT":
			b.append(" < ");
			break;
		case "NE":
			b.append(" != ");
			break;
		default:
			throw new IllegalArgumentException("Invalid condition: not a group comparison");
		}

		switch (group) {
		case "ANY":
			b.append("ANY (");
			break;
		case "SOME":
			b.append("SOME (");
			break;
		case "ALL":
			b.append("ALL (");
			break;
		default:
			throw new IllegalArgumentException("Invalid group modifier");
		}

		b.append(parameters(values));
		b.append(")");

		add(new Segment(b.toString(), values));
	}

	/**
	 * Returns the parameter list.
	 *
	 * @param values The values.
	 * @return a String
	 */
	private String parameters(List<Value> values) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				b.append(",");
			}
			b.append(" ?");
		}
		return b.toString();
	}

	/**
	 * Add a membership comparison condition to the filter, where the left expression is a field and the right
	 * expression is a value expression list.
	 *
	 * @param field The left expression is a field.
	 * @param values The right expression is a value expression list.
	 */
	public void condIsMember(Field field, List<Value> values) {
		condIsMember(field, values, false, false);
	}

	/**
	 * Add a membership comparison condition to the filter, where the left expression is a field and the right
	 * expression is a value expression list.
	 *
	 * @param field The left expression is a field.
	 * @param values The right expression is a value expression list.
	 */
	public void condIsMemberNoCase(Field field, List<Value> values) {
		condIsMember(field, values, false, true);
	}

	/**
	 * Add a membership comparison condition to the filter, where the left expression is a field and the right
	 * expression is a value expression list.
	 *
	 * @param field The left expression is a field.
	 * @param values The right expression is a value expression list.
	 */
	public void condIsNotMember(Field field, List<Value> values) {
		condIsMember(field, values, true, false);
	}

	/**
	 * Add a membership comparison condition to the filter, where the left expression is a field and the right
	 * expression is a value expression list.
	 *
	 * @param field The left expression is a field.
	 * @param values The right expression is a value expression list.
	 */
	public void condIsNotMemberNoCase(Field field, List<Value> values) {
		condIsMember(field, values, true, true);
	}

	/**
	 * Add a membership comparison condition to the filter, where the left expression is a field and the right
	 * expression is a value expression list.
	 *
	 * @param field The left expression is a field.
	 * @param values The right expression is a value expression list.
	 * @param not Not flag
	 * @param nocase No case flag
	 */
	private void condIsMember(
		Field field,
		List<Value> values,
		boolean not,
		boolean nocase) {

		if (nocase && !field.isString()) {
			throw new IllegalArgumentException(
				"No case applies only to string fields and values");
		}
		if (nocase) {
			for (int i = 0; i < values.size(); i++) {
				values.set(i, new Value(values.get(i).toString().toUpperCase()));
			}
		}

		StringBuilder b = new StringBuilder(256);
		if (nocase) {
			b.append("UPPER(");
		}
		b.append(getFieldName(field));
		if (nocase) {
			b.append(")");
		}
		if (not) {
			b.append(" NOT");
		}
		b.append(" IN (");
		b.append(parameters(values));
		b.append(")");

		add(new Segment(b.toString(), values));
	}

	/**
	 * Add a range condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value1 The first right expression is a single value.
	 * @param value2 The second right expression is a single value.
	 */
	public void condBetween(Field field, Value value1, Value value2) {
		condBetween(field, value1, value2, false, false);
	}

	/**
	 * Add a range condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value1 The first right expression is a single value.
	 * @param value2 The second right expression is a single value.
	 */
	public void condBetweenNoCase(Field field, Value value1, Value value2) {
		condBetween(field, value1, value2, false, true);
	}

	/**
	 * Add a range condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value1 The first right expression is a single value.
	 * @param value2 The second right expression is a single value.
	 */
	public void condNotBetween(Field field, Value value1, Value value2) {
		condBetween(field, value1, value2, true, false);
	}

	/**
	 * Add a range condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value1 The first right expression is a single value.
	 * @param value2 The second right expression is a single value.
	 */
	public void condNotBetweenNoCase(Field field, Value value1, Value value2) {
		condBetween(field, value1, value2, true, true);
	}

	/**
	 * Add a range condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value1 The first right expression is a single value.
	 * @param value2 The second right expression is a single value.
	 * @param not Not flag
	 * @param nocase No case flag
	 */
	private void condBetween(
		Field field,
		Value value1,
		Value value2,
		boolean not,
		boolean nocase) {

		if (nocase && !field.isString() && !value1.isString() && value2.isString()) {
			throw new IllegalArgumentException("No case only applies to string fields and values");
		}

		StringBuilder b = new StringBuilder();
		b.append(getFieldName(field));
		if (not) {
			b.append(" NOT");
		}
		b.append(" BETWEEN ? AND ?");

		if (nocase) {
			value1 = new Value(value1.toString().toUpperCase());
			value2 = new Value(value2.toString().toUpperCase());
		}
		add(new Segment(b.toString(), new Value[] { value1, value2 }));
	}

	/**
	 * Add a null condition to the filter. Valid conditions are NULL and NOT_NULL.
	 *
	 * @param field The left expression is a field.
	 */
	public void condIsNull(Field field) {

		StringBuilder b = new StringBuilder(128);
		b.append(getFieldName(field));
		b.append(" IS NULL");

		add(new Segment(b.toString()));
	}

	/**
	 * Add a null condition to the filter. Valid conditions are NULL and NOT_NULL.
	 *
	 * @param field The left expression is a field.
	 */
	public void condIsNotNull(Field field) {

		StringBuilder b = new StringBuilder(128);
		b.append(getFieldName(field));
		b.append(" IS NOT NULL");

		add(new Segment(b.toString()));
	}

	/**
	 * Validates a like condition.
	 *
	 * @param field The field.
	 * @param value The value.
	 */
	private void validateLike(Field field, Value value) {
		if (!field.isString()) {
			throw new IllegalArgumentException("Invalid condition: field is not a string");
		}
		if (!value.isString()) {
			throw new IllegalArgumentException("Invalid condition: value is not a string");
		}
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeLeft(Field field, Value value) {
		condLike(field, value, false, false, Like.Left);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeLeftNoCase(Field field, Value value) {
		condLike(field, value, true, false, Like.Left);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeMid(Field field, Value value) {
		condLike(field, value, false, false, Like.Mid);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeMidNoCase(Field field, Value value) {
		condLike(field, value, true, false, Like.Mid);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeRight(Field field, Value value) {
		condLike(field, value, false, false, Like.Right);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condLikeRightNoCase(Field field, Value value) {
		condLike(field, value, true, false, Like.Right);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeLeft(Field field, Value value) {
		condLike(field, value, false, true, Like.Left);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeLeftNoCase(Field field, Value value) {
		condLike(field, value, true, true, Like.Left);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeMid(Field field, Value value) {
		condLike(field, value, false, true, Like.Mid);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeMidNoCase(Field field, Value value) {
		condLike(field, value, true, true, Like.Mid);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeRight(Field field, Value value) {
		condLike(field, value, false, true, Like.Right);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a single value.
	 */
	public void condNotLikeRightNoCase(Field field, Value value) {
		condLike(field, value, true, true, Like.Right);
	}

	/**
	 * Add a like comparison condition to the filter.
	 *
	 * @param field The left expression is a field.
	 * @param value The right expression is a value.
	 * @param not Not flag
	 * @param nocase No case flag
	 * @param type The type of like condition
	 */
	private void condLike(
		Field field,
		Value value,
		boolean nocase,
		boolean not,
		Like type) {
		validateLike(field, value);
		StringBuilder b = new StringBuilder();
		if (nocase) {
			b.append("UPPER(");
		}
		b.append(getFieldName(field));
		if (nocase) {
			b.append(")");
		}
		if (not) {
			b.append(" NOT");
		}
		b.append(" LIKE '");
		if (type == Like.Mid || type == Like.Right) {
			b.append("%");
		}
		if (nocase) {
			b.append(value.toString().toUpperCase());
		} else {
			b.append(value.toString());
		}
		if (type == Like.Left || type == Like.Mid) {
			b.append("%");
		}
		b.append("'");
		add(new Segment(b.toString()));
	}

	/**
	 * Returns this filter as a string.
	 *
	 * @return This filter as a string representation.
	 */
	public String toSQL() {
		StringBuilder b = new StringBuilder(256);
		for (int i = 0; i < size(); i++) {
			b.append(get(i).toString());
		}
		return b.toString();
	}

	/**
	 * Returns this filter as a string.
	 *
	 * @return This filter as a string representation.
	 */
	@Override
	public String toString() {
		return toSQL();
	}
}
