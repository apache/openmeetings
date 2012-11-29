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
package org.apache.openmeetings.conference.room.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.persistence.beans.basic.Server;

/**
 * This is actually some maps, a single map is not enough, cause we have
 * multiple keys and multiple views on that list. And also we have combined 
 * keys, for example server + streamId is unique. But the streamId as-is can 
 * be available multiple times.
 * 
 * There are multiple ways to organize a Map by multiple keys, one is to have 
 * multiple lists, for every key needed, there is a separated list.
 * 
 * <ul>
 * <li>client by streamid</li>
 * <li>client by publicSID</li>
 * <li>client by userId</li>
 * <li>clients by roomId</li>
 * <li>TODO: roomIds by server</li>
 * </ul>
 * 
 * @author sebawagner
 * 
 */
public class HashMapStore {
	
	/**
	 * global client list by serverId and streamid
	 */
	private LinkedHashMap<Long, LinkedHashMap<String, RoomClient>> clientsByServer = new LinkedHashMap<Long, LinkedHashMap<String, RoomClient>>();

	/**
	 * global client list by serverId and publicSID, there can be multiple
	 * RoomClients with the same publicSID, the ScreenSharing client has the
	 * same publicSID as the normal client
	 */
	private LinkedHashMap<Long, LinkedHashMap<String, List<RoomClient>>> clientsByServerAndPublicSID = new LinkedHashMap<Long, LinkedHashMap<String, List<RoomClient>>>();

	/**
	 * global client list by serverId and userId, there can be multiple
	 * RoomClients with the same userId, the ScreenSharing client has the
	 * same userId as the normal client
	 */
	private LinkedHashMap<Long, LinkedHashMap<Long, List<RoomClient>>> clientsByServerAndUserId = new LinkedHashMap<Long, LinkedHashMap<Long, List<RoomClient>>>();

	/**
	 * global client list by roomId
	 * 
	 * It is internally a HashMap, not a simple list to make sure the order does not change
	 */
	private LinkedHashMap<Long, LinkedHashMap<String, RoomClient>> clientsByRoomId = new LinkedHashMap<Long, LinkedHashMap<String, RoomClient>>();

	/**
	 * global client list by server and roomId
	 */
	
	/**
	 * An empty Map to be returned instead of null
	 */
	private LinkedHashMap<String, RoomClient> EMPTY_MAP = new LinkedHashMap<String, RoomClient>(0);
	
	/**
	 * An empty List to be returned instead of null
	 */
	private List<RoomClient> EMPTY_LIST = new ArrayList<RoomClient>(0);
	
	/**
	 * null means its locally
	 * 
	 * @param server
	 * @return
	 */
	private Long getIdByServer(Server server) {
		if (server == null) {
			return null;
		}
		return server.getId();
	}

