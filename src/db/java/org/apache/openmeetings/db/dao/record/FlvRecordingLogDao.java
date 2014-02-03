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

import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingLog;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FlvRecordingLogDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public long countErrors(Long recordingId) {
		return em.createNamedQuery("countErrorRecordingLogsByRecording", Long.class)
				.setParameter("recId", recordingId).getSingleResult();
	}	
	
	public List<FlvRecordingLog> getByRecordingId(Long recordingId) {
		return em.createNamedQuery("getRecordingLogsByRecording", FlvRecordingLog.class)
				.setParameter("recId", recordingId).getResultList();
	}	
	
	public void deleteByRecordingId(Long flvRecordingId) {
		try {
			List<FlvRecordingLog> flvRecordingLogs = getByRecordingId(flvRecordingId);
			
			for (FlvRecordingLog flvRecordingLog : flvRecordingLogs) {
				flvRecordingLog = em.find(FlvRecordingLog.class, flvRecordingLog.getFlvRecordingLogId());
				em.remove(flvRecordingLog);
			}
			
		} catch (Exception ex2) {
			log.error("[deleteFLVRecordingLogByRecordingId] ", ex2);
		}
	}
	
	public Long addFLVRecordingLog(String msgType, FlvRecording flvRecording, ConverterProcessResult returnMap) {
		try { 
			FlvRecordingLog flvRecordingLog = new FlvRecordingLog();
			flvRecordingLog.setInserted(new Date());
			flvRecordingLog.setExitValue(returnMap.getExitValue());
			flvRecordingLog.setFlvRecording(flvRecording);
			flvRecordingLog.setFullMessage(returnMap.buildLogMessage());
			flvRecordingLog.setMsgType(msgType);
			
			flvRecordingLog = em.merge(flvRecordingLog);
			Long flvRecordingLogId = flvRecordingLog.getFlvRecordingLogId();
			
			return flvRecordingLogId;
		} catch (Exception ex2) {
			log.error("[addFLVRecordingLog]: ",ex2);
		}
		return -1L;
	}
}
