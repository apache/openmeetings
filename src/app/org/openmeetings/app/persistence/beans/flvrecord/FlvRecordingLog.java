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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 
 * @hibernate.class table="flvrecording_log"
 * lazy="false"
 *
 */
@Entity
@Table(name = "flvrecording_log")
public class FlvRecordingLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2577533628675416706L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="flvrecording_log_id")
	private long flvRecordingLogId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="flvrecording_id", nullable=true)
	private FlvRecording flvRecording;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="msg_type")
	private String msgType;
	@Lob
	@Column(name="ful_message")
	private String fullMessage;
	@Column(name="exit_value")
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
