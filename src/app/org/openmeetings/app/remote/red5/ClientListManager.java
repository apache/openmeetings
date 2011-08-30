package org.openmeetings.app.remote.red5;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientListManager {

	private static HashMap<String, RoomClient> clientList = new HashMap<String, RoomClient>();

	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientListManager.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private ManageCryptStyle manageCryptStyle;

	public synchronized RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl) {
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

			if (clientList.containsKey(streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			clientList.put(rcm.getStreamid(), rcm);

			// log.error(" :: addClientListItem :: "+clientList.size());

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	public synchronized HashMap<String, RoomClient> getClientList() {
		return clientList;
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

	public synchronized RoomClient getClientByPublicSID(String publicSID) {
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				RoomClient rcl = clientList.get(iter.next());
				if (rcl.getPublicSID().equals(publicSID)) {
					return rcl;
				}
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
			log.error("[remoteClient]", err);
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
	public synchronized HashMap<String, RoomClient> getClientListByRoom(
			Long room_id) {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				String key = iter.next();
				// log.debug("getClientList key: "+key);
				RoomClient rcl = clientList.get(key);
				// same room, same domain
				if (room_id != null && room_id.equals(rcl.getRoom_id())) {
					if (rcl.getIsScreenClient() != null
							&& rcl.getIsScreenClient()) {
						// continue
					} else {
						roomClientList.put(key, rcl);
					}
				}
			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	public synchronized HashMap<String, RoomClient> getClientListByRoomAll(
			Long room_id) {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		try {
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				String key = iter.next();
				// log.debug("getClientList key: "+key);
				RoomClient rcl = clientList.get(key);

				if (rcl.getRoom_id() != null
						&& rcl.getRoom_id().equals(room_id)) {
					// same room
					roomClientList.put(key, rcl);
				}
			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	/**
	 * get the current Moderator in this room
	 * 
	 * @param roomname
	 * @return
	 */
	public synchronized List<RoomClient> getCurrentModeratorByRoom(Long room_id)
			throws Exception {

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

	public synchronized SearchResult getListByStartAndMax(int start, int max,
			String orderby, boolean asc) throws Exception {

		SearchResult sResult = new SearchResult();
		sResult.setObjectName(RoomClient.class.getName());
		sResult.setRecords(Long.valueOf(clientList.size()).longValue());
		LinkedList<RoomClient> myList = new LinkedList<RoomClient>();

		int i = 0;
		// TODO Auto-generated method stub
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

}
