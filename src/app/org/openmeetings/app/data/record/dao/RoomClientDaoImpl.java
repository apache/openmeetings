package org.openmeetings.app.data.record.dao;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class RoomClientDaoImpl {

	private static final Logger log = Logger.getLogger(RoomClientDaoImpl.class);

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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("publicSID",publicSID);
			
			log.debug("Number OF Records: "+query.list().size());
			
			RoomClient roomClient = (RoomClient) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomClient;
			
		} catch (HibernateException ex) {
			log.error("[getRoomClientByPublicSID]: " , ex);
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("roomClientId",roomClientId);
			
			RoomClient roomClient = (RoomClient) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomClient;
			
		} catch (HibernateException ex) {
			log.error("[getRoomClientById]: " , ex);
		} catch (Exception ex2) {
			log.error("[getRoomClientById]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomClient(RoomClient roomClient) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long roomClientId = (Long) session.save(roomClient);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomClientId;
		} catch (HibernateException ex) {
			log.error("[addRoomClient]: " , ex);
		} catch (Exception ex2) {
			log.error("[addRoomClient]: " , ex2);
		}
		return null;
	}

}
