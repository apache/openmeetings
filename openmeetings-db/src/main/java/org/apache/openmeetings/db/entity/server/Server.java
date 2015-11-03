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
package org.apache.openmeetings.db.entity.server;

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

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
		@NamedQuery(name = "getAllServers", query = "SELECT s FROM Server s WHERE s.deleted = false ORDER BY s.id"),
		@NamedQuery(name = "getServerCount", query = "SELECT COUNT(s) FROM Server s WHERE s.deleted = false"),
		@NamedQuery(name = "getServerById", query = "SELECT s FROM Server s LEFT JOIN FETCH s.insertedby LEFT JOIN FETCH s.updatedby WHERE s.deleted = false AND s.id = :id"),
		@NamedQuery(name = "getServerByName", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.name LIKE :name"),
		@NamedQuery(name = "getServerByAddress", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.address LIKE :address"),
		@NamedQuery(name = "getServersWithNoUsers", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.id NOT IN (SELECT u.server.id FROM User u where u.server.id IS NOT NULL)"),
		@NamedQuery(name = "getServerWithMinimumUsers", query = "SELECT s.id, COUNT(u) AS cnt FROM User u JOIN u.server s WHERE s.deleted = false GROUP BY s.id ORDER BY cnt"),
		@NamedQuery(name = "getActiveServers", query = "SELECT s FROM Server s WHERE s.deleted = false AND s.active = true") //
})
@Table(name = "server")
@Root
public class Server implements Serializable, IDataProviderEntity {

	private static final long serialVersionUID = 1L;

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
	@ForeignKey(enabled = true)
	public User insertedby;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedby_id", updatable = true, insertable = true)
	@ForeignKey(enabled = true)
	public User updatedby;

	@Lob
	@Column(name = "comment_field", length = 2048)
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "last_ping", nullable = true)
	@Element(data = true, required = false)
	private Calendar lastPing;
	
	@Column(name = "port", nullable = true)
	@Element(data = true, required = false)
	private int port;

	@Column(name = "protocol", nullable = true)
	@Element(data = true, required = false)
	private String protocol;

	@Column(name = "webapp", nullable = true)
	@Element(data = true, required = false)
	private String webapp;

	@Column(name = "login", nullable = true)
	@Element(data = true, required = false)
	private String user;

	@Column(name = "pass", nullable = true)
	@Element(data = true, required = false)
	private String pass;

	@Column(name = "active", nullable = true)
	@Element(data = true, required = false)
	private boolean active;
	
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

	public User getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(User insertedby) {
		this.insertedby = insertedby;
	}

	public User getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(User updatedby) {
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getWebapp() {
		return webapp;
	}

	public void setWebapp(String webapp) {
		this.webapp = webapp;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", name=" + name + ", address=" + address
				+ ", port=" + port + ", user=" + user + ", pass=" + pass
				+ ", protocol=" + protocol 
				+ ", active=" + active + ", webapp=" + webapp + ", deleted="
				+ deleted + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Server other = (Server) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
