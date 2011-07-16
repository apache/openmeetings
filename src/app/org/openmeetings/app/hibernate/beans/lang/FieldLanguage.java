package org.openmeetings.app.hibernate.beans.lang;

import java.io.Serializable;
import java.util.Date;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "fieldlanguage")
public class FieldLanguage implements Serializable {

	private static final long serialVersionUID = 3501643212388395425L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="language_id")
	private Long language_id;
	@Column(name="name")
	private String name;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;
	@Column(name="rtl")
	private Boolean rtl;
	
    public FieldLanguage() {
		super();
	}

	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public Boolean getRtl() {
		return rtl;
	}
	public void setRtl(Boolean rtl) {
		this.rtl = rtl;
	}

	
	

}
