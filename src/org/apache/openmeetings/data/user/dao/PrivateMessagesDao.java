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
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.PrivateMessage;
import org.apache.openmeetings.persistence.beans.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrivateMessagesDao {

	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessagesDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public Long addPrivateMessage(String subject, String message, Long parentMessageId, 
			User from, User to, User owner, Boolean bookedRoom, Room room,
			Boolean isContactRequest, Long userContactId, String email) {
		try {
			PrivateMessage privateMessage = new PrivateMessage();
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
			privateMessage.setEmail(email);
			
			privateMessage = em.merge(privateMessage);
			Long privateMessageId = privateMessage.getPrivateMessageFolderId();
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public Long addPrivateMessageObj(PrivateMessage privateMessage) {
		try {
			
			privateMessage = em.merge(privateMessage);
			Long privateMessageId = privateMessage.getPrivateMessageFolderId();
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> getPrivateMessages() {
		try {
			TypedQuery<PrivateMessage> query = em.createNamedQuery("getPrivateMessages", PrivateMessage.class); 
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getPrivateMessages]",e);
		}
		return null;
	}
	
	public PrivateMessage getPrivateMessagesById(Long privateMessageId) {
		try {
			TypedQuery<PrivateMessage> query = em.createNamedQuery("getPrivateMessagesById", PrivateMessage.class); 
			query.setParameter("privateMessageId", privateMessageId);
			PrivateMessage privateMessage = null;
			try {
				privateMessage = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			return privateMessage;
		} catch (Exception e) {
			log.error("[countPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public void updatePrivateMessages(PrivateMessage privateMessage) {
		try {
			
			if (privateMessage.getPrivateMessageFolderId() == null) {
				em.persist(privateMessage);
		    } else {
		    	if (!em.contains(privateMessage)) {
		    		em.merge(privateMessage);
			    }
			}
			
		} catch (Exception e) {
			log.error("[updatePrivateMessages]",e);
		}
	}
	
	public Long countPrivateMessagesByUser(Long toUserId, String search, Long privateMessageFolderId) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessage c " +
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

			TypedQuery<Long> query = em.createQuery(hql, Long.class); 
			query.setParameter("toUserId", toUserId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			List<Long> ll = query.getResultList();
			
			return ll.get(0);
			
		} catch (Exception e) {
			log.error("[countPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public Long getNumberMessages(Long toUserId, Long privateMessageFolderId, boolean isRead) {
		try {
			TypedQuery<Long> query = em.createNamedQuery("getNumberMessages", Long.class); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("isRead", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			return query.getSingleResult();
		} catch (Exception e) {
			log.error("[getNumberMessages]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> getPrivateMessagesByUser(Long toUserId, String search,
			String orderBy, int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessage c " +
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

			TypedQuery<PrivateMessage> query = em.createQuery(hql, PrivateMessage.class); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessage> ll = query.getResultList();
			
			return ll;	
		} catch (Exception e) {
			log.error("[getPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public Long countSendPrivateMessagesByUser(Long toUserId, String search, 
			Long privateMessageFolderId) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessage c " +
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

			TypedQuery<Long> query = em.createQuery(hql, Long.class); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			List<Long> ll = query.getResultList();
			
			return ll.get(0);
			
		} catch (Exception e) {
			log.error("[countSendPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> getTrashPrivateMessagesByUser(Long user_id, String search, 
			String orderBy, int start, Boolean asc, int max) {
		try {
			
			String hql = "select c from PrivateMessage c " +
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

			TypedQuery<PrivateMessage> query = em.createQuery(hql, PrivateMessage.class); 
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("user_id", user_id);
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessage> ll = query.getResultList();
			
			return ll;	
		} catch (Exception e) {
			log.error("[getTrashPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public Long countTrashPrivateMessagesByUser(Long user_id, String search) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessage c " +
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
			
			TypedQuery<Long> query = em.createQuery(hql, Long.class); 
			query.setParameter("user_id", user_id);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			List<Long> ll = query.getResultList();
			
			return ll.get(0);
			
		} catch (Exception e) {
			log.error("[countTrashPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> getSendPrivateMessagesByUser(Long toUserId, String search, 
			String orderBy, int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessage c " +
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

			TypedQuery<PrivateMessage> query = em.createQuery(hql, PrivateMessage.class); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessage> ll = query.getResultList();
			
			return ll;	
		} catch (Exception e) {
			log.error("[getSendPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	
	public Long countFolderPrivateMessagesByUser(Long toUserId, Long privateMessageFolderId, String search) {
		try {
			
			String hql = "select count(c.privateMessageId) from PrivateMessage c " +
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
			
			TypedQuery<Long> query = em.createQuery(hql, Long.class); 
			query.setParameter("toUserId", toUserId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			List<Long> ll = query.getResultList();
			
			return ll.get(0);
			
		} catch (Exception e) {
			log.error("[countFolderPrivateMessagesByUser]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> getFolderPrivateMessagesByUser(Long toUserId, String search, String orderBy, 
			int start, Boolean asc, Long privateMessageFolderId, int max) {
		try {
			
			String hql = "select c from PrivateMessage c " +
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
			
			TypedQuery<PrivateMessage> query = em.createQuery(hql, PrivateMessage.class); 
			query.setParameter("toUserId", toUserId);
			query.setParameter("isTrash", false);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			if (search.length() != 0) {
				query.setParameter("search", StringUtils.lowerCase("%"+search+"%"));
			}
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<PrivateMessage> ll = query.getResultList();
			
			return ll;
			
		} catch (Exception e) {
			log.error("[getFolderPrivateMessagesByUser]",e);
		}
		return null;
	}

	public int updatePrivateMessagesToTrash(List<Long> privateMessageIds, Boolean isTrash, Long privateMessageFolderId) {
		try {
			Query query = em.createNamedQuery("updatePrivateMessagesToTrash"); 
			query.setParameter("isTrash", isTrash);
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			//FIXME weird code
			for (Long privateMessageId : privateMessageIds) {
				TypedQuery<PrivateMessage> querySel = em.createNamedQuery("getPrivateMessagesById", PrivateMessage.class); 
				querySel.setParameter("privateMessageId", privateMessageId);
				try {
					querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
			}
			
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesToTrash]",e);
		}
		return -1;
	}
	
	public int updatePrivateMessagesReadStatus(List<Long> privateMessageIds, Boolean isRead) {
		try {
			Query query = em.createNamedQuery("updatePrivateMessagesReadStatus"); 
			query.setParameter("isRead", isRead);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			//FIXME weird code
			for (Long privateMessageId : privateMessageIds) {
				TypedQuery<PrivateMessage> querySel = em.createNamedQuery("getPrivateMessagesById", PrivateMessage.class); 
				querySel.setParameter("privateMessageId", privateMessageId);
				try {
					querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
			}
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}

	public Integer moveMailsToFolder(List<Long> privateMessageIds, Long privateMessageFolderId) {
		try {
			Query query = em.createNamedQuery("moveMailsToFolder"); 
			query.setParameter("privateMessageFolderId", privateMessageFolderId);
			query.setParameter("privateMessageIds", privateMessageIds);
			int updatedEntities = query.executeUpdate();
			
			//Refresh the Entities in the Cache as Hibernate will not do it!
			//FIXME weird code
			for (Long privateMessageId : privateMessageIds) {
				TypedQuery<PrivateMessage> querySel = em.createNamedQuery("getPrivateMessagesById", PrivateMessage.class); 
				querySel.setParameter("privateMessageId", privateMessageId);
				try {
					querySel.getSingleResult();
			    } catch (NoResultException ex) {
			    }
			}
			return updatedEntities;
		} catch (Exception e) {
			log.error("[updatePrivateMessagesReadStatus]",e);
		}
		return -1;
	}
	
	public int deletePrivateMessages(List<Long> privateMessageIds) {
		try {
			Query query = em.createNamedQuery("deletePrivateMessages"); 
			query.setParameter("privateMessageIds", privateMessageIds);
			return query.executeUpdate();
		} catch (Exception e) {
			log.error("[deletePrivateMessages]",e);
		}
		return -1;
	}
	
	public List<PrivateMessage> getPrivateMessagesByRoom(Long roomId) {
		try {
			TypedQuery<PrivateMessage> query = em.createNamedQuery("getPrivateMessagesByRoom", PrivateMessage.class); 
			query.setParameter("roomId", roomId);
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getPrivateMessagesByRoom]",e);
		}
		return null;
	}

}
