package org.openmeetings.app.hibernate.beans.files;

import java.util.Date;

/**
 * 
 * @hibernate.class table="fileexploreritem"
 * lazy="false"
 *
 */
public class FileExplorerItem {
	
	private long fileExplorerItemId;
	private String fileName;
	private String fileHash;
	private Long parentFileExplorerItemId;
	private Long room_id;
	private Long ownerId;//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	//maybe are also in a Home directory but just because their parent is
	
	private Boolean isFolder;
	private Boolean isImage;
	private Boolean isPresentation;
	private Boolean isVideo;
	
	private Long insertedBy;
	private Date inserted;
	private Date updated;
	private String deleted;
	
	private Long fileSize;
	
	private Integer flvWidth;
	private Integer flvHeight;
	private String previewImage;
	
	private String wmlFilePath;
	private Boolean isStoredWmlFile;

		
	/**
	 *
	 * @hibernate.id
	 *  column="fileexploreritem_id"
	 *  generator-class="increment"
	 */
	public long getFileExplorerItemId() {
		return fileExplorerItemId;
	}
	public void setFileExplorerItemId(long fileExplorerItemId) {
		this.fileExplorerItemId = fileExplorerItemId;
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
     *  column="is_video"
     *  type="boolean"
     */
	public Boolean getIsVideo() {
		return isVideo;
	}
	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
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
     *  column="flv_width"
     *  type="int"
     */ 
	public Integer getFlvWidth() {
		return flvWidth;
	}
	public void setFlvWidth(Integer flvWidth) {
		this.flvWidth = flvWidth;
	}

	/**
     * @hibernate.property
     *  column="flv_height"
     *  type="int"
     */
	public Integer getFlvHeight() {
		return flvHeight;
	}
	public void setFlvHeight(Integer flvHeight) {
		this.flvHeight = flvHeight;
	}

	/**
     * @hibernate.property
     *  column="preview_image"
     *  type="string"
     */	
	public String getPreviewImage() {
		return previewImage;
	}	
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	
	/**
     * @hibernate.property
     *  column="wml_file_path"
     *  type="string"
     */
	public String getWmlFilePath() {
		return wmlFilePath;
	}
	public void setWmlFilePath(String wmlFilePath) {
		this.wmlFilePath = wmlFilePath;
	}
	
	/**
     * @hibernate.property
     *  column="is_stored_wml_file"
     *  type="boolean"
     */
	public Boolean getIsStoredWmlFile() {
		return isStoredWmlFile;
	}
	public void setIsStoredWmlFile(Boolean isStoredWmlFile) {
		this.isStoredWmlFile = isStoredWmlFile;
	}
	
}
