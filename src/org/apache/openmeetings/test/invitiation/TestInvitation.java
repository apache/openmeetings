package org.apache.openmeetings.test.invitiation;

import java.util.Date;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.InvitationService;
import org.apache.openmeetings.remote.MainService;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestInvitation extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = LoggerFactory.getLogger(TestInvitation.class);
	
	@Autowired
	private InvitationService invitationService;
	@Autowired
	private MainService mService;
	@Autowired
	private UserManager userManager;
	
	@Test
	public void testSendInvitationLink() {
		try {
			Sessiondata sessionData = mService.getsessiondata();
			
			User us = (User) userManager.loginUser(sessionData.getSession_id(), username, userpass, null, null, false);
			
			invitationService.sendInvitationHash(sessionData.getSession_id(), username, "message", "baseurl", "sebawagner@apache.org", 
					"subject", 1L, "", false, "", 1, new Date(), "12:00", new Date(), "14:00", 1L, us.getTimeZoneId(), true);
			
		} catch (Exception err) {
			log.error("[testSendInvitationLink]", err);
		}
	}

}
