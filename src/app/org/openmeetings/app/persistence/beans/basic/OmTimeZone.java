package org.openmeetings.app.persistence.beans.basic;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "om_timezone")
public class OmTimeZone implements Serializable {
	
	private static final long serialVersionUID = 6521571695782900198L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="om_timezone_id")
	private Long omtimezoneId;
	@Column(name="jname")
	private String jname;
	@Column(name="label")
	private String label;
	@Column(name="ical")
	private String ical;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="order_id")
	public Integer orderId;
	@Transient
	public String frontEndLabel;
	
	public Long getOmtimezoneId() {
		return omtimezoneId;
	}
	public void setOmtimezoneId(Long omtimezoneId) {
		this.omtimezoneId = omtimezoneId;
	}
	
	public String getJname() {
		return jname;
	}
	public void setJname(String jname) {
		this.jname = jname;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getIcal() {
		return ical;
	}
	public void setIcal(String ical) {
		this.ical = ical;
	}
	
	public Date getInserted() {
		return inserted;
	}
	
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	
	public String getFrontEndLabel() {
		return frontEndLabel;
	}
	
	public void setFrontEndLabel(String frontEndLabel) {
		this.frontEndLabel = frontEndLabel;
	}
	
}
