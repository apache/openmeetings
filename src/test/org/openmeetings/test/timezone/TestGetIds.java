package org.openmeetings.test.timezone;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

public class TestGetIds extends TestCase {
	
	@Test
	public void testGetIds() {
		try { 
			
			String[] ids = TimeZone.getAvailableIDs();
		    for (String id : ids) {
		    	
		    	TimeZone timeZone = TimeZone.getTimeZone(id);
		    	//TimeZone timeZone = timeRegistry.getTimeZone(id);
		    	
		    	Calendar cal = Calendar.getInstance();
				cal.setTimeZone(timeZone);
				// + cal.get(Calendar.DST_OFFSET)
				int offset = cal.get(Calendar.ZONE_OFFSET);
		    	
				int offsetInHours = offset/1000/60;
				
				System.out.println("<name>" + id + "</name><offset>"+offsetInHours+"</offset>");
		    }
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
