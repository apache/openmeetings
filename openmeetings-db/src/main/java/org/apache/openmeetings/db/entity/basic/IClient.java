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

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.room.Room;

/**
 * Temporary interface, will be removed after 2 types of cliens will be merged
 * {@link org.apache.openmeetings.db.entity.room.StreamClient}
 * {@link org.apache.openmeetings.db.entity.basic.Client}
 * @author solomax
 *
 */
public interface IClient extends IDataProviderEntity {
	enum Type {
		video
		, room // room flash client
		, sip
		, mobile
		, sharing
	}
	String getUid();
	String getSid();
	Long getUserId();
	String getLogin();
	String getFirstname();
	String getLastname();
	String getRemoteAddress();
	Long getRoomId();
	Room.Type getRoomType();
	int getWidth();
	int getHeight();
	void setRecordingStarted(boolean recordingStarted);
	Long getRecordingId();
	void setRecordingId(Long recordingId);
	String getServerId();
}
