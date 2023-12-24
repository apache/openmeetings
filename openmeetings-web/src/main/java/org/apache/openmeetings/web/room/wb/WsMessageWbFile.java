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
package org.apache.openmeetings.web.room.wb;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.openmeetings.util.NullStringer;

import com.github.openjson.JSONObject;

public class WsMessageWbFile implements IClusterWsMessage {
	private static final long serialVersionUID = 1L;
	private final Long roomId;
	private final long wbId;
	private final String ruid;
	private final String file;
	private final BaseFileItem fi;

	public WsMessageWbFile(Long roomId, long wbId, String ruid, JSONObject file, BaseFileItem fi) {
		this.roomId = roomId;
		this.wbId = wbId;
		this.ruid = ruid;
		this.file = file.toString(new NullStringer());
		this.fi = fi;
	}

	public Long getRoomId() {
		return roomId;
	}

	public long getWbId() {
		return wbId;
	}

	public String getRoomUid() {
		return ruid;
	}

	public JSONObject getFile() {
		return new JSONObject(file);
	}

	public BaseFileItem getFileItem() {
		return fi;
	}
}
