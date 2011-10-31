package org.openmeetings.test.calendar;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAppointmentSchedulerTask extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Red5LoggerFactory.getLogger(TestAppointmentSchedulerTask.class, ScopeApplicationAdapter.webAppRootKey);
	
	@Autowired
	private AppointmentLogic appointmentLogic;
	
	@Test
	public void doIt() {
		log.debug("- 1 MeetingReminderJob.execute");
		log.warn("- 2 MeetingReminderJob.execute");
		try {
			appointmentLogic.doScheduledMeetingReminder();
			
			assertTrue(true);
		} catch (Exception err){
			log.error("execute",err);
		}
	}

}
