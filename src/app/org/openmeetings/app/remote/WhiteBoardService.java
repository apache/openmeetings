package org.openmeetings.app.remote;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.conference.whiteboard.WhiteboardObject;
import org.openmeetings.app.conference.whiteboard.WhiteboardObjectList;
import org.openmeetings.app.conference.whiteboard.WhiteboardSyncLockObject;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.remote.red5.WhiteBoardObjectListManager;
import org.openmeetings.app.remote.red5.WhiteBoardObjectListManagerById;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebastianwagner
 *
 */
public class WhiteBoardService implements IPendingServiceCallback {
	
	private static final Logger log = Red5LoggerFactory.getLogger(WhiteBoardService.class, "openmeetings");
    @Autowired
    private Usermanagement userManagement;
    @Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter = null;
    @Autowired
	private ClientListManager clientListManager = null;
    @Autowired
	private WhiteBoardObjectListManager whiteBoardObjectListManager = null;
    @Autowired
	private WhiteBoardObjectListManagerById whiteBoardObjectListManagerById = null;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	 
	/**
	 * Loading the List of Objects on the whiteboard
	 * @return HashMap<String,Map>
	 */
	public WhiteboardObject getRoomItems(){
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			log.debug("getRoomItems: "+room_id);
			
			return this.whiteBoardObjectListManager.getWhiteBoardObjectRoomId(room_id);
			
			//HashMap<String,List> roomItems = this.whiteBoardObjectListManager.getWhiteBoardObjectListByRoomId(room_id);
			
			//There is a client side sorting now so no need for this
//			LinkedList<List> itemList = new LinkedList<List>();
//			for (Iterator<String> it = roomItems.keySet().iterator();it.hasNext();){
//				itemList.add(roomItems.get(it.next()));
//			}
			
			
			//return itemList;
		} catch (Exception err) {
			log.error("[getRoomItems]",err);
		}
		return null;
	}
	
	public Long getNewWhiteboardId() {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			
			Long whiteBoardId =  this.whiteBoardObjectListManagerById.getNewWhiteboardId(room_id);
			
			return whiteBoardId;
			
		} catch (Exception err) {
			log.error("[deleteWhiteboard]",err);
		}
		return null;
	}
	
	public Boolean deleteWhiteboard(Long whiteBoardId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			WhiteboardObjectList whiteboardObjectList = this.whiteBoardObjectListManagerById.getWhiteBoardObjectListByRoomId(room_id);
		
			for (Iterator<Long> iter = whiteboardObjectList.getWhiteboardObjects().keySet().iterator();iter.hasNext();) {
				Long storedWhiteboardId = iter.next();
				
				log.debug(" :: storedWhiteboardId :: "+storedWhiteboardId);
				
				if (storedWhiteboardId.equals(whiteBoardId)) {
					log.debug("Found Whiteboard to Remove");
				}
			}
			Object returnValue = whiteboardObjectList.getWhiteboardObjects().remove(whiteBoardId);
			
			log.debug(" :: whiteBoardId :: "+whiteBoardId);
			
			this.whiteBoardObjectListManagerById.setWhiteBoardObjectListRoomObj(room_id, whiteboardObjectList);
			
			if (returnValue != null) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception err) {
			log.error("[deleteWhiteboard]",err);
		}
		return null;
	}
	
	public WhiteboardObjectList getRoomItemsBy(){
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			log.debug("getRoomItems: "+room_id);
			WhiteboardObjectList whiteboardObjectList = this.whiteBoardObjectListManagerById.getWhiteBoardObjectListByRoomId(room_id);
			
			LinkedList<List> completeList = new LinkedList<List>();
			
			//LinkedList<List> itemList = new LinkedList<List>();
			
			if (whiteboardObjectList.getWhiteboardObjects().size() == 0) {
				
				Long whiteBoardId = this.whiteBoardObjectListManagerById.getNewWhiteboardId(room_id);
				
				this.whiteBoardObjectListManagerById.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id, new WhiteboardObject(), whiteBoardId);
				
				log.debug("Init New Room List");
				
				whiteboardObjectList = this.whiteBoardObjectListManagerById.getWhiteBoardObjectListByRoomId(room_id);
				
				return whiteboardObjectList;
				
			} else {
				
				return whiteboardObjectList;
				
//				for (Iterator<Long> it = whiteboardObjectList.getWhiteboardObjects().keySet().iterator();it.hasNext();){
//					WhiteboardObject whiteboardObject = whiteboardObjectList.getWhiteboardObjects().get(it.next());
//					
//					LinkedList<List> itemList = new LinkedList<List>();
//					for (Iterator<String> it2 = whiteboardItems.keySet().iterator();it2.hasNext();){
//						itemList.add(whiteboardItems.get(it2.next()));
//					}
//					
//					completeList.add(itemList);
//				}
				
//				for (Iterator<Long> it = roomItems.keySet().iterator();it.hasNext();){
//					HashMap<String,List> whiteboardItems = roomItems.get(it.next());
//					
//					LinkedList<List> itemList = new LinkedList<List>();
//					for (Iterator<String> it2 = whiteboardItems.keySet().iterator();it2.hasNext();){
//						itemList.add(whiteboardItems.get(it2.next()));
//					}
//					
//					completeList.add(itemList);
//				}
				
			}
			
			//return completeList;
		} catch (Exception err) {
			log.error("[getRoomItemsBy]",err);
		}
		return null;
	}
	
	/**
	 * change the draw status of a user, allow disallow him to draw anybody besides the Moderator to
	 * draw on the whiteboard, only a Moderator is allowed to trigger this function
	 * 
	 * @param SID
	 * @param publicSID
	 * @param canDraw
	 * @return
	 */
	public Boolean setCanDraw(String SID, String publicSID, boolean canDraw) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			Long users_id = sessionManagement.checkSession(SID);
	        Long user_level = userManagement.getUserLevelByID(users_id);
	        
	        if (authLevelManagement.checkUserLevel(user_level)) {
	        	
	        	if (currentClient.getIsMod()) {
	        		RoomClient rcl = this.clientListManager.getClientByPublicSID(publicSID);
	        		
	        		if (rcl != null) {
	        			rcl.setCanDraw(canDraw);
	        			this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
	        			
	        			HashMap<Integer,Object> newMessage = new HashMap<Integer,Object>();
	        			newMessage.put(0,"updateDrawStatus");
	        			newMessage.put(1,rcl);
	        			this.scopeApplicationAdapter.sendMessageWithClient(newMessage);
	        			
	        		} else {
	        			return false;
	        		}
	        	} else {
	        		return false;
	        	}
	        }
			
		} catch (Exception err) {
			log.error("[setCanDraw]",err);
		}
		return null;
	}
	
	public Boolean setCanShare(String SID, String publicSID, boolean canShare) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			Long users_id = sessionManagement.checkSession(SID);
	        Long user_level = userManagement.getUserLevelByID(users_id);
	        
	        if (authLevelManagement.checkUserLevel(user_level)) {
	        	
	        	if (currentClient.getIsMod()) {
	        		RoomClient rcl = this.clientListManager.getClientByPublicSID(publicSID);
	        		
	        		if (rcl != null) {
	        			rcl.setCanShare(canShare);
	        			this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
	        			
	        			HashMap<Integer,Object> newMessage = new HashMap<Integer,Object>();
	        			newMessage.put(0,"updateDrawStatus");
	        			newMessage.put(1,rcl);
	        			this.scopeApplicationAdapter.sendMessageWithClient(newMessage);
	        			
	        		} else {
	        			return false;
	        		}
	        	} else {
	        		return false;
	        	}
	        }
			
		} catch (Exception err) {
			log.error("[setCanDraw]",err);
		}
		return null;
	}
	
	public Boolean setCanRemote(String SID, String publicSID, boolean canRemote) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			Long users_id = sessionManagement.checkSession(SID);
	        Long user_level = userManagement.getUserLevelByID(users_id);
	        
	        if (authLevelManagement.checkUserLevel(user_level)) {
	        	
	        	if (currentClient.getIsMod()) {
	        		RoomClient rcl = this.clientListManager.getClientByPublicSID(publicSID);
	        		
	        		if (rcl != null) {
	        			rcl.setCanRemote(canRemote);
	        			this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
	        			
	        			HashMap<Integer,Object> newMessage = new HashMap<Integer,Object>();
	        			newMessage.put(0,"updateDrawStatus");
	        			newMessage.put(1,rcl);
	        			this.scopeApplicationAdapter.sendMessageWithClient(newMessage);
	        			
	        		} else {
	        			return false;
	        		}
	        	} else {
	        		return false;
	        	}
	        }
			
		} catch (Exception err) {
			log.error("[setCanDraw]",err);
		}
		return null;
	}
	
	public WhiteboardSyncLockObject startNewSyncprocess(){
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			WhiteboardSyncLockObject wSyncLockObject = new WhiteboardSyncLockObject();
			wSyncLockObject.setAddtime(new Date());
			wSyncLockObject.setPublicSID(currentClient.getPublicSID());
			wSyncLockObject.setInitialLoaded(true);
			
			Map<String,WhiteboardSyncLockObject> syncListRoom = this.whiteBoardObjectListManager.getWhiteBoardSyncListByRoomid(room_id);
			

			wSyncLockObject.setCurrentLoadingItem(true);
			wSyncLockObject.setStarttime(new Date());
		
			syncListRoom.put(currentClient.getPublicSID(), wSyncLockObject);
			this.whiteBoardObjectListManager.setWhiteBoardSyncListByRoomid(room_id, syncListRoom);
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								if (room_id!=null && room_id.equals(rcl.getRoom_id())) {
									((IServiceCapableConnection) conn).invoke("sendSyncFlag", new Object[] { wSyncLockObject },this);
								}
							}
						}
					}		
				}
			}	
			
			return wSyncLockObject;
			
			
		} catch (Exception err) {
			log.error("[startNewSyncprocess]",err);
		}
		return null;
	}
	
	public void sendCompletedSyncEvent() {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			Map<String,WhiteboardSyncLockObject> syncListRoom = this.whiteBoardObjectListManager.getWhiteBoardSyncListByRoomid(room_id);

			WhiteboardSyncLockObject wSyncLockObject = syncListRoom.get(currentClient.getPublicSID());
			
			if (wSyncLockObject == null) {
				log.error("WhiteboardSyncLockObject not found for this Client "+syncListRoom);
				return;
			} else if (!wSyncLockObject.isCurrentLoadingItem()) {
				log.warn("WhiteboardSyncLockObject was not started yet "+syncListRoom);
				return;
			} else {
				syncListRoom.remove(currentClient.getPublicSID());
				this.whiteBoardObjectListManager.setWhiteBoardSyncListByRoomid(room_id, syncListRoom);
				
				int numberOfInitial = this.getNumberOfInitialLoaders(syncListRoom);
				
				if (numberOfInitial==0){
					int returnVal = 0;
					Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										if (room_id!=null && room_id.equals(rcl.getRoom_id())) {
											returnVal++;
											((IServiceCapableConnection) conn).invoke("sendSyncCompleteFlag", new Object[] { wSyncLockObject },this);
										}
									}
								}
							}			
						}
					}	
					//return returnVal;
				} else {
					return;
				}
			}
			
			
		} catch (Exception err) {
			log.error("[sendCompletedSyncEvent]",err);
		}
		return;
	}
	
	private int getNumberOfInitialLoaders(Map<String,WhiteboardSyncLockObject> syncListRoom) throws Exception {
		int number = 0;
		for (Iterator<String> iter = syncListRoom.keySet().iterator();iter.hasNext();) {
			WhiteboardSyncLockObject lockObject = syncListRoom.get(iter.next());
			if (lockObject.isInitialLoaded()){
				number++;
			}
		}
		return number;
	}

	/*
	 * Image Sync Sequence
	 * 
	 */
	
	public void startNewObjectSyncProcess(String object_id, boolean isStarting){
		try {
			
			log.debug("startNewObjectSyncprocess: "+object_id);
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			WhiteboardSyncLockObject wSyncLockObject = new WhiteboardSyncLockObject();
			wSyncLockObject.setAddtime(new Date());
			wSyncLockObject.setPublicSID(currentClient.getPublicSID());
			wSyncLockObject.setStarttime(new Date());
			
			Map<String,WhiteboardSyncLockObject> syncListImage = this.whiteBoardObjectListManager.getWhiteBoardObjectSyncListByRoomAndObjectId(room_id,object_id);
			syncListImage.put(currentClient.getPublicSID(), wSyncLockObject);
			this.whiteBoardObjectListManager.setWhiteBoardImagesSyncListByRoomAndObjectId(room_id, object_id, syncListImage);
			
			//Do only send the Token to show the Loading Splash for the initial-Request that starts the loading
			if (isStarting) {
				Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							if (conn instanceof IServiceCapableConnection) {
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									log.debug("sending :"+rcl);
									if (room_id!=null && room_id.equals(rcl.getRoom_id())) {
										log.debug("sendObjectSyncFlag :"+rcl);
										((IServiceCapableConnection) conn).invoke("sendObjectSyncFlag", new Object[] { wSyncLockObject },this);
									}
								}
							}
						}		
					}
				}	
			}
			
		} catch (Exception err) {
			log.error("[startNewObjectSyncProcess]",err);
		}
	}
	
	public int sendCompletedObjectSyncEvent(String object_id) {
		try {
			
			log.debug("sendCompletedObjectSyncEvent: "+object_id);
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			Long room_id = currentClient.getRoom_id();
			
			Map<String,WhiteboardSyncLockObject> syncListImage = this.whiteBoardObjectListManager.getWhiteBoardObjectSyncListByRoomAndObjectId(room_id,object_id);

			log.debug("sendCompletedObjectSyncEvent syncListImage: "+syncListImage);
			
			WhiteboardSyncLockObject wSyncLockObject = syncListImage.get(currentClient.getPublicSID());
			
			if (wSyncLockObject == null) {
				log.error("WhiteboardSyncLockObject not found for this Client "+currentClient.getPublicSID());
				log.error("WhiteboardSyncLockObject not found for this syncListImage "+syncListImage);
				return -2;
				
			} else {
				
				log.debug("sendCompletedImagesSyncEvent remove: "+currentClient.getPublicSID());
				
				syncListImage.remove(currentClient.getPublicSID());
				this.whiteBoardObjectListManager.setWhiteBoardImagesSyncListByRoomAndObjectId(room_id, object_id, syncListImage);
				
				int numberOfInitial = this.whiteBoardObjectListManager.getWhiteBoardObjectSyncListByRoomid(room_id).size();
				
				log.debug("sendCompletedImagesSyncEvent numberOfInitial: "+numberOfInitial);
				
				if (numberOfInitial==0){
					int returnVal = 0;
					Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										if (room_id!=null && room_id.equals(rcl.getRoom_id())) {
											returnVal++;
											((IServiceCapableConnection) conn).invoke("sendObjectSyncCompleteFlag", new Object[] { wSyncLockObject },this);
										}
									}
								}
							}		
						}
					}	
					return returnVal;
				} else {
					return -4;
				}
			}
			
			
		} catch (Exception err) {
			log.error("[sendCompletedObjectSyncEvent]",err);
		}
		return -1;
	}
	
	
