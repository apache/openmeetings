package org.openmeetings.app.remote.red5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.conference.whiteboard.WhiteboardSyncLockObject;

public class WhiteBoardObjectListManager {
	
	private static HashMap<Long,HashMap<String,List>> whiteBoardObjectList = new HashMap<Long,HashMap<String,List>>();
	private static HashMap<Long,Map<String,WhiteboardSyncLockObject>> whiteBoardSyncList = new HashMap<Long,Map<String,WhiteboardSyncLockObject>>();
	
	private static HashMap<Long,Map<String,Map<String,WhiteboardSyncLockObject>>> whiteBoardObjectSyncList = new HashMap<Long,Map<String,Map<String,WhiteboardSyncLockObject>>>();

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteBoardObjectListManager.class, ScopeApplicationAdapter.webAppRootKey);

	private static WhiteBoardObjectListManager instance = null;

	private WhiteBoardObjectListManager() {
	}

	public static synchronized WhiteBoardObjectListManager getInstance() {
		if (instance == null) {
			instance = new WhiteBoardObjectListManager();
		}
		return instance;
	}
	
	/*
	 * Room items a Whiteboard
	 */
	public synchronized HashMap<String,List> getWhiteBoardObjectListByRoomId(Long room_id){
		HashMap<String,List> roomList = whiteBoardObjectList.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String,List>();
		}
		return roomList;
	}
	
	/*
	 * Initial Sync Process
	 */
	public synchronized void setWhiteBoardSyncListByRoomid(Long room_id, Map<String,WhiteboardSyncLockObject> mapObject ){
		whiteBoardSyncList.put(room_id, mapObject);
	}
	public synchronized Map<String,WhiteboardSyncLockObject> getWhiteBoardSyncListByRoomid(Long room_id){
		Map<String,WhiteboardSyncLockObject> roomList = whiteBoardSyncList.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String,WhiteboardSyncLockObject>();
		}
		return roomList;
	}
	
	/*
	 * Image Sync Process
	 */
	public synchronized void setWhiteBoardImagesSyncListByRoomid(Long room_id, Map<String,Map<String,WhiteboardSyncLockObject>> mapObject ){
		whiteBoardObjectSyncList.put(room_id, mapObject);
	}
	public synchronized void setWhiteBoardImagesSyncListByRoomAndObjectId(Long room_id, 
			String object_id,Map<String,WhiteboardSyncLockObject> imageSyncList){
		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
		}
		if (imageSyncList.size() == 0) {
			roomList.remove(object_id);
		} else {
			roomList.put(object_id, imageSyncList);
		}
		setWhiteBoardImagesSyncListByRoomid(room_id,roomList);
	}
	public synchronized Map<String,Map<String,WhiteboardSyncLockObject>> getWhiteBoardObjectSyncListByRoomid(Long room_id){
		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
		}
		return roomList;
	}
	public synchronized Map<String,WhiteboardSyncLockObject> getWhiteBoardObjectSyncListByRoomAndObjectId(Long room_id, 
			String object_id){
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid room_id: "+room_id);
		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
		if (roomList == null) {
			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
		}
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList: "+roomList);
		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid object_id: "+object_id);
		if (roomList.size() == 1) {
			log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList Key image_id: "+roomList.keySet().iterator().next());
		}
		Map<String,WhiteboardSyncLockObject> imageSyncList = roomList.get(object_id);
		if (imageSyncList == null) {
			imageSyncList = new HashMap<String,WhiteboardSyncLockObject>();
		}
		return imageSyncList;
	}
	
	/*
	 * Whiteboard Object List
	 * 
	 */
	public synchronized HashMap<Long,HashMap<String,List>> getWhiteBoardObjectList(){
		return whiteBoardObjectList;
	}
	public synchronized void setWhiteBoardObjectList(HashMap<Long,HashMap<String,List>> whiteBoardObjectListNew){
		whiteBoardObjectList = whiteBoardObjectListNew;
	}
	public synchronized void setWhiteBoardObjectListRoomObj(Long room_id, HashMap<String,List> roomList){
		whiteBoardObjectList.put(room_id, roomList);
	}

}
