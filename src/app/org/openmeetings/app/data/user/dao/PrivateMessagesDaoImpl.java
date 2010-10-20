package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.PrivateMessages;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PrivateMessagesDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessagesDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private static PrivateMessagesDaoImpl instance = null;

	private PrivateMessagesDaoImpl() {
	}

	public static synchronized PrivateMessagesDaoImpl getInstance() {
		if (instance == null) {
			instance = new PrivateMessagesDaoImpl();
		}
		return instance;
	}
	
	public Long addPrivateMessage(String subject, String message, Long parentMessageId, 
			Users from, Users to, Users owner, Boolean bookedRoom, Rooms room,
			Boolean isContactRequest, Long userContactId) {
		try {
			PrivateMessages privateMessage = new PrivateMessages();
			privateMessage.setInserted(new Date());
			privateMessage.setSubject(subject);
			privateMessage.setMessage(message);
			privateMessage.setFrom(from);
			privateMessage.setTo(to);
			privateMessage.setOwner(owner);
			privateMessage.setBookedRoom(bookedRoom);
			privateMessage.setRoom(room);
			privateMessage.setParentMessage(parentMessageId);
			privateMessage.setIsTrash(false);
			privateMessage.setPrivateMessageFolderId(0L);
			privateMessage.setIsRead(false);
			privateMessage.setIsContactRequest(isContactRequest);
			privateMessage.setUserContactId(userContactId);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long privateMessageId = (Long) session.save(privateMessage);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public PrivateMessages getPrivateMessagesById(Long privateMessageId) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.privateMessageId = :privateMessageId ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("privateMessageId", privateMessageId);
			
			PrivateMessages privateMessage = (PrivateMessages) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return privateMessage;
			
		} catch (Exception e) {
			log.error("[countPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public void updatePrivateMessages(PrivateMessages privateMessage) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(privateMessage);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception e) {
			log.error("[updatePrivateMessages]",e);
		}
	}
	
	public Long countPrivateMessagesByUser(Long toUserId, String search, Long privateMessageFolderId) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessages c " +
						"where c.to.user_id = :toUserId " +
						"AND c.isTrash = false " +
						"AND c.owner.user_id = :toUserId " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[countPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getPrivateMessagesByUser(Long toUserId, String search,
			String orderBy, int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.to.user_id = :toUserId " +
						"AND c.isTrash = :isTrash " +
						"AND c.owner.user_id = :toUserId " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			query.setBoolean("isTrash", false);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public Long countSendPrivateMessagesByUser(Long toUserId, String search, 
			Long privateMessageFolderId) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessages c " +
						"where c.from.user_id = :toUserId " +
						"AND c.isTrash = :isTrash " +
						"AND c.owner.user_id = :toUserId " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			query.setBoolean("isTrash", false);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[countSendPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getTrashPrivateMessagesByUser(Long user_id, String search, 
			String orderBy, int start, Boolean asc, int max) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.isTrash = true " +
						"AND c.owner.user_id = :user_id ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			query.setLong("user_id", user_id);
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getTrashPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public Long countTrashPrivateMessagesByUser(Long user_id, String search) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessages c " +
						"where c.isTrash = true  " +
						"AND c.owner.user_id = :user_id ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("user_id", user_id);
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[countTrashPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getSendPrivateMessagesByUser(Long toUserId, String search, 
			String orderBy, int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.from.user_id = :toUserId " +
						"AND c.isTrash = false " +
						"AND c.owner.user_id = :toUserId " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			
			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE lower(:search) ";
				hql += "OR lower(c.message) LIKE lower(:search) ";
				hql += "OR lower(c.from.firstname) LIKE lower(:search) ";
				hql += "OR lower(c.from.lastname) LIKE lower(:search) ";
				hql += "OR lower(c.from.login) LIKE lower(:search) ";
				hql += "OR lower(c.from.adresses.email) LIKE lower(:search) ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setString("search", "%"+search+"%");
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getSendPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	
	public Long countFolderPrivateMessagesByUser(Long toUserId, Long privateMessageFolderId) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessages c " +
						"where c.isTrash = false " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[countFolderPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getFolderPrivateMessagesByUser(Long toUserId, String orderBy, 
			int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.isTrash = false " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setLong("toUserId", toUserId);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getFolderPrivateMessagesByUser]",e);
		}
		return null;
	}

	public int updatePrivateMessagesToTrash(List privateMessageIds, Boolean isTrash, Long privateMessageFolderId) {
		try {
			
			String hql = "UPDATE PrivateMessages c " +
						"SET c.isTrash = :isTrash,c.privateMessageFolderId = :privateMessageFolderId " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setBoolean("isTrash", isTrash);
			query.setLong("privateMessageFolderId", privateMessageFolderId);
			query.setParameterList("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesToTrash]",e);
		}
		return -1;
	}
	
	public int updatePrivateMessagesReadStatus(List privateMessageIds, Boolean isRead) {
		try {
			
			String hql = "UPDATE PrivateMessages c " +
						"SET c.isRead = :isRead " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setBoolean("isRead", isRead);
			query.setParameterList("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}
	
	
	
}
