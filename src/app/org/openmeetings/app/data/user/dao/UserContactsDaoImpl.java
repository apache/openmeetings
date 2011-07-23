package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.user.UserContacts;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class UserContactsDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(UserContactsDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private static UserContactsDaoImpl instance = null;

	private UserContactsDaoImpl() {
	}

	public static synchronized UserContactsDaoImpl getInstance() {
		if (instance == null) {
			instance = new UserContactsDaoImpl();
		}
		return instance;
	}
	
	public Long addUserContact(Long user_id, Long ownerId, Boolean pending, String hash) {
		try {
			
			UserContacts userContact = new UserContacts();
			userContact.setInserted(new Date());
			userContact.setOwner(Usermanagement.getInstance().getUserById(ownerId));
			userContact.setContact(Usermanagement.getInstance().getUserById(user_id));
			userContact.setPending(pending);
			userContact.setHash(hash);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			userContact = session.merge(userContact);
			Long userContactId = userContact.getUserContactId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userContactId;			
		} catch (Exception e) {
			log.error("[addUserContact]",e);
		}
		return null;
	}
	
	public Long addUserContactObj(UserContacts userContact) {
		try {
			
			userContact.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			userContact = session.merge(userContact);
			Long userContactId = userContact.getUserContactId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userContactId;			
		} catch (Exception e) {
			log.error("[addUserContact]",e);
		}
		return null;
	}
	
	public Integer deleteUserContact(Long userContactId) {
		try {
			
			String hql = "delete from UserContacts where userContactId = :userContactId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql);
	        query.setParameter("userContactId",userContactId);
	        int rowCount = query.executeUpdate();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return rowCount;			
		} catch (Exception e) {
			log.error("[deleteUserContact]",e);
		}
		return null;
	}
	
	public Integer deleteAllUserContacts(Long ownerId) {
		try {
			
			String hql = "delete from UserContacts where owner.user_id = :ownerId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql);
	        query.setParameter("ownerId",ownerId);
	        int rowCount = query.executeUpdate();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return rowCount;			
		} catch (Exception e) {
			log.error("[deleteAllUserContacts]",e);
		}
		return null;
	}
	
	public Long checkUserContacts(Long user_id, Long ownerId) {
		try {
			
			String hql = "select count(c.userContactId) from UserContacts c " +
							"where c.contact.user_id = :user_id AND c.owner.user_id = :ownerId ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("user_id", user_id);
			query.setParameter("ownerId", ownerId);
			List ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			log.info("checkUserContacts"+(Long)ll.get(0));
			
			return (Long)ll.get(0);
			
		} catch (Exception e) {
			log.error("[checkUserContacts]",e);
		}
		return null;
	}
	
	public UserContacts getContactsByHash(String hash) {
		try {
			
			String hql = "select c from UserContacts c " +
							"where c.hash like :hash ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("hash", hash);
			List<UserContacts> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (ll.size() > 0) {
				return ll.get(0);
			}
			
			
		} catch (Exception e) {
			log.error("[getContactsByHash]",e);
		}
		return null;
	}
	
	public List<UserContacts> getContactsByUserAndStatus(Long ownerId, Boolean pending) {
		try {
			
			String hql = "select c from UserContacts c " +
							"where c.owner.user_id = :ownerId " +
							"AND c.pending = :pending " +
							"AND c.contact.deleted <> 'true'";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("ownerId", ownerId);
			query.setParameter("pending", pending);
			List<UserContacts> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
			
		} catch (Exception e) {
			log.error("[getContactsByUserAndStatus]",e);
		}
		return null;
	}
	
	public List<UserContacts> getContactsByShareCalendar(Long contactId, Boolean shareCalendar) {
		try {
			
			String hql = "select c from UserContacts c " +
							"where c.contact.user_id = :contactId " +
							"AND c.shareCalendar = :shareCalendar " +
							"AND c.contact.deleted <> 'true'";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("contactId", contactId);
			query.setParameter("shareCalendar", shareCalendar);
			List<UserContacts> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
			
		} catch (Exception e) {
			log.error("[getContactsByShareCalendar]",e);
		}
		return null;
	}
	
	public List<UserContacts> getContactRequestsByUserAndStatus(Long user_id, Boolean pending) {
		try {
			
			String hql = "select c from UserContacts c " +
							"where c.contact.user_id = :user_id " +
							"AND c.pending = :pending " +
							"AND c.contact.deleted <> 'true'";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("user_id", user_id);
			query.setParameter("pending", pending);
			List<UserContacts> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
			
		} catch (Exception e) {
			log.error("[getContactRequestsByUserAndStatus]",e);
		}
		return null;
	}
	
	public UserContacts getUserContacts(Long userContactId) {
		try {
			
			String hql = "select c from UserContacts c " +
							"where c.userContactId = :userContactId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			query.setParameter("userContactId", userContactId);
			UserContacts userContacts = null;
			try {
				userContacts = (UserContacts) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userContacts;
			
		} catch (Exception e) {
			log.error("[getUserContacts]",e);
		}
		return null;
	}
	
	public List<UserContacts> getUserContacts() {
		try {
			
			String hql = "select c from UserContacts c ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql); 
			List<UserContacts> userContacts = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userContacts;
			
		} catch (Exception e) {
			log.error("[getUserContacts]",e);
		}
		return null;
	}
	
	public Long updateContactStatus(Long userContactId, Boolean pending) {
		try {
			
			UserContacts userContacts = this.getUserContacts(userContactId);
			
			if (userContacts == null) {
				return null;
			}
			userContacts.setPending(pending);
			userContacts.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (userContacts.getUserContactId() == 0) {
				session.persist(userContacts);
			    } else {
			    	if (!session.contains(userContacts)) {
			    		session.merge(userContacts);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userContactId;
			
		} catch (Exception e) {
			log.error("[updateContactStatus]",e);
		}
		return null;
	}
	
	public void updateContact(UserContacts userContacts) {
		try {
			userContacts.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (userContacts.getUserContactId() == 0) {
				session.persist(userContacts);
			    } else {
			    	if (!session.contains(userContacts)) {
			    		session.merge(userContacts);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception e) {
			log.error("[updateContact]",e);
		}
	}

}
