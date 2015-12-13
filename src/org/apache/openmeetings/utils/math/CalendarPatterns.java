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
package org.apache.openmeetings.utils.math;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Sebastian Wagner
 * 27.08.2005 - 19:24:25
 *
 */
public class CalendarPatterns {
	
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarPatterns.class, "openmeetings");
	
	public static SimpleDateFormat dateFormat__ddMMyyyyHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	public static SimpleDateFormat dateFormat__ddMMyyyy = new SimpleDateFormat("dd.MM.yyyy");
	public static SimpleDateFormat dateFormat__ddMMyyyyBySeparator = new SimpleDateFormat("dd-MM-yyyy");
    
    public static String getDateByMiliSeconds(Date t){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }
    
    public static String getDateWithTimeByMiliSeconds(Date t){
    	if (t == null) {
    		return null;
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }
    
    public static String getDateWithTimeByMiliSeconds(Date t, TimeZone timezone){
    	if (t == null) {
    		return null;
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(timezone);
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }
    
    public static String getDateWithTimeByMiliSecondsWithZone(Date t){
    	if (t == null) {
    		return null;
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z (Z)");
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }
    
    public static String getExportDate(Date t){
    	if (t == null) {
    		return "";
    	}
        return ""+t.getTime();
    }
    
    public static Date parseImportDate(String dateString) {
    	try {
    		
    		Date resultDate = null;
    		
    		resultDate = validDate(dateFormat__ddMMyyyyHHmmss, dateString);
    		
    		if ( resultDate != null ) {
    			return resultDate;
    		}
    		
    		resultDate = validDate(dateFormat__ddMMyyyy, dateString);
    		
    		if ( resultDate != null ) {
    			return resultDate;
    		}
    		
    		resultDate = validDate(dateString);
    		
    		if (dateString != null) {
    			return resultDate;
    		}
    		
    		
    		throw new Exception ("Could not parse date string "+dateString);
    	} catch (Exception e) {
    		log.error("parseDate",e);
    	}
    	return null;
    }
    
    private static Date validDate(String testdate) {
    	try {
    		
    		Long t = Long.valueOf(testdate);
    		
    		if (t != null) {
    			return new Date(t);
    		}
    		
    	} catch (Exception err) {
    	}
    	return null;
    }
    
    private static Date validDate(SimpleDateFormat sdf, String testdate)
    {
        Date resultDate = null;
        try
        {
        	resultDate = sdf.parse(testdate);
        }

        // if the format of the string provided doesn't match the format we 
        // declared in SimpleDateFormat() we will get an exception

        catch (java.text.ParseException e)
        {
          return null;
        }

        if (!sdf.format(resultDate).equals(testdate)) 
        {
          return null;
        }
        
        return resultDate;

    }
    
    public static String getDateWithTimeByMiliSecondsAndTimeZone(Date t, TimeZone timezone){
    	if (t == null) {
    		return null;
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z (Z)");
        sdf.setTimeZone(timezone);
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }
    
    public static String getTimeForStreamId(Date t){
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    }    
    
    public static Date parseDate(String dateString) {
    	try {
    		return dateFormat__ddMMyyyy.parse(dateString);
    	} catch (Exception e) {
    		log.error("parseDate",e);
    	}
    	return null;
    }
    
    public static Date parseDateBySeparator(String dateString) {
    	try {
    		return dateFormat__ddMMyyyyBySeparator.parse(dateString);
    	} catch (Exception e) {
    		log.error("parseDate",e);
    	}
    	return null;
    }
    
    public static Date parseDateWithHour(String dateString) {
    	try {
    		if (dateString == null || dateString.length()==0 || dateString.equals("null") ) {
        		return null;
        	}
    		return dateFormat__ddMMyyyyHHmmss.parse(dateString);
    	} catch (Exception e) {
    		log.error("parseDate",e);
    	}
    	return null;
    }

	public static String getYear(Date t){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date dateOld = new Date();
        long timeAdv = t.getTime();
        dateOld.setTime(timeAdv);
        String result = sdf.format(dateOld);
        return result;
    } 
    
}
