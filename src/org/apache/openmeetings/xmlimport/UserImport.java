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
package org.apache.openmeetings.xmlimport;

import java.io.InputStream;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.user.Emailmanagement;
import org.apache.openmeetings.data.user.Organisationmanagement;
import org.apache.openmeetings.data.user.Statemanagement;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class UserImport {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserImport.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private Emailmanagement emailManagement;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private Statemanagement statemanagement;
	@Autowired
	private BackupImportController backupController;

	public Long addUsersByDocument(InputStream is) throws Exception {
		for(Users us : backupController.readUserList(is, "users")) {
			
			boolean mailCheck = true;

			if (!emailManagement.checkUserEMail(us.getAdresses().getEmail())) {
				mailCheck = false;
				log.info("mailCheck = " + mailCheck);
			}

			// check for duplicate Login or mail:
			if (usersDao.checkUserLogin(us.getLogin()) && mailCheck) {
				userManagement.addUser(us);
			}
		}
		return null;
	}
}
