package org.openmeetings.app.persistence.beans.calendar;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;

@Entity
@Table(name = "appointments")
public class Appointment implements Serializable {

	private static final long serialVersionUID = 2016808778885761525L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "appointment_id")
	private Long appointmentId;
	@Column(name = "appointmentname")
	private String appointmentName;
	@Column(name = "location")
	private String appointmentLocation;
	@Column(name = "appointment_starttime")
	private Date appointmentStarttime;
	@Column(name = "appointment_endtime")
	private Date appointmentEndtime;
	@Column(name = "description")
	private String appointmentDescription;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = true)
	private AppointmentCategory appointmentCategory;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	private Users userId;

	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;
	@Column(name = "comment_field")
	private String comment;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "remind_id", nullable = true)
	private AppointmentReminderTyps remind;

	@Column(name = "isdaily")
	private Boolean isDaily;
	@Column(name = "isweekly")
	private Boolean isWeekly;
	@Column(name = "ismonthly")
	private Boolean isMonthly;
	@Column(name = "isyearly")
	private Boolean isYearly;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	private Rooms room;

	@Column(name = "icalId")
	private String icalId;

	@Transient
	private List<MeetingMember> meetingMember;
	@Column(name = "language_id")
	private Long language_id;
	@Column(name = "is_password_protected")
	private Boolean isPasswordProtected;
	@Column(name = "password")
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

	public Users getUserId() {
		return userId;
	}

	public void setUserId(Users userId) {
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

	public void setAppointmentStarttime(Date appointmentStarttime) {
		this.appointmentStarttime = appointmentStarttime;
	}

	public Date getAppointmentEndtime() {
		return appointmentEndtime;
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

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
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
