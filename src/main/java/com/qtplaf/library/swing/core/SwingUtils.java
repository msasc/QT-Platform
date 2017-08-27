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
package com.qtplaf.library.swing.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuKeyListener;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditField;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Swing utilities. Since <code>javax.swing.SwingUtilities</code> has a private constructor and can not be extended,
 * what would be the natural way of adding functionality, the used methods of the swing class will be gradually
 * delegated, to avoid having the utility functionality distributed over several classes.
 * <p>
 * Simple methods like <i>invokeLater</i> will be simply copied, others more complicated will be delegated.
 * 
 * @author Miquel Sas
 */
public class SwingUtils {

	/**
	 * Add menu items to the popup menu.
	 * 
	 * @param popupMenu The popup menu.
	 * @param actions The actions.
	 */
	public static void addMenuItems(JPopupMenu popupMenu, List<Action> actions) {
		for (int i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			ActionGroup groupCurr = ActionUtils.getActionGroup(action);
			ActionGroup groupPrev = getLastActionGroup(popupMenu);
			if (groupCurr != null && groupPrev != null && !groupCurr.equals(groupPrev)) {
				popupMenu.addSeparator();
			}
			JMenuItem menuItem = null;
			if (ActionUtils.getButton(action) != null) {
				menuItem = SwingUtils.getMenuItem(ActionUtils.getButton(action));
			} else {
				menuItem = SwingUtils.getMenuItem(action);
			}
			popupMenu.add(menuItem);
		}
	}

	/**
	 * Returns the last action group of a <tt>JComponent</tt> that is either a <tt>JPopupMenu</tt> or a
	 * <tt>JMenuItem</tt>.
	 * 
	 * @param component The parent component.
	 * @return The las action group or null.
	 */
	private static ActionGroup getLastActionGroup(JComponent component) {
		if (!(component instanceof JPopupMenu) && !(component instanceof JMenuItem)) {
			throw new IllegalArgumentException();
		}
		if (isEmpty(component)) {
			return null;
		}
		Component last = component.getComponent(component.getComponentCount() - 1);
		if (!(last instanceof JMenuItem)) {
			return null;
		}
		JMenuItem menuItem = (JMenuItem) last;
		ActionListener[] actionListeners = menuItem.getActionListeners();
		for (ActionListener actionListener : actionListeners) {
			if (actionListener instanceof Action) {
				Action action = (Action) actionListener;
				ActionGroup actionGroup = ActionUtils.getActionGroup(action);
				if (actionGroup != null) {
					return actionGroup;
				}
			}
		}
		return null;
	}

	/**
	 * Check if the container is empty.
	 * 
	 * @param container The container.
	 * @return A boolean.
	 */
	public static boolean isEmpty(Container container) {
		return container.getComponentCount() == 0;
	}

	/**
	 * Returns the menu item.
	 * 
	 * @param action The source action.
	 * @return The menu iem.
	 */
	public static JMenuItem getMenuItem(Action action) {
		JMenuItem menuItem = new JMenuItem();
		menuItem.addActionListener(action);
		if (ActionUtils.getSmallIcon(action) != null) {
			menuItem.setIcon(ActionUtils.getSmallIcon(action));
		}
		menuItem.setText(ActionUtils.getName(action));
		menuItem.setToolTipText(ActionUtils.getShortDescription(action));
		menuItem.setEnabled(action.isEnabled());
		return menuItem;
	}

	/**
	 * Returns the menu item.
	 * 
	 * @param button The source button.
	 * @return The menu iem.
	 */
	public static JMenuItem getMenuItem(JButton button) {
		JMenuItem menuItem = new JMenuItem();
		Action action = button.getAction();
		menuItem.addActionListener(action);
		menuItem.setIcon(button.getIcon());
		menuItem.setText(button.getText());
		menuItem.setToolTipText(button.getToolTipText());
		menuItem.setVisible(button.isVisible());
		menuItem.setEnabled(button.isEnabled());
		return menuItem;
	}

