package org.openmeetings.app.quartz.scheduler;

//import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;

import org.slf4j.LoggerFactory; 
import org.apache.log4j.Logger;

import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.openmeetings.app.data.record.WhiteboardConvertionJobManager;
 
public class RecordingJob {

	private static Logger log = Logger.getLogger(RecordingJob.class);
	
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
