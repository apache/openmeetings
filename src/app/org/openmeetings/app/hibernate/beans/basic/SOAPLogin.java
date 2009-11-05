package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;

/**
 * 
 * @hibernate.class table="soaplogin"
 *
 */
public class SOAPLogin {
	
	private long soapLoginId;
	private String hash;
	private Long room_id;
	private String sessionHash;
	private Date created;
	private Boolean used;
	private Date useDate;
	private Boolean becomemoderator;
	private Boolean showAudioVideoTest;
	
	/**
     * 
     * @hibernate.id
     *  column="soapLoginId"
     *  generator-class="increment"
     */
	public long getSoapLoginId() {
		return soapLoginId;
	}
	public void setSoapLoginId(long soapLoginId) {
		this.soapLoginId = soapLoginId;
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
     *  column="room_id"
     *  type="long"
     */ 
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}
	
	/**
     * @hibernate.property
     *  column="session_hash"
     *  type="string"
     */
	public String getSessionHash() {
		return sessionHash;
	}
	public void setSessionHash(String sessionHash) {
		this.sessionHash = sessionHash;
	}
	
	/**
     * @hibernate.property
     *  column="created"
     *  type="java.util.Date"
     */
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
     * @hibernate.property
     *  column="used"
     *  type="boolean"
     */
	public Boolean getUsed() {
		return used;
	}
	public void setUsed(Boolean used) {
		this.used = used;
	}
	
	/**
     * @hibernate.property
     *  column="use_date"
     *  type="java.util.Date"
     */
	public Date getUseDate() {
		return useDate;
	}
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
	
	/**
     * @hibernate.property
     *  column="becomemoderator"
     *  type="boolean"
     */
	public Boolean getBecomemoderator() {
		return becomemoderator;
	}
	public void setBecomemoderator(Boolean becomemoderator) {
		this.becomemoderator = becomemoderator;
	}
	
	/**
     * @hibernate.property
     *  column="showaudiovideotest"
     *  type="boolean"
     */
	public Boolean getShowAudioVideoTest() {
		return showAudioVideoTest;
	}
	public void setShowAudioVideoTest(Boolean showAudioVideoTest) {
		this.showAudioVideoTest = showAudioVideoTest;
	}
	
}
