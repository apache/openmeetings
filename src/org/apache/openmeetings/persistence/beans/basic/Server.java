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
package org.apache.openmeetings.persistence.beans.basic;

import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
		@NamedQuery(name = "getAllServers", query = "SELECT s FROM Server s WHERE s.deleted = false"),
		@NamedQuery(name = "getServerCount", query = "SELECT COUNT(s) FROM Server s WHERE s.deleted = false"),
		@NamedQuery(name = "getServerById", query = "SELECT s FROM Server s LEFT JOIN FETCH s.insertedby LEFT JOIN FETCH s.updatedby WHERE s.deleted = false AND s.id = :id"),
		@NamedQuery(name = "getServerByName", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.address LIKE :name"),
		@NamedQuery(name = "getServerByAddress", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.address LIKE :address"),
		@NamedQuery(name = "getServersWithNoUsers", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.id NOT IN (SELECT u.server.id FROM Users u where u.server.id IS NOT NULL)"),
		@NamedQuery(name = "getServerWithMinimumUsers", query = "SELECT s.id, COUNT(u) AS cnt FROM Users u JOIN u.server s WHERE s.deleted = false GROUP BY s.id ORDER BY cnt") })
@Table(name = "server")
@Root
public class Server implements Serializable, IDataProviderEntity {

	private static final long serialVersionUID = -6822732074549167727L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true)
	private long id = 0;

	@Column(name = "name")
	@Element(data = true)
	private String name;

	@Column(name = "address")
	@Element(data = true)
	private String address;

	@Column(name = "inserted")
	public Date inserted;

	@Column(name = "updated")
	public Date updated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insertedby_id", updatable = true, insertable = true)
	public Users insertedby;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedby_id", updatable = true, insertable = true)
	public Users updatedby;

	@Lob
	@Column(name = "comment_field", length = 2048)
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "last_ping", nullable = true)
	@Element(data = true, required = false)
	private Calendar lastPing;
	
	@Column(name = "deleted", nullable = false)
	@Element(data = true)
	private boolean deleted = false;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Users getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(Users insertedby) {
		this.insertedby = insertedby;
	}

	public Users getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(Users updatedby) {
		this.updatedby = updatedby;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Calendar getLastPing() {
		return lastPing;
	}

	public void setLastPing(Calendar lastPing) {
		this.lastPing = lastPing;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", name=" + name + ", address=" + address
				+ ", deleted=" + deleted + "]";
	}
}
