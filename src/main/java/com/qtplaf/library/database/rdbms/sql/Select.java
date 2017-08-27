/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.sql;

import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Filter;
import com.qtplaf.library.database.Relation;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;

/**
 * A builder of simple SELECT queries.
 *
 * @author Miquel Sas
 */
public class Select extends Statement {

	/**
	 * The view (combination of tables) to select.
	 */
	private View view;
	/**
	 * The filter.
	 */
	private Filter filter;
	/**
	 * A flag indicating if it's a DISTINCT view.
	 */
	private boolean distinct = false;

	/**
	 * Default constructor.
	 */
	public Select() {
		super();
	}

	/**
	 * Check if this SELECT query should express relations in the explicit form.
	 *
	 * @return A boolean
	 */
	public boolean isExplicitRelation() {
		if (getDBEngineAdapter() == null) {
			throw new IllegalStateException("The database adapter must be set");
		}
		return getDBEngineAdapter().isExplicitRelation();
	}

	/**
	 * Check if this SELECT query should express relations in the implicit form.
	 *
	 * @return A boolean
	 */
	public boolean isImplicitRelation() {
		return !isExplicitRelation();
	}

	/**
	 * Returns the view.
	 *
	 * @return The view.
	 */
	public View getView() {
		return view;
	}

	/**
	 * Set the view to select.
	 *
	 * @param view The view.
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Returns the filter.
	 *
	 * @return The filter.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Set this select filter for the where clause.
	 *
	 * @param filter The filter.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Set this select filter for the where clause.
	 *
	 * @param filter The filter.
	 */
	public void setWhere(Filter filter) {
		setFilter(filter);
	}

	/**
	 * Check if this is a distinct view.
	 *
	 * @return A boolean
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * Set the distinct flag.
	 *
	 * @param distinct The distinct flag.
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * Returns the list of values to assign to parameters.
	 *
	 * @return The list of values to assign to parameters.
	 */
	@Override
	public List<Value> getValues() {
		if (getFilter() != null && !getFilter().isEmpty()) {
			return getFilter().getValues();
		}
		return super.getValues();
	}

	/**
	 * Returns this SELECT query as a string.
	 *
	 * @return The query.
	 */
	@Override
	public String toSQL() {
		if (getView() == null) {
			throw new IllegalStateException("Malformed SELECT query: view is null");
		}

		StringBuilder b = new StringBuilder(1024);

		// SELECT
		b.append("SELECT ");
		b.append(isDistinct() ? "DISTINCT " : "");

		// Field list
		b.append(fieldList());

		// No relations.
		if (getView().getRelationCount() == 0) {

			// FROM clause
			b.append(" FROM ");
			b.append(tableList());

			// WHERE clause if there is a filter
			if (getFilter() != null && !getFilter().isEmpty()) {
				b.append(" WHERE ");
				b.append(getFilter().toSQL());
			}
		}

		// With relations.
		if (getView().getRelationCount() != 0) {

			// Explicit relation
			if (isExplicitRelation()) {

				// FROM clause
				b.append(" FROM ");
				b.append(relateExplicit(getView().getMasterTable(), getView()
						.getRelations()));

				// WHERE clause if there is a filter
				if (getFilter() != null) {
					b.append(" WHERE ");
					b.append(getFilter().toSQL());
				}
			}

			// Implicit relation
			if (isImplicitRelation()) {

				// FROM clause
				b.append(" FROM ");
				b.append(tableList());

				// WHERE clause if there is a filter
				b.append(" WHERE ");
				b.append(relateImplicit(getView().getRelations()));
				if (getFilter() != null) {
					b.append(" AND (");
					b.append(getFilter().toSQL());
					b.append(")");
				}
			}
		}

		// GROUP BY clause if necessary
		if (getView().getGroupByCount() > 0) {
			b.append(" GROUP BY ");
			b.append(groupBy());
			if (getView().getHaving() != null) {
				b.append(" HAVING ");
				b.append(getView().getHaving());
			}
		}

		// ORDER BY clause if necessary
		if (getView().getOrderBy() != null) {
			b.append(" ORDER BY ");
			b.append(orderBy());
		}

		return b.toString();
	}

