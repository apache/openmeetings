package org.openmeetings.timezone;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class TestIds extends TestCase {
	
	public TestIds(String testname){
		super(testname);
	}
	
	public void testGetIds() {
		try { 
			
			Calendar cal = Calendar.getInstance();
			int offset = cal.get(Calendar.ZONE_OFFSET);
			
			offset = offset/1000/60/60;
			
			System.out.println("offset :: "+offset);
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
