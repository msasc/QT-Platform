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
package com.qtplaf.library.trading.data.info.validators;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Validator;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.util.NumberUtils;

/**
 * Validator for an double value.
 */
public class DoubleValidator extends Validator<Value> {

	/**
	 * Message label.
	 */
	private String label;
	/**
	 * Minimum.
	 */
	private double minimum = NumberUtils.MIN_DOUBLE;
	/**
	 * Maximum.
	 */
	private double maximum = NumberUtils.MAX_DOUBLE;

	/**
	 * Default constructor.
	 */
	public DoubleValidator() {
		super();
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param label The label.
	 * @param minimum The minimum value.
	 * @param maximum The maximum value.
	 */
	public DoubleValidator(String label, double minimum, double maximum) {
		super();
		this.label = label;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * Returns the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label The message label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the minimum value.
	 * 
	 * @return The minimum value.
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param minimum The minimum value.
	 */
	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	/**
	 * Returns the maximum value.
	 * 
	 * @return The maximum value.
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Set the maximum value.
	 * 
	 * @param maximum The maximum value.
	 */
	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	/**
	 * Validates the convenience of the given type for the object.
	 *
	 * @param value The type to validate.
	 * @return A boolean indicating if the type is valid.
	 */
	@Override
	public boolean validate(Value value) {
		if (value.getDouble() >= minimum && value.getDouble() <= maximum) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the validation message related to the type validation. Normally a null should be returned when the
	 * validate method returns true.
	 *
	 * @param session The working session.
	 * @param value The argument type.
	 * @return The validation message or null.
	 */
	@Override
	public String getMessage(Session session, Value value) {
		if (value.getDouble() < minimum) {
			StringBuilder b = new StringBuilder();
			if (label != null) {
				b.append(label);
			} else {
				b.append("Value");
			}
			b.append(" must be greater than or equal to " + minimum);
			return b.toString();
		}
		if (value.getDouble() > maximum) {
			StringBuilder b = new StringBuilder();
			if (label != null) {
				b.append(label);
			} else {
				b.append("Value");
			}
			b.append(" must be less than or equal to " + maximum);
			return b.toString();
		}
		return null;
	}
}