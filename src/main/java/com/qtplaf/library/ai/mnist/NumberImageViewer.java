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
package com.qtplaf.library.ai.mnist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.util.file.FileUtils;

/**
 * A simple number image viewer.
 * 
 * @author Miquel Sas
 */
public class NumberImageViewer extends JDialog {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		File fileImg = FileUtils.getFileFromClassPathEntries("train-images.idx3-ubyte");
		File fileLbl = FileUtils.getFileFromClassPathEntries("train-labels.idx1-ubyte");
		NumberImageReader reader = new NumberImageReader(fileLbl, fileImg);
		List<NumberImage> images =  reader.read();
		new NumberImageViewer(images);
	}

	private int rows = NumberImage.ROWS;
	private int columns = NumberImage.COLUMNS;

	private JPanel[][] pixelPanels;

	private List<NumberImage> numberImages;
	private int currentImage = 0;
	private JLabel labelNumber;

	/**
	 * Default constructor
	 * 
	 * @param numberImages The list of number images.
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless()
	 */
	public NumberImageViewer(List<NumberImage> numberImages) throws HeadlessException {
		super();
		this.numberImages = numberImages;
		setupFrame();
	}

	/**
	 * Setup the frame by adding its components.
	 */
	private void setupFrame() {
		
		setTitle("Number image viewer");

		addKeyListener(new KeyHandler());
		addMouseWheelListener(new MouseWheelHandler());

		JPanel panelPixels = new JPanel();
		panelPixels.setLayout(new GridLayout(rows, columns));

		pixelPanels = new JPanel[rows][columns];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				JPanel pixelPanel = new JPanel();
				pixelPanel.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1.0f, 1.0f, 1.0f, false));
				pixelPanels[r][c] = pixelPanel;
				panelPixels.add(pixelPanel);
			}
		}

		GridBagConstraints constraintsPanel = new GridBagConstraints();
		constraintsPanel.anchor = GridBagConstraints.NORTH;
		constraintsPanel.fill = GridBagConstraints.BOTH;
		constraintsPanel.gridheight = 1;
		constraintsPanel.gridwidth = 1;
		constraintsPanel.weightx = 1;
		constraintsPanel.weighty = 1;
		constraintsPanel.gridx = 0;
		constraintsPanel.gridy = 0;
		constraintsPanel.insets = new Insets(1, 1, 1, 1);

		labelNumber = new JLabel();
		labelNumber.setFont(new Font("Dialog", Font.BOLD, 20));
		labelNumber.setText("Image info...");
		labelNumber.setPreferredSize(new Dimension(0, 20));
		labelNumber.setMinimumSize(new Dimension(0, 20));
		labelNumber.setHorizontalAlignment(JLabel.CENTER);

		GridBagConstraints constraintsLabel = new GridBagConstraints();
		constraintsLabel.anchor = GridBagConstraints.NORTH;
		constraintsLabel.fill = GridBagConstraints.HORIZONTAL;
		constraintsLabel.gridheight = 1;
		constraintsLabel.gridwidth = 1;
		constraintsLabel.weightx = 1;
		constraintsLabel.weighty = 0;
		constraintsLabel.gridx = 0;
		constraintsLabel.gridy = 1;
		constraintsLabel.insets = new Insets(1, 1, 5, 1);

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(panelPixels, constraintsPanel);
		getContentPane().add(labelNumber, constraintsLabel);

		showImage();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		SwingUtils.setSizeAndCenterOnSreen(this, 0.2, 0.38);
		setModal(true);
		setVisible(true);
	}

	private void showImage() {
		NumberImage image = numberImages.get(currentImage);
		int number = image.getNumber();
		labelNumber.setText(Integer.toString(number) + " (" + (currentImage + 1) + "/" + numberImages.size() + ")");

		int[][] bytes = image.getImage();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				int b = bytes[r][c];
				pixelPanels[r][c].setBackground(new Color(b, b, b));
			}
		}
	}

	class MouseWheelHandler implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int move = e.getWheelRotation();
			currentImage += move;
			if (currentImage >= numberImages.size()) {
				currentImage = numberImages.size() - 1;
			}
			if (currentImage < 0) {
				currentImage = 0;
			}
			showImage();
		}

	}

	class KeyHandler implements KeyListener {
		private int pageSize = 100;

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (currentImage < numberImages.size() - 1) {
					currentImage++;
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (currentImage > 0) {
					currentImage--;
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (currentImage > 0) {
					currentImage--;
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (currentImage < numberImages.size() - 1) {
					currentImage++;
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
				if (currentImage > 0) {
					currentImage -= pageSize;
					if (currentImage < 0) {
						currentImage = 0;
					}
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
				if (currentImage < numberImages.size() - 1) {
					currentImage += pageSize;
					if (currentImage >= numberImages.size()) {
						currentImage = numberImages.size() - 1;
					}
					showImage();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_HOME) {
				currentImage = 0;
				showImage();
			}
			if (e.getKeyCode() == KeyEvent.VK_END) {
				currentImage = numberImages.size() - 1;
				showImage();
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				NumberImageViewer.this.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