	/**
	 * Returns the list of edit fields in the top component.
	 * 
	 * @param topComponent The top component.
	 * @return The list of edit fields.
	 */
	public static List<EditField> getEditFields(Component topComponent) {
		List<EditField> editFields = new ArrayList<>();
		List<Component> components = SwingUtils.getAllComponents(topComponent);
		for (Component component : components) {
			if (component instanceof EditField) {
				EditField editField = (EditField) component;
				// Skip edit fields contained in another edit field (JMaskedFieldButton <- JMaskedFied),
				// the internal edit field does not have a name.
				if (editField.getName() != null) {
					editFields.add(editField);
				}
			}
		}
		return editFields;
	}

	/**
	 * Returns the string representation of an edit field.
	 * 
	 * @param editField The edit field.
	 * @return The string representation.
	 */
	public static String toString(EditField editField) {
		StringBuilder b = new StringBuilder();
		if (editField.getEditContext() != null) {
			b.append("[");
			b.append(editField.getEditContext().getField());
			b.append("]");
			b.append("[");
			b.append(editField.getEditContext().getValue());
			b.append("]");
		}
		b.append("[");
		b.append(editField.getClass());
		b.append("]");
		return b.toString();
	}

	/**
	 * Remove key listeners form the argument component.
	 * 
	 * @param cmp The component.
	 */
	public static void removeKeyListeners(Component cmp) {
		KeyListener[] listeners = cmp.getKeyListeners();
		if (listeners != null) {
			for (KeyListener listener : listeners) {
				cmp.removeKeyListener(listener);
			}
		}
	}

	/**
	 * Remove mouse listeners form the argument component.
	 * 
	 * @param cmp The component.
	 */
	public static void removeMouseListeners(Component cmp) {
		MouseListener[] listeners = cmp.getMouseListeners();
		if (listeners != null) {
			for (MouseListener listener : listeners) {
				cmp.removeMouseListener(listener);
			}
		}
	}

	/**
	 * Returns the root pane or null.
	 * 
	 * @param cmp The source component.
	 * @return The root pane.
	 */
	public static JRootPane getRootPane(Component cmp) {
		return SwingUtilities.getRootPane(cmp);
	}

