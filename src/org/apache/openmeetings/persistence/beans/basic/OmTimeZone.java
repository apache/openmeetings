/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.persistence.beans.basic;

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
	@Column(name="id")
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
