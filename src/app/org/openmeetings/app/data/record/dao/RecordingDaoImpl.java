package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.persistence.beans.recording.Recording;
import org.openmeetings.app.persistence.beans.recording.RoomRecording;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(
			RecordingDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private Roommanagement roommanagement;

	public Long addRecording(String name, Long duration, String xmlString,
			Long rooms_id, Users recordedby, String comment,
			RoomRecording roomRecording) throws Exception {
		Recording recording = new Recording();
		recording.setDeleted("false");
		recording.setDuration(duration);
		recording.setComment(comment);
		recording.setRecordedby(recordedby);
		recording.setName(name);
		recording.setXmlString(xmlString);
		recording.setWhiteBoardConverted(false);
		recording.setRooms(roommanagement.getRoomById(rooms_id));
		recording.setStarttime(new java.util.Date());
		recording.setRoomRecording(roomRecording);
		return this.addRecording(recording);
	}

	public Long addRecording(Recording recording) {
		try {
			recording = em.merge(recording);
			Long recording_id = recording.getRecording_id();
			return recording_id;
		} catch (Exception ex2) {
			log.error("[addRecording] ", ex2);
		}
		return new Long(-1);
	}

	public List<Recording> getRecordings() {
		try {
			String hql = "select c from Recording as c where c.deleted <> :deleted";
			TypedQuery<Recording> query = em.createQuery(hql, Recording.class);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordings", ex2);
		}
		return null;
	}

	public List<Recording> getRecordingsByRoom(Long rooms_id) {
		try {
			String hql = "select c from Recording as c where c.rooms.rooms_id = :rooms_id AND c.deleted <> :deleted";
			TypedQuery<Recording> query = em.createQuery(hql, Recording.class);
			query.setParameter("rooms_id", rooms_id);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByRoom", ex2);
		}
		return null;
	}

	public List<Recording> getRecordingWhiteboardToConvert() {
		try {
			String hql = "select c from Recording as c "
					+ "where c.whiteBoardConverted = :whiteBoardConverted "
					+ "AND c.deleted <> :deleted";
			TypedQuery<Recording> query = em.createQuery(hql, Recording.class);
			query.setParameter("whiteBoardConverted", false);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByRoom", ex2);
		}
		return null;
	}

	public List<Recording> getRecordingsByWhereClause(String where) {
		try {
			String hql = "select c from Recording as c where " + where
					+ " c.deleted <> :deleted";
			log.error("getRecordingsByWhereClause: " + hql);
			TypedQuery<Recording> query = em.createQuery(hql, Recording.class);
			query.setParameter("deleted", "true");
			List<Recording> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getRecordingsByWhereClause", ex2);
		}
		return null;
	}

	public Recording getRecordingById(Long recording_id) {
		try {
			String hql = "select c from Recording as c where c.recording_id = :recording_id AND c.deleted <> :deleted";
			TypedQuery<Recording> query = em.createQuery(hql, Recording.class);
			query.setParameter("recording_id", recording_id);
			query.setParameter("deleted", "true");
			Recording rec = null;
			try {
				rec = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return rec;
		} catch (Exception ex2) {
			log.error("getRecordingById", ex2);
		}
		return null;
	}

	public void updateRecording(Recording rec) {
		try {

			log.debug("updateRecording SET TO TRUE NOW!!! "
					+ rec.getRecording_id() + " "
					+ rec.getWhiteBoardConverted());

			if (rec.getRecording_id() == null) {
				em.persist(rec);
			} else {
				if (!em.contains(rec)) {
					em.merge(rec);
				}
			}
		} catch (Exception ex2) {
			log.error("updateRecording", ex2);
		}
	}

}
