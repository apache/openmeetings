package org.openmeetings.app.persistence.beans.logs;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "conferencelogtype")
public class ConferenceLogType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4388958579350356294L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="conferencelogtype_id")
	private long conferenceLogTypeId;
	@Column(name="eventtype")
	private String eventType;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="insertedby")
	private long insertedby;
	
	public long getConferenceLogTypeId() {
		return conferenceLogTypeId;
	}
	public void setConferenceLogTypeId(long conferenceLogTypeId) {
		this.conferenceLogTypeId = conferenceLogTypeId;
	}

	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	public long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(long insertedby) {
		this.insertedby = insertedby;
	}
	
}
