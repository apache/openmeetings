package org.openmeetings.app.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.persistence.beans.rooms.Rooms;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "private_messages")
public class PrivateMessages implements Serializable {
	
	private static final long serialVersionUID = 7541117437029707792L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="private_message_id")
	private long privateMessageId;
	@Column(name="subject")
	private String subject;
	@Lob
	@Column(name="message")
	private String message;
	
	@Column(name="inserted")
	private Date inserted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="from_id")
	private Users from;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="to_id")
	private Users to;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_id")
	private Users owner;
	
		
	@Column(name="booked_room")
	private Boolean bookedRoom;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="rooms_id")
	private Rooms room;
	
	@Column(name="is_read")
	private Boolean isRead;
	
	@Column(name="is_trash")
	private Boolean isTrash;
	
	@Column(name="parent_message_id")
	private Long parentMessage;
	@Column(name="private_message_folder_id")
	private Long privateMessageFolderId;
	
	@Column(name="is_contact_request")
	private Boolean isContactRequest;
	@Column(name="user_contact_id")
	private Long userContactId;

	public long getPrivateMessageId() {
		return privateMessageId;
	}
	public void setPrivateMessageId(long privateMessageId) {
		this.privateMessageId = privateMessageId;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public Users getFrom() {
		return from;
	}
	public void setFrom(Users from) {
		this.from = from;
	}

	public Users getTo() {
		return to;
	}
	public void setTo(Users to) {
		this.to = to;
	}
	
	public Users getOwner() {
		return owner;
	}
	public void setOwner(Users owner) {
		this.owner = owner;
	}
	
	public Boolean getBookedRoom() {
		return bookedRoom;
	}
	public void setBookedRoom(Boolean bookedRoom) {
		this.bookedRoom = bookedRoom;
	}

	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}

	public Long getParentMessage() {
		return parentMessage;
	}
	public void setParentMessage(Long parentMessage) {
		this.parentMessage = parentMessage;
	}
	
	public Boolean getIsTrash() {
		return isTrash;
	}
	public void setIsTrash(Boolean isTrash) {
		this.isTrash = isTrash;
	}
	
	public Long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(Long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
	}
	
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	public Boolean getIsContactRequest() {
		return isContactRequest;
	}
	public void setIsContactRequest(Boolean isContactRequest) {
		this.isContactRequest = isContactRequest;
	}
	
	public Long getUserContactId() {
		return userContactId;
	}
	public void setUserContactId(Long userContactId) {
		this.userContactId = userContactId;
	}
	
}
