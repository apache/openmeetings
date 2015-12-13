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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.servlet.BaseHttpServlet;
import org.apache.openmeetings.servlet.ServerNotInitializedException;
import org.apache.openmeetings.utils.math.TimezoneUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class CalendarServlet extends BaseHttpServlet {
	
	private static final long serialVersionUID = 2192254610711799347L;
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			Calendar.class, OpenmeetingsVariables.webAppRootKey);

	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {
			String sid = httpServletRequest.getParameter("sid");

			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			Long users_id = getBean(SessiondataDao.class).checkSession(sid);
			Long user_level = getBean(UserManager.class).getUserLevelByID(users_id);

			if (user_level != null && user_level > 0) {

				String timeZoneIdAsStr = httpServletRequest
						.getParameter("timeZoneId");

				if (timeZoneIdAsStr == null) {
					log.error("No timeZoneIdAsStr given, using default");
					timeZoneIdAsStr = "";
				}

				TimeZone timezone = getBean(TimezoneUtil.class)
						.getTimezoneByOmTimeZoneId(
								Long.valueOf(timeZoneIdAsStr).longValue());

				String yearStr = httpServletRequest.getParameter("year");
				String monthStr = httpServletRequest.getParameter("month");
				String userStr = httpServletRequest.getParameter("user");
				String contactUser = httpServletRequest
						.getParameter("contactUser");

				Calendar starttime = GregorianCalendar.getInstance(timezone);
				starttime.set(Calendar.DATE, 1);
				starttime.set(Calendar.MONTH, Integer.parseInt(monthStr) - 1);
				starttime.set(Calendar.MINUTE, 0);
				starttime.set(Calendar.SECOND, 0);
				starttime.set(Calendar.YEAR, Integer.parseInt(yearStr));

				Calendar endtime = GregorianCalendar.getInstance(timezone);
				endtime.set(Calendar.DATE, 1);
				endtime.set(Calendar.MONTH, Integer.parseInt(monthStr));
				endtime.set(Calendar.MINUTE, 0);
				endtime.set(Calendar.SECOND, 0);
				endtime.set(Calendar.YEAR, Integer.parseInt(yearStr));

				Long userToShowId = Long.parseLong(contactUser);
				if (userToShowId == 0) {
					userToShowId = Long.parseLong(userStr);
				}

				List<Appointment> appointements = getBean(AppointmentLogic.class)
						.getAppointmentByRange(userToShowId,
								new Date(starttime.getTimeInMillis()),
								new Date(endtime.getTimeInMillis()));

				Document document = DocumentHelper.createDocument();
				document.setXMLEncoding("UTF-8");
				document.addComment("###############################################\n"
						+ getServletContext().getServletContextName()
						+ " Calendar \n"
						+ "###############################################");

				Element vcalendar = document.addElement("vcalendar");

				Element year = vcalendar.addElement("year" + yearStr);
				Element month = year.addElement("month" + monthStr);

				int previousDay = 0;
				Element day = null;

				for (Appointment appointment : appointements) {

					Calendar appStart = Calendar.getInstance(timezone);
					appStart.setTime(appointment.getAppointmentStarttime());

					int dayAsInt = appStart.get(Calendar.DATE);

					if (previousDay != dayAsInt) {

						day = month.addElement("day" + dayAsInt);

						previousDay = dayAsInt;

					}

					if (appStart.get(Calendar.MONTH) + 1 == Integer
							.parseInt(monthStr)) {

						Element event = day.addElement("event");

						Element appointementId = event
								.addElement("appointementId");
						appointementId.addAttribute("value",
								"" + appointment.getAppointmentId());

						Element isConnectedEvent = event
								.addElement("isConnectedEvent");
						isConnectedEvent.addAttribute("value",
								"" + appointment.getIsConnectedEvent());

						Element rooms_id = event.addElement("rooms_id");
						Element roomtype = event.addElement("roomtype");
						if (appointment.getRoom() != null) {
							rooms_id.addAttribute("value", ""
									+ appointment.getRoom().getRooms_id());
							roomtype.addAttribute("value", ""
									+ appointment.getRoom().getRoomtype()
											.getRoomtypes_id());
						} else {
							rooms_id.addAttribute("value", "0");
							roomtype.addAttribute("value", "1");
						}
						Element remindType = event.addElement("remindtype");
						remindType.addAttribute("value", appointment
								.getRemind() != null ? ""
								+ appointment.getRemind().getTypId() : "0");

						Element summary = event.addElement("summary");
						summary.addAttribute("value",
								appointment.getAppointmentName());

						Element comment = event.addElement("comment");
						comment.addAttribute("value",
								appointment.getAppointmentDescription());

						Element start = event.addElement("start");

						start.addAttribute("year",
								"" + appStart.get(Calendar.YEAR));
						start.addAttribute("month",
								"" + (appStart.get(Calendar.MONTH) + 1));
						start.addAttribute("day",
								"" + appStart.get(Calendar.DATE));
						start.addAttribute("hour",
								"" + appStart.get(Calendar.HOUR_OF_DAY));
						start.addAttribute("minute",
								"" + appStart.get(Calendar.MINUTE));

						Calendar appEnd = Calendar.getInstance(timezone);
						appEnd.setTime(appointment.getAppointmentEndtime());
						Element end = event.addElement("end");
						end.addAttribute("year", "" + appEnd.get(Calendar.YEAR));
						end.addAttribute("month",
								"" + (appEnd.get(Calendar.MONTH) + 1));
						end.addAttribute("day", "" + appEnd.get(Calendar.DATE));
						end.addAttribute("hour", "" + appEnd.get(Calendar.HOUR_OF_DAY));
						end.addAttribute("minute",
								"" + appEnd.get(Calendar.MINUTE));

						Element category = event.addElement("category");
						category.addAttribute("value", ""
								+ appointment.getAppointmentCategory()
										.getCategoryId());

						Element uid = event.addElement("uid");
						uid.addAttribute("value",
								"" + appointment.getAppointmentId());

						Element attendees = event.addElement("attendees");

						for (MeetingMember meetingMember : appointment
								.getMeetingMember()) {

							Element attendee = attendees.addElement("attendee");

							Element email = attendee.addElement("email");
							email.addAttribute("value",
									meetingMember.getEmail());

							Element userId = attendee.addElement("userId");
							if (meetingMember.getUserid() != null) {
								userId.addAttribute("value", ""
										+ meetingMember.getUserid()
												.getUser_id());
							} else {
								userId.addAttribute("value", "");
							}

							Element memberId = attendee.addElement("memberId");
							memberId.addAttribute("value",
									"" + meetingMember.getMeetingMemberId());

							Element firstname = attendee
									.addElement("firstname");
							memberId.addAttribute("value",
									"" + meetingMember.getMeetingMemberId());
							firstname.addAttribute("value",
									meetingMember.getFirstname());

							Element lastname = attendee.addElement("lastname");
							lastname.addAttribute("value",
									meetingMember.getLastname());

							Element jNameTimeZoneMember = attendee
									.addElement("jNameTimeZone");
							if (meetingMember.getOmTimeZone() != null) {
								jNameTimeZoneMember.addAttribute("value",
										meetingMember.getOmTimeZone()
												.getJname());
							} else {
								jNameTimeZoneMember.addAttribute("value", "");
							}

						}

					}

				}

				httpServletResponse.reset();
				httpServletResponse.resetBuffer();
				OutputStream out = httpServletResponse.getOutputStream();
				httpServletResponse.setContentType("text/xml");

				// httpServletResponse.setHeader("Content-Length", ""+
				// rf.length());

				OutputFormat outformat = OutputFormat.createPrettyPrint();
				outformat.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(out, outformat);
				writer.write(document);
				writer.flush();

				out.flush();
				out.close();

			}
		} catch (ServerNotInitializedException e) {
			return;
		} catch (Exception er) {
			log.error("[Calendar :: service]", er);
		}
	}

}
