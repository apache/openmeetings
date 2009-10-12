package org.openmeetings.app.quartz.scheduler;


import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.slf4j.LoggerFactory;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.apache.log4j.Logger;

public class QuartzMeetingReminderJob implements IScheduledJob {

	private static Logger log = Logger.getLogger(QuartzMeetingReminderJob.class);

	public void execute(ISchedulingService service) {
		log.debug("QuartzMeetingReminderJob.execute");
		try {
			AppointmentLogic.getInstance().doScheduledMeetingReminder();
		
		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
