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
	private Boolean isEndPadding;
	private String waveOutPutName;
	private Integer dataLengthPacket;
	private Long receivedAudioDataLength;
	private Date startTime;
	private Date currentTime;
	
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
	
}
