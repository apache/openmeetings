package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="private_messages_folder"
 * lazy="false"
 *
 */
public class PrivateMessageFolder {
	
	private long privateMessageFolderId;
	private String folderName;
	private Long userId;
	private Date inserted;
	private Date updated;
	
	/**
     * 
     * @hibernate.id
     *  column="private_message_folder_id"
     *  generator-class="increment"
     */
	public long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
	}
	
	/**
     * @hibernate.property
     *  column="folder_name"
     *  type="string"
     */
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	/**
     * @hibernate.property
     *  column="user_id"
     *  type="long"
     */
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
     *  column="updated"
     *  type="java.util.Date"
     */
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}
