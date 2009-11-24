package org.openmeetings.app.hibernate.beans.calendar;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.invitation.Invitations;
import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="meeting_members"
 * lazy="false"
 *
 */

public class MeetingMember {
	
	private Long meetingMemberId;
	private Users userid;
	private Appointment appointment;
	private String firstname;
	private String lastname;
	private String memberStatus; // internal, external.
	private String appointmentStatus; //status of the appointment denial, acceptance, wait. 
	private String email;
			
	private Date starttime;
	private Date updatetime;
	private Boolean deleted;
	private String comment;
	private Boolean invitor;
	
	
	private Invitations invitation;
	
	/**
     * 
     * @hibernate.id
     *  column="meeting_member_id"
     *  generator-class="increment"
     */  
	public Long getMeetingMemberId() {
		return meetingMemberId;
	}
	public void setMeetingMemberId(Long groupMemberId) {
		this.meetingMemberId = groupMemberId;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="user_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getUserid() {
		return userid;
	}
	public void setUserid(Users userid) {
		this.userid = userid;
	}
	
	/**
     * @hibernate.property
     *  column="email"
     *  type="string"
     */ 
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
     * @hibernate.property
     *  column="firstname"
     *  type="string"
     */ 
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	/**
     * @hibernate.property
     *  column="lastname"
     *  type="string"
     */ 
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	/**
     * @hibernate.property
     *  column="member_status"
     *  type="string"
     */ 
	public String getMemberStatus() {
		return memberStatus;
	}
	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}
	
	/**
     * @hibernate.property
     *  column="appointment_status"
     *  type="string"
     */ 
   public String getAppointmentStatus() {
		return appointmentStatus;
	}
	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}
	
	
    /**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="appointment_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.calendar.Appointment"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Appointment getAppointment() {
		return appointment;
	}
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	
	
	/**
	 * 
	 * @hibernate.many-to-one
	 * cascade="none"
	 * column="invitation"
	 * lazy="false"
	 * class="org.openmeetings.app.hibernate.beans.invitation.Invitations"
	 * not-null="false"
	 * outer-join="true"
	 */
	public Invitations getInvitation() {
		return invitation;
	}
	public void setInvitation(Invitations invitation) {
		this.invitation = invitation;
	}
	/**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */ 
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	
	/**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */ 
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	/**
     * @hibernate.property
     *  column="deleted"
     *  type="boolean"
     */ 
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	/**
     * @hibernate.property
*  column="comment_field"
     *  type="string"
     */ 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
     * @hibernate.property
     *  column="invitor"
     *  type="boolean"
     */ 
	public Boolean getInvitor() {
		return invitor;
	}
	public void setInvitor(Boolean invitor) {
		this.invitor = invitor;
	}
	
	
}
