package org.openmeetings.app.quartz.scheduler;

import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
 
public class SessionClearJob {
	private static Logger log = Red5LoggerFactory.getLogger(SessionClearJob.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private Sessionmanagement sessionManagement;

	public void doIt() {
		log.trace("SessionClearJob.execute");
		try {
			// TODO Generate report
			sessionManagement.clearSessionTable();
		} catch (Exception err){
			log.error("execute",err);
		}
	}
}
