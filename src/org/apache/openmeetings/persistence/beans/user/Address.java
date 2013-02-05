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
package org.apache.openmeetings.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "address")
@Root(name="address")
public class Address implements Serializable {

	private static final long serialVersionUID = 1387576041912128161L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long adresses_id;
	
	@Column(name = "additionalname")
	@Element(data=true, required=false)
	private String additionalname;
	
	@Lob
	@Column(name = "comment_field", length=2048)
	@Element(data=true, required=false)
	private String comment;
	
	@Column(name = "fax")
	@Element(data=true, required=false)
	private String fax;
	
	@Column(name = "starttime")
	@Element(data=true, required=false)
	private Date starttime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "state_id")
	@Element(name="state_id", data=true, required=false)
	private State states;
	
	@Column(name = "street")
	@Element(data=true, required=false)
	private String street;
	
	@Column(name = "town")
	@Element(data=true, required=false)
	private String town;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "zip")
	@Element(data=true, required=false)
	private String zip;
	
	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "email")
	@Element(name="mail", data=true, required=false)
	private String email;
	
	@Column(name = "phone")
	@Element(data=true, required=false)
	private String phone;

	public String getAdditionalname() {
		return additionalname;
	}

	public void setAdditionalname(String additionalname) {
		this.additionalname = additionalname;
	}

	public Long getAdresses_id() {
		return adresses_id;
	}

	public void setAdresses_id(Long adresses_id) {
		this.adresses_id = adresses_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public State getStates() {
		return states;
	}

	public void setStates(State states) {
		this.states = states;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
