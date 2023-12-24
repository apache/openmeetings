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

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.user.User;

@XmlRootElement
public class UserSearchResult {
	private String objectName;
	private Long records;
	private List<UserDTO> result;
	private String errorKey;

	public UserSearchResult() {
		//def constructor
	}

	public UserSearchResult(SearchResult<User> copy) {
		this.objectName = copy.getObjectName();
		this.records = copy.getRecords();
		this.result = new ArrayList<>(copy.getResult().size());
		for (User u : copy.getResult()) {
			result.add(new UserDTO(u));
		}
		this.errorKey = copy.getErrorKey();
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Long getRecords() {
		return records;
	}

	public void setRecords(Long records) {
		this.records = records;
	}

	public List<UserDTO> getResult() {
		return result;
	}

	public void setResult(List<UserDTO> result) {
		this.result = result;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
}
