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

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestDatabaseStructureUsersSearch extends AbstractOpenmeetingsSpringTest {
    @Autowired
    private UserManager userManager;

	private static final Logger log = Logger.getLogger(TestDatabaseStructureUsersSearch.class);

	@Test
	public void testAddingGroup(){

		try {
			
			 SearchResult<User> users = userManager.getAllUserByRange("first", 0, 10, "orderby", true);
			 log.debug("[result]" + users.getResult().size());
			 log.debug("[records]"+ users.getRecords());
			 
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

