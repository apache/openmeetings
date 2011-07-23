package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.user.PrivateMessageFolder;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PrivateMessageFolderDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessageFolderDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private static PrivateMessageFolderDaoImpl instance = null;

	private PrivateMessageFolderDaoImpl() {
	}

	public static synchronized PrivateMessageFolderDaoImpl getInstance() {
		if (instance == null) {
			instance = new PrivateMessageFolderDaoImpl();
		}
		return instance;
	}
	
	public Long addPrivateMessageFolder(String folderName, Long userId) {
		try {
			PrivateMessageFolder privateMessageFolder = new PrivateMessageFolder();
			privateMessageFolder.setFolderName(folderName);
			privateMessageFolder.setUserId(userId);
			privateMessageFolder.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			privateMessageFolder = session.merge(privateMessageFolder);
			Long privateMessageFolderId = privateMessageFolder.getPrivateMessageFolderId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessageFolderId;	
		} catch (Exception e) {
			log.error("[addPrivateMessageFolder]",e);
		}
		return null;
	}
	
	public Long addPrivateMessageFolderObj(PrivateMessageFolder privateMessageFolder) {
		try {
			privateMessageFolder.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			privateMessageFolder = session.merge(privateMessageFolder);
			Long privateMessageFolderId = privateMessageFolder.getPrivateMessageFolderId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
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

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			
			PrivateMessageFolder privateMessageFolder = null;
			try {
				privateMessageFolder = (PrivateMessageFolder) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessageFolder;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderById]",e);
		}
		return null;
	}

	public List<PrivateMessageFolder> getPrivateMessageFolders() {
		try {
			String hql = "select c from PrivateMessageFolder c ";

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			
			List<PrivateMessageFolder> privateMessageFolders = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessageFolders;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderById]",e);
		}
		return null;
	}

	public void updatePrivateMessages(PrivateMessageFolder privateMessageFolder) {
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (privateMessageFolder.getPrivateMessageFolderId() == 0) {
				session.persist(privateMessageFolder);
			    } else {
			    	if (!session.contains(privateMessageFolder)) {
			    		session.merge(privateMessageFolder);
			    }
			}
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception e) {
			log.error("[updatePrivateMessages]",e);
		}
	}
	
	public List<PrivateMessageFolder> getPrivateMessageFolderByUserId(Long userId) {
		try {
			String hql = "select c from PrivateMessageFolder c " +
						"where c.userId = :userId ";

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("userId", userId);
			
			List<PrivateMessageFolder> privateMessageFolders = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessageFolders;
		} catch (Exception e) {
			log.error("[getPrivateMessageFolderByUserId]",e);
		}
		return null;
	}

	public void deletePrivateMessages(PrivateMessageFolder privateMessageFolder) {
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			privateMessageFolder = session.find(PrivateMessageFolder.class, privateMessageFolder.getPrivateMessageFolderId());
			session.remove(privateMessageFolder);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception e) {
			log.error("[deletePrivateMessages]",e);
		}
	}	
	
}
