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
package org.openmeetings.app.persistence.beans.recording;

import java.io.Serializable;

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
import javax.persistence.Transient;



@Entity
@Table(name = "recording_chatvaluesevent")
public class ChatvaluesEvent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3651904977310257437L;
	//see WhiteboardEvent for Documentation, Comments
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chat_event_id")
	private Long chatvaluesEventId;
	
	@Column(name="starttime")
	private Long starttime;
	@Lob
	@Column(name="action")
	private String action;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomrecording_id", updatable=true, insertable=true)
	private RoomRecording roomRecording;
	
	//this is only Filled if send to client
	@Transient
	private Object actionObj;

	public Long getChatvaluesEventId() {
		return chatvaluesEventId;
	}
	public void setChatvaluesEventId(Long chatvaluesEventId) {
		this.chatvaluesEventId = chatvaluesEventId;
	}

	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
	public Object getActionObj() {
		return actionObj;
	}
	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}
	
	

}
