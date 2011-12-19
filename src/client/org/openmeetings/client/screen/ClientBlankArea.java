package org.openmeetings.client.screen;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Color;

public class ClientBlankArea extends JLabel {
	private static final long serialVersionUID = -824649802543748756L;
	Dimension minSize = new Dimension(100, 50);

    public ClientBlankArea(Color color) {
        setBackground(color);
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getMinimumSize() {
        return minSize;
    }

    public Dimension getPreferredSize() {
        return minSize;
    }
}
