package org.openmeetings.app.quartz.scheduler;

import org.openmeetings.app.data.record.WhiteboardConvertionJobManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
 
public class RecordingJob {
	private static Logger log = Red5LoggerFactory.getLogger(RecordingJob.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private WhiteboardConvertionJobManager whiteboardConvertionJobManager;

	public void doIt() {
		log.trace("RecordingJob.execute");
		try {
			// TODO Generate report
			this.whiteboardConvertionJobManager.initJobs();
		} catch (Exception err){
			log.error("execute",err);
		}
	}
}
