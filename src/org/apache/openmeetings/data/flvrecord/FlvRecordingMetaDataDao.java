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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FlvRecordingMetaDataDao {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDataDao.class,
			OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private FlvRecordingDao flvRecordingDao;
	
	public FlvRecordingMetaData getFlvRecordingMetaDataById(Long flvRecordingMetaDataId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecordingMetaDataId = :flvRecordingMetaDataId";
			
			TypedQuery<FlvRecordingMetaData> query = em.createQuery(hql, FlvRecordingMetaData.class);
			query.setParameter("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			FlvRecordingMetaData flvRecordingMetaData = null;
			try {
				flvRecordingMetaData = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return flvRecordingMetaData;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataById]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaDataByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND c.deleted <> :deleted ";
			
			TypedQuery<FlvRecordingMetaData> query = em.createQuery(hql, FlvRecordingMetaData.class);
			query.setParameter("flvRecordingId", flvRecordingId);
			query.setParameter("deleted", true);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			
			return flvRecordingMetaDatas;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataByRecording]: ",ex2);
		}
		return null;
	}
	
	public List<FlvRecordingMetaData> getFlvRecordingMetaDataAudioFlvsByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND (" +
						"(c.isScreenData = false) " +
							" AND " +
						"(c.isAudioOnly = true OR (c.isAudioOnly = false AND c.isVideoOnly = false))" +
					")";
			
			TypedQuery<FlvRecordingMetaData> query = em.createQuery(hql, FlvRecordingMetaData.class);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			
			return flvRecordingMetaDatas;
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataAudioFlvsByRecording]: ",ex2);
		}
		return null;
	}
	
	public FlvRecordingMetaData getFlvRecordingMetaDataScreenFlvByRecording(Long flvRecordingId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaData c " +
					"WHERE c.flvRecording.flvRecordingId = :flvRecordingId " +
					"AND c.isScreenData = true";
			
			TypedQuery<FlvRecordingMetaData> query = em.createQuery(hql, FlvRecordingMetaData.class);
			query.setParameter("flvRecordingId", flvRecordingId);
			
			List<FlvRecordingMetaData> flvRecordingMetaDatas = query.getResultList();
			
			if (flvRecordingMetaDatas.size() > 0) {
				return flvRecordingMetaDatas.get(0);
			}
			
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDataScreenFlvByRecording]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingMetaData(Long flvRecordingId, String freeTextUserName, 
					Date recordStart, Boolean isAudioOnly, Boolean isVideoOnly, 
					Boolean isScreenData, String streamName, Integer interiewPodId) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = new FlvRecordingMetaData();
			
			flvRecordingMetaData.setDeleted(false);
			
			flvRecordingMetaData.setFlvRecording(flvRecordingDao.getFlvRecordingById(flvRecordingId));
			flvRecordingMetaData.setFreeTextUserName(freeTextUserName);
			flvRecordingMetaData.setInserted(new Date());
			
			flvRecordingMetaData.setRecordStart(recordStart);
			
			flvRecordingMetaData.setIsAudioOnly(isAudioOnly);
			flvRecordingMetaData.setIsVideoOnly(isVideoOnly);
			flvRecordingMetaData.setIsScreenData(isScreenData);
			
			flvRecordingMetaData.setStreamName(streamName);
			
			flvRecordingMetaData.setInteriewPodId(interiewPodId);
			
			flvRecordingMetaData = em.merge(flvRecordingMetaData);
			Long flvRecordingMetaDataId = flvRecordingMetaData.getFlvRecordingMetaDataId();
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaData]: ",ex2);
		}
		return null;
	}
	
	public Long addFlvRecordingMetaDataObj(FlvRecordingMetaData flvRecordingMetaData) {
		try {

			flvRecordingMetaData = em.merge(flvRecordingMetaData);
			Long flvRecordingMetaDataId = flvRecordingMetaData.getFlvRecordingMetaDataId();

			return flvRecordingMetaDataId;

		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDataObj]: ", ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaDataEndDate(Long flvRecordingMetaDataId, 
										Date recordEnd) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = this.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			
			flvRecordingMetaData.setRecordEnd(recordEnd);
			
			log.debug("updateFlvRecordingMetaDataEndDate :: Start Date :"+flvRecordingMetaData.getRecordStart());
			log.debug("updateFlvRecordingMetaDataEndDate :: End Date :"+flvRecordingMetaData.getRecordEnd());
			
			this.updateFlvRecordingMetaData(flvRecordingMetaData);
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaDataInitialGap(Long flvRecordingMetaDataId, 
										long initalGap) {
		try { 
			
			FlvRecordingMetaData flvRecordingMetaData = this.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			
			flvRecordingMetaData.setInitialGapSeconds(Long.valueOf(initalGap).intValue());
			
			this.updateFlvRecordingMetaData(flvRecordingMetaData);
			
			return flvRecordingMetaDataId;
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDataEndDate]: ",ex2);
		}
		return null;
	}

	public Long updateFlvRecordingMetaData(FlvRecordingMetaData flvRecordingMetaData) {
		try { 
			
			if (flvRecordingMetaData.getFlvRecordingMetaDataId() == 0) {
				em.persist(flvRecordingMetaData);
		    } else {
		    	if (!em.contains(flvRecordingMetaData)) {
		    		em.merge(flvRecordingMetaData);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaData]: ",ex2);
		}
		return null;
	}
	
}
