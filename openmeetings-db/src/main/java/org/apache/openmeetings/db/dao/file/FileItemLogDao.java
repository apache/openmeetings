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
package org.apache.openmeetings.db.dao.file;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItemLog;
import org.apache.openmeetings.util.process.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class FileItemLogDao {
	private static final Logger log = LoggerFactory.getLogger(FileItemLogDao.class);
	private static final String PARAM_FILEID = "fileId";
	@PersistenceContext
	private EntityManager em;

	public long countErrors(BaseFileItem f) {
		return em.createNamedQuery("countErrorFileLogsByFile", Long.class).setParameter(PARAM_FILEID, f.getId())
				.getSingleResult();
	}

	public List<FileItemLog> get(BaseFileItem f) {
		return em.createNamedQuery("getFileLogsByFile", FileItemLog.class).setParameter(PARAM_FILEID, f.getId())
				.getResultList();
	}

	public void delete(BaseFileItem f) {
		em.createNamedQuery("deleteErrorFileLogsByFile").setParameter(PARAM_FILEID, f.getId())
				.executeUpdate();
	}

	public FileItemLog add(String name, BaseFileItem f, ProcessResult r) {
		log.trace("Adding log: {}, {}, {}", name, f, r);
		FileItemLog l = new FileItemLog()
				.setInserted(new Date())
				.setExitCode(r.getExitCode())
				.setFileId(f.getId())
				.setMessage(r.buildLogMessage())
				.setName(name)
				.setOptional(r.isOptional());

		em.persist(l);
		return l;
	}
}
