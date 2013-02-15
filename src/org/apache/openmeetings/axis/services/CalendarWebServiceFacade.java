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
import org.apache.openmeetings.axis.BaseWebService;
import org.apache.openmeetings.data.calendar.beans.Week;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentCategory;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;

public class CalendarWebServiceFacade extends BaseWebService {
	
	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentByRange(SID, starttime,
				endtime);
	}

	public List<Appointment> getAppointmentByRangeForUserId(String SID,
			long userId, Date starttime, Date endtime) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentByRangeForUserId(SID,
				userId, starttime, endtime);
	}

	public Appointment getNextAppointment(String SID) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getNextAppointment(SID);
	}

	public Appointment getNextAppointmentForUserId(String SID, long userId)
			throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getNextAppointmentForUserId(SID,
				userId);
	}

	public List<Appointment> searchAppointmentByName(String SID,
			String appointmentName) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).searchAppointmentByName(SID,
				appointmentName);
	}

	public Long saveAppointment(String SID, String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind, String[] mmClient,
			Long roomType, String baseUrl, Long languageId,
			Boolean isPasswordProtected, String password, long roomId) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).saveAppointment(SID, appointmentName,
				appointmentLocation, appointmentDescription, appointmentstart,
				appointmentend, isDaily, isWeekly, isMonthly, isYearly,
				categoryId, remind, mmClient, roomType, baseUrl, languageId,
				isPasswordProtected, password, roomId);
	}

	public Long updateAppointmentTimeOnly(String SID, Long appointmentId,
			Date appointmentstart, Date appointmentend, String baseurl,
			Long languageId) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).updateAppointmentTimeOnly(SID,
				appointmentId, appointmentstart, appointmentend, baseurl,
				languageId);
	}

	public Long updateAppointment(String SID, Long appointmentId,
			String appointmentName, String appointmentLocation,
			String appointmentDescription, Calendar appointmentstart,
			Calendar appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			String[] mmClient, Long roomType, String baseurl, Long languageId,
			Boolean isPasswordProtected, String password) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).updateAppointment(SID, appointmentId,
				appointmentName, appointmentLocation, appointmentDescription,
				appointmentstart, appointmentend, isDaily, isWeekly, isMonthly,
				isYearly, categoryId, remind, mmClient, roomType, baseurl,
				languageId, isPasswordProtected, password);
	}

	public Long deleteAppointment(String SID, Long appointmentId,
			Long language_id) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).deleteAppointment(SID, appointmentId,
				language_id);
	}

	public Appointment getAppointmentByRoomId(String SID, Long room_id)
			throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentByRoomId(SID, room_id);
	}

	public List<AppointmentCategory> getAppointmentCategoryList(String SID)
			throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentCategoryList(SID);
	}

	public List<AppointmentReminderTyps> getAppointmentReminderTypList(
			String SID) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentReminderTypList(SID);
	}

	public List<Week> getAppointmentsByWeekCalendar(String SID,
			int firstDayInWeek, Date startDate, Long requestUserId,
			Long omTimeZoneId, String javaTimeZoneName) throws AxisFault {
		return getBeanUtil().getBean(CalendarWebService.class, getServletContext()).getAppointmentsByWeekCalendar(SID,
				firstDayInWeek, startDate, requestUserId, omTimeZoneId,
				javaTimeZoneName);
	}

}
