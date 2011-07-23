package org.openmeetings.app.persistence.beans.lang;

import java.io.Serializable;
import java.util.Date;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "fieldlanguagesvalues")
public class Fieldlanguagesvalues implements Serializable {

	private static final long serialVersionUID = 1965055047163639210L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="fieldlanguagesvalues_id")
	private Long fieldlanguagesvalues_id;
	@Column(name="fieldvalues_id")
	private Long fieldvalues_id;
	@Column(name="language_id")
	private Long language_id;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;
	@Lob
	@Column(name="value")
	private String value;
	
	public Fieldlanguagesvalues() {
		super();
	}
	
	public Long getFieldlanguagesvalues_id() {
		return fieldlanguagesvalues_id;
	}
	public void setFieldlanguagesvalues_id(Long fieldlanguagesvalues_id) {
		this.fieldlanguagesvalues_id = fieldlanguagesvalues_id;
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
	
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
