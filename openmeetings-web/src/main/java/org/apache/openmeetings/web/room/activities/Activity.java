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
package org.apache.openmeetings.web.room.activities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Type { //TODO maybe additional type is not necessary
		roomEnter
		, roomExit
		, askModeration //TODO check
	}
	private String uid;
	private Long sender;
	private Date created;
	private Type type;
	
	public Activity(Long sender, Type type) {
		this.uid = UUID.randomUUID().toString();
		this.sender = sender;
		this.type = type;
		this.created = new Date(); //TODO timezone
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Long getSender() {
		return sender;
	}

	public void setSender(Long sender) {
		this.sender = sender;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
