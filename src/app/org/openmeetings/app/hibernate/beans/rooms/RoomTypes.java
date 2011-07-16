package org.openmeetings.app.hibernate.beans.rooms;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roomtypes")
public class RoomTypes implements Serializable {

	private static final long serialVersionUID = -4441029424324591356L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="roomtypes_id")
	private Long roomtypes_id;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name="name")
	private String name;
	@Column(name = "deleted")
	private String deleted;

    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
	public Long getRoomtypes_id() {
		return roomtypes_id;
	}
	public void setRoomtypes_id(Long roomtypes_id) {
		this.roomtypes_id = roomtypes_id;
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
}
