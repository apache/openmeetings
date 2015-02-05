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
package org.apache.openmeetings.axis.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.calendar.AppointmentReminderTypeDTO;
import org.apache.openmeetings.db.entity.calendar.AppointmentCategory;

public class CalendarWebServiceFacade extends BaseWebService {
	
	public List<AppointmentDTO> getAppointmentByRange(String SID, Calendar starttime, Calendar endtime) throws AxisFault {
		return getBean(CalendarWebService.class).getAppointmentByRange(SID, starttime, endtime);
	}

	public List<AppointmentDTO> getAppointmentByRangeForUserId(String SID, long userId, Calendar starttime, Calendar endtime) throws AxisFault {
		return getBean(CalendarWebService.class).getAppointmentByRangeForUserId(SID, userId, starttime, endtime);
	}

	public AppointmentDTO getNextAppointment(String SID) throws AxisFault {
		return getBean(CalendarWebService.class).getNextAppointment(SID);
	}

	public AppointmentDTO getNextAppointmentForUserId(String SID, long userId) throws AxisFault {
		return getBean(CalendarWebService.class).getNextAppointmentForUserId(SID, userId);
	}

	public List<AppointmentDTO> searchAppointmentByName(String SID, String appointmentName) throws AxisFault {
		return getBean(CalendarWebService.class).searchAppointmentByName(SID, appointmentName);
	}

	public Long saveAppointment(String SID, String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind, String[] mmClient,
			Long roomType, Long languageId,
			Boolean isPasswordProtected, String password, long roomId) throws AxisFault {
		return getBean(CalendarWebService.class).saveAppointment(SID, appointmentName,
				appointmentLocation, appointmentDescription, appointmentstart,
				appointmentend, isDaily, isWeekly, isMonthly, isYearly,
				categoryId, remind, mmClient, roomType, languageId,
				isPasswordProtected, password, roomId);
	}

	public Long updateAppointmentTimeOnly(String SID, Long appointmentId, Date appointmentstart, Date appointmentend, Long languageId) throws AxisFault {
		return getBean(CalendarWebService.class).updateAppointmentTimeOnly(SID, appointmentId, appointmentstart, appointmentend, languageId);
	}

	public Long updateAppointment(String SID, Long appointmentId,
			String appointmentName, String appointmentLocation,
			String appointmentDescription, Calendar appointmentstart,
			Calendar appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			String[] mmClient, Long roomType, Long languageId,
			Boolean isPasswordProtected, String password) throws AxisFault {
		return getBean(CalendarWebService.class).updateAppointment(SID, appointmentId,
				appointmentName, appointmentLocation, appointmentDescription,
				appointmentstart, appointmentend, isDaily, isWeekly, isMonthly,
				isYearly, categoryId, remind, mmClient, roomType, languageId, isPasswordProtected, password);
	}

	public Long deleteAppointment(String SID, Long appointmentId, Long language_id) throws AxisFault {
		return getBean(CalendarWebService.class).deleteAppointment(SID, appointmentId, language_id);
	}

	public AppointmentDTO getAppointmentByRoomId(String SID, Long room_id) throws AxisFault {
		return getBean(CalendarWebService.class).getAppointmentByRoomId(SID, room_id);
	}

	public List<AppointmentCategory> getAppointmentCategoryList(String SID) throws AxisFault {
		return getBean(CalendarWebService.class).getAppointmentCategoryList(SID);
	}

	public List<AppointmentReminderTypeDTO> getAppointmentReminderTypList(String SID) throws AxisFault {
		return getBean(CalendarWebService.class).getAppointmentReminderTypList(SID);
	}
}
