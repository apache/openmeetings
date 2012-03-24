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
package org.openmeetings.axis.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.calendar.beans.Week;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CalendarWebServiceFacade {

	private static final Logger log = Red5LoggerFactory.getLogger(
			RoomWebServiceFacade.class, OpenmeetingsVariables.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}

	private CalendarWebService getCalendarServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (CalendarWebService) context.getBean("calendarWebService");
		} catch (Exception err) {
			log.error("[getCalendarServiceProxy]", err);
		}
		return null;
	}

	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) throws AxisFault {
		return getCalendarServiceProxy().getAppointmentByRange(SID, starttime,
				endtime);
	}

	public List<Appointment> getAppointmentByRangeForUserId(String SID,
			long userId, Date starttime, Date endtime) throws AxisFault {
		return getCalendarServiceProxy().getAppointmentByRangeForUserId(SID,
				userId, starttime, endtime);
	}

	public Appointment getNextAppointment(String SID) throws AxisFault {
		return getCalendarServiceProxy().getNextAppointment(SID);
	}

	public Appointment getNextAppointmentForUserId(String SID, long userId)
			throws AxisFault {
		return getCalendarServiceProxy().getNextAppointmentForUserId(SID,
				userId);
	}

	public List<Appointment> searchAppointmentByName(String SID,
			String appointmentName) throws AxisFault {
		return getCalendarServiceProxy().searchAppointmentByName(SID,
				appointmentName);
	}

	public Long saveAppointment(String SID, String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind, String[] mmClient,
			Long roomType, String baseUrl, Long languageId,
			Boolean isPasswordProtected, String password) throws AxisFault {
		return getCalendarServiceProxy().saveAppointment(SID, appointmentName,
				appointmentLocation, appointmentDescription, appointmentstart,
				appointmentend, isDaily, isWeekly, isMonthly, isYearly,
				categoryId, remind, mmClient, roomType, baseUrl, languageId,
				isPasswordProtected, password);
	}

	public Long updateAppointmentTimeOnly(String SID, Long appointmentId,
			Date appointmentstart, Date appointmentend, String baseurl,
			Long languageId) throws AxisFault {
		return getCalendarServiceProxy().updateAppointmentTimeOnly(SID,
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
		return getCalendarServiceProxy().updateAppointment(SID, appointmentId,
				appointmentName, appointmentLocation, appointmentDescription,
				appointmentstart, appointmentend, isDaily, isWeekly, isMonthly,
				isYearly, categoryId, remind, mmClient, roomType, baseurl,
				languageId, isPasswordProtected, password);
	}

	public Long deleteAppointment(String SID, Long appointmentId,
			Long language_id) throws AxisFault {
		return getCalendarServiceProxy().deleteAppointment(SID, appointmentId,
				language_id);
	}

	public Appointment getAppointmentByRoomId(String SID, Long room_id)
			throws AxisFault {
		return getCalendarServiceProxy().getAppointmentByRoomId(SID, room_id);
	}

	public List<AppointmentCategory> getAppointmentCategoryList(String SID)
			throws AxisFault {
		return getCalendarServiceProxy().getAppointmentCategoryList(SID);
	}

	public List<AppointmentReminderTyps> getAppointmentReminderTypList(
			String SID) throws AxisFault {
		return getCalendarServiceProxy().getAppointmentReminderTypList(SID);
	}

	public List<Week> getAppointmentsByWeekCalendar(String SID,
			int firstDayInWeek, Date startDate, Long requestUserId,
			Long omTimeZoneId, String javaTimeZoneName) throws AxisFault {
		return getCalendarServiceProxy().getAppointmentsByWeekCalendar(SID,
				firstDayInWeek, startDate, requestUserId, omTimeZoneId,
				javaTimeZoneName);
	}

}
