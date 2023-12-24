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

import static org.apache.openmeetings.db.bind.Constants.APPOINTMENT_NODE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAppointmentPreStartMinutes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.AppointmentReminderAdapter;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.OmCalendarAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@Table(name = "appointment", indexes = {
		@Index(name = "title_idx", columnList = "title")
})
@NamedQuery(name="getAppointmentById", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.id = :id")
@NamedQuery(name="getAppointmentByIdAny", query="SELECT a FROM Appointment a WHERE a.id = :id")
@NamedQuery(name="getAppointments", query="SELECT a FROM Appointment a WHERE a.deleted = false ORDER BY a.id")
@NamedQuery(name="appointmentsInRange",
	query="SELECT a FROM Appointment a "
		+ "WHERE a.deleted = false "
		+ "  AND ( "
		+ "    (a.start BETWEEN :start AND :end) "
		+ "      OR (a.end BETWEEN :start AND :end) "
		+ "      OR (a.start < :start AND a.end > :end) "
		+ "    )"
		+ "  AND a.owner.id = :userId"
	)
@NamedQuery(name="joinedAppointmentsInRange",
	query="SELECT a FROM MeetingMember mm INNER JOIN mm.appointment a "
		+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
		+ "  AND a.id NOT IN (SELECT a.id FROM Appointment a WHERE a.owner.id = :userId)"
		+ "  AND mm.connectedEvent = false " //connectedEvent is set for the MeetingMember if event is created from "Private Messages", it is weird
		+ "  AND ( "
		+ "    (a.start BETWEEN :start AND :end) "
		+ "      OR (a.end BETWEEN :start AND :end) "
		+ "      OR (a.start < :start AND a.end > :end) "
		+ "  )"
	)
@NamedQuery(name="appointmentsInRangeRemind",
	query="SELECT a FROM Appointment a "
		//only ReminderType simple mail is concerned!
		+ "WHERE a.deleted = false AND a.reminderEmailSend = false"
		+ "  AND (a.reminder <> :none) "
		+ "  AND ( "
		+ "    (a.start BETWEEN :start AND :end) "
		+ "      OR (a.end BETWEEN :start AND :end) "
		+ "      OR (a.start < :start AND a.end > :end) "
		+ "  )"
	)
@NamedQuery(name="getAppointmentByRoomId", query="SELECT a FROM Appointment a WHERE a.room.id = :roomId")
@NamedQuery(name="getAppointmentByOwnerRoomId", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.owner.id = :userId AND a.room.id = :roomId")
//this query returns duplicates if the user books an appointment with his own user as second meeting-member, swagner 19.02.2012
@NamedQuery(name="appointmentsInRangeByUser",
	query="SELECT a FROM MeetingMember mm, IN(mm.appointment) a "
		+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
		+ "  AND ( "
		+ "    (a.start BETWEEN :start AND :end) "
		+ "      OR (a.end BETWEEN :start AND :end) "
		+ "      OR (a.start < :start AND a.end > :end) "
		+ "  )"
	)
@NamedQuery(name="appointedRoomsInRangeByUser",
	query="SELECT a.room FROM MeetingMember mm, IN(mm.appointment) a "
		+ "WHERE mm.deleted = false AND mm.user.id <> a.owner.id AND mm.user.id = :userId "
		+ "  AND ( "
		+ "    (a.start BETWEEN :start AND :end) "
		+ "      OR (a.end BETWEEN :start AND :end) "
		+ "      OR (a.start < :start AND a.end > :end) "
		+ "  )"
	)
@NamedQuery(name="getNextAppointment", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.start > :start AND a.owner.id = :userId")
@NamedQuery(name="getAppointmentsByTitle", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.title LIKE :title AND a.owner.id = :userId")

//Calendar Related Queries
@NamedQuery(name = "getAppointmentsbyCalendar",
	query = "SELECT a FROM Appointment a WHERE a.deleted = false AND a.calendar.id = :calId ORDER BY a.id")
@NamedQuery(name = "getHrefsforAppointmentsinCalendar",
	query = "SELECT a.href FROM Appointment a WHERE a.deleted = FALSE AND a.calendar.id = :calId ORDER BY a.id")
@NamedQuery(name = "deleteAppointmentsbyCalendar",
	query = "UPDATE Appointment a SET a.deleted = true WHERE a.calendar.id = :calId")
@XmlRootElement(name = APPOINTMENT_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Appointment extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final int REMINDER_NONE_ID = 1;
	public static final int REMINDER_EMAIL_ID = 2;
	public static final int REMINDER_ICAL_ID = 3;

	public enum Reminder {
		NONE(REMINDER_NONE_ID)
		, EMAIL(REMINDER_EMAIL_ID)
		, ICAL(REMINDER_ICAL_ID);

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
			Reminder r = Reminder.NONE;
			switch (type) {
				case REMINDER_EMAIL_ID:
					r = Reminder.EMAIL;
					break;
				case REMINDER_ICAL_ID:
					r = Reminder.ICAL;
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
	@XmlElement(name = "appointmentId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "title")
	@XmlElement(name = "appointmentName", required = false)
	private String title;

	@Column(name = "location")
	@XmlElement(name = "appointmentLocation", required = false)
	private String location;

	@Column(name = "app_start") // Oracle fails in case 'start' is used as column name
	@XmlElement(name = "appointmentStarttime")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date start;

	@Column(name = "app_end") // renamed to be in sync with 'app_start'
	@XmlElement(name = "appointmentEndtime")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date end;

	@Lob
	@Column(name = "description", length = 2048)
	@XmlElement(name = "appointmentDescription", required = false)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "users_id", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User owner;

	@Column(name = "reminder")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "typId", required = false)
	@XmlJavaTypeAdapter(AppointmentReminderAdapter.class)
	private Reminder reminder = Reminder.NONE;

	@Column(name = "isdaily")
	@XmlElement(name = "isDaily", required = false)
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean isDaily;

	@Column(name = "isweekly")
	@XmlElement(name = "isWeekly", required = false)
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean isWeekly;

	@Column(name = "ismonthly")
	@XmlElement(name = "isMonthly", required = false)
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean isMonthly;

	@Column(name = "isyearly")
	@XmlElement(name = "isYearly", required = false)
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean isYearly;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "room_id", required = false)
	@XmlJavaTypeAdapter(RoomAdapter.class)
	private Room room;

	@Column(name = "icalId")
	@XmlElement(name = "icalId", required = false)
	private String icalId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "appointment_id")
	@XmlTransient
	private List<MeetingMember> meetingMembers;

	@Column(name = "language_id")
	@XmlElement(name = "language_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long languageId;

	@Column(name = "is_password_protected", nullable = false)
	@XmlElement(name = "isPasswordProtected", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean passwordProtected;

	@Column(name = "password")
	@XmlElement(name = "icalId", required = false)
	private String password;

	@Column(name = "is_connected_event", nullable = false)
	@XmlElement(name = "connectedEvent", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean connectedEvent;

	@Column(name = "is_reminder_email_send", nullable = false)
	@XmlTransient
	private boolean reminderEmailSend;

	//Calendar Specific fields.
	@ManyToOne()
	@JoinColumn(name = "calendar_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "calendar_id", required = false)
	@XmlJavaTypeAdapter(OmCalendarAdapter.class)
	private OmCalendar calendar;

	@Column(name = "href")
	@XmlElement(name = "href", required = false)
	private String href;

	@Column(name = "etag")
	@XmlElement(name = "etag", required = false)
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

	public static Date allowedStart(Date start) {
		return new Date(start.getTime() - (getAppointmentPreStartMinutes() * 60 * 1000));
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

	public boolean isOwner(Long userId) {
		return owner.getId().equals(userId);
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", title=" + title + ", start=" + start + ", end=" + end + ", owner=" + owner
				+ ", deleted=" + isDeleted() + ", icalId=" + icalId + ", calendar=" + calendar + ", href=" + href + ", etag=" + etag + "]";
	}
}
