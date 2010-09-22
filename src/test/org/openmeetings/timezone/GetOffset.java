package org.openmeetings.timezone;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class GetOffset extends TestCase {
	
	public GetOffset(String testname){
		super(testname);
	}
	
	public void testGetIds() {
		try { 
			
			TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT+1");
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(timeZone);
			int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
			
			System.out.println("CalendarServlet offset "+offset );
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
