package org.openmeetings.app.hibernate.beans.user;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "private_messages_folder")
public class PrivateMessageFolder implements Serializable {
	
	private static final long serialVersionUID = 3689814412815025816L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="private_message_folder_id")
	private long privateMessageFolderId;
	@Column(name="folder_name")
	private String folderName;
	@Column(name="user_id")
	private Long userId;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="updated")
	private Date updated;
	
	public long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
	}
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}
