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
package org.openmeetings.app.persistence.beans.sip;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "open_xg_return_object")
public class OpenXGReturnObject implements Serializable {
	private static final long serialVersionUID = -5240675684036197687L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long openXGReturnObjectId;
	
	@Column(name="method_name")
	private String methodName;
	
	@Column(name="inserted")
	private Date inserted;
	
	@Column(name="status_code")
	private String status_code;
	
	@Column(name="status_string")
	private String status_string;
	
	@Column(name="conference_number")
	private String conferenceNumber;
	
	@Column(name="conference_pin")
	private String conferencePin;
	
	public Long getOpenXGReturnObjectId() {
		return openXGReturnObjectId;
	}
	public void setOpenXGReturnObjectId(Long openXGReturnObjectId) {
		this.openXGReturnObjectId = openXGReturnObjectId;
	}
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
    
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	public String getStatus_code() {
		return status_code;
	}
	public void setStatus_code(String statusCode) {
		status_code = statusCode;
	}
	
	public String getStatus_string() {
		return status_string;
	}
	public void setStatus_string(String statusString) {
		status_string = statusString;
	}
	
	public String getConferenceNumber() {
		return conferenceNumber;
	}
	public void setConferenceNumber(String conferenceNumber) {
		this.conferenceNumber = conferenceNumber;
	}
	
	public String getConferencePin() {
		return conferencePin;
	}
	public void setConferencePin(String conferencePin) {
		this.conferencePin = conferencePin;
	}

}
