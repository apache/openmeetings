package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.user.PrivateMessageFolder;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long privateMessageFolderId = (Long) session.save(privateMessageFolder);
			
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			
			PrivateMessageFolder privateMessageFolder = (PrivateMessageFolder) query.uniqueResult();
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			
			List<PrivateMessageFolder> privateMessageFolders = query.list();
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(privateMessageFolder);
			
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("userId", userId);
			
			List<PrivateMessageFolder> privateMessageFolders = query.list();
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.delete(privateMessageFolder);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception e) {
			log.error("[deletePrivateMessages]",e);
		}
	}	
	
}
