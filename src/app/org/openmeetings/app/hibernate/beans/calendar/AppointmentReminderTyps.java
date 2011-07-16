package org.openmeetings.app.hibernate.beans.calendar;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.hibernate.beans.user.Users;


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
@Table(name = "appointmentremindertyps")
public class AppointmentReminderTyps implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6543593995706839669L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="typ_id")
	private Long typId;
	@Column(name="name")
	private String name;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", nullable=true)
	private Users user;
	
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;
	@Column(name="comment_field")
	private String comment;
	
	public Long getTypId() {
		return typId;
	}
	public void setTypId(Long typId) {
		this.typId = typId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	

}
