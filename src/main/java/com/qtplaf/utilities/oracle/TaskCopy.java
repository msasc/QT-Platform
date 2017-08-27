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

package com.qtplaf.utilities.oracle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.TaskRunner;

/**
 * Copy the dump file.
 *
 * @author Miquel Sas
 */
public class TaskCopy extends TaskRunner {

	/** Input stream. */
	private BufferedInputStream in;
	/** Output stream. */
	private BufferedOutputStream out;
	/** File length. */
	private long fileLength;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param fileSrc Dump source file.
	 * @param fileDst Dump destination file.
	 * @throws FileNotFoundException If src or dst files are not found.
	 */
	public TaskCopy(Session session, File fileSrc, File fileDst) throws FileNotFoundException {
		super(session);
		this.in = new BufferedInputStream(new FileInputStream(fileSrc));
		this.out = new BufferedOutputStream(new FileOutputStream(fileDst), 8192 * 16);
		this.fileLength = fileSrc.length();

		setName("Copy");
		setDescription("Copy the file.");
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {
		notifyStepCount(fileLength);
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		try {

			// Count steps.
			countSteps();

			// Step and steps.
			long step = 0;
			long steps = getSteps();
			while (step < steps) {

				boolean notify = ((step % 1000000 == 0) || step == steps - 1);
				if (notify) {
					// Check request of cancel.
					if (checkCancel()) {
						break;
					}
					// Check pause resume.
					if (checkPause()) {
						continue;
					}
					notifyStepStart(step, getStepMessage(step, steps, null, null));
				}

				// Increase step and notify.
				step++;

				// Read/write byte.
				int b = in.read();
				out.write(b);

				// End of file reached, exit.
				if (b < 0) {
					break;
				}

			}

		} finally {
			// Close
			in.close();
			out.close();
		}
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}

}
