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

import static org.apache.openmeetings.db.util.DaoHelper.only;

import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.record.RecordingChunk;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Status;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RecordingChunkDao {
	private static final Logger log = LoggerFactory.getLogger(RecordingChunkDao.class);
	private static final String PARAM_RECID = "recordingId";
	@PersistenceContext
	private EntityManager em;

	@Inject
	private RecordingDao recordingDao;

	public RecordingChunk get(Long id) {
		return only(em.createNamedQuery("getChunkById", RecordingChunk.class)
				.setParameter("id", id).getResultList());
	}

	public List<RecordingChunk> getByRecording(Long recordingId) {
		return em.createNamedQuery("getChunkByRecording", RecordingChunk.class)
				.setParameter(PARAM_RECID, recordingId)
				.getResultList();
	}

	public List<RecordingChunk> getNotScreenChunksByRecording(Long recordingId) {
		return em.createNamedQuery("getNotScreenChunkByRecording", RecordingChunk.class)
				.setParameter(PARAM_RECID, recordingId)
				.setParameter("screen", Type.SCREEN)
				.setParameter("none", Status.NONE)
				.getResultList();
	}

	public RecordingChunk getScreenByRecording(Long recordingId) {
		List<RecordingChunk> list = em.createNamedQuery("getScreenChunkByRecording", RecordingChunk.class)
				.setParameter("screen", Type.SCREEN)
				.setParameter(PARAM_RECID, recordingId)
				.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public Long start(Long recordingId, Type type, String streamName, String sid) {
		RecordingChunk chunk = new RecordingChunk();
		chunk.setRecording(recordingDao.get(recordingId));
		chunk.setStart(new Date());
		chunk.setType(type);
		chunk.setStreamName(streamName);
		chunk.setStreamStatus(Status.STARTED);
		chunk.setSid(sid);
		chunk = update(chunk);
		return chunk.getId();
	}

	public void stop(Long chunkId) {
		RecordingChunk chunk = get(chunkId);
		if (chunk != null) {
			chunk.setEnd(new Date());
			chunk.setStreamStatus(Status.STOPPED);
			update(chunk);
		}
	}

	public RecordingChunk update(RecordingChunk chunk) {
		log.debug("[update]: ");
		if (chunk.getId() == null) {
			em.persist(chunk);
		} else {
			chunk = em.merge(chunk);
		}
		return chunk;
	}
}
