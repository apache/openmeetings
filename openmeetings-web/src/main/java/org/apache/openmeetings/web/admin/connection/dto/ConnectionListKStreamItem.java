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
package org.apache.openmeetings.web.admin.connection.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Type;

/**
 * A KStream for the Wicket UI to display. This object can be serialized, otherwise 
 * Wicket won't render it.
 * 
 * So It contains NO reference to Kurento client objects.
 * 
 * @author sebawagner
 *
 */
public class ConnectionListKStreamItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** StreamProcessor or KurentoHandler list */
	private String source; 
	
	private String sid;
	private String uid;
	private Long roomId;
	private Date connectedSince;
	private StreamType streamType;
	private String profile;
	private String recorder;
	private Long chunkId;
	private Type type;
	
	public ConnectionListKStreamItem(String source, String sid, String uid, Long roomId, Date connectedSince,
			StreamType streamType, String profile, String recorder, Long chunkId, Type type) {
		super();
		this.source = source;
		this.sid = sid;
		this.uid = uid;
		this.roomId = roomId;
		this.connectedSince = connectedSince;
		this.streamType = streamType;
		this.profile = profile;
		this.recorder = recorder;
		this.chunkId = chunkId;
		this.type = type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSource() {
		return source;
	}
	public String getSid() {
		return sid;
	}
	public String getUid() {
		return uid;
	}
	public Long getRoomId() {
		return roomId;
	}
	public Date getConnectedSince() {
		return connectedSince;
	}
	public StreamType getStreamType() {
		return streamType;
	}
	public String getProfile() {
		return profile;
	}
	public String getRecorder() {
		return recorder;
	}
	public Long getChunkId() {
		return chunkId;
	}
	public Type getType() {
		return type;
	}
	
}
