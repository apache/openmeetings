package org.openmeetings.app.hibernate.beans.rooms;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="rooms_moderator"
 * lazy="false"
 *
 */
public class RoomModerators {
	
	private long roomModeratorsId;
	private Long roomId;
	private Boolean isSuperModerator;
	private Users user;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	/**
     * 
     * @hibernate.id
     *  column="room_moderators_id"
     *  generator-class="increment"
     */ 
	public long getRoomModeratorsId() {
		return roomModeratorsId;
	}
	public void setRoomModeratorsId(long roomModeratorsId) {
		this.roomModeratorsId = roomModeratorsId;
	}
	
	/**
     * @hibernate.property
     *  column="is_supermoderator"
     *  type="boolean"
     */
	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}
	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="user_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
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
	
	/**
     * @hibernate.property
     *  column="roomId"
     *  type="long"
     */
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	
}
