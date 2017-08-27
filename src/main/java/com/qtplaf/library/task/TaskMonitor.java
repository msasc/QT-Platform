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

package com.qtplaf.library.task;

import java.util.List;

/**
 * An abstract monitor of task progress.
 * 
 * @author Miquel Sas
 */
public interface TaskMonitor {
	/**
	 * Add a task and monitor its progress.
	 * 
	 * @param task The task to monitor.
	 */
	void add(Task task);

	/**
	 * Remove the task and stop monitoring it.
	 * 
	 * @param task The task to remove.
	 */
	void remove(Task task);

	/**
	 * Returns the list of current tasks.
	 * 
	 * @return The list of tasks.
	 */
	List<Task> getTasks();
}
