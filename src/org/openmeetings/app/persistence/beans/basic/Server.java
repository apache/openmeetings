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
package org.openmeetings.app.persistence.beans.basic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name="getAllServers", query="SELECT s FROM Server s WHERE s.deleted = false")
	, @NamedQuery(name="getServerCount", query="SELECT COUNT(s) FROM Server s WHERE s.deleted = false")
	, @NamedQuery(name="getServerById", query="SELECT s FROM Server s WHERE s.deleted = false AND s.id = :id")
	, @NamedQuery(name="getServersWithNoUsers"
		, query="SELECT s FROM Server s WHERE s.deleted = false AND s.id NOT IN (SELECT u.server.id FROM Users u where u.server.id IS NOT NULL)")
	, @NamedQuery(name="getServerWithMinimumUsers"
		, query="SELECT s.id, COUNT(u) AS cnt FROM Users u JOIN u.server s WHERE s.deleted = false GROUP BY s.id ORDER BY cnt")
})
@Table(name = "server")
public class Server implements Serializable {
	private static final long serialVersionUID = -6822732074549167727L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="name")
	private String name;
	@Column(name="address")
	private String address;
	@Column(name="deleted", nullable=false)
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

	@Override
	public String toString() {
		return "Server [id=" + id + ", name=" + name + ", address=" + address
				+ ", deleted=" + deleted + "]";
	}
}
