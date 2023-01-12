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
package org.apache.openmeetings.db.dao.server;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.DaoHelper.only;

import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SOAPLoginDao {
	private static final Logger log = LoggerFactory.getLogger(SOAPLoginDao.class);

	@PersistenceContext
	private EntityManager em;

	public String addSOAPLogin(String sessionHash, RoomOptionsDTO options) {
		SOAPLogin soapLogin = new SOAPLogin();
		soapLogin.setCreated(new Date());
		soapLogin.setUsed(false);
		soapLogin.setRoomId(options.getRoomId());
		soapLogin.setExternalRoomId(options.getExternalRoomId());
		soapLogin.setExternalType(options.getExternalType());
		soapLogin.setAllowSameURLMultipleTimes(options.isAllowSameURLMultipleTimes());
		soapLogin.setHash(randomUUID().toString());
		soapLogin.setRecordingId(options.getRecordingId());
		soapLogin.setSessionHash(sessionHash);
		soapLogin.setModerator(options.isModerator());
		soapLogin.setShowAudioVideoTest(options.isShowAudioVideoTest());
		soapLogin.setAllowRecording(options.isAllowRecording());

		em.persist(soapLogin);
		em.flush();
		Long soapLoginId = soapLogin.getId();

		if (soapLoginId != null) {
			return soapLogin.getHash();
		} else {
			log.error("[addSOAPLogin]: Could not store SOAPLogin");
		}
		return null;
	}

	public SOAPLogin get(String hash) {
		if (hash == null) {
			return null;
		}
		try {
			//MSSql find nothing in case SID is passed as-is without wildcarting '%hash%'
			SOAPLogin sl = only(em.createNamedQuery("getSoapLoginByHash", SOAPLogin.class)
					.setParameter("hash", '%' + hash + '%')
					.getResultList());

			if (sl != null) {
				if (hash.equals(sl.getHash())) {
					return sl;
				} else {
					log.error("[get]: Wrong SOAPLogin was found by hash! {}", hash);
				}
			}
		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
	}

	public void update(SOAPLogin soapLogin) {
		try {
			if (soapLogin.getId() == null) {
				em.persist(soapLogin);
			} else {
				if (!em.contains(soapLogin)) {
					em.merge(soapLogin);
				}
			}
		} catch (Exception ex2) {
			log.error("[update]: ", ex2);
		}
	}
}
