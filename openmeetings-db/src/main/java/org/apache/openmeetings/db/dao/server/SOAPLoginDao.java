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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SOAPLoginDao {
	private static final Logger log = Red5LoggerFactory.getLogger(SOAPLoginDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public String addSOAPLogin(String sessionHash, Long roomId,
			boolean becomemoderator, boolean showAudioVideoTest,
			boolean allowSameURLMultipleTimes, Long recordingId,
			String landingZone, boolean allowRecording) {
		try {
			SOAPLogin soapLogin = new SOAPLogin();
			soapLogin.setCreated(new Date());
			soapLogin.setUsed(false);
			soapLogin.setRoomId(roomId);
			soapLogin.setAllowSameURLMultipleTimes(allowSameURLMultipleTimes);
			soapLogin.setHash(UUID.randomUUID().toString());
			soapLogin.setRecordingId(recordingId);
			soapLogin.setSessionHash(sessionHash);
			soapLogin.setModerator(becomemoderator);
			soapLogin.setShowAudioVideoTest(showAudioVideoTest);
			soapLogin.setLandingZone(landingZone);
			soapLogin.setAllowRecording(allowRecording);

			em.persist(soapLogin);
			em.flush();
			Long soapLoginId = soapLogin.getId();

			if (soapLoginId != null) {
				return soapLogin.getHash();
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addSOAPLogin]: ", ex2);
		}
		return null;
	}

	public SOAPLogin get(String hash) {
		try {
			//MSSql find nothing in case SID is passed as-is without wildcarting '%hash%'
			List<SOAPLogin> sList = em.createNamedQuery("getSoapLoginByHash", SOAPLogin.class)
					.setParameter("hash", String.format("%%%s%%", hash))
					.getResultList();

			if (sList.size() > 1) {
				throw new Exception("there are more then one SOAPLogin with identical hash! " + hash);
			}

			if (sList.size() == 1) {
				return sList.get(0);
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
