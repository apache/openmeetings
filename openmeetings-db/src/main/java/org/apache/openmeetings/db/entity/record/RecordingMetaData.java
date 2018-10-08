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
 * contains meta data about each stream, for example if it is a screen sharing or
 * audio/video stream. There is also a {@link Status} value
 * {@link #streamStatus}, as long as this variable is not set
 * to {@link Status#STOPPED}, the recording process will not proceed and start to convert all
 * input sources to a single recording file.
 *
 * @author sebawagner
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getMetaById", query = "SELECT c FROM RecordingMetaData c WHERE c.id = :id")
	, @NamedQuery(name = "getMetaByRecording", query = "SELECT c FROM RecordingMetaData c WHERE c.recording.id = :recordingId AND c.deleted = false")
	, @NamedQuery(name = "getNotScreenMetaByRecording", query = "SELECT c FROM RecordingMetaData c WHERE c.recording.id = :recordingId AND c.deleted = false "
			+ "AND c.screenData = false AND c.streamStatus <> :none")
	, @NamedQuery(name = "getScreenMetaByRecording", query = "SELECT c FROM RecordingMetaData c WHERE c.recording.id = :recordingId AND c.screenData = true")
})
@Table(name = "recording_metadata")
@Root(name = "flvrecordingmetadata")
public class RecordingMetaData extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@XmlType(namespace="org.apache.openmeetings.record.meta")
	public enum Status {
		NONE
		, STARTED
		, STOPPING
		, STOPPED
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

	@Column(name = "record_start")
	@Element(data = true)
	private Date recordStart;

	@Column(name = "record_end")
	@Element(data = true, required = false)
	private Date recordEnd;

	@Column(name = "stream_name")
	@Element(data = true)
	private String streamName;

	@Column(name = "sid")
	@Element(data = true, required = false)
	private String sid;

	@Column(name = "is_audio_only", nullable = false)
	@Element(name = "isAudioOnly", data = true)
	private boolean audioOnly;

	@Column(name = "is_video_only", nullable = false)
	@Element(name = "isVideoOnly", data = true)
	private boolean videoOnly;

	@Column(name = "is_screen_data", nullable = false)
	@Element(name = "isScreenData", data = true)
	private boolean screenData;

	@Column(name = "full_wav_audio_data")
	@Element(data = true, required = false)
	private String fullWavAudioData;

	@Column(name = "audio_is_valid", nullable = false)
	@Element(name="audioIsValid", data = true, required = false)
	private boolean audioValid;

	/**
	 * this is only STOPPED when the asynchronous stream writer's have completed to write packets to the file.
	 */
	@Column(name = "stream_status")
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

	public Date getRecordStart() {
		return recordStart;
	}

	public void setRecordStart(Date recordStart) {
		this.recordStart = recordStart;
	}

	public Date getRecordEnd() {
		return recordEnd;
	}

	public void setRecordEnd(Date recordEnd) {
		this.recordEnd = recordEnd;
	}

	public boolean isAudioOnly() {
		return audioOnly;
	}

	public void setAudioOnly(boolean audioOnly) {
		this.audioOnly = audioOnly;
	}

	public boolean isVideoOnly() {
		return videoOnly;
	}

	public void setVideoOnly(boolean videoOnly) {
		this.videoOnly = videoOnly;
	}

	public boolean isScreenData() {
		return screenData;
	}

	public void setScreenData(boolean screenData) {
		this.screenData = screenData;
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

	public String getFullWavAudioData() {
		return fullWavAudioData;
	}

	public void setFullWavAudioData(String fullWavAudioData) {
		this.fullWavAudioData = fullWavAudioData;
	}

	public boolean isAudioValid() {
		return audioValid;
	}

	public void setAudioValid(boolean audioValid) {
		this.audioValid = audioValid;
	}

	public Status getStreamStatus() {
		return streamStatus;
	}

	public void setStreamStatus(Status status) {
		this.streamStatus = status;
	}
}
