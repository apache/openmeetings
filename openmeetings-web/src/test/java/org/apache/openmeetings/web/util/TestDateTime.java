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
package org.apache.openmeetings.web.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.openmeetings.web.RegularTest;
import org.junit.jupiter.api.Test;

@RegularTest
class TestDateTime {

	@Test
	void test1() throws Exception {
		final String dateStr = "19-11-29 07:05";
		final String pattern = "y-MM-dd HH:mm";
		final Locale loc = new Locale.Builder()
				.setLanguage("sv")
				.setRegion("SE")
				.build();
		Date d = new SimpleDateFormat(pattern, loc).parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertEquals(2019, c.get(Calendar.YEAR), "4 digit year expected");
		LocalDateTime dt = LocalDateTime.parse(dateStr
				, DateTimeFormatter.ofPattern(pattern, loc));
		assertEquals(19, dt.getYear(), "2 digit year expected");
		dt = LocalDateTime.parse(dateStr
				, DateTimeFormatter.ofPattern(pattern.replace("y", "yy"), loc));
		assertEquals(2019, dt.getYear(), "4 digit year expected");
	}

	@Test
	void test2() throws Exception {
		final String dateStr = "2020-05-12, 6:43 a.m.";
		final String jsDateStr = "2020-05-12, 6:43 AM";
		final String pattern = "y-MM-dd, h:mm a";
		final Locale loc = new Locale.Builder()
				.setLanguage("en")
				.setRegion("CA")
				.build();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern(pattern)
				.toFormatter(loc);
		assertNotNull(formatter.parse(dateStr));
		DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern(pattern)
				.toFormatter(Locale.ENGLISH);
		assertNotNull(formatter1.parse(jsDateStr));
	}
}
