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
package org.apache.openmeetings.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.basic.SOAPLogin;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SOAPLoginDao {

	private static final Logger log = Red5LoggerFactory.getLogger(
			SOAPLoginDao.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ManageCryptStyle manageCryptStyle;

	public String addSOAPLogin(String sessionHash, Long room_id,
			boolean becomemoderator, boolean showAudioVideoTest,
			boolean allowSameURLMultipleTimes, Long recording_id,
			boolean showNickNameDialog, String landingZone,
			boolean allowRecording) {
		try {

			String thistime = "TIME_" + (new Date().getTime());

			String hash = manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(thistime);

			SOAPLogin soapLogin = new SOAPLogin();
			soapLogin.setCreated(new Date());
			soapLogin.setUsed(false);
			soapLogin.setRoom_id(room_id);
			soapLogin.setAllowSameURLMultipleTimes(allowSameURLMultipleTimes);
			soapLogin.setHash(hash);
			soapLogin.setRoomRecordingId(recording_id);
			soapLogin.setSessionHash(sessionHash);
			soapLogin.setBecomemoderator(becomemoderator);
			soapLogin.setShowAudioVideoTest(showAudioVideoTest);
			soapLogin.setShowNickNameDialog(showNickNameDialog);
			soapLogin.setLandingZone(landingZone);
			soapLogin.setAllowRecording(allowRecording);

			soapLogin = em.merge(soapLogin);
			Long soapLoginId = soapLogin.getSoapLoginId();

			if (soapLoginId > 0) {
				return hash;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addSOAPLogin]: ", ex2);
		}
		return null;
	}

	public SOAPLogin getSOAPLoginByHash(String hash) {
		try {
			String hql = "select sl from SOAPLogin as sl "
					+ "WHERE sl.hash LIKE :hash";
			TypedQuery<SOAPLogin> query = em.createQuery(hql, SOAPLogin.class);
			query.setParameter("hash", hash);
			List<SOAPLogin> sList = query.getResultList();

			if (sList.size() > 1) {
				throw new Exception(
						"there are more then one SOAPLogin with identical hash! "
								+ hash);
			}

			if (sList.size() == 1) {
				return sList.get(0);
			}

		} catch (Exception ex2) {
			log.error("[getSOAPLoginByHash]: ", ex2);
		}
		return null;
	}

	public void updateSOAPLogin(SOAPLogin soapLogin) {
		try {
			if (soapLogin.getSoapLoginId() == 0) {
				em.persist(soapLogin);
			} else {
				if (!em.contains(soapLogin)) {
					em.merge(soapLogin);
				}
			}
		} catch (Exception ex2) {
			log.error("[updateSOAPLogin]: ", ex2);
		}
	}

}
