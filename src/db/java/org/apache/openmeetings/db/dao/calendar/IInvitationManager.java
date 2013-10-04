package org.apache.openmeetings.db.dao.calendar;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;

public interface IInvitationManager {
	enum MessageType {
		Create
		, Update
		, Cancel
	}
	
	void processInvitation(Appointment a, MeetingMember member, MessageType type, String baseUrl);
	
	void processInvitation(Appointment a, MeetingMember mm, MessageType type, String baseUrl, boolean sendMail);
}
