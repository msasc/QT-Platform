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
 * A condition used to build filtering criteria.
 *
 * @author Miquel Sas
 */
public class Condition {

	/**
	 * Enumeration constants of Operators.
	 */
	public static enum Operator {

		/**
		 * Starts with, in SQL <code>FIELD LIKE '....&#37;'</code>
		 */
		LIKE_LEFT(1),
		/**
		 * Contains, in SQL <code>FIELD LIKE '&#37;....&#37;'</code>
		 */
		LIKE_MID(1),
		/**
		 * Ends with, in SQL <code>FIELD LIKE '&#37;....'</code>
		 */
		LIKE_RIGHT(1),
		/**
		 * Equals, in SQL <code>FIELD = &#63;</code>
		 */
		FIELD_EQ(1),
		/**
		 * Greater than, in SQL <code>FIELD &gt; &#63;</code>
		 */
		FIELD_GT(1),
		/**
		 * Greater than or equal to, in SQL <code>FIELD &gt;= &#63;</code>
		 */
		FIELD_GE(1),
		/**
		 * Less than, in SQL <code>FIELD &lt; &#63;</code>
		 */
		FIELD_LT(1),
		/**
		 * Less than or equal to, in SQL <code>FIELD &lt;= &#63;</code>
		 */
		FIELD_LE(1),
		/**
		 * Not equal to, in SQL <code>FIELD != &#63;</code>
		 */
		FIELD_NE(1),
		/**
		 * In the list, in SQL <code>FIELD IN (&#63;,&#63;,...)</code>
		 */
		IN_LIST(-1),
		/**
		 * Is null, in SQL <code>FIELD IS NULL</code>
		 */
		IS_NULL(0),
		/**
		 * Between, in SQL <code>FIELD BETWEEN &#63; AND &#63;</code>
		 */
		BETWEEN(2),
		/**
		 * Does not start with, in SQL <code>FIELD NOT LIKE '....&#37;'</code>
		 */
		NOT_LIKE_LEFT(1),
		/**
		 * Does not contains, in SQL <code>FIELD NOT LIKE '&#37;....&#37;'</code>
		 */
		NOT_LIKE_MID(1),
		/**
		 * Does not end with, in SQL <code>FIELD NOT LIKE '&#37;....'</code>
		 */
		NOT_LIKE_RIGHT(1),
		/**
		 * Not in the list, in SQL <code>FIELD NOT IN (&#63;,&#63;,...)</code>
		 */
		NOT_IN_LIST(-1),
		/**
		 * Not null, in SQL <code>FIELD IS NOT NULL</code>
		 */
		NOT_IS_NULL(0),
		/**
		 * Not between, in SQL <code>FIELD NOT BETWEEN &#63; AND &#63;</code>
		 */
		NOT_BETWEEN(2),
		/**
		 * Starts with, no case, in SQL <code>UPPER(FIELD) LIKE '....&#37;'</code>
		 */
		LIKE_LEFT_NOCASE(1),
		/**
		 * Contains no case, in SQL <code>UPPER(FIELD) LIKE '&#37;....&#37;'</code>
		 */
		LIKE_MID_NOCASE(1),
		/**
		 * Ends with no case, in SQL <code>UPPER(FIELD) LIKE '&#37;....'</code>
		 */
		LIKE_RIGHT_NOCASE(1),
		/**
		 * Equals no case, in SQL <code>UPPER(FIELD) = UPPER(&#63;)</code>
		 */
		FIELD_EQ_NOCASE(1),
		/**
		 * Greater than no case, in SQL <code>UPPER(FIELD) &gt; UPPER(&#63;)</code>
		 */
		FIELD_GT_NOCASE(1),
		/**
		 * Greater than or equal to no case, in SQL <code>UPPER(FIELD) &gt;= UPPER(&#63;)</code>
		 */
		FIELD_GE_NOCASE(1),
		/**
		 * Less than no case, in SQL <code>UPPER(FIELD) &lt; UPPER(&#63;)</code>
		 */
		FIELD_LT_NOCASE(1),
		/**
		 * Less than or equal to no case, in SQL <code>UPPER(FIELD) &lt;= UPPER(&#63;)</code>
		 */
		FIELD_LE_NOCASE(1),
		/**
		 * Not equal no case <code>UPPER(FIELD) != UPPER(&#63;)</code>
		 */
		FIELD_NE_NOCASE(1),
		/**
		 * In list no case <code>UPPER(FIELD) IN (UPPER(&#63;),UPPER(&#63;),...)</code>
		 */
		IN_LIST_NOCASE(-1),
		/**
		 * Between no case <code>UPPER(FIELD) BETWEEN UPPER(&#63;) AND UPPER(&#63;)</code>
		 */
		BETWEEN_NOCASE(2),
		/**
		 * Does not start with no case, in SQL <code>UPPER(FIELD) NOT LIKE '....&#37;'</code>
		 */
		NOT_LIKE_LEFT_NOCASE(1),
		/**
		 * Does not contain no case, in SQL <code>UPPER(FIELD) NOT LIKE '&#37;....&#37;'</code>
		 */
		NOT_LIKE_MID_NOCASE(1),
		/**
		 * Does not end with no case, in SQL <code>UPPER(FIELD) NOT LIKE '&#37;....'</code>
		 */
		NOT_LIKE_RIGHT_NOCASE(1),
		/**
		 * Not in list no case, in SQL <code>UPPER(FIELD) NOT IN (UPPER(&#63;),UPPER(&#63;),...)</code>
		 */
		NOT_IN_LIST_NOCASE(-1),
		/**
		 * Not between no case, in SQL <code>UPPER(FIELD) NOT BETWEEN UPPER(&#63;) AND UPPER(&#63;)</code>
		 */
		NOT_BETWEEN_NOCASE(2);