//	private int getNumberOfImageLoaders(Map<String,WhiteboardSyncLockObject> syncListRoom) throws Exception {
//		int number = 0;
//		for (Iterator<String> iter = syncListRoom.keySet().iterator();iter.hasNext();) {
//			WhiteboardSyncLockObject lockObject = syncListRoom.get(iter.next());
//			if (lockObject.isImageLoader()){
//				number++;
//			}
//		}
//		return number;
//	}	
//	

	/*
	 * Image Sync Sequence
	 * 
	 */
	
//	public WhiteboardSyncLockObject startNewSWFSyncprocess(){
//		try {
//			
//			IConnection current = Red5.getConnectionLocal();
//			String streamid = current.getClient().getId();
//			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
//			Long room_id = currentClient.getRoom_id();
//			
//			WhiteboardSyncLockObject wSyncLockObject = new WhiteboardSyncLockObject();
//			wSyncLockObject.setAddtime(new Date());
//			wSyncLockObject.setRoomclient(currentClient);
//			wSyncLockObject.setSWFLoader(true);
//			
//			Map<String,WhiteboardSyncLockObject> syncListRoom = Application.getWhiteBoardSWFSyncListByRoomid(room_id);
//			
//
//			wSyncLockObject.setCurrentLoadingItem(true);
//			wSyncLockObject.setStarttime(new Date());
//		
//			syncListRoom.put(currentClient.getPublicSID(), wSyncLockObject);
//			Application.setWhiteBoardSWFSyncListByRoomid(room_id, syncListRoom);
//			
//			Iterator<IConnection> it = current.getScope().getConnections();
//			while (it.hasNext()) {
//				IConnection conn = it.next();
//				if (conn instanceof IServiceCapableConnection) {
//					RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
//					if (room_id!=null && room_id == rcl.getRoom_id()) {
//						((IServiceCapableConnection) conn).invoke("sendSWFSyncFlag", new Object[] { wSyncLockObject },this);
//					}
//				}
//			}	
//			
//			return wSyncLockObject;
//			
//			
//		} catch (Exception err) {
//			log.error("[startNewSWFSyncprocess]",err);
//		}
//		return null;
//	}
//	
//	public int sendCompletedSWFSyncEvent() {
//		try {
//			
//			IConnection current = Red5.getConnectionLocal();
//			String streamid = current.getClient().getId();
//			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
//			Long room_id = currentClient.getRoom_id();
//			
//			Map<String,WhiteboardSyncLockObject> syncListRoom = Application.getWhiteBoardSWFSyncListByRoomid(room_id);
//
//			WhiteboardSyncLockObject wSyncLockObject = syncListRoom.get(currentClient.getPublicSID());
//			
//			if (wSyncLockObject == null) {
//				log.error("WhiteboardSyncLockObject not found for this Client "+syncListRoom);
//				return -2;
//			} else if (!wSyncLockObject.isCurrentLoadingItem()) {
//				log.warn("WhiteboardSyncLockObject was not started yet "+syncListRoom);
//				return -3;
//			} else {
//				syncListRoom.remove(currentClient.getPublicSID());
//				Application.setWhiteBoardSWFSyncListByRoomid(room_id, syncListRoom);
//				
//				int numberOfInitial = this.getNumberOfSWFLoaders(syncListRoom);
//				
//				if (numberOfInitial==0){
//					int returnVal = 0;
//					Iterator<IConnection> it = current.getScope().getConnections();
//					while (it.hasNext()) {
//						IConnection conn = it.next();
//						if (conn instanceof IServiceCapableConnection) {
//							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
//							if (room_id!=null && room_id == rcl.getRoom_id()) {
//								returnVal++;
//								((IServiceCapableConnection) conn).invoke("sendSWFSyncCompleteFlag", new Object[] { wSyncLockObject },this);
//							}
//						}
//					}	
//					return returnVal;
//				} else {
//					return -4;
//				}
//			}
//			
//			
//		} catch (Exception err) {
//			log.error("[sendCompletedSWFSyncEvent]",err);
//		}
//		return -1;
//	}
//	
	
