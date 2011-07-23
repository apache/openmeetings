package org.openmeetings.app.persistence.beans.recording;

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



@Entity
@Table(name = "recording_conversion_job")
public class RecordingConversionJob implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1553612596973538373L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="recording_conversion_job_id")
	private long recordingConversionJobId;
	@Column(name="imagenumber")
	private Long imageNumber = 0L;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="recording_id", updatable=true, insertable=true)
	private Recording recording;
	@Column(name="started")
	private Date started;
	//this is only the end Date for the SVG Conversion
	@Column(name="ended")
	private Date ended;
	@Lob
	@Column(name="currentwhiteboardasxml")
	private String currentWhiteBoardAsXml;
	@Column(name="endtimeinmilliseconds")
	private Long endTimeInMilliSeconds;
	@Column(name="startedpngconverted")
	private Date startedPngConverted;
	@Column(name="endpngconverted")
	private Date endPngConverted;
	@Column(name="batchprocesscounter")
	private Long batchProcessCounter = 0L;
	@Column(name="startedswfconverted")
	private Date startedSWFConverted;
	@Column(name="endswfconverted")
	private Date endSWFConverted;
	
	
	public long getRecordingConversionJobId() {
		return recordingConversionJobId;
	}
	public void setRecordingConversionJobId(long recordingConversionJobId) {
		this.recordingConversionJobId = recordingConversionJobId;
	}
	
	public Recording getRecording() {
		return recording;
	}
	public void setRecording(Recording recording) {
		this.recording = recording;
	}
	
	public Long getImageNumber() {
		return imageNumber;
	}
	public void setImageNumber(Long imageNumber) {
		this.imageNumber = imageNumber;
	}
	
	public Date getStarted() {
		return started;
	}
	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getEnded() {
		return ended;
	}
	public void setEnded(Date ended) {
		this.ended = ended;
	}
	
	public String getCurrentWhiteBoardAsXml() {
		return currentWhiteBoardAsXml;
	}
	public void setCurrentWhiteBoardAsXml(String currentWhiteBoardAsXml) {
		this.currentWhiteBoardAsXml = currentWhiteBoardAsXml;
	}
	
	public Long getEndTimeInMilliSeconds() {
		return endTimeInMilliSeconds;
	}
	public void setEndTimeInMilliSeconds(Long endTimeInMilliSeconds) {
		this.endTimeInMilliSeconds = endTimeInMilliSeconds;
	}
	
	public Date getStartedPngConverted() {
		return startedPngConverted;
	}
	public void setStartedPngConverted(Date startedPngConverted) {
		this.startedPngConverted = startedPngConverted;
	}
	
	public Date getEndPngConverted() {
		return endPngConverted;
	}
	public void setEndPngConverted(Date endPngConverted) {
		this.endPngConverted = endPngConverted;
	}
	
	public Long getBatchProcessCounter() {
		return batchProcessCounter;
	}
	public void setBatchProcessCounter(Long batchProcessCounter) {
		this.batchProcessCounter = batchProcessCounter;
	}
	
	public Date getStartedSWFConverted() {
		return startedSWFConverted;
	}
	public void setStartedSWFConverted(Date startedSWFConverted) {
		this.startedSWFConverted = startedSWFConverted;
	}
	
	public Date getEndSWFConverted() {
		return endSWFConverted;
	}
	public void setEndSWFConverted(Date endSWFConverted) {
		this.endSWFConverted = endSWFConverted;
	}
	

}
