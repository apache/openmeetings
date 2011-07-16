package org.openmeetings.app.hibernate.beans.adresses;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "states")
public class States implements Serializable {
	
	private static final long serialVersionUID = -1629546369219451403L;
	@Id
	@GeneratedValue
    
	@Column(name="state_id")
 	private Long state_id;
	@Column(name="name")
	private String name;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;

	public States() {
		super();
		// TODO Auto-generated constructor stub
	}

    /**
     * @hibernate.property
     *  column="name"
     *  type="string"
     */ 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getState_id() {
		return state_id;
	}
	public void setState_id(Long state_id) {
		this.state_id = state_id;
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
