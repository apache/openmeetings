package org.openmeetings.app.hibernate.beans.rooms;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.domain.Organisation;

/**
 * 
 * @hibernate.class table="rooms_organisation"
 * lazy="false"
 *
 */
public class Rooms_Organisation {
	
	private Long rooms_organisation_id;
	private Rooms room;
	private Organisation organisation;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="organisation_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.domain.Organisation"
     *  not-null="false"
     *  outer-join="true"
     */		
    public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="rooms_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.rooms.Rooms"
     *  not-null="false"
     *  outer-join="true"
     */	
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}

    /**
     * 
     * @hibernate.id
     *  column="rooms_organisation_id"
     *  generator-class="increment"
     */  
	public Long getRooms_organisation_id() {
		return rooms_organisation_id;
	}
	public void setRooms_organisation_id(Long rooms_organisation_id) {
		this.rooms_organisation_id = rooms_organisation_id;
	}
    
    /**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
    /**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
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
}
