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
package org.openmeetings.app.persistence.beans.flvrecord;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 
 */
@Entity
@Table(name = "flvrecording_metadata")
public class FlvRecordingMetaData implements Serializable {
	
	public Boolean getStreamReaderThreadComplete() {
		return streamReaderThreadComplete;
	}
	public void setStreamReaderThreadComplete(Boolean streamReaderThreadComplete) {
		this.streamReaderThreadComplete = streamReaderThreadComplete;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8444176152324513716L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="flv_meta_id")
	private long flvRecordingMetaDataId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="flvrecording_id", nullable=true)
	private FlvRecording flvRecording;
	
	@Column(name="record_start")
	private Date recordStart;
	@Column(name="record_end")
	private Date recordEnd;
	@Column(name="stream_name")
	private String streamName;
	
	@Column(name="free_text_user_name")
	private String freeTextUserName;
	@Column(name="is_audio_only")
	private Boolean isAudioOnly;
	@Column(name="is_video_only")
	private Boolean isVideoOnly;
	@Column(name="is_screen_data")
	private Boolean isScreenData;

	@Column(name="inserted_by")
	private Long insertedBy;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="updated")
	private Date updated;
	@Column(name="deleted")
	private String deleted;
	
	@Column(name="wav_audio_data")
	private String wavAudioData;
	@Column(name="full_wav_audio_data")
	private String fullWavAudioData;
	
	@Column(name="audio_is_valid")
	private Boolean audioIsValid;
	
	@Column(name="interiew_pod_id")
	private Integer interiewPodId;
	@Column(name="initial_gap_seconds")
	private Integer initialGapSeconds;
	
	//this is only true when the asynchronous stream writer has 
	//completed to write packets to the file
	@Column(name="stream_reader_thread_complete")
	private Boolean streamReaderThreadComplete;
	
	public long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	public void setFlvRecordingMetaDataId(long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}
	
	public FlvRecording getFlvRecording() {
		return flvRecording;
	}
	public void setFlvRecording(FlvRecording flvRecording) {
		this.flvRecording = flvRecording;
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

	public String getFreeTextUserName() {
		return freeTextUserName;
	}
	public void setFreeTextUserName(String freeTextUserName) {
		this.freeTextUserName = freeTextUserName;
	}
	
	public Boolean getIsAudioOnly() {
		return isAudioOnly;
	}
	public void setIsAudioOnly(Boolean isAudioOnly) {
		this.isAudioOnly = isAudioOnly;
	}
	
	public Boolean getIsVideoOnly() {
		return isVideoOnly;
	}
	public void setIsVideoOnly(Boolean isVideoOnly) {
		this.isVideoOnly = isVideoOnly;
	}
	
	public Long getInsertedBy() {
		return insertedBy;
	}
	public void setInsertedBy(Long insertedBy) {
		this.insertedBy = insertedBy;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public Boolean getIsScreenData() {
		return isScreenData;
	}
	public void setIsScreenData(Boolean isScreenData) {
		this.isScreenData = isScreenData;
	}

	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	
	public String getWavAudioData() {
		return wavAudioData;
	}
	public void setWavAudioData(String wavAudioData) {
		this.wavAudioData = wavAudioData;
	}
	
	public String getFullWavAudioData() {
		return fullWavAudioData;
	}
	public void setFullWavAudioData(String fullWavAudioData) {
		this.fullWavAudioData = fullWavAudioData;
	}
	
	public Boolean getAudioIsValid() {
		return audioIsValid;
	}
	public void setAudioIsValid(Boolean audioIsValid) {
		this.audioIsValid = audioIsValid;
	}

	public Integer getInteriewPodId() {
		return interiewPodId;
	}
	public void setInteriewPodId(Integer interiewPodId) {
		this.interiewPodId = interiewPodId;
	}
	
	public Integer getInitialGapSeconds() {
		return initialGapSeconds;
	}
	public void setInitialGapSeconds(Integer initialGapSeconds) {
		this.initialGapSeconds = initialGapSeconds;
	}
	
}
