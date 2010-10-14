package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;

/**
 * 
 * @hibernate.class table="private_messages"
 * lazy="false"
 *
 */
public class PrivateMessages {
	
	public long privateMessageId;
	public String subject;
	public String message;
	
	public Date inserted;
	public Users from;
	public Users to;
	private Users owner;
	
	public Boolean bookedRoom;
	public Rooms room;
	
	public Boolean isRead;
	
	private Boolean isTrash;
	
	public Long parentMessage;
	public Long privateMessageFolderId;

	/**
     * 
     * @hibernate.id
     *  column="private_message_id"
     *  generator-class="increment"
     */
	public long getPrivateMessageId() {
		return privateMessageId;
	}
	public void setPrivateMessageId(long privateMessageId) {
		this.privateMessageId = privateMessageId;
	}

	/**
     * @hibernate.property
     *  column="subject"
     *  type="string"
     */ 
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
     * @hibernate.property
     *  column="message"
     *  type="text"
     */ 
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
     * @hibernate.property
     *  column="inserted"
     *  type="java.util.Date"
     */
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="from_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getFrom() {
		return from;
	}
	public void setFrom(Users from) {
		this.from = from;
	}

	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="to_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getTo() {
		return to;
	}
	public void setTo(Users to) {
		this.to = to;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="owner_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */
	public Users getOwner() {
		return owner;
	}
	public void setOwner(Users owner) {
		this.owner = owner;
	}
	
	/**
     * @hibernate.property
     *  column="booked_room"
     *  type="boolean"
     */
	public Boolean getBookedRoom() {
		return bookedRoom;
	}
	public void setBookedRoom(Boolean bookedRoom) {
		this.bookedRoom = bookedRoom;
	}

	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="rooms_id"
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
     * @hibernate.property
     *  column="parent_message_id"
     *  type="long"
     */
	public Long getParentMessage() {
		return parentMessage;
	}
	public void setParentMessage(Long parentMessage) {
		this.parentMessage = parentMessage;
	}
	
	/**
     * @hibernate.property
     *  column="is_trash"
     *  type="boolean"
     */
	public Boolean getIsTrash() {
		return isTrash;
	}
	public void setIsTrash(Boolean isTrash) {
		this.isTrash = isTrash;
	}
	
	/**
     * @hibernate.property
     *  column="private_message_folder_id"
     *  type="long"
     */
	public Long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(Long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
	}
	
	/**
     * @hibernate.property
     *  column="is_read"
     *  type="boolean"
     */
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
}
