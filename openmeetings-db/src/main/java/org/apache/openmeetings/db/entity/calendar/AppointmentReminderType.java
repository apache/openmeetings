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
package org.apache.openmeetings.db.entity.calendar;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;


@Entity
@NamedQueries({
	@NamedQuery(name="getAppointmentReminderTypeById", query="SELECT app FROM AppointmentReminderType app WHERE app.deleted = false AND app.id = :typId")
	, @NamedQuery(name="getAppointmentReminderTypes", query="SELECT a FROM AppointmentReminderType a WHERE a.deleted = false")
})
@Table(name = "appt_reminder_type")
public class AppointmentReminderType implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", nullable=true)
	@ForeignKey(enabled = true)
	private User user;
	
	@Column(name="starttime")
	private Date starttime;
	
	@Column(name="updatetime")
	private Date updatetime;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Column(name="fieldvalues_id")
	@Element(data=true, required=false)
	private Long fieldvalues_id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
}
