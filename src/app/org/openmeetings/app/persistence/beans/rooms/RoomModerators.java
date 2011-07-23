package org.openmeetings.app.persistence.beans.rooms;

import java.io.Serializable;
import java.util.Date;


import org.openmeetings.app.persistence.beans.user.Users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "rooms_moderator")
public class RoomModerators implements Serializable {
	
	private static final long serialVersionUID = 5407758673591515018L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="room_moderators_id")
	private long roomModeratorsId;
	@Column(name = "roomId")
	private Long roomId;
	@Column(name="is_supermoderator")
	private Boolean isSuperModerator;
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn (name="user_id")
	private Users user;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;
	
	public long getRoomModeratorsId() {
		return roomModeratorsId;
	}
	public void setRoomModeratorsId(long roomModeratorsId) {
		this.roomModeratorsId = roomModeratorsId;
	}
	
	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}
	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}
	
	
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	
}
