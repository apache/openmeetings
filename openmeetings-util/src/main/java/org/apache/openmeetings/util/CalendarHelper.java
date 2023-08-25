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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalendarHelper {
	private CalendarHelper() {
		// denied
	}

	public static ZoneId getZoneId(String tzId) {
		return ZoneId.of(tzId, ZoneId.SHORT_IDS);
	}

	public static Date getDate(LocalDate d, String tzId) {
		return getDate(d.atStartOfDay(), tzId);
	}

	public static Date getDate(LocalDateTime d, String tzId) {
		return Date.from(d.atZone(getZoneId(tzId)).toInstant());
	}

	public static ZonedDateTime getZoneDateTime(Date d, String tzId) {
		long milli = (d == null ? new Date() : d).getTime();
		return Instant.ofEpochMilli(milli).atZone(getZoneId(tzId));
	}

	public static LocalDate getDate(Date d, String tzId) {
		return getZoneDateTime(d == null ? new Date() : d, tzId).toLocalDate();
	}

	public static LocalDateTime getDateTime(Date d, String tzId) {
		return getZoneDateTime(d == null ? new Date() : d, tzId).toLocalDateTime();
	}

	public static String formatMillis(long millis) {
		long m = millis;
		long hours = TimeUnit.MILLISECONDS.toHours(m);
		m -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(m);
		m -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(m);
		m -= TimeUnit.SECONDS.toMillis(seconds);
		return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, m);
	}
}
