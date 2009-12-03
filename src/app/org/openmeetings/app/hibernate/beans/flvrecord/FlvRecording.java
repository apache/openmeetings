package org.openmeetings.app.hibernate.beans.flvrecord;

import java.util.Date;

/**
 * 
 * @hibernate.class table="flvrecording"
 * lazy="false"
 *
 */
public class FlvRecording {
	
	private long flvRecordingId;
	
	private String fileName;
	private String fileHash;
	private String comment;
	
	private Long parentFileExplorerItemId;
	private Long room_id;
	private Long ownerId;//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	//maybe are also in a Home directory but just because their parent is
	
	private Boolean isFolder;
	private Boolean isImage;
	private Boolean isPresentation;
	private Boolean isRecording;
	
	private Date recordStart;
	private Date recordEnd;
	
	private Long insertedBy;
	private Date inserted;
	private Date updated;
	private String deleted;
	
	private Long fileSize;
	
	private String recorderStreamId;

	/**
	 *
	 * @hibernate.id
	 *  column="flvrecording_id"
	 *  generator-class="increment"
	 */
	public long getFlvRecordingId() {
		return flvRecordingId;
	}
	public void setFlvRecordingId(long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	/**
     * @hibernate.property
     *  column="filename"
     *  type="string"
     */
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
     * @hibernate.property
     *  column="comment"
     *  type="string"
     */
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
     * @hibernate.property
     *  column="filehash"
     *  type="string"
     */
	public String getFileHash() {
		return fileHash;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	/**
     * @hibernate.property
     *  column="parent_fileexploreritem_id"
     *  type="long"
     */
	public Long getParentFileExplorerItemId() {
		return parentFileExplorerItemId;
	}
	public void setParentFileExplorerItemId(Long parentFileExplorerItemId) {
		this.parentFileExplorerItemId = parentFileExplorerItemId;
	}

	/**
     * @hibernate.property
     *  column="room_id"
     *  type="long"
     */
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	/**
     * @hibernate.property
     *  column="owner_id"
     *  type="long"
     */
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	/**
     * @hibernate.property
     *  column="is_folder"
     *  type="boolean"
     */
	public Boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}
	
	/**
     * @hibernate.property
     *  column="is_image"
     *  type="boolean"
     */
	public Boolean getIsImage() {
		return isImage;
	}
	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}
	
	/**
     * @hibernate.property
     *  column="is_presentation"
     *  type="boolean"
     */
	public Boolean getIsPresentation() {
		return isPresentation;
	}
	public void setIsPresentation(Boolean isPresentation) {
		this.isPresentation = isPresentation;
	}

	/**
     * @hibernate.property
     *  column="is_recording"
     *  type="boolean"
     */
	public Boolean getIsRecording() {
		return isRecording;
	}
	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
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
     *  column="filesize"
     *  type="long"
     */ 
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
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
     *  column="recorder_stream_id"
     *  type="string"
     */
	public String getRecorderStreamId() {
		return recorderStreamId;
	}
	public void setRecorderStreamId(String recorderStreamId) {
		this.recorderStreamId = recorderStreamId;
	}
	
	
	
	
}