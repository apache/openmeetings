package org.openmeetings.app.hibernate.beans.invitation;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="invitations"
 * lazy="false"
 *
 */
public class Invitations {

	private Long invitations_id;
	
	private Users invitedBy;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	private Rooms room;
	
	//the hash for the link
	private String hash;
	private String invitedname;
	private String invitedEMail;
	private Boolean isPasswordProtected;
	private String invitationpass;
	
	//this is necessary as a room can be shared between multiple domains
	//is eithter *public* or an organisation_ID
	private String conferencedomain;
	
	//Invitations by Time are only valid between the validFrom validTo TimeStamp
	private Boolean isValidByTime = false;
	private Date validFrom;
	private Date validTo;
	//An invitation which is canBeUsedOnlyOneTime = true can be only used one-time
	private Boolean canBeUsedOnlyOneTime = false;
	private Boolean invitationWasUsed = false;
	
	private Long appointmentId;
	
	// BaseuRL defined at creation of InvitationLink - used for chronological reminders on serverside (updates)
	private String baseUrl;
	
	public Invitations() {
		super();
		//	TODO Auto-generated constructor stub
	}

	
	/**
	 *
	 * @hibernate.id
	 *  column="invitations_id"
	 *  generator-class="increment"
	 */
	public Long getInvitations_id() {
		return invitations_id;
	}
	public void setInvitations_id(Long invitations_id) {
		this.invitations_id = invitations_id;
	}

	/**
	 * @hibernate.many-to-one
	 *  cascade="none"
	 *  column="roomid"
	 *  lazy="false"
	 *  class="org.openmeetings.app.hibernate.beans.rooms.Rooms"
	 *  not-null="false"
	 *  outer-join="true"
	 */
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}


	/**
	 * @hibernate.many-to-one
	 *  cascade="none"
	 *  column="invitedBy"
	 *  lazy="false"
	 *  class="org.openmeetings.app.hibernate.beans.user.Users"
	 *  not-null="false"
	 *  outer-join="true"
	 */
	public Users getInvitedBy() {
		return invitedBy;
	}
	public void setInvitedBy(Users invitedBy) {
		this.invitedBy = invitedBy;
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
     *  type="string"
     */  
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}


    /**
     * @hibernate.property
     *  column="hash"
     *  type="string"
     */ 
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

    /**
     * @hibernate.property
     *  column="invitedname"
     *  type="string"
     */ 
	public String getInvitedname() {
		return invitedname;
	}
	public void setInvitedname(String invitedname) {
		this.invitedname = invitedname;
	}

    /**
     * @hibernate.property
     *  column="invitedEMail"
     *  type="string"
     */ 
	public String getInvitedEMail() {
		return invitedEMail;
	}
	public void setInvitedEMail(String invitedEMail) {
		this.invitedEMail = invitedEMail;
	}
	
    /**
     * @hibernate.property
     *  column="ispasswordprotected"
     *  type="boolean"
     */ 
    public Boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}
	public void setIsPasswordProtected(Boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}


	/**
     * @hibernate.property
     *  column="invitationpass"
     *  type="string"
     */ 
	public String getInvitationpass() {
		return invitationpass;
	}
	public void setInvitationpass(String invitationpass) {
		this.invitationpass = invitationpass;
	}

	
	/**
     * @hibernate.property
     *  column="conferencedomain"
     *  type="string"
     */ 
    public String getConferencedomain() {
		return conferencedomain;
	}
	public void setConferencedomain(String conferencedomain) {
		this.conferencedomain = conferencedomain;
	}


	/**
     * @hibernate.property
     *  column="validFrom"
     *  type="java.util.Date"
     */
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

    /**
     * @hibernate.property
     *  column="validTo"
     *  type="java.util.Date"
     */
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	
    /**
     * @hibernate.property
     *  column="canBeUsedOnlyOneTime"
     *  type="boolean"
     */	
	public Boolean getCanBeUsedOnlyOneTime() {
		return canBeUsedOnlyOneTime;
	}
	public void setCanBeUsedOnlyOneTime(Boolean canBeUsedOnlyOneTime) {
		this.canBeUsedOnlyOneTime = canBeUsedOnlyOneTime;
	}
	
    /**
     * @hibernate.property
     *  column="invitationWasUsed"
     *  type="boolean"
     */	
	public Boolean getInvitationWasUsed() {
		return invitationWasUsed;
	}
	public void setInvitationWasUsed(Boolean invitationWasUsed) {
		this.invitationWasUsed = invitationWasUsed;
	}
	
    /**
     * @hibernate.property
     *  column="isValidByTime"
     *  type="boolean"
     */	
	public Boolean getIsValidByTime() {
		return isValidByTime;
	}
	public void setIsValidByTime(Boolean isValidByTime) {
		this.isValidByTime = isValidByTime;
	}

	/**
     * @hibernate.property
     *  column="baseUrl"
     *  type="string"
     */	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
     * @hibernate.property
     *  column="appointment_id"
     *  type="long"
     */
	public Long getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

}
