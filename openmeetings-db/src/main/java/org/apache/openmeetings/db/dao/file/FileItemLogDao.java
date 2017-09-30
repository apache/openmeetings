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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItemLog;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class FileItemLogDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FileItemLogDao.class, getWebAppRootKey());
	@PersistenceContext
	private EntityManager em;

	public long countErrors(BaseFileItem f) {
		return em.createNamedQuery("countErrorFileLogsByFile", Long.class).setParameter("fileId", f.getId())
				.getSingleResult();
	}

	public List<FileItemLog> get(BaseFileItem f) {
		return em.createNamedQuery("getFileLogsByFile", FileItemLog.class).setParameter("fileId", f.getId())
				.getResultList();
	}

	public void delete(BaseFileItem f) {
		em.createNamedQuery("deleteErrorFileLogsByFile").setParameter("fileId", f.getId())
				.executeUpdate();
	}

	public FileItemLog add(String name, BaseFileItem f, ConverterProcessResult returnMap) {
		log.trace("Adding log: {}, {}, {}", name, f, returnMap);
		FileItemLog l = new FileItemLog();
		l.setInserted(new Date());
		l.setExitCode(returnMap.getExitCode());
		l.setFileId(f.getId());
		l.setMessage(returnMap.buildLogMessage());
		l.setName(name);

		em.persist(l);
		return l;
	}
}
