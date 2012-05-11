package org.openmeetings.screen.webstart.gui;

import java.awt.event.MouseAdapter;

import javax.swing.JLabel;

public class MouseListenerable extends JLabel {
	private static final long serialVersionUID = -6862930681858866872L;

	public void addListener(MouseAdapter listner) {
		addMouseListener(listner);
		addMouseMotionListener(listner);
	}
}