	/**
	 * Returns the key stroke representation translated.
	 * 
	 * @param keyStroke The key stroke.
	 * @param locale The locale.
	 * @return The key stroke representation translated.
	 */
	public static String translate(KeyStroke keyStroke, Locale locale) {
		String[] tokens = StringUtils.parse(StringUtils.toString(keyStroke), " ");
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			String key = "key_" + token;
			String translated = TextServer.getString(key, locale);
			if (translated.equals(TextServer.notFoundKey(key))) {
				translated = token;
			}
			if (i > 0) {
				b.append(" ");
			}
			b.append(translated);
		}
		return b.toString();
	}

	/**
	 * Sets the appropriate preferred and minimum sizes to the label.
	 * 
	 * @param label The label.
	 */
	public static void setLabelPreferredAndMinimumSize(JLabel label) {
		label.setPreferredSize(getLabelPreferredSize(label));
		label.setMinimumSize(getLabelPreferredSize(label));
	}

	/**
	 * Sets the appropriate preferred and minimum sizes to the label.
	 * 
	 * @param label The label.
	 * @param sampleText Sample text.
	 */
	public static void setLabelPreferredAndMinimumSize(JLabel label, String sampleText) {
		Dimension size = getLabelPreferredSize(label, sampleText);
		label.setPreferredSize(size);
		label.setMinimumSize(size);
	}

	/**
	 * Returns the preferred size for a label.
	 * 
	 * @param label The label.
	 * @return The preferred size.
	 */
	public static Dimension getLabelPreferredSize(JLabel label) {
		return getLabelPreferredSize(label, "Sample text");
	}

	/**
	 * Returns the preferred size for a label.
	 * 
	 * @param label The label.
	 * @param sampleText The sample text used to get the size.
	 * @return The preferred size.
	 */
	public static Dimension getLabelPreferredSize(JLabel label, String sampleText) {
		String text = label.getText();
		label.setText(sampleText);
		Dimension size = label.getPreferredSize();
		label.setText(text);
		return size;
	}

	/**
	 * Returns a suitabel screen font metrics for the font.
	 * 
	 * @param font The font.
	 * @return The font megtrics.
	 */
	public static FontMetrics getFontMetrics(Font font) {
		return new JLabel().getFontMetrics(font);
	}

	/**
	 * Execute the action by clicking the first button with the given action class.
	 * 
	 * @param parent The parent component.
	 * @param actionClass The action name.
	 */
	public static void executeButtonAction(Component parent, Class<? extends Action> actionClass) {
		List<Component> components = SwingUtils.getAllComponents(parent, JButton.class);
		for (Component component : components) {
			JButton button = (JButton) component;
			Action action = button.getAction();
			if (action != null) {
				if (action.getClass() == actionClass) {
					button.doClick();
				}
			}
		}
	}

	/**
	 * Returns the preferred form field width.
	 * 
	 * @param metrics The font metrics to calculate the width.
	 * @param field The field.
	 * @param locale Working locale.
	 * @return The preferred width.
	 */
	public static int getPreferredFieldWidth(FontMetrics metrics, Field field, Locale locale) {
		String str = null;
		if (field.isDate()) {
			return (int) (metrics.stringWidth("99/99/9999") * 1.3);
		}
		if (field.isTime()) {
			return (int) (metrics.stringWidth("99:99:99") * 1.3);
		}
		if (field.isTimestamp()) {
			return (int) (metrics.stringWidth("99/99/9999 99:99:99") * 1.15);
		}
		if (field.isPossibleValues()) {
			List<Value> values = field.getPossibleValues();
			int width = field.getMinimumWidth();
			if (width < 0)
				width = field.getPreferredWidth();
			if (width < 0)
				width = 0;
			for (Value value : values) {
				String string = value.toStringFormatted(locale);
				if (value.getLabel() != null)
					string = value.getLabel();
				width = Math.max(width, metrics.stringWidth(string));
			}
			width = (int) ((1.1 * width) + 20);
			return width;
		}
		if (field.isUppercase()) {
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789";
		} else {
			str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789";
		}
		double length = field.getDisplayLength() + 1;
		double avgWidth = (double) metrics.stringWidth(str) / (double) str.length();
		double factor = 1.2;
		if (field.getWidthFactor() > 0) {
			factor *= field.getWidthFactor();
		}
		double width = avgWidth * factor * (length + 1);
		return (int) NumberUtils.round(width, 0);
	}

	/**
	 * Causes <i>runnable.run()</i> to be executed asynchronously on the AWT event dispatching thread. This will happen
	 * after all pending AWT events have been processed.
	 * 
	 * @param runnable The runnable object.
	 */
	public static void invokeLater(Runnable runnable) {
		EventQueue.invokeLater(runnable);
	}

	/**
	 * Sets the size of the window to a width and height factor of the screen size, and centers it on the screen.
	 * 
	 * @param window The window to set the size.
	 * @param widthFactor The width factor of the screen size.
	 * @param heightFactor The height factor of the screen size.
	 */
	public static void setSizeAndCenterOnSreen(Window window, double widthFactor, double heightFactor) {
		setSize(window, widthFactor, heightFactor);
		centerOnScreen(window);
	}

	/**
	 * Sets the size of the window to a width and height factor of the screen size.
	 * 
	 * @param window The window to set the size.
	 * @param widthFactor The width factor of the screen size.
	 * @param heightFactor The height factor of the screen size.
	 */
	public static void setSize(Window window, double widthFactor, double heightFactor) {
		Dimension screenSize = getScreenSize(window);
		int width = (int) (screenSize.getWidth() * widthFactor);
		int height = (int) (screenSize.getHeight() * heightFactor);
		window.setSize(width, height);
	}

	/**
	 * Centers the window on the screen.
	 * 
	 * @param window The window to center.
	 * @return The location or top-left corner.
	 */
	public static Point centerOnScreen(Window window) {
		Dimension screenSize = getScreenSize(window);
		Dimension windowSize = window.getSize();
		int x = (int) ((screenSize.getWidth() - windowSize.getWidth()) / 2);
		int y = (int) ((screenSize.getHeight() - windowSize.getHeight()) / 2);
		window.setLocation(x, y);
		return window.getLocation();
	}

	/**
	 * Returns the top component from a component.
	 * 
	 * @return The top component.
	 * @param cmp A component.
	 */
	public static Component getTopComponent(Component cmp) {
		if (cmp == null) {
			return null;
		}
		while (cmp.getParent() != null) {
			cmp = cmp.getParent();
		}
		return cmp;
	}

	/**
	 * Returns a map with all named components included in the argument top component.
	 * 
	 * @param topComponent The top component.
	 * @return The map of named components.
	 */
	public static Map<String, Component> getComponentMap(Component topComponent) {
		Map<String, Component> map = new HashMap<>();
		List<Component> components = getAllComponents(topComponent);
		for (Component component : components) {
			String name = component.getName();
			if (name != null) {
				map.put(name, component);
			}
		}
		return map;
	}

	/**
	 * Return the first parent that is a <code>JFrame</code> or a <code>JDialog</code>.
	 * 
	 * @return The parent frame or dialog.
	 * @param cmp The start search component.
	 */
	public static Component getFirstParentFrameOrDialog(Component cmp) {
		while (cmp != null) {
			if (cmp instanceof JFrame || cmp instanceof JDialog) {
				return cmp;
			}
			cmp = cmp.getParent();
		}
		return null;
	}

	/**
	 * Returns the first Window ancestor of a given component, or {@code null} if the component is not contained inside
	 * a Window.
	 * 
	 * @param cmp <code>Component</code> to get <code>Window</code> ancestor of.
	 * @return the window instance.
	 */
	public static Window getWindowAncestor(Component cmp) {
		while (cmp != null) {
			if (cmp instanceof Window)
				return (Window) cmp;
			cmp = cmp.getParent();
		}
		return null;
	}

	/**
	 * Sets the mnemonics for an array of actions or buttons. Starts with the first character of the text, and if the
	 * mnemonic is used, scans the text for the first character not used.
	 * 
	 * @param buttons The list of buttons to set the mnemonics.
	 */
	public static void setMnemonics(JButton... buttons) {
		setMnemonics(ListUtils.asList(buttons));
	}

	/**
	 * Sets the mnemonics for an array of actions or buttons. Starts with the first character of the text, and if the
	 * mnemonic is used, scans the text for the first character not used.
	 * 
	 * @param buttons The list of buttons to set the mnemonics.
	 */
	public static void setMnemonics(List<JButton> buttons) {
		HashMap<Character, Character> mnemonicMap = new HashMap<>();
		for (JButton button : buttons) {
			Action action = button.getAction();
			if (action != null) {
				String sourceName = ActionUtils.getSourceName(action);
				if (sourceName != null && !sourceName.isEmpty()) {
					String name = ActionUtils.getActionName(action);
					ActionUtils.setName(action, name);
					button.setText(name);
				}
			}

			String sourceName = null;
			if (action != null) {
				sourceName = ActionUtils.getSourceName(action);
				if (sourceName == null) {
					sourceName = ActionUtils.getName(action);
				}
			}
			if (sourceName == null) {
				sourceName = button.getText();
			}
			if (sourceName != null && sourceName.length() > 0) {
				int index = 0;
				char mnemonic = sourceName.charAt(index);
				char uppercase = Character.toUpperCase(mnemonic);
				while (mnemonicMap.containsKey(uppercase)) {
					index++;
					if (index >= 0 && index < sourceName.length() && sourceName.charAt(index) != ' ') {
						mnemonic = sourceName.charAt(index);
						uppercase = Character.toUpperCase(mnemonic);
					} else {
						if (setMnemonicsGet(mnemonic) > 0) {
							break;
						}
					}
				}
				if (!mnemonicMap.containsKey(uppercase)) {
					mnemonicMap.put(uppercase, uppercase);
					button.setMnemonic(mnemonic);
					setMnemonicsToHtml(button, index);
				}
			}
		}
	}

	/**
	 * Converts the text (name in the action) when t is <code>HTML</code> to show the mnemonic.
	 * 
	 * @param button The button.
	 * @param index The index of the mnemonic.
	 */
	private static void setMnemonicsToHtml(JButton button, int index) {
		String text = button.getText();
		if (text == null) {
			return;
		}
		if (!text.startsWith("<html>")) {
			return;
		}
		index += 6;
		String prefix = text.substring(0, index);
		String mnemonic = text.substring(index, index + 1);
		String suffix = text.substring(index + 1);
		StringBuilder b = new StringBuilder();
		b.append(prefix);
		b.append("<u>");
		b.append(mnemonic);
		b.append("</u>");
		b.append(suffix);

		button.setText(b.toString());
	}

	/**
	 * Returns the mnemonic integer.
	 * 
	 * @param c The character (uppercase)
	 * @return The mnemonic.
	 */
	@SuppressWarnings("incomplete-switch")
	private static int setMnemonicsGet(char c) {
		int mnemonic = 0;
		switch (Character.toUpperCase(c)) {
		case '0':
			mnemonic = KeyEvent.VK_0;
			break;
		case '1':
			mnemonic = KeyEvent.VK_1;
			break;
		case '2':
			mnemonic = KeyEvent.VK_2;
			break;
		case '3':
			mnemonic = KeyEvent.VK_3;
			break;
		case '4':
			mnemonic = KeyEvent.VK_4;
			break;
		case '5':
			mnemonic = KeyEvent.VK_5;
			break;
		case '6':
			mnemonic = KeyEvent.VK_6;
			break;
		case '7':
			mnemonic = KeyEvent.VK_7;
			break;
		case '8':
			mnemonic = KeyEvent.VK_8;
			break;
		case '9':
			mnemonic = KeyEvent.VK_9;
			break;
		case 'A':
			mnemonic = KeyEvent.VK_A;
			break;
		case 'B':
			mnemonic = KeyEvent.VK_B;
			break;
		case 'C':
			mnemonic = KeyEvent.VK_C;
			break;
		case 'D':
			mnemonic = KeyEvent.VK_D;
			break;
		case 'E':
			mnemonic = KeyEvent.VK_E;
			break;
		case 'F':
			mnemonic = KeyEvent.VK_F;
			break;
		case 'G':
			mnemonic = KeyEvent.VK_G;
			break;
		case 'H':
			mnemonic = KeyEvent.VK_H;
			break;
		case 'I':
			mnemonic = KeyEvent.VK_I;
			break;
		case 'J':
			mnemonic = KeyEvent.VK_J;
			break;
		case 'K':
			mnemonic = KeyEvent.VK_K;
			break;
		case 'L':
			mnemonic = KeyEvent.VK_L;
			break;
		case 'M':
			mnemonic = KeyEvent.VK_M;
			break;
		case 'N':
			mnemonic = KeyEvent.VK_N;
			break;
		case 'O':
			mnemonic = KeyEvent.VK_O;
			break;
		case 'P':
			mnemonic = KeyEvent.VK_P;
			break;
		case 'Q':
			mnemonic = KeyEvent.VK_Q;
			break;
		case 'R':
			mnemonic = KeyEvent.VK_R;
			break;
		case 'S':
			mnemonic = KeyEvent.VK_S;
			break;
		case 'T':
			mnemonic = KeyEvent.VK_T;
			break;
		case 'U':
			mnemonic = KeyEvent.VK_U;
			break;
		case 'V':
			mnemonic = KeyEvent.VK_V;
			break;
		case 'W':
			mnemonic = KeyEvent.VK_W;
			break;
		case 'X':
			mnemonic = KeyEvent.VK_X;
			break;
		case 'Y':
			mnemonic = KeyEvent.VK_Y;
			break;
		case 'Z':
			mnemonic = KeyEvent.VK_Z;
			break;
		}
		return mnemonic;
	}

	/**
	 * Returns the component with the given name contained in the top component, or null if it does not contain a
	 * component with that name.
	 * 
	 * @param topComponent The top component.
	 * @param name The name of the component to search.
	 * @return The component with the name or null.
	 */
	public static Component getComponent(Component topComponent, String name) {
		List<Component> components = getAllComponents(topComponent);
		for (Component component : components) {
			if (component.getName() != null && component.getName().equals(name)) {
				return component;
			}
		}
		return null;
	}

	/**
	 * Returns an array with all JButton components contained in a top component.
	 * 
	 * @param topComponent The top component to scan.
	 * @return An list with all JButton components.
	 */
	public static List<JButton> getAllButtons(Component topComponent) {
		List<Component> components = getAllComponents(topComponent);
		List<JButton> buttons = new ArrayList<>();
		for (Component component : components) {
			if (component instanceof JButton) {
				buttons.add((JButton) component);
			}
		}
		return buttons;
	}

	/**
	 * Returns the list of all buttons included in the list ob objects.
	 * 
	 * @param objects The list of objects.
	 * @return The list of buttons.
	 */
	public static List<JButton> getAllButtons(List<Object> objects) {
		List<JButton> buttons = new ArrayList<>();
		for (Object object : objects) {
			if (object instanceof JButton) {
				buttons.add((JButton) object);
			}
		}
		return buttons;
	}

	/**
	 * Returns the list of all components contained in a component and its subcomponents.
	 * 
	 * @return The list of components.
	 * @param parent The parent component.
	 */
	public static List<Component> getAllComponents(Component parent) {
		List<Component> list = new ArrayList<>();
		fillComponentList(parent, list);
		return list;
	}

	/**
	 * Returns the array of all components contained in a component and its subcomponents.
	 * 
	 * @param parent The parent component.
	 * @param clazz The class to filter components.
	 * @return The array of components.
	 */
	public static List<Component> getAllComponents(Component parent, Class<?> clazz) {
		return getAllComponents(parent, new Class[] { clazz });
	}

	/**
	 * Returns the list of all components contained in a component and its subcomponents.
	 * 
	 * @param parent The parent component.
	 * @param classes an array of possible classes.
	 * @return The list of components.
	 */
	public static List<Component> getAllComponents(Component parent, Class<?>[] classes) {
		List<Component> list = new ArrayList<>();
		List<Component> components = getAllComponents(parent);
		for (Component component : components) {
			for (int j = 0; j < classes.length; j++) {
				if (classes[j].isInstance(component)) {
					list.add(component);
				}
			}
		}
		return list;
	}

	/**
	 * Fills the array list with the all the components contained in the parent component and its sub-components.
	 * 
	 * @param list An <code>ArrayList</code>.
	 * @param cmp The parent component.
	 */
	public static void fillComponentList(Component cmp, List<Component> list) {
		list.add(cmp);
		if (cmp instanceof Container) {
			Container cnt = (Container) cmp;
			for (int i = 0; i < cnt.getComponentCount(); i++) {
				fillComponentList(cnt.getComponent(i), list);
			}
		}
	}

	/**
	 * Returns the dimension of the component applying the new width.
	 * 
	 * @param cmp The component.
	 * @param width The desired width.
	 * @return The new Dimension.
	 */
	public static Dimension getWidthDimension(JComponent cmp, int width) {
		Dimension size = cmp.getPreferredSize();
		size.width = width;
		return size;
	}

	/**
	 * Returns the dimension of the component for the desired text.
	 * 
	 * @param cmp The component.
	 * @param text The desired text.
	 * @return The new Dimension.
	 */
	public static Dimension getWidthDimension(JComponent cmp, String text) {
		Font font = cmp.getFont();
		if (font != null) {
			FontMetrics fm = cmp.getFontMetrics(font);
			if (fm != null) {
				int width = (int) (fm.stringWidth(text) * 1.1);
				Dimension size = SwingUtils.getWidthDimension(cmp, width);
				return size;
			}
		}
		return null;
	}

	/**
	 * Returns the graphics device that should apply to a window.
	 * 
	 * @param window The window.
	 * @return The graphics device.
	 */
	public static GraphicsDevice getGraphicsDevice(Window window) {
		if (window != null) {
			return window.getGraphicsConfiguration().getDevice();
		}
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}

	/**
	 * Returns the size of the screen containing the argument window or the primary screen if current window is not
	 * selected.
	 * 
	 * @param window The window.
	 * @return The screen size.
	 */
	public static Dimension getScreenSize(Window window) {
		return getGraphicsDevice(window).getConfigurations()[0].getBounds().getSize();
	}

	/**
	 * Returns the position of the screen containing the argument window or the primary screen if current window is not
	 * selected.
	 * 
	 * @param window The window.
	 * @return The location point.
	 */
	public static Point getScreenPosition(Window window) {
		GraphicsDevice graphicsDevice = getGraphicsDevice(window);
		GraphicsConfiguration[] configurations = graphicsDevice.getConfigurations();
		return configurations[0].getBounds().getLocation();
	}

	/**
	 * Gets a dimension applying a width and/or height factor relative to the screen dimension.
	 * 
	 * @param window The window.
	 * @param widthFactor The width factor relative to the screen (0 &lt; factor &lt;= 1).
	 * @param heightFactor The height factor relative to the screen (0 &lt; factor &lt;= 1).
	 * @return The screen dimension.
	 */
	public static Dimension factorScreenDimension(Window window, double widthFactor, double heightFactor) {
		Dimension d = getScreenSize(window);
		d.width *= widthFactor;
		d.height *= heightFactor;
		return d;
	}

	/**
	 * Locates a dimension on the screen being the left space the width factor of difference between the screen size and
	 * the dimension. A value of 0 moves the dimension to the left, while a value of 1 moves the dimension to the right.
	 * The same applies to the height.
	 * 
	 * @param window The window.
	 * @param factorWidth Width factor.
	 * @param factorHeight Height factor.
	 * @return The top-left corner point.
	 */
	public static Point moveWindowOnScreen(Window window, double factorWidth, double factorHeight) {
		Dimension sz = window.getSize();
		Dimension szScreen = getScreenSize(window);
		Point pt = getScreenPosition(window);
		if (szScreen.width > sz.width) {
			pt.x = pt.x + (int) ((szScreen.width - sz.width) * factorWidth);
			pt.y = pt.y + (int) ((szScreen.height - sz.height) * factorHeight);
		}
		return pt;
	}

	/**
	 * A key listener to manage accelerator keys.
	 */
	public static class AcceleratorKeyListener extends KeyAdapter {

		/** The list of key strokes to manage. */
		private Map<KeyStroke, Action> keyStrokesMap;

		/**
		 * Default constructor.
		 */
		public AcceleratorKeyListener() {
			super();
		}

		/**
		 * Returns and if necessary rebuilds the list of key strokes.
		 * 
		 * @param source The source of the key event.
		 * @return The map of key strokes and actions.
		 */
		private Map<KeyStroke, Action> getKeyStrokesMap(Object source) {
			if (keyStrokesMap == null) {
				keyStrokesMap = new HashMap<>();
				if (source instanceof Component) {
					Component cmp = (Component) source;
					List<JButton> buttons = getAllButtons(getFirstParentFrameOrDialog(cmp));
					for (JButton button : buttons) {
						Action action = button.getAction();
						if (action != null) {
							KeyStroke keyStroke = ActionUtils.getAcceleratorKey(action);
							if (keyStroke != null) {
								keyStrokesMap.put(keyStroke, action);
							}
						}
					}
				}
			}
			return keyStrokesMap;
		}

		/**
		 * Invoked when a key has been pressed. See the class description for KeyEvent for a definition of a key pressed
		 * event.
		 * 
		 * @param e The key event source.
		 */
		@Override
		public void keyReleased(KeyEvent e) {

			int keyCode = e.getKeyCode();
			int modifiers = e.getModifiers();
			KeyStroke keyStrokePressed = KeyStroke.getKeyStroke(keyCode, modifiers);

			// Manage special components that do not fire accelerator keys like, for instance, VK_ENTER.
			if (e.getSource() instanceof JTextArea) {
				JTextArea textArea = (JTextArea) e.getSource();
				if (textArea.isEditable()) {
					if (keyCode == KeyEvent.VK_ENTER) {
						return;
					}
				}
			}

			Action action = getKeyStrokesMap(e.getSource()).get(keyStrokePressed);
			if (action != null) {
				JButton button = ActionUtils.getButton(action);
				if (button != null) {
					button.doClick();
				} else {
					ActionEvent actionEvent = new ActionEvent(e.getSource(), 0, null, modifiers);
					action.actionPerformed(actionEvent);
				}
			}

		}
	}

	/**
	 * Installs an instance of the accelerator key listener in the tree of components where the argument component is
	 * included, starting in the first parent <i>JFrame</i> or <i>JDialog</i> parent.
	 * 
	 * @param cmp The starting components in the tree.
	 */
	public static void installAcceleratorKeyListener(Component cmp) {
		Component parent = getFirstParentFrameOrDialog(cmp);
		List<Component> components = getAllComponents(parent);
		AcceleratorKeyListener acceleratorKeyListener = new AcceleratorKeyListener();
		for (Component component : components) {
			component.addKeyListener(acceleratorKeyListener);
		}
	}

	/**
	 * Removes the accelerator key listener in the tree of components where the argument component is included, starting
	 * in the first parent <i>JFrame</i> or <i>JDialog</i> parent.
	 * 
	 * @param cmp The starting components in the tree.
	 */
	public static void removeAcceleratorKeyListener(Component cmp) {
		Component parent = getFirstParentFrameOrDialog(cmp);
		List<Component> components = getAllComponents(parent);
		for (Component component : components) {
			KeyListener[] keyListeners = component.getKeyListeners();
			if (keyListeners != null) {
				for (KeyListener keyListener : keyListeners) {
					if (keyListener instanceof AcceleratorKeyListener) {
						component.removeKeyListener(keyListener);
					}
				}
			}
		}
	}

	/**
	 * Installs the key listener in the tree of components where the argument component is included, starting in the
	 * first parent <i>JFrame</i> or <i>JDialog</i> parent, without removing previous key listeners.
	 * 
	 * @param cmp The starting components in the tree.
	 * @param keyListener The key listener to install.
	 */
	public static void installKeyListener(Component cmp, KeyListener keyListener) {
		installKeyListener(cmp, keyListener, false);
	}

	/**
	 * Installs the key listener in the tree of components where the argument component is included, starting in the
	 * first parent <i>JFrame</i> or <i>JDialog</i> parent.
	 * 
	 * @param cmp The starting components in the tree.
	 * @param keyListener The key listener to install.
	 * @param removePrevious A boolean indicating whether previous key listeners should be removed.
	 */
	public static void installKeyListener(Component cmp, KeyListener keyListener, boolean removePrevious) {
		Component parent = getFirstParentFrameOrDialog(cmp);
		if (parent == null) {
			parent = cmp;
		}
		List<Component> components = getAllComponents(parent);
		for (Component component : components) {
			if (removePrevious) {
				removeKeyListeners(component);
			}
			component.addKeyListener(keyListener);
		}
	}

	/**
	 * Installs the key listener in the tree of components where the argument component is included, starting in the
	 * first parent <i>JFrame</i> or <i>JDialog</i> parent.
	 * 
	 * @param cmp The starting components in the tree.
	 * @param keyListener The key listener to install.
	 */
	public static void installMenuKeyListener(Component cmp, MenuKeyListener keyListener) {
		List<Component> components = getAllComponents(cmp);
		for (Component component : components) {
			if ((component instanceof JPopupMenu)) {
				JPopupMenu popupMenu = (JPopupMenu) component;
				popupMenu.addMenuKeyListener(keyListener);
			}
			if ((component instanceof JMenuItem)) {
				JMenuItem menuItem = (JMenuItem) component;
				menuItem.addMenuKeyListener(keyListener);
			}
		}
	}

	/**
	 * Installs the mouse listener in the tree of components where the argument component is included, starting in the
	 * first parent <i>JFrame</i> or <i>JDialog</i> parent, without removing previous mouse listeners.
	 * 
	 * @param cmp The starting components in the tree.
	 * @param mouseListener The mouse listener to install.
	 */
	public static void installMouseListener(Component cmp, MouseListener mouseListener) {
		installMouseListener(cmp, mouseListener, false);
	}

	/**
	 * Installs the mouse listener in the tree of components where the argument component is included, starting in the
	 * first parent <i>JFrame</i> or <i>JDialog</i> parent.
	 * 
	 * @param cmp The starting components in the tree.
	 * @param mouseListener The mouse listener to install.
	 * @param removePrevious A boolean indicating whether previous key listeners should be removed.
	 */
	public static void installMouseListener(Component cmp, MouseListener mouseListener, boolean removePrevious) {
		Component parent = getFirstParentFrameOrDialog(cmp);
		if (parent == null) {
			parent = cmp;
		}
		List<Component> components = getAllComponents(parent);
		for (Component component : components) {
			if (removePrevious) {
				removeMouseListeners(component);
			}
			component.addMouseListener(mouseListener);
		}
	}

}
