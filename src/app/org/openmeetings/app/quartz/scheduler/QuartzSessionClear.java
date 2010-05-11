package org.openmeetings.app.quartz.scheduler;

//import org.slf4j.Logger;
//import org.red5.logging.Red5LoggerFactory;

import org.red5.logging.Red5LoggerFactory; 
import org.slf4j.Logger;

import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
 
public class QuartzSessionClear implements IScheduledJob {

	private static Logger log = Red5LoggerFactory.getLogger(QuartzSessionClear.class, ScopeApplicationAdapter.webAppRootKey);

	public void execute(ISchedulingService service) {
		try {
			
			//cntxt.getScheduler().rescheduleJob("Income Session", "SessionClear Generation", cntxt.getTrigger());
			
			// TODO Generate report
			Sessionmanagement.getInstance().clearSessionTable();
			//System.out.println("ISchedulingService do Clear Session"+this);
			//log.debug("ISchedulingService do Clear Session",this);
		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
