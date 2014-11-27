package org.apache.openmeetings.web.room.activities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Type { //TODO maybe additional type is not necessary
		roomEnter
		, roomExit
		, askModeration //TODO check
	}
	private String uid;
	private Long sender;
	private Date created;
	private Type type;
	
	public Activity(Long sender, Type type) {
		this.uid = UUID.randomUUID().toString();
		this.sender = sender;
		this.type = type;
		this.created = new Date(); //TODO timezone
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Long getSender() {
		return sender;
	}

	public void setSender(Long sender) {
		this.sender = sender;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
