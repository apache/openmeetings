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
package org.apache.openmeetings.db.dto.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.entity.calendar.AppointmentCategory;

public class AppointmentCategoryDTO {
	private Long id;
	private String name;
	private Date inserted;
	private Date updated;
	private boolean deleted;
	private String comment;
	
	public AppointmentCategoryDTO() {}
	
	public AppointmentCategoryDTO(AppointmentCategory c) {
		this.id = c.getCategoryId();
		this.name = c.getName();
		this.inserted = c.getStarttime();
		this.updated = c.getUpdatetime();
		this.deleted = c.getDeleted();
		this.comment = c.getComment();
	}

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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public static List<AppointmentCategoryDTO> list(List<AppointmentCategory> l) {
		List<AppointmentCategoryDTO> rList = new ArrayList<>();
		if (l != null) {
			for (AppointmentCategory c : l) {
				rList.add(new AppointmentCategoryDTO(c));
			}
		}
		return rList;
	}
}
