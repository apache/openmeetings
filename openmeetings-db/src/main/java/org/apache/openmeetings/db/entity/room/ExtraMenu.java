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
package org.apache.openmeetings.db.entity.room;

import static org.apache.openmeetings.db.bind.Constants.EXTRA_MENU_NODE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@NamedQuery(name = "getExtraMenuById", query = "SELECT m FROM ExtraMenu m WHERE m.id = :id")
@NamedQuery(name = "getExtraMenus", query = "SELECT m FROM ExtraMenu m")
@NamedQuery(name = "getExtraMenuByGroups", query = "SELECT m FROM ExtraMenu m WHERE m.groups IS NULL OR m.groups IN (:ids)")
@NamedQuery(name = "countExtraMenus", query = "SELECT COUNT(m) FROM ExtraMenu m")
@Table(name = "extra_menu")
@XmlRootElement(name = EXTRA_MENU_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtraMenu implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "id")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "name")
	private String name;

	@Column(name = "link")
	@XmlElement(name = "link")
	private String link;

	@Column(name = "description")
	@XmlElement(name = "description")
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "group_id", nullable = false)
	@CollectionTable(name = "menu_group", joinColumns = @JoinColumn(name = "menu_id"))
	@XmlElementWrapper(name = "groups", required = false)
	@XmlElement(name = "group", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private List<Long> groups = new ArrayList<>();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Long> getGroups() {
		return groups;
	}

	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}
}
