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
package org.apache.openmeetings.remote.red5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.rooms.RoomClient;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ClientListDatabaseStore implements IClientList {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientListDatabaseStore.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ManageCryptStyle manageCryptStyle;
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getRoomClients(java.lang.Long)
	 */
	public List<RoomClient> getRoomClients(Long room_id) {
		return getClientListByRoom(room_id);
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#addClientListItem(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, boolean)
	 */
	public RoomClient addClientListItem(String streamId,
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

			if (getClientByStreamId(streamId) != null) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			rcm = em.merge(rcm);

			log.debug(" :: addClientListItem :: " + rcm.getRoomClientId());

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getAllClients()
	 */
	public Collection<RoomClient> getAllClients() {
		TypedQuery<RoomClient> q = em.createNamedQuery("getAllRoomClients", RoomClient.class);
		return q.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getClientByStreamId(java.lang.String)
	 */
	public RoomClient getClientByStreamId(String streamId) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByStreamId", RoomClient.class);
			q.setParameter("streamid", streamId);
			
			if (q.getResultList().isEmpty()) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return q.getSingleResult();
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getSyncClientByStreamId(java.lang.String)
	 */
	public RoomClient getSyncClientByStreamId(String streamId) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByStreamIdNonScreen", RoomClient.class);
			q.setParameter("streamid", streamId);
			if (q.getResultList().isEmpty()) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return q.getSingleResult();
		} catch (Exception err) {
			log.error("[getSyncClientByStreamId]", err);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getClientByPublicSID(java.lang.String, boolean)
	 */
	public RoomClient getClientByPublicSID(String publicSID, boolean isAVClient) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByPublicSidAvClient", RoomClient.class);
			q.setParameter("publicSID", publicSID);
			q.setParameter("isAVClient", isAVClient);
				
			List<RoomClient> r = q.getResultList();
			return r.isEmpty() ? null : r.get(0);
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getClientByUserId(java.lang.Long)
	 */
	public RoomClient getClientByUserId(Long userId) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByUserId", RoomClient.class);
			q.setParameter("userId", userId);
			
			List<RoomClient> r = q.getResultList();
			return r.isEmpty() ? null : r.get(0);
		} catch (Exception err) {
			log.error("[getClientByUserId]", err);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#updateAVClientByStreamId(java.lang.String, org.apache.openmeetings.persistence.beans.rooms.RoomClient)
	 */
	public Boolean updateAVClientByStreamId(String streamId, RoomClient rcm) {
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
			
				rclUsual = em.merge(rclUsual);
			}
			updateClientByStreamId(streamId, rcm);
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#updateClientByStreamId(java.lang.String, org.apache.openmeetings.persistence.beans.rooms.RoomClient)
	 */
	public Boolean updateClientByStreamId(String streamId, RoomClient rcm) {
		try {
			if (getClientByStreamId(streamId) != null) {
				rcm = em.merge(rcm);
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

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#removeClient(java.lang.String)
	 */
	public Boolean removeClient(String streamId) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("deleteByStreamId", RoomClient.class);
			q.setParameter("streamid", streamId);
			
			q.executeUpdate();
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getClientListByRoom(java.lang.Long)
	 */
	public List<RoomClient> getClientListByRoom(Long room_id) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByRoomId", RoomClient.class);
			q.setParameter("room_id", room_id);
					
			return q.getResultList();
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return null;
	}
	
	

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getClientListByRoomAll(java.lang.Long)
	 */
	public List<RoomClient> getClientListByRoomAll(Long room_id) {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByRoomIdAll", RoomClient.class);
			q.setParameter("room_id", room_id);

			return q.getResultList();
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return new ArrayList<RoomClient>();
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getCurrentModeratorByRoom(java.lang.Long)
	 */
	public List<RoomClient> getCurrentModeratorByRoom(Long room_id) {
		if (room_id != null) {
			TypedQuery<RoomClient> q = em.createNamedQuery("getByRoomIdMod", RoomClient.class);
			q.setParameter("room_id", room_id);
			return q.getResultList();
		}
		return new ArrayList<RoomClient>();
	}

	//FIXME not sorted
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getListByStartAndMax(int, int, java.lang.String, boolean)
	 */
	public SearchResult<RoomClient> getListByStartAndMax(int start, int max, String orderby, boolean asc) {
		SearchResult<RoomClient> sResult = new SearchResult<RoomClient>();
		sResult.setObjectName(RoomClient.class.getName());

		TypedQuery<RoomClient> q = em.createNamedQuery("getAllRoomClients", RoomClient.class);
		q.setFirstResult(start);
		q.setMaxResults(max);
		
		List<RoomClient> l = q.getResultList();
		sResult.setRecords((long)l.size());
		sResult.setResult(l);

		return sResult;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#removeAllClients()
	 */
	public void removeAllClients() {
		try {
			TypedQuery<RoomClient> q = em.createNamedQuery("deleteAll", RoomClient.class);
			q.executeUpdate();
		} catch (Exception err) {
			log.error("[removeAllClients]", err);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getRecordingCount(long)
	 */
	public long getRecordingCount(long roomId) {
		TypedQuery<Long> q = em.createNamedQuery("getRecordingCountByRoomId", Long.class);
		q.setParameter("room_id", roomId);
		return q.getSingleResult();
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.remote.red5.IClientList#getPublisingCount(long)
	 */
	public long getPublisingCount(long roomId) {
		TypedQuery<Long> q = em.createNamedQuery("getPublisingCountByRoomId", Long.class);
		q.setParameter("room_id", roomId);
		return q.getSingleResult();
	}
}
