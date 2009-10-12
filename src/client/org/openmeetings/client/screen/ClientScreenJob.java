 package org.openmeetings.client.screen;

import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;

import org.apache.log4j.Logger;

public class ClientScreenJob implements Job {
	
	private static Logger log = Logger.getLogger(ClientScreenJob.class);
	
    public ClientScreenJob() { }

    public void execute(JobExecutionContext context) throws JobExecutionException {
    	log.debug("ScreenJob is executing.");
    	
    	new ClientCaptureScreen(false);
    	
    }

}
