package org.openmeetings.app.hibernate.beans.flvrecord;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "flvrecording")
public class FlvRecording implements Serializable {
	
	private static final long serialVersionUID = -2234874663310617072L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="flvrecording_id")
	private long flvRecordingId;
	
	@Column(name="filename")
	private String fileName;
	@Column(name="alternate_download")
	private String alternateDownload;
	@Column(name="filehash")
	private String fileHash;
	@Column(name="comment_field")
	private String comment;
	
	@Column(name="parent_fileexploreritem_id")
	private Long parentFileExplorerItemId;
	@Column(name="room_id")
	private Long room_id;
	@Column(name="owner_id")
	private Long ownerId;//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	//maybe are also in a Home directory but just because their parent is
	
	@Column(name="is_folder")
	private Boolean isFolder;
	@Column(name="is_image")
	private Boolean isImage;
	@Column(name="is_presentation")
	private Boolean isPresentation;
	@Column(name="is_recording")
	private Boolean isRecording;
	
	@Column(name="record_start")
	private Date recordStart;
	@Column(name="record_end")
	private Date recordEnd;
	
	@Column(name="inserted_by")
	private Long insertedBy;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="updated")
	private Date updated;
	@Column(name="deleted")
	private String deleted;
	
	@Column(name="width")
	private Integer width;
	@Column(name="height")
	private Integer height;
	
	@Column(name="flv_width")
	private Integer flvWidth;
	@Column(name="flv_height")
	private Integer flvHeight;
	@Column(name="preview_image")
	private String previewImage;
	
	@Column(name="filesize")
	private Long fileSize;
	
	@Column(name="recorder_stream_id")
	private String recorderStreamId;
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="is_interview")
	private Boolean isInterview;
	@Column(name="progress_post_processing")
	private Integer progressPostProcessing;
	
	//Not Mapped
	@Transient
	private List<FlvRecordingMetaData> flvRecordingMetaData;
	@Transient
	private Users creator;
	@Transient
	private Rooms room;
	@Transient
	private List<FlvRecordingLog> flvRecordingLog;
	
	public long getFlvRecordingId() {
		return flvRecordingId;
	}
	public void setFlvRecordingId(long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getFileHash() {
		return fileHash;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Long getParentFileExplorerItemId() {
		return parentFileExplorerItemId;
	}
	public void setParentFileExplorerItemId(Long parentFileExplorerItemId) {
		this.parentFileExplorerItemId = parentFileExplorerItemId;
	}

	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}
	
	public Boolean getIsImage() {
		return isImage;
	}
	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}
	
	public Boolean getIsPresentation() {
		return isPresentation;
	}
	public void setIsPresentation(Boolean isPresentation) {
		this.isPresentation = isPresentation;
	}

	public Boolean getIsRecording() {
		return isRecording;
	}
	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public Long getInsertedBy() {
		return insertedBy;
	}
	public void setInsertedBy(Long insertedBy) {
		this.insertedBy = insertedBy;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(Date recordStart) {
		this.recordStart = recordStart;
	}

	public Date getRecordEnd() {
		return recordEnd;
	}
	public void setRecordEnd(Date recordEnd) {
		this.recordEnd = recordEnd;
	}
	
	public String getRecorderStreamId() {
		return recorderStreamId;
	}
	public void setRecorderStreamId(String recorderStreamId) {
		this.recorderStreamId = recorderStreamId;
	}
	
	public Long getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaData() {
		return flvRecordingMetaData;
	}
	public void setFlvRecordingMetaData(
			List<FlvRecordingMetaData> flvRecordingMetaData) {
		this.flvRecordingMetaData = flvRecordingMetaData;
	}
	
	public Users getCreator() {
		return creator;
	}
	public void setCreator(Users creator) {
		this.creator = creator;
	}
	
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}
	
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	
	public Integer getFlvWidth() {
		return flvWidth;
	}
	public void setFlvWidth(Integer flvWidth) {
		this.flvWidth = flvWidth;
	}
	
	public Integer getFlvHeight() {
		return flvHeight;
	}
	public void setFlvHeight(Integer flvHeight) {
		this.flvHeight = flvHeight;
	}

	public String getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	
	public String getAlternateDownload() {
		return alternateDownload;
	}
	public void setAlternateDownload(String alternateDownload) {
		this.alternateDownload = alternateDownload;
	}

	public List<FlvRecordingLog> getFlvRecordingLog() {
		return flvRecordingLog;
	}
	public void setFlvRecordingLog(List<FlvRecordingLog> flvRecordingLog) {
		this.flvRecordingLog = flvRecordingLog;
	}
	
	public Boolean getIsInterview() {
		return isInterview;
	}
	public void setIsInterview(Boolean isInterview) {
		this.isInterview = isInterview;
	}

	public Integer getProgressPostProcessing() {
		return progressPostProcessing;
	}
	public void setProgressPostProcessing(Integer progressPostProcessing) {
		this.progressPostProcessing = progressPostProcessing;
	}
	
	
}