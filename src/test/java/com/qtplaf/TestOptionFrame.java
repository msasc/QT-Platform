package com.qtplaf;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.OptionDialog;
import com.qtplaf.library.swing.core.JOptionDialog;

public class TestOptionFrame {

	public static void main(String[] args) {
		OptionDialog dlg = new OptionDialog(Session.UK);
		dlg.addOption("Close");
		dlg.setMessage("<html>Hello</html>");
		dlg.showDialog();
	}

}
