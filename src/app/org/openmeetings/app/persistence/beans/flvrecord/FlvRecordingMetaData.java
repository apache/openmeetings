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
 * @hibernate.class table="flvrecording_metadata"
 * lazy="false"
 *
 */
@Entity
@Table(name = "flvrecording_metadata")
public class FlvRecordingMetaData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8444176152324513716L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="flvrecording_metadata_id")
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
     *  update="false"
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
	
	/**
     * @hibernate.property
     *  column="initial_gap_seconds"
     *  type="int"
     */
	public Integer getInitialGapSeconds() {
		return initialGapSeconds;
	}
	public void setInitialGapSeconds(Integer initialGapSeconds) {
		this.initialGapSeconds = initialGapSeconds;
	}
}