package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.persistence.beans.recording.Recording;
import org.openmeetings.app.persistence.beans.recording.RoomRecording;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RecordingDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private static RecordingDaoImpl instance;

	private RecordingDaoImpl() {}

	public static synchronized RecordingDaoImpl getInstance() {
		if (instance == null) {
			instance = new RecordingDaoImpl();
		}
		return instance;
	}
	
	public Long addRecording(String name, Long duration, String xmlString, Long rooms_id, Users recordedby, String  comment, RoomRecording roomRecording) throws Exception{
		Recording recording = new Recording();
		recording.setDeleted("false");
		recording.setDuration(duration);
		recording.setComment(comment);
		recording.setRecordedby(recordedby);
		recording.setName(name);
		recording.setXmlString(xmlString);
		recording.setWhiteBoardConverted(false);
		recording.setRooms(Roommanagement.getInstance().getRoomById(rooms_id));
		recording.setStarttime(new java.util.Date());
		recording.setRoomRecording(roomRecording);
		return this.addRecording(recording);
	}
	
	public Long addRecording(Recording recording) {
		try {
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			recording = session.merge(recording);
			Long recording_id = recording.getRecording_id();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return recording_id;
		} catch (Exception ex2) {
			log.error("[addRecording] ",ex2);
		}
		return new Long(-1);
	}
	
	public List<Recording> getRecordings(){
		try {
			String hql = "select c from Recording as c where c.deleted <> :deleted";
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordings",ex2);
		}
		return null;
	}
	
	public List<Recording> getRecordingsByRoom(Long rooms_id){
		try {
			String hql = "select c from Recording as c where c.rooms.rooms_id = :rooms_id AND c.deleted <> :deleted";
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("rooms_id", rooms_id);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByRoom",ex2);
		}
		return null;
	}
	
	public List<Recording> getRecordingWhiteboardToConvert(){
		try {
			String hql = "select c from Recording as c " +
					"where c.whiteBoardConverted = :whiteBoardConverted " +
					"AND c.deleted <> :deleted";
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("whiteBoardConverted", false);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByRoom",ex2);
		}
		return null;
	}
	
	public List<Recording> getRecordingsByWhereClause(String where){
		try {
			String hql = "select c from Recording as c where " + where + " c.deleted <> :deleted";
			log.error("getRecordingsByWhereClause: "+hql);
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByWhereClause",ex2);
		}
		return null;
	}
	
	
	public Recording getRecordingById(Long recording_id) {
		try {
			String hql = "select c from Recording as c where c.recording_id = :recording_id AND c.deleted <> :deleted";
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("recording_id", recording_id);
			query.setParameter("deleted", "true");
			Recording rec = null;
			try {
				rec = (Recording) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			return rec;
		} catch (Exception ex2) {
			log.error("getRecordingById",ex2);
		}
		return null;
	}	
	
	public void updateRecording(Recording rec){
		try {
			
			log.debug("updateRecording SET TO TRUE NOW!!! "+rec.getRecording_id()+" "+rec.getWhiteBoardConverted());
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (rec.getRecording_id() == null) {
				session.persist(rec);
			    } else {
			    	if (!session.contains(rec)) {
			    		session.merge(rec);
			    }
			}
			//session.refresh(rec);
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("updateRecording",ex2);
		}
	}
	
	
}
