package org.openmeetings.test.timezone;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import junit.framework.TestCase;

public class TestGetOffset extends TestCase {
	
	@Test
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
