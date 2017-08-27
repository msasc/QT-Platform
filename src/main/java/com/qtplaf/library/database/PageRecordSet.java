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
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.database.Condition.Operator;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A <tt>PageRecordSet</tt> is a <tt>RecordSet</tt> that first paginates the underliying <tt>Persistor</tt> and
 * <tt>Criteria</tt>. It is aimed to be used when the underlying view is very large and visually browse is required.
 * <p>
 * Note that they can not be greater that <tt>NumberUtils.MAX_INTEGER</tt>.
 * <p>
 * Additionally, this <tt>PageRecordSet</tt> can not be sorted since it strictly uses the underlying order of the
 * persistor.
 * 
 * @author Miquel Sas
 */
public class PageRecordSet extends RecordSet {

	/** Logger instance to log exceptions. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Runable to paginate in a thread.
	 */
	private class Paginator implements Runnable {
		@Override
		public void run() {
			try {
				setPaginating(true);
				paginate();
			} catch (PersistorException exc) {
				LOGGER.catching(exc);
			} finally {
				setPaginating(false);
			}
		}
	}

	/**
	 * Page information.
	 */
	private class Page {
		/** Index of first record. */
		private int firstIndex;
		/** Key of first record. */
		private OrderKey firstKey;
		/** Index of last record. */
		private int lastIndex;
		/** Key of last record. */
		private OrderKey lastKey;

		/**
		 * Default constructor.
		 */
		private Page() {
			super();
		}

		/**
		 * Constructor.
		 * 
		 * @param firstIndex First index.
		 * @param firstKey First key.
		 * @param lastIndex Last index.
		 * @param lastKey Last key.
		 */
		private Page(int firstIndex, OrderKey firstKey, int lastIndex, OrderKey lastKey) {
			this.firstIndex = firstIndex;
			this.firstKey = firstKey;
			this.lastIndex = lastIndex;
			this.lastKey = lastKey;
		}

		/**
		 * Returns the first index.
		 * 
		 * @return The first index.
		 */
		private int getFirstIndex() {
			return firstIndex;
		}

		/**
		 * Set the first index.
		 * 
		 * @param firstIndex The first index.
		 */
		private void setFirstIndex(int firstIndex) {
			this.firstIndex = firstIndex;
		}

		/**
		 * Returns the first key.
		 * 
		 * @return The first key.
		 */
		private OrderKey getFirstKey() {
			return firstKey;
		}

		/**
		 * Set the first key.
		 * 
		 * @param firstKey The first key.
		 */
		private void setFirstKey(OrderKey firstKey) {
			this.firstKey = firstKey;
		}

		/**
		 * Returns the last index.
		 * 
		 * @return The last index.
		 */
		private int getLastIndex() {
			return lastIndex;
		}

		/**
		 * Set the last index.
		 * 
		 * @param lastIndex The last index.
		 */
		private void setLastIndex(int lastIndex) {
			this.lastIndex = lastIndex;
		}

		/**
		 * Returns the last key.
		 * 
		 * @return The last key.
		 */
		private OrderKey getLastKey() {
			return lastKey;
		}

		/**
		 * Set the last key.
		 * 
		 * @param lastKey The last key.
		 */
		private void setLastKey(OrderKey lastKey) {
			this.lastKey = lastKey;
		}

		/**
		 * Check equal.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof Page) {
				Page p = (Page) o;
				return getFirstIndex() == p.getFirstIndex();
			}
			return false;
		}

		/**
		 * Returns the string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("[");
			b.append(Integer.toString(firstIndex));
			b.append(", ");
			b.append(Integer.toString(lastIndex));
			b.append("]");
			b.append(", ");
			b.append("[");
			b.append(firstKey);
			b.append("]");
			b.append(", ");
			b.append("[");
			b.append(lastKey);
			b.append("]");
			return b.toString();
		}
	}

	/**
	 * The persistor that will provide the rows.
	 */
	private Persistor persistor;
	/**
	 * The criteria used to retrieve the rows.
	 */
	private Criteria criteria;
	/**
	 * The stack of pages. The page in the recordset will always be the last.
	 */
	private List<Page> pages = new ArrayList<>();
	/**
	 * The size of the recordset.
	 */
	private int size = -1;
	/**
	 * The page size, default is 100.
	 */
	private int pageSize = 100;
	/**
	 * Current loaded page.
	 */
	private Page currentPage;

