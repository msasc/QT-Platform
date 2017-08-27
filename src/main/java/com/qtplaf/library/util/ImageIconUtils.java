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
package com.qtplaf.library.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.qtplaf.library.util.file.FileUtils;

/**
 * Static image icon utilities.
 * 
 * @author Miquel Sas
 */
public class ImageIconUtils {

	/**
	 * The list of jar files that contains images.
	 */
	private static ArrayList<String> iconImagesFiles = new ArrayList<>();
	/**
	 * The map that will contain images loaded from jar files or the file system.
	 */
	private static HashMap<String, ImageIcon> iconImagesMap = new HashMap<>();

	/**
	 * Add a file name to the list of file name that contain icon images. Default is images.jar
	 * 
	 * @param fileName The file name to add.
	 */
	synchronized public static void addIconImageFile(String fileName) {
		iconImagesFiles.add(fileName);
	}

	/**
	 * Returns the image icon scanning a list of jar files that contain images and if not found finally scanning the
	 * file system.
	 * 
	 * @param imageName The image path name.
	 * @return The ImageIcon or null if the image was not found or an IO exception was thrown.
	 */
	synchronized public static ImageIcon getImageIcon(String imageName) {
		try {
			ImageIcon imageIcon = iconImagesMap.get(imageName);
			if (imageIcon != null) {
				return imageIcon;
			}
			if (iconImagesFiles.isEmpty()) {
//				addIconImageFile("images.jar");
			}
			for (String fileName : iconImagesFiles) {
				byte[] bytes = FileUtils.getJarEntry(fileName, imageName);
				if (bytes != null) {
					imageIcon = new ImageIcon(bytes);
					iconImagesMap.put(imageName, imageIcon);
					return imageIcon;
				}
			}
			byte[] bytes = FileUtils.getFileBytes(imageName);
			if (bytes != null) {
				imageIcon = new ImageIcon(bytes);
				iconImagesMap.put(imageName, imageIcon);
				return imageIcon;
			}
			throw new IOException(MessageFormat.format("Image {0} not found", imageName));
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		}
		return null;
	}
}
