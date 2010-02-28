package org.openmeetings.app.hibernate.beans.flvrecord;

import java.util.Date;

/**
 * 
 * @hibernate.class table="flvrecording_metadata"
 * lazy="false"
 *
 */
public class FlvRecordingMetaData {
	
	private long flvRecordingMetaDataId;
	private FlvRecording flvRecording;
	
	private Date recordStart;
	private Date recordEnd;
	private String streamName;
	
	private String freeTextUserName;
	private Boolean isAudioOnly;
	private Boolean isVideoOnly;
	private Boolean isScreenData;

	private Long insertedBy;
	private Date inserted;
	private Date updated;
	private String deleted;
	
	private String wavAudioData;
	private String fullWavAudioData;
	
	private Boolean audioIsValid;
	
	private Integer interiewPodId;
	
	/**
	 *
	 * @hibernate.id
	 *  column="flvrecording_metadata_id"
	 *  generator-class="increment"
	 */
	public long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	public void setFlvRecordingMetaDataId(long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="flvrecording_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public FlvRecording getFlvRecording() {
		return flvRecording;
	}
	public void setFlvRecording(FlvRecording flvRecording) {
		this.flvRecording = flvRecording;
	}

	/**
     * @hibernate.property
     *  column="record_start"
     *  type="java.util.Date"
     */
	public Date getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(Date recordStart) {
		this.recordStart = recordStart;
	}

	/**
     * @hibernate.property
     *  column="record_end"
     *  type="java.util.Date"
     */
	public Date getRecordEnd() {
		return recordEnd;
	}
	public void setRecordEnd(Date recordEnd) {
		this.recordEnd = recordEnd;
	}

	/**
     * @hibernate.property
     *  column="free_text_user_name"
     *  type="string"
     */
	public String getFreeTextUserName() {
		return freeTextUserName;
	}
	public void setFreeTextUserName(String freeTextUserName) {
		this.freeTextUserName = freeTextUserName;
	}
	
	/**
     * @hibernate.property
     *  column="is_audio_only"
     *  type="boolean"
     */
	public Boolean getIsAudioOnly() {
		return isAudioOnly;
	}
	public void setIsAudioOnly(Boolean isAudioOnly) {
		this.isAudioOnly = isAudioOnly;
	}
	
	/**
     * @hibernate.property
     *  column="is_video_only"
     *  type="boolean"
     */	
	public Boolean getIsVideoOnly() {
		return isVideoOnly;
	}
	public void setIsVideoOnly(Boolean isVideoOnly) {
		this.isVideoOnly = isVideoOnly;
	}
	
	/**
     * @hibernate.property
     *  column="inserted_by"
     *  type="long"
     */
	public Long getInsertedBy() {
		return insertedBy;
	}
	public void setInsertedBy(Long insertedBy) {
		this.insertedBy = insertedBy;
	}
	
	/**
     * @hibernate.property
     *  column="inserted"
     *  type="java.util.Date"
     */ 
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

    /**
     * @hibernate.property
     *  column="deleted"
     *  type="string"
     */  
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
	/**
     * @hibernate.property
     *  column="updated"
     *  type="java.util.Date"
     */ 
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	/**
     * @hibernate.property
     *  column="is_screen_data"
     *  type="boolean"
     */
	public Boolean getIsScreenData() {
		return isScreenData;
	}
	public void setIsScreenData(Boolean isScreenData) {
		this.isScreenData = isScreenData;
	}

	/**
     * @hibernate.property
     *  column="stream_name"
     *  type="string"
     */
	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	
	/**
     * @hibernate.property
     *  column="wav_audio_data"
     *  type="string"
     */
	public String getWavAudioData() {
		return wavAudioData;
	}
	public void setWavAudioData(String wavAudioData) {
		this.wavAudioData = wavAudioData;
	}
	
	/**
     * @hibernate.property
     *  column="full_wav_audio_data"
     *  type="string"
     */
	public String getFullWavAudioData() {
		return fullWavAudioData;
	}
	public void setFullWavAudioData(String fullWavAudioData) {
		this.fullWavAudioData = fullWavAudioData;
	}
	
	/**
     * @hibernate.property
     *  column="audio_is_valid"
     *  type="boolean"
     */
	public Boolean getAudioIsValid() {
		return audioIsValid;
	}
	public void setAudioIsValid(Boolean audioIsValid) {
		this.audioIsValid = audioIsValid;
	}

	/**
     * @hibernate.property
     *  column="interiew_pod_id"
     *  type="int"
     */
	public Integer getInteriewPodId() {
		return interiewPodId;
	}
	public void setInteriewPodId(Integer interiewPodId) {
		this.interiewPodId = interiewPodId;
	}
	
	
}