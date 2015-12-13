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
package org.apache.openmeetings.persistence.beans.flvrecord;

import java.io.Serializable;
import java.util.Date;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "flvrecording_log")
public class FlvRecordingLog implements Serializable {
	private static final long serialVersionUID = -2577533628675416706L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long flvRecordingLogId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="flvrecording_id", nullable=true)
	private FlvRecording flvRecording;
	
	@Column(name="inserted")
	private Date inserted;
	
	@Column(name="msg_type")
	private String msgType;
	
	@Lob
	@Column(name="ful_message")
	private String fullMessage;
	
	@Column(name="exit_value")
	private String exitValue;
	
	public long getFlvRecordingLogId() {
		return flvRecordingLogId;
	}
	public void setFlvRecordingLogId(long flvRecordingLogId) {
		this.flvRecordingLogId = flvRecordingLogId;
	}
	
	public FlvRecording getFlvRecording() {
		return flvRecording;
	}
	public void setFlvRecording(FlvRecording flvRecording) {
		this.flvRecording = flvRecording;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}
	
	public String getExitValue() {
		return exitValue;
	}
	public void setExitValue(String exitValue) {
		this.exitValue = exitValue;
	}
}
