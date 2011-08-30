package org.openmeetings.app.persistence.beans.basic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "errortypes")
public class ErrorType implements Serializable {
	private static final long serialVersionUID = 1519570470483604258L;

	@Id
	@Column(name = "errortype_id")
	private Long errortype_id;

	@Column(name = "fieldvalues_id")
	private Long fieldvalues_id;

	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;

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

	public Long getFieldvalues_id() {
		return this.fieldvalues_id;
	}

	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

}
