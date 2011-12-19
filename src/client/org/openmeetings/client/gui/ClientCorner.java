package org.openmeetings.client.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author sebastianwagner
 *
 */
public class ClientCorner extends JComponent {
	private static final long serialVersionUID = -3407636607902521760L;

	protected void paintComponent(Graphics g) {
        // Fill me with dirty brown/orange.
        g.setColor(new Color(230, 163, 4));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
