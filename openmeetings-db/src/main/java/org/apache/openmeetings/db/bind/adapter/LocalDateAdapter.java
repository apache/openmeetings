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
package org.apache.openmeetings.db.bind.adapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.util.CalendarPatterns;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public String marshal(LocalDate v) throws Exception {
		return v.format(DateTimeFormatter.ofPattern(CalendarPatterns.ISO8601_DATE_FORMAT_STRING));
	}

	@Override
	public LocalDate unmarshal(String v) throws Exception {
		if (v == null || "null".equals(v)) {
			return null;
		}
		try {
			return LocalDate.parse(v, DateTimeFormatter.ofPattern(CalendarPatterns.ISO8601_DATE_FORMAT_STRING));
		} catch (Exception err) {
			//no-op
		}
		try {
			Long t = Long.valueOf(v);

			if (t != null) {
				return Instant.ofEpochMilli(t).atZone(ZoneOffset.UTC).toLocalDate();
			}
		} catch (Exception err) {
			//no-op
		}
		return null;
	}
}
