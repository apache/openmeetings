package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;

/**
 * 
 * @hibernate.class table="om_timezone"
 *
 */
public class OmTimeZone {
	
	private Long omtimezoneId;
	private String jname;
	private String label;
	private String ical;
	private Date inserted;
	public String frontEndLabel;
	
	/**
     * 
     * @hibernate.id
     *  column="om_timezone_id"
     *  generator-class="increment"
     */
	public Long getOmtimezoneId() {
		return omtimezoneId;
	}
	public void setOmtimezoneId(Long omtimezoneId) {
		this.omtimezoneId = omtimezoneId;
	}
	
	/**
     * @hibernate.property
     *  column="jname"
     *  type="string"
     */
	public String getJname() {
		return jname;
	}
	public void setJname(String jname) {
		this.jname = jname;
	}
	
	/**
     * @hibernate.property
     *  column="label"
     *  type="string"
     */
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
     * @hibernate.property
     *  column="ical"
     *  type="string"
     */
	public String getIcal() {
		return ical;
	}
	public void setIcal(String ical) {
		this.ical = ical;
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
	
	public String getFrontEndLabel() {
		return frontEndLabel;
	}
	public void setFrontEndLabel(String frontEndLabel) {
		this.frontEndLabel = frontEndLabel;
	}
	
}
