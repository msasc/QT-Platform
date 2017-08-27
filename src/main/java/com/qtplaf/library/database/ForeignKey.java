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
import java.util.Collection;
import java.util.Objects;

/**
 * A table foreign key.
 *
 * @author Miquel Sas
 */
public class ForeignKey extends ArrayList<ForeignKey.Segment> {

	/**
	 * Enumerate the ON DELETE actions
	 */
	public enum OnDelete {

		/**
		 * Constant for ON DELETE [NO ACTION|RESTRICT]
		 */
		RESTRICT,
		/**
		 * Constant for ON DELETE [CASCADE]
		 */
		CASCADE,
		/**
		 * Constant for ON DELETE [SET NULL]
		 */
		SET_NULL;
	}

	/**
	 * A segment packs the local and foreign fields.
	 */
	public static class Segment {

		/**
		 * The local field.
		 */
		private Field localField;
		/**
		 * The foreign field.
		 */
		private Field foreignField;
		/**
		 * Local field alias.
		 */
		private String localFieldAlias;
		/**
		 * Foreign field alias.
		 */
		private String foreignFieldAlias;
		/**
		 * The parent foreign key or relation.
		 */
		private ForeignKey parent;

		/**
		 * Default constructor.
		 */
		public Segment() {
			super();
		}

		/**
		 * Constructor assigning the fields.
		 *
		 * @param localField The local field.
		 * @param foreignField The foreign field.
		 */
		public Segment(Field localField, Field foreignField) {
			super();
			this.localField = localField;
			this.foreignField = foreignField;
		}

		/**
		 * Set the parent foreign key or relation.
		 * 
		 * @param parent The parent foreign key or relation.
		 */
		public void setParent(ForeignKey parent) {
			this.parent = parent;
		}

		/**
		 * Returns the parent foreign key or relation.
		 * 
		 * @return The parent foreign key or relation.
		 */
		public ForeignKey getParent() {
			return parent;
		}

		/**
		 * Get the local field.
		 *
		 * @return The local field.
		 */
		public Field getLocalField() {
			return localField;
		}

		/**
		 * Get the foreign field.
		 *
		 * @return The foreign field.
		 */
		public Field getForeignField() {
			return foreignField;
		}

		/**
		 * Set the local field.
		 *
		 * @param localField The local field.
		 */
		public void setLocalField(Field localField) {
			this.localField = localField;
		}

		/**
		 * Set the foreign field.
		 *
		 * @param foreignField The foreign field.
		 */
		public void setForeignField(Field foreignField) {
			this.foreignField = foreignField;
		}

		/**
		 * Returns the local field alias.
		 * 
		 * @return The local field alias.
		 */
		public String getLocalFieldAlias() {
			return localFieldAlias;
		}

		/**
		 * Sets the local field alias.
		 * 
		 * @param localFieldAlias The local field alias.
		 */
		public void setLocalFieldAlias(String localFieldAlias) {
			this.localFieldAlias = localFieldAlias;
		}

		/**
		 * Returns the foreign field alias.
		 * 
		 * @return The foreign field alias.
		 */
		public String getForeignFieldAlias() {
			return foreignFieldAlias;
		}

		/**
		 * Sets the foreign field alias.
		 * 
		 * @param foreignFieldAlias The foreign field alias.
		 */
		public void setForeignFieldAlias(String foreignFieldAlias) {
			this.foreignFieldAlias = foreignFieldAlias;
		}

		/**
		 * Appends this segment to an SQL select construction.
		 *
		 * @param b The string builder where the SQL query is being appended.
		 */
		public void appendToSQL(StringBuilder b) {
			b.append(localField.getNameRelate());
			b.append(" = ");
			b.append(foreignField.getNameRelate());
		}

		/**
		 * Returns the hash code for this order key.
		 *
		 * @return The hash code
		 */
		@Override
		public int hashCode() {
			int hash = 0;
			hash ^= localField.hashCode();
			hash ^= foreignField.hashCode();
			return hash;
		}

		/**
		 * Check whether the argument object is equal to this order segment.
		 *
		 * @param obj The object to compare
		 * @return A boolean.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Segment other = (Segment) obj;
			if (!Objects.equals(this.localField, other.localField)) {
				return false;
			}
			return Objects.equals(this.foreignField, other.foreignField);
		}

		/**
		 * Returns a string representation of this segment.
		 *
		 * @return A string
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(64);
			b.append(getLocalField().getName());
			b.append(" -> ");
			b.append(getForeignField().getName());
			return b.toString();
		}
	}

	/**
	 * The name of this constraint.
	 */
	private String name = null;
	/**
	 * The local table.
	 */
	private Table localTable = null;
	/**
	 * The foreign table.
	 */
	private Table foreignTable = null;
	/**
	 * The type of deletion restriction: default RESTRICT.
	 */
	private OnDelete deleteRestriction = OnDelete.RESTRICT;
	/**
	 * A boolean that indicates if the foreing key is persistent.
	 */
	private boolean persistent = false;

	/**
	 * Default constructor.
	 */
	public ForeignKey() {
		super();
	}

	/**
	 * Constructor indicating if this foreign key is persisten.
	 * 
	 * @param persistent A boolean that indicates if the foreing key is persistent.
	 */
	public ForeignKey(boolean persistent) {
		super();
		this.persistent = persistent;
	}

