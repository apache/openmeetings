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
package org.apache.openmeetings.core.util.ws;

import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.ws.IClusterWsMessage;

import com.github.openjson.JSONObject;

public class WsMessageRoom implements IClusterWsMessage {
	private static final long serialVersionUID = 1L;
	private final Long roomId;
	private final String msg;

	public WsMessageRoom(Long roomId, JSONObject msg) {
		this.roomId = roomId;
		this.msg = msg.toString(new NullStringer());
	}

	public Long getRoomId() {
		return roomId;
	}

	public JSONObject getMsg() {
		return new JSONObject(msg);
	}
}
