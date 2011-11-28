package org.openmeetings.app.persistence.beans.flvrecord;

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -733483974696234137L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="flv_delta_id")
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
	@Column(name="offset_time")
	private Long offset;
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
	@Column(name="stream_creation_time")
	private Long streamCreationTime;
	@Column(name="stream_creation_time_date")
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
     *  column="last_time_stamp"
     *  type="long"
     */	
	public Long getLastTimeStamp() {
		return lastTimeStamp;
	}
	public void setLastTimeStamp(Long lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
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
     *  column="offset_time"
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
