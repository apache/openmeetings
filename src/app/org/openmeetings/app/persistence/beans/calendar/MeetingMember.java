package org.openmeetings.app.persistence.beans.calendar;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.invitation.Invitations;
import org.openmeetings.app.persistence.beans.user.Users;


@Entity
@Table(name = "meeting_members")
public class MeetingMember implements Serializable {
	
	private static final long serialVersionUID = -3864571325368787524L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="meeting_member_id")
	private Long meetingMemberId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", nullable=true)
	private Users userid;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="appointment_id", nullable=true)
	private Appointment appointment;
	@Column(name="firstname")
	private String firstname;
	@Column(name="lastname")
	private String lastname;
	@Column(name="member_status")
	private String memberStatus; // internal, external.
	@Column(name="appointment_status")
	private String appointmentStatus; //status of the appointment denial, acceptance, wait. 
	@Column(name="email")
	private String email;
			
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private Boolean deleted;
	@Column(name="comment_field")
	private String comment;
	@Column(name="invitor")
	private Boolean invitor;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="invitation", nullable=true, insertable=false)
	private Invitations invitation;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="omtimezoneId", nullable=true, insertable=true)
	private OmTimeZone omTimeZone;
	
	@Column(name="is_connected_event")
	private Boolean isConnectedEvent;
	
	public Long getMeetingMemberId() {
		return meetingMemberId;
	}
	public void setMeetingMemberId(Long groupMemberId) {
		this.meetingMemberId = groupMemberId;
	}
	
	public Users getUserid() {
		return userid;
	}
	public void setUserid(Users userid) {
		this.userid = userid;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getMemberStatus() {
		return memberStatus;
	}
	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}
	
   public String getAppointmentStatus() {
		return appointmentStatus;
	}
	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}
	
	
	public Appointment getAppointment() {
		return appointment;
	}
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	
	
	public Invitations getInvitation() {
		return invitation;
	}
	public void setInvitation(Invitations invitation) {
		this.invitation = invitation;
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
	
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Boolean getInvitor() {
		return invitor;
	}
	public void setInvitor(Boolean invitor) {
		this.invitor = invitor;
	}

	public OmTimeZone getOmTimeZone() {
		return omTimeZone;
	}
	public void setOmTimeZone(OmTimeZone omTimeZone) {
		this.omTimeZone = omTimeZone;
	}
	
	public Boolean getIsConnectedEvent() {
		return isConnectedEvent;
	}
	public void setIsConnectedEvent(Boolean isConnectedEvent) {
		this.isConnectedEvent = isConnectedEvent;
	}
	
	
}
