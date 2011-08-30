package org.openmeetings.test.timezone;

import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

public class TestIds extends TestCase {
	
	@Test
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
