package org.openmeetings.app.hibernate.beans.logs;

import java.util.Date;

/**
 * 
 * @hibernate.class table="conferencelogtype"
 *
 */
public class ConferenceLogType {

	private long conferenceLogTypeId;
	private String eventType;
	private Date inserted;
	private long insertedby;
	
	/**
     * 
     * @hibernate.id
     *  column="conferencelogtype_id"
     *  generator-class="increment"
     */ 
	public long getConferenceLogTypeId() {
		return conferenceLogTypeId;
	}
	public void setConferenceLogTypeId(long conferenceLogTypeId) {
		this.conferenceLogTypeId = conferenceLogTypeId;
	}

	/**
     * @hibernate.property
     *  column="eventtype"
     *  type="string"
     */
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
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
     * @hibernate.property
     *  column="insertedby"
     *  type="long"
     */ 
	public long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(long insertedby) {
		this.insertedby = insertedby;
	}
	
}
