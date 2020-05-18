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
package org.apache.openmeetings.web.admin.connection;

import java.util.Date;

import org.apache.openmeetings.core.remote.KStream;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
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
public class KStreamDto implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	private String sid;
	private String uid;
	private Long roomId;
	private Date connectedSince;
	private StreamType streamType;
	private String profile;
	private String recorder;
	private Long chunkId;
	private Type type;

	public KStreamDto(KStream kStream) {
		this.sid = kStream.getSid();
		this.uid = kStream.getUid();
		this.roomId = (kStream.getRoom() == null) ? null : kStream.getRoom().getRoomId();
		this.connectedSince = kStream.getConnectedSince();
		this.streamType = kStream.getStreamType();
		this.profile = kStream.getProfile().toString();
		this.recorder = (kStream.getRecorder() == null) ? null : kStream.getRecorder().toString();
		this.chunkId = kStream.getChunkId();
		this.type = kStream.getType();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
		// no-op
	}
}
