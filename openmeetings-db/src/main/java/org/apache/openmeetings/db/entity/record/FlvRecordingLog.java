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
package org.apache.openmeetings.db.entity.record;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
@Entity
@NamedQueries({ 
	@NamedQuery(name = "getRecordingLogsByRecording", query = "SELECT fl FROM FlvRecordingLog fl WHERE fl.flvRecording.id = :recId")
	, @NamedQuery(name = "countErrorRecordingLogsByRecording", query = "SELECT COUNT(fl) FROM FlvRecordingLog fl WHERE fl.flvRecording.id = :recId AND fl.exitValue <> '0'")
	, @NamedQuery(name = "deleteErrorRecordingLogsByRecording", query = "DELETE FROM FlvRecordingLog fl WHERE fl.flvRecording.id = :recId")
})
@Table(name = "flvrecording_log")
public class FlvRecordingLog implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	public static final int MAX_LOG_SIZE = 1 * 1024 * 1024;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="flvrecording_id", nullable=true)
	private FlvRecording flvRecording;
	
	@Column(name="inserted")
	private Date inserted;
	
	@Column(name="msg_type")
	private String msgType;
	
	@Lob
	@Column(name="ful_message", length = MAX_LOG_SIZE)
	private String fullMessage;
	
	@Column(name="exit_value")
	private String exitValue;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
		this.fullMessage = fullMessage == null || fullMessage.length() < MAX_LOG_SIZE ? fullMessage : fullMessage.substring(0,  MAX_LOG_SIZE);
	}
	
	public String getExitValue() {
		return exitValue;
	}
	public void setExitValue(String exitValue) {
		this.exitValue = exitValue;
	}
}
