package org.openmeetings.app.remote.red5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.conference.whiteboard.WhiteboardObject;
import org.openmeetings.app.conference.whiteboard.WhiteboardObjectList;
import org.openmeetings.app.conference.whiteboard.WhiteboardSyncLockObject;

public class WhiteBoardObjectListManagerById {
	
	private static HashMap<Long,WhiteboardObjectList> whiteBoardObjectList = new HashMap<Long,WhiteboardObjectList>();
	
	private static Long whiteboardId = 0L;
	
//	private static HashMap<Long,Map<String,WhiteboardSyncLockObject>> whiteBoardSyncList = new HashMap<Long,Map<String,WhiteboardSyncLockObject>>();
//	
//	private static HashMap<Long,Map<String,Map<String,WhiteboardSyncLockObject>>> whiteBoardObjectSyncList = new HashMap<Long,Map<String,Map<String,WhiteboardSyncLockObject>>>();

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteBoardObjectListManagerById.class, "openmeetings");

	private static WhiteBoardObjectListManagerById instance = null;

	private WhiteBoardObjectListManagerById() {
	}

	public static synchronized WhiteBoardObjectListManagerById getInstance() {
		if (instance == null) {
			instance = new WhiteBoardObjectListManagerById();
		}
		return instance;
	}
	
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
	
//	/*
//	 * Initial Sync Process
//	 */
//	public synchronized void setWhiteBoardSyncListByRoomid(Long room_id, Map<String,WhiteboardSyncLockObject> mapObject ){
//		whiteBoardSyncList.put(room_id, mapObject);
//	}
//	public synchronized Map<String,WhiteboardSyncLockObject> getWhiteBoardSyncListByRoomid(Long room_id){
//		Map<String,WhiteboardSyncLockObject> roomList = whiteBoardSyncList.get(room_id);
//		if (roomList == null) {
//			roomList = new HashMap<String,WhiteboardSyncLockObject>();
//		}
//		return roomList;
//	}
//	
//	/*
//	 * Image Sync Process
//	 */
//	public synchronized void setWhiteBoardImagesSyncListByRoomid(Long room_id, Map<String,Map<String,WhiteboardSyncLockObject>> mapObject ){
//		whiteBoardObjectSyncList.put(room_id, mapObject);
//	}
//	public synchronized void setWhiteBoardImagesSyncListByRoomAndObjectId(Long room_id, 
//			String object_id,Map<String,WhiteboardSyncLockObject> imageSyncList){
//		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
//		if (roomList == null) {
//			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
//		}
//		if (imageSyncList.size() == 0) {
//			roomList.remove(object_id);
//		} else {
//			roomList.put(object_id, imageSyncList);
//		}
//		setWhiteBoardImagesSyncListByRoomid(room_id,roomList);
//	}
//	public synchronized Map<String,Map<String,WhiteboardSyncLockObject>> getWhiteBoardObjectSyncListByRoomid(Long room_id){
//		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
//		if (roomList == null) {
//			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
//		}
//		return roomList;
//	}
//	public synchronized Map<String,WhiteboardSyncLockObject> getWhiteBoardObjectSyncListByRoomAndObjectId(Long room_id, 
//			String object_id){
//		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid room_id: "+room_id);
//		Map<String,Map<String,WhiteboardSyncLockObject>> roomList = whiteBoardObjectSyncList.get(room_id);
//		if (roomList == null) {
//			roomList = new HashMap<String,Map<String,WhiteboardSyncLockObject>>();
//		}
//		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList: "+roomList);
//		log.debug("getWhiteBoardImagesSyncListByRoomAndImageid object_id: "+object_id);
//		if (roomList.size() == 1) {
//			log.debug("getWhiteBoardImagesSyncListByRoomAndImageid roomList Key image_id: "+roomList.keySet().iterator().next());
//		}
//		Map<String,WhiteboardSyncLockObject> imageSyncList = roomList.get(object_id);
//		if (imageSyncList == null) {
//			imageSyncList = new HashMap<String,WhiteboardSyncLockObject>();
//		}
//		return imageSyncList;
//	}
	
	/*
	 * Whiteboard Object List
	 * 
	 */
	
//	public synchronized HashMap<Long,HashMap<String,List>> getWhiteBoardObjectList(){
//		return whiteBoardObjectList;
//	}
//	
//	public synchronized void setWhiteBoardObjectList(HashMap<Long,HashMap<String,List>> whiteBoardObjectListNew){
//		whiteBoardObjectList = whiteBoardObjectListNew;
//	}
//	
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
