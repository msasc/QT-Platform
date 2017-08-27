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

package com.qtplaf.library.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;

/**
 * Statistics descriptor.
 *
 * @author Miquel Sas
 */
public abstract class Statistics {

	/**
	 * An identifier.
	 */
	private String id;
	/**
	 * A title or short description.
	 */
	private String title;
	/**
	 * A description.
	 */
	private String description;
	/**
	 * List of outputs.
	 */
	private List<Output> outputs = new ArrayList<>();

	/**
	 * Working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public Statistics(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Add an output value definition.
	 * 
	 * @param output The output definition.
	 */
	public void add(Output output) {
		outputs.add(output);
	}

	/**
	 * Clear output values.
	 */
	public void clear() {
		outputs.clear();
	}

	/**
	 * Returns the output at the given index.
	 * 
	 * @param index The output index.
	 * @return The output.
	 */
	public Output get(int index) {
		return outputs.get(index);
	}

	/**
	 * Returns the number of output values.
	 * 
	 * @return The size or number of output values.
	 */
	public int size() {
		return outputs.size();
	}

	/**
	 * Returns the id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 * 
	 * @param id The id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return the title.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the list of tables where statistic results are stored.
	 * 
	 * @return The list of result tables.
	 */
	public abstract List<Table> getTables();

	/**
	 * Returns the list of tasks to calculate the results. Tasks are expected to be executed sequentially.
	 * 
	 * @return The list of tasks.
	 */
	public abstract List<Task> getTasks();

	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public abstract List<Action> getActions();
}
