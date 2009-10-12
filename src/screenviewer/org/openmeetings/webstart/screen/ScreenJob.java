 package org.openmeetings.webstart.screen;

import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;

import org.openmeetings.webstart.beans.ConnectionBean;
import org.openmeetings.webstart.gui.StartScreen;

public class ScreenJob implements Job {
	
    public ScreenJob() { }

    public void execute(JobExecutionContext context) throws JobExecutionException {
    	System.out.println("ScreenJob is executing.");
    	if (ConnectionBean.isloading){
    		StartScreen.instance.showBandwidthWarning("Your Bandwidth is bad. Frames have been droped. You can alter the Quality settings to reduce Bandwidth usage.");
    		ConnectionBean.isloading = false;
    	} else {
    		StartScreen.instance.showBandwidthWarning("sending");
    		new CaptureScreen(ConnectionBean.connectionURL,ConnectionBean.SID,ConnectionBean.room,ConnectionBean.domain,ConnectionBean.publicSID,ConnectionBean.record);
    	}
    }

}
