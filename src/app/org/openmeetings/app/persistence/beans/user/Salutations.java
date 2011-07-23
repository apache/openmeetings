package org.openmeetings.app.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "salutations")
public class Salutations implements Serializable {
	
	private static final long serialVersionUID = -5806449519074435223L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="salutations_id")
	private Long salutations_id;
	@Column(name="name")
	private String name;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;
	@Column(name="fieldvalues_id")
	private Long fieldvalues_id;
	@Transient
	private Fieldlanguagesvalues label;	
	public Salutations() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
	public Long getSalutations_id() {
		return salutations_id;
	}
	public void setSalutations_id(Long salutations_id) {
		this.salutations_id = salutations_id;
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

	public Fieldlanguagesvalues getLabel() {
		return label;
	}

	public void setLabel(Fieldlanguagesvalues label) {
		this.label = label;
	}	
	
}
