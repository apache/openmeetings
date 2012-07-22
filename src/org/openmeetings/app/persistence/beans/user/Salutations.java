/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
	private boolean deleted;
	@Column(name="fieldvalues_id")
	private Long fieldvalues_id;
	@Transient
	private Fieldlanguagesvalues label;	

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
	
	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
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
