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

package com.qtplaf.platform.statistics.action;

import java.awt.event.ActionEvent;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Perform calculations.
 *
 * @author Miquel Sas
 */
public class ActionCalculate extends ActionTickerStatistics {
	
	/** The task to perform calculations. */
	private Task task;

	/**
	 * Constructor.
	 * 
	 * @param statistics The source statistics.
	 * @param task The task to perform calculations.
	 */
	public ActionCalculate(TickerStatistics statistics, Task task) {
		super(statistics);
		this.task = task;
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_execute));
	}

	/**
	 * Perform calculations.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Session session = ActionUtils.getSession(this);
		ProgressManager progress = new ProgressManager(session);
		progress.setSize(0.4, 0.4);
		progress.addTask(task);
		progress.setProgressDecimals(2);
		progress.showFrame();
	}

}
