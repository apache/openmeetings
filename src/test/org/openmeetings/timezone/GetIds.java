package org.openmeetings.timezone;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class GetIds extends TestCase {
	
	public GetIds(String testname){
		super(testname);
	}
	
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
