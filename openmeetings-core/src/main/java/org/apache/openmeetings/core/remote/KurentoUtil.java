/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 */
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
package org.apache.openmeetings.core.remote;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.kurento.client.MediaObject;
import org.kurento.client.Tag;

import com.github.openjson.JSONObject;

public class KurentoUtil {

	public static final String KURENTO_TYPE = "kurento";

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
	}

	static JSONObject newStoppedMsg(StreamDesc sd) {
		return newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", sd.getUid());
	}

	static Map<String, String> tagsAsMap(MediaObject pipe) {
		return pipe.getTags().stream()
				.collect(Collectors.toMap(Tag::getKey, Tag::getValue));
	}

	public static void sendError(IWsClient c, String msg) {
		WebSocketHelper.sendClient(c, newKurentoMsg()
				.put("id", "error")
				.put("message", msg));
	}
}
