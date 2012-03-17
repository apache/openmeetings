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
package org.openmeetings.test.domain;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAddGroup extends AbstractOpenmeetingsSpringTest {

	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private UsersDaoImpl usersDao;

	private static final Logger log = Logger.getLogger(TestAddGroup.class);

	@Test
	public void testAddingGroup() {

		long organisation_id = organisationmanagement.addOrganisation(
				"default", 1);

		log.error("new organisation: " + organisation_id);

		long organisation_usersid = organisationmanagement
				.addUserToOrganisation(new Long(1), organisation_id,
						new Long(1));

		log.error("new organisation_user: " + organisation_usersid);

		Users us = usersDao.getUser(new Long(1));

		log.error(us.getLastname());
		log.error(us.getAdresses().getTown());

		/*
		 * for (Iterator it = us.getAdresses().getEmails().iterator();
		 * it.hasNext();){ Adresses_Emails addrMails = (Adresses_Emails)
		 * it.next(); log.error(addrMails.getMail().getEmail()); }
		 * log.error("size of domains: "+us.getOrganisation_users().size()); for
		 * (Iterator it2 = us.getOrganisation_users().iterator();
		 * it2.hasNext();){ Organisation_Users orgUsers = (Organisation_Users)
		 * it2.next(); log.error(orgUsers.getOrganisation().getName()); }
		 */
	}

}