	/**
	 * 
	 * @param server
	 * @param streamId
	 * @param rcl
	 */
	public void put(Server server, String streamId, RoomClient rcl) {

		// By server and streamid
		
		//Server and streamId are always given, if server is null, it means the user is locally
		//and not on any slave host
		LinkedHashMap<String, RoomClient> clientList = clientsByServer.get(getIdByServer(server));
		if (clientList == null) {
			clientList = new LinkedHashMap<String, RoomClient>();
		}
		clientList.put(streamId, rcl);
		clientsByServer.put(getIdByServer(server), clientList);

		// By publicSID
		
		//publicSID id might be null and then change to something, 
		//but we will not want to search for a user with the publicSID == null
		//so as long as publicSID is null, we don't organize the session on a special list
		if (rcl.getPublicSID() != null) {
			LinkedHashMap<String, List<RoomClient>> clientListPublicSID = clientsByServerAndPublicSID
					.get(getIdByServer(server));
			if (clientListPublicSID == null) {
				clientListPublicSID = new LinkedHashMap<String, List<RoomClient>>();
			}
			List<RoomClient> clientsByPublicSIDList = clientListPublicSID.get(rcl
					.getPublicSID());
			if (clientsByPublicSIDList == null) {
				clientsByPublicSIDList = new ArrayList<RoomClient>();
			}
			if (!clientsByPublicSIDList.contains(rcl)) {
				clientsByPublicSIDList.add(rcl);
			}
			clientListPublicSID.put(rcl.getPublicSID(), clientsByPublicSIDList);
			clientsByServerAndPublicSID.put(getIdByServer(server), clientListPublicSID);
		}
		
		// By userId
		
		//user id might be null and then change to something, 
		//but we will not want to search for a user with the userId == null
		//so as long as userId is null, we don't organize the session on a special list
		if (rcl.getUser_id() != null) {
			LinkedHashMap<Long, List<RoomClient>> clientListUserId = clientsByServerAndUserId
					.get(getIdByServer(server));
			if (clientListUserId == null) {
				clientListUserId = new LinkedHashMap<Long, List<RoomClient>>();
			}
			List<RoomClient> clientListUserIdList = clientListUserId.get(rcl
					.getPublicSID());
			if (clientListUserIdList == null) {
				clientListUserIdList = new ArrayList<RoomClient>();
			}
			if (!clientListUserIdList.contains(rcl)) {
				clientListUserIdList.add(rcl);
			}
			clientListUserId.put(rcl.getUser_id(), clientListUserIdList);
			clientsByServerAndUserId.put(getIdByServer(server), clientListUserId);
		}

		// By roomId
		
		//room id might be null and then change to something, 
		//but we will not want to search for a user with the roomId == null
		//so as long as roomId is null, we don't organize the session on a special list
		if (rcl.getRoom_id() != null) {
			LinkedHashMap<String, RoomClient> clientRoomList = clientsByRoomId.get(rcl
					.getRoom_id());
			if (clientRoomList == null) {
				clientRoomList = new LinkedHashMap<String, RoomClient>();
			}
			clientRoomList.put(streamId, rcl);
			clientsByRoomId.put(rcl.getRoom_id(), clientRoomList);
		}
	}

	/**
	 * 
	 * @param server
	 * @param streamId
	 * @return
	 */
	public boolean containsKey(Server server, String streamId) {
		if (clientsByServer.containsKey(getIdByServer(server))) {
			return clientsByServer.get(getIdByServer(server)).containsKey(streamId);
		}
		return false;
	}

	/**
	 * by server and publicSID
	 * 
	 * @param server
	 * @param streamId
	 * @return will return null if the client does not exist in the list
	 */
	public RoomClient get(Server server, String streamId) {
		LinkedHashMap<String, RoomClient> listMap = clientsByServer.get(getIdByServer(server));
		if (listMap != null) {
			return listMap.get(streamId);
		}
		return null;
	}

	/**
	 * 
	 * @param server
	 * @param publicSID
	 * @return will return an empty list if nothing available
	 */
	public List<RoomClient> getClientsByPublicSID(Server server, String publicSID) {
		LinkedHashMap<String, List<RoomClient>> clientListPublicSID = clientsByServerAndPublicSID.get(getIdByServer(server));
		if (clientListPublicSID == null) {
			return EMPTY_LIST;
		}
		List<RoomClient> clientList = clientListPublicSID.get(publicSID);
		if (clientList == null) {
			return EMPTY_LIST;
		}
		return clientList;
	}
	
	/**
	 * get all clients by a specific {@link Server}
	 * 
	 * @param server
	 * @return will return an empty map if nothing available
	 */
	public LinkedHashMap<String, RoomClient> getClientsByServer(Server server) {
		LinkedHashMap<String, RoomClient> listMap = clientsByServer.get(getIdByServer(server));
		if (listMap == null) {
			return EMPTY_MAP;
		}
		return listMap;
	}
	
	/**
	 * 
	 * @param server
	 * @param userId
	 * @return will return an empty list if nothing available
	 */
	public List<RoomClient> getClientsByUserId(Server server, Long userId) {
		LinkedHashMap<Long, List<RoomClient>> clientListUserId = clientsByServerAndUserId.get(getIdByServer(server));
		if (clientListUserId == null) {
			return EMPTY_LIST;
		}
		List<RoomClient> clientList = clientListUserId.get(userId);
		if (clientList == null) {
			return EMPTY_LIST;
		}
		return clientList;
	}
	
