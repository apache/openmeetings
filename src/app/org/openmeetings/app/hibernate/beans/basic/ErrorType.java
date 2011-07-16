package org.openmeetings.app.hibernate.beans.basic;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "errortypes")
public class ErrorType implements Serializable {
	 
	private static final long serialVersionUID = 8014699369591094542L;

	@Id
	
	@Column(name="errortype_id")
	private Long errortype_id;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="fieldvalues_id", updatable=false, insertable=false)
	private Fieldvalues fieldvalues;

	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;

	@Column(name="fieldvalues_id")
	private Long fieldvalues_id;
	
	public Long getErrortype_id() {
		return errortype_id;
	}
	public void setErrortype_id(Long errortype_id) {
		this.errortype_id = errortype_id;
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

	public Fieldvalues getFieldvalues() {
		return fieldvalues;
	}
	public void setFieldvalues(Fieldvalues fieldvalues) {
		this.fieldvalues = fieldvalues;
	}
	
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}


}
