package org.openmeetings.app.hibernate.beans.sip;

import java.util.Date;

/**
 * 
 * @hibernate.class table="open_xg_return_object"
 * lazy="false"
 *
 */
public class OpenXGReturnObject {
	
	private Long openXGReturnObjectId;
	private String methodName;
	private Date inserted;
	private String status_code;
	private String status_string;
	private String conferenceNumber;
	private String conferencePin;
	
    /**
     * 
     * @hibernate.id
     *  column="open_xg_return_object_id"
     *  generator-class="increment"
     */  
	public Long getOpenXGReturnObjectId() {
		return openXGReturnObjectId;
	}
	public void setOpenXGReturnObjectId(Long openXGReturnObjectId) {
		this.openXGReturnObjectId = openXGReturnObjectId;
	}
	
    /**
     * @hibernate.property
     *  column="method_name"
     *  type="string"
     */
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
     *  column="status_code"
     *  type="string"
     */	
	public String getStatus_code() {
		return status_code;
	}
	public void setStatus_code(String statusCode) {
		status_code = statusCode;
	}
	
    /**
     * @hibernate.property
     *  column="status_string"
     *  type="string"
     */	
	public String getStatus_string() {
		return status_string;
	}
	public void setStatus_string(String statusString) {
		status_string = statusString;
	}
	
    /**
     * @hibernate.property
     *  column="conference_number"
     *  type="string"
     */	
	public String getConferenceNumber() {
		return conferenceNumber;
	}
	public void setConferenceNumber(String conferenceNumber) {
		this.conferenceNumber = conferenceNumber;
	}
	
    /**
     * @hibernate.property
     *  column="conference_pin"
     *  type="string"
     */
	public String getConferencePin() {
		return conferencePin;
	}
	public void setConferencePin(String conferencePin) {
		this.conferencePin = conferencePin;
	}

}
