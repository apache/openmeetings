package org.openmeetings.app.quartz.scheduler;


import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingReminderJob {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingReminderJob.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private AppointmentLogic appointmentLogic;
	
	public void doIt() {
		log.trace("MeetingReminderJob.execute");
		try {
			appointmentLogic.doScheduledMeetingReminder();
		} catch (Exception err){
			log.error("execute",err);
		}
	}
}