//	private int getNumberOfSWFLoaders(Map<String,WhiteboardSyncLockObject> syncListRoom) throws Exception {
//		int number = 0;
//		for (Iterator<String> iter = syncListRoom.keySet().iterator();iter.hasNext();) {
//			WhiteboardSyncLockObject lockObject = syncListRoom.get(iter.next());
//			if (lockObject.isSWFLoader()){
//				number++;
//			}
//		}
//		return number;
//	}	
//	
	public synchronized void removeUserFromAllLists(IScope scope, RoomClient currentClient){
		try {
			
			Long room_id = currentClient.getRoom_id();
			
			//TODO: Maybe we should also check all rooms, independent from the current
			//room_id if there is any user registered
			if (room_id != null) {
				
				
				log.debug("removeUserFromAllLists this.whiteBoardObjectListManager: "+this.whiteBoardObjectListManager);
				log.debug("removeUserFromAllLists room_id: "+room_id);
				
				//Check Initial Loaders
				Map<String,WhiteboardSyncLockObject> syncListRoom = this.whiteBoardObjectListManager.getWhiteBoardSyncListByRoomid(room_id);
				
				WhiteboardSyncLockObject wSyncLockObject = syncListRoom.get(currentClient.getPublicSID());
				
				if (wSyncLockObject != null) {
					syncListRoom.remove(currentClient.getPublicSID());
				}
				this.whiteBoardObjectListManager.setWhiteBoardSyncListByRoomid(room_id, syncListRoom);
				
				int numberOfInitial = this.getNumberOfInitialLoaders(syncListRoom);
				
				log.debug("scope "+scope);

				
				if (numberOfInitial==0 && scope != null){
					Collection<Set<IConnection>> conCollection = scope.getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl != null) {
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											if (!rcl.getPublicSID().equals(currentClient.getPublicSID())) {
												//do not send to current
												((IServiceCapableConnection) conn).invoke("sendSyncCompleteFlag", new Object[] { wSyncLockObject },this);
											}
										}
									}
								}
							}		
						}
					}	
				}
				
				
				//Check Image Loaders
				Map<String,Map<String,WhiteboardSyncLockObject>> syncListRoomImages = this.whiteBoardObjectListManager.getWhiteBoardObjectSyncListByRoomid(room_id);
				
				for (Iterator<String> iter = syncListRoomImages.keySet().iterator();iter.hasNext();) {
					String object_id = iter.next();
					Map<String,WhiteboardSyncLockObject> syncListImages = syncListRoomImages.get(object_id);
					WhiteboardSyncLockObject wImagesSyncLockObject = syncListImages.get(currentClient.getPublicSID());
					if (wImagesSyncLockObject != null) {
						syncListImages.remove(currentClient.getPublicSID());
					}
					this.whiteBoardObjectListManager.setWhiteBoardImagesSyncListByRoomAndObjectId(room_id, object_id, syncListImages);
				}
				
				int numberOfImageLoaders = this.whiteBoardObjectListManager.getWhiteBoardObjectSyncListByRoomid(room_id).size();
				
				if (numberOfImageLoaders==0 && scope != null){
					Collection<Set<IConnection>> conCollection = scope.getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										if (rcl != null) {
											((IServiceCapableConnection) conn).invoke("sendImagesSyncCompleteFlag", new Object[] { "remove" },this);
										} 
										/*
										else if (!rcl.getPublicSID().equals(currentClient.getPublicSID())) {
											//do not send to current
											((IServiceCapableConnection) conn).invoke("sendImagesSyncCompleteFlag", new Object[] { "remove" },this);
										} else {
											log.debug("IS current");
										}
										*/
									}
								}
							}			
						}
					}	
				}
				
				
