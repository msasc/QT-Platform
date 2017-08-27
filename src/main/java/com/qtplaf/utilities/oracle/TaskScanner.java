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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Scan the dump file quering the implementor to process the bytes buffer.
 *
 * @author Miquel Sas
 */
public class TaskScanner extends TaskRunner {

	/**
	 * Check if the buffer bytes is equal to the compare bytes. To be comparable, the buffer bytes must start with a
	 * zero byte and be 2 bytes larger that the compare.
	 * 
	 * @param bytes The source chunk of bytes.
	 * @param compare The chunk of bytes to compare with.
	 * @return A boolean.
	 */
	private static boolean equals(int[] bytes, int[] compare) {
		int length = compare.length;
		if (compare.length != bytes.length - 2) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (compare[i] != bytes[i + 2]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the bytes in the buffer to process and leaves the last two bytes.
	 * 
	 * @param buffer The buffer.
	 * @param last Last call, get all buffer.
	 * @return The array of bytes.
	 */
	private static int[] getBytes(List<Integer> buffer, boolean last) {
		int size = buffer.size() - (last ? 0 : 2);
		int[] bytes = new int[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = buffer.get(i);
		}
		if (!last) {
			int b0 = buffer.get(buffer.size() - 1);
			int b1 = buffer.get(buffer.size() - 2);
			buffer.clear();
			buffer.add(b1);
			buffer.add(b0);
		}
		return bytes;
	}

	/** List of search-replace pairs. */
	private List<Data> datas;
	/** Input stream. */
	private BufferedInputStream in;
	/** Output stream. */
	private BufferedOutputStream out;
	/** Bytes to process. */
	private long fileSrcLength;
	/** Pair maximum length. */
	private int maximumLength;
	/** Pair minimum length. */
	private int minimumLength;
	/** First bytes of search and replace. */
	private int[] firstBytes;
	/** Second bytes of search and replace. */
	private int[] secondBytes;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param fileSrc Dump source file.
	 * @param fileDst Dump destination file.
	 * @param datas List of search-replace pairs.
	 * @throws FileNotFoundException If src or dst files are not found.
	 */
	public TaskScanner(Session session, File fileSrc, File fileDst, List<Data> datas) throws FileNotFoundException {
		super(session);
		this.in = new BufferedInputStream(new FileInputStream(fileSrc));
		this.out = new BufferedOutputStream(new FileOutputStream(fileDst));
		this.datas = datas;
		this.fileSrcLength = fileSrc.length();

		maximumLength = Integer.MIN_VALUE;
		minimumLength = Integer.MAX_VALUE;

		List<Integer> firstBytesList = new ArrayList<>();
		List<Integer> secondBytesList = new ArrayList<>();

		for (int i = 0; i < datas.size(); i++) {
			Data data = datas.get(i);
			if (data.getSearchBytes().length < minimumLength) {
				minimumLength = data.getSearchBytes().length;
			}
			if (data.getReplaceBytes().length < minimumLength) {
				minimumLength = data.getReplaceBytes().length;
			}
			if (data.getSearchBytes().length > maximumLength) {
				maximumLength = data.getSearchBytes().length;
			}
			if (data.getReplaceBytes().length > maximumLength) {
				maximumLength = data.getReplaceBytes().length;
			}
			
			if (!firstBytesList.contains(data.getSearchBytes()[0])) {
				firstBytesList.add(data.getSearchBytes()[0]);
			}
			if (!firstBytesList.contains(data.getReplaceBytes()[0])) {
				firstBytesList.add(data.getReplaceBytes()[0]);
			}
			if (!secondBytesList.contains(data.getSearchBytes()[1])) {
				secondBytesList.add(data.getSearchBytes()[1]);
			}
			if (!secondBytesList.contains(data.getReplaceBytes()[1])) {
				secondBytesList.add(data.getReplaceBytes()[1]);
			}
			
		}

		firstBytes = ListUtils.toIntegerArray(firstBytesList);
		secondBytes = ListUtils.toIntegerArray(secondBytesList);

		setName("Replace");
		setDescription("Perform replacement of strings.");
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {
		notifyStepCount(fileSrcLength);
		return getSteps();
	}

	/**
	 * Check the length of the bytes buffer to start checking search-replace.
	 * 
	 * @param bytes The bytes buffer.
	 * @return A boolean that indicates if we can check replace.
	 */
	private boolean checkProcess(int[] bytes) {
		if (bytes.length - 2 < minimumLength) {
			return false;
		}
		if (bytes.length - 2 > maximumLength) {
			return false;
		}
		if (checkFirstByte(bytes)) {
			if (checkSecondByte(bytes)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check that the first byte is one of the list.
	 * 
	 * @param bytes The bytes buffer.
	 * @return A boolean.
	 */
	private boolean checkFirstByte(int[] bytes) {
		int b = bytes[2];
		for (int i = 0; i < firstBytes.length; i++) {
			if (firstBytes[i] == b) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check that the second byte is one of the list.
	 * 
	 * @param bytes The bytes buffer.
	 * @return A boolean.
	 */
	private boolean checkSecondByte(int[] bytes) {
		int b = bytes[3];
		for (int i = 0; i < secondBytes.length; i++) {
			if (secondBytes[i] == b) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process the bytes, check dups and eventually replace.
	 * 
	 * @param bytes The array of bytes.
	 * @return The result chunk of bytes.
	 */
	private int[] processBytes(int[] bytes) {
		if (bytes.length == 0) {
			return bytes;
		}
		if (!checkProcess(bytes)) {
			return bytes;
		}
		int size = datas.size();
		for (int p = 0; p < size; p++) {
			Data data = datas.get(p);
			int[] search = data.getSearchBytes();
			int[] replace = data.getReplaceBytes();
			// Check replace not exists.
//			if (equals(bytes, replace)) {
//				throw new Exception("The replace string '" + data.getReplaceString() + "' already exists.");
//			}
			// Check search exists.
			if (equals(bytes, search)) {
				int length = replace.length;
				bytes = new int[length + 2];
				bytes[0] = length;
				bytes[1] = 0;
				for (int i = 0; i < length; i++) {
					bytes[i + 2] = replace[i];
				}
				break;
			}
		}
		return bytes;
	}

	/**
	 * Write the bytes.
	 * 
	 * @param bytes The bytes.
	 * @throws IOException If any IO error occurs.
	 */
	private void write(int[] bytes) throws IOException {
		int length = bytes.length;
		for (int i = 0; i < length; i++) {
			out.write(bytes[i]);
		}
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

			// Buffer.
			List<Integer> buffer = new ArrayList<>();

			// Step and steps.
			long step = 0;
			long steps = getSteps();
			while (step < steps) {

				boolean notify = ((step % 500000 == 0) || step == steps - 1);
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

				// Read byte.
				int b = in.read();
				buffer.add(b);

				// End of file reached, exit.
				if (b < 0) {
					break;
				}

				// Byte zero, process the buffer.
				if (b == 0 && step > 2) {
					write(processBytes(getBytes(buffer, false)));
				}
			}

			// Process the last buffer.
			write(processBytes(getBytes(buffer, true)));

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
