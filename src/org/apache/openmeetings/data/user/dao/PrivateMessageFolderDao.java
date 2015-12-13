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
package org.apache.openmeetings.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.user.PrivateMessageFolder;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrivateMessageFolderDao {
	
	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessageFolderDao.class, OpenmeetingsVariables.webAppRootKey);
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
	
	public PrivateMessageFolder getPrivateMessageFolderById(Long privateMessageFolderId) {
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

	public List<PrivateMessageFolder> getPrivateMessageFolders() {
		try {
			String hql = "select c from PrivateMessageFolder c ";

			TypedQuery<PrivateMessageFolder> query = em.createQuery(hql, PrivateMessageFolder.class); 
			
			List<PrivateMessageFolder> privateMessageFolders = query.getResultList();
			
			return privateMessageFolders;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderById]",e);
		}
		return null;
	}

	public void updatePrivateMessages(PrivateMessageFolder privateMessageFolder) {
		try {
			
			if (privateMessageFolder.getPrivateMessageFolderId() == 0) {
				em.persist(privateMessageFolder);
		    } else {
		    	if (!em.contains(privateMessageFolder)) {
		    		em.merge(privateMessageFolder);
			    }
			}
			
		} catch (Exception e) {
			log.error("[updatePrivateMessages]",e);
		}
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

	public void deletePrivateMessages(PrivateMessageFolder privateMessageFolder) {
		try {
			
			privateMessageFolder = em.find(PrivateMessageFolder.class, privateMessageFolder.getPrivateMessageFolderId());
			em.remove(privateMessageFolder);
			
		} catch (Exception e) {
			log.error("[deletePrivateMessages]",e);
		}
	}	
	
}
