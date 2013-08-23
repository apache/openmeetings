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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Transactional
public class FlvRecordingMetaDeltaDao {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecordingMetaDeltaDao.class,
			OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public List<FlvRecordingMetaDelta> getFlvRecordingMetaDeltaByMetaId(Long flvRecordingMetaDataId) {
		try { 
			
			String hql = "SELECT c FROM FlvRecordingMetaDelta c " +
					"WHERE c.flvRecordingMetaDataId = :flvRecordingMetaDataId";
			
			TypedQuery<FlvRecordingMetaDelta> query = em.createQuery(hql, FlvRecordingMetaDelta.class);
			query.setParameter("flvRecordingMetaDataId", flvRecordingMetaDataId);
			
			List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = query.getResultList();
			
			return flvRecordingMetaDeltas;
		} catch (Exception ex2) {
			log.error("[getFlvRecordingMetaDeltaByMetaId]: ",ex2);
		}
		return null;
	}
	
	
	
	public Long addFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			
			flvRecordingMetaDelta = em.merge(flvRecordingMetaDelta);
			Long flvRecordingMetaDeltaId = flvRecordingMetaDelta.getFlvRecordingMetaDeltaId();
			
			log.debug("flvRecordingMetaDeltaId "+flvRecordingMetaDeltaId);
			
			return flvRecordingMetaDeltaId;
		} catch (Exception ex2) {
			log.error("[addFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}
	
	public Long updateFlvRecordingMetaDelta(FlvRecordingMetaDelta flvRecordingMetaDelta) {
		try { 
			if (flvRecordingMetaDelta.getFlvRecordingMetaDataId() == 0) {
				em.persist(flvRecordingMetaDelta);
		    } else {
		    	if (!em.contains(flvRecordingMetaDelta)) {
		    		em.merge(flvRecordingMetaDelta);
			    }
			}
			
			return flvRecordingMetaDelta.getFlvRecordingMetaDataId();
		} catch (Exception ex2) {
			log.error("[updateFlvRecordingMetaDelta]: ",ex2);
		}
		return null;
	}	
	
}
