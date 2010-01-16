package org.openmeetings.app.hibernate.beans.flvrecord;

import java.util.Date;

/**
 * 
 * @hibernate.class table="flvrecording_meta_delta"
 * lazy="false"
 *
 */
public class FlvRecordingMetaDelta {
	
	private long flvRecordingMetaDeltaId;
	private Long flvRecordingMetaDataId;
	private Integer timeStamp;
	private Long deltaTime;
	private Date startTime;
	private Date currentTime;
	private Long deltaTimeStamp;
	private Long offset;
	private Long missingTime;
	private Integer duration;
	private Integer startTimeStamp;
	private Integer packetTimeStamp;
	private String waveOutPutName;
	private Integer dataLengthPacket;
	private Long receivedAudioDataLength;
	private Boolean isEndPadding;
	private Boolean isStartPadding;
	private String debugStatus;
	private Long streamCreationTime;
	private Date streamCreationTimeDate;
	
	/**
	 *
	 * @hibernate.id
	 *  column="flvrecording_meta_delta_id"
	 *  generator-class="increment"
	 */
	public long getFlvRecordingMetaDeltaId() {
		return flvRecordingMetaDeltaId;
	}
	public void setFlvRecordingMetaDeltaId(long flvRecordingMetaDeltaId) {
		this.flvRecordingMetaDeltaId = flvRecordingMetaDeltaId;
	}
	
	/**
     * @hibernate.property
     *  column="flvrecording_metadata_id"
     *  type="long"
     */	
	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	public void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}
	
	/**
     * @hibernate.property
     *  column="time_stamp"
     *  type="int"
     */	
	public Integer getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
     * @hibernate.property
     *  column="delta_time"
     *  type="long"
     */		
	public Long getDeltaTime() {
		return deltaTime;
	}
	public void setDeltaTime(Long deltaTime) {
		this.deltaTime = deltaTime;
	}
	
	/**
     * @hibernate.property
     *  column="delta_time_stamp"
     *  type="long"
     */		
	public Long getDeltaTimeStamp() {
		return deltaTimeStamp;
	}
	public void setDeltaTimeStamp(Long deltaTimeStamp) {
		this.deltaTimeStamp = deltaTimeStamp;
	}
	
	/**
     * @hibernate.property
     *  column="packet_time_stamp"
     *  type="int"
     */
	public Integer getPacketTimeStamp() {
		return packetTimeStamp;
	}
	public void setPacketTimeStamp(Integer packetTimeStamp) {
		this.packetTimeStamp = packetTimeStamp;
	}
	
	/**
     * @hibernate.property
     *  column="packet_time_stamp"
     *  type="long"
     */
	public Long getOffset() {
		return offset;
	}
	public void setOffset(Long offset) {
		this.offset = offset;
	}
	/**
     * @hibernate.property
     *  column="start_time_stamp"
     *  type="int"
     */		
	public Integer getStartTimeStamp() {
		return startTimeStamp;
	}
	public void setStartTimeStamp(Integer startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}
	
	/**
     * @hibernate.property
     *  column="missing_time"
     *  type="long"
     */	
	public Long getMissingTime() {
		return missingTime;
	}
	public void setMissingTime(Long missingTime) {
		this.missingTime = missingTime;
	}
	
	/**
     * @hibernate.property
     *  column="duration"
     *  type="int"
     */		
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	/**
     * @hibernate.property
     *  column="start_time"
     *  type="java.util.Date"
     */
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	/**
     * @hibernate.property
     *  column="current_event_time"
     *  type="java.util.Date"
     */	
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
	/**
     * @hibernate.property
     *  column="is_end_padding"
     *  type="boolean"
     */
	public Boolean getIsEndPadding() {
		return isEndPadding;
	}
	public void setIsEndPadding(Boolean isEndPadding) {
		this.isEndPadding = isEndPadding;
	}
	
	/**
     * @hibernate.property
     *  column="is_start_padding"
     *  type="boolean"
     */	
	public Boolean getIsStartPadding() {
		return isStartPadding;
	}
	public void setIsStartPadding(Boolean isStartPadding) {
		this.isStartPadding = isStartPadding;
	}
	
	/**
     * @hibernate.property
     *  column="debug_status"
     *  type="string"
     */		
	public String getDebugStatus() {
		return debugStatus;
	}
	public void setDebugStatus(String debugStatus) {
		this.debugStatus = debugStatus;
	}
	
	/**
     * @hibernate.property
     *  column="stream_creation_time"
     *  type="long"
     */	
	public Long getStreamCreationTime() {
		return streamCreationTime;
	}
	public void setStreamCreationTime(Long streamCreationTime) {
		this.streamCreationTime = streamCreationTime;
	}
	
	/**
     * @hibernate.property
     *  column="stream_creation_time_date"
     *  type="java.util.Date"
     */		
	public Date getStreamCreationTimeDate() {
		return streamCreationTimeDate;
	}
	public void setStreamCreationTimeDate(Date streamCreationTimeDate) {
		this.streamCreationTimeDate = streamCreationTimeDate;
	}
	
	/**
     * @hibernate.property
     *  column="wave_out_put_name"
     *  type="string"
     */	
	public String getWaveOutPutName() {
		return waveOutPutName;
	}
	public void setWaveOutPutName(String waveOutPutName) {
		this.waveOutPutName = waveOutPutName;
	}
	
	/**
     * @hibernate.property
     *  column="data_length_packet"
     *  type="int"
     */		
	public Integer getDataLengthPacket() {
		return dataLengthPacket;
	}
	public void setDataLengthPacket(Integer dataLengthPacket) {
		this.dataLengthPacket = dataLengthPacket;
	}
	
	/**
     * @hibernate.property
     *  column="received_audio_data_length"
     *  type="long"
     */		
	public Long getReceivedAudioDataLength() {
		return receivedAudioDataLength;
	}
	public void setReceivedAudioDataLength(Long receivedAudioDataLength) {
		this.receivedAudioDataLength = receivedAudioDataLength;
	}
	
}
