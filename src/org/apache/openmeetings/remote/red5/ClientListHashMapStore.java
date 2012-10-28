package org.apache.openmeetings.remote.red5;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.rooms.RoomClient;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientListHashMapStore implements IClientList {

	private static HashMap<String, RoomClient> clientList = new HashMap<String, RoomClient>();

	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientListHashMapStore.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ManageCryptStyle manageCryptStyle;
	
	/**
	 * Get current clients and extends the room client with its potential 
	 * audio/video client and settings
	 * 
	 * @param room_id
	 * @return
	 */
	public List<RoomClient> getRoomClients(Long room_id) {
		try {
			return this.getClientListByRoom(room_id);
		} catch (Exception err) {
			log.error("[getRoomClients]", err);
		}
		return null;
	}

	public synchronized RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl, boolean isAVClient) {
		try {

			// Store the Connection into a bean and add it to the HashMap
			RoomClient rcm = new RoomClient();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope(scopeName);
			long thistime = new Date().getTime();
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(thistime).toString()));

			rcm.setUserport(remotePort);
			rcm.setUserip(remoteAddress);
			rcm.setSwfurl(swfUrl);
			rcm.setIsMod(new Boolean(false));
			rcm.setCanDraw(new Boolean(false));
			rcm.setIsAVClient(isAVClient);

			if (clientList.containsKey(streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			clientList.put(rcm.getStreamid(), rcm);

			log.debug(" :: addClientListItem :: " + rcm.getRoomClientId());

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	public synchronized Collection<RoomClient> getAllClients() {
		return clientList.values();
	}

	public synchronized RoomClient getClientByStreamId(String streamId) {
		try {
			if (!clientList.containsKey(streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return clientList.get(streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}
	
	/**
	 * Additionally checks if the client receives sync events
	 * 
	 * Sync events will no be broadcasted to:
	 * - Screensharing users
	 * - Audio/Video connections only
	 * 
	 * @param streamId
	 * @return
	 */
	public synchronized RoomClient getSyncClientByStreamId(String streamId) {
		try {
			if (!clientList.containsKey(streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			
			RoomClient rcl = clientList.get(streamId);
			
			if (rcl == null) {
				return null;
			}
			
			if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				return null;
			}
			
			return clientList.get(streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public RoomClient getClientByPublicSID(String publicSID, boolean isAVClient) {
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				RoomClient rcl = clientList.get(iter.next());
				
				if (!rcl.getPublicSID().equals(publicSID)) {
					continue;
				}
				if (rcl.getIsAVClient() != isAVClient) {
					continue;
				}
				
				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	public synchronized RoomClient getClientByUserId(Long userId) {
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				RoomClient rcl = clientList.get(iter.next());
				if (rcl.getUser_id().equals(userId)) {
					return rcl;
				}
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}
	
	/**
	 * Update the session object of the audio/video-connection and additionally swap the 
	 * values to the session object of the user that holds the full session object
	 * @param streamId
	 * @param rcm
	 * @return
	 */
	public synchronized Boolean updateAVClientByStreamId(String streamId,
			RoomClient rcm) {
		try {
			
			//get the corresponding user session object and update the settings
			RoomClient rclUsual = getClientByPublicSID(rcm.getPublicSID(), false);
			if (rclUsual != null) {
				rclUsual.setBroadCastID(rcm.getBroadCastID());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setVHeight(rcm.getVHeight());
				rclUsual.setVWidth(rcm.getVWidth());
				rclUsual.setVX(rcm.getVX());
				rclUsual.setVY(rcm.getVY());
				if (clientList.containsKey(rclUsual.getStreamid())) {
					clientList.put(rclUsual.getStreamid(), rclUsual);
				} else {
					 log.debug("Tried to update a non existing Client " + rclUsual.getStreamid());
				}
			}
			
			updateClientByStreamId(streamId, rcm);
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean updateClientByStreamId(String streamId,
			RoomClient rcm) {
		try {
			if (clientList.containsKey(streamId)) {
				clientList.put(streamId, rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[updateClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean removeClient(String streamId) {
		try {
			if (clientList.containsKey(streamId)) {
				clientList.remove(streamId);
				// log.debug(":: removeClient ::"+clientList.size());
				return true;
			} else {
				log.debug("Tried to remove a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return null;
	}

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 * 
	 * @return
	 */
	public synchronized List<RoomClient> getClientListByRoom(
			Long room_id) {
		List<RoomClient> roomClientList = new ArrayList<RoomClient>();
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				String key = iter.next();
				RoomClient rcl = clientList.get(key);
				
				// client initialized and same room
				if (rcl.getRoom_id() == null || !room_id.equals(rcl.getRoom_id())) {
					continue;
				}
				if (rcl.getIsScreenClient() == null ||
						rcl.getIsScreenClient()) {
					continue;
				}
				if (rcl.getIsAVClient()) {
					continue;
				}
					
				//Only parse really those users out that are really a full session object 
				//and no pseudo session object like the audio/video or screen sharing connection 
				roomClientList.add(rcl);
					
			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}
	
	public synchronized List<RoomClient> getClientListByRoomAll(
			Long room_id) {
		List<RoomClient> roomClientList = new ArrayList<RoomClient>();
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				String key = iter.next();
				// log.debug("getClientList key: "+key);
				RoomClient rcl = clientList.get(key);

				if (rcl.getRoom_id() != null
						&& rcl.getRoom_id().equals(room_id)) {
					// same room
					roomClientList.add(rcl);
				}
			}
		} catch (Exception err) {
			log.error("[getClientListByRoomAll]", err);
		}
		return roomClientList;
	}

	/**
	 * get the current Moderator in this room
	 * 
	 * @param roomname
	 * @return
	 */
	public synchronized List<RoomClient> getCurrentModeratorByRoom(Long room_id) {

		List<RoomClient> rclList = new LinkedList<RoomClient>();
		for (Iterator<String> iter = clientList.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			RoomClient rcl = clientList.get(key);
			//
			log.debug("*..*unsetModerator ClientList key: " + rcl.getStreamid());
			//
			// Check if the Client is in the same room
			if (room_id != null && room_id.equals(rcl.getRoom_id())
					&& rcl.getIsMod()) {
				log.debug("found client who is the Moderator: " + rcl);
				rclList.add(rcl);
			}
		}

		return rclList;
	}

	public synchronized SearchResult<RoomClient> getListByStartAndMax(int start, int max,
			String orderby, boolean asc) {
		SearchResult<RoomClient> sResult = new SearchResult<RoomClient>();
		sResult.setObjectName(RoomClient.class.getName());
		sResult.setRecords(Long.valueOf(clientList.size()).longValue());
		LinkedList<RoomClient> myList = new LinkedList<RoomClient>();

		int i = 0;
		Iterator<String> iter = clientList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (i >= start) {
				myList.add(clientList.get(key));
			}
			if (i > max) {
				break;
			}
			i++;
		}
		sResult.setResult(myList);

		return sResult;
	}

	public synchronized void removeAllClients() {
		try {
			clientList.clear();
		} catch (Exception err) {
			log.error("[removeAllClients]", err);
		}
	}


	public long getRecordingCount(long roomId) {
		List<RoomClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfRecordingUsers = 0;
		for (RoomClient rcl : currentClients) {
			if (rcl.isStartRecording()) {
				numberOfRecordingUsers++;
			}
		}
		return numberOfRecordingUsers;
	}

	public long getPublisingCount(long roomId) {
		List<RoomClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfPublishingUsers = 0;
		for (RoomClient rcl : currentClients) {
			if (rcl.isStreamPublishStarted()) {
				numberOfPublishingUsers++;
			}
		}
		return numberOfPublishingUsers;
	}

}
