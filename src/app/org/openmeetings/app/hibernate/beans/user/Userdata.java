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
@Table(name = "userdata")
public class Userdata implements Serializable {
	
	private static final long serialVersionUID = 8365799229794940172L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="data_id")
    private Long data_id;
	@Column(name="user_id")
    private Long user_id;    
	@Column(name="data_key")
    private String data_key;
	@Column(name="data")
    private String data;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "comment_field")
    private String comment;
	@Column(name = "deleted")
    private String deleted;

	public Userdata() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public String getData_key() {
		return data_key;
	}
	public void setData_key(String data_key) {
		this.data_key = data_key;
	}
    
	public Long getData_id() {
		return data_id;
	}
	public void setData_id(Long data_id) {
		this.data_id = data_id;
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

	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}



}
