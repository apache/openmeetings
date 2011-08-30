package org.openmeetings.app.data.record.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomClientDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomClientDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
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
			
			Query query = em.createQuery(hql);
			query.setParameter("publicSID",publicSID);
			
			log.debug("Number OF Records: "+query.getResultList().size());
			
			RoomClient roomClient = null;
			try {
				roomClient = (RoomClient) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
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
			
			Query query = em.createQuery(hql);
			query.setParameter("roomClientId",roomClientId);
			
			RoomClient roomClient = null;
			try {
				roomClient = (RoomClient) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return roomClient;
			
		} catch (Exception ex2) {
			log.error("[getRoomClientById]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomClient(RoomClient roomClient) {
		try {
			
			roomClient = em.merge(roomClient);
			Long roomClientId = roomClient.getRoomClientId();
			
			return roomClientId;
		} catch (Exception ex2) {
			log.error("[addRoomClient]: " , ex2);
		}
		return null;
	}

}
