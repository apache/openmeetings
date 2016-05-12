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
package org.apache.openmeetings.db.dao.room;

import java.util.Date;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;

public interface IInvitationManager {
	void processInvitation(Appointment a, MeetingMember member, MessageType type);
	
	void processInvitation(Appointment a, MeetingMember mm, MessageType type, boolean sendMail);
	
	Invitation getInvitation(Invitation _invitation, User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, Long languageId, Date gmtTimeStart, Date gmtTimeEnd
			, Appointment appointment);
	
	Invitation getInvitation(User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, Long languageId, Date gmtTimeStart, Date gmtTimeEnd
			, Appointment appointment);
	
	void sendInvitationLink(Invitation i, MessageType type, String subject, String message, boolean ical) throws Exception ;
	
	Object checkInvitationPass(String hashCode, String pass);
	
	Object getInvitationByHashCode(String hashCode, boolean hidePass);
	
	boolean sendInvitationReminderSMS(String phone, String subject, long languageId);
}
