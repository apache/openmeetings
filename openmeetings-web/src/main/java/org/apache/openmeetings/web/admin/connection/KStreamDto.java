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

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Type;
import org.apache.openmeetings.mediaserver.KStream;

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

	private final String sid;
	private final String uid;
	private final Long roomId;
	private final Date connectedSince;
	private final StreamType streamType;
	private final String profile;
	private final String recorder;
	private final Long chunkId;
	private final Type type;

	public KStreamDto(KStream kStream) {
		sid = kStream.getSid();
		uid = kStream.getUid();
		roomId = kStream.getRoomId();
		connectedSince = kStream.getConnectedSince();
		streamType = kStream.getStreamType();
		profile = "" + kStream.getProfile();
		recorder = (kStream.getRecorder() == null) ? null : kStream.getRecorder().toString();
		chunkId = kStream.getChunkId();
		type = kStream.getType();
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