	/**
	 * Add a segment to this foreign key.
	 *
	 * @param localField The local field.
	 * @param foreignField The foreign field.
	 */
	public void add(Field localField, Field foreignField) {
		add(new ForeignKey.Segment(localField, foreignField));
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param segment element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 */
	@Override
	public boolean add(Segment segment) {
		segment.setParent(this);
		return super.add(segment);
	}

	/**
	 * Inserts the specified element at the specified position in this list. Shifts the element currently at that
	 * position (if any) and any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param segment element to be inserted
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	@Override
	public void add(int index, Segment segment) {
		segment.setParent(this);
		super.add(index, segment);
	}

	/**
	 * Appends all of the elements in the specified collection to the end of this list, in the order that they are
	 * returned by the specified collection's Iterator. The behavior of this operation is undefined if the specified
	 * collection is modified while the operation is in progress. (This implies that the behavior of this call is
	 * undefined if the specified collection is this list, and this list is nonempty.)
	 *
	 * @param segments collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws NullPointerException if the specified collection is null
	 */
	@Override
	public boolean addAll(Collection<? extends Segment> segments) {
		for (Segment segment : segments) {
			segment.setParent(this);
		}
		return super.addAll(segments);
	}

	/**
	 * Inserts all of the elements in the specified collection into this list, starting at the specified position.
	 * Shifts the element currently at that position (if any) and any subsequent elements to the right (increases their
	 * indices). The new elements will appear in the list in the order that they are returned by the specified
	 * collection's iterator.
	 *
	 * @param index index at which to insert the first element from the specified collection
	 * @param segments collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 * @throws NullPointerException if the specified collection is null
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Segment> segments) {
		for (Segment segment : segments) {
			segment.setParent(this);
		}
		return super.addAll(index, segments);
	}

	/**
	 * Get the name of this foreign key.
	 *
	 * @return The foreign key name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this foreign key.
	 *
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a boolean that indicates if the foreing key is persistent.
	 * 
	 * @return A boolean that indicates if the foreing key is persistent.
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * Sets a boolean that indicates if the foreing key is persistent.
	 * 
	 * @param persistent A boolean that indicates if the foreing key is persistent.
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Get the local table.
	 *
	 * @return The local table
	 */
	public Table getLocalTable() {
		return localTable;
	}

	/**
	 * Set the local table.
	 *
	 * @param localTable The local table
	 */
	public void setLocalTable(Table localTable) {
		this.localTable = localTable;
	}

	/**
	 * Getthe foreign table.
	 *
	 * @return The foreign table.
	 */
	public Table getForeignTable() {
		return foreignTable;
	}

	/**
	 * Set the foreign table.
	 *
	 * @param foreignTable The foreign table
	 */
	public void setForeignTable(Table foreignTable) {
		this.foreignTable = foreignTable;
	}

	/**
	 * Get the delete restriction.
	 *
	 * @return The delete restriction.
	 */
	public OnDelete getDeleteRestriction() {
		return deleteRestriction;
	}

	/**
	 * Set the delete restriction.
	 *
	 * @param deleteRestriction The delete restriction.
	 */
	public void setDeleteRestriction(OnDelete deleteRestriction) {
		this.deleteRestriction = deleteRestriction;
	}

	/**
	 * Returns the default outer relation that could be build with this foreign key.
	 * 
	 * @return The rrelation.
	 */
	public Relation getRelation() {
		return getRelation(true, null, null);
	}

	/**
	 * Returns the relation that could be build based on this foreign key.
	 * 
	 * @param outer The outer flag.
	 * @param localTableAlias The local table alias.
	 * @param foreignTableAlias The foreign table alias.
	 * @return The relation.
	 */
	public Relation getRelation(boolean outer, String localTableAlias, String foreignTableAlias) {
		Relation relation = new Relation();
		relation.setLocalTable(localTable);
		relation.setForeignTable(foreignTable);
		relation.setOuter(outer);
		relation.setLocalTableAlias(localTableAlias);
		relation.setForeignTableAlias(foreignTableAlias);
		for (ForeignKey.Segment segment : this) {
			Relation.Segment relationSegment = new Relation.Segment();
			relationSegment.setLocalField(segment.getLocalField());
			relationSegment.setForeignField(segment.getForeignField());
			relation.add(relationSegment);
		}
		return relation;
	}

	/**
	 * Returns the local field index or -1.
	 * 
	 * @param field The field.
	 * @return The index.
	 */
	public int getLocalFieldIndex(Field field) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getLocalField().equals(field)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the foreign field index or -1.
	 * 
	 * @param field The field.
	 * @return The index.
	 */
	public int getForeignFieldIndex(Field field) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getForeignField().equals(field)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a boolean indicating if the field is contained as a local field.
	 * 
	 * @param field The field.
	 * @return A boolean.
	 */
	public boolean containsLocalField(Field field) {
		return getLocalFieldIndex(field) >= 0;
	}

	/**
	 * Returns a boolean indicating if the field is contained as a foreign field.
	 * 
	 * @param field The field.
	 * @return A boolean.
	 */
	public boolean containsForeignField(Field field) {
		return getForeignFieldIndex(field) >= 0;
	}

	/**
	 * Return a string representation of this foreign key.
	 *
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(64);
		b.append(getLocalTable().getName());
		b.append(" -> ");
		b.append(getForeignTable().getName());
		return b.toString();
	}

	/**
	 * Returns the hashcode for this object.
	 *
	 * @return The hashcode.
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash ^= Objects.hashCode(this.name);
		hash ^= Objects.hashCode(this.localTable);
		hash ^= Objects.hashCode(this.foreignTable);
		hash ^= Objects.hashCode(this.deleteRestriction);
		return hash;
	}

	/**
	 * Check if the argument object is equal to this foreign key.
	 *
	 * @param obj The object to compare for equality.
	 * @return A boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ForeignKey other = (ForeignKey) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.localTable, other.localTable)) {
			return false;
		}
		if (!Objects.equals(this.foreignTable, other.foreignTable)) {
			return false;
		}
		if (this.deleteRestriction != other.deleteRestriction) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(other.get(i))) {
				return false;
			}
		}
		return true;
	}
}
