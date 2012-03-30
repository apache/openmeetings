package org.openmeetings.screen.webstart.gui;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class NumberSpinner extends JSpinner {
	private static final long serialVersionUID = -1964457022937740633L;

	public NumberSpinner(int value, int min, int max, int step) {
		super(new SpinnerNumberModel(value, min, max, step));
	}
	
	public Integer getValue() {
		return (Integer)super.getValue();
	}
}
