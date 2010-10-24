package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.basic.OmTimeZone;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class CalendarServlet extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(Calendar.class, ScopeApplicationAdapter.webAppRootKey);
	
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

			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);

			if (user_level!=null && user_level > 0) {
				
				String yearStr = httpServletRequest.getParameter("year");
				String monthStr = httpServletRequest.getParameter("month");
				String userStr = httpServletRequest.getParameter("user");
				String contactUser = httpServletRequest.getParameter("contactUser");
				
				Calendar starttime = GregorianCalendar.getInstance();
				
				starttime.set(Calendar.DATE, 1);
				starttime.set(Calendar.MONTH, Integer.parseInt(monthStr)-1);
				starttime.set(Calendar.MINUTE,0);
				starttime.set(Calendar.SECOND, 0);
				starttime.set(Calendar.YEAR, Integer.parseInt(yearStr));
				
				Calendar endtime = GregorianCalendar.getInstance();
				
				endtime.set(Calendar.DATE, 1);
				endtime.set(Calendar.MONTH, Integer.parseInt(monthStr));
				endtime.set(Calendar.MINUTE,0);
				endtime.set(Calendar.SECOND, 0);
				endtime.set(Calendar.YEAR, Integer.parseInt(yearStr));
				
				Long userToShowId = Long.parseLong(contactUser);
				if (userToShowId == 0) {
					userToShowId = Long.parseLong(userStr);
				}
				
				List<Appointment> appointements = AppointmentLogic.getInstance().getAppointmentByRange(userToShowId, new Date(starttime.getTimeInMillis()), new Date(endtime.getTimeInMillis()));
				
				Document document = DocumentHelper.createDocument();
				document.setXMLEncoding("UTF-8");
				document.addComment(
						"###############################################\n" +
						"OpenMeetings Calendar \n" +
						"###############################################");
				
				Element vcalendar = document.addElement("vcalendar");
				
				Element year = vcalendar.addElement("year"+yearStr);
				Element month = year.addElement("month"+monthStr);
				
				int previousDay = 0;
				Element day = null;
				
				for (Appointment appointment : appointements) {
					String jNameTimeZone = "Europe/Berlin";
					OmTimeZone omTimeZone = null;
					
					String timeZoneIdAsStr = httpServletRequest.getParameter("timeZoneId");
					
					System.out.println("CalendarServlet jNameTimeZone "+jNameTimeZone );
					
					if (timeZoneIdAsStr == null) {
						
						Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
						if (conf != null) {
							jNameTimeZone = conf.getConf_value();
						}
						omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
						
					} else {
						
						//System.out.println("CalendarServlet TimeZone "+jNameTimeZone );
						omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZoneById(Long.valueOf(timeZoneIdAsStr).longValue());
						
						if (omTimeZone == null) {
							Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
							if (conf != null) {
								jNameTimeZone = conf.getConf_value();
							}
							omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
						}
						
					}
					
					jNameTimeZone = omTimeZone.getIcal();
					TimeZone timeZone = TimeZone.getTimeZone(jNameTimeZone);
					
					Calendar cal = Calendar.getInstance();
					cal.setTimeZone(timeZone);
					int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
					
					//System.out.println("CalendarServlet offset "+offset );
					//System.out.println("CalendarServlet TimeZone "+TimeZone.getDefault().getID() );
					//log.debug("addAppointment offset :: "+offset);
					
					appointment.setAppointmentStarttime(new Date(appointment.getAppointmentStarttime().getTime() + offset));
					appointment.setAppointmentEndtime(new Date(appointment.getAppointmentEndtime().getTime() + offset));
					
					int dayAsInt = appointment.getAppointmentStarttime().getDate();
					
					if (previousDay != dayAsInt){
					
						day = month.addElement("day"+dayAsInt);
						
						previousDay = dayAsInt;
					
					}
					
					if (appointment.getAppointmentStarttime().getMonth()+1 == Integer.parseInt(monthStr)) {
					
						Element event = day.addElement("event");
						
						Element appointementId = event.addElement("appointementId");
						appointementId.addAttribute("value",""+appointment.getAppointmentId());
						
						Element isConnectedEvent = event.addElement("isConnectedEvent");
						isConnectedEvent.addAttribute("value",""+appointment.getIsConnectedEvent());
						
						Element summary = event.addElement("summary");
						summary.addAttribute("value",appointment.getAppointmentName());
						
						Element comment = event.addElement("comment");
						comment.addAttribute("value", appointment.getAppointmentDescription());
						
						Element start = event.addElement("start");
						
						start.addAttribute("year", ""+(appointment.getAppointmentStarttime().getYear()+1900));
						start.addAttribute("month", ""+(appointment.getAppointmentStarttime().getMonth()+1));
						start.addAttribute("day", ""+appointment.getAppointmentStarttime().getDate());
						start.addAttribute("hour", ""+appointment.getAppointmentStarttime().getHours());
						start.addAttribute("minute", ""+appointment.getAppointmentStarttime().getMinutes());
						
						Element end = event.addElement("end");
						end.addAttribute("year", ""+(appointment.getAppointmentEndtime().getYear()+1900));
						end.addAttribute("month", ""+(appointment.getAppointmentEndtime().getMonth()+1));
						end.addAttribute("day", ""+appointment.getAppointmentEndtime().getDate());
						end.addAttribute("hour", ""+appointment.getAppointmentEndtime().getHours());
						end.addAttribute("minute", ""+appointment.getAppointmentEndtime().getMinutes());
					
						Element category = event.addElement("category");
						category.addAttribute("value",""+appointment.getAppointmentCategory().getCategoryId());
						
						Element uid = event.addElement("uid");
						uid.addAttribute("value",""+appointment.getAppointmentId());
						
						Element attendees = event.addElement("attendees");
						
						for (MeetingMember meetingMember : appointment.getMeetingMember()) {
							
							Element attendee = attendees.addElement("attendee");
							
							Element email = attendee.addElement("email");
							email.addAttribute("value", meetingMember.getEmail());
							
							Element userId = attendee.addElement("userId");
							if (meetingMember.getUserid() != null) {
								userId.addAttribute("value", ""+meetingMember.getUserid().getUser_id());
							} else {
								userId.addAttribute("value", "");
							}
							
							Element memberId = attendee.addElement("memberId");
							memberId.addAttribute("value", ""+meetingMember.getMeetingMemberId());
							
							Element firstname = attendee.addElement("firstname");memberId.addAttribute("value", ""+meetingMember.getMeetingMemberId());
							firstname.addAttribute("value", meetingMember.getFirstname());
							
							Element lastname = attendee.addElement("lastname");
							lastname.addAttribute("value", meetingMember.getLastname());
							
							Element jNameTimeZoneMember = attendee.addElement("jNameTimeZone");
							if (meetingMember.getOmTimeZone() != null) {
								jNameTimeZoneMember.addAttribute("value", meetingMember.getOmTimeZone().getJname());
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
			
		} catch (Exception er) {
			System.out.println("Error downloading: " + er);
			er.printStackTrace();
			log.error("[Calendar :: service]",er);
		}
	}

}
