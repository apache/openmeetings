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
package org.apache.openmeetings.webservice.util;

import static org.apache.openmeetings.db.util.DtoHelper.optEnum;
import static org.apache.openmeetings.db.util.DtoHelper.optLong;
import static org.apache.openmeetings.util.CalendarPatterns.ISO8601_FULL_FORMAT;

import java.util.Date;

import jakarta.ws.rs.ext.ParamConverter;

import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.calendar.MeetingMemberDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class AppointmentParamConverter implements ParamConverter<AppointmentDTO> {
	public static final String ROOT = "appointmentDTO";

	@Override
	public AppointmentDTO fromString(String val) {
		JSONObject o = new JSONObject(val);
		if (o.has(ROOT)) {
			o = o.getJSONObject(ROOT);
		}
		AppointmentDTO a = new AppointmentDTO();
		a.setId(optLong(o, "id"));
		a.setTitle(o.optString("title"));
		a.setLocation(o.optString("location"));
		a.setOwner(UserDTO.get(o.optJSONObject("owner")));
		String tzId = a.getOwner() == null ? null : a.getOwner().getTimeZoneId();
		a.setStart(CalendarParamConverter.get(o.optString("start"), tzId));
		a.setEnd(CalendarParamConverter.get(o.optString("end"), tzId));
		a.setDescription(o.optString("description"));
		a.setInserted(DateParamConverter.get(o.optString("inserted")));
		a.setUpdated(DateParamConverter.get(o.optString("updated")));
		a.setDeleted(o.optBoolean("deleted"));
		a.setReminder(optEnum(Reminder.class, o, "reminder"));
		a.setRoom(RoomDTO.get(o.optJSONObject("room")));
		a.setIcalId(o.optString("icalId"));
		JSONArray mm = o.optJSONArray("meetingMembers");
		if (mm != null) {
			for (int i = 0; i < mm.length(); ++i) {
				a.getMeetingMembers().add(MeetingMemberDTO.get(mm.getJSONObject(i)));
			}
		}
		a.setLanguageId(o.optLong("languageId"));
		a.setPassword(o.optString("password"));
		a.setPasswordProtected(o.optBoolean("passwordProtected"));
		a.setConnectedEvent(o.optBoolean("connectedEvent"));
		a.setReminderEmailSend(o.optBoolean("reminderEmailSend"));
		return a;
	}

	public static JSONObject json(AppointmentDTO val) {
		Date i = val.getInserted(), u = val.getUpdated();
		return new JSONObject(val)
				.put("start", ISO8601_FULL_FORMAT.format(val.getStart()))
				.put("end", ISO8601_FULL_FORMAT.format(val.getEnd()))
				.put("inserted", i == null ? null : ISO8601_FULL_FORMAT.format(i))
				.put("updated", u == null ? null : ISO8601_FULL_FORMAT.format(u));
	}

	@Override
	public String toString(AppointmentDTO val) {
		return json(val).toString();
	}
}
