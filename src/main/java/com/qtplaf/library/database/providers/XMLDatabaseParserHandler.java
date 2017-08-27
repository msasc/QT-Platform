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
package com.qtplaf.library.database.providers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Validator;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.util.Alignment;
import com.qtplaf.library.util.xml.ParserHandler;

/**
 * A handler to parse database members like fields, tables or views.
 * 
 * @author Miquel Sas
 */
public class XMLDatabaseParserHandler extends ParserHandler {

	/**
	 * The XML database provider that instantiated this handler.
	 */
	private XMLDatabaseProvider provider;

	/**
	 * A flag that indicates if the current field being parsed has cleared the list of possible values, necessary if
	 * possible values are set through a field and then overwritten.
	 */
	private boolean fieldPossibleValuesCleared = false;

	/**
	 * Constructor assigning the provider.
	 * 
	 * @param provider The XML database provider.
	 */
	public XMLDatabaseParserHandler(XMLDatabaseProvider provider) {
		super();
		this.provider = provider;
	}

	/**
	 * Returns the parsed field.
	 * 
	 * @return The parsed field.
	 * @throws UnsupportedOperationException If such an error occurs.
	 */
	public Field getField() {
		Object object = getDeque().getLast();
		if (object instanceof Field) {
			return (Field) object;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the parsed table.
	 * 
	 * @return The parsed table.
	 * @throws UnsupportedOperationException If such an error occurs.
	 */
	public Table getTable() {
		Object object = getDeque().getLast();
		if (object instanceof Table) {
			return (Table) object;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Called to notify an element start.
	 */
	@Override
	public void elementStart(String namespace, String elementName, String path, Attributes attributes)
		throws SAXException {

		try {

			// element: field
			if (path.equals("field")) {
				// Initialize the field to parse adding it to the deque.
				getDeque().addFirst(new Field());
				// Set field attributes
				setFieldAttributes(getField(), attributes);
			}

			// element: field/possible-values/possible-value
			if (path.equals("field/possible-values/possible-value")) {
				addFieldPossibleValue(getField(), attributes);
			}

			// element: table
			if (path.equals("table")) {
				// Initialize the table to parse adding it to the deque.
				getDeque().addFirst(new Table());
				// Set table attributes
				setTableAttributes(getTable(), attributes);
			}

			// element: table/fields/field
			if (path.equals("table/fields/field")) {
				// Initialize the field to parse and add it to the deque.
				Field field = new Field();
				getDeque().addFirst(field);
				// If there is a master-field reference, instantiate it.
				String smfield = attributes.getValue("master-field");
				if (smfield != null) {
					Field mfield = provider.getDatabase().getField(smfield);
					if (mfield != null) {
						field.put(mfield);
					}
				}
				// Set declared attributes inherited from field.
				setFieldAttributes(field, attributes);
				// Set attributes not contained in the field.
				// Set the flag that controls clearing possible values to false.
				fieldPossibleValuesCleared = false;
			}

			// element: table/fields/field/possible-values
			if (path.equals("table/fields/field/possible-values/possible-value")) {
				Field field = (Field) getDeque().removeFirst();
				if (!fieldPossibleValuesCleared) {
					field.clearPossibleValues();
					fieldPossibleValuesCleared = true;
				}
				addFieldPossibleValue(field, attributes);
			}

		} catch (Exception exc) {
			throw new SAXException(exc);
		}
	}

	/**
	 * Called to notify an element body.
	 */
	@Override
	public void elementBody(String namespace, String elementName, String path, String text) throws SAXException {

		// element: field/description
		if (path.equals("field/description")) {
			setFieldDescription(getField(), text);
		}
	}

	/**
	 * Called to notify an element end.
	 */
	@Override
	public void elementEnd(String namespace, String elementName, String path) throws SAXException {

		// element: table/fields/field
		if (path.equals("table/fields/field")) {
			Field field = (Field) getDeque().removeFirst();
			getTable().addField(field);
		}
	}

	/**
	 * Read and set table attributes.
	 * 
	 * @param table The table.
	 * @param attributes The attributes to read.
	 * @throws SAXException If such an error occurs.
	 */
	private void setTableAttributes(Table table, Attributes attributes) throws SAXException {
		try {
			String name = attributes.getValue("name");
			if (name != null) {
				table.setName(name);
			}
			String alias = attributes.getValue("alias");
			if (alias != null) {
				table.setAlias(alias);
			}
			String spersistent = attributes.getValue("persistent");
			if (spersistent != null && !spersistent.isEmpty()) {
				boolean persistent = Boolean.parseBoolean(spersistent);
				table.setPersistent(persistent);
			}
			String spersistent_constraints = attributes.getValue("persistent-constraints");
			if (spersistent_constraints != null && !spersistent_constraints.isEmpty()) {
				boolean persistent_constraints = Boolean.parseBoolean(spersistent_constraints);
				table.setPersistentConstraints(persistent_constraints);
			}
			String schema = attributes.getValue("schema");
			if (schema != null) {
				table.setSchema(schema);
			}
			String title = attributes.getValue("title");
			if (title != null) {
				table.setTitle(title);
			}
			String description = attributes.getValue("description");
			if (description != null) {
				table.setDescription(description);
			}
		} catch (Exception exc) {
			throw new SAXException(exc);
		}
	}

	/**
	 * Set the field description, called from an <i>elementBody</i> callback.
	 * 
	 * @param field The field.
	 * @param text The text from the element body.
	 */
	private void setFieldDescription(Field field, String text) {
		if (text != null && text.isEmpty()) {
			field.setDescription(text);
		}
	}

	/**
	 * Add a possible value to a field.
	 * 
	 * @param field The field.
	 * @param attributes The parser attributes.
	 * @throws SAXException If such an error occurs.
	 */
	private void addFieldPossibleValue(Field field, Attributes attributes) throws SAXException {
		try {
			String svalue = attributes.getValue("value");
			if (svalue != null && !svalue.isEmpty()) {
				Value value = field.getDefaultValue();
				value.fromStringUnformatted(svalue);
				field.addPossibleValue(value);
			}
		} catch (Exception exc) {
			throw new SAXException(exc);
		}
	}

	/**
	 * Read and set attributes to the field, given we are reading a field element.
	 * 
	 * @param field The field.
	 * @param attributes The attributes to read.
	 * @throws SAXException If such an error occurs.
	 */
	private void setFieldAttributes(Field field, Attributes attributes) throws SAXException {

		try {
			String name = attributes.getValue("name");
			if (name != null) {
				field.setName(name);
			}
			String alias = attributes.getValue("alias");
			if (alias != null) {
				field.setAlias(alias);
			}
			String stype = attributes.getValue("type");
			if (stype != null) {
				field.setType(Types.parseType(stype));
			}

			String slength = attributes.getValue("length");
			if (slength != null && !slength.isEmpty()) {
				int length = Integer.parseInt(slength);
				field.setLength(length);
			}

			String sdecimals = attributes.getValue("decimals");
			if (sdecimals != null && !sdecimals.isEmpty()) {
				int decimals = Integer.parseInt(sdecimals);
				field.setDecimals(decimals);
			}

			String spersistent = attributes.getValue("persistent");
			if (spersistent != null && !spersistent.isEmpty()) {
				boolean persistent = Boolean.parseBoolean(spersistent);
				field.setPersistent(persistent);
			}

			String description = attributes.getValue("description");
			if (description != null) {
				field.setDescription(description);
			}
			String title = attributes.getValue("title");
			if (title != null) {
				field.setTitle(title);
			}
			String label = attributes.getValue("label");
			if (label != null) {
				field.setLabel(label);
			}
			String header = attributes.getValue("header");
			if (header != null) {
				field.setHeader(header);
			}

			String smaximumValue = attributes.getValue("maximum-value");
			if (smaximumValue != null && !smaximumValue.isEmpty()) {
				Value maximumValue = field.getDefaultValue();
				maximumValue.fromStringUnformatted(smaximumValue);
				field.setMaximumValue(maximumValue);
			}

			String sminimumValue = attributes.getValue("minimum-value");
			if (sminimumValue != null && !sminimumValue.isEmpty()) {
				Value minimumValue = field.getDefaultValue();
				minimumValue.fromStringUnformatted(sminimumValue);
				field.setMinimumValue(minimumValue);
			}

			String srequired = attributes.getValue("required");
			if (srequired != null && !srequired.isEmpty()) {
				boolean required = Boolean.parseBoolean(srequired);
				field.setRequired(required);
			}

			String snullable = attributes.getValue("nullable");
			if (snullable != null && !snullable.isEmpty()) {
				boolean nullable = Boolean.parseBoolean(snullable);
				field.setNullable(nullable);
			}

			String validatorClassName = attributes.getValue("validator");
			if (validatorClassName != null && !validatorClassName.isEmpty()) {
				Validator<Value> validator = getValidator(validatorClassName);
				field.setValidator(validator);
			}

			String sinitialValue = attributes.getValue("initial-value");
			if (sinitialValue != null && !sinitialValue.isEmpty()) {
				Value initialValue = field.getDefaultValue();
				initialValue.fromStringUnformatted(sinitialValue);
				field.setInitialValue(initialValue);
			}

			String scurrentDateTimeOrTimestamp = attributes.getValue("current-date-time-or-timestamp");
			if (scurrentDateTimeOrTimestamp != null && !scurrentDateTimeOrTimestamp.isEmpty()) {
				boolean currentDateTimeOrTimestamp = Boolean.parseBoolean(scurrentDateTimeOrTimestamp);
				field.setCurrentDateTimeOrTimestamp(currentDateTimeOrTimestamp);
			}

			String shorizontalAlignment = attributes.getValue("horizontal-alignment");
			if (shorizontalAlignment != null && !shorizontalAlignment.isEmpty()) {
				Alignment horizontalAlignment = Alignment.parseAlignment(shorizontalAlignment);
				if (horizontalAlignment.isHorizontal()) {
					field.setHorizontalAlignment(horizontalAlignment);
				}
			}

			String sverticalAlignment = attributes.getValue("vertical-alignment");
			if (sverticalAlignment != null && !sverticalAlignment.isEmpty()) {
				Alignment verticalAlignment = Alignment.parseAlignment(sverticalAlignment);
				if (verticalAlignment.isVertical()) {
					field.setVerticalAlignment(verticalAlignment);
				}
			}

			String sdisplayLength = attributes.getValue("display-length");
			if (sdisplayLength != null && !sdisplayLength.isEmpty()) {
				int displayLength = Integer.parseInt(sdisplayLength);
				field.setDecimals(displayLength);
			}

			String suppercase = attributes.getValue("uppercase");
			if (suppercase != null && !suppercase.isEmpty()) {
				boolean uppercase = Boolean.parseBoolean(suppercase);
				field.setUppercase(uppercase);
			}

			String sminimumWidth = attributes.getValue("minimum-width");
			if (sminimumWidth != null && !sminimumWidth.isEmpty()) {
				int minimumWidth = Integer.parseInt(sminimumWidth);
				field.setMinimumWidth(minimumWidth);
			}

			String smaximumWidth = attributes.getValue("maximum-width");
			if (smaximumWidth != null && !smaximumWidth.isEmpty()) {
				int maximumWidth = Integer.parseInt(smaximumWidth);
				field.setMaximumWidth(maximumWidth);
			}

			String spreferredWidth = attributes.getValue("preferred-width");
			if (spreferredWidth != null && !spreferredWidth.isEmpty()) {
				int preferredWidth = Integer.parseInt(spreferredWidth);
				field.setPreferredWidth(preferredWidth);
			}

			String sautoSize = attributes.getValue("auto-size");
			if (sautoSize != null && !sautoSize.isEmpty()) {
				boolean autoSize = Boolean.parseBoolean(sautoSize);
				field.setAutoSize(autoSize);
			}

			String swidthFactor = attributes.getValue("width-factor");
			if (swidthFactor != null && !swidthFactor.isEmpty()) {
				double widthFactor = Double.parseDouble(swidthFactor);
				field.setWidthFactor(widthFactor);
			}

			String sfixedWidth = attributes.getValue("fixed-width");
			if (sfixedWidth != null && !sfixedWidth.isEmpty()) {
				boolean fixedWidth = Boolean.parseBoolean(sfixedWidth);
				field.setFixedWidth(fixedWidth);
			}

		} catch (Exception exc) {
			throw new SAXException(exc);
		}

	}

	/**
	 * Returns the validator given its class name.
	 * 
	 * @param className The class name.
	 * @return The validator.
	 * @throws NoSuchMethodException If such an error occurs.
	 * @throws SecurityException If such an error occurs.
	 * @throws ClassNotFoundException If such an error occurs.
	 * @throws InstantiationException If such an error occurs.
	 * @throws IllegalAccessException If such an error occurs.
	 * @throws IllegalArgumentException If such an error occurs.
	 * @throws InvocationTargetException If such an error occurs.
	 */
	private Validator<Value> getValidator(String className)
		throws NoSuchMethodException,
		SecurityException,
		ClassNotFoundException,
		InstantiationException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException {
		if (className == null) {
			return null;
		}
		Constructor<?> constructor = Class.forName(className).getConstructor();
		@SuppressWarnings("unchecked")
		Validator<Value> validator = (Validator<Value>) constructor.newInstance();
		return validator;
	}

}
