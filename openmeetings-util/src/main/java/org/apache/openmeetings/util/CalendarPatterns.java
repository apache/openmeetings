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
package org.apache.openmeetings.util;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Wagner 27.08.2005 - 19:24:25
 *
 */
public class CalendarPatterns {
	private static final Logger log = LoggerFactory.getLogger(CalendarPatterns.class);

	public static final FastDateFormat dateFormat__ddMMyyyyHHmmss = FastDateFormat.getInstance("dd.MM.yyyy HH:mm:ss");
	public static final FastDateFormat dateFormat__ddMMyyyy = FastDateFormat.getInstance("dd.MM.yyyy");
	public static final FastDateFormat dateFormat__ddMMyyyyBySeparator = FastDateFormat.getInstance("dd-MM-yyyy");
	public static final FastDateFormat dateFormat__yyyyMMddHHmmss = FastDateFormat.getInstance("yyyy.MM.dd HH:mm:ss");
	public static final FastDateFormat STREAM_DATE_FORMAT = FastDateFormat.getInstance("yyyy_MM_dd_HH_mm_ss");
	public static final String FULL_DF_PATTERN = "dd.MM.yyyy HH:mm:ss z (Z)";
	public static final String ISO8601_FULL_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String ISO8601_DATETIME_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String ISO8601_DATE_FORMAT_STRING = "yyyy-MM-dd";
	public static final FastDateFormat FULL_DATE_FORMAT = FastDateFormat.getInstance(FULL_DF_PATTERN);
	public static final FastDateFormat ISO8601_FULL_FORMAT = FastDateFormat.getInstance(ISO8601_FULL_FORMAT_STRING);
	public static final FastDateFormat ISO8601_DATETIME_FORMAT = FastDateFormat.getInstance(ISO8601_DATETIME_FORMAT_STRING);
	public static final FastDateFormat ISO8601_DATE_FORMAT = FastDateFormat.getInstance(ISO8601_DATE_FORMAT_STRING);

	private CalendarPatterns() {}

	public static String getDateByMiliSeconds(Date t) {
		return dateFormat__yyyyMMddHHmmss.format(t);
	}

	public static String getDateWithTimeByMiliSeconds(Date t) {
		return t == null ? null : dateFormat__yyyyMMddHHmmss.format(t);
	}

	public static String getDateWithTimeByMiliSecondsWithZone(Date t) {
		return t == null ? null : FULL_DATE_FORMAT.format(t);
	}

	public static String getExportDate(Date t) {
		if (t == null) {
			return "";
		}
		return String.valueOf(t.getTime());
	}

	public static Date parseImportDate(String dateString) {
		try {
			Date resultDate = validDate(dateFormat__ddMMyyyyHHmmss, dateString);

			if (resultDate != null) {
				return resultDate;
			}

			resultDate = validDate(dateFormat__ddMMyyyy, dateString);

			if (resultDate != null) {
				return resultDate;
			}

			resultDate = validDate(dateString);

			if (dateString != null) {
				return resultDate;
			}

			log.error("parseImportDate:: Could not parse date string {}", dateString);
		} catch (Exception e) {
			log.error("parseImportDate", e);
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
			//no-op
		}
		return null;
	}

	private static Date validDate(FastDateFormat sdf, String testdate) {
		Date resultDate = null;
		try {
			resultDate = sdf.parse(testdate);
		} catch (ParseException|NumberFormatException e) {
			// if the format of the string provided doesn't match the format we
			// declared in SimpleDateFormat() we will get an exception
			return null;
		}

		if (!sdf.format(resultDate).equals(testdate)) {
			return null;
		}

		return resultDate;
	}

	public static String getDateWithTimeByMiliSecondsAndTimeZone(Date t, TimeZone timezone) {
		return t == null ? null : FastDateFormat.getInstance(FULL_DF_PATTERN, timezone).format(t);
	}

	public static String getTimeForStreamId(Date t) {
		return STREAM_DATE_FORMAT.format(t);
	}

	public static Date parseDate(String dateString) {
		try {
			return dateFormat__ddMMyyyy.parse(dateString);
		} catch (Exception e) {
			log.error("parseDate", e);
		}
		return null;
	}

	public static Date parseDateBySeparator(String dateString) {
		try {
			return dateFormat__ddMMyyyyBySeparator.parse(dateString);
		} catch (Exception e) {
			log.error("parseDateBySeparator", e);
		}
		return null;
	}

	public static Date parseDateWithHour(String dateString) {
		try {
			if (dateString == null || dateString.length() == 0
					|| "null".equals(dateString)) {
				return null;
			}
			return dateFormat__ddMMyyyyHHmmss.parse(dateString);
		} catch (Exception e) {
			log.error("parseDateWithHour", e);
		}
		return null;
	}
}
