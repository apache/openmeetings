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
package org.apache.openmeetings.persistence.beans.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "appointments")
@NamedQueries({
    @NamedQuery(name="appointmentsInRange",
        	query="SELECT a FROM Appointment a "
    			+ "WHERE a.deleted <> :deleted "
    			+ "	AND ( "
    			+ "		(a.appointmentStarttime BETWEEN :starttime AND :endtime) "
    			+ "		OR (a.appointmentEndtime BETWEEN :starttime AND :endtime) "
    			+ "		OR (a.appointmentStarttime < :starttime AND a.appointmentEndtime > :endtime) "
    			+ "	)"
    			+ "	AND a.userId.user_id = :userId"
    	)
    , @NamedQuery(name="joinedAppointmentsInRange",
    	query="SELECT a FROM MeetingMember mm, IN(mm.appointment) a "
			+ "WHERE mm.deleted <> true AND mm.invitor <> true AND mm.userid.user_id = :userId "
			+ "	AND a.appointmentId NOT IN (SELECT a.appointmentId FROM Appointment a WHERE a.userId.user_id = :userId)"
			+ "	AND mm.isConnectedEvent <> true " //TODO review: isConnectedEvent is set for the MeetingMember if event is created from "Private Messages", it is weird
			+ "	AND ( "
			+ "		(a.appointmentStarttime BETWEEN :starttime AND :endtime) "
			+ "		OR (a.appointmentEndtime BETWEEN :starttime AND :endtime) "
			+ "		OR (a.appointmentStarttime < :starttime AND a.appointmentEndtime > :endtime) "
			+ "	)"
	)
})
@Root(name="appointment")
public class Appointment implements Serializable {
	private static final long serialVersionUID = 2016808778885761525L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data=true)
	private Long appointmentId;
	
	@Column(name = "appointmentname")
	@Element(data=true, required=false)
	private String appointmentName;
	
	@Column(name = "location")
	@Element(data=true, required=false)
	private String appointmentLocation;
	
	@Column(name = "appointment_starttime")
	@Element(data=true)
	private Date appointmentStarttime;
	
	@Column(name = "appointment_endtime")
	@Element(data=true)
	private Date appointmentEndtime;
	
	@Lob 
	@Column(name = "description", length=2048)
	@Element(data=true, required=false)
	private String appointmentDescription;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="categoryId", data=true, required=false)
	private AppointmentCategory appointmentCategory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="users_id", data=true, required=false)
	private User userId;

	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "remind_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="typId", data=true, required=false)
	private AppointmentReminderTyps remind;

	@Column(name = "isdaily")
	@Element(data=true)
	private Boolean isDaily;
	
	@Column(name = "isweekly")
	@Element(data=true)
	private Boolean isWeekly;
	
	@Column(name = "ismonthly")
	@Element(data=true)
	private Boolean isMonthly;
	
	@Column(name = "isyearly")
	@Element(data=true)
	private Boolean isYearly;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name="room_id", data=true, required=false)
	private Room room;

	@Column(name = "icalId")
	@Element(data=true, required=false)
	private String icalId;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointment_id")
	private List<MeetingMember> meetingMember;
	
	@Column(name = "language_id")
	@Element(data=true, required=false)
	private Long language_id;
	
	@Column(name = "is_password_protected")
	@Element(data=true, required=false)
	private Boolean isPasswordProtected;
	
	@Column(name = "password")
	@Element(data=true, required=false)
	private String password;

	@Column(name = "is_connected_event")
	private Boolean isConnectedEvent;

	@Column(name = "is_reminder_email_send")
	private Boolean isReminderEmailSend = false; //default to false

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getAppointmentName() {
		return appointmentName;
	}

	public void setAppointmentName(String appointmentName) {
		this.appointmentName = appointmentName;
	}

	public String getAppointmentLocation() {
		return appointmentLocation;
	}

	public void setAppointmentLocation(String appointmentLocation) {
		this.appointmentLocation = appointmentLocation;
	}

	public Date getAppointmentStarttime() {
		return appointmentStarttime;
	}

	public Calendar appointmentStartAsCalendar(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(appointmentStarttime);
		return cal;
	}

	public void setAppointmentStarttime(Date appointmentStarttime) {
		this.appointmentStarttime = appointmentStarttime;
	}

	public Date getAppointmentEndtime() {
		return appointmentEndtime;
	}

	public Calendar appointmentEndAsCalendar(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(appointmentEndtime);
		return cal;
	}

	public void setAppointmentEndtime(Date appointmentEndtime) {
		this.appointmentEndtime = appointmentEndtime;
	}

	public String getAppointmentDescription() {
		return appointmentDescription;
	}

	public void setAppointmentDescription(String appointmentDescription) {
		this.appointmentDescription = appointmentDescription;
	}

	public AppointmentCategory getAppointmentCategory() {
		return appointmentCategory;
	}

	public void setAppointmentCategory(AppointmentCategory appointmentCategory) {
		this.appointmentCategory = appointmentCategory;
	}

	public AppointmentReminderTyps getRemind() {
		return remind;
	}

	public void setRemind(AppointmentReminderTyps remind) {
		this.remind = remind;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public boolean getDeleted() {
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

	public List<MeetingMember> getMeetingMember() {
		return meetingMember;
	}

	public void setMeetingMember(List<MeetingMember> meetingMember) {
		this.meetingMember = meetingMember;
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

	public Long getLanguage_id() {
		return language_id;
	}

	public void setLanguage_id(Long languageId) {
		language_id = languageId;
	}

	public Boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}

	public void setIsPasswordProtected(Boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsConnectedEvent() {
		return isConnectedEvent;
	}

	public void setIsConnectedEvent(Boolean isConnectedEvent) {
		this.isConnectedEvent = isConnectedEvent;
	}

	public Boolean getIsReminderEmailSend() {
		return isReminderEmailSend;
	}

	public void setIsReminderEmailSend(Boolean isReminderEmailSend) {
		this.isReminderEmailSend = isReminderEmailSend;
	}

}
