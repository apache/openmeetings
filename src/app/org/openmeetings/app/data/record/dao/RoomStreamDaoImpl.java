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
package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.RoomStream;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomStreamDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomStreamDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RoomClientDaoImpl roomClientDao;

	public List<RoomStream> getRoomStreamsByRoomRecordingId(Long roomrecordingId) {
		try {
			
			String hql = "select c from RoomStream as c " +
						"where c.roomRecording.roomrecordingId = :roomrecordingId";
			
			TypedQuery<RoomStream> query = em.createQuery(hql, RoomStream.class);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<RoomStream> ll = query.getResultList();
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getRoomStreamsByRoomRecordingId]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomStream(RoomStream roomStream) {
		try {
			
			//Fill and remove duplicated RoomClient Objects
			if (roomStream.getRcl() != null) {
				roomStream.setRcl(roomClientDao.getAndAddRoomClientByPublicSID(roomStream.getRcl()));
			}
			
			roomStream = em.merge(roomStream);
			Long roomStreamId = roomStream.getRoomStreamId();
			
			return roomStreamId;
		} catch (Exception ex2) {
			log.error("[addRoomStream]: " , ex2);
		}
		return null;
	}
}
