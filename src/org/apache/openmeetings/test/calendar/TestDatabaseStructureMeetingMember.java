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
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestDatabaseStructureMeetingMember extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureMeetingMember.class);
	@Autowired
	private MeetingMemberDao meetingMemberDao;

	@Test
	public void testAddingGroup(){

		try {
			
			
			//MeetingMemberDaoImpl.getInstance().addMeetingMember("Adddd", "dir", "1", "2", 1L, 1L, "test");	
			//MeetingMemberDaoImpl.getInstance().addMeetingMember(firstname, lastname, memberStatus, appointmentStatus, appointmentId, userid, email)
			
			
			//MeetingMemberDaoImpl.getInstance().getMeetingMemberById(1L);
			//MeetingMemberDaoImpl.getInstance().deleteMeetingMember(2L);
			meetingMemberDao.updateMeetingMember(1l,"bbbbbb", "dir", "1", "2", 1L, 1L, "test", ""); 
		} catch (Exception err) {

			log.error("[testAddingMeeting]",err);

		}

		

		

	}



}

