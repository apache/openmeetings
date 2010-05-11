package org.openmeetings.app.quartz.scheduler;

//import org.slf4j.Logger;
//import org.red5.logging.Red5LoggerFactory;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory; 
import org.slf4j.Logger;

import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
 
public class QuartzZombieJob implements IScheduledJob {

	private static Logger log = Red5LoggerFactory.getLogger(QuartzZombieJob.class, ScopeApplicationAdapter.webAppRootKey);

	public void execute(ISchedulingService service) {
		try {
			
			//cntxt.getScheduler().rescheduleJob("Income Session", "SessionClear Generation", cntxt.getTrigger());
			
			// FIXME: Check for Zombies Issue 583
			//
			//ScopeApplicationAdapter.getInstance().clearZombiesFromAllConnection();

		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
