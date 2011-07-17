package org.openmeetings.app.hibernate.beans.invitation;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "invitations")
public class Invitations implements Serializable {

	private static final long serialVersionUID = 1153321347974705506L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invitations_id")
	private Long invitations_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invitedBy", nullable = true)
	private Users invitedBy;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomid", nullable = true)
	private Rooms room;

	// the hash for the link
	@Column(name = "hash")
	private String hash;
	@Column(name = "invitedname")
	private String invitedname;
	@Column(name = "invitedEMail")
	private String invitedEMail;
	@Column(name = "ispasswordprotected")
	private Boolean isPasswordProtected;
	@Column(name = "invitationpass")
	private String invitationpass;

	// this is necessary as a room can be shared between multiple domains
	// is eithter *public* or an organisation_ID
	@Column(name = "conferencedomain")
	private String conferencedomain;

	// Invitations by Time are only valid between the validFrom validTo
	// TimeStamp
	@Column(name = "isValidByTime")
	private Boolean isValidByTime = false;
	@Column(name = "validFrom")
	private Date validFrom;
	@Column(name = "validTo")
	private Date validTo;
	// An invitation which is canBeUsedOnlyOneTime = true can be only used
	// one-time
	@Column(name = "canBeUsedOnlyOneTime")
	private Boolean canBeUsedOnlyOneTime = false;
	@Column(name = "invitationWasUsed")
	private Boolean invitationWasUsed = false;

	@Column(name = "appointment_id")
	private Long appointmentId;

	// BaseuRL defined at creation of InvitationLink - used for chronological
	// reminders on serverside (updates)
	@Column(name = "baseUrl")
	private String baseUrl;

	public Invitations() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getInvitations_id() {
		return invitations_id;
	}

	public void setInvitations_id(Long invitations_id) {
		this.invitations_id = invitations_id;
	}

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	public Users getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(Users invitedBy) {
		this.invitedBy = invitedBy;
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getInvitedname() {
		return invitedname;
	}

	public void setInvitedname(String invitedname) {
		this.invitedname = invitedname;
	}

	public String getInvitedEMail() {
		return invitedEMail;
	}

	public void setInvitedEMail(String invitedEMail) {
		this.invitedEMail = invitedEMail;
	}

	public Boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}

	public void setIsPasswordProtected(Boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}

	public String getInvitationpass() {
		return invitationpass;
	}

	public void setInvitationpass(String invitationpass) {
		this.invitationpass = invitationpass;
	}

	public String getConferencedomain() {
		return conferencedomain;
	}

	public void setConferencedomain(String conferencedomain) {
		this.conferencedomain = conferencedomain;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Boolean getCanBeUsedOnlyOneTime() {
		return canBeUsedOnlyOneTime;
	}

	public void setCanBeUsedOnlyOneTime(Boolean canBeUsedOnlyOneTime) {
		this.canBeUsedOnlyOneTime = canBeUsedOnlyOneTime;
	}

	public Boolean getInvitationWasUsed() {
		return invitationWasUsed;
	}

	public void setInvitationWasUsed(Boolean invitationWasUsed) {
		this.invitationWasUsed = invitationWasUsed;
	}

	public Boolean getIsValidByTime() {
		return isValidByTime;
	}

	public void setIsValidByTime(Boolean isValidByTime) {
		this.isValidByTime = isValidByTime;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

}
