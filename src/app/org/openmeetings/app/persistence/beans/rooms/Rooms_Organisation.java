package org.openmeetings.app.persistence.beans.rooms;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.persistence.beans.domain.Organisation;


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
@Table(name = "rooms_organisation")
public class Rooms_Organisation implements Serializable {
	
	private static final long serialVersionUID = 4153935045968138984L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="rooms_organisation_id")
	private Long rooms_organisation_id;
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name="rooms_id", nullable=true)
	private Rooms room;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="organisation_id", nullable=true)
	private Organisation organisation;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;

    public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}

	public Long getRooms_organisation_id() {
		return rooms_organisation_id;
	}
	public void setRooms_organisation_id(Long rooms_organisation_id) {
		this.rooms_organisation_id = rooms_organisation_id;
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
