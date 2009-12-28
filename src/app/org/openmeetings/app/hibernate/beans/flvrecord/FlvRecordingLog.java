package org.openmeetings.app.hibernate.beans.flvrecord;

import java.util.Date;

/**
 * 
 * @hibernate.class table="flvrecording_log"
 * lazy="false"
 *
 */
public class FlvRecordingLog {

	private long flvRecordingLogId;
	private FlvRecording flvRecording;
	private Date inserted;
	private String msgType;
	private String fullMessage;
	private String exitValue;
	
	/**
	 *
	 * @hibernate.id
	 *  column="flvrecording_log_id"
	 *  generator-class="increment"
	 */
	public long getFlvRecordingLogId() {
		return flvRecordingLogId;
	}
	public void setFlvRecordingLogId(long flvRecordingLogId) {
		this.flvRecordingLogId = flvRecordingLogId;
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
     *  column="msg_type"
     *  type="string"
     */
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	/**
     * @hibernate.property
     *  column="ful_message"
     *  type="text"
     */
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}
	
	/**
     * @hibernate.property
     *  column="exit_value"
     *  type="string"
     */
	public String getExitValue() {
		return exitValue;
	}
	public void setExitValue(String exitValue) {
		this.exitValue = exitValue;
	}
	
}
