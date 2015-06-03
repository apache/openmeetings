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

import org.apache.openmeetings.db.dto.room.WhiteboardObject;
import org.apache.openmeetings.db.dto.room.WhiteboardObjectList;

/**
 * Memory based cache, configured as singleton in spring configuration
 * 
 * @author sebawagner
 *
 */
public class WhiteBoardObjectListManagerById {
	
	private HashMap<Long,WhiteboardObjectList> whiteBoardObjectList = new HashMap<Long,WhiteboardObjectList>();
	
	private Long whiteboardId = 0L;
	
	public Long getNewWhiteboardId(Long roomId) throws Exception {
		whiteboardId++;
		this.setWhiteBoardObjectListRoomObjAndWhiteboardId(roomId, new WhiteboardObject(), whiteboardId);
		return whiteboardId;
	}
	
	/*
	 * Room items a Whiteboard
	 */
	public synchronized WhiteboardObjectList getWhiteBoardObjectListByRoomId(Long roomId){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(roomId);
		if (whiteboardObjectList == null) {
			whiteboardObjectList = new WhiteboardObjectList();
		}
		return whiteboardObjectList;
	}
	
	public synchronized WhiteboardObject getWhiteBoardObjectListByRoomIdAndWhiteboard(Long roomId, Long whiteBoardId){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(roomId);
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
	
	public synchronized void setWhiteBoardObjectListRoomObj(Long roomId, WhiteboardObjectList whiteboardObjectList){
		whiteBoardObjectList.put(roomId, whiteboardObjectList);
	}
	
	public synchronized void setWhiteBoardObjectListRoomObjAndWhiteboardId(Long roomId, WhiteboardObject whiteboardObjects, Long whiteBoardId){
		WhiteboardObjectList whiteboardObjectList = whiteBoardObjectList.get(roomId);
		if (whiteboardObjectList == null) {
			whiteboardObjectList = new WhiteboardObjectList();
			whiteboardObjectList.setRoomId(roomId);
		}
		whiteboardObjects.setWhiteBoardId(whiteBoardId);
		whiteboardObjectList.getWhiteboardObjects().put(whiteBoardId, whiteboardObjects);
		whiteBoardObjectList.put(roomId, whiteboardObjectList);
	}
}
