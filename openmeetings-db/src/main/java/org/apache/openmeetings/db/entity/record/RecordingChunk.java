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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * contains data about each stream, for example if it is a screen sharing or
 * audio/video stream. There is also a {@link Status} value
 * {@link #streamStatus}, as long as this variable is not set
 * to {@link Status#STOPPED}, the recording process will not proceed and start to convert all
 * input sources to a single recording file.
 *
 * @author sebawagner
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getChunkById", query = "SELECT c FROM RecordingChunk c WHERE c.id = :id")
	, @NamedQuery(name = "getChunkByRecording", query = "SELECT c FROM RecordingChunk c WHERE c.recording.id = :recordingId AND c.deleted = false")
	, @NamedQuery(name = "getNotScreenChunkByRecording", query = "SELECT c FROM RecordingChunk c WHERE c.recording.id = :recordingId AND c.deleted = false "
			+ "AND c.type <> :screen AND c.streamStatus <> :none")
	, @NamedQuery(name = "getScreenChunkByRecording", query = "SELECT c FROM RecordingChunk c WHERE c.recording.id = :recordingId AND c.type = :screen")
})
@Table(name = "recording_chunk")
@Root(name = "flvrecordingmetadata")
public class RecordingChunk extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@XmlType(namespace="org.apache.openmeetings.record.chunk")
	public enum Status {
		NONE
		, STARTED
		, STOPPING
		, STOPPED
	}
	@XmlType(namespace="org.apache.openmeetings.record.chunk")
	public enum Type {
		AUDIO_ONLY
		, VIDEO_ONLY
		, AUDIO_VIDEO
		, SCREEN
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "flvRecordingMetaDataId", data = true, required = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recording_id", nullable = true)
	@ForeignKey(enabled = true)
	private Recording recording;

	@Column(name = "start")
	@Element(name = "recordStart", data = true)
	private Date start;

	@Column(name = "end")
	@Element(name = "recordEnd", data = true, required = false)
	private Date end;

	@Column(name = "stream_name")
	@Element(data = true)
	private String streamName;

	@Column(name = "sid")
	@Element(data = true, required = false)
	private String sid;

	@Column(name = "type")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	/**
	 * this is only STOPPED when the asynchronous stream writer's have completed to write packets to the file.
	 */
	@Column(name = "stream_status")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private Status streamStatus = Status.NONE;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Recording getRecording() {
		return recording;
	}

	public void setRecording(Recording recording) {
		this.recording = recording;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isAudioOnly() {
		return Type.AUDIO_ONLY == type;
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Status getStreamStatus() {
		return streamStatus;
	}

	public void setStreamStatus(Status status) {
		this.streamStatus = status;
	}
}
