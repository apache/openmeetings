package org.openmeetings.app.hibernate.beans.basic;

import java.io.Serializable;
import java.util.Date;
import org.openmeetings.app.hibernate.beans.user.Users;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "configuration")
public class Configuration implements Serializable {
	
	private static final long serialVersionUID = -6129473946508963339L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="configuration_id")
	private Long configuration_id;
	@Column(name="conf_key")
	private String conf_key;
	@Column(name="conf_value")
	private String conf_value;	
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="comment_field")
	private String comment;
	@Column(name="deleted")
	private String deleted;
	@Column(name="user_id")
	private Long user_id;

	@Transient
	private Users users;
	
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getConf_key() {
        return conf_key;
    }
    public void setConf_key(String conf_key) {
        this.conf_key = conf_key;
    }
    
    public String getConf_value() {
        return conf_value;
    }
    public void setConf_value(String conf_value) {
        this.conf_value = conf_value;
    }
    
    public Long getConfiguration_id() {
        return configuration_id;
    }
    public void setConfiguration_id(Long configuration_id) {
        this.configuration_id = configuration_id;
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

 public Users getUsers() {
     return users;
 }
 public void setUsers(Users users) {
     this.users = users;
 }

	
}
