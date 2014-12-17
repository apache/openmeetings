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

import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData.Status;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FlvRecordingMetaDataDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDataDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private FlvRecordingDao flvRecordingDao;

	public FlvRecordingMetaData get(Long id) {
		try {
			TypedQuery<FlvRecordingMetaData> query = em.createNamedQuery("getMetaById", FlvRecordingMetaData.class);
			query.setParameter("id", id);

			FlvRecordingMetaData flvRecordingMetaData = null;
			try {
				flvRecordingMetaData = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return flvRecordingMetaData;
		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
	}

	public List<FlvRecordingMetaData> getByRecording(Long recordingId) {
		try {
			TypedQuery<FlvRecordingMetaData> query = em.createNamedQuery("getMetaByRecording", FlvRecordingMetaData.class);
			query.setParameter("recordingId", recordingId);

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getByRecording]: ", ex2);
		}
		return null;
	}

	public List<FlvRecordingMetaData> getAudioMetaDataByRecording(Long recordingId) {
		try {
			TypedQuery<FlvRecordingMetaData> query = em.createNamedQuery("getAudioMetaByRecording", FlvRecordingMetaData.class);
			query.setParameter("recordingId", recordingId);
			query.setParameter("none", Status.NONE);

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getAudioMetaDataByRecording]: ", ex2);
		}
		return null;
	}

	public FlvRecordingMetaData getScreenMetaDataByRecording(Long flvRecordingId) {
		try {
			TypedQuery<FlvRecordingMetaData> query = em.createNamedQuery("getScreenMetaByRecording", FlvRecordingMetaData.class);
			query.setParameter("flvRecordingId", flvRecordingId);

			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();

			if (flvRecordingMetaDatas.size() > 0) {
				return flvRecordingMetaDatas.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getScreenMetaDataByRecording]: ", ex2);
		}
		return null;
	}

	public Long addFlvRecordingMetaData(Long flvRecordingId, String freeTextUserName, Date recordStart, Boolean isAudioOnly,
			Boolean isVideoOnly, Boolean isScreenData, String streamName, Integer interiewPodId) {
		try {

			FlvRecordingMetaData flvRecordingMetaData = new FlvRecordingMetaData();

			flvRecordingMetaData.setDeleted(false);

			flvRecordingMetaData.setFlvRecording(flvRecordingDao.get(flvRecordingId));
			flvRecordingMetaData.setFreeTextUserName(freeTextUserName);
			flvRecordingMetaData.setInserted(new Date());

			flvRecordingMetaData.setRecordStart(recordStart);

			flvRecordingMetaData.setAudioOnly(isAudioOnly);
			flvRecordingMetaData.setIsVideoOnly(isVideoOnly);
			flvRecordingMetaData.setScreenData(isScreenData);

			flvRecordingMetaData.setStreamName(streamName);

			flvRecordingMetaData.setInteriewPodId(interiewPodId);

			flvRecordingMetaData = em.merge(flvRecordingMetaData);
			Long flvRecordingMetaDataId = flvRecordingMetaData.getId();

			return flvRecordingMetaDataId;

		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaData]: ", ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaDataEndDate(Long metaId, Date recordEnd) {
		try {
			FlvRecordingMetaData meta = get(metaId);

			meta.setRecordEnd(recordEnd);
			
			log.debug("updateFlvRecordingMetaDataEndDate :: Start Date :" + meta.getRecordStart());
			log.debug("updateFlvRecordingMetaDataEndDate :: End Date :" + meta.getRecordEnd());
			
			update(meta);
			
			return metaId;
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ", ex2);
		}
		return null;
	}

	public Long update(FlvRecordingMetaData flvRecordingMetaData) {
		try {
			if (flvRecordingMetaData.getId() == 0) {
				em.persist(flvRecordingMetaData);
			} else {
				if (!em.contains(flvRecordingMetaData)) {
					em.merge(flvRecordingMetaData);
				}
			}
		} catch (Exception ex2) {
			log.error("[update]: ", ex2);
		}
		return null;
	}
}
