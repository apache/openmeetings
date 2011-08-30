package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.user.PrivateMessageFolder;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrivateMessageFolderDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessageFolderDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
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

			Query query = em.createQuery(hql); 
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			
			PrivateMessageFolder privateMessageFolder = null;
			try {
				privateMessageFolder = (PrivateMessageFolder) query.getSingleResult();
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

			Query query = em.createQuery(hql); 
			
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

			Query query = em.createQuery(hql); 
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
