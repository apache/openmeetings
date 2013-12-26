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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "appointments")
@NamedQueries({
    @NamedQuery(name="getAppointmentById", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.id = :id")
    , @NamedQuery(name="appointmentsInRange",
    	query="SELECT a FROM Appointment a "
			+ "WHERE a.deleted = false "
			+ "	AND ( "
			+ "		(a.start BETWEEN :starttime AND :endtime) "
			+ "		OR (a.end BETWEEN :starttime AND :endtime) "
			+ "		OR (a.start < :starttime AND a.end > :endtime) "
			+ "	)"
			+ "	AND a.owner.user_id = :userId"
    	)
    , @NamedQuery(name="joinedAppointmentsInRange",
		query="SELECT a FROM MeetingMember mm INNER JOIN mm.appointment a "
			+ "WHERE mm.deleted = false AND mm.user.user_id <> a.owner.user_id AND mm.user.user_id = :userId "
			+ "	AND a.id NOT IN (SELECT a.id FROM Appointment a WHERE a.owner.user_id = :userId)"
			+ "	AND mm.connectedEvent = false " //TODO review: isConnectedEvent is set for the MeetingMember if event is created from "Private Messages", it is weird
			+ "	AND ( "
			+ "		(a.start BETWEEN :starttime AND :endtime) "
			+ "		OR (a.end BETWEEN :starttime AND :endtime) "
			+ "		OR (a.start < :starttime AND a.end > :endtime) "
			+ "	)"
    	)
    , @NamedQuery(name="appointmentsInRangeRemind",
		query="SELECT a FROM MeetingMember mm INNER JOIN mm.appointment a "
			//only ReminderType simple mail is concerned!
			+ "WHERE mm.deleted = false AND a.deleted = false AND a.reminderEmailSend = false"
			+ " AND (a.remind.typId = 2 OR a.remind.typId = 3) "
			+ "	AND ( "
			+ "		(a.start BETWEEN :starttime AND :endtime) "
			+ "		OR (a.end BETWEEN :starttime AND :endtime) "
			+ "		OR (a.start < :starttime AND a.end > :endtime) "
			+ "	)"
    	)
    , @NamedQuery(name="getAppointmentByRoomId", query="SELECT a FROM Appointment a WHERE a.deleted = false AND a.room.rooms_id = :room_id")
	//TODO this query returns duplicates if the user books an appointment with
	//his own user as second meeting-member, swagner 19.02.2012
    , @NamedQuery(name="appointmentsInRangeByUser",
		query="SELECT a FROM MeetingMember mm, IN(mm.appointment) a "
			+ "WHERE mm.deleted = false AND mm.user.user_id <> a.owner.user_id AND mm.user.user_id = :userId "
			+ "	AND ( "
			+ "		(a.start BETWEEN :starttime AND :endtime) "
			+ "		OR (a.end BETWEEN :starttime AND :endtime) "
			+ "		OR (a.start < :starttime AND a.end > :endtime) "
			+ "	)"
	    )
    , @NamedQuery(name="appointedRoomsInRangeByUser",
		query="SELECT a.room FROM MeetingMember mm, IN(mm.appointment) a "
			+ "WHERE mm.deleted <> true AND mm.user.user_id <> a.owner.user_id AND mm.user.user_id = :userId "
			+ "	AND ( "
			+ "		(a.start BETWEEN :starttime AND :endtime) "
			+ "		OR (a.end BETWEEN :starttime AND :endtime) "
			+ "		OR (a.start < :starttime AND a.end > :endtime) "
			+ "	)"
	    )
})
@Root(name="appointment")
public class Appointment implements Serializable {
	private static final long serialVersionUID = 2016808778885761525L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "appointmentId", data = true)
	private Long id;
	
	@Column(name = "appointmentname")
	@Element(name="appointmentName", data=true, required=false)
	private String title;
	
	@Column(name = "location")
	@Element(name="appointmentLocation", data=true, required=false)
	private String location;
	
	@Column(name = "appointment_starttime")
	@Element(name="appointmentStarttime", data=true)
	private Date start;
	
	@Column(name = "appointment_endtime")
	@Element(name="appointmentEndtime", data=true)
	private Date end;
	
	@Lob 
	@Column(name = "description", length=2048)
	@Element(name="appointmentDescription", data=true, required=false)
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="categoryId", data=true, required=false)
	private AppointmentCategory category;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="users_id", data=true, required=false)
	private User owner;

	@Column(name = "starttime")
	@Element(name="inserted", data=true, required=false)
	private Date inserted;
	
	@Column(name = "updatetime")
	@Element(name="updated", data=true, required=false)
	private Date updated;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "remind_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="typId", data=true, required=false)
	private AppointmentReminderTyps remind;

	@Column(name = "isdaily")
	@Element(data=true, required = false)
	private Boolean isDaily;
	
	@Column(name = "isweekly")
	@Element(data=true, required = false)
	private Boolean isWeekly;
	
	@Column(name = "ismonthly")
	@Element(data=true, required = false)
	private Boolean isMonthly;
	
	@Column(name = "isyearly")
	@Element(data=true, required = false)
	private Boolean isYearly;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="room_id", data=true, required=false)
	private Room room;

	@Column(name = "icalId")
	@Element(data=true, required=false)
	private String icalId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "appointment_id")
	private List<MeetingMember> meetingMembers;
	
	@Column(name = "language_id")
	@Element(name="language_id", data=true, required=false)
	private Long languageId;
	
	@Column(name = "is_password_protected")
	@Element(name="isPasswordProtected", data=true, required=false)
	private boolean passwordProtected;
	
	@Column(name = "password")
	@Element(data=true, required=false)
	private String password;

	@Column(name = "is_connected_event")
	private boolean connectedEvent;

	@Column(name = "is_reminder_email_send")
	private boolean reminderEmailSend;

	public Long getId() {
		return id;
	}

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

	public AppointmentCategory getCategory() {
		return category;
	}

	public void setCategory(AppointmentCategory category) {
		this.category = category;
	}

	public AppointmentReminderTyps getRemind() {
		return remind;
	}

	public void setRemind(AppointmentReminderTyps remind) {
		this.remind = remind;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", title=" + title + ", start=" + start + ", end=" + end + ", owner=" + owner
				+ ", deleted=" + deleted + ", icalId=" + icalId + "]";
	}
	
}
