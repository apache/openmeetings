package org.openmeetings.app.data.conference;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.user.Usermanagement;
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
	private Usermanagement usermanagement;
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
	
	public List<PollType> getPollTypes() {
		TypedQuery<PollType> q = em.createQuery("SELECT pt FROM PollType pt",PollType.class);
		return q.getResultList();
	}
	
	public PollType getPollType(Long typeId) {
		Query q = em.createQuery("SELECT pt FROM PollType pt WHERE pt.pollTypesId = :pollTypesId");
		q.setParameter("pollTypesId", typeId);
		return (PollType)q.getSingleResult();
	}
	
	public RoomPoll createPoll(RoomClient rc, String pollName, String pollQuestion, Long pollTypeId) {
		RoomPoll roomP = new RoomPoll();
		
		roomP.setCreatedBy(usermanagement.getUserById(rc.getUser_id()));
		roomP.setCreated(new Date());
		roomP.setPollName(pollName);
		roomP.setPollQuestion(pollQuestion);
		roomP.setPollType(getPollType(pollTypeId));
		roomP.setRoom(roommanagement.getRoomById(rc.getRoom_id()));
		
		em.persist(roomP);
		return roomP;
	}
	
	public void savePollBackup(RoomPoll rp) {
		em.persist(rp);
	}


	public RoomPoll updatePoll(RoomPoll rp) {
		return em.merge(rp);
	}

	public boolean closePoll(Long room_id){
		try {
			log.debug(" :: closePoll :: ");
			Query q = em.createQuery("UPDATE RoomPoll rp SET rp.archived = :archived WHERE rp.room.rooms_id = :rooms_id");
			q.setParameter("rooms_id", room_id);
			q.setParameter("archived", true);
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[closePoll]", err);
		}
		return false;
	}

	public boolean deletePoll(Long poll_id){
		try {
			log.debug(" :: deletePoll :: ");
			Query q = em.createQuery("DELETE FROM RoomPoll rp WHERE rp.roomPollId = :roomPollId");
			q.setParameter("roomPollId", poll_id);
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[deletePoll]", err);
		}
		return false;
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
	
	public List<RoomPoll> getPollListBackup() {
		try {
			TypedQuery<RoomPoll> q = em.createQuery("SELECT rp FROM RoomPoll rp",RoomPoll.class);
			return q.getResultList();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public List<RoomPoll> getArchivedPollList(Long room_id) {
		try {
			log.debug(" :: getPoll :: " + room_id);
			TypedQuery<RoomPoll> q = em.createQuery("SELECT rp FROM RoomPoll rp WHERE rp.room.rooms_id = :room_id AND rp.archived = :archived",RoomPoll.class);
			q.setParameter("room_id", room_id);
			q.setParameter("archived", true);
			return q.getResultList();
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
	
	public boolean hasVoted(Long room_id, Long userid) {
		try {
			log.debug(" :: hasVoted :: " + room_id + ", " + userid);
			Query q = em.createQuery("SELECT rpa FROM RoomPollAnswers rpa "
				+ "WHERE rpa.roomPoll.room.rooms_id = :room_id AND rpa.votedUser.user_id = :userid AND rpa.roomPoll.archived = :archived");
			q.setParameter("room_id", room_id);
			q.setParameter("userid", userid);
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
