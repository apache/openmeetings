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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 *
 */
@Entity
@Table(name = "flvrecording_meta_delta")
public class FlvRecordingMetaDelta implements Serializable {
	private static final long serialVersionUID = -733483974696234137L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long flvRecordingMetaDeltaId;
	
	@Column(name="flvrecording_metadata_id")
	private Long flvRecordingMetaDataId;
	
	@Column(name="time_stamp")
	private Integer timeStamp;
	
	@Column(name="delta_time")
	private Long deltaTime;
	
	@Column(name="last_time_stamp")
	private Long lastTimeStamp;
	
	@Column(name="start_time")
	private Date startTime;
	
	@Column(name="current_event_time")
	private Date currentTime;
	
	@Column(name="delta_time_stamp")
	private Long deltaTimeStamp;
	
	@Column(name="missing_time")
	private Long missingTime;
	
	@Column(name="duration")
	private Integer duration;
	
	@Column(name="start_time_stamp")
	private Integer startTimeStamp;
	
	@Column(name="packet_time_stamp")
	private Integer packetTimeStamp;
	
	@Column(name="wave_out_put_name")
	private String waveOutPutName;
	
	@Column(name="data_length_packet")
	private Integer dataLengthPacket;
	
	@Column(name="received_audio_data_length")
	private Long receivedAudioDataLength;
	
	@Column(name="is_end_padding")
	private Boolean isEndPadding;
	
	@Column(name="is_start_padding")
	private Boolean isStartPadding;
	
	@Column(name="debug_status")
	private String debugStatus;
	
	public long getFlvRecordingMetaDeltaId() {
		return flvRecordingMetaDeltaId;
	}
	public void setFlvRecordingMetaDeltaId(long flvRecordingMetaDeltaId) {
		this.flvRecordingMetaDeltaId = flvRecordingMetaDeltaId;
	}
	
	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	public void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}
	
	public Integer getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public Long getDeltaTime() {
		return deltaTime;
	}
	public void setDeltaTime(Long deltaTime) {
		this.deltaTime = deltaTime;
	}
	
	public Long getLastTimeStamp() {
		return lastTimeStamp;
	}
	public void setLastTimeStamp(Long lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}
	
	public Long getDeltaTimeStamp() {
		return deltaTimeStamp;
	}
	public void setDeltaTimeStamp(Long deltaTimeStamp) {
		this.deltaTimeStamp = deltaTimeStamp;
	}
	
	public Integer getPacketTimeStamp() {
		return packetTimeStamp;
	}
	public void setPacketTimeStamp(Integer packetTimeStamp) {
		this.packetTimeStamp = packetTimeStamp;
	}
	
	public Integer getStartTimeStamp() {
		return startTimeStamp;
	}
	public void setStartTimeStamp(Integer startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}
	
	public Long getMissingTime() {
		return missingTime;
	}
	public void setMissingTime(Long missingTime) {
		this.missingTime = missingTime;
	}
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
	public Boolean getIsEndPadding() {
		return isEndPadding;
	}
	public void setIsEndPadding(Boolean isEndPadding) {
		this.isEndPadding = isEndPadding;
	}
	
	public Boolean getIsStartPadding() {
		return isStartPadding;
	}
	public void setIsStartPadding(Boolean isStartPadding) {
		this.isStartPadding = isStartPadding;
	}
	
	public String getDebugStatus() {
		return debugStatus;
	}
	public void setDebugStatus(String debugStatus) {
		this.debugStatus = debugStatus;
	}
	
	public String getWaveOutPutName() {
		return waveOutPutName;
	}
	public void setWaveOutPutName(String waveOutPutName) {
		this.waveOutPutName = waveOutPutName;
	}
	
	public Integer getDataLengthPacket() {
		return dataLengthPacket;
	}
	public void setDataLengthPacket(Integer dataLengthPacket) {
		this.dataLengthPacket = dataLengthPacket;
	}
	
	public Long getReceivedAudioDataLength() {
		return receivedAudioDataLength;
	}
	public void setReceivedAudioDataLength(Long receivedAudioDataLength) {
		this.receivedAudioDataLength = receivedAudioDataLength;
	}
	
}