		/**
		 * This operator required number of values: -1 more than one, 1 one, 2 two, 0 none.
		 */
		private int requiredValues = -1;

		/**
		 * Constructor assigning the number of required values.
		 *
		 * @param requiredValues Number of required values.
		 */
		Operator(int requiredValues) {
			this.requiredValues = requiredValues;
		}

		/**
		 * Returns the number of required values in the right operand.
		 *
		 * @return The number of required values.
		 */
		public int getRequiredValues() {
			return requiredValues;
		}

		/**
		 * Check if this operator is a NO CASE operator.
		 *
		 * @return A boolean
		 */
		public boolean isNoCase() {
			return toString().contains("NOCASE");
		}
	}

	/**
	 * Validates field, operator and values to construct a valid condition.
	 *
	 * @param field The field or left operand.
	 * @param operator The operator.
	 * @param values The possible list of values as right operand.
	 */
	@SuppressWarnings("null")
	public static final void validate(Field field, Operator operator, List<Value> values) {
		// The field can not be null.
		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}
		// Null or empty values can only apply to null or not null operators.
		if (values == null || values.isEmpty()) {
			if (!operator.equals(Operator.IS_NULL) && !operator.equals(Operator.NOT_IS_NULL)) {
				throw new IllegalArgumentException(
					MessageFormat.format("Right operand is expected for operator: {0}", operator));
			}
		}
		// The type of the right operand must be compatible with the type of the field.
		final Types fieldType = field.getType();
		for (Value value : values) {
			boolean invalidValueType = false;
			if (fieldType.isBoolean() && !value.isBoolean()) {
				invalidValueType = true;
			}
			if (fieldType.isString() && !value.isString()) {
				invalidValueType = true;
			}
			if (fieldType.isNumber() && !value.isNumber()) {
				invalidValueType = true;
			}
			if (fieldType.isDateTimeOrTimestamp() && !value.isDateTimeOrTimestamp()) {
				invalidValueType = true;
			}
			if (fieldType.isByteArray() && !value.isByteArray()) {
				invalidValueType = true;
			}
			if (invalidValueType) {
				throw new IllegalArgumentException(
					MessageFormat
						.format("Invalid value type ({0}) for field type {1}", value.getType(), field.getType()));
			}
		}
		// A no case operator requires a field and a value of type string.
		if (operator.isNoCase()) {
			if (!fieldType.isString()) {
				throw new IllegalArgumentException("No case only applies to string fields and values");
			}
			for (Value value : values) {
				if (!value.isString()) {
					throw new IllegalArgumentException("No case only applies to string fields and values");
				}
			}
		}
		// The number of values in the right operand.
		boolean invalidNumberOfValues = false;
		if (operator.getRequiredValues() == -1) {
			if (values == null || values.isEmpty()) {
				invalidNumberOfValues = true;
			}
		}
		if (operator.getRequiredValues() == 0) {
			if (values != null && values.size() > 0) {
				invalidNumberOfValues = true;
			}
		}
		if (operator.getRequiredValues() == 1) {
			if (values == null || values.size() != 1) {
				invalidNumberOfValues = true;
			}
		}
		if (operator.getRequiredValues() == 2) {
			if (values == null || values.size() != 2) {
				invalidNumberOfValues = true;
			}
		}
		if (invalidNumberOfValues) {
			throw new IllegalArgumentException(
				MessageFormat.format("Invalid number of values for operator {0}", operator));
		}
	}

	/**
	 * Creates a LIKE_LEFT condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeLeft(Field field, Value value) {
		return new Condition(field, Operator.LIKE_LEFT, value);
	}

	/**
	 * Creates a LIKE_LEFT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeLeftNoCase(Field field, Value value) {
		return new Condition(field, Operator.LIKE_LEFT_NOCASE, value);
	}

	/**
	 * Creates a NOT_LIKE_LEFT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition notLikeLeftNoCase(Field field, Value value) {
		return new Condition(field, Operator.NOT_LIKE_LEFT_NOCASE, value);
	}

	/**
	 * Creates a LIKE_MID condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeMid(Field field, Value value) {
		return new Condition(field, Operator.LIKE_MID, value);
	}

	/**
	 * Creates a LIKE_MID_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeMidNoCase(Field field, Value value) {
		return new Condition(field, Operator.LIKE_MID_NOCASE, value);
	}

	/**
	 * Creates a NOT_LIKE_MID condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition notLikeMid(Field field, Value value) {
		return new Condition(field, Operator.NOT_LIKE_MID, value);
	}

	/**
	 * Creates a NOT_LIKE_MID_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition notLikeMidNoCased(Field field, Value value) {
		return new Condition(field, Operator.NOT_LIKE_MID_NOCASE, value);
	}

	/**
	 * Creates a LIKE_RIGHT condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeRight(Field field, Value value) {
		return new Condition(field, Operator.LIKE_RIGHT, value);
	}

	/**
	 * Creates a LIKE_RIGHT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition likeRightNoCase(Field field, Value value) {
		return new Condition(field, Operator.LIKE_RIGHT_NOCASE, value);
	}

	/**
	 * Creates a NOT_LIKE_RIGHT condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition notLikeRight(Field field, Value value) {
		return new Condition(field, Operator.NOT_LIKE_RIGHT, value);
	}

	/**
	 * Creates a NOT_LIKE_RIGHT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition notLikeRightNoCase(Field field, Value value) {
		return new Condition(field, Operator.NOT_LIKE_RIGHT_NOCASE, value);
	}

	/**
	 * Creates a FIELD_EQ condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldEQ(Field field, Value value) {
		return new Condition(field, Operator.FIELD_EQ, value);
	}

	/**
	 * Creates a FIELD_GT condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldGT(Field field, Value value) {
		return new Condition(field, Operator.FIELD_GT, value);
	}

	/**
	 * Creates a FIELD_GE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldGE(Field field, Value value) {
		return new Condition(field, Operator.FIELD_GE, value);
	}

	/**
	 * Creates a FIELD_LT condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldLT(Field field, Value value) {
		return new Condition(field, Operator.FIELD_LT, value);
	}

	/**
	 * Creates a FIELD_LEcondition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldLE(Field field, Value value) {
		return new Condition(field, Operator.FIELD_LE, value);
	}

	/**
	 * Creates a FIELD_NE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldNE(Field field, Value value) {
		return new Condition(field, Operator.FIELD_NE, value);
	}

	/**
	 * Creates a FIELD_EQ_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldEQNoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_EQ_NOCASE, value);
	}

	/**
	 * Creates a FIELD_GT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldGTNoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_GT_NOCASE, value);
	}

	/**
	 * Creates a FIELD_GE_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldGENoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_GE_NOCASE, value);
	}

	/**
	 * Creates a FIELD_LT_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldLTNoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_LT_NOCASE, value);
	}

	/**
	 * Creates a FIELD_LE_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldLENoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_LE_NOCASE, value);
	}

	/**
	 * Creates a FIELD_NE_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value The value to check.
	 * @return The condition.
	 */
	public static Condition fieldNENoCase(Field field, Value value) {
		return new Condition(field, Operator.FIELD_NE_NOCASE, value);
	}

	/**
	 * Creates a IN_LIST condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition inList(Field field, Value... values) {
		return new Condition(field, Operator.IN_LIST, values);
	}

	/**
	 * Creates a IN_LIST condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition inList(Field field, List<Value> values) {
		return new Condition(field, Operator.IN_LIST, values);
	}

	/**
	 * Creates a NOT_IN_LIST condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition notInList(Field field, Value... values) {
		return new Condition(field, Operator.NOT_IN_LIST, values);
	}

	/**
	 * Creates a NOT_IN_LIST condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition notInList(Field field, List<Value> values) {
		return new Condition(field, Operator.NOT_IN_LIST, values);
	}

	/**
	 * Creates a IN_LIST_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition inListNoCase(Field field, Value... values) {
		return new Condition(field, Operator.IN_LIST_NOCASE, values);
	}

	/**
	 * Creates a IN_LIST_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition inListNoCase(Field field, List<Value> values) {
		return new Condition(field, Operator.IN_LIST_NOCASE, values);
	}

	/**
	 * Creates a NOT_IN_LIST_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition notInListNoCase(Field field, Value... values) {
		return new Condition(field, Operator.NOT_IN_LIST_NOCASE, values);
	}

	/**
	 * Creates a NOT_IN_LIST_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param values The values to check.
	 * @return The condition.
	 */
	public static Condition notInListNoCase(Field field, List<Value> values) {
		return new Condition(field, Operator.NOT_IN_LIST_NOCASE, values);
	}

	/**
	 * Creates a IS_NULL condition.
	 *
	 * @param field The field to be checked.
	 * @return The condition.
	 */
	public static Condition isNull(Field field) {
		return new Condition(field, Operator.IS_NULL, new ArrayList<Value>());
	}

	/**
	 * Creates a NOT_IS_NULL condition.
	 *
	 * @param field The field to be checked.
	 * @return The condition.
	 */
	public static Condition isNotNull(Field field) {
		return new Condition(field, Operator.NOT_IS_NULL, new ArrayList<Value>());
	}

	/**
	 * Creates a BETWEEN condition.
	 *
	 * @param field The field to be checked.
	 * @param value1 The first value to check.
	 * @param value2 The last value to check.
	 * @return The condition.
	 */
	public static Condition between(Field field, Value value1, Value value2) {
		return new Condition(field, Operator.BETWEEN, new Value[] { value1, value2 });
	}

	/**
	 * Creates a NOT_BETWEEN condition.
	 *
	 * @param field The field to be checked.
	 * @param value1 The first value to check.
	 * @param value2 The last value to check.
	 * @return The condition.
	 */
	public static Condition notBetween(Field field, Value value1, Value value2) {
		return new Condition(field, Operator.NOT_BETWEEN, new Value[] { value1, value2 });
	}

	/**
	 * Creates a BETWEEN_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value1 The first value to check.
	 * @param value2 The last value to check.
	 * @return The condition.
	 */
	public static Condition betweenNoCase(Field field, Value value1, Value value2) {
		return new Condition(field, Operator.BETWEEN_NOCASE, new Value[] { value1, value2 });
	}

	/**
	 * Creates a NOT_BETWEEN_NOCASE condition.
	 *
	 * @param field The field to be checked.
	 * @param value1 The first value to check.
	 * @param value2 The last value to check.
	 * @return The condition.
	 */
	public static Condition notBetweenNoCase(Field field, Value value1, Value value2) {
		return new Condition(field, Operator.NOT_BETWEEN_NOCASE, new Value[] { value1, value2 });
	}

	/**
	 * The field to compare is the left operand.
	 */
	private Field field = null;
	/**
	 * The operator to apply.
	 */
	private Operator operator = null;
	/**
	 * The right operand is a list of one or more values.
	 */
	private final List<Value> values = new ArrayList<>();

	/**
	 * Generic constructor.
	 *
	 * @param field The field or left operand
	 * @param operator The operator
	 * @param value The value that is the right operand.
	 */
	public Condition(Field field, Operator operator, Value value) {
		this.field = field;
		this.operator = operator;
		this.values.add(value);
		validate(field, operator, values);
	}

	/**
	 * Generic constructor.
	 *
	 * @param field The field or left operand
	 * @param operator The operator
	 * @param values The value or values that are the right operand.
	 */
	public Condition(Field field, Operator operator, List<Value> values) {
		this.field = field;
		this.operator = operator;
		this.values.addAll(values);
		validate(field, operator, values);
	}

	/**
	 * Generic constructor.
	 *
	 * @param field The field or left operand
	 * @param operator The operator
	 * @param values The value or values that are the right operand.
	 */
	public Condition(Field field, Operator operator, Value... values) {
		this.field = field;
		this.operator = operator;
		this.values.addAll(ListUtils.asList(values));
		validate(field, operator, this.values);
	}

	/**
	 * Returns the field or left operand.
	 *
	 * @return The field.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Returns the operator.
	 *
	 * @return The operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Returns the list of values or right operand.
	 *
	 * @return The list of values.
	 */
	public List<Value> getValues() {
		return values;
	}

	/**
	 * Check if this condition is not case sensitive.
	 * 
	 * @return A boolean indicating if this condition is not case sensitive.
	 */
	@SuppressWarnings("incomplete-switch")
	public boolean isNoCase() {
		switch (getOperator()) {
		case LIKE_LEFT_NOCASE:
		case LIKE_MID_NOCASE:
		case LIKE_RIGHT_NOCASE:
		case FIELD_EQ_NOCASE:
		case FIELD_GT_NOCASE:
		case FIELD_GE_NOCASE:
		case FIELD_LT_NOCASE:
		case FIELD_LE_NOCASE:
		case FIELD_NE_NOCASE:
		case IN_LIST_NOCASE:
		case BETWEEN_NOCASE:
		case NOT_LIKE_LEFT_NOCASE:
		case NOT_LIKE_MID_NOCASE:
		case NOT_LIKE_RIGHT_NOCASE:
		case NOT_IN_LIST_NOCASE:
		case NOT_BETWEEN_NOCASE:
			return true;
		}
		return false;
	}

	/**
	 * Check if this condition is a not condition.
	 * 
	 * @return A boolean indicating if this condition is a not condition.
	 */
	@SuppressWarnings("incomplete-switch")
	public boolean isNot() {
		switch (getOperator()) {
		case NOT_LIKE_LEFT:
		case NOT_LIKE_MID:
		case NOT_LIKE_RIGHT:
		case NOT_IN_LIST:
		case NOT_IS_NULL:
		case NOT_BETWEEN:
		case NOT_LIKE_LEFT_NOCASE:
		case NOT_LIKE_MID_NOCASE:
		case NOT_LIKE_RIGHT_NOCASE:
		case NOT_IN_LIST_NOCASE:
		case NOT_BETWEEN_NOCASE:
			return true;
		}
		return false;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @return A boolean.
	 * @param o The object to compare with.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		final Condition cmp = (Condition) o;
		if (!field.equals(cmp.field)) {
			return false;
		}
		if (!operator.equals(cmp.operator)) {
			return false;
		}
		if (!values.equals(cmp.values)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the hash code value for this List.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash ^= field.hashCode();
		hash ^= operator.hashCode();
		hash ^= values.hashCode();
		return hash;
	}

	/**
	 * Check if this is a string condition, that is, if it requires a string value.
	 * 
	 * @return A boolean indicating if this is a string condition, that is, if it requires a string value.
	 */
	public boolean isStringCondition() {
		switch (getOperator()) {
		case LIKE_LEFT:
		case LIKE_LEFT_NOCASE:
		case NOT_LIKE_LEFT:
		case NOT_LIKE_LEFT_NOCASE:
		case LIKE_MID:
		case LIKE_MID_NOCASE:
		case NOT_LIKE_MID:
		case NOT_LIKE_MID_NOCASE:
		case LIKE_RIGHT:
		case LIKE_RIGHT_NOCASE:
		case NOT_LIKE_RIGHT:
		case NOT_LIKE_RIGHT_NOCASE:
		case FIELD_EQ_NOCASE:
		case FIELD_GT_NOCASE:
		case FIELD_GE_NOCASE:
		case FIELD_LT_NOCASE:
		case FIELD_LE_NOCASE:
		case FIELD_NE_NOCASE:
		case IN_LIST_NOCASE:
		case NOT_IN_LIST_NOCASE:
		case BETWEEN_NOCASE:
		case NOT_BETWEEN_NOCASE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check the like left condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeLeft(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().startsWith(vCnd.getString());
	}

	/**
	 * Check the like left condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeLeftNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().startsWith(vCnd.getString().toUpperCase());
	}

	/**
	 * Check the like mid condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeMid(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().contains(vCnd.getString());
	}

	/**
	 * Check the like mid condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeMidNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().contains(vCnd.getString().toUpperCase());
	}

	/**
	 * Check the like right condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeRight(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().endsWith(vCnd.getString());
	}

	/**
	 * Check the like right condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkLikeRightNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().endsWith(vCnd.getString().toUpperCase());
	}

	/**
	 * Check the field EQ condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldEQ(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.compareTo(vCnd) == 0;
	}

	/**
	 * Check the field EQ condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldEQNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().compareTo(vCnd.getString().toUpperCase()) == 0;
	}

	/**
	 * Check the field GT condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldGT(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.compareTo(vCnd) > 0;
	}

	/**
	 * Check the field GT condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldGTNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().compareTo(vCnd.getString().toUpperCase()) > 0;
	}

	/**
	 * Check the field GE condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldGE(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.compareTo(vCnd) >= 0;
	}

	/**
	 * Check the field GE condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldGENoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().compareTo(vCnd.getString().toUpperCase()) >= 0;
	}

	/**
	 * Check the field LT condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldLT(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.compareTo(vCnd) < 0;
	}

	/**
	 * Check the field LT condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldLTNoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().compareTo(vCnd.getString().toUpperCase()) < 0;
	}

	/**
	 * Check the field LE condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldLE(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.compareTo(vCnd) <= 0;
	}

	/**
	 * Check the field LE condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkFieldLENoCase(Value vChk) {
		Value vCnd = values.get(0);
		return vChk.getString().toUpperCase().compareTo(vCnd.getString().toUpperCase()) <= 0;
	}

	/**
	 * Check the in list condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkInList(Value vChk) {
		return vChk.in(values);
	}

	/**
	 * Check the in list condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkInListNoCase(Value vChk) {
		String sChk = vChk.getString().toUpperCase();
		for (int i = 0; i < values.size(); i++) {
			if (sChk.compareTo(values.get(i).getString().toUpperCase()) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check the is null condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkIsNull(Value vChk) {
		return vChk.isNull();
	}

	/**
	 * Check the between condition.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkBetween(Value vChk) {
		Value vMin = values.get(0);
		Value vMax = values.get(1);
		return vChk.compareTo(vMin) >= 0 && vChk.compareTo(vMax) <= 0;
	}

	/**
	 * Check the between condition no case.
	 * 
	 * @param vChk The value to check.
	 * @return A boolean.
	 */
	private boolean checkBetweenNoCase(Value vChk) {
		String sChk = vChk.getString().toUpperCase();
		Value vMin = values.get(0);
		Value vMax = values.get(1);
		return sChk.compareTo(vMin.getString().toUpperCase()) >= 0
			&& sChk.compareTo(vMax.getString().toUpperCase()) <= 0;
	}

	/**
	 * Check that a record meets the condition.
	 * 
	 * @param record The record to check.
	 * @return A boolean indicating if the record meets the condition.
	 */
	public boolean check(Record record) {
		int index = record.getFieldList().getFieldIndex(field);
		if (index < 0) {
			return false;
		}
		Value value = record.getValue(index);
		return check(value);
	}

	/**
	 * Check that a value meets the condition.
	 * 
	 * @param value The value to check.
	 * @return A boolean indicating if the value meets the condition.
	 */
	public boolean check(Value value) {
		switch (getOperator()) {
		case LIKE_LEFT:
			return checkLikeLeft(value);
		case LIKE_MID:
			return checkLikeMid(value);
		case LIKE_RIGHT:
			return checkLikeRight(value);
		case FIELD_EQ:
			return checkFieldEQ(value);
		case FIELD_GT:
			return checkFieldGT(value);
		case FIELD_GE:
			return checkFieldGE(value);
		case FIELD_LT:
			return checkFieldLT(value);
		case FIELD_LE:
			return checkFieldLE(value);
		case FIELD_NE:
			return !checkFieldEQ(value);
		case IN_LIST:
			return checkInList(value);
		case IS_NULL:
			return checkIsNull(value);
		case BETWEEN:
			return checkBetween(value);
		case NOT_LIKE_LEFT:
			return !checkLikeLeft(value);
		case NOT_LIKE_MID:
			return !checkLikeMid(value);
		case NOT_LIKE_RIGHT:
			return !checkLikeRight(value);
		case NOT_IN_LIST:
			return !checkInList(value);
		case NOT_IS_NULL:
			return !checkIsNull(value);
		case NOT_BETWEEN:
			return !checkBetween(value);
		case LIKE_LEFT_NOCASE:
			return checkLikeLeftNoCase(value);
		case LIKE_MID_NOCASE:
			return checkLikeMidNoCase(value);
		case LIKE_RIGHT_NOCASE:
			return checkLikeRightNoCase(value);
		case FIELD_EQ_NOCASE:
			return checkFieldEQNoCase(value);
		case FIELD_GT_NOCASE:
			return checkFieldGTNoCase(value);
		case FIELD_GE_NOCASE:
			return checkFieldGENoCase(value);
		case FIELD_LT_NOCASE:
			return checkFieldLTNoCase(value);
		case FIELD_LE_NOCASE:
			return checkFieldLENoCase(value);
		case FIELD_NE_NOCASE:
			return !checkFieldEQNoCase(value);
		case IN_LIST_NOCASE:
			return checkInListNoCase(value);
		case BETWEEN_NOCASE:
			return checkBetweenNoCase(value);
		case NOT_LIKE_LEFT_NOCASE:
			return !checkLikeLeftNoCase(value);
		case NOT_LIKE_MID_NOCASE:
			return !checkLikeMidNoCase(value);
		case NOT_LIKE_RIGHT_NOCASE:
			return !checkLikeRightNoCase(value);
		case NOT_IN_LIST_NOCASE:
			return !checkInListNoCase(value);
		case NOT_BETWEEN_NOCASE:
			return !checkBetweenNoCase(value);
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns a string representation of this condition.
	 * 
	 * @return A string representation of this condition.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (isNoCase()) {
			b.append("UPPER(");
		}
		b.append(getField().getName());
		if (isNoCase()) {
			b.append(")");
		}

		switch (getOperator()) {
		case LIKE_LEFT:
		case LIKE_LEFT_NOCASE:
		case NOT_LIKE_LEFT:
		case NOT_LIKE_LEFT_NOCASE:
			if (isNot()) {
				b.append(" NOT");
			}
			b.append(" LIKE '");
			b.append(toString(getValues().get(0), isNoCase()));
			b.append("%'");
			break;
		case LIKE_MID:
		case LIKE_MID_NOCASE:
		case NOT_LIKE_MID:
		case NOT_LIKE_MID_NOCASE:
			if (isNot()) {
				b.append(" NOT");
			}
			b.append(" LIKE '%");
			b.append(toString(getValues().get(0), isNoCase()));
			b.append("%'");
			break;
		case LIKE_RIGHT:
		case LIKE_RIGHT_NOCASE:
		case NOT_LIKE_RIGHT:
		case NOT_LIKE_RIGHT_NOCASE:
			if (isNot()) {
				b.append(" NOT");
			}
			b.append(" LIKE '%");
			b.append(toString(getValues().get(0), isNoCase()));
			b.append("'");
			break;
		case FIELD_EQ:
		case FIELD_EQ_NOCASE:
			b.append(" = ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case FIELD_GT:
		case FIELD_GT_NOCASE:
			b.append(" > ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case FIELD_GE:
		case FIELD_GE_NOCASE:
			b.append(" >= ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case FIELD_LT:
		case FIELD_LT_NOCASE:
			b.append(" < ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case FIELD_LE:
		case FIELD_LE_NOCASE:
			b.append(" <= ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case FIELD_NE:
		case FIELD_NE_NOCASE:
			b.append(" != ");
			b.append(toString(getValues().get(0), isNoCase()));
			break;
		case IN_LIST:
		case IN_LIST_NOCASE:
		case NOT_IN_LIST:
		case NOT_IN_LIST_NOCASE:
			if (isNot())
				b.append(" NOT");
			b.append(" IN (");
			for (int i = 0; i < getValues().size(); i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append(toString(getValues().get(i), isNoCase()));
			}
			b.append(")");
			break;
		case IS_NULL:
		case NOT_IS_NULL:
			b.append(" IS");
			if (isNot()) {
				b.append(" NOT");
			}
			b.append(" NULL");
			break;
		case BETWEEN:
		case BETWEEN_NOCASE:
		case NOT_BETWEEN:
		case NOT_BETWEEN_NOCASE:
			if (isNot()) {
				b.append(" NOT");
			}
			b.append(" BETWEEN ");
			b.append(toString(getValues().get(0), isNoCase()));
			b.append(" AND ");
			b.append(toString(getValues().get(1), isNoCase()));
			break;
		default:
			throw new IllegalStateException();
		}
		return b.toString();
	}

	/**
	 * Returns the value converted to string, uppercase if applies.
	 * 
	 * @param value The value.
	 * @param noCase The no case flag.
	 * @return The value converted to string.
	 */
	private String toString(Value value, boolean noCase) {
		if (noCase) {
			return value.toString().toUpperCase();
		}
		return value.toString();
	}
}
