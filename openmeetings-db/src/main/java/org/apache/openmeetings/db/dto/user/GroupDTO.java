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
package org.apache.openmeetings.db.dto.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.user.Group;

@XmlRootElement
public class GroupDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String tag;

	public GroupDTO() {
		//def constructor
	}

	public GroupDTO(Group g) {
		id = g.getId();
		name = g.getName();
		tag = g.getTag();
	}

	public static List<GroupDTO> list(List<Group> l) {
		List<GroupDTO> gList = new ArrayList<>();
		if (l != null) {
			for (Group g : l) {
				gList.add(new GroupDTO(g));
			}
		}
		return gList;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
