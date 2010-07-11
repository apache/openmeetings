package org.openmeetings.rdc;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import junit.framework.TestCase;

public class TestKeyCodesNumber extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestKeyCodesNumber.class);
	
	public void testTestKeyCodesNumber() {
		try {
			
			this.testKeyCodes();
			
		} catch (Exception er) {
			log.debug("ERROR ", er);
		}
	}
	
	 public void testKeyCodes() {
	    	try {
	    		
	    		for (int i=1;i<600;i++) {
	    			
	    			String charText = KeyEvent.getKeyText(i);
	    			
	    			log.debug("ERROR "+ i + " "+charText);
	    			
	    		}
	    		
	    	} catch (Exception err) {
	    		log.error( "[testKeyCodes]", err );
	    	}
	    	
	    }
	
}