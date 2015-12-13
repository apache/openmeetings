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
package org.apache.openmeetings.data.flvrecord;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingLog;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FlvRecordingLogDao {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingLogDao.class);
	@PersistenceContext
	private EntityManager em;
	
	public List<FlvRecordingLog> getFLVRecordingLogByRecordingId(Long flvRecordingId){
		try {
			String hql = "select c from FlvRecordingLog as c where c.flvRecording.flvRecordingId = :flvRecordingId";
			
			TypedQuery<FlvRecordingLog> query = em.createQuery(hql, FlvRecordingLog.class);
			query.setParameter("flvRecordingId", flvRecordingId);
			List<FlvRecordingLog> flvRecordingList = query.getResultList();
			
			return flvRecordingList;
			
		} catch (Exception ex2) {
			log.error("[getFLVRecordingLogByRecordingId] ", ex2);
		}
		return null;
	}	
	
	public void deleteFLVRecordingLogByRecordingId(Long flvRecordingId){
		try {
			List<FlvRecordingLog> flvRecordingLogs = this.getFLVRecordingLogByRecordingId(flvRecordingId);
			
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
