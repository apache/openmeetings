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
package org.apache.openmeetings.db.dao.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrivateMessageFolderDao implements IDataProviderDao<PrivateMessageFolder> {
	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessageFolderDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public Long addPrivateMessageFolder(String folderName, Long userId) {
		try {
			PrivateMessageFolder privateMessageFolder = new PrivateMessageFolder();
			privateMessageFolder.setFolderName(folderName);
			privateMessageFolder.setUserId(userId);
			privateMessageFolder.setInserted(new Date());
			
			privateMessageFolder = em.merge(privateMessageFolder);
			Long privateMessageFolderId = privateMessageFolder.getPrivateMessageFolderId();
			
			return privateMessageFolderId;	
		} catch (Exception e) {
			log.error("[addPrivateMessageFolder]",e);
		}
		return null;
	}
	
	public Long addPrivateMessageFolderObj(PrivateMessageFolder privateMessageFolder) {
		try {
			privateMessageFolder.setInserted(new Date());
			
			privateMessageFolder = em.merge(privateMessageFolder);
			Long privateMessageFolderId = privateMessageFolder.getPrivateMessageFolderId();
			
			return privateMessageFolderId;	
		} catch (Exception e) {
			log.error("[addPrivateMessageFolder]",e);
		}
		return null;
	}
	
	public PrivateMessageFolder get(long privateMessageFolderId) {
		try {
			String hql = "select c from PrivateMessageFolder c " +
						"where c.privateMessageFolderId = :privateMessageFolderId ";

			TypedQuery<PrivateMessageFolder> query = em.createQuery(hql, PrivateMessageFolder.class); 
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			
			PrivateMessageFolder privateMessageFolder = null;
			try {
				privateMessageFolder = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return privateMessageFolder;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderById]",e);
		}
		return null;
	}

	public List<PrivateMessageFolder> get(int start, int count) {
		return em.createQuery("SELECT c FROM PrivateMessageFolder c ORDER BY c.privateMessageFolderId", PrivateMessageFolder.class)
				.setFirstResult(start).setMaxResults(count)
				.getResultList();
	}

	public PrivateMessageFolder update(PrivateMessageFolder privateMessageFolder, Long userId) {
		if (privateMessageFolder.getPrivateMessageFolderId() == 0) {
			em.persist(privateMessageFolder);
	    } else {
	    	if (!em.contains(privateMessageFolder)) {
	    		privateMessageFolder = em.merge(privateMessageFolder);
		    }
		}
		return privateMessageFolder;
	}
	
	public List<PrivateMessageFolder> getPrivateMessageFolderByUserId(Long userId) {
		try {
			String hql = "select c from PrivateMessageFolder c " +
						"where c.userId = :userId ";

			TypedQuery<PrivateMessageFolder> query = em.createQuery(hql, PrivateMessageFolder.class); 
			query.setParameter("userId", userId);
			
			List<PrivateMessageFolder> privateMessageFolders = query.getResultList();
			
			return privateMessageFolders;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderByUserId]",e);
		}
		return null;
	}

	public void delete(PrivateMessageFolder privateMessageFolder, Long userId) {
		privateMessageFolder = em.find(PrivateMessageFolder.class, privateMessageFolder.getPrivateMessageFolderId());
		em.remove(privateMessageFolder);
	}

	public List<PrivateMessageFolder> get(String search, int start, int count, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long count(String search) {
		// TODO Auto-generated method stub
		return 0;
	}

}
