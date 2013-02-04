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

import org.apache.openmeetings.data.whiteboard.dto.WhiteboardObject;
import org.apache.openmeetings.data.whiteboard.dto.WhiteboardObjectList;

/**
 * Memory based cache, configured as singleton in spring configuration
 * 
 * @author sebawagner
 *
 */
public class WhiteBoardObjectListManagerById {
	
	private HashMap<Long,WhiteboardObjectList> whiteBoardObjectList = new HashMap<Long,WhiteboardObjectList>();
	
	private Long whiteboardId = 0L;
	
	public Long getNewWhiteboardId(Long room_id) throws Exception {
		whiteboardId++;
		this.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id, new WhiteboardObject(), whiteboardId);
		return whiteboardId;
	}
	
	/*
	 * Room items a Whiteboard
	 */
	public synchronized WhiteboardObjectList getWhiteBoardObjectListByRoomId(Long room_id){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(room_id);
		if (whiteboardObjectList == null) {
			whiteboardObjectList = new WhiteboardObjectList();
		}
		return whiteboardObjectList;
	}
	
	public synchronized WhiteboardObject getWhiteBoardObjectListByRoomIdAndWhiteboard(Long room_id, Long whiteBoardId){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(room_id);
		if (whiteboardObjectList == null) {
			whiteboardObjectList = new WhiteboardObjectList();
		}
		WhiteboardObject whiteboardObjects = whiteboardObjectList.getWhiteboardObjects().get(whiteBoardId);
		if (whiteboardObjects == null) {
			whiteboardObjects = new WhiteboardObject();
		}
		return whiteboardObjects;
	}
	
	/*
	 * Whiteboard Object List
	 * 
	 */
	
	public synchronized void setWhiteBoardObjectListRoomObj(Long room_id, WhiteboardObjectList whiteboardObjectList){
		whiteBoardObjectList.put(room_id, whiteboardObjectList);
	}
	
	public synchronized void setWhiteBoardObjectListRoomObjAndWhiteboardId(Long room_id, WhiteboardObject whiteboardObjects, Long whiteBoardId){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(room_id);
		if (whiteboardObjectList == null) {
			whiteboardObjectList = new WhiteboardObjectList();
			whiteboardObjectList.setRoom_id(room_id);
		}
		whiteboardObjects.setWhiteBoardId(whiteBoardId);
		whiteboardObjectList.getWhiteboardObjects().put(whiteBoardId, whiteboardObjects);
		whiteBoardObjectList.put(room_id, whiteboardObjectList);
	}
}
