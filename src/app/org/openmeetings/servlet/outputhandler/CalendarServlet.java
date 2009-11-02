package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class CalendarServlet extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(Calendar.class, "openmeetings");
	
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

			if (true || (user_level!=null && user_level > 0)) {
				
				String yearStr = httpServletRequest.getParameter("year");
				String monthStr = httpServletRequest.getParameter("month");
				String userStr = httpServletRequest.getParameter("user");
				
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
				
				List<Appointment> appointements = AppointmentLogic.getInstance().getAppointmentByRange(Long.parseLong(userStr), new Date(starttime.getTimeInMillis()), new Date(endtime.getTimeInMillis()));
				
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
					
					int dayAsInt = appointment.getAppointmentStarttime().getDate();
					
					if (previousDay != dayAsInt){
					
						day = month.addElement("day"+dayAsInt);
						
						previousDay = dayAsInt;
					
					}
					
					if (appointment.getAppointmentStarttime().getMonth()+1 == Integer.parseInt(monthStr)) {
					
						Element event = day.addElement("event");
						
						Element appointementId = event.addElement("appointementId");
						appointementId.addAttribute("value",""+appointment.getAppointmentId());
						
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