	/**
	 * Returns the field list of the SELECT query.
	 *
	 * @return The field list as a string.
	 */
	private String fieldList() {
		StringBuilder b = new StringBuilder(128);
		int fieldCount = getView().getFieldCount();
		for (int i = 0; i < fieldCount; i++) {
			Field field = getView().getField(i);
			if (field.isPersistent() || field.isVirtual()) {
				if (i > 0) {
					b.append(", ");
				}
				b.append(field.getNameSelect());
			}
		}
		return b.toString();
	}

	/**
	 * Returns the list of tables as a string.
	 *
	 * @return The list tables as a string suited for the query.
	 */
	private String tableList() {
		StringBuilder b = new StringBuilder(128);
		List<Table> tables = getView().getAllTables();
		for (int i = 0; i < tables.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(tables.get(i).getNameFrom());
		}
		return b.toString();
	}

	/**
	 * Returns the group by part of the query.
	 *
	 * @return The group by part.
	 */
	private String groupBy() {
		StringBuilder b = new StringBuilder(128);
		for (int i = 0; i < view.getGroupByCount(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			b.append(view.getGroupBy(i).getNameGroupBy());
		}
		return b.toString();
	}

	/**
	 * Returns the order by part of the query.
	 *
	 * @return The order by part.
	 */
	private String orderBy() {
		StringBuilder b = new StringBuilder(128);
		int size = getView().getOrderBy().size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				b.append(", ");
			}
			Field field = getView().getOrderBy().get(i).getField();
			b.append(field.getNameOrderBy());
			if (!getView().getOrderBy().get(i).isAsc()) {
				b.append(" DESC");
			}
		}
		return b.toString();
	}

	/**
	 * Check if the foreign table is a local table on one of the relations.
	 *
	 * @param foreignTable The foreign table to check.
	 * @param relations The list of relations.
	 * @return A boolean indicating if the foreign table is a local table on one of the relations.
	 */
	private boolean isLocalTable(Table foreignTable, List<Relation> relations) {
		for (Relation relation : relations) {
			if (relation.getLocalTable().equals(foreignTable)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Builds the explicit relation string.
	 *
	 * @param localTable The local table.
	 * @param relations The list of relations.
	 * @return The relation part of the query.
	 */
	private String relateExplicit(Table localTable, List<Relation> relations) {
		StringBuilder b = new StringBuilder(512);
		b.append(localTable.getNameSchema());
		b.append(" AS ");
		b.append(localTable.getAlias());
		for (int i = 0; i < relations.size(); i++) {
			Relation relation = relations.get(i);
			if (relation.getLocalTable().equals(localTable)) {
				if (i > 0) {
					b.insert(0, "(");
					b.append(")");
				}
				b.append(" LEFT");
				b.append((relation.isOuter() ? " OUTER" : ""));
				b.append(" JOIN ");
				if (!isLocalTable(relation.getForeignTable(), relations)) {
					b.append(relation.getForeignTable().getNameSchema());
					b.append(" AS ");
					b.append(relation.getForeignTable().getAlias());
				} else {
					b.append("( ");
					b.append(relateExplicit(relation.getForeignTable(),
							relations));
					b.append(" )");
				}
				b.append(" ON ( ");
				for (int j = 0; j < relation.size(); j++) {
					if (j > 0) {
						b.append(" AND ");
					}
					relation.get(j).appendToSQL(b);
				}
				b.append(" )");
			}
		}
		return b.toString();
	}

	/**
	 * Builds the implicit relation string.
	 *
	 * @param relations The list of relations.
	 * @return The relation part of the query.
	 */
	private String relateImplicit(List<Relation> relations) {
		StringBuilder b = new StringBuilder(256);
		for (int i = 0; i < relations.size(); i++) {
			Relation relation = relations.get(i);
			if (i > 0) {
				b.append(" AND ");
			}
			b.append("( ");
			for (int j = 0; j < relation.size(); j++) {
				if (j > 0) {
					b.append(" AND ");
				}
				relation.get(j).appendToSQL(b);
				if (relation.isOuter()) {
					b.append("(+)");
				}
			}
			b.append(" )");
		}
		return b.toString();
	}
}
