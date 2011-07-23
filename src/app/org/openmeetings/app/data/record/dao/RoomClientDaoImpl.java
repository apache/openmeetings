package org.openmeetings.app.data.record.dao;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RoomClientDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomClientDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private RoomClientDaoImpl() {
	}

	private static RoomClientDaoImpl instance = null;

	public static synchronized RoomClientDaoImpl getInstance() {
		if (instance == null) {
			instance = new RoomClientDaoImpl();
		}

		return instance;
	}
	
	public RoomClient getAndAddRoomClientByPublicSID(RoomClient rcl) throws Exception {
		// TODO Auto-generated method stub
		
		RoomClient remoteRcl = this.getRoomClientByPublicSID(rcl.getPublicSID());
		if (remoteRcl == null) {
			if (rcl.getRoomClientId() != null) {
				log.error("###### ERROR IN getRoomClientByPublicSID TRIED TO ADD CLIENT ALREADY EXISTING: "+rcl);
				log.error("###### ERROR IN getRoomClientByPublicSID TRIED TO ADD CLIENT ALREADY EXISTING: "+rcl.getStreamid());
				log.error("###### ERROR IN getRoomClientByPublicSID TRIED TO ADD CLIENT ALREADY EXISTING: "+rcl.getRoomClientId());
				return this.getRoomClientById(rcl.getRoomClientId());
			} else {
				Long roomClientId = this.addRoomClient(rcl);
				return this.getRoomClientById(roomClientId);
			}
		} else {
			return remoteRcl;
		}

	}
	
	/**
	 * If two RoomClient have the Same publicSID they are equal
	 * @param roomClientId
	 * @return
	 */
	public RoomClient getRoomClientByPublicSID(String publicSID) {
		try {
			log.debug("getRoomClientByStreamId: "+ publicSID);
			
			String hql = "select r from RoomClient r " +
					"WHERE r.publicSID = :publicSID ";
			
			log.debug("hql: "+hql);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("publicSID",publicSID);
			
			log.debug("Number OF Records: "+query.getResultList().size());
			
			RoomClient roomClient = null;
			try {
				roomClient = (RoomClient) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return roomClient;
			
		} catch (Exception ex2) {
			log.error("[getRoomClientByPublicSID]: " , ex2);
		}
		return null;
	}

	public RoomClient getRoomClientById(Long roomClientId) {
		try {
			log.debug("getRoomClientById: "+ roomClientId);
			
			String hql = "select r from RoomClient r " +
					"WHERE r.roomClientId = :roomClientId ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("roomClientId",roomClientId);
			
			RoomClient roomClient = null;
			try {
				roomClient = (RoomClient) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return roomClient;
			
		} catch (Exception ex2) {
			log.error("[getRoomClientById]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomClient(RoomClient roomClient) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			roomClient = session.merge(roomClient);
			Long roomClientId = roomClient.getRoomClientId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return roomClientId;
		} catch (Exception ex2) {
			log.error("[addRoomClient]: " , ex2);
		}
		return null;
	}

}
