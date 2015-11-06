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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaData.Status;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecordingMetaDataDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingMetaDataDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RecordingDao recordingDao;

	public RecordingMetaData get(Long id) {
		try {
			TypedQuery<RecordingMetaData> query = em.createNamedQuery("getMetaById", RecordingMetaData.class);
			query.setParameter("id", id);

			RecordingMetaData metaData = null;
			try {
				metaData = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return metaData;
		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
	}

	public List<RecordingMetaData> getByRecording(Long recordingId) {
		return em.createNamedQuery("getMetaByRecording", RecordingMetaData.class).setParameter("recordingId", recordingId).getResultList();
	}

	public List<RecordingMetaData> getAudioMetaDataByRecording(Long recordingId) {
		try {
			TypedQuery<RecordingMetaData> query = em.createNamedQuery("getAudioMetaByRecording", RecordingMetaData.class);
			query.setParameter("recordingId", recordingId);
			query.setParameter("none", Status.NONE);

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getAudioMetaDataByRecording]: ", ex2);
		}
		return null;
	}

	public RecordingMetaData getScreenMetaDataByRecording(Long recordingId) {
		try {
			TypedQuery<RecordingMetaData> query = em.createNamedQuery("getScreenMetaByRecording", RecordingMetaData.class);
			query.setParameter("recordingId", recordingId);

			List<RecordingMetaData> metaDatas = query.getResultList();

			if (metaDatas.size() > 0) {
				return metaDatas.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getScreenMetaDataByRecording]: ", ex2);
		}
		return null;
	}

	public Long add(Long recordingId, String freeTextUserName, Date recordStart, Boolean isAudioOnly,
			Boolean isVideoOnly, Boolean isScreenData, String streamName, Integer interiewPodId) {
		try {

			RecordingMetaData metaData = new RecordingMetaData();

			metaData.setDeleted(false);

			metaData.setRecording(recordingDao.get(recordingId));
			metaData.setFreeTextUserName(freeTextUserName);
			metaData.setInserted(new Date());

			metaData.setRecordStart(recordStart);

			metaData.setAudioOnly(isAudioOnly);
			metaData.setVideoOnly(isVideoOnly);
			metaData.setScreenData(isScreenData);

			metaData.setStreamName(streamName);

			metaData.setInteriewPodId(interiewPodId);

			metaData = em.merge(metaData);
			Long metaDataId = metaData.getId();

			return metaDataId;

		} catch (Exception ex2) {
			log.error("[add]: ", ex2);
		}
		return null;
	}

	public Long updateEndDate(Long metaId, Date recordEnd) {
		try {
			RecordingMetaData meta = get(metaId);

			meta.setRecordEnd(recordEnd);
			
			log.debug("updateEndDate :: Start Date :" + meta.getRecordStart());
			log.debug("updateEndDate :: End Date :" + meta.getRecordEnd());
			
			update(meta);
			
			return metaId;
		} catch (Exception ex2) {
			log.error("[updateEndDate]: ", ex2);
		}
		return null;
	}

	public Long update(RecordingMetaData metaData) {
		try {
			if (metaData.getId() == 0) {
				em.persist(metaData);
			} else {
				if (!em.contains(metaData)) {
					em.merge(metaData);
				}
			}
		} catch (Exception ex2) {
			log.error("[update]: ", ex2);
		}
		return null;
	}
}
