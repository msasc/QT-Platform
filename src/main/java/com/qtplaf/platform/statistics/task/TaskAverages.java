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

package com.qtplaf.platform.statistics.task;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.platform.statistics.Averages;

/**
 * Root class for states statistics tasks.
 *
 * @author Miquel Sas
 */
public abstract class TaskAverages extends TaskRunner {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public TaskAverages(Session session) {
		super(session);
	}
	
	/**
	 * Set the name and the description given the averages statistics.
	 * 
	 * @param averages The averages statistics.
	 * @param taskName The task name.
	 * @param taskDescription The task description.
	 */
	protected void setNameAndDescription(Averages averages, String taskName, String taskDescription) {
		
		StringBuilder name = new StringBuilder();
		name.append(averages.getServer().getId());
		name.append(" - ");
		name.append(averages.getInstrument().getId());
		name.append(" - ");
		name.append(averages.getPeriod().toString());
		name.append(" - ");
		name.append(averages.getId());
		name.append(" - ");
		name.append(taskName);
		setName(name.toString());

		setDescription(taskDescription);
	}
	
	/**
	 * Returns a boolean indicating whether the task will support cancel requests. This task supports cancel.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task supports counting steps.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This task is not indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. This task supports pause.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}
}
