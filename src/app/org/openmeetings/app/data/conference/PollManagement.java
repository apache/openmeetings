package org.openmeetings.app.data.conference;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.openmeetings.app.persistence.beans.poll.RoomPoll;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PollManagement {
	private static final Logger log = Red5LoggerFactory
			.getLogger(PollManagement.class);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private Roommanagement roommanagement;

	public Long addPollType(Long labelId, Boolean isNumeric) {
		log.debug("Adding poll type: " + labelId + ", " + isNumeric);
		PollType pt = new PollType();
		pt.setLabel(fieldmanagment.getFieldvaluesById(labelId));
		pt.setIsNumericAnswer(isNumeric);

		em.persist(pt);
		
		return pt.getPollTypesId();
	}
	
	@SuppressWarnings("unchecked")
	public List<PollType> getPollTypes() {
		Query q = em.createQuery("SELECT pt FROM PollType pt");
		return q.getResultList();
	}
	
	public PollType getPollType(Long typeId) {
		Query q = em.createQuery("SELECT pt FROM PollType pt WHERE pt.pollTypesId = :pollTypesId");
		q.setParameter("pollTypesId", typeId);
		return (PollType)q.getSingleResult();
	}
	
	public RoomPoll createPoll(RoomClient rc, String pollQuestion, Long pollTypeId) {
		RoomPoll roomP = new RoomPoll();
		
		roomP.setCreatedBy(rc);
		roomP.setCreated(new Date());
		roomP.setPollQuestion(pollQuestion);
		roomP.setPollType(getPollType(pollTypeId));
		roomP.setRoom(roommanagement.getRoomById(rc.getRoom_id()));
		
		em.persist(roomP);
		return roomP;
	}

	public RoomPoll updatePoll(RoomPoll rp) {
		return em.merge(rp);
	}

	public void clearRoomPollList(Long room_id){
		try {
			log.debug(" :: clearRoomPollList :: ");
			Query q = em.createQuery("UPDATE RoomPoll rp SET rp.archived = :archived WHERE rp.room.rooms_id = :rooms_id");
			q.setParameter("rooms_id", room_id);
			q.setParameter("archived", true);
			q.executeUpdate();
		} catch (Exception err) {
			log.error("[clearRoomPollList]", err);
		}
	}

	public RoomPoll getPoll(Long room_id) {
		try {
			log.debug(" :: getPoll :: " + room_id);
			Query q = em.createQuery("SELECT rp FROM RoomPoll rp WHERE rp.room.rooms_id = :room_id AND rp.archived = :archived");
			q.setParameter("room_id", room_id);
			q.setParameter("archived", false);
			return (RoomPoll)q.getSingleResult();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public boolean hasPoll(Long room_id) {
		try {
			log.debug(" :: hasPoll :: " + room_id);
			Query q = em.createQuery("SELECT COUNT(rp) FROM RoomPoll rp WHERE rp.room.rooms_id = :room_id AND rp.archived = :archived");
			q.setParameter("room_id", room_id);
			q.setParameter("archived", false);
			return (Long)q.getSingleResult() > 0;
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return false;
	}
	
	public boolean hasVoted(Long room_id, String streamid) {
		try {
			log.debug(" :: hasVoted :: " + room_id + ", " + streamid);
			Query q = em.createQuery("SELECT rpa FROM RoomPollAnswers rpa "
				+ "WHERE rpa.roomPoll.room.rooms_id = :room_id AND rpa.votedClient.streamid = :streamid AND rpa.roomPoll.archived = :archived");
			q.setParameter("room_id", room_id);
			q.setParameter("streamid", streamid);
			q.setParameter("archived", false);
			q.getSingleResult();
			return true;
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return false;
	}
}