	/**
	 * 
	 * @param roomId
	 * @return will return an empty map if nothing available
	 */
	public  LinkedHashMap<String, RoomClient> getClientsByRoomId(Long roomId) {
		LinkedHashMap<String, RoomClient> clients = clientsByRoomId.get(roomId);
		if (clients == null) {
			return EMPTY_MAP;
		}
		return clients;
	}

	public void remove(Server server, String streamId) {
		// TODO Auto-generated method stub
		
		// By server and streamid
		
		//Server and streamId are always given, if server is null, it means the user is locally
		//and not on any slave host
		LinkedHashMap<String, RoomClient> clientList = clientsByServer.get(getIdByServer(server));
		if (clientList == null) {
			clientList = new LinkedHashMap<String, RoomClient>();
		}
		RoomClient rcl = clientList.get(streamId);
		
		if (rcl == null) {
			throw new NullPointerException("Could not find RoomClient with that streamId: "+streamId);
		}
		
		clientList.remove(streamId);
		clientsByServer.put(getIdByServer(server), clientList);

		// By publicSID
		
		//publicSID id might be null and then change to something, 
		//but we will not want to search for a user with the publicSID == null
		//so as long as publicSID is null, we don't organize the session on a special list
		if (rcl.getPublicSID() != null) {
			LinkedHashMap<String, List<RoomClient>> clientListPublicSID = clientsByServerAndPublicSID
					.get(getIdByServer(server));
			if (clientListPublicSID == null) {
				clientListPublicSID = new LinkedHashMap<String, List<RoomClient>>();
			}
			List<RoomClient> clientsByPublicSIDList = clientListPublicSID.get(rcl
					.getPublicSID());
			if (clientsByPublicSIDList == null) {
				clientsByPublicSIDList = new ArrayList<RoomClient>();
			}
			clientsByPublicSIDList.remove(rcl);
			clientListPublicSID.put(rcl.getPublicSID(), clientsByPublicSIDList);
			clientsByServerAndPublicSID.put(getIdByServer(server), clientListPublicSID);
		}
		
		// By userId
		
		//user id might be null and then change to something, 
		//but we will not want to search for a user with the userId == null
		//so as long as userId is null, we don't organize the session on a special list
		if (rcl.getUser_id() != null) {
			LinkedHashMap<Long, List<RoomClient>> clientListUserId = clientsByServerAndUserId
					.get(getIdByServer(server));
			if (clientListUserId == null) {
				clientListUserId = new LinkedHashMap<Long, List<RoomClient>>();
			}
			List<RoomClient> clientListUserIdList = clientListUserId.get(rcl
					.getPublicSID());
			if (clientListUserIdList == null) {
				clientListUserIdList = new ArrayList<RoomClient>();
			}
			clientListUserIdList.remove(rcl);
			clientListUserId.put(rcl.getUser_id(), clientListUserIdList);
			clientsByServerAndUserId.put(getIdByServer(server), clientListUserId);
		}

		// By roomId
		
		//room id might be null and then change to something, 
		//but we will not want to search for a user with the roomId == null
		//so as long as roomId is null, we don't organize the session on a special list
		if (rcl.getRoom_id() != null) {
			LinkedHashMap<String, RoomClient> clientRoomList = clientsByRoomId.get(rcl
					.getRoom_id());
			if (clientRoomList == null) {
				clientRoomList = new LinkedHashMap<String, RoomClient>();
			}
			clientRoomList.remove(streamId);
			clientsByRoomId.put(rcl.getRoom_id(), clientRoomList);
		}
		
	}

	public int size() {
		int size = 0;
		for (Entry<Long, LinkedHashMap<String, RoomClient>> entry : clientsByServer.entrySet()) {
			size += entry.getValue().size();
		}
		return size;
	}
	
	public int sizeByServer(Server server) {
		if (clientsByServer.get(getIdByServer(server)) == null) {
			return 0;
		}
		return clientsByServer.get(getIdByServer(server)).size();
	}

	public LinkedHashMap<Long, LinkedHashMap<String, RoomClient>> values() {
		return clientsByServer;
	}

}
