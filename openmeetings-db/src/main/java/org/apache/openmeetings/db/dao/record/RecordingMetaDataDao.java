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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaData.Status;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RecordingMetaDataDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingMetaDataDao.class, getWebAppRootKey());
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RecordingDao recordingDao;

	public RecordingMetaData get(Long id) {
		List<RecordingMetaData> list = em.createNamedQuery("getMetaById", RecordingMetaData.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public List<RecordingMetaData> getByRecording(Long recordingId) {
		return em.createNamedQuery("getMetaByRecording", RecordingMetaData.class).setParameter("recordingId", recordingId).getResultList();
	}

	public List<RecordingMetaData> getNotScreenMetaDataByRecording(Long recordingId) {
		return em.createNamedQuery("getNotScreenMetaByRecording", RecordingMetaData.class)
				.setParameter("recordingId", recordingId)
				.setParameter("none", Status.NONE)
				.getResultList();
	}

	public RecordingMetaData getScreenByRecording(Long recordingId) {
		List<RecordingMetaData> list = em.createNamedQuery("getScreenMetaByRecording", RecordingMetaData.class)
				.setParameter("recordingId", recordingId)
				.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public Long add(Long recordingId, Date recordStart, boolean isAudioOnly,
			boolean isVideoOnly, boolean isScreenData, String streamName, String sid) {
		try {

			RecordingMetaData metaData = new RecordingMetaData();
			metaData.setRecording(recordingDao.get(recordingId));
			metaData.setRecordStart(recordStart);
			metaData.setAudioOnly(isAudioOnly);
			metaData.setVideoOnly(isVideoOnly);
			metaData.setScreenData(isScreenData);
			metaData.setStreamName(streamName);
			metaData.setSid(sid);
			metaData = update(metaData);
			return metaData.getId();
		} catch (Exception ex2) {
			log.error("[add]: ", ex2);
		}
		return null;
	}

	public Long updateEndDate(Long metaId, Date recordEnd) {
		try {
			RecordingMetaData meta = get(metaId);

			if (meta != null) {
				meta.setRecordEnd(recordEnd);
				log.debug("updateEndDate :: Start Date : {}", meta.getRecordStart());
				log.debug("updateEndDate :: End Date : {}", meta.getRecordEnd());
				update(meta);
			}
			return metaId;
		} catch (Exception ex2) {
			log.error("[updateEndDate]: ", ex2);
		}
		return null;
	}

	public RecordingMetaData update(RecordingMetaData metaData) {
		log.debug("[update]: ");
		if (metaData.getId() == null) {
			metaData.setInserted(new Date());
			em.persist(metaData);
		} else {
			metaData.setUpdated(new Date());
			metaData = em.merge(metaData);
		}
		return metaData;
	}
}
