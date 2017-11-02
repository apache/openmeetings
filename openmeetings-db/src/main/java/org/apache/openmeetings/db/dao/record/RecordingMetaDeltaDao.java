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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.record.RecordingMetaDelta;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sebastianwagner
 *
 */
@Repository
@Transactional
public class RecordingMetaDeltaDao {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingMetaDeltaDao.class, getWebAppRootKey());
	@PersistenceContext
	private EntityManager em;

	public List<RecordingMetaDelta> getByMetaId(Long metaDataId) {
		return em.createNamedQuery("getRecordingMetaDeltaByMetaDataId", RecordingMetaDelta.class).setParameter("metaDataId", metaDataId).getResultList();
	}

	public Long add(RecordingMetaDelta metaDelta) {
		try {

			metaDelta = em.merge(metaDelta);
			Long metaDeltaId = metaDelta.getId();

			log.debug("metaDeltaId "+metaDeltaId);

			return metaDeltaId;
		} catch (Exception ex2) {
			log.error("[add]: ",ex2);
		}
		return null;
	}

	public RecordingMetaDelta update(RecordingMetaDelta metaDelta) {
		log.debug("[update]: ");
		if (metaDelta.getMetaDataId() == null) {
			em.persist(metaDelta);
		} else {
			metaDelta = em.merge(metaDelta);
		}

		return metaDelta;
	}
}