//				//TODO: Check SWF Loaders
//				Map<String,WhiteboardSyncLockObject> syncListSWF = Application.getWhiteBoardSWFSyncListByRoomid(room_id);
//				
//				WhiteboardSyncLockObject wSWFSyncLockObject = syncListImages.get(currentClient.getPublicSID());
//				
//				if (wSWFSyncLockObject != null) {
//					syncListSWF.remove(currentClient.getPublicSID());
//				}
//				Application.setWhiteBoardSWFSyncListByRoomid(room_id, syncListSWF);
//				
//				int numberOfSWFLoaders = this.getNumberOfSWFLoaders(syncListImages);
//				
//				if (numberOfSWFLoaders==0){
//					Iterator<IConnection> it = current.getScope().getConnections();
//					while (it.hasNext()) {
//						IConnection conn = it.next();
//						if (conn instanceof IServiceCapableConnection) {
//							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
//							if (room_id!=null && room_id == rcl.getRoom_id()) {
//								//do not send to current
//								if (!rcl.getPublicSID().equals(currentClient.getPublicSID())) {
//									((IServiceCapableConnection) conn).invoke("sendSWFSyncCompleteFlag", new Object[] { wSWFSyncLockObject },this);
//								}
//							}
//						}
//					}	
//				}
//				
//				
			}
			
		} catch (Exception err) {
			log.error("[removeUserFromAllLists]",err);
		}
	}
	
	public String[] getClipArtIcons() {
		try {
			
			String current_dir = ScopeApplicationAdapter.webAppPath
									+ File.separatorChar + "public"
									+ File.separatorChar + "cliparts";
			
			File clipart_dir = new File(current_dir);
			
			return clipart_dir.list();
			
		} catch (Exception err) {
			log.error("[getClipArtIcons]",err);
		}
		return null;
	}
	
	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		log.debug("resultReceived: "+arg0);
	}


}
