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
package org.apache.openmeetings.core.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.openmeetings.core.session.store.IClientPersistenceStore;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.server.ClientSessionInfo;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handle {@link Client} objects.
 * 
 * Use a kind of decorator pattern to inject the {@link Server} into every call.
 * 
 * @author sebawagner
 * 
 */
public class SessionManager implements ISessionManager {
	
	protected static final Logger log = Red5LoggerFactory.getLogger(
			SessionManager.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private ServerUtil serverUtil;
	
	/**
	 * Injected via Spring, needs a getter/setter because it can be configured
	 * Autowired will not suit here as there are multiple implementations of the
	 * {@link IClientPersistenceStore}
	 */
	private IClientPersistenceStore cache;
	
	public IClientPersistenceStore getCache() {
		return cache;
	}

	public void setCache(IClientPersistenceStore cache) {
		this.cache = cache;
	}

	private ISessionManager sessionManager = new ISessionManager() {
		
		public synchronized Client addClientListItem(String streamId,
				String scopeName, Integer remotePort, String remoteAddress,
				String swfUrl, boolean isAVClient, Server server) {
			try {

				// Store the Connection into a bean and add it to the HashMap
				Client rcm = new Client();
				rcm.setConnectedSince(new Date());
				rcm.setStreamid(streamId);
				rcm.setScope(scopeName);
				long random = System.currentTimeMillis() + new BigInteger(256, new Random()).longValue();
				
				rcm.setPublicSID(ManageCryptStyle.getInstanceOfCrypt()
						.createPassPhrase(String.valueOf(random).toString()));

				rcm.setServer(server);
				rcm.setUserport(remotePort);
				rcm.setUserip(remoteAddress);
				rcm.setSwfurl(swfUrl);
				rcm.setIsMod(new Boolean(false));
				rcm.setCanDraw(new Boolean(false));
				rcm.setIsAVClient(isAVClient);

				if (cache.containsKey(null, streamId)) {
					log.error("Tried to add an existing Client " + streamId);
					return null;
				}

				cache.put(rcm.getStreamid(), rcm);

				return rcm;
			} catch (Exception err) {
				log.error("[addClientListItem]", err);
			}
			return null;
		}

		public synchronized Collection<Client> getClients() {
			return cache.getClients();
		}
		
		public synchronized Collection<Client> getClientsWithServer() {
			return cache.getClientsWithServer();
		}

		public synchronized Client getClientByStreamId(String streamId, Server server) {
			try {
				if (!cache.containsKey(server, streamId)) {
					log.debug("Tried to get a non existing Client " + streamId + " server " + server);
					return null;
				}
				return cache.get(server, streamId);
			} catch (Exception err) {
				log.error("[getClientByStreamId]", err);
			}
			return null;
		}

		public Client getClientByPublicSID(String publicSID, boolean isAVClient, Server server) {
			try {
				for (Client rcl : cache.getClientsByPublicSID(server, publicSID)) {
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
		
		public ClientSessionInfo getClientByPublicSIDAnyServer(String publicSID, boolean isAVClient) {
			try {
				for (Entry<Long,List<Client>> entry : cache.getClientsByPublicSID(publicSID).entrySet()) {
					for (Client rcl : entry.getValue()) {
						if (rcl.getIsAVClient() != isAVClient) {
							continue;
						}
						return new ClientSessionInfo(rcl, entry.getKey());
					}
				}
			} catch (Exception err) {
				log.error("[getClientByPublicSIDAnyServer]", err);
			}
			return null;
		}

		public synchronized Client getClientByUserId(Long userId) {
			try {
				for (Client rcl : cache.getClientsByUserId(null, userId)) {
					
					if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
						continue;
					}
					
					if (rcl.getIsAVClient()) {
						continue;
					}
					
					return rcl;
				}
			} catch (Exception err) {
				log.error("[getClientByUserId]", err);
			}
			return null;
		}

		public synchronized Boolean updateAVClientByStreamId(String streamId, Client rcm, Server server) {
			try {
				// get the corresponding user session object and update the settings
				Client rclUsual = getClientByPublicSID(rcm.getPublicSID(), false, server);
				if (rclUsual != null) {
					rclUsual.setBroadCastID(rcm.getBroadCastID());
					rclUsual.setAvsettings(rcm.getAvsettings());
					rclUsual.setVHeight(rcm.getVHeight());
					rclUsual.setVWidth(rcm.getVWidth());
					rclUsual.setVX(rcm.getVX());
					rclUsual.setVY(rcm.getVY());
					Client rclSaved = cache.get(server, rclUsual.getStreamid());
					if (rclSaved != null) {
						cache.put(rclUsual.getStreamid(), rclUsual);
					} else {
						log.debug("Tried to update a non existing Client " + rclUsual.getStreamid());
					}
				}

				updateClientByStreamId(streamId, rcm, false, server);
			} catch (Exception err) {
				log.error("[updateAVClientByStreamId]", err);
			}
			return null;
		}

		public synchronized Boolean updateClientByStreamId(String streamId,
				Client rcm, boolean updateRoomCount, Server server) {
			try {
				
				Client rclSaved = cache.get(server, streamId);
				
				if (rclSaved != null) {
					cache.put(streamId, rcm);
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

		public synchronized Boolean removeClient(String streamId, Server server) {
			try {
				if (cache.containsKey(server,streamId)) {
					cache.remove(server,streamId);
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

		public synchronized ArrayList<Client> getClientListByRoom(Long roomId) {
			ArrayList<Client> roomClientList = new ArrayList<Client>();
			try {

				for (Client rcl : cache.getClientsByRoomId(roomId)) {

					if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
						continue;
					}
					if (rcl.getIsAVClient()) {
						continue;
					}

					// Only parse really those users out that are really a full session object
					// and no pseudo session object like the audio/video or screen
					// sharing connection
					roomClientList.add(rcl);
				}
			} catch (Exception err) {
				log.error("[getClientListByRoom]", err);
			}
			return roomClientList;
		}
		
		public synchronized Collection<Client> getClientListByRoomAll(Long roomId) {
			try {
				return cache.getClientsByRoomId(roomId);
			} catch (Exception err) {
				log.error("[getClientListByRoomAll]", err);
			}
			return null;
		}

		public synchronized List<Client> getCurrentModeratorByRoom(Long room_id) {
			List<Client> rclList = new LinkedList<Client>();
			List<Client> currentClients = this.getClientListByRoom(room_id);
			for (Client rcl : currentClients) {
				if (rcl.getIsMod()) {
					rclList.add(rcl);
				}
			}

			return rclList;
		}

		// FIXME not sorted
		public synchronized SearchResult<Client> getListByStartAndMax(
				int start, int max, String orderby, boolean asc) {
			SearchResult<Client> sResult = new SearchResult<Client>();
			sResult.setObjectName(Client.class.getName());
			sResult.setRecords(Long.valueOf(cache.size()).longValue());
			sResult.setResult(cache.getClientsWithServer());
			return sResult;
		}

		public long getRecordingCount(long roomId) {
			List<Client> currentClients = this.getClientListByRoom(roomId);
			int numberOfRecordingUsers = 0;
			for (Client rcl : currentClients) {
				if (rcl.isStartRecording()) {
					numberOfRecordingUsers++;
				}
			}
			return numberOfRecordingUsers;
		}

		public long getPublishingCount(long roomId) {
			List<Client> currentClients = this.getClientListByRoom(roomId);
			int numberOfPublishingUsers = 0;
			for (Client rcl : currentClients) {
				if (rcl.isStreamPublishStarted()) {
					numberOfPublishingUsers++;
				}
			}
			return numberOfPublishingUsers;
		}
		
		public List<Long> getActiveRoomIdsByServer(Server server) {
			return cache.getRoomsIdsByServer(server);
		}
		
		
		public String getSessionStatistics() {
			return cache.getDebugInformation(Arrays.asList(IClientPersistenceStore.DEBUG_DETAILS.SIZE));
		}

		public void sessionStart() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public Client addClientListItem(String streamId, String scopeName,
			Integer remotePort, String remoteAddress, String swfUrl,
			boolean isAVClient, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.addClientListItem(streamId, scopeName,
				remotePort, remoteAddress, swfUrl, isAVClient, server);
	}

	public Collection<Client> getClients() {
		return sessionManager.getClients();
	}
	
	public Collection<Client> getClientsWithServer() {
		return sessionManager.getClientsWithServer();
	}

	public Client getClientByStreamId(String streamId, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.getClientByStreamId(streamId, server);
	}

	public Client getClientByPublicSID(String publicSID, boolean isAVClient,
			Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.getClientByPublicSID(publicSID, isAVClient,
				server);
	}

	public ClientSessionInfo getClientByPublicSIDAnyServer(String publicSID,
			boolean isAVClient) {
		return sessionManager.getClientByPublicSIDAnyServer(publicSID,
				isAVClient);
	}

	public Client getClientByUserId(Long userId) {
		return sessionManager.getClientByUserId(userId);
	}

	public Boolean updateAVClientByStreamId(String streamId, Client rcm, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.updateAVClientByStreamId(streamId, rcm, server);
	}

	public Boolean updateClientByStreamId(String streamId, Client rcm,
			boolean updateRoomCount, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.updateClientByStreamId(streamId, rcm,
				updateRoomCount, server);
	}

	public Boolean removeClient(String streamId, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		return sessionManager.removeClient(streamId, server);
	}

	public List<Client> getClientListByRoom(Long room_id) {
		return sessionManager.getClientListByRoom(room_id);
	}

	public Collection<Client> getClientListByRoomAll(Long room_id) {
		return sessionManager.getClientListByRoomAll(room_id);
	}

	public List<Client> getCurrentModeratorByRoom(Long room_id) {
		return sessionManager.getCurrentModeratorByRoom(room_id);
	}

	public SearchResult<Client> getListByStartAndMax(int start, int max,
			String orderby, boolean asc) {
		return sessionManager.getListByStartAndMax(start, max, orderby, asc);
	}

	public long getRecordingCount(long roomId) {
		return sessionManager.getRecordingCount(roomId);
	}

	public long getPublishingCount(long roomId) {
		return sessionManager.getPublishingCount(roomId);
	}

	public List<Long> getActiveRoomIdsByServer(Server server) {
		return sessionManager.getActiveRoomIdsByServer(server == null ? serverUtil.getCurrentServer() : server);
	}

	public String getSessionStatistics() {
		return sessionManager.getSessionStatistics();
	}

	public void sessionStart() {
		sessionManager.sessionStart();
	}
	
}
