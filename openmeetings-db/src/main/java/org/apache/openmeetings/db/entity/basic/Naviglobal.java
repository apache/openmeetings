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
package org.apache.openmeetings.db.entity.basic;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@NamedQueries({
		@NamedQuery(name = "getNavigation", query = "SELECT DISTINCT ng from Naviglobal ng JOIN ng.mainnavi nm "
				+ "WHERE nm.deleted = false AND ng.levelId <= :levelId AND nm.levelId <= :levelId "
				+ "AND ng.deleted = false ORDER BY ng.naviorder ASC"),
		@NamedQuery(name = "getNavigationById", query = "SELECT ng from Naviglobal ng WHERE ng.id = :id") })
@Table(name = "naviglobal")
public class Naviglobal implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "icon")
	private String icon;

	@Column(name = "isleaf")
	private Boolean isleaf;

	@Column(name = "isopen")
	private Boolean isopen;

	@Column(name = "action")
	private String action;

	@Column(name = "updated")
	private Date updated;

	@Column(name = "inserted")
	private Date inserted;

	@Column(name = "comment_field")
	private String comment;

	@Column(name = "naviorder")
	private Integer naviorder;

	@Column(name = "level_id")
	private Long levelId;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "label_id")
	private Long labelId;

	@Column(name = "tooltip_label_id")
	private Long tooltipLabelId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "global_id")
	@ForeignKey(enabled = true)
	@OrderBy("naviorder")
	private List<Navimain> mainnavi;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(Boolean isleaf) {
		this.isleaf = isleaf;
	}

	public Boolean getIsopen() {
		return isopen;
	}

	public void setIsopen(Boolean isopen) {
		this.isopen = isopen;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNaviorder() {
		return naviorder;
	}

	public void setNaviorder(Integer naviorder) {
		this.naviorder = naviorder;
	}

	public Long getLevelId() {
		return levelId;
	}

	public void setLevelId(Long levelId) {
		this.levelId = levelId;
	}

	public List<Navimain> getMainnavi() {
		return mainnavi;
	}

	public void setMainnavi(List<Navimain> mainnavi) {
		this.mainnavi = mainnavi;
	}

	public Long getLabelId() {
		return labelId;
	}

	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}

	public Long getTooltipLabelId() {
		return tooltipLabelId;
	}

	public void setTooltipLabelId(Long tooltipLabelId) {
		this.tooltipLabelId = tooltipLabelId;
	}

	@Override
	public String toString() {
		return "Naviglobal [id=" + id + ", name=" + name + ", action=" + action + ", naviorder=" + naviorder + ", deleted="
				+ deleted + ", labelId=" + labelId + ", tooltipLabelId=" + tooltipLabelId + "]";
	}

}
