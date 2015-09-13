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
package org.apache.openmeetings.db.dao.record;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingLog;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecordingLogDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingLogDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public long countErrors(Long recordingId) {
		return em.createNamedQuery("countErrorRecordingLogsByRecording", Long.class)
				.setParameter("recId", recordingId).getSingleResult();
	}	
	
	public List<RecordingLog> getByRecordingId(Long recordingId) {
		return em.createNamedQuery("getRecordingLogsByRecording", RecordingLog.class)
				.setParameter("recId", recordingId).getResultList();
	}	
	
	public void deleteByRecordingId(Long recordingId) {
		em.createNamedQuery("deleteErrorRecordingLogsByRecording").setParameter("recId", recordingId).executeUpdate();
	}
	
	public Long add(String msgType, Recording recording, ConverterProcessResult returnMap) {
		try { 
			RecordingLog log = new RecordingLog();
			log.setInserted(new Date());
			log.setExitValue(returnMap.getExitValue());
			log.setRecording(recording);
			log.setFullMessage(returnMap.buildLogMessage());
			log.setMsgType(msgType);
			
			log = em.merge(log);
			Long logId = log.getId();
			
			return logId;
		} catch (Exception ex2) {
			log.error("[add]: ",ex2);
		}
		return -1L;
	}
}
