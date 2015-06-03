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
package org.apache.openmeetings.core.data.whiteboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.db.dto.room.WhiteboardSyncLockObject;
import org.apache.openmeetings.util.OpenmeetingsVariables;
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
	public synchronized void setWhiteBoardSyncListByRoomid(Long roomId, Map<String, WhiteboardSyncLockObject> mapObject) {
		whiteBoardSyncList.put(roomId, mapObject);
	}

	public synchronized Map<String, WhiteboardSyncLockObject> getWhiteBoardSyncListByRoomid(Long roomId) {
		Map<String, WhiteboardSyncLockObject> roomList = whiteBoardSyncList.get(roomId);
		if (roomList == null) {
			roomList = new HashMap<String, WhiteboardSyncLockObject>();
		}
		return roomList;
	}

	/*
	 * Image Sync Process
	 */
	public synchronized void setWhiteBoardImagesSyncListByRoomid(Long roomId,
			Map<String, Map<String, WhiteboardSyncLockObject>> mapObject) {
		whiteBoardObjectSyncList.put(roomId, mapObject);
	}

	public synchronized void setWhiteBoardImagesSyncListByRoomAndObjectId(
			Long roomId, String objectId,
			Map<String, WhiteboardSyncLockObject> imageSyncList) {
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList
				.get(roomId);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		if (imageSyncList.size() == 0) {
			roomList.remove(objectId);
		} else {
			roomList.put(objectId, imageSyncList);
		}
		setWhiteBoardImagesSyncListByRoomid(roomId, roomList);
	}

	public synchronized Map<String, Map<String, WhiteboardSyncLockObject>> getWhiteBoardObjectSyncListByRoomid(Long roomId) {
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(roomId);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		return roomList;
	}

	public synchronized Map<String, WhiteboardSyncLockObject> getWhiteBoardObjectSyncListByRoomAndObjectId(Long roomId, String objectId) {
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomId: "
				+ roomId);
		Map<String, Map<String, WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList
				.get(roomId);
		if (roomList == null) {
			roomList = new HashMap<String, Map<String, WhiteboardSyncLockObject>>();
		}
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList: "
				+ roomList);
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid objectId: "
				+ objectId);
		if (roomList.size() == 1) {
			log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList Key imageId: "
					+ roomList.keySet().iterator().next());
		}
		Map<String, WhiteboardSyncLockObject> imageSyncList = roomList
				.get(objectId);
		if (imageSyncList == null) {
			imageSyncList = new HashMap<String, WhiteboardSyncLockObject>();
		}
		return imageSyncList;
	}

}
