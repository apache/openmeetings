/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.test.timezone;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

public class TestGetIds extends TestCase {
	
	@Test
	public void testGetIds() {
		try { 
			
			Date tDate = new Date();
			
			String[] ids = TimeZone.getAvailableIDs();
		    for (String id : ids) {
		    	
		    	TimeZone timeZone = TimeZone.getTimeZone(id);
		    	//TimeZone timeZone = timeRegistry.getTimeZone(id);
		    	
		    	Calendar cal = Calendar.getInstance();
				cal.setTimeZone(timeZone);
				// + cal.get(Calendar.DST_OFFSET)
				int offset = cal.get(Calendar.ZONE_OFFSET);
				cal.setTime(tDate);
		    	
				int offsetInHours = offset/1000/60;
				
				String tString = cal.get(Calendar.DAY_OF_MONTH)
								+"."+cal.get(Calendar.DAY_OF_MONTH)
								+"."+cal.get(Calendar.YEAR)
								+" "
								+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE);
				
				
				System.out.println("<name>" + id + "</name><offset>"+offsetInHours+"</offset><now>"+tString+"</now>");
		    }
		    
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
