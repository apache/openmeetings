package org.openmeetings.rdc;

import java.awt.*;
import java.awt.event.*;

class WindowClosingAdapter extends WindowAdapter {
	public void windowClosing(WindowEvent event) {
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		System.exit(0);
	}
}

public class TestKeyCodes extends Frame implements KeyListener {
	String msg1 = "";
	String msg2 = "";

	public static void main(String[] args) {
		TestKeyCodes wnd = new TestKeyCodes();
	}

	public TestKeyCodes() {
		super("TestKeyCodes");
		addKeyListener(this);
		addWindowListener(new WindowClosingAdapter());
		setBackground(Color.lightGray);
		setSize(300, 200);
		setLocation(200, 100);
		setVisible(true);
	}

	public void paint(Graphics g) {
		if (msg1.length() > 0) {
			draw3DRect(g, 20, 50, 250, 30);
			g.setColor(Color.black);
			g.drawString(msg1, 30, 70);
		}
		if (msg2.length() > 0) {
			draw3DRect(g, 20, 100, 250, 30);
			g.setColor(Color.black);
			g.drawString(msg2, 30, 120);
		}
	}

	void draw3DRect(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.darkGray);
		g.drawLine(x, y, x, y + height);
		g.drawLine(x, y, x + width, y);
		g.setColor(Color.white);
		g.drawLine(x + width, y + height, x, y + height);
		g.drawLine(x + width, y + height, x + width, y);
	}

	public void keyPressed(KeyEvent event) {
		msg1 = "";
		System.out.println("keyPressed "+event.getKeyCode());
		if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
			int key = event.getKeyCode();
			// Funktionstaste abfragen
			if (key == KeyEvent.VK_F1) {
				msg1 = "F1";
			} else if (key == KeyEvent.VK_F2) {
				msg1 = "F2";
			} else if (key == KeyEvent.VK_F3) {
				msg1 = "F3";
			}
			// Modifier abfragen
			if (msg1.length() > 0) {
				if (event.isAltDown()) {
					msg1 = "ALT + " + msg1;
				}
				if (event.isControlDown()) {
					msg1 = "STRG + " + msg1;
				}
				if (event.isShiftDown()) {
					msg1 = "UMSCHALT + " + msg1;
				}
			}
		}
		repaint();
	}

	public void keyReleased(KeyEvent event) {
		msg1 = "";
		repaint();
	}

	public void keyTyped(KeyEvent event) {
		char key = event.getKeyChar();
		if (key == KeyEvent.VK_BACK_SPACE) {
			if (msg2.length() > 0) {
				msg2 = msg2.substring(0, msg2.length() - 1);
			}
		} else if (key >= KeyEvent.VK_SPACE) {
			if (msg2.length() < 40) {
				msg2 += event.getKeyChar();
			}
		}
		repaint();
	}
}