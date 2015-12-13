/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.persistence.beans.files;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="getAllFiles", query="SELECT c FROM FileExplorerItem c")
    , @NamedQuery(name="getById", query="SELECT c FROM FileExplorerItem c WHERE c.fileExplorerItemId = :fileExplorerItemId")
    , @NamedQuery(name="getByHash", query="SELECT c FROM FileExplorerItem c WHERE c.fileHash = :fileHash")
})
@Table(name = "fileexploreritem")
@Root
public class FileExplorerItem implements Serializable {
	private static final long serialVersionUID = 242843782142620566L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private long fileExplorerItemId;
	
	@Column(name="filename")
	@Element(data=true, required = false)
	private String fileName;
	
	@Column(name="filehash")
	@Element(data=true, required = false)
	private String fileHash;
	
	@Column(name="parent_fileexploreritem_id")
	@Element(data=true)
	private Long parentFileExplorerItemId;
	
	@Column(name="room_id")
	@Element(data=true, required=false)
	private Long room_id;
	
	@Column(name="owner_id")
	@Element(data=true, required=false)
	private Long ownerId;//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	//maybe are also in a Home directory but just because their parent is
	
	@Column(name="is_folder")
	@Element(data=true)
	private Boolean isFolder;
	
	@Column(name="is_image")
	@Element(data=true)
	private Boolean isImage;
	
	@Column(name="is_presentation")
	@Element(data=true)
	private Boolean isPresentation;
	
	@Column(name="is_video")
	@Element(data=true, required=false)
	private Boolean isVideo;
	
	@Column(name="inserted_by")
	@Element(data=true)
	private Long insertedBy;
	
	@Column(name="inserted")
	@Element(data=true)
	private Date inserted;
	
	@Column(name="updated")
	@Element(data=true)
	private Date updated;
	
	@Column(name="deleted")
	@Element(data=true)
	private boolean deleted;
	
	@Column(name="filesize")
	@Element(data=true, required=false)
	private Long fileSize;
	
	@Column(name="flv_width")
	@Element(data=true, required=false)
	private Integer flvWidth;
	
	@Column(name="flv_height")
	@Element(data=true, required=false)
	private Integer flvHeight;
	
	@Column(name="preview_image")
	@Element(data=true, required = false)
	private String previewImage;
	
	@Column(name="wml_file_path")
	@Element(data=true, required = false)
	private String wmlFilePath;
	
	@Column(name="is_stored_wml_file")
	@Element(data=true, required = false)
	private Boolean isStoredWmlFile;
	
	@Column(name="is_chart")
	@Element(data=true, required = false)
    private Boolean isChart;
    
	@Column(name="external_file_id")
    private Long externalFileId;
	
	@Column(name="external_type")
    private String externalType;

		
	public long getFileExplorerItemId() {
		return fileExplorerItemId;
	}
	public void setFileExplorerItemId(long fileExplorerItemId) {
		this.fileExplorerItemId = fileExplorerItemId;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
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
	
	public Boolean getIsVideo() {
		return isVideo;
	}
	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	public Boolean getIsPresentation() {
		return isPresentation;
	}
	public void setIsPresentation(Boolean isPresentation) {
		this.isPresentation = isPresentation;
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

	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
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
	
	public String getWmlFilePath() {
		return wmlFilePath;
	}
	public void setWmlFilePath(String wmlFilePath) {
		this.wmlFilePath = wmlFilePath;
	}
	
	public Boolean getIsStoredWmlFile() {
		return isStoredWmlFile;
	}
	public void setIsStoredWmlFile(Boolean isStoredWmlFile) {
		this.isStoredWmlFile = isStoredWmlFile;
	}

	public Boolean getIsChart() {
        return isChart;
    }
    public void setIsChart(Boolean isChart) {
        this.isChart = isChart;
    }
    
	public Long getExternalFileId() {
		return externalFileId;
	}
	public void setExternalFileId(Long externalFileId) {
		this.externalFileId = externalFileId;
	}
	
	public String getExternalType() {
		return externalType;
	}
	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}
	
}
