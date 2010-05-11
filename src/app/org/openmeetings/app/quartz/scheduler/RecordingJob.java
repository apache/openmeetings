package org.openmeetings.app.quartz.scheduler;

//import org.slf4j.Logger;
//import org.red5.logging.Red5LoggerFactory;

import org.red5.logging.Red5LoggerFactory; 
import org.slf4j.Logger;

import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.openmeetings.app.data.record.WhiteboardConvertionJobManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
 
public class RecordingJob {

	private static Logger log = Red5LoggerFactory.getLogger(RecordingJob.class, ScopeApplicationAdapter.webAppRootKey);
	
	//Spring loaded beans
	private WhiteboardConvertionJobManager whiteboardConvertionJobManager;

	public WhiteboardConvertionJobManager getWhiteboardConvertionJobManager() {
		return whiteboardConvertionJobManager;
	}
	public void setWhiteboardConvertionJobManager(
			WhiteboardConvertionJobManager whiteboardConvertionJobManager) {
		this.whiteboardConvertionJobManager = whiteboardConvertionJobManager;
	}

	public RecordingJob () {}
	
	public void doIt() {
		try {
			
			//cntxt.getScheduler().rescheduleJob("Income Session", "SessionClear Generation", cntxt.getTrigger());
			
			// TODO Generate report
			this.whiteboardConvertionJobManager.initJobs();
			//System.out.println("ISchedulingService do Clear Session"+this);
			//log.debug("ISchedulingService do Clear Session",this);
		} catch (Exception err){
			log.error("execute",err);
		}
	}
	

}
