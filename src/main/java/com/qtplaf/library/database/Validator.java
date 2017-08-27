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

import com.qtplaf.library.app.Session;

/**
 * A generic class to validate the convenience of a given type for a certain operation. Methods are intended to be
 * overwritten for convenience.
 *
 * @author Miquel Sas
 * @param <T> The type to validate.
 */
public class Validator<T> {

	/**
	 * Validates the convenience of the given type for the object.
	 *
	 * @param type The type to validate.
	 * @return A boolean indicating whether the type is valid.
	 */
	public boolean validate(T type) {
		return validate(type, null);
	}

	/**
	 * Validates the argument type for the given operation.
	 * 
	 * @param type The type to validate.
	 * @param operation The operation to be performed on the type.
	 * @return A boolean indicating whether the type is valid.
	 */
	public boolean validate(T type, Object operation) {
		return true;
	}

	/**
	 * Returns the validation message related to the type validation. Normally a null should be returned when the
	 * validate method returns true.
	 *
	 * @param session The working session.
	 * @param type The argument type.
	 * @return The validation message or null.
	 */
	public String getMessage(Session session, T type) {
		return getMessage(session, type, null);
	}

	/**
	 * Returns the validation message related to the type validation. Normally a null should be returned when the
	 * validate method returns true.
	 *
	 * @param session The working session.
	 * @param type The argument type.
	 * @param operation The operation to be performed on the type.
	 * @return The validation message or null.
	 */
	public String getMessage(Session session, T type, Object operation) {
		return null;
	}
}
