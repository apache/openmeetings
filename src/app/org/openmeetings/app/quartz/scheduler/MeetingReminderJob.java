package org.openmeetings.app.quartz.scheduler;


import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.slf4j.Logger;

public class MeetingReminderJob {

	private static Logger log = Red5LoggerFactory.getLogger(MeetingReminderJob.class, "openmeetings");

	public void doIt() {
		log.debug("QuartzMeetingReminderJob.execute");
		try {
			AppointmentLogic.getInstance().doScheduledMeetingReminder();
		
		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
