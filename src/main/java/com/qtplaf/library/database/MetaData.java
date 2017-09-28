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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBUtils;

/**
 * A manager of the underlying database engine JDBC meta data.
 * 
 * @author Miquel Sas
 */
public class MetaData {

	public static final String TABLE_CATALOG = "TABLE_CAT";
	public static final String TABLE_SCHEMA = "TABLE_SCHEM";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String TYPE_NAME = "TYPE_NAME";

	/**
	 * Helper to rapidly create fields.
	 * 
	 * @param name Field name.
	 * @param description Description.
	 * @param type Type.
	 * @param length Length.
	 * @param decimals Decimals.
	 * @param primaryKey primary key indicator.
	 * @return The field definition.
	 */
	private static Field createField(
		String name,
		String description,
		Types type,
		int length,
		int decimals,
		boolean primaryKey) {
		Field field = new Field();
		field.setName(name);
		field.setAlias(name);
		field.setDescription(description);
		field.setType(type);
		field.setLength(length);
		field.setDecimals(decimals);
		return field;
	}

	/**
	 * Returns The CATALOG INFO field list.
	 * 
	 * @return The CATALOG INFO field list
	 */
	public static FieldList getFieldListCatalogInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_CATALOG, "Catalog", Types.STRING, 40, 0, true));
		return fieldList;
	}

	/**
	 * Returns the COLUMN INFO field list.
	 * 
	 * @return The COLUMN INFO field list.
	 */
	public static FieldList getFieldListColumnInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_CATALOG, "Catalog", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_SCHEMA, "Schema", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_NAME, "Table", Types.STRING, 40, 0, false));
		fieldList.addField(createField(COLUMN_NAME, "Column", Types.STRING, 40, 0, false));
		fieldList.addField(createField(DATA_TYPE, "Data type", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField(TYPE_NAME, "Type name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("COLUMN_SIZE", "Column size", Types.LONG, 0, 0, false));
		fieldList.addField(createField("BUFFER_LENGTH", "Buffer length", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("DECIMAL_DIGITS", "Decimal digits", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("NUM_PREC_RADIX", "Num prec radix", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("NULLABLE", "Nullable", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("REMARKS", "Remarks", Types.STRING, 128, 0, false));
		fieldList.addField(createField("COLUMN_DEF", "Column def", Types.STRING, 128, 0, false));
		fieldList.addField(createField("SQL_DATA_TYPE", "SQL data type", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("SQL_DATETIME_SUB", "SQL date time sub", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("CHAR_OCTET_LENGTH", "Char octet length", Types.LONG, 0, 0, false));
		fieldList.addField(createField("ORDINAL_POSITION", "Ordinal position", Types.INTEGER, 0, 0, true));
		fieldList.addField(createField("IS_NULLABLE", "Is nullable", Types.STRING, 5, 0, false));
		fieldList.addField(createField("DATA_TYPE_NAME", "Data type name", Types.STRING, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the INDEX INFO field list.
	 * 
	 * @return The INDEX INFO field list
	 */
	public static FieldList getFieldListIndexInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_CATALOG, "Catalog", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_SCHEMA, "Schema", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_NAME, "Table", Types.STRING, 40, 0, true));
		fieldList.addField(createField("NON_UNIQUE", "Non unique", Types.BOOLEAN, 1, 0, false));
		fieldList.addField(createField("INDEX_QUALIFIER", "Index qualifier", Types.STRING, 40, 0, false));
		fieldList.addField(createField("INDEX_NAME", "Index name", Types.STRING, 40, 0, false));
		fieldList.addField(createField("TYPE", "Index type", Types.INTEGER, 40, 0, false));
		fieldList.addField(createField("ORDINAL_POSITION", "Ordinal position", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField(COLUMN_NAME, "Column name", Types.STRING, 0, 0, false));
		fieldList.addField(createField("ASC_OR_DESC", "Asc/desc", Types.STRING, 2, 0, false));
		fieldList.addField(createField("CARDINALITY", "Cardinality", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("PAGES", "Pages", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("FILTER_CONDITION", "Filter condition", Types.STRING, 128, 0, false));
		fieldList.addField(createField("INDEX_TYPE_DESC", "Index type desc", Types.STRING, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the TABLE INFO field list.
	 * 
	 * @return The TABLE INFO field list.
	 */
	public static FieldList getFieldListTableInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_CATALOG, "Catalog", Types.STRING, 40, 0, false));
		fieldList.addField(createField(TABLE_SCHEMA, "Schema", Types.STRING, 40, 0, false));
		fieldList.addField(createField(TABLE_NAME, "Table", Types.STRING, 40, 0, false));
		fieldList.addField(createField("TABLE_TYPE", "Table type", Types.STRING, 40, 0, false));
		fieldList.addField(createField("REMARKS", "Remarks", Types.STRING, 128, 0, false));
		return fieldList;
	}

	/**
	 * Returns the PRIMARY KEY INFO field list.
	 * 
	 * @return The PRIMARY KEY INFO field list.
	 */
	public static FieldList getFieldListPrimaryKeyInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_CATALOG, "Catalog", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_SCHEMA, "Schema", Types.STRING, 40, 0, true));
		fieldList.addField(createField(TABLE_NAME, "Table", Types.STRING, 40, 0, true));
		fieldList.addField(createField(COLUMN_NAME, "Column", Types.STRING, 40, 0, true));
		fieldList.addField(createField("KEY_SEQ", "Key seq", Types.INTEGER, 4, 0, true));
		fieldList.addField(createField("PK_NAME", "PK name", Types.STRING, 40, 0, false));
		return fieldList;
	}

	/**
	 * Returns the SCHEMA INFO field list.
	 * 
	 * @return The SCHEMA INFO field list.
	 */
	public static FieldList getFieldListSchemaInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TABLE_SCHEMA, "Schema", Types.STRING, 40, 0, true));
		return fieldList;
	}

	/**
	 * Returns the TYPE INFO field list.
	 * 
	 * @return The TYPE INFO field list.
	 */
	public static FieldList getFieldListTypeInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField(TYPE_NAME, "Type name", Types.STRING, 20, 0, true));
		fieldList.addField(createField(DATA_TYPE, "Data type", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("PRECISION", "Precision", Types.LONG, 0, 0, false));
		fieldList.addField(createField("LITERAL_PREFIX", "Literal prefix", Types.STRING, 5, 0, false));
		fieldList.addField(createField("LITERAL_SUFFIX", "Literal suffix", Types.STRING, 5, 0, false));
		fieldList.addField(createField("CREATE_PARAMS", "Create params", Types.STRING, 20, 0, false));
		fieldList.addField(createField("NULLABLE", "Nullable", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("CASE_SENSITIVE", "Case sensitive", Types.BOOLEAN, 0, 0, false));
		fieldList.addField(createField("SEARCHABLE", "Searcheable", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("UNSIGNED_ATTRIBUTE", "Unsigned attribute", Types.BOOLEAN, 0, 0, false));
		fieldList.addField(createField("FIXED_PREC_SCALE", "Fixed prec scale", Types.BOOLEAN, 0, 0, false));
		fieldList.addField(createField("AUTO_INCREMENT", "Auto increment", Types.BOOLEAN, 0, 0, false));
		fieldList.addField(createField("LOCAL_TYPE_NAME", "Local type name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("MINIMUM_SCALE", "Minimum scale", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("MAXIMUM_SCALE", "Maximum scale", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("SQL_DATA_TYPE", "SQL data type", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("SQL_DATETIME_SUB", "SQL date time sub", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("NUM_PREC_RADIX", "Num prec radix", Types.INTEGER, 0, 0, false));
		fieldList.addField(createField("DATA_TYPE_NAME", "Data type name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("NULLABLE_DESC", "Nullable desc", Types.STRING, 20, 0, false));
		return fieldList;
	}

	/**
	 * Returns the FOREIGN KEY INFO field list.
	 * 
	 * @return The FOREIGN KEY INFO field list.
	 */
	public static FieldList getFieldListForeignKeyInfo() {
		FieldList fieldList = new FieldList();
		fieldList.addField(createField("PKTABLE_CAT", "PK Table catalog", Types.STRING, 20, 0, true));
		fieldList.addField(createField("PKTABLE_SCHEM", "PK Table schema", Types.STRING, 20, 0, false));
		fieldList.addField(createField("PKTABLE_NAME", "PK Table name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("PKCOLUMN_NAME", "PK Column name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("FKTABLE_CAT", "FK Table catalog", Types.STRING, 20, 0, false));
		fieldList.addField(createField("FKTABLE_SCHEM", "FK Table schema", Types.STRING, 20, 0, false));
		fieldList.addField(createField("FKTABLE_NAME", "FK Table name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("FKCOLUMN_NAME", "FK Column name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("KEY_SEQ", "Key sequence", Types.INTEGER, 2, 0, false));
		fieldList.addField(createField("UPDATE_RULE", "Update rule", Types.INTEGER, 2, 0, false));
		fieldList.addField(createField("DELETE_RULE", "Delete rule", Types.INTEGER, 2, 0, false));
		fieldList.addField(createField("FK_NAME", "FK Name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("PK_NAME", "PK Name", Types.STRING, 20, 0, false));
		fieldList.addField(createField("DEFERRABILITY", "Deferrability", Types.INTEGER, 20, 0, false));
		return fieldList;
	}

	/**
	 * The database engine.
	 */
	private DBEngine dbEngine;

	/**
	 * Constructor assigning the <i>DBEngine</i>.
	 * 
	 * @param dbEngine The <i>DBEngine</i>.
	 */
	public MetaData(DBEngine dbEngine) {
		super();
		this.dbEngine = dbEngine;
	}

	/**
	 * Reads the correspondent record set.
	 * 
	 * @param rs The JDBC result set
	 * @param fieldList The applying field list
	 * @return The record set.
	 * @throws SQLException If an SQL error occurs.
	 */
	private RecordSet readRecordSet(ResultSet rs, FieldList fieldList) throws SQLException {
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(fieldList);
		while (rs.next()) {
			Record record = DBUtils.readRecord(fieldList, rs);
			recordSet.add(record);
		}
		rs.close();
		return recordSet;
	}

	/**
	 * Returns the catalogs recordset.
	 *
	 * @return The recordset.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetCatalogs() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getCatalogs();
			recordSet = readRecordSet(rs, getFieldListCatalogInfo());
			return recordSet;
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
	}

	/**
	 * Returns the schema recordset.
	 *
	 * @return The recordset.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetSchemas() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getSchemas();
			recordSet = readRecordSet(rs, getFieldListSchemaInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns the table recordset (only TABLE types).
	 *
	 * @param schema The table schema or null
	 * @return A recordset with table definition.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetTables(String schema) throws SQLException {
		return getRecordSetTables(null, schema, null, "TABLE");
	}

	/**
	 * Returns the table recordset (only TABLE types).
	 *
	 * @param schema The table schema or null
	 * @param table The table name prefix or null
	 * @return A recordset with table definition.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetTables(String schema, String table) throws SQLException {
		return getRecordSetTables(null, schema, table, "TABLE");
	}

	/**
	 * Returns the table recordset.
	 *
	 * @param catalog The table catalog or null
	 * @param schema The table schema or null
	 * @param table The table name prefix or null
	 * @param types An array of possible table types
	 * @return A recordset with table definition.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetTables(String catalog, String schema, String table, String... types)
		throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getTables(catalog, schema, table, types);
			recordSet = readRecordSet(rs, getFieldListTableInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the columns of a table.
	 *
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetColumns(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getColumns(catalog, schema, table, null);
			recordSet = readRecordSet(rs, getFieldListColumnInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the indexes of a table.
	 *
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetIndexes(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getIndexInfo(catalog, schema, table, false, false);
			recordSet = readRecordSet(rs, getFieldListIndexInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the primary key of a table.
	 *
	 * @param catalog Catalog name.
	 * @param schema Schema name.
	 * @param table Table name.
	 * @return the RecordSet
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetPrimaryKey(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getPrimaryKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListPrimaryKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the imported keys of a table. In our db system theese are foreign
	 * keys of the argument table.
	 * 
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetImportedKeys(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getImportedKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListForeignKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the exported keys of a table. In our db system theese are referrers
	 * of the argument table.
	 * 
	 * @return The record set.
	 * @param catalog The catalog name
	 * @param schema The schema name.
	 * @param table The table name.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetExportedKeys(String catalog, String schema, String table) throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getExportedKeys(catalog, schema, table);
			recordSet = readRecordSet(rs, getFieldListForeignKeyInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Returns a record set with information about the database types.
	 *
	 * @return The record set.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet getRecordSetTypes() throws SQLException {
		Connection cn = null;
		RecordSet recordSet = null;
		try {
			cn = dbEngine.getConnection();
			ResultSet rs = cn.getMetaData().getTypeInfo();
			recordSet = readRecordSet(rs, getFieldListTypeInfo());
		} finally {
			if (cn != null && !cn.isClosed())
				cn.close();
		}
		return recordSet;
	}

	/**
	 * Check if the schema exists, case insensitive.
	 * 
	 * @param schema The schema name.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsSchema(String schema) throws SQLException {
		RecordSet recordSet = getRecordSetSchemas();
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			if (record.getValue(TABLE_SCHEMA).getString().toLowerCase().equals(schema.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the table exists, case insensitive.
	 * 
	 * @param schema The schema.
	 * @param table The table name.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsTable(String schema, String table) throws SQLException {
		RecordSet recordSet = getRecordSetTables(schema);
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			if (record.getValue(TABLE_NAME).getString().toLowerCase().equals(table.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
