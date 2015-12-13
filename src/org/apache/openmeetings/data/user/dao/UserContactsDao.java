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

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.user.UserContact;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserContactsDao {
	
	private static final Logger log = Red5LoggerFactory.getLogger(UserContactsDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
    @Autowired
    private UserManager userManager;

	public Long addUserContact(Long user_id, Long ownerId, Boolean pending, String hash) {
		try {
			
			UserContact userContact = new UserContact();
			userContact.setInserted(new Date());
			userContact.setOwner(userManager.getUserById(ownerId));
			userContact.setContact(userManager.getUserById(user_id));
			userContact.setPending(pending);
			userContact.setHash(hash);
			
			userContact = em.merge(userContact);
			Long userContactId = userContact.getUserContactId();
			
			return userContactId;			
		} catch (Exception e) {
			log.error("[addUserContact]",e);
		}
		return null;
	}
	
	public Long addUserContactObj(UserContact userContact) {
		try {
			
			userContact.setInserted(new Date());
			
			userContact = em.merge(userContact);
			Long userContactId = userContact.getUserContactId();
			
			return userContactId;			
		} catch (Exception e) {
			log.error("[addUserContact]",e);
		}
		return null;
	}
	
	/**
	 * @param userContactDeleteId
	 * @return rowcount of update
	 */
	public Integer deleteUserContact(Long userContactDeleteId) {
		try {
			Query query = em.createNamedQuery("deleteUserContact");
			query.setParameter("userContactDeleteId", userContactDeleteId);
	        return query.executeUpdate();
		} catch (Exception e) {
			log.error("[deleteUserContact]",e);
		}
		return null;
	}
	
	/**
	 * @param ownerId
	 * @return rowcount of update
	 */
	public Integer deleteAllUserContacts(Long ownerId) {
		try {
			Query query = em.createNamedQuery("deleteAllUserContacts");
	        query.setParameter("ownerId",ownerId);
	        return query.executeUpdate();
		} catch (Exception e) {
			log.error("[deleteAllUserContacts]",e);
		}
		return null;
	}
	
	public Long checkUserContacts(Long user_id, Long ownerId) {
		try {
			TypedQuery<Long> query = em.createNamedQuery("checkUserContacts", Long.class); 
			query.setParameter("user_id", user_id);
			query.setParameter("ownerId", ownerId);
			List<Long> ll = query.getResultList();
			
			log.info("checkUserContacts" + ll.get(0));
			
			return ll.get(0);
			
		} catch (Exception e) {
			log.error("[checkUserContacts]",e);
		}
		return null;
	}
	
	public UserContact getContactsByHash(String hash) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getContactsByHash", UserContact.class); 
			query.setParameter("hash", hash);
			List<UserContact> ll = query.getResultList();
			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception e) {
			log.error("[getContactsByHash]",e);
		}
		return null;
	}
	
	public List<UserContact> getContactsByUserAndStatus(Long ownerId, Boolean pending) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getContactsByUserAndStatus", UserContact.class);
			query.setParameter("ownerId", ownerId);
			query.setParameter("pending", pending);
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getContactsByUserAndStatus]",e);
		}
		return null;
	}
	
	public UserContact getUserContactByShareCalendar(Long contactId,
			Boolean shareCalendar, Long userId) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getUserContactByShareCalendar",
					UserContact.class);
			query.setParameter("contactId", contactId);
			query.setParameter("userId", userId);
			query.setParameter("shareCalendar", shareCalendar);
			List<UserContact> ll = query.getResultList();
			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception e) {
			log.error("[getUserContactByShareCalendar]", e);
		}
		return null;
	}

	public List<UserContact> getContactsByShareCalendar(Long contactId, Boolean shareCalendar) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getContactsByShareCalendar", UserContact.class); 
			query.setParameter("contactId", contactId);
			query.setParameter("shareCalendar", shareCalendar);
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getContactsByShareCalendar]",e);
		}
		return null;
	}
	
	public List<UserContact> getContactRequestsByUserAndStatus(Long user_id, Boolean pending) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getContactRequestsByUserAndStatus", UserContact.class); 
			query.setParameter("user_id", user_id);
			query.setParameter("pending", pending);
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getContactRequestsByUserAndStatus]",e);
		}
		return null;
	}
	
	public UserContact getUserContacts(Long userContactId) {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getUserContactsById", UserContact.class); 
			query.setParameter("userContactId", userContactId);
			UserContact userContacts = null;
			try {
				userContacts = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			return userContacts;
		} catch (Exception e) {
			log.error("[getUserContacts]",e);
		}
		return null;
	}
	
	public List<UserContact> getUserContacts() {
		try {
			TypedQuery<UserContact> query = em.createNamedQuery("getUserContacts", UserContact.class); 
			return query.getResultList();
		} catch (Exception e) {
			log.error("[getUserContacts]",e);
		}
		return null;
	}
	
	public Long updateContactStatus(Long userContactId, Boolean pending) {
		try {
			
			UserContact userContacts = this.getUserContacts(userContactId);
			
			if (userContacts == null) {
				return null;
			}
			userContacts.setPending(pending);
			userContacts.setUpdated(new Date());
			
			if (userContacts.getUserContactId() == 0) {
				em.persist(userContacts);
		    } else {
		    	if (!em.contains(userContacts)) {
		    		em.merge(userContacts);
			    }
			}
			
			return userContactId;
			
		} catch (Exception e) {
			log.error("[updateContactStatus]",e);
		}
		return null;
	}
	
	public void updateContact(UserContact userContacts) {
		try {
			userContacts.setUpdated(new Date());
			
			if (userContacts.getUserContactId() == 0) {
				em.persist(userContacts);
		    } else {
		    	if (!em.contains(userContacts)) {
		    		em.merge(userContacts);
			    }
			}
			
		} catch (Exception e) {
			log.error("[updateContact]",e);
		}
	}

}
