package org.openmeetings.app.quartz.scheduler;


import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.slf4j.LoggerFactory;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.apache.log4j.Logger;

public class MeetingReminderJob {

	private static Logger log = Logger.getLogger(MeetingReminderJob.class);

	public void doIt() {
		log.debug("QuartzMeetingReminderJob.execute");
		try {
			AppointmentLogic.getInstance().doScheduledMeetingReminder();
		
		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
