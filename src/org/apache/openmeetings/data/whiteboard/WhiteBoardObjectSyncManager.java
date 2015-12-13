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
package org.apache.openmeetings.data.whiteboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.whiteboard.dto.WhiteboardSyncLockObject;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Memory based, configured as singleton in spring configuration
 * 
 * @author sebawagner
 *
 */
public class WhiteBoardObjectSyncManager {

	private HashMap<Long, Map<String, WhiteboardSyncLockObject>> whiteBoardSyncList = new HashMap<Long, Map<String, WhiteboardSyncLockObject>>();

	private HashMap<Long, Map<String, Map<String, WhiteboardSyncLockObject>>> whiteBoardObjectSyncList = new HashMap<Long, Map<String, Map<String, WhiteboardSyncLockObject>>>();

	private static final Logger log = Red5LoggerFactory.getLogger(
			WhiteBoardObjectSyncManager.class,
			OpenmeetingsVariables.webAppRootKey);

	/*
	 * Initial Sync Process
	 */
	public synchronized void setWhiteBoardSyncListByRoomid(Long room_id,
			Map<String, WhiteboardSyncLockObject> mapObject) {
		whiteBoardSyncList.put(room_id, mapObject);
	}

	public synchronized Map<String, WhiteboardSyncLockObject> getWhiteBoardSyncListByRoomid(
			Long room_id) {
		Map<String, WhiteboardSyncLockObject> roomList = whiteBoardSyncList
				.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String, WhiteboardSyncLockObject>();
		}
		return roomList;
	}

	/*
	 * Image Sync Process
	 */
	public synchronized void setWhiteBoardImagesSyncListByRoomid(Long room_id,
			Map<String, Map<String, WhiteboardSyncLockObject>> mapObject) {
		whiteBoardObjectSyncList.put(room_id, mapObject);
	}

	public synchronized void setWhiteBoardImagesSyncListByRoomAndObjectId(
			Long room_id, String object_id,
			Map<String, WhiteboardSyncLockObject> imageSyncList) {
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList
				.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		if (imageSyncList.size() == 0) {
			roomList.remove(object_id);
		} else {
			roomList.put(object_id, imageSyncList);
		}
		setWhiteBoardImagesSyncListByRoomid(room_id, roomList);
	}

	public synchronized Map<String, Map<String, WhiteboardSyncLockObject>> getWhiteBoardObjectSyncListByRoomid(
			Long room_id) {
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList
				.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		return roomList;
	}

	public synchronized Map<String, WhiteboardSyncLockObject> getWhiteBoardObjectSyncListByRoomAndObjectId(
			Long room_id, String object_id) {
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid room_id: "
				+ room_id);
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList
				.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList: "
				+ roomList);
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid object_id: "
				+ object_id);
		if (roomList.size() == 1) {
			log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList Key image_id: "
					+ roomList.keySet().iterator().next());
		}
		Map<String, WhiteboardSyncLockObject> imageSyncList = roomList
				.get(object_id);
		if (imageSyncList == null) {
			imageSyncList = new HashMap<String, WhiteboardSyncLockObject>();
		}
		return imageSyncList;
	}

}
