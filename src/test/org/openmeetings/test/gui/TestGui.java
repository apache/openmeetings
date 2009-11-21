package org.openmeetings.test.gui;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.webstart.gui.StartScreen;


public class TestGui  extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestGui.class);
	
	public TestGui(String testname){
		//super(testname);
	}
	
	public static void main(String[] args){
		new StartScreen("","","","","","");
	}
}
