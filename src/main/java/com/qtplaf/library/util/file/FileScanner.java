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

package com.qtplaf.library.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A <code>FileScanner</code> scans the source directory and optionally its sub-directories, notifying each file or
 * directory found. It is implemented as a task to allow it to be executed in a separated thread with all the control
 * options of a task.
 * 
 * @author Miquel Sas
 */
public class FileScanner extends TaskRunner {

	/**
	 * The list of source directories or files to scan.
	 */
	private List<File> sourceFiles = new ArrayList<>();
	/**
	 * A boolean to indicate if sub-directories should be scanned.
	 */
	private boolean scanSubDirectories = true;
	/**
	 * A boolean to indicate if directories should be notified.
	 */
	private boolean notifyDirectories = true;
	/**
	 * A boolean to indicate if files should be notified.
	 */
	private boolean notifyFiles = true;
	/**
	 * The list of scanner listeners.
	 */
	private List<FileScannerListener> listeners = new ArrayList<>();

	/**
	 * The current source directory being scanned.
	 */
	private File currentSourceDirectory;
	/**
	 * The current step.
	 */
	private long step;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public FileScanner(Session session) {
		super(session);
	}

	/**
	 * Add a source directory or file to the list of source directories or files to scan.
	 * 
	 * @param file The source directory or file.
	 */
	public void addSource(File file) {
		sourceFiles.add(file);
	}

	/**
	 * Add a scanner listener.
	 * 
	 * @param listener The listener.
	 */
	public void addListener(FileScannerListener listener) {
		listeners.add(listener);
	}

	/**
	 * Returns a boolean indicating whether sub-directories should be scanned.
	 * 
	 * @return A boolean.
	 */
	public boolean isScanSubDirectories() {
		return scanSubDirectories;
	}

	/**
	 * Sets a boolean indicating whether sub-directories should be scanned.
	 * 
	 * @param scanSubDirectories A boolean.
	 */
	public void setScanSubDirectories(boolean scanSubDirectories) {
		this.scanSubDirectories = scanSubDirectories;
	}

	/**
	 * Returns a boolean indicating whether directories should be notified.
	 * 
	 * @return A boolean.
	 */
	public boolean isNotifyDirectories() {
		return notifyDirectories;
	}

	/**
	 * Sets a boolean indicating whether directories should be notified.
	 * 
	 * @param notifyDirectories A boolean.
	 */
	public void setNotifyDirectories(boolean notifyDirectories) {
		this.notifyDirectories = notifyDirectories;
	}

	/**
	 * Returns a boolean indicating whether files should be notified.
	 * 
	 * @return A boolean.
	 */
	public boolean isNotifyFiles() {
		return notifyFiles;
	}

	/**
	 * Sets a boolean indicating whether files should be notified.
	 * 
	 * @param notifyFiles A boolean.
	 */
	public void setNotifyFiles(boolean notifyFiles) {
		this.notifyFiles = notifyFiles;
	}

	/**
	 * Count the number of steps.
	 * 
	 * @return The number of steps.
	 */
	@Override
	public long countSteps() {
		return 0;
	}

	/**
	 * Executes the copy task.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Check source files.
		for (File file : sourceFiles) {
			if (file.isFile()) {
				if (!file.exists()) {
					throw new IOException("Invalid source file " + file);
				}
			}
			if (file.isDirectory()) {
				if (!file.exists()) {
					throw new IOException("Invalid source directory " + file);
				}
			}
		}

		// The delist with scanning files.
		List<File> files = new ArrayList<>();
		for (File file : sourceFiles) {
			files.add(file);
		}

		// Initialize the step.
		step = 0;

		// Scan while files left.
		while (!files.isEmpty()) {

			// Check cancel.
			if (checkCancel()) {
				break;
			}
			// Check pause.
			if (checkPause()) {
				continue;
			}
			
			// File does not exist (passed a non existing file to scan)
			if (!ListUtils.getLast(files).exists()) {
				ListUtils.removeLast(files);
				continue;
			}

			// If last file is a file, notify if applicable and remove it.
			if (ListUtils.getLast(files).isFile()) {
				if (isNotifyFiles()) {
					notifyFile(ListUtils.getLast(files));
				}
				ListUtils.removeLast(files);
				continue;
			}

			// If last file is a directory, first notify if applicable, remove it and if should scan subdirectories, add
			// the directory files.
			if (ListUtils.getLast(files).isDirectory()) {
				
				// Current source directory must be in the list of source files.
				if (sourceFiles.contains(ListUtils.getLast(files))) {
					currentSourceDirectory = ListUtils.getLast(files);
				}
				
				// Notify directory if applicable.
				if (isNotifyDirectories()) {
					notifyFile(ListUtils.getLast(files));
				}
				
				// Remove.
				File directory = ListUtils.removeLast(files);
				
				// Add children if should scan deeper.
				if (isScanSubDirectories()) {
					File[] children = directory.listFiles();
					if (children != null) {
						for (File child : children) {
							files.add(child);
						}
					}
				}
				
				continue;
			}

		}

		// Notify cancelled.
//		if (cancelRequested()) {
//			notifyCancelled();
//		}

	}

	/**
	 * Notify the file to listeners.
	 * 
	 * @param file The file to notify.
	 * @throws IOException If any error occurs when the listeners process the events.
	 */
	private void notifyFile(File file) throws IOException {
		notifyStepStart(++step, "");
		for (FileScannerListener listener : listeners) {
			listener.file(currentSourceDirectory, file);
		}
		notifyStepEnd();
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
		return false;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps. A
	 * file scanner that only notifies files is by definition indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return true;
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
