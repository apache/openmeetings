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
package org.apache.openmeetings.db.entity.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "appointment")
@NamedQueries({
	@NamedQuery(name="getAppointmentById", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.id = :id")
	, @NamedQuery(name="getAppointmentByIdAny", query="SELECT a FROM Appointment a WHERE a.id = :id")
	, @NamedQuery(name="getAppointments", query="SELECT a FROM Appointment a WHERE a.deleted = false ORDER BY a.id")
	, @NamedQuery(name="appointmentsInRange",
		query="SELECT a FROM Appointment a "
			+ "WHERE a.deleted = false "
			+ "	AND ( "
			+ "		(a.start BETWEEN :start AND :end) "
			+ "		OR (a.end BETWEEN :start AND :end) "
			+ "		OR (a.start < :start AND a.end > :end) "
			+ "	)"
			+ "	AND a.owner.id = :userId"
		)
	, @NamedQuery(name="joinedAppointmentsInRange",
		query="SELECT a FROM MeetingMember mm INNER JOIN mm.appointment a "
			+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
			+ "	AND a.id NOT IN (SELECT a.id FROM Appointment a WHERE a.owner.id = :userId)"
			+ "	AND mm.connectedEvent = false " //connectedEvent is set for the MeetingMember if event is created from "Private Messages", it is weird
			+ "	AND ( "
			+ "		(a.start BETWEEN :start AND :end) "
			+ "		OR (a.end BETWEEN :start AND :end) "
			+ "		OR (a.start < :start AND a.end > :end) "
			+ "	)"
		)
	, @NamedQuery(name="appointmentsInRangeRemind",
		query="SELECT a FROM Appointment a "
			//only ReminderType simple mail is concerned!
			+ "WHERE a.deleted = false AND a.reminderEmailSend = false"
			+ " AND (a.reminder <> :none) "
			+ "	AND ( "
			+ "		(a.start BETWEEN :start AND :end) "
			+ "		OR (a.end BETWEEN :start AND :end) "
			+ "		OR (a.start < :start AND a.end > :end) "
			+ "	)"
		)
	, @NamedQuery(name="getAppointmentByRoomId", query="SELECT a FROM Appointment a WHERE a.room.id = :roomId")
	, @NamedQuery(name="getAppointmentByOwnerRoomId", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.owner.id = :userId AND a.room.id = :roomId")
	//this query returns duplicates if the user books an appointment with his own user as second meeting-member, swagner 19.02.2012
	, @NamedQuery(name="appointmentsInRangeByUser",
		query="SELECT a FROM MeetingMember mm, IN(mm.appointment) a "
			+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
			+ "	AND ( "
			+ "		(a.start BETWEEN :start AND :end) "
			+ "		OR (a.end BETWEEN :start AND :end) "
			+ "		OR (a.start < :start AND a.end > :end) "
			+ "	)"
		)
	, @NamedQuery(name="appointedRoomsInRangeByUser",
		query="SELECT a.room FROM MeetingMember mm, IN(mm.appointment) a "
			+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
			+ "	AND ( "
			+ "		(a.start BETWEEN :start AND :end) "
			+ "		OR (a.end BETWEEN :start AND :end) "
			+ "		OR (a.start < :start AND a.end > :end) "
			+ "	)"
		)
	, @NamedQuery(name="getNextAppointment", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.start > :start AND a.owner.id = :userId")
	, @NamedQuery(name="getAppointmentsByTitle", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.title LIKE :title AND a.owner.id = :userId")

	//Calendar Related Queries
	, @NamedQuery(name = "getAppointmentsbyCalendar",
		query = "SELECT a FROM Appointment a WHERE a.deleted = false AND a.calendar.id = :calId ORDER BY a.id")
	, @NamedQuery(name = "getHrefsforAppointmentsinCalendar",
		query = "SELECT a.href FROM Appointment a WHERE a.deleted = FALSE AND a.calendar.id = :calId ORDER BY a.id")
	, @NamedQuery(name = "deleteAppointmentsbyCalendar",
		query = "UPDATE Appointment a SET a.deleted = true WHERE a.calendar.id = :calId")
})
@Root(name = "appointment")
public class Appointment extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final int REMINDER_NONE_ID = 1;
	public static final int REMINDER_EMAIL_ID = 2;
	public static final int REMINDER_ICAL_ID = 3;

	public enum Reminder {
		none(REMINDER_NONE_ID), email(REMINDER_EMAIL_ID), ical(REMINDER_ICAL_ID);

		private int id;

		Reminder(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Reminder get(Long type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Reminder get(Integer type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Reminder get(int type) {
			Reminder r = Reminder.none;
			switch (type) {
				case REMINDER_EMAIL_ID:
					r = Reminder.email;
					break;
				case REMINDER_ICAL_ID:
					r = Reminder.ical;
					break;
				default:
					// no-op
			}
			return r;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "appointmentId", data = true)
	private Long id;

	@Column(name = "title")
	@Element(name = "appointmentName", data = true, required = false)
	private String title;

	@Column(name = "location")
	@Element(name = "appointmentLocation", data = true, required = false)
	private String location;

	@Column(name = "app_start") // Oracle fails in case 'start' is used as column name
	@Element(name = "appointmentStarttime", data = true)
	private Date start;

	@Column(name = "app_end") // renamed to be in sync with 'app_start'
	@Element(name = "appointmentEndtime", data = true)
	private Date end;

	@Lob
	@Column(name = "description", length = 2048)
	@Element(name = "appointmentDescription", data = true, required = false)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name = "users_id", data = true, required = false)
	private User owner;

	@Column(name = "reminder")
	@Enumerated(EnumType.STRING)
	@Element(name = "typId", data = true, required = false)
	private Reminder reminder = Reminder.none;

	@Column(name = "isdaily")
	@Element(data = true, required = false)
	private Boolean isDaily;

	@Column(name = "isweekly")
	@Element(data = true, required = false)
	private Boolean isWeekly;

	@Column(name = "ismonthly")
	@Element(data = true, required = false)
	private Boolean isMonthly;

	@Column(name = "isyearly")
	@Element(data = true, required = false)
	private Boolean isYearly;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name = "room_id", data = true, required = false)
	private Room room;

	@Column(name = "icalId")
	@Element(data = true, required = false)
	private String icalId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "appointment_id")
	private List<MeetingMember> meetingMembers;

	@Column(name = "language_id")
	@Element(name = "language_id", data = true, required = false)
	private Long languageId;

	@Column(name = "is_password_protected", nullable = false)
	@Element(name = "isPasswordProtected", data = true, required = false)
	private boolean passwordProtected;

	@Column(name = "password")
	@Element(data = true, required = false)
	private String password;

	@Column(name = "is_connected_event", nullable = false)
	private boolean connectedEvent;

	@Column(name = "is_reminder_email_send", nullable = false)
	private boolean reminderEmailSend;

	//Calendar Specific fields.
	@ManyToOne()
	@JoinColumn(name = "calendar_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name = "calendar_id", data = true, required = false)
	private OmCalendar calendar;

	@Column(name = "href")
	@Element(data = true, required = false)
	private String href;

	@Column(name = "etag")
	@Element(data = true, required = false)
	private String etag;


	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStart() {
		return start;
	}

	public Calendar startCalendar(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(start);
		return cal;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public Calendar endCalendar(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(end);
		return cal;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Reminder getReminder() {
		return reminder;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}

	public Boolean getIsWeekly() {
		return isWeekly;
	}

	public void setIsWeekly(Boolean isWeekly) {
		this.isWeekly = isWeekly;
	}

	public Boolean getIsMonthly() {
		return isMonthly;
	}

	public void setIsMonthly(Boolean isMonthly) {
		this.isMonthly = isMonthly;
	}

	public Boolean getIsYearly() {
		return isYearly;
	}

	public void setIsYearly(Boolean isYearly) {
		this.isYearly = isYearly;
	}

	public Boolean getIsDaily() {
		return isDaily;
	}

	public void setIsDaily(Boolean isDaily) {
		this.isDaily = isDaily;
	}

	public List<MeetingMember> getMeetingMembers() {
		return meetingMembers;
	}

	public void setMeetingMembers(List<MeetingMember> meetingMembers) {
		this.meetingMembers = meetingMembers;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getIcalId() {
		return icalId;
	}

	public void setIcalId(String icalId) {
		this.icalId = icalId;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(boolean isPasswordProtected) {
		this.passwordProtected = isPasswordProtected;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isConnectedEvent() {
		return connectedEvent;
	}

	public void setConnectedEvent(boolean isConnectedEvent) {
		this.connectedEvent = isConnectedEvent;
	}

	public boolean isReminderEmailSend() {
		return reminderEmailSend;
	}

	public void setReminderEmailSend(boolean isReminderEmailSend) {
		this.reminderEmailSend = isReminderEmailSend;
	}

	public OmCalendar getCalendar() {
		return calendar;
	}

	public void setCalendar(OmCalendar calendar) {
		this.calendar = calendar;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", title=" + title + ", start=" + start + ", end=" + end + ", owner=" + owner
				+ ", deleted=" + isDeleted() + ", icalId=" + icalId + ", calendar=" + calendar + ", href=" + href + ", etag=" + etag + "]";
	}
}
