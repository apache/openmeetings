package org.openmeetings.app.conference.whiteboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.remote.red5.WhiteBoardObjectListManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardManagement {
	
	private static final Logger log = Logger.getLogger(WhiteboardManagement.class);

	private WhiteboardManagement() {}
	
	private static WhiteboardManagement instance = null;
	
	public static synchronized WhiteboardManagement getInstance() {
		if (instance == null) {
			instance = new WhiteboardManagement();
		}
		return instance;
	}
	
	public void addWhiteBoardObject(Long room_id, Map whiteboardObj) {
		try {
			log.debug("addWhiteBoardObject: "+whiteboardObj);
			
			log.debug("whiteboardObj 0: Event: "+whiteboardObj.get(0));
			log.debug("whiteboardObj 1: Event: "+whiteboardObj.get(1));
			log.debug("whiteboardObj 2: Event: "+whiteboardObj.get(2));
			log.debug("whiteboardObj 3: Event: "+whiteboardObj.get(3));
			
			//log.debug("whiteboardObj NUMB3: Event: "+whiteboardObj.get(3).getClass().getName());
			
			Date dateOfEvent = (Date) whiteboardObj.get(1);
			String action = whiteboardObj.get(2).toString();	
			List actionObject = (List) whiteboardObj.get(3);
			
			log.debug("action: "+action);
			
			if (action.equals("draw") || action.equals("redo")){
				HashMap<String,List> roomList = WhiteBoardObjectListManager.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				
				//log.debug(actionObject);
				//log.debug(actionObject.size()-1);
				//log.debug(actionObject.get(actionObject.size()-1));
				
				String objectOID = actionObject.get(actionObject.size()-1).toString();
				log.debug("objectOID: "+objectOID);
				roomList.put(objectOID, actionObject);
				WhiteBoardObjectListManager.getInstance().setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else if (action.equals("clear")) {
				HashMap<String,List> roomList = WhiteBoardObjectListManager.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				roomList = new HashMap<String,List>();
				WhiteBoardObjectListManager.getInstance().setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else if (action.equals("delete") || action.equals("undo")) {
				HashMap<String,List> roomList = WhiteBoardObjectListManager.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				String objectOID = actionObject.get(actionObject.size()-1).toString();
				log.debug("removal of objectOID: "+objectOID);
				roomList.remove(objectOID);
				WhiteBoardObjectListManager.getInstance().setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else if (action.equals("size") || action.equals("editProp") 
					|| action.equals("editText") || action.equals("swf")) {
				HashMap<String,List> roomList = WhiteBoardObjectListManager.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				String objectOID = actionObject.get(actionObject.size()-1).toString();
				List roomItem = roomList.get(objectOID);
				roomList.put(objectOID, actionObject);
				WhiteBoardObjectListManager.getInstance().setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else {
				log.warn("Unkown Type: "+action+" actionObject: "+actionObject);
			}
			
		} catch (Exception err) {
			log.error("[addWhiteBoardObject]",err);
		}
	}
	
}
