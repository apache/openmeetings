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

import java.util.function.Predicate;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.openmeetings.web.user.record.JpgRecordingResourceReference;
import org.apache.openmeetings.web.user.record.Mp4RecordingResourceReference;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.FileSystemResourceReference;

import com.github.openjson.JSONObject;

public class WbWebSocketHelper extends WebSocketHelper {
	public static final String PARAM_OBJ = "obj";

	public static void send(IClusterWsMessage _m) {
		if (_m instanceof WsMessageWb) {
			WsMessageWb m = (WsMessageWb)_m;
			if (m.getUid() == null) {
				sendWbAll(m.getRoomId(), m.getMeth(), m.getObj(), false);
			} else {
				sendWbOthers(m.getRoomId(), m.getMeth(), m.getObj(), m.getUid(), false);
			}
		} else if (_m instanceof WsMessageWbFile) {
			WsMessageWbFile m = (WsMessageWbFile)_m;
			sendWbFile(m.getRoomId(), m.getWbId(), m.getRoomUid(), m.getFile(), m.getFileItem(), false);
		} else {
			WebSocketHelper.send(_m);
		}
	}

	public static void sendWbAll(Long roomId, WbAction meth, JSONObject obj) {
		sendWbAll(roomId, meth, obj, true);
	}

	private static void sendWbAll(Long roomId, WbAction meth, JSONObject obj, boolean publish) {
		if (publish) {
			publish(new WsMessageWb(roomId, meth, obj, null));
		}
		sendWb(roomId, meth, obj, null);
	}

	public static void sendWbOthers(Long roomId, WbAction meth, JSONObject obj, final String uid) {
		sendWbOthers(roomId, meth, obj, uid, true);
	}

	private static void sendWbOthers(Long roomId, WbAction meth, JSONObject obj, final String uid, boolean publish) {
		if (publish) {
			publish(new WsMessageWb(roomId, meth, obj, uid));
		}
		sendWb(roomId, meth, obj, c -> !uid.equals(c.getUid()));
	}

	public static JSONObject getObjWbJson(Long wbId, Object o) {
		return new JSONObject().put("wbId", wbId).put(PARAM_OBJ, o);
	}

	private static CharSequence urlFor(final ResourceReference resourceReference, PageParameters parameters) {
		return RequestCycle.get().urlFor(resourceReference, parameters);
	}

	public static JSONObject addFileUrl(String ruid, JSONObject _file, FileItem fi, Client c) {
		JSONObject file = new JSONObject(_file.toString(new NullStringer()));
		final FileSystemResourceReference ref;
		final PageParameters pp = new PageParameters()
				.add("id", fi.getId()).add("uid", c.getUid())
				.add("ruid", ruid).add("wuid", _file.optString("uid"));
		switch (fi.getType()) {
			case Video:
				ref = new RoomResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("_poster", urlFor(ref, new PageParameters(pp).add("preview", true)));
				break;
			case Recording:
				ref = new Mp4RecordingResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("_poster", urlFor(new JpgRecordingResourceReference(), pp));
				break;
			case Presentation:
				ref = new RoomResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("deleted", !fi.exists());
				break;
			default:
				ref = new RoomResourceReference();
				file.put("src", urlFor(ref, pp));
				break;
		}
		return file;
	}

	public static void sendWbFile(Long roomId, long wbId, String ruid, JSONObject file, FileItem fi) {
		sendWbFile(roomId, wbId, ruid, file, fi, true);
	}

	private static void sendWbFile(Long roomId, long wbId, String ruid, JSONObject file, FileItem fi, boolean publish) {
		if (publish) {
			publish(new WsMessageWbFile(roomId, wbId, ruid, file, fi));
		}
		WebSocketHelper.sendRoom(
				roomId
				, new JSONObject().put("type", "wb")
				, null
				, (o, c) -> {
						return o.put("func", String.format("WbArea.%s(%s);"
								, WbAction.createObj.name()
								, getObjWbJson(wbId, addFileUrl(ruid, file, fi, c)).toString())
							).toString();
					}
				);
	}

	private static void sendWb(Long roomId, WbAction meth, JSONObject obj, Predicate<Client> check) {
		WebSocketHelper.sendRoom(
				roomId
				, new JSONObject().put("type", "wb")
				, check
				, (o, c) -> o.put("func", String.format("WbArea.%s(%s);", meth.name(), obj.toString(new NullStringer()))).toString()
			);
	}
}
