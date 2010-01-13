package org.openmeetings.app.hibernate.beans.flvrecord;

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
	private String trimFile;
	private String padFile;
	
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
     *  column="trim_file"
     *  type="string"
     */	
	public String getTrimFile() {
		return trimFile;
	}
	public void setTrimFile(String trimFile) {
		this.trimFile = trimFile;
	}
	
	/**
     * @hibernate.property
     *  column="pad_file"
     *  type="string"
     */		
	public String getPadFile() {
		return padFile;
	}
	public void setPadFile(String padFile) {
		this.padFile = padFile;
	}
	
}