	/**
	 * A boolean that indicates whether paginating is on.
	 */
	private boolean paginating = false;

	/**
	 * Constructor.
	 */
	public PageRecordSet() {
		super();
	}

	/**
	 * Constructor assigning the field list.
	 * 
	 * @param fields The list of fields.
	 */
	public PageRecordSet(FieldList fields) {
		super(fields);
	}

	/**
	 * Set the persistor.
	 * 
	 * @param persistor The persistor.
	 */
	public void setPersistor(Persistor persistor) {
		this.persistor = persistor;
	}

	/**
	 * Sets the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Set the page size, default is 100.
	 * 
	 * @param pageSize The page size.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Get a record given its index in the record list.
	 *
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	@Override
	public Record get(int index) {

		// Check pagination.
		checkPaginate();

		try {

			// Page...
			Page page = null;

			// Initial state.
			if (currentPage == null) {
				page = findPage(index);
				loadPage(page);
			}

			// Current loaded page.
			page = currentPage;

			// First check if the required record is in the current loaded page.
			if (inPage(index, page)) {
				return get(index, page);
			}

			// need to find the page.
			page = findPage(index);
			loadPage(page);
			return get(index, page);

		} catch (PersistorException exc) {
			LOGGER.catching(exc);
		}

		return null;
	}

	/**
	 * Check whether the index is in the page.
	 * 
	 * @param index The index.
	 * @param page The page.
	 * @return A boolean.
	 */
	private boolean inPage(int index, Page page) {
		return index >= page.getFirstIndex() && index <= page.getLastIndex();
	}

	/**
	 * Get the record at the view index.
	 * 
	 * @param index The index of the record in the view.
	 * @param page The current loaded page.
	 * @return The record.
	 */
	private Record get(int index, Page page) {
		if (!inPage(index, page)) {
			throw new IllegalArgumentException();
		}
		return super.get(index - page.getFirstIndex());
	}

	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	@Override
	public int size() {
		checkPaginate();
		if (size < 0) {
			try {
				size = (int) persistor.count(criteria);
			} catch (PersistorException exc) {
				LOGGER.catching(exc);
			}
		}
		return size;
	}

	/**
	 * Returns the view order.
	 * 
	 * @return The view order.
	 */
	private Order getOrder() {
		return persistor.getView().getOrderBy();
	}

	/**
	 * Load a selected page.
	 * 
	 * @param page The page to load.
	 * @throws PersistorException If a persistor error occurs.
	 */
	private void loadPage(Page page) throws PersistorException {
		RecordIterator iter = null;
		try {
			clear();

			OrderKey firstKey = page.getFirstKey();
			OrderKey lastKey = page.getLastKey();

			Criteria criteria = new Criteria();
			if (this.criteria != null) {
				criteria.add(this.criteria);
			}
			criteria.add(getCriteria(firstKey, Operator.FIELD_GE));
			criteria.add(getCriteria(lastKey, Operator.FIELD_LE));

			iter = persistor.iterator(criteria, getOrder());
			while (iter.hasNext()) {
				add(iter.next());
			}
			currentPage = page;

		} finally {
			if (iter != null) {
				iter.close();
			}
		}
	}

	/**
	 * Returns the additional criteria to retrieve records GE/GT the key.
	 * 
	 * @param key The order key.
	 * @param operator The condition operator (FIELD_GE or FIELD_GT).
	 * @return The additional criteria to retrieve records GE/GT the key.
	 */
	private Criteria getCriteria(OrderKey key, Condition.Operator operator) {
		Criteria criteria = new Criteria();
		for (int i = 0; i < key.size(); i++) {
			Field field = getOrder().get(i).getField();
			Value value = key.get(i).getValue();
			criteria.add(new Condition(field, operator, value));
		}
		return criteria;
	}

