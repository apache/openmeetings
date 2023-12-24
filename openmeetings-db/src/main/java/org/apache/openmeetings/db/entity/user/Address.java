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
package org.apache.openmeetings.db.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import org.apache.openmeetings.db.entity.HistoricalEntity;

@Entity
@Table(name = "address", indexes = {
		@Index(name = "email_idx", columnList = "email")
})
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@Column(name = "additionalname")
	@XmlElement(name = "additionalname", required = false)
	private String additionalname;

	@Lob
	@Column(name = "comment", length = 2048)
	@XmlElement(name = "comment", required = false)
	private String comment;

	@Column(name = "fax")
	@XmlElement(name = "fax", required = false)
	private String fax;

	@Column(name = "country")
	@XmlElement(name = "country", required = false)
	private String country;

	@Column(name = "street")
	@XmlElement(name = "street", required = false)
	private String street;

	@Column(name = "town")
	@XmlElement(name = "town", required = false)
	private String town;

	@Column(name = "zip")
	@XmlElement(name = "zip", required = false)
	private String zip;

	@Column(name = "email")
	@XmlElement(name = "mail", required = false)
	private String email;

	@Column(name = "phone")
	@XmlElement(name = "phone", required = false)
	private String phone;

	public String getAdditionalname() {
		return additionalname;
	}

	public void setAdditionalname(String additionalname) {
		this.additionalname = additionalname;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
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

	@Override
	public String toString() {
		return "Address [id=" + id + ", country=" + country + ", street=" + street + ", town=" + town + ", zip=" + zip + ", deleted="
				+ isDeleted() + ", email=" + email + ", phone=" + phone + "]";
	}
}
