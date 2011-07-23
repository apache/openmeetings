package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.PrivateMessages;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
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
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			privateMessage = session.merge(privateMessage);
			Long privateMessageId = privateMessage.getPrivateMessageFolderId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public Long addPrivateMessageObj(PrivateMessages privateMessage) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			privateMessage = session.merge(privateMessage);
			Long privateMessageId = privateMessage.getPrivateMessageFolderId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getPrivateMessages() {
		try {
			
			String hql = "select c from PrivateMessages c ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			
			List<PrivateMessages> privateMessages = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return privateMessages;
			
		} catch (Exception e) {
			log.error("[getPrivateMessages]",e);
		}
		return null;
	}
	
	public PrivateMessages getPrivateMessagesById(Long privateMessageId) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.privateMessageId = :privateMessageId ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("privateMessageId", privateMessageId);
			
			PrivateMessages privateMessage = null;
			try {
				privateMessage = (PrivateMessages) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return privateMessage;
			
		} catch (Exception e) {
			log.error("[countPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public void updatePrivateMessages(PrivateMessages privateMessage) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (privateMessage.getPrivateMessageFolderId() == null) {
				session.persist(privateMessage);
			    } else {
			    	if (!session.contains(privateMessage)) {
			    		session.merge(privateMessage);
			    }
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			List ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			List ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("user_id", user_id);
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("user_id", user_id);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			List ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
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
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getSendPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	
	public Long countFolderPrivateMessagesByUser(Long toUserId, Long privateMessageFolderId, String search) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessages c " +
						"where c.isTrash = false " +
						"AND c.owner.user_id = :toUserId " +
						"AND c.privateMessageFolderId = :privateMessageFolderId ";

			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			List ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[countFolderPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessages> getFolderPrivateMessagesByUser(Long toUserId, String search, String orderBy, 
			int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessages c " +
							"where c.isTrash = :isTrash " +
							"AND c.owner.user_id = :toUserId " +
							"AND c.privateMessageFolderId = :privateMessageFolderId ";

			if (search.length() != 0) {
				hql += "AND ( ";
				hql += "lower(c.subject) LIKE :search ";
				hql += "OR lower(c.message) LIKE :search ";
				hql += "OR lower(c.from.firstname) LIKE :search ";
				hql += "OR lower(c.from.lastname) LIKE :search ";
				hql += "OR lower(c.from.login) LIKE :search ";
				hql += "OR lower(c.from.adresses.email) LIKE :search ";
				hql += " ) ";
			}
			
			hql += "ORDER BY "+orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}
			
			log.debug("HQL "+hql);
			
			log.debug("privateMessageFolderId "+privateMessageFolderId);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessages> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;
			
		} catch (Exception e) {
			log.error("[getFolderPrivateMessagesByUser]",e);
		}
		return null;
	}

	public int updatePrivateMessagesToTrash(List<Long> privateMessageIds, Boolean isTrash, Long privateMessageFolderId) {
		try {
			
			String hql = "UPDATE PrivateMessages c " +
						"SET c.isTrash = :isTrash,c.privateMessageFolderId = :privateMessageFolderId " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("isTrash", isTrash);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			for (Long privateMessageId : privateMessageIds) {
				String hqlSel = "select c from PrivateMessages c " +
								"where c.privateMessageId = :privateMessageId ";
	
				Query querySel = session.createQuery(hqlSel); 
				querySel.setParameter("privateMessageId", privateMessageId);
				
				PrivateMessages privateMessages = null;
				try {
					privateMessages = (PrivateMessages) querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
				
				if (privateMessages != null) {
					session.refresh(privateMessages);
				}
			}
			
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			
			
			
			
			
			return updatedEntities;
			
			
			
		} catch (Exception e) {
			log.error("[updatePrivateMessagesToTrash]",e);
		}
		return -1;
	}
	
	public int updatePrivateMessagesReadStatus(List<Long> privateMessageIds, Boolean isRead) {
		try {
			
			String hql = "UPDATE PrivateMessages c " +
						"SET c.isRead = :isRead " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("isRead", isRead);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			for (Long privateMessageId : privateMessageIds) {
				String hqlSel = "select c from PrivateMessages c " +
								"where c.privateMessageId = :privateMessageId ";
	
				Query querySel = session.createQuery(hqlSel); 
				querySel.setParameter("privateMessageId", privateMessageId);
				
				PrivateMessages privateMessages = null;
				try {
					privateMessages = (PrivateMessages) querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
				
				if (privateMessages != null) {
					session.refresh(privateMessages);
				}
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}

	public Integer moveMailsToFolder(List<Long> privateMessageIds, Long privateMessageFolderId) {
		try {
			
			String hql = "UPDATE PrivateMessages c " +
						"SET c.privateMessageFolderId = :privateMessageFolderId, c.isTrash = false " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			for (Long privateMessageId : privateMessageIds) {
				String hqlSel = "select c from PrivateMessages c " +
								"where c.privateMessageId = :privateMessageId ";
	
				Query querySel = session.createQuery(hqlSel); 
				querySel.setParameter("privateMessageId", privateMessageId);
				
				PrivateMessages privateMessages = null;
				try {
					privateMessages = (PrivateMessages) querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
				
				if (privateMessages != null) {
					session.refresh(privateMessages);
				}
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}
	
	public int deletePrivateMessages(List<Long> privateMessageIds) {
		try {
			
			String hql = "DELETE PrivateMessages c " +
						"where c.privateMessageId IN (:privateMessageIds) ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
//			//Refresh the Entities in the Cache as Hibernate will not do it!
//			for (Long privateMessageId : privateMessageIds) {
//				String hqlSel = "select c from PrivateMessages c " +
//								"where c.privateMessageId = :privateMessageId ";
//	
//				Query querySel = session.createQuery(hqlSel); 
//				querySel.setParameter("privateMessageId", privateMessageId);
//				
//				PrivateMessages privateMessages = (PrivateMessages) querySel.uniqueResult();
//				
//				if (privateMessages != null) {
//					session.refresh(privateMessages);
//				}
//			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}
	
	public List<PrivateMessages> getPrivateMessagesByRoom(Long roomId) {
		try {
			
			String hql = "select c from PrivateMessages c " +
						"where c.room.rooms_id = :roomId ";
			
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("roomId", roomId);
			List<PrivateMessages> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;	
		} catch (Exception e) {
			log.error("[getPrivateMessagesByRoom]",e);
		}
		return null;
	}

}
