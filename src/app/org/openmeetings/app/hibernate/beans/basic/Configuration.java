package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;
import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="configuration"
 *
 */
public class Configuration {
	
	private Long configuration_id;
	private String conf_key;
	private String conf_value;	
	private Date starttime;
	private Date updatetime;
	private String comment;
	private String deleted;
	private Long user_id;
	
	private Users users;
	
	public Configuration() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    /**
     * @hibernate.property
*  column="comment_field"
     *  type="string"
     */  
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * @hibernate.property
     *  column="conf_key"
     *  type="string"
     */     
    public String getConf_key() {
        return conf_key;
    }
    public void setConf_key(String conf_key) {
        this.conf_key = conf_key;
    }
    
    /**
     * @hibernate.property
     *  column="conf_value"
     *  type="string"
     */     
    public String getConf_value() {
        return conf_value;
    }
    public void setConf_value(String conf_value) {
        this.conf_value = conf_value;
    }
    
    /**
     * 
     * @hibernate.id
     *  column="configuration_id"
     *  generator-class="increment"
     */     
    public Long getConfiguration_id() {
        return configuration_id;
    }
    public void setConfiguration_id(Long configuration_id) {
        this.configuration_id = configuration_id;
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
     *  column="user_id"
     *  type="long"
     */	
    public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	//    /**
//     * @hibernate.many-to-one
//     *  cascade="none"
//     *  column="user_id"
//     *  lazy="false"
//     *  class="org.openmeetings.app.hibernate.beans.user.Users"
//     *  not-null="false"
//     *  outer-join="true"
//     */     
    public Users getUsers() {
        return users;
    }
    public void setUsers(Users users) {
        this.users = users;
    }

	
}