	/**
	 * Find the page that contains the record index.
	 * 
	 * @param recordIndex The record index.
	 * @return The page.
	 */
	private Page findPage(int recordIndex) {
		int pageIndex = 0;
		Page page = null;
		while (true) {
			if (pageIndex == getPagesSize()) {
				if (isPaginating()) {
					sleep(50);
					continue;
				}
				break;
			}
			page = getPage(pageIndex);
			if (inPage(recordIndex, page)) {
				break;
			}
			pageIndex++;
		}
		return page;
	}

	/**
	 * Sleep.
	 * 
	 * @param time Time in millis
	 */
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ignore) {
		}
	}

	/**
	 * Check if pagination should be started.
	 */
	private void checkPaginate() {
		if (!isPaginating() && isPagesEmpty()) {
			new Thread(new Paginator()).start();
		}
	}

	/**
	 * Paginate the underlying persistor.
	 * 
	 * @throws PersistorException If a persistor error occurs.
	 */
	@SuppressWarnings("null")
	private void paginate() throws PersistorException {
		RecordIterator iter = null;
		try {

			Order order = getOrder();

			int index = -1;
			Page currentPage = null;
			int currentPageSize = 0;
			Record record = null;

			iter = persistor.iterator(criteria, order);
			while (iter.hasNext()) {
				index++;
				record = iter.next();
				if (currentPageSize == 0) {
					int firstIndex = index;
					OrderKey firstKey = record.getOrderKey(order);
					currentPage = new Page();
					currentPage.setFirstIndex(firstIndex);
					currentPage.setFirstKey(firstKey);
				}
				if (currentPageSize == pageSize - 1) {
					int lastIndex = index;
					OrderKey lastKey = record.getOrderKey(order);
					currentPage.setLastIndex(lastIndex);
					currentPage.setLastKey(lastKey);
					addPage(currentPage);
					currentPage = null;
					currentPageSize = 0;
					continue;
				}
				currentPageSize++;
			}
			if (record != null && currentPage != null && !currentPage.equals(getLastPage())) {
				int lastIndex = index;
				OrderKey lastKey = record.getOrderKey(order);
				currentPage.setLastIndex(lastIndex);
				currentPage.setLastKey(lastKey);
				addPage(currentPage);
			}
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
	}

	/**
	 * Synchronized access to the paginating flag.
	 * 
	 * @return A boolean.
	 */
	synchronized private boolean isPaginating() {
		return paginating;
	}

	/**
	 * Synchronized access to the paginating flag.
	 * 
	 * @param b A boolean.
	 */
	synchronized private void setPaginating(boolean b) {
		this.paginating = b;
	}

	/**
	 * Synchronized access to pages.
	 * 
	 * @param page Add a page.
	 */
	synchronized private void addPage(Page page) {
		pages.add(page);
	}

	/**
	 * Synchronized access to pages.
	 * 
	 * @return The last page in the list.
	 */
	synchronized private Page getLastPage() {
		return ListUtils.getLast(pages);
	}

	/**
	 * Synchronized access to pages.
	 * 
	 * @param index The index of the page.
	 * @return The page of index.
	 */
	synchronized private Page getPage(int index) {
		return pages.get(index);
	}

	/**
	 * Synchronized access to pages.
	 * 
	 * @return A boolean.
	 */
	synchronized private boolean isPagesEmpty() {
		return pages.isEmpty();
	}

	/**
	 * Synchronized access to pages.
	 * 
	 * @return The size of the pages.
	 */
	synchronized private int getPagesSize() {
		return pages.size();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Order order) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Comparator<Record> comparator) {
		throw new UnsupportedOperationException();
	}

}
