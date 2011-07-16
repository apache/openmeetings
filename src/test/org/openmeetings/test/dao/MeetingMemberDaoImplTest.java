package org.openmeetings.test.dao;

import java.util.Date;

import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class MeetingMemberDaoImplTest extends AbstractTestCase {
	
	public MeetingMemberDaoImplTest(String name) {
		super(name);
	}

	final public void testMeetingMemberDaoImpl() throws Exception {
		
		Long userId = 1L;
		Users user = Usermanagement.getInstance().getUserById(userId);
		assertNotNull("Cann't get default user", user);
		
		// add new appointment
		Appointment ap = new Appointment();
		
		ap.setAppointmentName("appointmentName");
		ap.setAppointmentLocation("appointmentLocation");
		
		Date appointmentstart = new Date();
		Date appointmentend = new Date();
		appointmentend.setTime(appointmentstart.getTime() + 3600);
		
		ap.setAppointmentStarttime(appointmentstart);
	 	ap.setAppointmentEndtime(appointmentend);
		ap.setAppointmentDescription("appointmentDescription");
		ap.setStarttime(new Date());
		ap.setDeleted("false");
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setIsPasswordProtected(false);

		ap.setUserId(UsersDaoImpl.getInstance().getUser(userId));
		ap.setIsConnectedEvent(false);
		Long appointmentId = AppointmentDaoImpl.getInstance().addAppointmentObj(ap);
		assertNotNull("Cann't add appointment", appointmentId);
	
		String jNameMemberTimeZone = "";
		if (user.getOmTimeZone() != null) {
			jNameMemberTimeZone = user.getOmTimeZone().getJname();
		}
		
		Long mmId = MeetingMemberDaoImpl.getInstance().addMeetingMember(user.getFirstname(), user.getLastname(), "", "", appointmentId, userId, user.getAdresses().getEmail(), false, jNameMemberTimeZone, false);
		assertNotNull("Cann't add MeetingMember", mmId);
		
		MeetingMember mm = MeetingMemberDaoImpl.getInstance().getMeetingMemberById(mmId);
		assertNotNull("Cann't get MeetingMember", mm);
		
		mmId = MeetingMemberDaoImpl.getInstance().deleteMeetingMember(mmId);
		assertNotNull("Cann't delete MeetingMember", mmId);
		
		appointmentId = AppointmentDaoImpl.getInstance().deleteAppointement(appointmentId);
		assertNotNull("Cann't delete appointment", appointmentId);
	}
}
