/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.test.invitiation;

import java.util.Date;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.remote.InvitationService;
import org.apache.openmeetings.remote.MainService;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestInvitation extends AbstractJUnitDefaults {

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
