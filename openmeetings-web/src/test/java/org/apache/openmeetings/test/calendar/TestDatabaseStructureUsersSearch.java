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
package org.apache.openmeetings.test.calendar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.List;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureUsersSearch extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestDatabaseStructureUsersSearch.class, webAppRootKey);

	@Autowired
    private UserDao userDao;

	@Test
	public void testAddingGroup(){
		try {
			 List<User> users = userDao.get("first", 0, 10, "lastname", false, 1);
			 log.debug("[result]" + users.size());
			 log.debug("[records]"+ users);
			 
			 //GroupMemberDaoImpl.getInstance().addGroupMember("Ad", "dir", Calendar.getInstance().getTime(), "1", "2", "interpol155", 1L, 1L);	
			//GroupMemberDaoImpl.getInstance().addGroupMember(firstname, lastname, age, memberStatus, appointmentStatus, password, adresses_id, appointmentId);
			
			//GroupMemberDaoImpl.getInstance().getGroupMemberById(1L);
			//GroupMemberDaoImpl.getInstance().deleteGroupMember(2L);
			//GroupMemberDaoImpl.getInstance().updateGroupMember(1L, "Eugen", "Schwert", Calendar.getInstance().getTime(), "1", "2", "interpol155", 1L, 1L);
		} catch (Exception err) {
			log.error("[testAddingGroup]",err);
		}
	}
}
